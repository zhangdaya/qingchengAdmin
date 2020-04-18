package com.qingcheng.service.impl;

import com.qingcheng.dao.CategoryReportMapper;
import com.qingcheng.pojo.order.CategoryReport;
import com.qingcheng.service.order.CategoryReportService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @program: qingchengAdmin
 * @description:
 * @author: 张梦雅
 * @create: 2020-04-16 13:53
 */
@Service(interfaceClass = CategoryReportService.class)
public class CategoryReportServiceImpl implements CategoryReportService {

    @Autowired
    private CategoryReportMapper categoryReportMapper;

    /**
     * 商品类目销售分析表：类目统计
     * @param localDate
     * @return
     */
    @Override
    public List<CategoryReport> categoryReport(LocalDate localDate) {
        return categoryReportMapper.categoryReport(localDate);
    }

    /**
     * 定时任务-生成统计数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void creatDate() {
        //1.查询昨天的数据
        LocalDate localDate = LocalDate.now().minusDays(1);
        List<CategoryReport> categoryReports = categoryReportMapper.categoryReport(localDate);

        //2.把查询数据保存到tb_category_report
        for (CategoryReport c:categoryReports){
            categoryReportMapper.insert(c);
        }
    }

    /**
     * 按时间段统计一级类目
     * @param firstDate
     * @param lastDate
     * @return
     */
    @Override
    public List<Map> categoryId1(String firstDate, String lastDate) {
        return categoryReportMapper.categoryId1(firstDate,lastDate);
    }
}
