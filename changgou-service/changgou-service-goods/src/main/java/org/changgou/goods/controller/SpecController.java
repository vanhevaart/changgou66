package org.changgou.goods.controller;

import com.github.pagehelper.PageInfo;
import org.changgou.entity.Result;
import org.changgou.entity.StatusCode;
import org.changgou.goods.pojo.Spec;
import org.changgou.goods.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author:  HZ
 * <p> 商品规格管理Controller
 * Create:  2019/8/10  11:19
 */
@RestController
@RequestMapping("/spec")
public class SpecController {

    @Autowired
    private SpecService specService;

    /**
     * 根据templateId查询规格数据
     * @param templateId 模板id
     * @return 该模板id所对应的所有规格数据
     */
    @GetMapping("/get/{templateId}")
    public Result<List<Spec>> findByTemplateId(@PathVariable Integer templateId){
        List<Spec> byTemplateId = specService.findByTemplateId(templateId);
        return new Result<>(true, StatusCode.OK, "查询规格数据成功", byTemplateId);
    }

    /**
     * 查询所有商品规格数据
     *
     * @return 返回规格数据
     */
    @GetMapping
    public Result<List<Spec>> findAll() {
        List<Spec> all = specService.findAll();
        return new Result<>(true, StatusCode.OK, "查询成功", all);
    }

    /**
     * 根据Id查询规格
     *
     * @param id 规格id
     * @return 返回规格数据
     */
    @GetMapping("/{id}")
    public Result<Spec> findById(@PathVariable Integer id) {
        Spec spec = specService.findById(id);
        return new Result<>(true, StatusCode.OK, "查询成功", spec);
    }

    /**
     * 增加商品规格数据
     *
     * @param spec 封装了要新增的规格信息
     */
    @PostMapping
    public Result save(@RequestBody Spec spec) {
        specService.add(spec);
        return new Result<>(true, StatusCode.OK, "增加成功");
    }

    /**
     * 更新商品规格信息
     *
     * @param spec 封装了要更新的规格信息
     * @param id    要更新的规格的主键id
     */
    @PutMapping("/{id}")
    public Result update(@RequestBody Spec spec, @PathVariable Integer id) {
        spec.setId(id);
        specService.update(spec);
        return new Result<>(true, StatusCode.OK, "修改成功");
    }

    /**
     * 根据id删除商品规格数据
     *
     * @param id 规格id
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Integer id) {
        specService.deleteById(id);
        return new Result<>(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据条件查询商品规格数据
     *
     * @param spec 封装了查询的条件
     * @return 返回符合条件的规格数据
     */
    @PostMapping("/search")
    public Result<List<Spec>> findByCondition(@RequestBody Spec spec) {
        List<Spec> byCondition = specService.findByCondition(spec);
        return new Result<>(true, StatusCode.OK, "查询成功", byCondition);
    }

    /**
     * 分页查询商品规格数据
     *
     * @param page 当前页码数
     * @param size    每页显示个数
     * @return 当前页面所需要的规格数据,
     */
    @GetMapping("/search/{page}/{size}")
    public Result<PageInfo<Spec>> findByPage(@PathVariable Integer page, @PathVariable Integer size) {
        PageInfo<Spec> byPage = specService.findByPage(page, size);
        return new Result<>(true, StatusCode.OK, "查询成功", byPage);
    }

    /**
     * 分页+条件查询
     *
     * @param spec   封装了查询条件
     * @param page 当前页码数
     * @param size    每页显示个数
     * @return 当前页面所需要的规格数据
     */
    @PostMapping("/search/{page}/{size}")
    public Result<PageInfo<Spec>> search(@RequestBody Spec spec, @PathVariable Integer page, @PathVariable Integer size) {
        PageInfo<Spec> byPage = specService.search(spec, page, size);
        return new Result<>(true, StatusCode.OK, "查询成功", byPage);
    }
}

