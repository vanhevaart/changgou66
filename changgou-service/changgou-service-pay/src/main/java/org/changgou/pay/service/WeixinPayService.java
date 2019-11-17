package org.changgou.pay.service;

import java.util.Map;

/**
 * 微信支付Service层接口
 */
public interface WeixinPayService {

    /**
     * 生成支付二维码,支持接收附加参数,用于区分不同类型的订单
     * 附加参数主要包括是否包含用户名和MQ队列名,MQ交换机名
     * @param params 订单信息,用于调用微信远端生成二维码
     * @return 包含了创建二维码数据的Map
     */
    Map<String,String> createNative(Map<String, String> params);

    /**
     * 根据商户Id去微信远端查询支付状态
     * 定时任务可调用此方法
     * @param id 商户订单Id
     * @return
     */
    Map<String, String> queryPayStatus(String id);
}
