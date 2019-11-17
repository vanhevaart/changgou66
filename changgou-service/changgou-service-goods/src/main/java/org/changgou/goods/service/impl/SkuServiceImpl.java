package org.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.changgou.goods.dao.SkuMapper;
import org.changgou.goods.pojo.Sku;
import org.changgou.goods.service.SkuService;
import org.changgou.order.pojo.OrderItem;
import org.changgou.utils.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:Sku业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SkuServiceImpl implements SkuService {

    @Autowired(required = false)
    private SkuMapper skuMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 生成订单后的减少库存操作
     * @param username
     * @return
     */
    @Override
    public void decr(String username) {
        // 根据用户名获取购物车信息
        List<Object> values = stringRedisTemplate.boundHashOps("Cart_" + username).values();
        List<OrderItem> list = new ArrayList<>();
        if(values != null && values.size() > 0) {
            for (Object value : values) {
                String str = (String) value;
                OrderItem orderItem = JSON.parseObject(str, OrderItem.class);
                list.add(orderItem);
            }
        }
        // 根据购物车数据获取skuId,执行减库存操作
        for (OrderItem orderItem : list) {
            Long skuId = orderItem.getSkuId();
            Integer num = orderItem.getNum();
            int decrCount = skuMapper.decr(skuId,num);
            if(decrCount <= 0){
                throw new RuntimeException("商品库存已告罄");
            }
        }
    }

    /**
     * Sku条件+分页查询
     *
     * @param sku  查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Sku> findPage(Sku sku, int page, int size) {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建
        Example example = createExample(sku);
        //执行搜索
        return new PageInfo<Sku>(skuMapper.selectByExample(example));
    }

    /**
     * Sku分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Sku> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page, size);
        //分页查询
        return new PageInfo<Sku>(skuMapper.selectAll());
    }

    /**
     * Sku条件查询
     *
     * @param sku
     * @return
     */
    @Override
    public List<Sku> findList(Sku sku) {
        //构建查询条件
        Example example = createExample(sku);
        //根据构建的条件查询数据
        return skuMapper.selectByExample(example);
    }


    /**
     * Sku构建查询对象
     *
     * @param sku
     * @return
     */
    public Example createExample(Sku sku) {
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        if(sku != null) {
            // 商品id
            if(!StringUtils.isEmpty(sku.getId())) {
                criteria.andEqualTo("id", sku.getId());
            }
            // 商品条码
            if(!StringUtils.isEmpty(sku.getSn())) {
                criteria.andEqualTo("sn", sku.getSn());
            }
            // SKU名称
            if(!StringUtils.isEmpty(sku.getName())) {
                criteria.andLike("name", "%" + sku.getName() + "%");
            }
            // 价格（分）
            if(!StringUtils.isEmpty(sku.getPrice())) {
                criteria.andEqualTo("price", sku.getPrice());
            }
            // 库存数量
            if(!StringUtils.isEmpty(sku.getNum())) {
                criteria.andEqualTo("num", sku.getNum());
            }
            // 库存预警数量
            if(!StringUtils.isEmpty(sku.getAlertNum())) {
                criteria.andEqualTo("alertNum", sku.getAlertNum());
            }
            // 商品图片
            if(!StringUtils.isEmpty(sku.getImage())) {
                criteria.andEqualTo("image", sku.getImage());
            }
            // 商品图片列表
            if(!StringUtils.isEmpty(sku.getImages())) {
                criteria.andEqualTo("images", sku.getImages());
            }
            // 重量（克）
            if(!StringUtils.isEmpty(sku.getWeight())) {
                criteria.andEqualTo("weight", sku.getWeight());
            }
            // 创建时间
            if(!StringUtils.isEmpty(sku.getCreateTime())) {
                criteria.andEqualTo("createTime", sku.getCreateTime());
            }
            // 更新时间
            if(!StringUtils.isEmpty(sku.getUpdateTime())) {
                criteria.andEqualTo("updateTime", sku.getUpdateTime());
            }
            // SPUID
            if(!StringUtils.isEmpty(sku.getSpuId())) {
                criteria.andEqualTo("spuId", sku.getSpuId());
            }
            // 类目ID
            if(!StringUtils.isEmpty(sku.getCategoryId())) {
                criteria.andEqualTo("categoryId", sku.getCategoryId());
            }
            // 类目名称
            if(!StringUtils.isEmpty(sku.getCategoryName())) {
                criteria.andEqualTo("categoryName", sku.getCategoryName());
            }
            // 品牌名称
            if(!StringUtils.isEmpty(sku.getBrandName())) {
                criteria.andEqualTo("brandName", sku.getBrandName());
            }
            // 规格
            if(!StringUtils.isEmpty(sku.getSpec())) {
                criteria.andEqualTo("spec", sku.getSpec());
            }
            // 销量
            if(!StringUtils.isEmpty(sku.getSaleNum())) {
                criteria.andEqualTo("saleNum", sku.getSaleNum());
            }
            // 评论数
            if(!StringUtils.isEmpty(sku.getCommentNum())) {
                criteria.andEqualTo("commentNum", sku.getCommentNum());
            }
            // 商品状态 1-正常，2-下架，3-删除
            if(!StringUtils.isEmpty(sku.getStatus())) {
                criteria.andEqualTo("status", sku.getStatus());
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
        skuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Sku
     *
     * @param sku
     */
    @Override
    public void update(Sku sku) {
        skuMapper.updateByPrimaryKey(sku);
    }

    /**
     * 增加Sku
     *
     * @param sku
     */
    @Override
    public void add(Sku sku) {
        skuMapper.insert(sku);
    }

    /**
     * 根据ID查询Sku
     *
     * @param id
     * @return
     */
    @Override
    public Sku findById(Long id) {
        return skuMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Sku全部数据
     *
     * @return
     */
    @Override
    public List<Sku> findAll() {
        return skuMapper.selectAll();
    }

    /**
     * 根据商品状态查询Sku数据,可以作为导入sku数据到ES中的方法调用
     *
     * @param status 审核状态
     * @return
     */
    @Override
    public List<Sku> findByStatus(String status) {
        Sku sku = new Sku();
        sku.setStatus(status);
        return skuMapper.select(sku);
    }

}
