package org.changgou.goods.controller;

import com.github.pagehelper.PageInfo;
import org.changgou.entity.Result;
import org.changgou.entity.StatusCode;
import org.changgou.goods.pojo.Category;
import org.changgou.goods.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Author:  HZ
 * <p> 商品分类管理Controller
 * Create:  2019/8/10  11:19
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据categoryId查询,使用Map返回新增商品页面需要的所有回显数据
     *
     * @param categoryId 三级分类id
     * @return 包含了所有回显所需数据的Map
     */
    @GetMapping("/list/{categoryId}")
    public Result<Map<String, Object>> getAllData4AddGoods(@PathVariable(name = "categoryId") Integer categoryId) {
        Map<String, Object> allData4AddGoods = categoryService.getAllData4AddGoods(categoryId);
        return new Result<>(true, StatusCode.OK, "查询成功", allData4AddGoods);
    }

    /**
     * 查询所有商品分类数据
     *
     * @return 返回分类数据
     */
    @GetMapping
    public Result<List<Category>> findAll() {
        List<Category> all = categoryService.findAll();
        return new Result<>(true, StatusCode.OK, "查询成功", all);
    }

    /**
     * 根据Id查询分类
     *
     * @param id 分类id
     * @return 返回分类数据
     */
    @GetMapping("/{id}")
    public Result<Category> findById(@PathVariable Integer id) {
        Category category = categoryService.findById(id);
        return new Result<>(true, StatusCode.OK, "查询成功", category);
    }

    /**
     * 根据父分类的id查询
     *
     * @param parentId 父分类的id
     * @return 分类数据
     */
    @GetMapping("/parent/{parentId}")
    public Result<Category> findByParentId(@PathVariable Integer parentId) {
        List<Category> byParentId = categoryService.findByParentId(parentId);
        return new Result<>(true, StatusCode.OK, "查询成功", byParentId);
    }


    /**
     * 增加商品分类数据
     *
     * @param category 封装了要新增的分类信息
     */
    @PostMapping
    public Result save(@RequestBody Category category) {
        categoryService.add(category);
        return new Result<>(true, StatusCode.OK, "增加成功");
    }

    /**
     * 更新商品分类信息
     *
     * @param category 封装了要更新的分类信息
     * @param id       要更新的分类的主键id
     */
    @PutMapping("/{id}")
    public Result update(@RequestBody Category category, @PathVariable Integer id) {
        category.setId(id);
        categoryService.update(category);
        return new Result<>(true, StatusCode.OK, "修改成功");
    }

    /**
     * 根据id删除商品分类数据
     *
     * @param id 分类id
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Integer id) {
        categoryService.deleteById(id);
        return new Result<>(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据条件查询商品分类数据
     *
     * @param category 封装了查询的条件
     * @return 返回符合条件的分类数据
     */
    @PostMapping("/search")
    public Result<List<Category>> findByCondition(@RequestBody Category category) {
        List<Category> byCondition = categoryService.findByCondition(category);
        return new Result<>(true, StatusCode.OK, "查询成功", byCondition);
    }

    /**
     * 分页查询商品分类数据
     *
     * @param page 当前页码数
     * @param size 每页显示个数
     * @return 当前页面所需要的分类数据,
     */
    @GetMapping("/search/{page}/{size}")
    public Result<PageInfo<Category>> findByPage(@PathVariable Integer page, @PathVariable Integer size) {
        PageInfo<Category> byPage = categoryService.findByPage(page, size);
        return new Result<>(true, StatusCode.OK, "查询成功", byPage);
    }

    /**
     * 分页+条件查询
     *
     * @param category 封装了查询条件
     * @param page     当前页码数
     * @param size     每页显示个数
     * @return 当前页面所需要的分类数据
     */
    @PostMapping("/search/{page}/{size}")
    public Result<PageInfo<Category>> search(@RequestBody Category category, @PathVariable Integer page, @PathVariable Integer size) {
        PageInfo<Category> byPage = categoryService.search(category, page, size);
        return new Result<>(true, StatusCode.OK, "查询成功", byPage);
    }
}

