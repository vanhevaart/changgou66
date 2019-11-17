package org.changgou.seckill.service;

import com.github.pagehelper.PageInfo;
import org.changgou.seckill.pojo.SeckillOrder;
import org.changgou.seckill.pojo.SeckillStatus;

import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:SeckillOrder业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SeckillOrderService {

    /**
     * 更新用户订单状态
     * @param id 商品id
     * @param transaction_id 支付后支付方返回的交易流水号
     * @param username 用户名
     * @param time_end 支付时间
     */
    void updatePayStatus(String id, String transaction_id,String username,String time_end);

    /**
     * 删除用户秒杀订单相关信息,需要进行商品库存的回滚
     * @param username 用户名
     * @param id 商品id
     */
    void deleteOrder(String username, String id);

    /**
     * 根据用户名查询下单状态
     * @param username
     * @param id 商品Id
     * @return
     */
    SeckillStatus queryStatus(String username,String id);

    /**
     * 秒杀商品下单
     * @param id 秒杀商品id(skuId)
     * @param time 秒杀商品所属时间段,用于从redis中读取数据的key
     * @param username 下单用户
     * @return 是否下单成功
     */
    boolean add(String id, String time, String username);

    /***
     * SeckillOrder多条件分页查询
     * @param seckillOrder
     * @param page
     * @param size
     * @return
     */
    PageInfo<SeckillOrder> findPage(SeckillOrder seckillOrder, int page, int size);

    /***
     * SeckillOrder分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<SeckillOrder> findPage(int page, int size);

    /***
     * SeckillOrder多条件搜索方法
     * @param seckillOrder
     * @return
     */
    List<SeckillOrder> findList(SeckillOrder seckillOrder);

    /***
     * 删除SeckillOrder
     * @param id
     */
    void delete(Long id);

    /***
     * 修改SeckillOrder数据
     * @param seckillOrder
     */
    void update(SeckillOrder seckillOrder);

    /***
     * 新增SeckillOrder
     * @param seckillOrder
     */
    void add(SeckillOrder seckillOrder);

    /**
     * 根据ID查询SeckillOrder
     * @param id
     * @return
     */
     SeckillOrder findById(Long id);

    /***
     * 查询所有SeckillOrder
     * @return
     */
    List<SeckillOrder> findAll();

}
