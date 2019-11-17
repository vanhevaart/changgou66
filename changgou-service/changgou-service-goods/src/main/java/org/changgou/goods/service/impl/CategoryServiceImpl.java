package org.changgou.goods.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.changgou.goods.dao.*;
import org.changgou.goods.pojo.*;
import org.changgou.goods.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author:  HZ
 * <p> 商品分类管理service
 * Create:  2019/8/11  22:49
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired(required = false)
    private CategoryMapper categoryMapper;

    @Autowired(required = false)
    private BrandMapper brandMapper;

    @Autowired(required = false)
    private TemplateMapper templateMapper;

    @Autowired(required = false)
    private SpecMapper specMapper;

    @Autowired(required = false)
    private ParaMapper paraMapper;

    /**
     * 根据categoryId查询,使用Map返回新增商品页面需要的所有回显数据
     *
     * @param categoryId 三级分类id
     * @return 包含了所有回显所需数据的Map
     */
    @Override
    public Map<String, Object> getAllData4AddGoods(Integer categoryId) {
        Map<String, Object> map = new HashMap<>();
        //1. 根据三级分类id查询品牌列表
        List<Brand> byCategoryId = brandMapper.findByCategoryId(categoryId);
        //2. 根据三级分类id查询模板数据
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        Template template = templateMapper.selectByPrimaryKey(category.getTemplateId());
        //3. 根据查询出的模板id查询规格和参数数据
        Spec spec = new Spec();
        spec.setTemplateId(template.getId());
        List<Spec> specList = specMapper.select(spec);
        Para para = new Para();
        para.setTemplateId(template.getId());
        List<Para> paraList = paraMapper.select(para);
        // 封装数据返回
        map.put("brands", byCategoryId);
        map.put("template", template);
        map.put("specs", specList);
        map.put("paras", paraList);
        return map;
    }

    /**
     * 查询所有商品分类数据
     *
     * @return 返回分类数据
     */
    @Override
    public List<Category> findAll() {
        return categoryMapper.selectAll();
    }

    /**
     * 根据Id查询分类
     *
     * @param id 分类id
     * @return 返回分类数据
     */
    @Override
    public Category findById(Integer id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据父分类的id查询
     *
     * @param parentId 父分类的id
     * @return 分类数据
     */
    @Override
    public List<Category> findByParentId(Integer parentId) {
        Category category = new Category();
        category.setParentId(parentId);
        // 利用条件查询方法,查询parentId等于指定值的Category数据
        return categoryMapper.select(category);
    }

    /**
     * 增加商品分类数据,需要更新模板表的数据
     *
     * @param category 封装了要新增的分类信息
     */
    @Override
    public void add(Category category) {
        // 保存一个实体，null的属性不会保存，会使用数据库默认值
        categoryMapper.insertSelective(category);
    }

    /**
     * 更新商品分类信息,需要更新模板表的数据
     *
     * @param category 封装了要更新的分类信息
     */
    @Override
    public void update(Category category) {
        categoryMapper.updateByPrimaryKeySelective(category);

    }

    /**
     * 根据id删除商品分类数据,需要更新模板表的数据
     *
     * @param id 分类id
     */
    @Override
    public void deleteById(Integer id) {
        // 根据id删除数据
        categoryMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据条件查询商品分类数据
     *
     * @param category 封装了查询的条件
     * @return 返回符合条件的分类数据
     */
    @Override
    public List<Category> findByCondition(Category category) {
        // 封装查询条件
        Example example = getExample(category);
        return categoryMapper.selectByExample(example);
    }

    /**
     * 分页查询商品分类数据
     *
     * @param pageNum 当前页码数
     * @param size    每页显示个数
     * @return 当前页面所需要的分类数据,
     */
    @Override
    public PageInfo<Category> findByPage(Integer pageNum, Integer size) {
        // 使用第三方插件进行分页查询
        PageHelper.startPage(pageNum, size);
        List<Category> categorys = categoryMapper.selectAll();
        // 将查询结果进行封装,并返回
        return new PageInfo<>(categorys);
    }

    /**
     * 分页+条件查询
     *
     * @param category 封装了查询条件
     * @param pageNum  当前页码数
     * @param size     每页显示个数
     * @return 当前页面所需要的分类数据
     */
    @Override
    public PageInfo<Category> search(Category category, Integer pageNum, Integer size) {
        Example example = getExample(category);
        PageHelper.startPage(pageNum, size);
        List<Category> categorys = categoryMapper.selectByExample(example);
        return new PageInfo<>(categorys);
    }

    /**
     * 封装查询条件的方法
     *
     * @param category 含有查询条件的实体类对象
     * @return 封装了查询条件的对象
     */
    private Example getExample(Category category) {
        // 封装查询条件对象 允许表的列不存在,也允许实体类的字段不存在
        Example example = new Example(Category.class, false, false);
        // Criteria为实际进行动态Sql语句拼接的对象
        Example.Criteria criteria = example.createCriteria();
        if(category != null) {
            criteria.andLike("name", "%" + (category.getName() == null ? "" : category.getName()) + "%")
                    .andEqualTo("seq", category.getSeq())
                    .andEqualTo("templateId", category.getTemplateId())
                    .andEqualTo("id", category.getId())
                    .andEqualTo("goodsNum", category.getGoodsNum())
                    .andEqualTo("isShow", category.getIsShow())
                    .andEqualTo("isMenu", category.getIsMenu())
                    .andEqualTo("parentId", category.getParentId());
        }
        return example;
    }

}
