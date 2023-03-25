package com.mbcoder.scheduler.condition;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

/**
 * @program:DynamicSchedulingTutorial
 * @description: .
 * @author: liusf
 * @create: 2021/8/21 上午10:59
 **/
@Configuration
public class ScheduledConfig {

    @Conditional(ScheduledCondition.class)
    @Bean
    public ScheduledAnnotationBeanPostProcessor processor() {
        return new ScheduledAnnotationBeanPostProcessor();
    }
}
