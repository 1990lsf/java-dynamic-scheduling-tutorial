package com.mbcoder.scheduler.model;

/**
 * @program:DynamicSchedulingTutorial
 * @description: .
 * @author: liusf
 * @create: 2021/8/21 下午1:22
 **/

public class TaskDefinition {

    private String cronExpression;
    private String actionType;
    private String data;

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
