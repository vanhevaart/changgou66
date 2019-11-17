package org.changgou.seckill.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.changgou.entity.Result;
import org.changgou.entity.StatusCode;
import org.changgou.seckill.pojo.SeckillOrder;
import org.changgou.seckill.pojo.SeckillStatus;
import org.changgou.seckill.service.SeckillOrderService;
import org.changgou.utils.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/****
 * @Author:shenkunlin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/
@Api(value = "SeckillOrderController")
@RestController
@RequestMapping("/seckillOrder")
@CrossOrigin
public class SeckillOrderController {

    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * 更新用户秒杀订单状态
     * @param params
     * @return
     */
    @RequestMapping("/updatePayStatus")
    public Result updatePayStatus(@RequestBody Map<String, String> params) {
        String username = TokenDecode.getUserInfo().get("username");
        String out_trade_no = params.get("out_trade_no");
        String transaction_id = params.get("transaction_id");
        String time_end = params.get("time_end");
        try {
            if(!StringUtils.isEmpty(out_trade_no) && !StringUtils.isEmpty(transaction_id) && !StringUtils.isEmpty(time_end)) {
                seckillOrderService.updatePayStatus(out_trade_no, transaction_id, username, time_end);
                return new Result(true, StatusCode.OK, "修改用户订单状态成功");
            }else{
                return new Result(false, StatusCode.REMOTEERROR, "其实必须的参数");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR, "修改用户订单状态失败");
        }
    }

    /**
     * 根据用户名和商品id查询订单状态
     *
     * @param username 用户名
     * @param id       商品id
     * @return
     */
    @GetMapping("/status/{username}/{id}")
    public Result<SeckillStatus> queryStatus(@PathVariable(name = "username") String username,
                                             @PathVariable(name = "id") String id) {
        SeckillStatus seckillStatus = seckillOrderService.queryStatus(username, id);
        if(seckillStatus == null) {
            return new Result<>(false, StatusCode.NOTFOUNDERROR, "没有秒杀信息");
        }
        return new Result<>(true, StatusCode.OK, "查询订单状态成功", seckillStatus);
    }

    /**
     * 秒杀商品下单
     *
     * @param id   秒杀商品id
     * @param time 秒杀商品所属时间段,用于从redis中读取数据的key
     * @return 是否下单成功
     */
    @GetMapping("/add/{time}/{id}")
    public Result add(@PathVariable(name = "id") String id, @PathVariable(name = "time") String time) {
        String username = TokenDecode.getUserInfo().get("username");
        try {
            boolean add = seckillOrderService.add(id, time, username);
            if(add) {
                return new Result(true, StatusCode.OK, "新增订单成功");
            }
            return new Result(false, StatusCode.ERROR, "新增订单失败");
        } catch (Exception e) {
            String message = e.getMessage();
            return new Result(false, Integer.parseInt(message), "重复操作");
        }
    }

    /***
     * SeckillOrder分页条件搜索实现
     * @param seckillOrder
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value = "SeckillOrder条件分页查询", notes = "分页条件查询SeckillOrder方法详情", tags = {"SeckillOrderController"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "size", value = "每页显示条数", required = true, dataType = "Integer")
    })
    @PostMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@RequestBody(required = false) @ApiParam(name = "SeckillOrder对象", value = "传入JSON数据", required = false) SeckillOrder seckillOrder, @PathVariable int page, @PathVariable int size) {
        //调用SeckillOrderService实现分页条件查询SeckillOrder
        PageInfo<SeckillOrder> pageInfo = seckillOrderService.findPage(seckillOrder, page, size);
        return new Result(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     * SeckillOrder分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @ApiOperation(value = "SeckillOrder分页查询", notes = "分页查询SeckillOrder方法详情", tags = {"SeckillOrderController"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "size", value = "每页显示条数", required = true, dataType = "Integer")
    })
    @GetMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@PathVariable int page, @PathVariable int size) {
        //调用SeckillOrderService实现分页查询SeckillOrder
        PageInfo<SeckillOrder> pageInfo = seckillOrderService.findPage(page, size);
        return new Result<PageInfo>(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param seckillOrder
     * @return
     */
    @ApiOperation(value = "SeckillOrder条件查询", notes = "条件查询SeckillOrder方法详情", tags = {"SeckillOrderController"})
    @PostMapping(value = "/search")
    public Result<List<SeckillOrder>> findList(@RequestBody(required = false) @ApiParam(name = "SeckillOrder对象", value = "传入JSON数据", required = false) SeckillOrder seckillOrder) {
        //调用SeckillOrderService实现条件查询SeckillOrder
        List<SeckillOrder> list = seckillOrderService.findList(seckillOrder);
        return new Result<List<SeckillOrder>>(true, StatusCode.OK, "查询成功", list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @ApiOperation(value = "SeckillOrder根据ID删除", notes = "根据ID删除SeckillOrder方法详情", tags = {"SeckillOrderController"})
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Long")
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable Long id) {
        //调用SeckillOrderService实现根据主键删除
        seckillOrderService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /***
     * 修改SeckillOrder数据
     * @param seckillOrder
     * @param id
     * @return
     */
    @ApiOperation(value = "SeckillOrder根据ID修改", notes = "根据ID修改SeckillOrder方法详情", tags = {"SeckillOrderController"})
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Long")
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody @ApiParam(name = "SeckillOrder对象", value = "传入JSON数据", required = false) SeckillOrder seckillOrder, @PathVariable Long id) {
        //设置主键值
        seckillOrder.setId(id);
        //调用SeckillOrderService实现修改SeckillOrder
        seckillOrderService.update(seckillOrder);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /***
     * 新增SeckillOrder数据
     * @param seckillOrder
     * @return
     */
    @ApiOperation(value = "SeckillOrder添加", notes = "添加SeckillOrder方法详情", tags = {"SeckillOrderController"})
    @PostMapping
    public Result add(@RequestBody @ApiParam(name = "SeckillOrder对象", value = "传入JSON数据", required = true) SeckillOrder seckillOrder) {
        //调用SeckillOrderService实现添加SeckillOrder
        seckillOrderService.add(seckillOrder);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    /***
     * 根据ID查询SeckillOrder数据
     * @param id
     * @return
     */
    @ApiOperation(value = "SeckillOrder根据ID查询", notes = "根据ID查询SeckillOrder方法详情", tags = {"SeckillOrderController"})
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Long")
    @GetMapping("/{id}")
    public Result<SeckillOrder> findById(@PathVariable Long id) {
        //调用SeckillOrderService实现根据主键查询SeckillOrder
        SeckillOrder seckillOrder = seckillOrderService.findById(id);
        return new Result<SeckillOrder>(true, StatusCode.OK, "查询成功", seckillOrder);
    }

    /***
     * 查询SeckillOrder全部数据
     * @return
     */
    @ApiOperation(value = "查询所有SeckillOrder", notes = "查询所SeckillOrder有方法详情", tags = {"SeckillOrderController"})
    @GetMapping
    public Result<List<SeckillOrder>> findAll() {
        //调用SeckillOrderService实现查询所有SeckillOrder
        List<SeckillOrder> list = seckillOrderService.findAll();
        return new Result<List<SeckillOrder>>(true, StatusCode.OK, "查询成功", list);
    }
}
