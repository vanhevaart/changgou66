package org.changgou.goods.feign;

import org.changgou.entity.Result;
import org.changgou.goods.pojo.Sku;
import org.changgou.goods.pojo.Spu;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Goods远程调用Spu接口
 */
@FeignClient(name = "goods")
@RequestMapping("/spu")
public interface SpuFeign {

    /**
     * 根据SpuId查询Spu
     */
    @GetMapping("/{id}")
    Result<Spu> findById(@PathVariable Long id);
}
