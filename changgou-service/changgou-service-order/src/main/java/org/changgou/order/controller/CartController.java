package org.changgou.order.controller;

import org.changgou.entity.Result;
import org.changgou.entity.StatusCode;
import org.changgou.order.pojo.OrderItem;
import org.changgou.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/24  20:29
 */
@RestController
@RequestMapping("/cart")
@CrossOrigin
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 保存商品进购物车方法
     *
     * @param id  商品skuID
     * @param num 商品数量
     */
    @GetMapping("/{id}/{num}")
    public Result add(@PathVariable(name = "id") Long id, @PathVariable(name = "num") Integer num){
        cartService.add(id,num);
        return new Result(true, StatusCode.OK,"添加购物车成功");
    }

    /**
     * 查询购物车列表
     *
     * @return
     */
    @GetMapping("/list")
    public Result<List<OrderItem>> findAll() {
        List<OrderItem> list = cartService.findAll();
        return new Result<>(true, StatusCode.OK,"查询购物车成功",list);
    }
}
