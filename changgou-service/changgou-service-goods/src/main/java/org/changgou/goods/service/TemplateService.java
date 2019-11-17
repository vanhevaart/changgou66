package org.changgou.goods.service;

import com.github.pagehelper.PageInfo;
import org.changgou.goods.pojo.Template;

import java.util.List;

/**
 * 商品模板管理service接口
 */
public interface TemplateService {

    /**
     * 根据categoryId查询模板数据
     * @param categoryId 分类id
     * @return
     */
    Template findByCategoryId(Integer categoryId);

    /**
     * 查询所有模板数据
     * @return 返回模板数据
     */
    List<Template> findAll();

    /**
     * 根据Id查询模板
     * @param id 模板id
     * @return 返回模板数据
     */
    Template findById(Integer id);

    /**
     * 增加商品模板数据
     * @param template 封装了要新增的模板信息
     */
    void add(Template template);

    /**
     * 更新商品模板信息
     * @param template 封装了要更新的模板信息
     */
    void update(Template template);

    /**
     * 根据id删除商品模板数据
     * @param id 模板id
     */
    void deleteById(Integer id);

    /**
     * 根据条件查询商品模板数据
     * @param template 封装了查询的条件
     * @return 返回符合条件的模板数据
     */
    List<Template> findByCondition(Template template);

    /**
     * 分页查询商品模板数据
     * @param pageNum 当前页码数
     * @param size 每页显示个数
     * @return 当前页面所需要的模板数据
     */
    PageInfo<Template> findByPage(Integer pageNum, Integer size);

    /**
     * 分页+条件查询
     * @param template 封装了查询条件
     * @param pageNum 当前页码数
     * @param size 每页显示个数
     * @return 当前页面所需要的模板数据
     */
    PageInfo<Template> search(Template template, Integer pageNum, Integer size);
}
