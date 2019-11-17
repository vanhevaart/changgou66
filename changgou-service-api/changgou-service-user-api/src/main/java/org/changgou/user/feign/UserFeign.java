package org.changgou.user.feign;

import org.changgou.entity.Result;
import org.changgou.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/21  20:07
 */
@FeignClient(name = "user")
@RequestMapping("/user")
public interface UserFeign {

    @RequestMapping("/load/{id}")
    Result<User> findById(@PathVariable(name = "id") String id);

    /**
     * 下订单后的增加积分操作
     * @param username 用户名
     * @param score 增加的积分
     * @return
     */
    @GetMapping("point/{username}/{score}")
    Result points(@PathVariable(name = "username") String username, @PathVariable(name = "score") Integer score);
}
