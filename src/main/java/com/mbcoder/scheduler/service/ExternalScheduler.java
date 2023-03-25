package com.mbcoder.scheduler.service;

import com.mbcoder.scheduler.model.ConfigItem;
import com.mbcoder.scheduler.model.Configuration;
import com.mbcoder.scheduler.model.TaskDefinition;
import com.mbcoder.scheduler.repository.ConfigRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Service
public class ExternalScheduler implements SchedulingConfigurer {

    private static Logger LOGGER = LoggerFactory.getLogger(ExternalScheduler.class);

    ScheduledTaskRegistrar scheduledTaskRegistrar;

    Map<String, ScheduledFuture> futureMap = new HashMap<>();

    @Autowired
    ConfigRepo configRepo;

    @Bean
    public TaskScheduler poolScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        scheduler.setPoolSize(1);
        scheduler.initialize();
        return scheduler;
    }

    // Initially scheduler has no job
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        if (scheduledTaskRegistrar == null) {
            scheduledTaskRegistrar = taskRegistrar;
        }
        if (taskRegistrar.getScheduler() == null) {
            taskRegistrar.setScheduler(poolScheduler());
        }
    }

    public boolean addJob(String jobName) {
        if (futureMap.containsKey(jobName)) {
            return false;
        }

        ScheduledFuture future =
            scheduledTaskRegistrar.getScheduler().schedule(() -> methodToBeExecuted(jobName, "5"), t -> {
            Calendar nextExecutionTime = new GregorianCalendar();
            Date lastActualExecutionTime = t.lastActualExecutionTime();
            nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
            nextExecutionTime.add(Calendar.SECOND, 5);
            return nextExecutionTime.getTime();
        });

        configureTasks(scheduledTaskRegistrar);
        futureMap.put(jobName, future);
        return true;
    }

    public boolean removeJob(String name) {
        if (!futureMap.containsKey(name)) {
            return false;
        }
        ScheduledFuture future = futureMap.get(name);
        future.cancel(true);
        futureMap.remove(name);
        return true;
    }

    public void methodToBeExecuted(String action, String cron) {
        LOGGER.info("methodToBeExecuted: Next execution time of this taken from : {},{}", action, cron);
    }

    /**
     * 增加job.
     * 
     * @param taskDefinition
     */
    public void addJobByDB(TaskDefinition taskDefinition) {
        ConfigItem configItem = new ConfigItem();
        configItem.setConfigKey(taskDefinition.getActionType());
        configItem.setConfigValue(taskDefinition.getCronExpression());
        configRepo.save(configItem);
        ScheduledFuture future = scheduledTaskRegistrar.getScheduler().schedule(
            () -> methodToBeExecuted(taskDefinition.getActionType(), taskDefinition.getCronExpression()), t -> {
                CronTrigger crontrigger = new CronTrigger(taskDefinition.getCronExpression());
                return crontrigger.nextExecutionTime(t);
            });
        configureTasks(scheduledTaskRegistrar);
        futureMap.put(taskDefinition.getActionType(), future);
    }

    /**
     * 修改时间.
     */
    public void modifyJobByDB(TaskDefinition taskDefinition) {
        ConfigItem one = configRepo.getOne(taskDefinition.getActionType());
        if (Objects.nonNull(one)) {
            one.setConfigValue(taskDefinition.getCronExpression());
            configRepo.save(one);
        }
        ScheduledFuture future = futureMap.get(taskDefinition.getActionType());
        future.cancel(true);
        futureMap.remove(taskDefinition.getActionType());

        ScheduledFuture futureNew = scheduledTaskRegistrar.getScheduler().schedule(
            () -> methodToBeExecuted(taskDefinition.getActionType(), taskDefinition.getCronExpression()), t -> {
                CronTrigger crontrigger = new CronTrigger(taskDefinition.getCronExpression());
                return crontrigger.nextExecutionTime(t);
            });
        configureTasks(scheduledTaskRegistrar);
        futureMap.put(taskDefinition.getActionType(), futureNew);

    }

    /**
     * 取消job.
     */
    public void removeJobByDB(TaskDefinition taskDefinition) {
        ConfigItem one = configRepo.getOne(taskDefinition.getActionType());
        if (Objects.nonNull(one)) {
            ScheduledFuture future = futureMap.get(taskDefinition.getActionType());
            future.cancel(true);
            futureMap.remove(taskDefinition.getActionType());
        }

    }
}
