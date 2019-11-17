package org.changgou.order.controller;

import com.netflix.discovery.converters.Auto;
import org.changgou.entity.Result;
import org.changgou.entity.StatusCode;
import org.changgou.order.feign.CartFeign;
import org.changgou.order.feign.OrderFeign;
import org.changgou.order.pojo.Order;
import org.changgou.order.pojo.OrderItem;
import org.changgou.user.feign.AddressFeign;
import org.changgou.user.pojo.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/26  17:44
 */
@Controller
@RequestMapping("/worder")
public class OrderWebController {

    @Autowired
    private AddressFeign addressFeign;
    @Autowired
    private CartFeign cartFeign;
    @Autowired
    private OrderFeign orderFeign;

    @RequestMapping("/ready/order")
    public String findAll(Model model) {
        Result<List<Address>> byUsername = addressFeign.findByUsername();
        Result<List<OrderItem>> cartList = cartFeign.findAll();
        model.addAttribute("address",byUsername.getData());
        model.addAttribute("cartList",cartList.getData());
        return "order";
    }

    /**
     * 新增订单
     * @return
     */
    @PostMapping("/order")
    @ResponseBody
    public Result add(@RequestBody Order order){
        orderFeign.add(order);
        return new Result(true, StatusCode.OK,"新增订单成功");
    }
}
