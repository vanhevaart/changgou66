package org.changgou.order.feign;

import org.changgou.entity.Result;
import org.changgou.order.pojo.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "order")
@RequestMapping("/order")
public interface OrderFeign {

    @PostMapping
    Result add(@RequestBody Order order);

    /**
     * 根据失败的支付结果更新订单状态
     *
     * @param id 订单Id
     */
    @RequestMapping("/deleteOrder/{id}")
    void deleteOrder(@PathVariable(name = "id") String id);

    /**
     * 根据成功的支付结果更新订单状态
     *
     * @param id            订单Id
     * @param transactionId 微信方生成的交易流水号
     * @param payTime       付款时间-来自微信
     */
    @PostMapping("/updateOrder")
    void updateOrder(@RequestBody Order order);
}
