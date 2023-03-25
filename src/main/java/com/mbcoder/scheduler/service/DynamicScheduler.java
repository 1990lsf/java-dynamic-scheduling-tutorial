package com.mbcoder.scheduler.service;

import com.mbcoder.scheduler.model.ConfigItem;
import com.mbcoder.scheduler.repository.ConfigRepo;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
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

@Service
public class DynamicScheduler implements SchedulingConfigurer {

    private static Logger LOGGER = LoggerFactory.getLogger(DynamicScheduler.class);

    @Autowired
    ConfigRepo repo;
    @Autowired
    LockProvider lockProvider;
    // @PostConstruct
    // public void initDatabase() {
    // ConfigItem config = new ConfigItem("next_exec_time", "4");
    // repo.save(config);
    // }

    @Bean
    public TaskScheduler poolScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        scheduler.setPoolSize(1);
        scheduler.initialize();
        return scheduler;
    }

    // We can have multiple tasks inside the same registrar as we can see below.
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
//        LockConfiguration lockConfiguration = new LockConfiguration(Instant.now(), "test", Duration.ofMinutes(1),
//        Duration.ofMinutes(1));
//        LockConfigurationExtractor lockConfigurationExtractor = new DefaultLockConfigurationExtractor
//        (lockConfiguration);
//        LockManager lockManager = new DefaultLockManager(lockProvider, lockConfigurationExtractor);
//        LockableTaskScheduler lockableTaskScheduler = new LockableTaskScheduler(poolScheduler(),lockManager);

        taskRegistrar.setScheduler(poolScheduler());
        LOGGER.info("configureTasks");
        // Random next execution time.
        // taskRegistrar.addTriggerTask(() -> scheduleDynamically(), t -> {
        // Calendar nextExecutionTime = new GregorianCalendar();
        // Date lastActualExecutionTime = t.lastActualExecutionTime();
        // nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
        // nextExecutionTime.add(Calendar.SECOND, getNextExecutionTime()); // This is where we set the next execution
        // time.
        // return nextExecutionTime.getTime();
        // });

        // Fixed next execution time.
        // taskRegistrar.addTriggerTask(() -> scheduleFixed(), t -> {
        // Calendar nextExecutionTime = new GregorianCalendar();
        // Date lastActualExecutionTime = t.lastActualExecutionTime();
        // nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
        // nextExecutionTime.add(Calendar.SECOND, 7); // This is where we set the next execution time.
        // return nextExecutionTime.getTime();
        // });

        // Next execution time is taken from DB, so if the value in DB changes, next execution time will change too.
//         taskRegistrar.addTriggerTask(() -> scheduledDatabase(repo.findById("next_exec_time").get().getConfigValue()),
//         t -> {
//         Calendar nextExecutionTime = new GregorianCalendar();
//         Date lastActualExecutionTime = t.lastActualExecutionTime();
//         nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
//         nextExecutionTime.add(Calendar.SECOND,
//         Integer.parseInt(repo.findById("next_exec_time").get().getConfigValue()));
//         return nextExecutionTime.getTime();
//         });


        List<ConfigItem> all = repo.findAll();
        for (ConfigItem configItem : all) {
            // or cron way, you can also get the expression from DB or somewhere else just like we did above.
            Runnable runnable = () -> {
                LockingTaskExecutor executor = new DefaultLockingTaskExecutor(lockProvider);
                Duration duration = Duration.ofSeconds(60);
                Duration duration2 = Duration.ofSeconds(50);
                executor.executeWithLock((Runnable) () -> scheduleCron(configItem.getConfigKey(),
                    configItem.getConfigValue()), new LockConfiguration(Instant.now(),configItem.getConfigKey(),
                    duration, duration2));
            };

            taskRegistrar.addTriggerTask(runnable,
                t -> {
                    Optional<ConfigItem> byId = repo.findById(configItem.getConfigKey());
                    String cron = "0 0 5 31 2 ?";
                    if (byId.isPresent()) {
                        cron = byId.get().getConfigValue();
                    }
                    CronTrigger crontrigger = new CronTrigger(cron);
                    return crontrigger.nextExecutionTime(t);
                });

        }

    }

    public void scheduleDynamically() {
        LOGGER.info("scheduleDynamically: Next execution time of this changes every time between 1 and 5 seconds");
    }

    // I added this to show that one taskRegistrar can have multiple different tasks.
    // And each of those tasks can have their own next execution time.
    public void scheduleFixed() {
        LOGGER.info("scheduleFixed: Next execution time of this will always be 7 seconds");
    }

    public void scheduledDatabase(String time) {
        LOGGER.info("scheduledDatabase: Next execution time of this will be taken from DB -> {}", time);
    }

    // Only reason this method gets the cron as parameter is for debug purposes.
    public void scheduleCron(String key, String cron) {
        LOGGER.info("scheduleCron: Next execution time of this taken from : {},{}", key, cron);

    }

    // This is only to show that next execution time can be changed on the go with SchedulingConfigurer.
    // This can not be done via @Scheduled annotation.
    public int getNextExecutionTime() {
        return new Random().nextInt(5) + 1;
    }

}
