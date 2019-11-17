package org.changgou.pay.mq.queue;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Author:  HZ
 * <p> order变更MQ队列信息
 * Create:  2019/8/20  16:09
 */
@Configuration
public class DirectQueue {

    public static final String DIRECT_QUEUE_ORDER = "direct.queue.order";
    public static final String DIRECT_EXCHANGE_ORDER = "direct.exchange.order";
    @Autowired
    private Environment env;

    /**
     * direct,order变更队列
     *
     * @return
     */
    @Bean
    public Queue directQueueOrder() {
        return new Queue(DIRECT_QUEUE_ORDER);
    }

    /**
     * order队列的direct模式交换机
     *
     * @return
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE_ORDER, true, false);
    }

    /**
     * 绑定队列到交换机
     *
     * @return
     */
    @Bean
    public Binding directBinding() {
        return BindingBuilder.bind(directQueueOrder()).to(directExchange()).with(DIRECT_QUEUE_ORDER);
    }

    /**
     * direct seckill的消息队列
     * @return
     */
    @Bean
    public Queue seckillQueue(){
        return new Queue(env.getProperty("mq.pay.queue.seckillorder"),true);
    }

    /**
     * direct seckill交换机
     * @return
     */
    @Bean
    public Exchange seckillExchange(){
        return new DirectExchange(env.getProperty("mq.pay.exchange.seckillorder"),true,false);
    }

    /**
     * 绑定seckill的队列和交换机
     * @return
     */
    @Bean
    public Binding seckillBinding(){
        return BindingBuilder.bind(seckillQueue()).to(seckillExchange()).with(env.getProperty("mq.pay.routing.seckillkey")).noargs();
    }

}
