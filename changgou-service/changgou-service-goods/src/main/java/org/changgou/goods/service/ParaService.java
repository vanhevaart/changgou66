package org.changgou.goods.service;

import com.github.pagehelper.PageInfo;
import org.changgou.goods.pojo.Para;
import org.changgou.goods.pojo.Spec;

import java.util.List;

/**
 * 商品参数管理service接口
 */
public interface ParaService {

    /**
     * 根据templateId查询参数数据
     * @param templateId 模板id
     * @return 该模板id所对应的所有参数数据
     */
    List<Para> findByTemplateId(Integer templateId);

    /**
     * 查询所有参数数据
     * @return 返回参数数据
     */
    List<Para> findAll();

    /**
     * 根据Id查询参数
     * @param id 参数id
     * @return 返回参数数据
     */
    Para findById(Integer id);

    /**
     * 增加商品参数数据
     * @param para 封装了要新增的参数信息
     */
    void add(Para para);

    /**
     * 更新商品参数信息
     * @param para 封装了要更新的参数信息
     */
    void update(Para para);

    /**
     * 根据id删除商品参数数据
     * @param id 参数id
     */
    void deleteById(Integer id);

    /**
     * 根据条件查询商品参数数据
     * @param para 封装了查询的条件
     * @return 返回符合条件的参数数据
     */
    List<Para> findByCondition(Para para);

    /**
     * 分页查询商品参数数据
     * @param pageNum 当前页码数
     * @param size 每页显示个数
     * @return 当前页面所需要的参数数据
     */
    PageInfo<Para> findByPage(Integer pageNum, Integer size);

    /**
     * 分页+条件查询
     * @param para 封装了查询条件
     * @param pageNum 当前页码数
     * @param size 每页显示个数
     * @return 当前页面所需要的参数数据
     */
    PageInfo<Para> search(Para para, Integer pageNum, Integer size);
}
