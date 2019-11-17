package org.changgou.goods.controller;

import com.github.pagehelper.PageInfo;
import org.changgou.entity.Result;
import org.changgou.entity.StatusCode;
import org.changgou.goods.pojo.Para;
import org.changgou.goods.service.ParaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author:  HZ
 * <p> 商品参数管理Controller
 * Create:  2019/8/10  11:19
 */
@RestController
@RequestMapping("/para")
public class ParaController {

    @Autowired
    private ParaService paraService;

    /**
     * 根据templateId查询规格数据
     *
     * @param templateId 模板id
     * @return 该模板id所对应的所有规格数据
     */
    @GetMapping("/get/{templateId}")
    public Result<List<Para>> findByTemplateId(@PathVariable Integer templateId) {
        List<Para> byTemplateId = paraService.findByTemplateId(templateId);
        return new Result<>(true, StatusCode.OK, "查询规格数据成功", byTemplateId);
    }

    /**
     * 查询所有商品参数数据
     *
     * @return 返回参数数据
     */
    @GetMapping
    public Result<List<Para>> findAll() {
        List<Para> all = paraService.findAll();
        return new Result<>(true, StatusCode.OK, "查询成功", all);
    }

    /**
     * 根据Id查询参数
     *
     * @param id 参数id
     * @return 返回参数数据
     */
    @GetMapping("/{id}")
    public Result<Para> findById(@PathVariable Integer id) {
        Para para = paraService.findById(id);
        return new Result<>(true, StatusCode.OK, "查询成功", para);
    }

    /**
     * 增加商品参数数据
     *
     * @param para 封装了要新增的参数信息
     */
    @PostMapping
    public Result save(@RequestBody Para para) {
        paraService.add(para);
        return new Result<>(true, StatusCode.OK, "增加成功");
    }

    /**
     * 更新商品参数信息
     *
     * @param para 封装了要更新的参数信息
     * @param id   要更新的参数的主键id
     */
    @PutMapping("/{id}")
    public Result update(@RequestBody Para para, @PathVariable Integer id) {
        para.setId(id);
        paraService.update(para);
        return new Result<>(true, StatusCode.OK, "修改成功");
    }

    /**
     * 根据id删除商品参数数据
     *
     * @param id 参数id
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Integer id) {
        paraService.deleteById(id);
        return new Result<>(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据条件查询商品参数数据
     *
     * @param para 封装了查询的条件
     * @return 返回符合条件的参数数据
     */
    @PostMapping("/search")
    public Result<List<Para>> findByCondition(@RequestBody Para para) {
        List<Para> byCondition = paraService.findByCondition(para);
        return new Result<>(true, StatusCode.OK, "查询成功", byCondition);
    }

    /**
     * 分页查询商品参数数据
     *
     * @param page 当前页码数
     * @param size 每页显示个数
     * @return 当前页面所需要的参数数据,
     */
    @GetMapping("/search/{page}/{size}")
    public Result<PageInfo<Para>> findByPage(@PathVariable Integer page, @PathVariable Integer size) {
        PageInfo<Para> byPage = paraService.findByPage(page, size);
        return new Result<>(true, StatusCode.OK, "查询成功", byPage);
    }

    /**
     * 分页+条件查询
     *
     * @param para 封装了查询条件
     * @param page 当前页码数
     * @param size 每页显示个数
     * @return 当前页面所需要的参数数据
     */
    @PostMapping("/search/{page}/{size}")
    public Result<PageInfo<Para>> search(@RequestBody Para para, @PathVariable Integer page, @PathVariable Integer size) {
        PageInfo<Para> byPage = paraService.search(para, page, size);
        return new Result<>(true, StatusCode.OK, "查询成功", byPage);
    }
}

