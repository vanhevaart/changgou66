package org.changgou.goods.service;

import com.github.pagehelper.PageInfo;
import org.changgou.goods.pojo.Goods;
import org.changgou.goods.pojo.Spu;

import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:Spu业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SpuService {

    /**
     * 逻辑性的改变商品状态(改变是否删除字段的值)
     * @param id 商品id
     */
    void logicDelete(Long id, String isDelete);

    /**
     * 批量上下架
     * @param ids 需要进行操作的id的集合
     * @param isMarketable 上/下架标志
     */
    int isShows(Long[] ids, String isMarketable);

    /**
     * 根据id上/下架商品
     * @param id 商品id
     * @param isMarketable 上/下架标志
     */
    void isShow (Long id, String isMarketable);

    /**
     * 根据id改变商品的审核状态,并实现自动上架
     * @param id 商品id
     */
    void audit(Long id);

    /**
     * 根据id查询商品信息(包括spu和sku信息)
     * @param spuId spuId
     * @return 商品信息
     */
    Goods findGoodsById(Long spuId);

    /**
     * 保存商品,分别进行SPU和SKU的保存
     * @param goods 包含了SPU和SKU信息的对象
     */
    void saveGoods(Goods goods);

    /***
     * Spu多条件分页查询
     * @param spu
     * @param page
     * @param size
     * @return
     */
    PageInfo<Spu> findPage(Spu spu, int page, int size);

    /***
     * Spu分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Spu> findPage(int page, int size);

    /***
     * Spu多条件搜索方法
     * @param spu
     * @return
     */
    List<Spu> findList(Spu spu);

    /***
     * 删除Spu
     * @param id
     */
    void delete(Long id);

    /***
     * 修改Spu数据
     * @param spu
     */
    void update(Spu spu);

    /***
     * 新增Spu
     * @param spu
     */
    void add(Spu spu);

    /**
     * 根据ID查询Spu
     * @param id
     * @return
     */
     Spu findById(Long id);

    /***
     * 查询所有Spu
     * @return
     */
    List<Spu> findAll();
}
