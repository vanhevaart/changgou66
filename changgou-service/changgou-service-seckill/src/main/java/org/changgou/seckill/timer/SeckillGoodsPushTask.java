package org.changgou.seckill.timer;

import com.alibaba.fastjson.JSON;
import org.changgou.seckill.dao.SeckillGoodsMapper;
import org.changgou.seckill.pojo.SeckillGoods;
import org.changgou.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/28  16:02
 */
@Component
public class SeckillGoodsPushTask {

    /**
     * 为简便,在此处规定秒杀业务redis中的各种key
     */
    public static final String SECKILLGOODS = "SeckillGoods_"; // 秒杀商品列表前缀
    public static final String SECKILL_ORDERS = "SeckillOrders"; // 秒杀商品订单合集的key
    public static final String SECKILL_ORDER_QUEUE = "SeckillOrderQueue"; // 下单排队队列key
    public static final String USER_SECKILL_STATUS = "UserSeckillStatus"; // 用户下单状态,以确认订单是否成功和后续的订单失败库存回滚操作
    public static final String USER_SECKILL_COUNT = "UserSeckillCount"; // 用户秒杀排队状态,用于确认是否重复排队
    public static final String SECKILL_GOODS_QUEUE = "SeckillGoodsQueue_"; // 秒杀商品的库存队列的前缀,一个商品一个队列
    public static final String SECKILL_GOODS_COUNT = "SeckillGoodsCount"; // 商品库存队列的计数器,用于控制商品库存的数据安全

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired(required = false)
    private SeckillGoodsMapper seckillGoodsMapper;

    /**
     * 定时任务,从数据库查询出符合规则的秒杀商品
     * 将数据压入redis
     */
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void pushSeckillGoods2Redis() {
        // 根据当前时间获取时间列表
        List<Date> dateMenus = DateUtil.getDateMenus();
        // 按照时间列表依次去数据库中查询符合条件的商品,压入redis
        for (Date dateMenu : dateMenus) {
            // 转化时间至字符串类型
            String data2str = DateUtil.data2str(dateMenu, DateUtil.PATTERN_YYYYMMDDHH);
            // 装配查询对象
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            // 商品库存需大于0
            criteria.andGreaterThan("stockCount", 0);
            // 商品需审核通过
            criteria.andEqualTo("status", "1");
            // 商品开始秒杀时间需大于当前时间段的起始时间
            criteria.andGreaterThanOrEqualTo("startTime", dateMenu);
            // 商品结束秒杀的时间需要下于当前时间段的结束时间
            criteria.andLessThan("endTime", DateUtil.addDateHour(dateMenu, 2));
            // 去redis中查询该时间段的商品ID信息(hash数据类型的field),如果存在,则说明已经压入redis中,无需再去读取数据库数据
            Set<Object> keys = stringRedisTemplate.boundHashOps(SECKILLGOODS + data2str).keys();
            if(keys != null && keys.size() > 0) {
                criteria.andNotIn("id", keys);
            }
            // 去数据库中查询数据
            List<SeckillGoods> list = seckillGoodsMapper.selectByExample(example);
            System.out.println("查询出数据:"+list.size()+"条");
            //  循环将数据压入redis
            for (SeckillGoods seckillGoods : list) {
                String string = JSON.toJSONString(seckillGoods);
                stringRedisTemplate.boundHashOps(SECKILLGOODS+data2str).put(seckillGoods.getId().toString(),string);
                // 额外的将商品id信息压入一个单独的队列,有多少库存压入多少次,用于高并发下控制商品超卖
                String[] ids = pushIds(seckillGoods.getStockCount(), seckillGoods.getId());
                stringRedisTemplate.boundListOps(SECKILL_GOODS_QUEUE+seckillGoods.getId()).leftPushAll(ids);
                // 商品库存计数器,用于在减少库存时控制商品库存的数据安全
                stringRedisTemplate.boundHashOps(SECKILL_GOODS_COUNT).put(seckillGoods.getId().toString(),seckillGoods.getStockCount().toString());
            }
        }
    }

    /**
     * 将每个商品的id按库存数量压入一个队列,用于控制商品超卖问题
     * @param len 库存数量
     * @param id 商品id
     * @return 转换成String数组的商品id的集合
     */
    private String[] pushIds(int len, Long id){
        String[] ids = new String[len];
        for (int i = 0; i < len; i++) {
            ids[i] = id.toString();
        }
        return ids;
    }
}
