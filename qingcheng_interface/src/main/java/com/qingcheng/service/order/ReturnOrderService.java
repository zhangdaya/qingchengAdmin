package com.qingcheng.service.order;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.order.ReturnOrder;

import java.util.*;

/**
 * returnOrder业务逻辑层
 */
public interface ReturnOrderService {


    public List<ReturnOrder> findAll();


    public PageResult<ReturnOrder> findPage(int page, int size);


    public List<ReturnOrder> findList(Map<String,Object> searchMap);


    public PageResult<ReturnOrder> findPage(Map<String,Object> searchMap,int page, int size);


    public ReturnOrder findById(Long id);

    public void add(ReturnOrder returnOrder);


    public void update(ReturnOrder returnOrder);


    public void delete(Long id);

    /**
     *  同意退款
     *  根据id修改退货退款订单的状态为1，记录当前管理员id和当前时间。
     *  需要做一些必要的验证，退款的金额不能大于原订单的金额。
     * @param id
     * @param money
     * @param adminID
     */
    public void agreeRefund(String id,Integer money,Integer adminID);

    /**
     * 驳回退款请求
     * @param id
     * @param remark 驳回理由
     * @param adminId
     */
    public void rejectRefund(String id,String remark,Integer adminId );

}
