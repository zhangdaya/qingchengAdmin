package com.qingcheng.dao;

import com.qingcheng.pojo.order.CategoryReport;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 商品类目销售分析表
 * @program: qingchengAdmin
 * @description:定时统计上一天的数据，将统计结果存储在tb_category_report
 * @author: 张梦雅
 * @create: 2020-04-16 13:52
 */
public interface CategoryReportMapper extends Mapper<CategoryReport> {

    /**
     * 商品类目销售分析表：类目统计
     * @param localDate:只会获取年月日
     * @return
     */
    @Select("select oi.category_id1 categoryId1,oi.category_id2 categoryId2,oi.category_id3 categoryId3,Date_FORMAT(o.pay_time,'%Y-%m-%d') countDate,SUM(oi.num) num,SUM(oi.pay_money) money "+
            "from  tb_order o,tb_order_item oi "+
            "where o.id=oi.order_id and o.pay_status='1' and o.is_delete='0' and Date_FORMAT(o.pay_time,'%Y-%m-%d')=#{localDate} "+
            "GROUP BY oi.category_id1,oi.category_id2,oi.category_id3,Date_FORMAT(o.pay_time,'%Y-%m-%d')")
    public List<CategoryReport> categoryReport(@Param("localDate") LocalDate localDate);

    /**
     * 按日期统计一级分类数据
     * @param firstDate
     * @param lastDate
     * @return
     */
    @Select("select r.category_id1 categoryId1,SUM(num) num,SUM(money) money,v.name categoryName " +
            "from tb_category_report r,v_category1 v " +
            "where r.category_id1=v.id and r.count_date>=#{firstDate} and r.count_date<=#{lastDate} " +
            "GROUP BY r.category_id1,v.name")
    public List<Map> categoryId1(@Param("firstDate") String firstDate,@Param("lastDate") String lastDate);
}
