package org.changgou.goods.service;

import com.github.pagehelper.PageInfo;
import org.changgou.goods.pojo.Brand;

import java.util.List;

/**
 * 商品品牌Service接口
 */
public interface BrandService {

    /**
     * 根据分类id查询品牌新
     * @param categoryId 分类id
     * @return 该分类下所拥有的品牌信息
     */
    List<Brand> findByCategoryId(Integer categoryId);

    /**
     * 查询所有商品品牌数据
     * @return 返回品牌数据
     */
    List<Brand> findAll();

    /**
     * 根据Id查询品牌
     * @param id 品牌id
     * @return 返回品牌数据
     */
    Brand findById(Integer id);

    /**
     * 增加商品品牌数据
     * @param brand 封装了要新增的品牌信息
     */
    void add(Brand brand);

    /**
     * 更新商品品牌信息
     * @param brand 封装了要更新的品牌信息
     */
    void update(Brand brand);

    /**
     * 根据id删除商品品牌数据
     * @param id 品牌id
     */
    void deleteById(Integer id);

    /**
     * 根据条件查询商品品牌数据
     * @param brand 封装了查询的条件
     * @return 返回符合条件的品牌数据
     */
    List<Brand> findByCondition(Brand brand);

    /**
     * 分页查询商品品牌数据
     * @param pageNum 当前页码数
     * @param size 每页显示个数
     * @return 当前页面所需要的品牌数据
     */
    PageInfo<Brand> findByPage(Integer pageNum, Integer size);

    /**
     * 分页+条件查询
     * @param brand 封装了查询条件
     * @param pageNum 当前页码数
     * @param size 每页显示个数
     * @return 当前页面所需要的品牌数据
     */
    PageInfo<Brand> search(Brand brand, Integer pageNum, Integer size);

}
