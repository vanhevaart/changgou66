package org.changgou.goods.service;

/**
 * redis缓存商品搜素信息的业务接口
 */
public interface CacheService {

    /**
     * 更新缓存的商品搜索信息
     */
    void refreshGoodsCache();


}
