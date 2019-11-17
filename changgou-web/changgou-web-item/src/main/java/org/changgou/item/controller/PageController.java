package org.changgou.item.controller;

import org.changgou.entity.Result;
import org.changgou.entity.StatusCode;
import org.changgou.item.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/20  10:56
 */
@RestController
@RequestMapping("/page")
public class PageController {

    @Autowired
    private PageService itemService;

    @GetMapping("{id}")
    public Result createHtml(@PathVariable(name = "id") long spuId){
        itemService.createHtml(spuId);
        return new Result<>(true, StatusCode.OK,"成功");
    }
}
