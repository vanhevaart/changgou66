package org.changgou.goods.feign;

import org.changgou.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 远程调用Cache接口
 */
@FeignClient(name = "goods")
@RequestMapping("/cache")
public interface CacheFeign {

    /**
     * 更新缓存的商品搜索信息
     * @return
     */
    @GetMapping("/refresh")
    Result refreshGoodsCache();
}
