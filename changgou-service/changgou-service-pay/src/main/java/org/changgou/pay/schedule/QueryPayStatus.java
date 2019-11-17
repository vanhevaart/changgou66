package org.changgou.pay.schedule;

import org.changgou.order.feign.OrderFeign;
import org.changgou.order.pojo.Order;
import org.changgou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Author:  HZ
 * <p> 定时轮询redis里的
 * Create:  2019/8/27  23:01
 */
@Component
public class QueryPayStatus {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private OrderFeign orderFeign;
    @Autowired
    private WeixinPayService weixinPayService;

    @Scheduled(cron = "0/5 * * * * *")
    public void queryPay(){
        // 从redis中循环获取OrderList,得到订单ID
        String orderId = stringRedisTemplate.boundListOps("OrderId").rightPop();
        if(orderId != null){
            // 查询订单日志
            Object order = stringRedisTemplate.boundHashOps("Order").get(orderId);
            if(order == null){
                // 如果日志为空,说明该订单已经处理过了,直接返回
                return;
            }
            // 调用微信远端API,查询该订单号的支付状态
            Map<String, String> statusMap = weixinPayService.queryPayStatus(orderId);
            String return_code = statusMap.get("return_code");
            String result_code = statusMap.get("result_code");
            String trade_state = statusMap.get("trade_state");
            if("SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)){
                if("SUCCESS".equals(trade_state)){
                    // 通讯且交易成功,调用order微服务修改订单状态
                    String out_trade_no = statusMap.get("out_trade_no");
                    String transaction_id = statusMap.get("transaction_id");
                    String time_end = statusMap.get("time_end");
                    Order order1 = new Order();
                    order1.setId(out_trade_no);
                    order1.setTransactionId(transaction_id);
                    Date date = null;
                    try {
                        date = new SimpleDateFormat("yyyyMMddhhmmss").parse(time_end);
                        order1.setPayTime(date);
                        orderFeign.updateOrder(order1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else if("notpay".equalsIgnoreCase(trade_state)|| "userpaying".equalsIgnoreCase(trade_state)){
                    // 支付中或未支付状态,则将该订单信息再次存入redis供再次发起查询
                    stringRedisTemplate.boundListOps("OrderId").leftPush(orderId);
                }else {
                    //取消本地订单状态,回滚库存，refund:转入退款 closed：已关闭 revoked：已撤销 payerror:支付失败
                    orderFeign.deleteOrder(orderId);
                }
            }
        }
    }
}
