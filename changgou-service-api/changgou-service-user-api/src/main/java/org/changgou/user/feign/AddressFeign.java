package org.changgou.user.feign;

import org.changgou.entity.Result;
import org.changgou.user.pojo.Address;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "user")
@RequestMapping("/address")
public interface AddressFeign {

    @GetMapping("/list")
    Result<List<Address>> findByUsername();
}
