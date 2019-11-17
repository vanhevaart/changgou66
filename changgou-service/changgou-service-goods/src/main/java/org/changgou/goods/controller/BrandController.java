package org.changgou.goods.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.changgou.entity.Result;
import org.changgou.entity.StatusCode;
import org.changgou.goods.pojo.Brand;
import org.changgou.goods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author:  HZ
 * <p> 商品品牌Controller
 * Create:  2019/8/10  11:19
 */
@RestController
@Api("商品品牌类")
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * 根据分类id查询品牌新
     *
     * @param categoryId 分类id
     * @return 该分类下所拥有的品牌信息
     */
    @GetMapping("list/{categoryId}")
    @ApiOperation(value = "根据分类id商品品牌",notes = "查询该id下的所有商品品牌",tags = "brand")
    @ApiImplicitParam(paramType = "path",name = "categoryId",value = "分类id",dataType = "Integer",required = true)
    @ApiResponse(code = 400,message = "请求参数有误")
    public Result<List<Brand>> findByCategoryId(@PathVariable(name = "categoryId") Integer categoryId){
        List<Brand> byCategoryId = brandService.findByCategoryId(categoryId);
        return new Result<>(true, StatusCode.OK, "查询该分类下品牌列表成功", byCategoryId);
    }

    /**
     * 查询所有商品品牌数据
     *
     * @return 返回品牌数据
     */
    @GetMapping
    public Result<List<Brand>> findAll() {
        List<Brand> all = brandService.findAll();
        return new Result<>(true, StatusCode.OK, "查询成功", all);
    }

    /**
     * 根据Id查询品牌
     *
     * @param id 品牌id
     * @return 返回品牌数据
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询品牌",notes = "商品品牌id",tags = "brand")
    @ApiImplicitParam(paramType = "path",name = "id",value = "品牌id",dataType = "Integer",required = true)
    public Result<Brand> findById(@PathVariable Integer id) {
        Brand brand = brandService.findById(id);
        return new Result<>(true, StatusCode.OK, "查询成功", brand);
    }

    /**
     * 增加商品品牌数据
     *
     * @param brand 封装了要新增的品牌信息
     */
    @PostMapping
    public Result save(@RequestBody Brand brand) {
        brandService.add(brand);
        return new Result<>(true, StatusCode.OK, "增加成功");
    }

    /**
     * 更新商品品牌信息
     *
     * @param brand 封装了要更新的品牌信息
     * @param id    要更新的品牌的主键id
     */
    @PutMapping("/{id}")
    public Result update(@RequestBody Brand brand, @PathVariable Integer id) {
        brand.setId(id);
        brandService.update(brand);
        return new Result<>(true, StatusCode.OK, "修改成功");
    }

    /**
     * 根据id删除商品品牌数据
     *
     * @param id 品牌id
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Integer id) {
        brandService.deleteById(id);
        return new Result<>(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据条件查询商品品牌数据
     *
     * @param brand 封装了查询的条件
     * @return 返回符合条件的品牌数据
     */
    @PostMapping("/search")
    public Result<List<Brand>> findByCondition(@RequestBody Brand brand) {
        List<Brand> byCondition = brandService.findByCondition(brand);
        return new Result<>(true, StatusCode.OK, "查询成功", byCondition);
    }

    /**
     * 分页查询商品品牌数据
     *
     * @param page 当前页码数
     * @param size    每页显示个数
     * @return 当前页面所需要的品牌数据,
     */
    @GetMapping("/search/{page}/{size}")
    public Result<PageInfo<Brand>> findByPage(@PathVariable Integer page, @PathVariable Integer size) {
        PageInfo<Brand> byPage = brandService.findByPage(page, size);
        return new Result<>(true, StatusCode.OK, "查询成功", byPage);
    }

    /**
     * 分页+条件查询
     *
     * @param brand   封装了查询条件
     * @param page 当前页码数
     * @param size    每页显示个数
     * @return 当前页面所需要的品牌数据
     */
    @PostMapping("/search/{page}/{size}")
    public Result<PageInfo<Brand>> search(@RequestBody Brand brand, @PathVariable Integer page, @PathVariable Integer size) {
        PageInfo<Brand> byPage = brandService.search(brand, page, size);
        return new Result<>(true, StatusCode.OK, "查询成功", byPage);
    }
}

