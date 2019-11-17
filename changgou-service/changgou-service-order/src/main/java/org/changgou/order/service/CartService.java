package org.changgou.order.service;

import org.changgou.order.pojo.OrderItem;

import java.util.List;

/**
 * 购物车service层接口
 */
public interface CartService {

    /**
     * 保存商品进购物车方法
     *
     * @param id  商品skuID
     * @param num 商品数量
     */
    void add(Long id, Integer num);

    /**
     * 查询购物车列表
     *
     * @return
     */
    List<OrderItem> findAll();
}
