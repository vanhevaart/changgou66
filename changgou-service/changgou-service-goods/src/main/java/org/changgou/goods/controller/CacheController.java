package org.changgou.goods.controller;

import org.changgou.entity.Result;
import org.changgou.entity.StatusCode;
import org.changgou.goods.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author:  HZ
 * <p> redis缓存商品搜素信息的控制层
 * Create:  2019/8/17  19:20
 */
@RestController
@RequestMapping("/cache")
public class CacheController {

    @Autowired
    private CacheService cacheService;

    /**
     * 更新缓存的商品搜索信息
     * @return
     */
    @GetMapping("/refresh")
    public Result refreshGoodsCache(){
        cacheService.refreshGoodsCache();
        return new Result(true, StatusCode.OK,"更新商品缓存成功");
    }
}
