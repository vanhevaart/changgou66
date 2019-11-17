package org.changgou.canal.mq.send;

import com.alibaba.fastjson.JSON;
import org.changgou.utils.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/20  16:19
 */
@Component
public class TopicMessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(Message message){
        rabbitTemplate.convertAndSend(message.getExechange(),message.getRoutekey(), JSON.toJSONString(message));
    }
}
