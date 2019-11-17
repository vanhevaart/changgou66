package org.changgou.seckill.service;

import com.github.pagehelper.PageInfo;
import org.changgou.seckill.pojo.SeckillGoods;

import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:SeckillGoods业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SeckillGoodsService {

    /**
     * 查询秒杀商品详情
     * @param id 商品id
     * @param time 秒杀时间段,主要用于redis中key的检索
     * @return
     */
    SeckillGoods findByIdFromRedis(String id, String time);

    /**
     * 从redis中查询当前时间段的秒杀商品列表
     *
     * @param time 当前时间段的起始时间,MMddhh格式
     * @return redis中的秒杀商品数据
     */
    List<SeckillGoods> listFromRedis(String time);

    /***
     * SeckillGoods多条件分页查询
     * @param seckillGoods
     * @param page
     * @param size
     * @return
     */
    PageInfo<SeckillGoods> findPage(SeckillGoods seckillGoods, int page, int size);

    /***
     * SeckillGoods分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<SeckillGoods> findPage(int page, int size);

    /***
     * SeckillGoods多条件搜索方法
     * @param seckillGoods
     * @return
     */
    List<SeckillGoods> findList(SeckillGoods seckillGoods);

    /***
     * 删除SeckillGoods
     * @param id
     */
    void delete(Long id);

    /***
     * 修改SeckillGoods数据
     * @param seckillGoods
     */
    void update(SeckillGoods seckillGoods);

    /***
     * 新增SeckillGoods
     * @param seckillGoods
     */
    void add(SeckillGoods seckillGoods);

    /**
     * 根据ID查询SeckillGoods
     * @param id
     * @return
     */
     SeckillGoods findById(Long id);

    /***
     * 查询所有SeckillGoods
     * @return
     */
    List<SeckillGoods> findAll();
}
