package com.mbcoder.scheduler.repository;

import com.mbcoder.scheduler.model.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @program:DynamicSchedulingTutorial
 * @description: .
 * @author: liusf
 * @create: 2021/8/21 上午10:35
 **/
public interface ConfigRepository extends JpaRepository<Configuration, String> {

}