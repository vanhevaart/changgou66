package org.changgou.goods.feign;

import org.changgou.entity.Result;
import org.changgou.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Goods远程调用Sku接口
 */
@FeignClient(name = "goods")
@RequestMapping("/sku")
public interface SkuFeign {

    /**
     * 生成订单后的减少库存操作
     * @param username
     * @return
     */
    @GetMapping("/decr/{username}")
    Result decr(@PathVariable(name = "username") String username);

    /**
     * 根据商品状态查询Sku数据,可以作为导入sku数据到ES中的方法调用
     */
    @PostMapping("/findByStatus/{status}")
    Result<List<Sku>> findByStatus(@PathVariable(name = "status") String status);

    /**
     * 根据条件查询Sku列表
     * @param sku 包含了查询条件的对象
     * @return
     */
    @PostMapping("/search")
    Result<List<Sku>> findBySpuId(@RequestBody Sku sku);

    /**
     * 根据ID查询Sku数据
     * @param id skuId
     * @return
     */
    @GetMapping("/{id}")
    Result<Sku> findById(@PathVariable(name = "id") Long id);
}
