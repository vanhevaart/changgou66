package org.changgou.pay.controller;

import com.alibaba.fastjson.JSON;
import com.github.wxpay.sdk.WXPayUtil;
import org.changgou.entity.Result;
import org.changgou.entity.StatusCode;
import org.changgou.pay.mq.queue.DirectQueue;
import org.changgou.pay.mq.send.DirectMessageSender;
import org.changgou.pay.service.WeixinPayService;
import org.changgou.utils.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/27  9:48
 */
@RestController
@RequestMapping("/pay")
public class WeixinPayController {

    @Autowired
    private WeixinPayService weixinPayService;
    @Autowired
    private DirectMessageSender messageSender;

    /**
     * 生成支付二维码,支持接收附加参数,用于区分不同类型的订单
     * 附加参数主要包括是否包含用户名和MQ队列名,MQ交换机名
     *
     * @param params 订单信息,用于调用微信远端生成二维码
     * @return 包含了创建二维码数据的Map
     */
    @PostMapping("/create")
    public Result<Map<String, String>> createNative(@RequestBody Map<String, String> params) {
        Map<String, String> aNative = weixinPayService.createNative(params);
        return new Result<>(true, StatusCode.OK, "生成二维码成功", aNative);
    }

    /**
     * 根据商户Id去微信远端查询支付状态
     * 定时任务可调用此方法
     *
     * @param id 商户订单Id
     * @return
     */
    @RequestMapping("/queryStates/{id}")
    public Result<Map<String, String>> queryPayStatus(@PathVariable(name = "id") String id) {
        Map<String, String> map = weixinPayService.queryPayStatus(id);
        return new Result<>(true, StatusCode.OK, "查询订单状态成功", map);
    }

    /**
     * 支付成功的回调方法,
     * 支付成功后写更改订单状态的消息进入RabbitMq
     *
     * @param request 请求信息
     */
    @RequestMapping("/notify")
    public void notifyUrl(HttpServletRequest request) {
        try {
            ServletInputStream inputStream = request.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] array = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(array)) != -1) {
                byteArrayOutputStream.write(array, 0, len);
            }
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            inputStream.close();
            String content = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
            // XML转换成map
            Map<String, String> map = WXPayUtil.xmlToMap(content);
            String return_code = map.get("return_code");
            if("SUCCESS".equals(return_code)) {
                // 获取附加信息
                String attach = map.get("attach");
                // 获取附加信息中的各详细信息
                Map<String, String> attachParams = JSON.parseObject(attach, Map.class);
                String routeKey = attachParams.get("routeKey");
                String exchangeName = attachParams.get("exchangeName");
                // 说明支付成功,需要写消息进入RabbitMq
                Message message = new Message(2, map, routeKey, exchangeName);
                messageSender.sendMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
