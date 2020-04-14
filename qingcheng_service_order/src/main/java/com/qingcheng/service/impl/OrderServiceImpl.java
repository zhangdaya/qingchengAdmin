package com.qingcheng.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.OrderConfigMapper;
import com.qingcheng.dao.OrderItemMapper;
import com.qingcheng.dao.OrderLogMapper;
import com.qingcheng.dao.OrderMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.order.*;
import com.qingcheng.service.order.OrderService;
import com.qingcheng.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderLogMapper orderLogMapper;

    @Autowired
    private OrderConfigMapper orderConfigMapper;

    /**
     * 返回全部记录
     * @return
     */
    @Override
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    @Override
    public PageResult<Order> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Order> orders = (Page<Order>) orderMapper.selectAll();
        return new PageResult<Order>(orders.getTotal(),orders.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    @Override
    public List<Order> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return orderMapper.selectByExample(example);
    }

    /**
     * 2.4.1.根据选中的ID查询未发货订单
     * 后端编写方法，根据id数组查询未发货订单，前端向后端传递id数据和consignStatus状态，后端返回给前端订单列表
     * @param ids
     * @param consignStatus
     * @return
     */
    @Override
    public List<Order> findList1(String[] ids,String consignStatus) {
        Example example=new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        //数组转换为集合List ：Arrays.asList(ids)
        criteria.andIn("id",Arrays.asList(ids));
        //批量发货只包括等待发货状态的订单；
        criteria.andEqualTo("consignStatus",0);

        List<Order> order = orderMapper.selectByExample(example);
        return order;
    }

    /**
     * 批量发货
     * 批量发货，前端给后端发送订单集合（post），后端代码接受后批量修改订单状态、发货状态、发货时间
     * @param orders
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSend(List<Order> orders) {
        //判断运单号和物流公司是否为空
        for (Order o : orders ){
            if (o.getShippingCode()==null|| o.getShippingName()==null){
                throw new RuntimeException("请选择快递公司和填写快递单号");
            }
        }
        //循环订单
        for (Order order : orders ){
            //将订单状态变为已发货：2
            order.setOrderStatus("2");
            //将发货状态变为已发货：1
            order.setConsignStatus("1");
            //发货时间也改变
            order.setConsignTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
            //============================================================
            //记录订单日志
            OrderLog orderLog=new OrderLog();
            orderLog.setId(idWorker.nextId()+"" );
            //系统
            orderLog.setOperater("system");
            //操作时间
            orderLog.setOperateTime(new Date());
            //订单状态：已发货
            orderLog.setOrderStatus("2");
            //支付状态:已支付
            orderLog.setPayStatus("1");
            //将发货状态变为已发货：1
            orderLog.setConsignStatus("1");

            orderLog.setOrderId(order.getId());
            orderLogMapper.insert(orderLog);
        }
    }

    /**
     * 订单超时处理逻辑
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void orderTimeOutLogic() {
        //只有一条数据，id始终为1
        //订单超时未付款 自动关闭,查询超时时间
        OrderConfig orderConfig = orderConfigMapper.selectByPrimaryKey(1);
        //超时时间（分） 60
        Integer orderTimeout = orderConfig.getOrderTimeout();
        //得到超时的时间点
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(orderTimeout);
        //查询超过时间，没有付款，没有删除的订单
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLessThan("createTime",localDateTime);
        criteria.andEqualTo("orderStatus",0);
        criteria.andEqualTo("isDelete","0");
        List<Order> orders = orderMapper.selectByExample(example);
        for (Order o:orders){
           //记录订单变动日志
            OrderLog orderLog=new OrderLog();
            // 操作员：系统
            orderLog.setOperater("System");
            orderLog.setOperateTime(new Date());
            orderLog.setOrderId(o.getId());
            //0待付款、1待发货、2已发货、3已完成、4已关闭
            orderLog.setOrderStatus("4");
            //0未支付、1已支付、2已退款
            orderLog.setPayStatus(o.getPayStatus());
            //0未发货、1已发货
            orderLog.setConsignStatus(o.getConsignStatus());
            //备注
            orderLog.setRemarks("超时订单，系统自动关闭");
            orderLogMapper.insert(orderLog);

            //更改订单状态
            o.setOrderStatus("4");
            o.setCloseTime(new Date());
            orderMapper.updateByPrimaryKeySelective(o);

        }


    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<Order> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Order> orders = (Page<Order>) orderMapper.selectByExample(example);
        return new PageResult<Order>(orders.getTotal(),orders.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    @Override
    public Order findById(String id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param order
     */
    @Override
    public void add(Order order) {
        orderMapper.insert(order);
    }

    /**
     * 修改
     * @param order
     */
    @Override
    public void update(Order order) {
        orderMapper.updateByPrimaryKeySelective(order);
    }

    /**
     *  删除
     * @param id
     */
    @Override
    public void delete(String id) {
        orderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 查询订单id
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orders findOrdersById(String id) {
        //查询order的id
        Order order = orderMapper.selectByPrimaryKey(id);
        //查询orderItem的id
        Example example=new Example(OrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId",id);
        List<OrderItem> orderItems = orderItemMapper.selectByExample(example);
        //组合成orders
        Orders orders = new Orders();
        orders.setOrder(order);
        orders.setOrderItemList(orderItems);
        return orders;
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 订单id
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andLike("id","%"+searchMap.get("id")+"%");
            }
            // 支付类型，1、在线支付、0 货到付款
            if(searchMap.get("payType")!=null && !"".equals(searchMap.get("payType"))){
                criteria.andLike("payType","%"+searchMap.get("payType")+"%");
            }
            // 物流名称
            if(searchMap.get("shippingName")!=null && !"".equals(searchMap.get("shippingName"))){
                criteria.andLike("shippingName","%"+searchMap.get("shippingName")+"%");
            }
            // 物流单号
            if(searchMap.get("shippingCode")!=null && !"".equals(searchMap.get("shippingCode"))){
                criteria.andLike("shippingCode","%"+searchMap.get("shippingCode")+"%");
            }
            // 用户名称
            if(searchMap.get("username")!=null && !"".equals(searchMap.get("username"))){
                criteria.andLike("username","%"+searchMap.get("username")+"%");
            }
            // 买家留言
            if(searchMap.get("buyerMessage")!=null && !"".equals(searchMap.get("buyerMessage"))){
                criteria.andLike("buyerMessage","%"+searchMap.get("buyerMessage")+"%");
            }
            // 是否评价
            if(searchMap.get("buyerRate")!=null && !"".equals(searchMap.get("buyerRate"))){
                criteria.andLike("buyerRate","%"+searchMap.get("buyerRate")+"%");
            }
            // 收货人
            if(searchMap.get("receiverContact")!=null && !"".equals(searchMap.get("receiverContact"))){
                criteria.andLike("receiverContact","%"+searchMap.get("receiverContact")+"%");
            }
            // 收货人手机
            if(searchMap.get("receiverMobile")!=null && !"".equals(searchMap.get("receiverMobile"))){
                criteria.andLike("receiverMobile","%"+searchMap.get("receiverMobile")+"%");
            }
            // 收货人地址
            if(searchMap.get("receiverAddress")!=null && !"".equals(searchMap.get("receiverAddress"))){
                criteria.andLike("receiverAddress","%"+searchMap.get("receiverAddress")+"%");
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if(searchMap.get("sourceType")!=null && !"".equals(searchMap.get("sourceType"))){
                criteria.andLike("sourceType","%"+searchMap.get("sourceType")+"%");
            }
            // 交易流水号
            if(searchMap.get("transactionId")!=null && !"".equals(searchMap.get("transactionId"))){
                criteria.andLike("transactionId","%"+searchMap.get("transactionId")+"%");
            }
            // 订单状态
            if(searchMap.get("orderStatus")!=null && !"".equals(searchMap.get("orderStatus"))){
                criteria.andLike("orderStatus","%"+searchMap.get("orderStatus")+"%");
            }
            // 支付状态
            if(searchMap.get("payStatus")!=null && !"".equals(searchMap.get("payStatus"))){
                criteria.andLike("payStatus","%"+searchMap.get("payStatus")+"%");
            }
            // 发货状态
            if(searchMap.get("consignStatus")!=null && !"".equals(searchMap.get("consignStatus"))){
                criteria.andLike("consignStatus","%"+searchMap.get("consignStatus")+"%");
            }
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andLike("isDelete","%"+searchMap.get("isDelete")+"%");
            }

            // 数量合计
            if(searchMap.get("totalNum")!=null ){
                criteria.andEqualTo("totalNum",searchMap.get("totalNum"));
            }
            // 金额合计
            if(searchMap.get("totalMoney")!=null ){
                criteria.andEqualTo("totalMoney",searchMap.get("totalMoney"));
            }
            // 优惠金额
            if(searchMap.get("preMoney")!=null ){
                criteria.andEqualTo("preMoney",searchMap.get("preMoney"));
            }
            // 邮费
            if(searchMap.get("postFee")!=null ){
                criteria.andEqualTo("postFee",searchMap.get("postFee"));
            }
            // 实付金额
            if(searchMap.get("payMoney")!=null ){
                criteria.andEqualTo("payMoney",searchMap.get("payMoney"));
            }
            // 根据 id 数组查询
            if(searchMap.get("ids")!=null ){
                criteria.andIn("id", Arrays.asList((String[])searchMap.get("ids")));
            }

        }
        return example;
    }

}
