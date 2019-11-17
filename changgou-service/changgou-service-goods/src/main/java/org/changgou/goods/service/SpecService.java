package org.changgou.goods.service;

import com.github.pagehelper.PageInfo;
import org.changgou.goods.pojo.Spec;

import java.util.List;

/**
 * 商品规格管理service接口
 */
public interface SpecService {

    /**
     * 根据templateId查询规格数据
     * @param templateId 模板id
     * @return 该模板id所对应的所有规格数据
     */
    List<Spec> findByTemplateId(Integer templateId);

    /**
     * 查询所有规格数据
     * @return 返回规格数据
     */
    List<Spec> findAll();

    /**
     * 根据Id查询规格
     * @param id 规格id
     * @return 返回规格数据
     */
    Spec findById(Integer id);

    /**
     * 增加商品规格数据
     * @param spec 封装了要新增的规格信息
     */
    void add(Spec spec);

    /**
     * 更新商品规格信息
     * @param spec 封装了要更新的规格信息
     */
    void update(Spec spec);

    /**
     * 根据id删除商品规格数据
     * @param id 规格id
     */
    void deleteById(Integer id);

    /**
     * 根据条件查询商品规格数据
     * @param spec 封装了查询的条件
     * @return 返回符合条件的规格数据
     */
    List<Spec> findByCondition(Spec spec);

    /**
     * 分页查询商品规格数据
     * @param pageNum 当前页码数
     * @param size 每页显示个数
     * @return 当前页面所需要的规格数据
     */
    PageInfo<Spec> findByPage(Integer pageNum, Integer size);

    /**
     * 分页+条件查询
     * @param spec 封装了查询条件
     * @param pageNum 当前页码数
     * @param size 每页显示个数
     * @return 当前页面所需要的规格数据
     */
    PageInfo<Spec> search(Spec spec, Integer pageNum, Integer size);
}
