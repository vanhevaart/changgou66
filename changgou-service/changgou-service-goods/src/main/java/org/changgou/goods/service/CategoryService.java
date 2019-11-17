package org.changgou.goods.service;

import com.github.pagehelper.PageInfo;
import org.changgou.goods.pojo.Category;

import java.util.List;
import java.util.Map;

/**
 * 商品分类管理service接口
 */
public interface CategoryService {

    /**
     * 根据categoryId查询,使用Map返回新增商品页面需要的所有回显数据
     * @param categoryId 三级分类id
     * @return 包含了所有回显所需数据的Map
     */
    Map<String, Object> getAllData4AddGoods(Integer categoryId);

    /**
     * 查询所有分类数据
     * @return 返回分类数据
     */
    List<Category> findAll();

    /**
     * 根据Id查询分类
     * @param id 分类id
     * @return 返回分类数据
     */
    Category findById(Integer id);

    /**
     * 根据父分类的id查询
     * @param parentId 父分类的id
     * @return 分类数据
     */
    List<Category> findByParentId(Integer parentId);

    /**
     * 增加商品分类数据
     * @param category 封装了要新增的分类信息
     */
    void add(Category category);

    /**
     * 更新商品分类信息
     * @param category 封装了要更新的分类信息
     */
    void update(Category category);

    /**
     * 根据id删除商品分类数据
     * @param id 分类id
     */
    void deleteById(Integer id);

    /**
     * 根据条件查询商品分类数据
     * @param category 封装了查询的条件
     * @return 返回符合条件的分类数据
     */
    List<Category> findByCondition(Category category);

    /**
     * 分页查询商品分类数据
     * @param pageNum 当前页码数
     * @param size 每页显示个数
     * @return 当前页面所需要的分类数据
     */
    PageInfo<Category> findByPage(Integer pageNum, Integer size);

    /**
     * 分页+条件查询
     * @param category 封装了查询条件
     * @param pageNum 当前页码数
     * @param size 每页显示个数
     * @return 当前页面所需要的分类数据
     */
    PageInfo<Category> search(Category category, Integer pageNum, Integer size);
}
