package com.qingcheng.service.impl;

import com.qingcheng.service.business.AdService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: qingchengAdmin
 * @description:
 * @author: 张梦雅
 * @create: 2020-05-17 16:44
 */
@Component
public class Init implements InitializingBean {

    @Autowired
    private AdService adService;

    public void afterPropertiesSet() throws Exception {
        System.out.println("缓存预热");
        adService.saveAllAdToRedis();
    }
}
