package org.changgou.search.service;

import org.changgou.goods.pojo.Sku;

import java.util.List;
import java.util.Map;

/**
 * Author:  HZ
 * <p> ES搜索服务业务接口
 * Create:  2019/8/15  19:40
 */
public interface SkuSearchService {

    /**
     * 导入Sku数据到ES中
     * @param data Sku数据集合
     */
    void importSkuData(List<Sku> data);

    /**
     * 搜索,具备关键字搜索,品牌分类,规格分类等完整功能
     * @param searchMap 包含搜索条件的map
     * @return
     */
    Map<String, Object> search(Map<String,String> searchMap);


}
