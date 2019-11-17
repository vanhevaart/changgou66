package org.changgou.item.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/20  15:59
 */
@FeignClient(name = "item")
@RequestMapping("/page")
public interface PageFeign {

    @GetMapping("{id}")
    String createHtml(@PathVariable(name = "id") long spuId);

}
