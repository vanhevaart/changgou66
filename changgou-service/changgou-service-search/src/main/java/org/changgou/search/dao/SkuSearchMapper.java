package org.changgou.search.dao;

import org.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/15  19:44
 */
public interface SkuSearchMapper extends ElasticsearchRepository<SkuInfo,Long> {

}
