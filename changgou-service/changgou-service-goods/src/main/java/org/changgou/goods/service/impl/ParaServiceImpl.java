package org.changgou.goods.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.changgou.goods.dao.ParaMapper;
import org.changgou.goods.dao.TemplateMapper;
import org.changgou.goods.pojo.Para;
import org.changgou.goods.pojo.Template;
import org.changgou.goods.service.ParaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Author:  HZ
 * <p> 商品参数管理service
 * Create:  2019/8/11  22:49
 */
@Service
public class ParaServiceImpl implements ParaService {

    @Autowired(required = false)
    private ParaMapper paraMapper;

    @Autowired(required = false)
    private TemplateMapper templateMapper;

    /**
     * 根据templateId查询参数数据
     *
     * @param templateId 模板id
     * @return 该模板id所对应的所有参数数据
     */
    @Override
    public List<Para> findByTemplateId(Integer templateId) {
        Para para = new Para();
        para.setTemplateId(templateId);
        Example example = getExample(para);
        return paraMapper.selectByExample(example);
    }

    /**
     * 查询所有商品参数数据
     *
     * @return 返回参数数据
     */
    @Override
    public List<Para> findAll() {
        return paraMapper.selectAll();
    }

    /**
     * 根据Id查询参数
     *
     * @param id 参数id
     * @return 返回参数数据
     */
    @Override
    public Para findById(Integer id) {
        return paraMapper.selectByPrimaryKey(id);
    }

    /**
     * 增加商品参数数据,需要更新模板表的数据
     *
     * @param para 封装了要新增的参数信息
     */
    @Override
    public void add(Para para) {
        // 保存一个实体，null的属性不会保存，会使用数据库默认值
        paraMapper.insertSelective(para);
        // 更新模板表的数据
        updateTemplate(para.getTemplateId(), 1);
    }

    /**
     * 更新商品参数信息,需要更新模板表的数据
     *
     * @param para 封装了要更新的参数信息
     */
    @Override
    public void update(Para para) {
        // 根据id查询出需要更新的记录
        Para byId = paraMapper.selectByPrimaryKey(para.getId());
        // 分别取出数据库和前端传来的两者的templateId
        Integer templateId = para.getTemplateId();
        Integer byIdTemplateId = byId.getTemplateId();
        // 判断是否更改了templateId的值
        if(templateId != null && !templateId.equals(byIdTemplateId)) {
            // templateId发生了改变
            // 更新模板表的数据
            updateTemplate(byIdTemplateId, -1);
            updateTemplate(templateId, 1);
        }
        paraMapper.updateByPrimaryKeySelective(para);

    }

    /**
     * 根据id删除商品参数数据,需要更新模板表的数据
     *
     * @param id 参数id
     */
    @Override
    public void deleteById(Integer id) {
        // 先查询出需要删除的数据,记录其templateId
        Para para = paraMapper.selectByPrimaryKey(id);
        // 根据id删除数据
        paraMapper.deleteByPrimaryKey(id);
        // 更新模板表的数据
        updateTemplate(para.getTemplateId(), -1);
    }

    /**
     * 根据条件查询商品参数数据
     *
     * @param para 封装了查询的条件
     * @return 返回符合条件的参数数据
     */
    @Override
    public List<Para> findByCondition(Para para) {
        // 封装查询条件
        Example example = getExample(para);
        return paraMapper.selectByExample(example);
    }

    /**
     * 分页查询商品参数数据
     *
     * @param pageNum 当前页码数
     * @param size    每页显示个数
     * @return 当前页面所需要的参数数据,
     */
    @Override
    public PageInfo<Para> findByPage(Integer pageNum, Integer size) {
        // 使用第三方插件进行分页查询
        PageHelper.startPage(pageNum, size);
        List<Para> paras = paraMapper.selectAll();
        // 将查询结果进行封装,并返回
        return new PageInfo<>(paras);
    }

    /**
     * 分页+条件查询
     *
     * @param para    封装了查询条件
     * @param pageNum 当前页码数
     * @param size    每页显示个数
     * @return 当前页面所需要的参数数据
     */
    @Override
    public PageInfo<Para> search(Para para, Integer pageNum, Integer size) {
        Example example = getExample(para);
        PageHelper.startPage(pageNum, size);
        List<Para> paras = paraMapper.selectByExample(example);
        return new PageInfo<>(paras);
    }

    /**
     * 封装查询条件的方法
     *
     * @param para 含有查询条件的实体类对象
     * @return 封装了查询条件的对象
     */
    private Example getExample(Para para) {
        // 封装查询条件对象 允许表的列不存在,也允许实体类的字段不存在
        Example example = new Example(Para.class, false, false);
        // Criteria为实际进行动态Sql语句拼接的对象
        Example.Criteria criteria = example.createCriteria();
        if(para != null) {
            criteria.andLike("name", "%" + (para.getName() == null ? "" : para.getName()) + "%")
                    .andEqualTo("seq", para.getSeq())
                    .andEqualTo("templateId", para.getTemplateId())
                    .andEqualTo("id", para.getId())
                    .andLike("options", "%" + (para.getOptions() == null ? "" : para.getOptions()) + "%");
        }
        return example;
    }

    /**
     * 更新模板表的数据
     *
     * @param templateId 要更新的模板的id
     * @param count      变更的数量
     */
    private void updateTemplate(Integer templateId, Integer count) {
        Template template = templateMapper.selectByPrimaryKey(templateId);
        template.setParaNum(template.getParaNum() + count);
        templateMapper.updateByPrimaryKeySelective(template);
    }
}
