package org.changgou.rabbitmq.listener.item;

import com.alibaba.fastjson.JSON;
import org.changgou.item.feign.PageFeign;
import org.changgou.utils.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/20  16:57
 */
@Component
@RabbitListener(queues = {"topic.queue.spu"})
public class HtmlGenerateListener {

    @Autowired
    private PageFeign pageFeign;

    @RabbitHandler
    public void getInfo(String msg){
        // 转换消息
        Message message = JSON.parseObject(msg, Message.class);
        if(message.getCode() == 2){
            pageFeign.createHtml(Long.parseLong(message.getContent().toString()));
        }
    }
}
