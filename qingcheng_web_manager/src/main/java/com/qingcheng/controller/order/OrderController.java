package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.PageResult;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.Orders;
import com.qingcheng.service.order.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @GetMapping("/findAll")
    public List<Order> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Order> findPage(int page, int size) {
        return orderService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Order> findList(@RequestBody Map<String, Object> searchMap) {
        return orderService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Order> findPage(@RequestBody Map<String, Object> searchMap, int page, int size) {
        return orderService.findPage(searchMap, page, size);
    }

    @GetMapping("/findById")
    public Order findById(String id) {
        return orderService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody Order order) {
        orderService.add(order);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Order order) {
        orderService.update(order);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(String id) {
        orderService.delete(id);
        return new Result();
    }

    /**
     * 查询订单id
     *
     * @param id
     */
    @GetMapping("/findOrdersById")
    public Orders findOrdersById(String id) {
        Orders ordersById = orderService.findOrdersById(id);
        return ordersById;
    }

    /**
     * 2.4.1.根据选中的ID查询未发货订单
     * 后端编写方法，根据id数组查询未发货订单，前端向后端传递id数据和consignStatus状态，后端返回给前端订单列表
     *
     * @param ids
     * @param consignStatus
     * @return
     */
    @PostMapping("/findList1")
    public List<Order> findList1(@RequestBody String[] ids, String consignStatus) {
        return orderService.findList1(ids, consignStatus);
    }

    /**
     * 批量发货
     * 批量发货，前端给后端发送订单集合（post），后端代码接受后批量修改订单状态、发货状态、发货时间
     *
     * @param orders
     */
    @PostMapping("/batchSend")
    public Result batchSend(@RequestBody List<Order> orders) {
        orderService.batchSend(orders);
        return new Result();
    }


}
