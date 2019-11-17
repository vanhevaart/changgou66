package org.changgou.seckill.task;

import com.alibaba.fastjson.JSON;
import org.changgou.seckill.dao.SeckillGoodsMapper;
import org.changgou.seckill.pojo.SeckillGoods;
import org.changgou.seckill.pojo.SeckillOrder;
import org.changgou.seckill.pojo.SeckillStatus;
import org.changgou.seckill.timer.SeckillGoodsPushTask;
import org.changgou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Author:  HZ
 * <p> 多线程秒杀下单业务
 * Create:  2019/8/28  19:39
 */
@Component
@Transactional
public class MultiThreadingCreateOrder {

    @Autowired(required = false)
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IdWorker idWorker;

    @Async
    public void add() {
        // 为限制并发,使用redis队列进行订单排队处理,从订单排队队列中弹出数据
        String rightPop = stringRedisTemplate.boundListOps(SeckillGoodsPushTask.SECKILL_ORDER_QUEUE).rightPop();
        SeckillStatus seckillStatus = JSON.parseObject(rightPop, SeckillStatus.class);
        // 获取下单队列中的数据
        Long id = seckillStatus.getGoodsId();
        String time = seckillStatus.getTime();
        String username = seckillStatus.getUsername();
        // 根据商品id从该商品的队列中获取信息
        String pop = stringRedisTemplate.boundListOps(SeckillGoodsPushTask.SECKILL_GOODS_QUEUE + id).rightPop();
        if(pop == null) {
            // 说明该商品已经售罄,需要清理用户排队信息
            clearUserQueue(username,id.toString());
            throw new RuntimeException("商品已售罄");
        }
        // 从redis中查询该id和时间段的商品信息
        Object object = stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.SECKILLGOODS + time).get(id.toString());
        if(object == null) {
            clearUserQueue(username,id.toString());
            throw new RuntimeException("商品不存在或已下架");
        }
        // JSON数据转换成SeckillGoods对象
        SeckillGoods seckillGoods = JSON.parseObject((String) object, SeckillGoods.class);
        // 封装订单数据
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setSeckillId(seckillGoods.getId());
        seckillOrder.setMoney(seckillGoods.getCostPrice());
        seckillOrder.setUserId(username);
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setStatus("0");
        // 下单,下临时订单到redis中,用户支付后再将临时订单同步至数据库,并删除redis中的临时订单
        stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.SECKILL_ORDERS).put(username+"_"+id, JSON.toJSONString(seckillOrder));
        // 下单后更新用户的下单状态
        seckillStatus.setStatus(2);
        // 往用户下单状态中写入了订单ID和商品价格,供前端查询展示,并用于生成微信支付二维码
        seckillStatus.setOrderId(seckillOrder.getId());
        seckillStatus.setMoney(Float.valueOf(seckillGoods.getCostPrice()));
        // 更新用户下单状态为秒杀等待支付
        stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.USER_SECKILL_STATUS).put(username+"_"+id, JSON.toJSONString(seckillStatus));
        // 重重用户的秒杀排队状态,可以重新进行秒杀排队了
        stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.USER_SECKILL_COUNT).delete(username+"_"+id);
        // 减少redis中该商品的库存,使用redis的自增减来减少库存,确保高并发下的数据安全
        Long increment = stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.SECKILL_GOODS_COUNT).increment(id.toString(), -1);
        seckillGoods.setStockCount(Math.toIntExact(increment));
        // 需要对库存进行判断
        if(increment <= 0) {
            // 如果商品售罄,则更新库存信息至数据库中
            seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
            // 并清空redis中的数据
            stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.SECKILLGOODS + time).delete(id.toString());
        } else {
            // 如果还有库存,则直接更新redis中数据
            String jsonString = JSON.toJSONString(seckillGoods);
            stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.SECKILLGOODS + time).put(id.toString(), jsonString);
        }
    }

    /**
     * 如果商品售罄,需要清理当前用户的下单状态和排队状态数据
     *
     * @param username 用户名
     */
    private void clearUserQueue(String username,String id) {
        stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.USER_SECKILL_STATUS).delete(username+"_"+id);
        stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.USER_SECKILL_COUNT).delete(username+"_"+id);
    }
}
