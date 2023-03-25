package com.mbcoder.scheduler.model;

import com.sun.istack.internal.NotNull;
import javax.persistence.Entity;
import javax.persistence.Id;


/**
 * @program:DynamicSchedulingTutorial
 * @description: .
 * @author: liusf
 * @create: 2021/8/21 上午10:34
 **/
@Entity
public class Configuration {
    @Id
    String  configKey;

    @NotNull
    String  configValue;

    public Configuration() {
    }

    public Configuration(String configKey, String configValue) {
        this.configKey = configKey;
        this.configValue = configValue;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

}
