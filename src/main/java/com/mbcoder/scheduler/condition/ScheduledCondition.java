package com.mbcoder.scheduler.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @program:DynamicSchedulingTutorial
 * @description: .
 * @author: liusf
 * @create: 2021/8/21 上午10:58
 **/
public class ScheduledCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        //读取配置中的属性
        return Boolean.valueOf(context.getEnvironment().getProperty("enable.scheduled"));
    }
}