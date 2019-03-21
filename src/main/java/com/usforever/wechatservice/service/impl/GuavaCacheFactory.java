package com.usforever.wechatservice.service.impl;

import com.usforever.wechatservice.common.Constants;
import com.usforever.wechatservice.service.GuavaCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @Author ：yaxuSong
 * @Description:
 * @Date: 17:48 2018/12/18
 * @Modified by:
 */
@Slf4j
@Service
public class GuavaCacheFactory {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 根据名称获取已经实例化的对象
     * @param beanName 对象名称
     * @return
     */
    public GuavaCacheService getGuavaCacheService(String beanName){
        return applicationContext.getBean(beanName,GuavaCacheService.class);
    }

    /**
     * 可实例化不同名称的GuavaCache对象
     * @param beanName 实例化bean名称
     * @param expireTime 过期时间
     * @return
     */
    public GuavaCacheService buildGuavaCacheService(String beanName,Integer expireTime){
        try {
            GuavaCacheService guavaCacheService = applicationContext.getBean(beanName,GuavaCacheService.class);
            return guavaCacheService;
        }
        catch (Exception e){
            log.warn("没有找到相关对象");
        }
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)applicationContext.getAutowireCapableBeanFactory();
        BeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClassName(GuavaCacheServiceImpl.class.getName());
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
        return applicationContext.getBean(beanName,GuavaCacheService.class);
    }

    public GuavaCacheService buildGuavaCacheService(String beanName){
        return buildGuavaCacheService(beanName, Constants.EXPIRE_TIME);
    }
}
