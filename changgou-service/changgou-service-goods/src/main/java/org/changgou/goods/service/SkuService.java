package org.changgou.goods.service;

import com.github.pagehelper.PageInfo;
import org.changgou.goods.pojo.Sku;

import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:Sku业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SkuService {

    /**
     * 生成订单后的减少库存操作
     * @param username
     * @return
     */
    void decr(String username);

    /***
     * Sku多条件分页查询
     * @param sku
     * @param page
     * @param size
     * @return
     */
    PageInfo<Sku> findPage(Sku sku, int page, int size);

    /***
     * Sku分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Sku> findPage(int page, int size);

    /***
     * Sku多条件搜索方法
     * @param sku
     * @return
     */
    List<Sku> findList(Sku sku);

    /***
     * 删除Sku
     * @param id
     */
    void delete(Long id);

    /***
     * 修改Sku数据
     * @param sku
     */
    void update(Sku sku);

    /***
     * 新增Sku
     * @param sku
     */
    void add(Sku sku);

    /**
     * 根据ID查询Sku
     * @param id
     * @return
     */
     Sku findById(Long id);

    /***
     * 查询所有Sku
     * @return
     */
    List<Sku> findAll();

    /**
     * 根据商品状态查询Sku数据,可以作为导入sku数据到ES中的方法调用
     * @param status 审核状态
     * @return
     */
    List<Sku> findByStatus(String status);

}
