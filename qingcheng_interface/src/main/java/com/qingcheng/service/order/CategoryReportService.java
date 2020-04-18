package com.qingcheng.service.order;

import com.qingcheng.pojo.order.CategoryReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @program: qingchengAdmin
 * @description:
 * @author: 张梦雅
 * @create: 2020-04-16 13:54
 */
public interface CategoryReportService {
    /**
     * 商品类目销售分析表：类目统计
     *定时统计上一天的数据，将统计结果存储在tb_category_report
     * @param localDate
     * @return
     */
    public List<CategoryReport> categoryReport(LocalDate localDate);

    /**
     * 定时任务-生成统计数据
     */
    public void creatDate();

    /**
     * 按时间段统计一级类目
     * @param firstDate
     * @param lastDate
     * @return
     */
    public List<Map> categoryId1(String firstDate,String lastDate);

}
