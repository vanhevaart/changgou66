package org.changgou.goods.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.changgou.goods.dao.CategoryMapper;
import org.changgou.goods.dao.TemplateMapper;
import org.changgou.goods.pojo.Category;
import org.changgou.goods.pojo.Template;
import org.changgou.goods.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Author:  HZ
 * <p> 商品模板管理service
 * Create:  2019/8/11  22:49
 */
@Service
public class TemplateServiceImpl implements TemplateService {

    @Autowired(required = false)
    private TemplateMapper templateMapper;
    @Autowired(required = false)
    private CategoryMapper categoryMapper;

    /**
     * 根据categoryId查询模板数据
     *
     * @param categoryId 分类id
     * @return
     */
    @Override
    public Template findByCategoryId(Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        // 此处可以不做非空判断,因为前端传来的categoryId都是从数据库查询出来的,正常情况下是不会查不到分类数据的0
        return templateMapper.selectByPrimaryKey(category.getTemplateId());
    }

    /**
     * 查询所有商品模板数据
     *
     * @return 返回模板数据
     */
    @Override
    public List<Template> findAll() {
        return templateMapper.selectAll();
    }

    /**
     * 根据Id查询模板
     *
     * @param id 模板id
     * @return 返回模板数据
     */
    @Override
    public Template findById(Integer id) {
        return templateMapper.selectByPrimaryKey(id);
    }

    /**
     * 增加商品模板数据
     *
     * @param template 封装了要新增的模板信息
     */
    @Override
    public void add(Template template) {
        // 保存一个实体，null的属性不会保存，会使用数据库默认值
        templateMapper.insertSelective(template);
    }

    /**
     * 更新商品模板信息
     *
     * @param template 封装了要更新的模板信息
     */
    @Override
    public void update(Template template) {
        templateMapper.updateByPrimaryKeySelective(template);
    }

    /**
     * 根据id删除商品模板数据
     *
     * @param id 模板id
     */
    @Override
    public void deleteById(Integer id) {
        templateMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据条件查询商品模板数据
     *
     * @param template 封装了查询的条件
     * @return 返回符合条件的模板数据
     */
    @Override
    public List<Template> findByCondition(Template template) {
        // 封装查询条件
        Example example = getExample(template);
        return templateMapper.selectByExample(example);
    }

    /**
     * 分页查询商品模板数据
     *
     * @param pageNum 当前页码数
     * @param size    每页显示个数
     * @return 当前页面所需要的模板数据,
     */
    @Override
    public PageInfo<Template> findByPage(Integer pageNum, Integer size) {
        // 使用第三方插件进行分页查询
        PageHelper.startPage(pageNum, size);
        List<Template> templates = templateMapper.selectAll();
        // 将查询结果进行封装,并返回
        return new PageInfo<>(templates);
    }

    /**
     * 分页+条件查询
     *
     * @param template 封装了查询条件
     * @param pageNum  当前页码数
     * @param size     每页显示个数
     * @return 当前页面所需要的模板数据
     */
    @Override
    public PageInfo<Template> search(Template template, Integer pageNum, Integer size) {
        Example example = getExample(template);
        PageHelper.startPage(pageNum, size);
        List<Template> templates = templateMapper.selectByExample(example);
        return new PageInfo<>(templates);
    }

    /**
     * 封装查询条件的方法
     *
     * @param template 含有查询条件的实体类对象
     * @return 封装了查询条件的对象
     */
    private Example getExample(Template template) {
        // 封装查询条件对象 允许表的列不存在,也允许实体类的字段不存在
        Example example = new Example(Template.class, false, false);
        // Criteria为实际进行动态Sql语句拼接的对象
        Example.Criteria criteria = example.createCriteria();
        if(template != null) {
            criteria.andLike("name", "%" + (template.getName() == null ? "" : template.getName()) + "%")
                    .andEqualTo("specNum", template.getSpecNum())
                    .andEqualTo("paraNum", template.getParaNum())
                    .andEqualTo("id", template.getId());
        }
        return example;
    }
}
