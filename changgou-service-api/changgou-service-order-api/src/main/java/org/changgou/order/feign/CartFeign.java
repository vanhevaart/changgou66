package org.changgou.order.feign;

import org.changgou.entity.Result;
import org.changgou.order.pojo.OrderItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "order")
@RequestMapping("/cart")
public interface CartFeign {

    /**
     * 保存商品进购物车方法
     *
     * @param id  商品skuID
     * @param num 商品数量
     */
    @GetMapping("/{id}/{num}")
    Result add(@PathVariable(name = "id") Long id, @PathVariable(name = "num") Integer num);

    /**
     * 查询购物车列表
     *
     * @return
     */
    @GetMapping("/list")
    Result<List<OrderItem>> findAll();
}
