package com.mbcoder.scheduler.service;

import com.mbcoder.scheduler.model.Configuration;
import com.mbcoder.scheduler.model.Constants;
import com.mbcoder.scheduler.repository.ConfigRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program:DynamicSchedulingTutorial
 * @description: .
 * @author: liusf
 * @create: 2021/8/21 上午10:33
 **/
@Service
public class ConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

    ConfigRepository configRepository;

    private Map<String, Configuration> configurationList;

    private List<String> mandatoryConfigs;

//    @Autowired
//    public ConfigurationService(ConfigRepository configRepository) {
//        this.configRepository = configRepository;
//        this.configurationList = new ConcurrentHashMap<>();
//        this.mandatoryConfigs = new ArrayList<>();
//        this.mandatoryConfigs.add(Constants.CONFIG_KEY_REFRESH_RATE_CONFIG);
//        this.mandatoryConfigs.add(Constants.CONFIG_KEY_REFRESH_RATE_METRIC);
//        this.mandatoryConfigs.add(Constants.CONFIG_KEY_REFRESH_RATE_TOKEN);
//        this.mandatoryConfigs.add(Constants.CONFIG_KEY_REFRESH_RATE_USER);
//    }
//
//    /**
//     * Loads configuration parameters from Database
//     */
//    @PostConstruct
//    public void loadConfigurations() {
//        LOGGER.info("Scheduled Event: Configuration table loaded/updated from database");
//        StringBuilder sb = new StringBuilder();
//        sb.append("Configuration Parameters:");
//        List<Configuration> configs = configRepository.findAll();
//        for (Configuration configuration : configs) {
//            sb.append("\n" + configuration.getConfigKey() + ":" + configuration.getConfigValue());
//            this.configurationList.put(configuration.getConfigKey(), configuration);
//        }
//        LOGGER.info(sb.toString());
//
//        checkMandatoryConfigurations();
//    }
//
//    public Configuration getConfiguration(String key) {
//        return configurationList.get(key);
//    }
//
//    /**
//     * Checks if the mandatory parameters are exists in Database
//     */
//    public void checkMandatoryConfigurations() {
//        for (String mandatoryConfig : mandatoryConfigs) {
//            boolean exists = false;
//            for (Map.Entry<String, Configuration> pair : configurationList.entrySet()) {
//                if (pair.getKey().equalsIgnoreCase(mandatoryConfig) && !pair.getValue().getConfigValue().isEmpty()) {
//                    exists = true;
//                }
//            }
//            if (!exists) {
//                String errorLog =
//                    String.format("A mandatory Configuration parameter is not found in DB: %s", mandatoryConfig);
//                LOGGER.error(errorLog);
//            }
//        }
//
//    }
}
