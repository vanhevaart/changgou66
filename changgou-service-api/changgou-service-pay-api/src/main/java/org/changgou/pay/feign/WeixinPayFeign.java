package org.changgou.pay.feign;

import org.changgou.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient(name = "pay")
@RequestMapping("/pay")
public interface WeixinPayFeign {

    /**
     * 根据商户Id去微信远端查询支付状态
     * 定时任务可调用此方法
     *
     * @param id 商户订单Id
     * @return
     */
    @RequestMapping("/queryStates/{id}")
    Result<Map<String, String>> queryPayStatus(@PathVariable(name = "id") String id);
}
