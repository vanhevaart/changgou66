package org.changgou.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.wxpay.sdk.WXPayUtil;
import org.changgou.pay.service.WeixinPayService;
import org.changgou.utils.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Author:  HZ
 * <p> 微信支付Service层
 * Create:  2019/8/27  9:49
 */
@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    /**
     * 微信统一下单接口url
     */
    private static final String PAY_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    /**
     * 微信查询订单接口url
     */
    private static final String QUERY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
    @Autowired
    private Environment env;


    /**
     * 生成支付二维码,支持接收附加参数,用于区分不同类型的订单
     * 附加参数主要包括是否包含用户名和MQ队列名,MQ交换机名
     * @param params 订单信息,用于调用微信远端生成二维码
     * @return 包含了创建二维码数据的Map
     */
    @Override
    public Map<String, String> createNative(Map<String, String> params) {
        // 封装参数数据
        Map<String, String> data = new HashMap<>();
        Map<String, String> result = new HashMap<>();
        // 公众账号ID
        data.put("appid", env.getProperty("weixin.appid"));
        // 商户号
        data.put("mch_id", env.getProperty("weixin.partner"));
        // 随机字符串
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        // 商品描述
        data.put("body", "畅购商场-微信支付");
        // 商户订单号
        String out_trade_no = params.get("out_trade_no");
        data.put("out_trade_no", out_trade_no);
        // 标价金额 单位分
        String total_fee = params.get("total_fee");
        data.put("total_fee", total_fee);
        // 终端IP
        data.put("spbill_create_ip", "127.0.0.1");
        // 通知地址
        data.put("notify_url", env.getProperty("weixin.notifyurl"));
        // 交易类型
        data.put("trade_type", "NATIVE");
        // 添加附加参数
        String username = params.get("username");
        String goodsId = params.get("goodsId");
        String routeKey = params.get("routeKey");
        String exchangeName = params.get("exchangeName");
        // 将附加参数转换成JSON形式的Map数据
        Map<String,String> attachParams = new HashMap<>();
        attachParams.put("username",username);
        attachParams.put("goodsId",goodsId);
        attachParams.put("routeKey",routeKey);
        attachParams.put("exchangeName",exchangeName);
        data.put("attach", JSON.toJSONString(attachParams));
        // sign 签名并转换数据
        try {
            String signedXml = WXPayUtil.generateSignedXml(data, env.getProperty("weixin.partnerkey"));
            // 创建HttpClient客户端
            HttpClient httpClient = new HttpClient(PAY_URL);
            httpClient.setXmlParam(signedXml);
            httpClient.setHttps(true);
            httpClient.post();
            // 获取并处理响应数据
            String content = httpClient.getContent();
            // 利用WX工具包将xml格式的字符串转换成map
            result = WXPayUtil.xmlToMap(content);
            // 添加前端所需数据,进行回显
            result.put("out_trade_no", out_trade_no);
            result.put("goodsId", goodsId);
            result.put("total_fee", total_fee);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("响应数据的Map格式");
        System.out.println(result);
        return result;
    }

    /**
     * 根据商户Id去微信远端查询支付状态
     * 定时任务可调用此方法
     *
     * @param id 商户订单Id
     * @return
     */
    @Override
    public Map<String, String> queryPayStatus(String id) {
        Map<String, String> data = new HashMap<>();
        Map<String, String> result = new HashMap<>();
        // 公众账号ID
        data.put("appid", env.getProperty("weixin.appid"));
        // 商户号
        data.put("mch_id", env.getProperty("weixin.partner"));
        // 商户订单号
        data.put("out_trade_no", id);
        // 随机字符串
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        // sign 签名并转换数据
        try {
            String signedXml = WXPayUtil.generateSignedXml(data, env.getProperty("weixin.partnerkey"));
            // 创建HttpClient客户端
            HttpClient httpClient = new HttpClient(QUERY_URL);
            httpClient.setXmlParam(signedXml);
            httpClient.setHttps(true);
            httpClient.post();
            // 获取并处理响应数据
            String content = httpClient.getContent();
            // 利用WX工具包将xml格式的字符串转换成map
            result = WXPayUtil.xmlToMap(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
