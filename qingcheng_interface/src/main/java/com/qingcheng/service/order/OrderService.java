package com.qingcheng.service.order;

import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.Orders;

import java.util.*;

/**
 * order业务逻辑层
 */
public interface OrderService {


    public List<Order> findAll();


    public PageResult<Order> findPage(int page, int size);


    public List<Order> findList(Map<String, Object> searchMap);


    public PageResult<Order> findPage(Map<String, Object> searchMap, int page, int size);


    public Order findById(String id);

    public void add(Order order);


    public void update(Order order);


    public void delete(String id);

    /**
     * 根据ID查询订单
     *
     * @param id
     * @return
     */
    public Orders findOrdersById(String id);

    /**
     * 2.4.1.根据选中的ID查询未发货订单
     * 后端编写方法，根据id数组查询未发货订单，前端向后端传递id数据和consignStatus状态，后端返回给前端订单列表
     *
     * @param ids
     * @param consignStatus
     * @return
     */
    public List<Order> findList1(String[] ids, String consignStatus);

    /**
     * 批量发货
     *
     * @param orders
     */
    public void batchSend(List<Order> orders);

}
