package org.changgou.order.mq.listener;

import com.alibaba.fastjson.JSON;
import org.changgou.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/27  17:53
 */
@Component
@RabbitListener(queues = "direct.queue.order")
public class OrderListener {

    @Autowired
    private OrderService orderService;

    /**
     * 监听MQ的订单变更队列,读取消息,执行不同的订单更新操作
     * @param msg
     * 20190827172002格式日期
     */
    @RabbitHandler
    public void updateOrder(String msg) {
        // 解析数据
        Map<String,Object> message = JSON.parseObject(msg, Map.class);
        Map<String,String> map = (Map<String, String>) message.get("content");
        // 获取return_code字段,判断是否通讯成功
        if("SUCCESS".equals(map.get("return_code"))){
            if("SUCCESS".equals(map.get("result_code"))){
                // 支付成功,调用更新订单方法
                String out_trade_no = map.get("out_trade_no");
                String transaction_id = map.get("transaction_id");
                String time_end = map.get("time_end");
                Date date = null;
                try {
                    date = new SimpleDateFormat("yyyyMMddhhmmss").parse(time_end);
                    orderService.updateOrder(out_trade_no,transaction_id,date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else {
                // 支付失败,调用删除订单方法,进行库存回滚
                String out_trade_no = map.get("out_trade_no");
                orderService.deleteOrder(out_trade_no);
            }
        }
    }
}
