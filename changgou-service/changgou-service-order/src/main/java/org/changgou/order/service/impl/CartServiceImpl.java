package org.changgou.order.service.impl;

import com.alibaba.fastjson.JSON;
import org.changgou.goods.feign.SkuFeign;
import org.changgou.goods.feign.SpuFeign;
import org.changgou.goods.pojo.Sku;
import org.changgou.goods.pojo.Spu;
import org.changgou.order.pojo.OrderItem;
import org.changgou.order.service.CartService;
import org.changgou.utils.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/24  19:38
 * @author HeZheng
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private SpuFeign spuFeign;
    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 保存商品进购物车方法
     *
     * @param id  商品skuID
     * @param num 商品数量
     */
    @Override
    public void add(Long id, Integer num) {
        // 从授权信息中取出用户信息 用户名
        String username = TokenDecode.getUserInfo().get("username");
        if(username == null){
            throw new RuntimeException("未登录,请登录");
        }
        if(num<=0){
            // num<=0 说明是删除操作
            redisTemplate.boundHashOps("Cart_"+username).delete(id);
        }
        // 根据SkuId查询出sku信息
        Sku sku = skuFeign.findById(id).getData();
        if(sku != null) {
            // 根据spuId查询出spu信息
            Spu spu = spuFeign.findById(sku.getSpuId()).getData();
            // 将查询到的信息封装进OrderItem类中
            OrderItem orderItem = goods2OrderItem(sku, spu, num);
            // 存入redis中保存
            redisTemplate.boundHashOps("Cart_" + username).put(id.toString(), JSON.toJSONString(orderItem));
        }
    }

    /**
     * 查询购物车列表
     *
     * @return
     */
    @Override
    public List<OrderItem> findAll() {
        List<OrderItem> list = new ArrayList<>();
        String username = TokenDecode.getUserInfo().get("username");
        List<Object> values = redisTemplate.boundHashOps("Cart_" + username).values();
        if(values != null && values.size() > 0) {
            for (Object value : values) {
                String str = (String) value;
                OrderItem orderItem = JSON.parseObject(str, OrderItem.class);
                list.add(orderItem);
            }
        }
        return list;
    }

    private OrderItem goods2OrderItem(Sku sku, Spu spu, Integer num) {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(UUID.randomUUID().toString());
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());
        orderItem.setSpuId(spu.getId());
        orderItem.setSkuId(sku.getId());
        orderItem.setName(sku.getName());
        orderItem.setPrice(sku.getPrice());
        orderItem.setNum(num);
        orderItem.setMoney(sku.getPrice() * num);
        orderItem.setImage(spu.getImage());
        orderItem.setWeight(num * sku.getWeight());
        orderItem.setIsReturn("0");
        return orderItem;
    }
}
