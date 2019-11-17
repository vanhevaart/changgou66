package org.changgou.canal.mq.queue;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.BindingProvider;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/20  16:09
 */
@Configuration
public class TopicQueue {

    public static final String TOPIC_QUEUE_SPU = "topic.queue.spu";
    public static final String TOPIC_EXCHANGE_SPU = "topic.exchange.spu";

    /**
     * topic模式,SPU变更队列
     * @return
     */
    @Bean
    public Queue topicQueueSpu(){
        return new Queue(TOPIC_QUEUE_SPU);
    }

    /**
     * SPU队列的Topic模式交换机
     * @return
     */
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE_SPU);
    }

    /**
     * 绑定队列到交换机
     * @return
     */
    @Bean
    public Binding topicBinding(){
        return BindingBuilder.bind(topicQueueSpu()).to(topicExchange()).with(TOPIC_QUEUE_SPU);
    }

}
