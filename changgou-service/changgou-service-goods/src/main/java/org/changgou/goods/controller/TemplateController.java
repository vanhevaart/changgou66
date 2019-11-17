package org.changgou.goods.controller;

import com.github.pagehelper.PageInfo;
import org.changgou.entity.Result;
import org.changgou.entity.StatusCode;
import org.changgou.goods.pojo.Template;
import org.changgou.goods.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author:  HZ
 * <p> 商品模板管理Controller
 * Create:  2019/8/10  11:19
 */
@RestController
@RequestMapping("/template")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    /**
     * 根据categoryId查询模板数据
     * @param categoryId 分类id
     * @return
     */
    @GetMapping("/get/{categoryId}")
    public Result<Template> findByCategoryId(@PathVariable(name = "categoryId") Integer categoryId){
        Template byCategoryId = templateService.findByCategoryId(categoryId);
        return new Result<>(true, StatusCode.OK, "查询成功", byCategoryId);
    }

    /**
     * 查询所有商品模板数据
     *
     * @return 返回模板数据
     */
    @GetMapping
    public Result<List<Template>> findAll() {
        List<Template> all = templateService.findAll();
        return new Result<>(true, StatusCode.OK, "查询成功", all);
    }

    /**
     * 根据Id查询模板
     *
     * @param id 模板id
     * @return 返回模板数据
     */
    @GetMapping("/{id}")
    public Result<Template> findById(@PathVariable Integer id) {
        Template template = templateService.findById(id);
        return new Result<>(true, StatusCode.OK, "查询成功", template);
    }

    /**
     * 增加商品模板数据
     *
     * @param template 封装了要新增的模板信息
     */
    @PostMapping
    public Result save(@RequestBody Template template) {
        templateService.add(template);
        return new Result<>(true, StatusCode.OK, "增加成功");
    }

    /**
     * 更新商品模板信息
     *
     * @param template 封装了要更新的模板信息
     * @param id    要更新的模板的主键id
     */
    @PutMapping("/{id}")
    public Result update(@RequestBody Template template, @PathVariable Integer id) {
        template.setId(id);
        templateService.update(template);
        return new Result<>(true, StatusCode.OK, "修改成功");
    }

    /**
     * 根据id删除商品模板数据
     *
     * @param id 模板id
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Integer id) {
        templateService.deleteById(id);
        return new Result<>(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据条件查询商品模板数据
     *
     * @param template 封装了查询的条件
     * @return 返回符合条件的模板数据
     */
    @PostMapping("/search")
    public Result<List<Template>> findByCondition(@RequestBody Template template) {
        List<Template> byCondition = templateService.findByCondition(template);
        return new Result<>(true, StatusCode.OK, "查询成功", byCondition);
    }

    /**
     * 分页查询商品模板数据
     *
     * @param page 当前页码数
     * @param size    每页显示个数
     * @return 当前页面所需要的模板数据,
     */
    @GetMapping("/search/{page}/{size}")
    public Result<PageInfo<Template>> findByPage(@PathVariable Integer page, @PathVariable Integer size) {
        PageInfo<Template> byPage = templateService.findByPage(page, size);
        return new Result<>(true, StatusCode.OK, "查询成功", byPage);
    }

    /**
     * 分页+条件查询
     *
     * @param template   封装了查询条件
     * @param page 当前页码数
     * @param size    每页显示个数
     * @return 当前页面所需要的模板数据
     */
    @PostMapping("/search/{page}/{size}")
    public Result<PageInfo<Template>> search(@RequestBody Template template, @PathVariable Integer page, @PathVariable Integer size) {
        PageInfo<Template> byPage = templateService.search(template, page, size);
        return new Result<>(true, StatusCode.OK, "查询成功", byPage);
    }
}

