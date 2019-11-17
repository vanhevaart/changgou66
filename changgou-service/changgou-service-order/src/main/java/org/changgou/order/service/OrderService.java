package org.changgou.order.service;

import com.github.pagehelper.PageInfo;
import org.changgou.order.pojo.Order;

import java.util.Date;
import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:Order业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface OrderService {

    /**
     * 根据失败的支付结果更新订单状态
     * @param id 订单Id
     */
    void deleteOrder(String id);

    /**
     * 根据成功的支付结果更新订单状态
     * @param id 订单Id
     * @param transactionId 微信方生成的交易流水号
     */
    void updateOrder(String id, String transactionId, Date payTime);

    /***
     * Order多条件分页查询
     * @param order
     * @param page
     * @param size
     * @return
     */
    PageInfo<Order> findPage(Order order, int page, int size);

    /***
     * Order分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Order> findPage(int page, int size);

    /***
     * Order多条件搜索方法
     * @param order
     * @return
     */
    List<Order> findList(Order order);

    /***
     * 删除Order
     * @param id
     */
    void delete(String id);

    /***
     * 修改Order数据
     * @param order
     */
    void update(Order order);

    /***
     * 新增Order
     * @param order 返回订单信息供前端和支付微服务使用
     */
    Order add(Order order);

    /**
     * 根据ID查询Order
     * @param id
     * @return
     */
     Order findById(String id);

    /***
     * 查询所有Order
     * @return
     */
    List<Order> findAll();
}
