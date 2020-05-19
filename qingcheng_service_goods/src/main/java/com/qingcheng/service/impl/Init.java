package com.qingcheng.service.impl;

import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SkuService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: qingchengAdmin
 * @description:InitializingBean接口为bean提供了初始化方法的方式，
 * 它只包括afterPropertiesSet方法，凡是继承该接口的类，在初始化bean的时候都会执行该方法。
 * @author: 张梦雅
 * @create: 2020-05-10 16:16
 */
@Component
public class Init implements InitializingBean {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SkuService skuService;

    public void afterPropertiesSet() throws Exception {
        System.out.println("---缓存预热---");
        categoryService.saveCategoryTreeToRedis();
        skuService.saveAllPriceToRedis();
    }
}
