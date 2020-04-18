package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.order.CategoryReportService;
import com.qingcheng.service.order.OrderService;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @program: 订单超时处理:每2分钟处理一次请求，将60分钟前未付款订单关闭
 * @description:
 * @author: 张梦雅
 * @create: 2020-04-14 21:28
 * @Component:组件的意思
 */
@Component
public class OrderTask {

    @Reference
    private OrderService orderService;

    @Reference
    private CategoryReportService categoryReportService;

    @Scheduled(cron = "0 0/2 * * * ?")
    public void orderTimeOutLogic(){
        System.out.println("每两分钟间隔执行一次任务"+ new Date());
        orderService.orderTimeOutLogic();
    }
    /**
     * 定时任务-生成统计数据
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void createCategoryReportData(){
        System.out.println("生成类目统计数据");
        categoryReportService.creatDate();
    }
}
