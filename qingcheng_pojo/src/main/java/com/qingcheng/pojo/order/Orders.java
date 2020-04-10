package com.qingcheng.pojo.order;

import java.io.Serializable;
import java.util.List;

/**
 * @program: qingchengAdmin
 * @description:
 * @author: 张梦雅
 * @create: 2020-04-10 13:50
 */
public class Orders implements Serializable {

    private Order order;

    private List<OrderItem> orderItemList;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
