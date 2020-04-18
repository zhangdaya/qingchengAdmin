package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.order.CategoryReport;
import com.qingcheng.service.order.CategoryReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @program: qingchengAdmin
 * @description:
 * @author: 张梦雅
 * @create: 2020-04-16 16:06
 */
@RestController
@RequestMapping("/categoryReport")
public class CategoryReportController {
    @Reference
    private CategoryReportService categoryReportService;

    /**
     * 定时统计上一天的数据，将统计结果存储在tb_category_report
     * @return
     */
    @GetMapping("/yesterday")
    public List<CategoryReport> yesterday(){
        //得到昨天的日期
        //minusDays 减一的意思
        LocalDate localDate = LocalDate.now().minusDays(1);
        return categoryReportService.categoryReport(localDate);
    }

    /**
     * 按时间段统计一级类目
     * @param firstDate
     * @param lastDate
     * @return
     */
    @GetMapping("/categoryId1")
    public List<Map> categoryId1(String firstDate, String lastDate){
        return categoryReportService.categoryId1(firstDate,lastDate);
    }

}
