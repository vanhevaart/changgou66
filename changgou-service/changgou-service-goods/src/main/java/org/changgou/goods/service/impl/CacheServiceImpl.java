package org.changgou.goods.service.impl;

import org.changgou.goods.dao.BrandMapper;
import org.changgou.goods.dao.CategoryMapper;
import org.changgou.goods.dao.SpecMapper;
import org.changgou.goods.pojo.Brand;
import org.changgou.goods.pojo.Category;
import org.changgou.goods.pojo.Spec;
import org.changgou.goods.service.CacheService;
import org.changgou.utils.CacheKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author:  HZ
 * <p> redis缓存商品搜素信息的业务层
 * Create:  2019/8/17  19:21
 */
@Service
public class CacheServiceImpl implements CacheService {

    @Autowired(required = false)
    private CategoryMapper categoryMapper;
    @Autowired(required = false)
    private BrandMapper brandMapper;
    @Autowired(required = false)
    private SpecMapper specMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 更新缓存的商品搜索信息
     */
    @Override
    public void refreshGoodsCache() {
        List<Category> categories = refreshCategory();
        refreshBrand(categories);
        refreshSpec(categories);
    }

    private List<Category> refreshCategory() {
        List<Category> selectAll = categoryMapper.selectAll();
        for (Category category : selectAll) {
            // 分类名为key, 对应的模板id为value,整体作为一个Hash存入redis
            redisTemplate.boundHashOps(CacheKey.CATEGORY).put(category.getName(), category.getTemplateId());
        }
        return selectAll;
    }

    private void refreshBrand(List<Category> categoryList) {
        for (Category category : categoryList) {
            List<Brand> byCategoryId = brandMapper.findByCategoryId(category.getId());
            redisTemplate.boundHashOps(CacheKey.BRAND).put(category.getName(), byCategoryId);
        }
    }

    private void refreshSpec(List<Category> categoryList) {
        for (Category category : categoryList) {
            Integer templateId = category.getTemplateId();
            Spec spec = new Spec();
            spec.setTemplateId(templateId);
            List<Spec> select = specMapper.select(spec);
            redisTemplate.boundHashOps(CacheKey.SPEC).put(templateId, select);
        }
    }
}
