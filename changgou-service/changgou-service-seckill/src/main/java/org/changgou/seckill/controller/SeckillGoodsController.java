package org.changgou.seckill.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.changgou.entity.Result;
import org.changgou.entity.StatusCode;
import org.changgou.seckill.pojo.SeckillGoods;
import org.changgou.seckill.service.SeckillGoodsService;
import org.changgou.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/
@Api(value = "SeckillGoodsController")
@RestController
@RequestMapping("/seckillGoods")
@CrossOrigin
public class SeckillGoodsController {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    /**
     * 查询秒杀商品详情
     * @param id 商品id
     * @param time 秒杀时间段,主要用于redis中key的检索
     * @return
     */
    @GetMapping("/selectOne/{time}/{id}")
    public Result<SeckillGoods> findByIdFromRedis(@PathVariable(name = "id") String id, @PathVariable(name = "time") String time){
        SeckillGoods seckillGoods = seckillGoodsService.findByIdFromRedis(id, time);
        if(seckillGoods != null){
            return new Result<>(true, StatusCode.OK, "查询秒杀商品成功", seckillGoods);
        }
        return new Result<>(false, StatusCode.ERROR, "查询秒杀商品失败");
    }

    /**
     * 从redis中查询当前时间段的秒杀商品列表
     *
     * @param time 当前时间段的起始时间,MMddhh格式
     * @return redis中的秒杀商品数据
     */
    @GetMapping("/list/{time}")
    public Result<List<SeckillGoods>> listFromRedis(@PathVariable(name = "time") String time) {
        List<SeckillGoods> list = seckillGoodsService.listFromRedis(time);
        if(list != null){
            return new Result<>(true, StatusCode.OK, "查询秒杀列表成功", list);
        }
        return new Result<>(false, StatusCode.NOTFOUNDERROR, "查询秒杀列表失败");
    }

    /*****
     * 获取时间菜单
     * URLL:/seckillGoods/menus
     */
    @RequestMapping(value = "/menus")
    public List<Date> dateMenus() {
        return DateUtil.getDateMenus();
    }

    /***
     * SeckillGoods分页条件搜索实现
     * @param seckillGoods
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value = "SeckillGoods条件分页查询", notes = "分页条件查询SeckillGoods方法详情", tags = {"SeckillGoodsController"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "size", value = "每页显示条数", required = true, dataType = "Integer")
    })
    @PostMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@RequestBody(required = false) @ApiParam(name = "SeckillGoods对象", value = "传入JSON数据", required = false) SeckillGoods seckillGoods, @PathVariable int page, @PathVariable int size) {
        //调用SeckillGoodsService实现分页条件查询SeckillGoods
        PageInfo<SeckillGoods> pageInfo = seckillGoodsService.findPage(seckillGoods, page, size);
        return new Result(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     * SeckillGoods分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @ApiOperation(value = "SeckillGoods分页查询", notes = "分页查询SeckillGoods方法详情", tags = {"SeckillGoodsController"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "size", value = "每页显示条数", required = true, dataType = "Integer")
    })
    @GetMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@PathVariable int page, @PathVariable int size) {
        //调用SeckillGoodsService实现分页查询SeckillGoods
        PageInfo<SeckillGoods> pageInfo = seckillGoodsService.findPage(page, size);
        return new Result<PageInfo>(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param seckillGoods
     * @return
     */
    @ApiOperation(value = "SeckillGoods条件查询", notes = "条件查询SeckillGoods方法详情", tags = {"SeckillGoodsController"})
    @PostMapping(value = "/search")
    public Result<List<SeckillGoods>> findList(@RequestBody(required = false) @ApiParam(name = "SeckillGoods对象", value = "传入JSON数据", required = false) SeckillGoods seckillGoods) {
        //调用SeckillGoodsService实现条件查询SeckillGoods
        List<SeckillGoods> list = seckillGoodsService.findList(seckillGoods);
        return new Result<List<SeckillGoods>>(true, StatusCode.OK, "查询成功", list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @ApiOperation(value = "SeckillGoods根据ID删除", notes = "根据ID删除SeckillGoods方法详情", tags = {"SeckillGoodsController"})
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Long")
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable Long id) {
        //调用SeckillGoodsService实现根据主键删除
        seckillGoodsService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /***
     * 修改SeckillGoods数据
     * @param seckillGoods
     * @param id
     * @return
     */
    @ApiOperation(value = "SeckillGoods根据ID修改", notes = "根据ID修改SeckillGoods方法详情", tags = {"SeckillGoodsController"})
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Long")
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody @ApiParam(name = "SeckillGoods对象", value = "传入JSON数据", required = false) SeckillGoods seckillGoods, @PathVariable Long id) {
        //设置主键值
        seckillGoods.setId(id);
        //调用SeckillGoodsService实现修改SeckillGoods
        seckillGoodsService.update(seckillGoods);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /***
     * 新增SeckillGoods数据
     * @param seckillGoods
     * @return
     */
    @ApiOperation(value = "SeckillGoods添加", notes = "添加SeckillGoods方法详情", tags = {"SeckillGoodsController"})
    @PostMapping
    public Result add(@RequestBody @ApiParam(name = "SeckillGoods对象", value = "传入JSON数据", required = true) SeckillGoods seckillGoods) {
        //调用SeckillGoodsService实现添加SeckillGoods
        seckillGoodsService.add(seckillGoods);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    /***
     * 根据ID查询SeckillGoods数据
     * @param id
     * @return
     */
    @ApiOperation(value = "SeckillGoods根据ID查询", notes = "根据ID查询SeckillGoods方法详情", tags = {"SeckillGoodsController"})
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Long")
    @GetMapping("/{id}")
    public Result<SeckillGoods> findById(@PathVariable Long id) {
        //调用SeckillGoodsService实现根据主键查询SeckillGoods
        SeckillGoods seckillGoods = seckillGoodsService.findById(id);
        return new Result<SeckillGoods>(true, StatusCode.OK, "查询成功", seckillGoods);
    }

    /***
     * 查询SeckillGoods全部数据
     * @return
     */
    @ApiOperation(value = "查询所有SeckillGoods", notes = "查询所SeckillGoods有方法详情", tags = {"SeckillGoodsController"})
    @GetMapping
    public Result<List<SeckillGoods>> findAll() {
        //调用SeckillGoodsService实现查询所有SeckillGoods
        List<SeckillGoods> list = seckillGoodsService.findAll();
        return new Result<List<SeckillGoods>>(true, StatusCode.OK, "查询成功", list);
    }
}
