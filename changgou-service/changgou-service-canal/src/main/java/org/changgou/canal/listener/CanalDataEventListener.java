package org.changgou.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.*;
import org.changgou.canal.mq.queue.TopicQueue;
import org.changgou.canal.mq.send.TopicMessageSender;
import org.changgou.content.feign.ContentFeign;
import org.changgou.goods.feign.CacheFeign;
import org.changgou.utils.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/14  21:27
 */
@CanalEventListener
public class CanalDataEventListener {

    @Autowired(required = false)
    private ContentFeign contentFeign;
    @Autowired(required = false)
    private CacheFeign cacheFeign;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private TopicMessageSender topicMessageSender;

    /***
     * 增加数据监听
     * @param eventType
     * @param rowData
     */
    @InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        rowData.getAfterColumnsList().forEach((c) -> System.out.println("By--Annotation: " + c.getName() + " ::   " + c.getValue()));
    }

    /***
     * 修改数据监听
     * @param rowData
     */
    @UpdateListenPoint
    public void onEventUpdate(CanalEntry.RowData rowData) {
        System.out.println("UpdateListenPoint");
        rowData.getAfterColumnsList().forEach((c) -> System.out.println("By--Annotation: " + c.getName() + " ::   " + c.getValue()));
    }

    /***
     * 删除数据监听
     * @param eventType
     */
    @DeleteListenPoint
    public void onEventDelete(CanalEntry.EventType eventType) {
        System.out.println("DeleteListenPoint");
    }

    /***
     * 自定义数据修改监听
     * @param eventType
     * @param rowData
     */
   /* @ListenPoint(destination = "example", schema = "changgou_content", table = {"tb_content_category", "tb_content"}, eventType = CanalEntry.EventType.UPDATE)
    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        // 获取改变的id的值
        String category_id = getColumn(rowData, "category_id");
        // 调用content微服务,获取该id对应的广告数据
        Result<List<Content>> all = contentFeign.findByCategoryId(Long.valueOf(category_id));
        // 将更新后数据推至redis
        redisTemplate.boundValueOps("content_" + category_id).set(JSON.toJSONString(all.getData()));
    }*/

    /**
     * 自定义数据修改监听,只负责监听,广告缓存的数据更新交由具体微服务进行
     * ListenPoint注解中的destination属性表示canal服务端中配置监控实例的名字
     *
     * @param eventType
     * @param rowData
     */
    @ListenPoint(destination = "example", schema = "changgou_content", table = {"tb_content_category", "tb_content"}, eventType = CanalEntry.EventType.UPDATE)
    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        // 获取Content表改变的数据的category_id
        String category_id = getColumn(rowData, "category_id");
        // 调用Content服务的更新广告数据方法,即完成任务,余下业务交由Content服务完成
        contentFeign.refreshCache(Long.valueOf(category_id));
    }

    /**
     * 自定义数据变更监听,只负责监听,缓存的数据更新交由具体微服务进行
     * 监听商品分类,品牌,品牌分类中间表,规格信息,无需获取具体变更的数据,直接调用其他微服务进行redis缓存的更新即可
     * @param eventType
     * @param rowData
     */
    @ListenPoint(destination = "example",schema = "changgou_goods",table = {"tb_category", "tb_brand","tb_spec","tb_category_brand"},
                    eventType = {CanalEntry.EventType.UPDATE,CanalEntry.EventType.DELETE,CanalEntry.EventType.INSERT})
    public void onEventCategorySpecUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        cacheFeign.refreshGoodsCache();
    }

    /**
     * 自定义数据变更监听,只负责监听
     * 监听商品spu,sku 表的变动,推送生成新新静态页的消息到RabbitMQ
     * @param eventType
     * @param rowData
     */
    @ListenPoint(destination = "example",schema = "changgou_goods",table = {"tb_spu"},
            eventType = {CanalEntry.EventType.UPDATE,CanalEntry.EventType.DELETE,CanalEntry.EventType.INSERT})
    public void onEventSpuUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        int number = eventType.getNumber();
        String id = getColumn(rowData, "id");
        Message message = new Message(number,Long.valueOf(id), TopicQueue.TOPIC_QUEUE_SPU,TopicQueue.TOPIC_EXCHANGE_SPU);
        topicMessageSender.sendMessage(message);
    }

    private String getColumn(CanalEntry.RowData rowData, String name) {
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        // 有可能是删除操作
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        for (CanalEntry.Column column : afterColumnsList) {
            if(name.equals(column.getName())) {
                return column.getValue();
            }
        }
        // 有可能是删除操作
        for (CanalEntry.Column column : beforeColumnsList) {
            if(name.equals(column.getName())) {
                return column.getValue();
            }
        }
        return null;
    }
}

