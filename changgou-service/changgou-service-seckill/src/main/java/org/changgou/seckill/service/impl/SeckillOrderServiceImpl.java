package org.changgou.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.changgou.entity.StatusCode;
import org.changgou.seckill.dao.SeckillGoodsMapper;
import org.changgou.seckill.dao.SeckillOrderMapper;
import org.changgou.seckill.pojo.SeckillGoods;
import org.changgou.seckill.pojo.SeckillOrder;
import org.changgou.seckill.pojo.SeckillStatus;
import org.changgou.seckill.service.SeckillOrderService;
import org.changgou.seckill.task.MultiThreadingCreateOrder;
import org.changgou.seckill.timer.SeckillGoodsPushTask;
import org.changgou.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:SeckillOrder业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private MultiThreadingCreateOrder multiThreadingCreateOrder;
    @Autowired(required = false)
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired(required = false)
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 更新用户订单状态
     *
     * @param id   商品id
     * @param transaction_id 支付后支付方返回的交易流水号
     * @param username       用户名
     * @param time_end       支付时间
     */
    @Override
    public void updatePayStatus(String id, String transaction_id, String username, String time_end) {
        // 从redis中查询出用户秒杀订单状态数据
        Object object = stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.USER_SECKILL_STATUS).get(username+"_"+id);
        SeckillStatus seckillStatus = JSON.parseObject((String) object, SeckillStatus.class);
        // 用户已经下单
        if(seckillStatus != null && seckillStatus.getStatus() == 2) {
            // 取出用户的订单数据
            Object object1 = stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.SECKILL_ORDERS).get(username + "_" + id);
            SeckillOrder seckillOrder = JSON.parseObject((String) object1, SeckillOrder.class);
            // 更新用户订单数据
            if(seckillOrder != null && "0".equals(seckillOrder.getStatus())) {
                Date payTime = DateUtil.formatStr4WeiXin(time_end);
                seckillOrder.setPayTime(payTime);
                seckillOrder.setStatus("1");
                seckillOrder.setTransactionId(transaction_id);
                // 持久化订单数据
                seckillOrderMapper.insertSelective(seckillOrder);
                // 删除redis中用户的订单数据,下单状态数据,排队状态数据
                stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.SECKILL_ORDERS).delete(username + "_" + id);
                stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.USER_SECKILL_STATUS).delete(username + "_" + id);
                stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.USER_SECKILL_COUNT).delete(username + "_" + id);
            }
        }
    }

    /**
     * 删除用户秒杀订单相关信息,需要进行商品库存的回滚
     *
     * @param username     用户名
     * @param id 商品id
     */
    @Override
    public void deleteOrder(String username, String id) {
        // 获取用户的下单状态和订单数据
        Object objectOrder = stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.SECKILL_ORDERS).get(username + "_" + id);
        Object objectStatus = stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.USER_SECKILL_STATUS).get(username + "_" + id);
        if(objectOrder != null && objectStatus != null) {
            // 已下单未支付的状态,删除订单数据
            stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.SECKILL_ORDERS).delete(username + "_" + id);
            // 获取下单状态中的信息,进行商品库存的回滚
            SeckillStatus seckillStatus = JSON.parseObject((String) objectStatus, SeckillStatus.class);
            // 商品id
            Long goodsId = seckillStatus.getGoodsId();
            // 秒杀商品所属的时间段
            String time = seckillStatus.getTime();
            // 获取商品数据
            Object objectGoods = stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.SECKILLGOODS + time).get(goodsId.toString());
            SeckillGoods seckillGoods = JSON.parseObject((String) objectGoods, SeckillGoods.class);
            // 如果是最后一件商品,由于商品数据已经从redis中删除,需要从数据库中获取
            if(seckillGoods == null) {
                seckillGoods = seckillGoodsMapper.selectByPrimaryKey(id);
            }
            // 将商品压入队列
            stringRedisTemplate.boundListOps(SeckillGoodsPushTask.SECKILL_GOODS_QUEUE + id).leftPushAll(id);
            // 将商品数量加入库存计数器
            Long increment = stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.SECKILL_GOODS_COUNT).increment(id, 1);
            // 更新商品列表中的数据
            seckillGoods.setStockCount(Math.toIntExact(increment));
            String string = JSON.toJSONString(seckillGoods);
            stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.SECKILLGOODS + time).put(id , seckillGoods);
            // 删除用户下单状态和排队状态数据
            stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.USER_SECKILL_STATUS).delete(username + "_" + id);
            stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.USER_SECKILL_COUNT).delete(username + "_" + id);
        }
    }

    /**
     * 根据用户名查询订单状态
     *
     * @param username 用户名
     * @return 用户订单状态信息, 可能包含了订单ID和商品价格等信息可供前端用于请求微信接口生成支付二维码
     */
    @Override
    public SeckillStatus queryStatus(String username, String id) {
        Object object = stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.USER_SECKILL_STATUS).get(username + "_" + id);
        if(object != null) {
            SeckillStatus seckillStatus = JSON.parseObject((String) object, SeckillStatus.class);
            return seckillStatus;
        }
        return null;
    }

    /**
     * 秒杀商品下单
     *
     * @param id       秒杀商品id(skuId)
     * @param time     秒杀商品所属时间段,用于从redis中读取数据的key
     * @param username 下单用户
     * @return 是否下单成功
     */
    @Override
    public boolean add(String id, String time, String username) {
        try {
            // 为限制并发,使用redis队列进行订单排队处理
            // 更新用户秒杀排队状态的数据,判断是否是重复排队
            Long increment = stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.USER_SECKILL_COUNT).increment(username + "_" + id, 1);
            if(increment > 1) {
                throw new RuntimeException(String.valueOf(StatusCode.REPERROR));
            }
            // 将数据封装进SeckillStatus对象中,该对象即代表了下单队列中的一条数据,用于队列操作,给下单具体操作提供数据
            SeckillStatus seckillStatus = new SeckillStatus(username, new Date(), 1, Long.valueOf(id), time);
            // 压入队列,进行订单排队
            stringRedisTemplate.boundListOps(SeckillGoodsPushTask.SECKILL_ORDER_QUEUE).leftPush(JSON.toJSONString(seckillStatus));
            // 往redis中存入订单状态seckillStatus信息,以确认订单是否成功和后续的订单失败库存回滚操作
            stringRedisTemplate.boundHashOps(SeckillGoodsPushTask.USER_SECKILL_STATUS).put(username + "_" + id, JSON.toJSONString(seckillStatus));
            multiThreadingCreateOrder.add();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * SeckillOrder条件+分页查询
     *
     * @param seckillOrder 查询条件
     * @param page         页码
     * @param size         页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<SeckillOrder> findPage(SeckillOrder seckillOrder, int page, int size) {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建
        Example example = createExample(seckillOrder);
        //执行搜索
        return new PageInfo<SeckillOrder>(seckillOrderMapper.selectByExample(example));
    }

    /**
     * SeckillOrder分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<SeckillOrder> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page, size);
        //分页查询
        return new PageInfo<SeckillOrder>(seckillOrderMapper.selectAll());
    }

    /**
     * SeckillOrder条件查询
     *
     * @param seckillOrder
     * @return
     */
    @Override
    public List<SeckillOrder> findList(SeckillOrder seckillOrder) {
        //构建查询条件
        Example example = createExample(seckillOrder);
        //根据构建的条件查询数据
        return seckillOrderMapper.selectByExample(example);
    }


    /**
     * SeckillOrder构建查询对象
     *
     * @param seckillOrder
     * @return
     */
    public Example createExample(SeckillOrder seckillOrder) {
        Example example = new Example(SeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if(seckillOrder != null) {
            // 主键
            if(!StringUtils.isEmpty(seckillOrder.getId())) {
                criteria.andEqualTo("id", seckillOrder.getId());
            }
            // 秒杀商品ID
            if(!StringUtils.isEmpty(seckillOrder.getSeckillId())) {
                criteria.andEqualTo("seckillId", seckillOrder.getSeckillId());
            }
            // 支付金额
            if(!StringUtils.isEmpty(seckillOrder.getMoney())) {
                criteria.andEqualTo("money", seckillOrder.getMoney());
            }
            // 用户
            if(!StringUtils.isEmpty(seckillOrder.getUserId())) {
                criteria.andEqualTo("userId", seckillOrder.getUserId());
            }
            // 创建时间
            if(!StringUtils.isEmpty(seckillOrder.getCreateTime())) {
                criteria.andEqualTo("createTime", seckillOrder.getCreateTime());
            }
            // 支付时间
            if(!StringUtils.isEmpty(seckillOrder.getPayTime())) {
                criteria.andEqualTo("payTime", seckillOrder.getPayTime());
            }
            // 状态，0未支付，1已支付
            if(!StringUtils.isEmpty(seckillOrder.getStatus())) {
                criteria.andEqualTo("status", seckillOrder.getStatus());
            }
            // 收货人地址
            if(!StringUtils.isEmpty(seckillOrder.getReceiverAddress())) {
                criteria.andEqualTo("receiverAddress", seckillOrder.getReceiverAddress());
            }
            // 收货人电话
            if(!StringUtils.isEmpty(seckillOrder.getReceiverMobile())) {
                criteria.andEqualTo("receiverMobile", seckillOrder.getReceiverMobile());
            }
            // 收货人
            if(!StringUtils.isEmpty(seckillOrder.getReceiver())) {
                criteria.andEqualTo("receiver", seckillOrder.getReceiver());
            }
            // 交易流水
            if(!StringUtils.isEmpty(seckillOrder.getTransactionId())) {
                criteria.andEqualTo("transactionId", seckillOrder.getTransactionId());
            }
        }
        return example;
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        seckillOrderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改SeckillOrder
     *
     * @param seckillOrder
     */
    @Override
    public void update(SeckillOrder seckillOrder) {
        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
    }

    /**
     * 增加SeckillOrder
     *
     * @param seckillOrder
     */
    @Override
    public void add(SeckillOrder seckillOrder) {
        seckillOrderMapper.insert(seckillOrder);
    }

    /**
     * 根据ID查询SeckillOrder
     *
     * @param id
     * @return
     */
    @Override
    public SeckillOrder findById(Long id) {
        return seckillOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询SeckillOrder全部数据
     *
     * @return
     */
    @Override
    public List<SeckillOrder> findAll() {
        return seckillOrderMapper.selectAll();
    }
}
