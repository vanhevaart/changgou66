package org.changgou.seckill.listener;

import com.alibaba.fastjson.JSON;
import org.changgou.seckill.service.SeckillOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/31  19:22
 */
@Component
@RabbitListener(queues = "${mq.pay.queue.seckillorder}")
public class SeckillOrderListener {

    @Autowired
    private SeckillOrderService seckillOrderService;

    @RabbitHandler
    public void updateSeckillOrder(String msg){
        // 解析消息
        // 解析数据
        Map<String,Object> message = JSON.parseObject(msg, Map.class);
        Map<String,String> map = (Map<String, String>) message.get("content");
        // 获取return_code字段,判断是否通讯成功
        if("SUCCESS".equals(map.get("return_code"))){
            // 获取附加信息
            String attach = map.get("attach");
            // 获取附加信息中的各详细信息
            Map<String, String> attachParams = JSON.parseObject(attach, Map.class);
            String username = attachParams.get("username");
            String goodsId = attachParams.get("goodsId");
            if("SUCCESS".equals(map.get("result_code"))){
                // 支付成功,调用更新订单方法,进行订单持久化
                String transaction_id = map.get("transaction_id");
                String time_end = map.get("time_end");
                // 调用service层的方法进行订单持久化并删除redis中垃圾数据
                seckillOrderService.updatePayStatus(goodsId,transaction_id,username,time_end);
            }else{
                // 调用service层的方法删除订单,更新下单状态,删除排队数据(不删除下单状态,用户可以在30分内再度发起支付流程,30分后未支付,则删除)
                seckillOrderService.deleteOrder(username,goodsId);
            }
        }
    }
}
