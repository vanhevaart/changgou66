package org.changgou.order.controller;

import org.changgou.entity.Result;
import org.changgou.entity.StatusCode;
import org.changgou.order.feign.CartFeign;
import org.changgou.order.pojo.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/26  11:33
 */
@Controller
@RequestMapping("/wcart")
public class CartWebController {

    @Autowired
    private CartFeign cartFeign;

    /**
     * 查询登录用户的购物车列表
     * @param model
     * @return
     */
    @RequestMapping("/list")
    public String findAll(Model model){
        Result<List<OrderItem>> all = cartFeign.findAll();
        model.addAttribute("shopcarlist",all.getData());
        return "cart";
    }

    /**
     * 添加购物车,需要登录
     * @param id 商品skuId
     * @param num 商品数量
     */
    @RequestMapping("/add")
    @ResponseBody
    public Result add(@RequestParam(name = "id") Long id, @RequestParam(name = "num") Integer num){
        cartFeign.add(id,num);
        return new Result(true, StatusCode.OK,"添加购物车成功");
    }
}
