package org.changgou.goods.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.changgou.goods.dao.SpecMapper;
import org.changgou.goods.dao.TemplateMapper;
import org.changgou.goods.pojo.Spec;
import org.changgou.goods.pojo.Template;
import org.changgou.goods.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Author:  HZ
 * <p> 商品规格管理service
 * Create:  2019/8/11  22:49
 */
@Service
public class SpecServiceImpl implements SpecService {

    @Autowired(required = false)
    private SpecMapper specMapper;

    @Autowired(required = false)
    private TemplateMapper templateMapper;

    /**
     * 根据templateId查询规格数据
     *
     * @param templateId 模板id
     * @return 该模板id所对应的所有规格数据
     */
    @Override
    public List<Spec> findByTemplateId(Integer templateId) {
        Spec spec = new Spec();
        spec.setTemplateId(templateId);
        Example example = getExample(spec);
        return specMapper.selectByExample(example);
    }

    /**
     * 查询所有商品规格数据
     *
     * @return 返回规格数据
     */
    @Override
    public List<Spec> findAll() {
        return specMapper.selectAll();
    }

    /**
     * 根据Id查询规格
     *
     * @param id 规格id
     * @return 返回规格数据
     */
    @Override
    public Spec findById(Integer id) {
        return specMapper.selectByPrimaryKey(id);
    }

    /**
     * 增加商品规格数据,需要更新模板表的数据
     *
     * @param spec 封装了要新增的规格信息
     */
    @Override
    public void add(Spec spec) {
        // 保存一个实体，null的属性不会保存，会使用数据库默认值
        specMapper.insertSelective(spec);
        // 更新模板表的数据
        updateTemplate(spec.getTemplateId(), 1);
    }

    /**
     * 更新商品规格信息,需要更新模板表的数据
     *
     * @param spec 封装了要更新的规格信息
     */
    @Override
    public void update(Spec spec) {
        // 根据id查询出需要更新的记录
        Spec byId = specMapper.selectByPrimaryKey(spec.getId());
        // 分别取出数据库和前端传来的两者的templateId
        Integer templateId = spec.getTemplateId();
        Integer byIdTemplateId = byId.getTemplateId();
        // 判断是否更改了templateId的值
        if(templateId != null && !templateId.equals(byIdTemplateId)) {
            // templateId发生了改变
            // 更新模板表的数据
            updateTemplate(byIdTemplateId, -1);
            updateTemplate(templateId, 1);
        }
        specMapper.updateByPrimaryKeySelective(spec);

    }

    /**
     * 根据id删除商品规格数据,需要更新模板表的数据
     *
     * @param id 规格id
     */
    @Override
    public void deleteById(Integer id) {
        // 先查询出需要删除的数据,记录其templateId
        Spec spec = specMapper.selectByPrimaryKey(id);
        // 根据id删除数据
        specMapper.deleteByPrimaryKey(id);
        // 更新模板表的数据
        updateTemplate(spec.getTemplateId(), -1);
    }

    /**
     * 根据条件查询商品规格数据
     *
     * @param spec 封装了查询的条件
     * @return 返回符合条件的规格数据
     */
    @Override
    public List<Spec> findByCondition(Spec spec) {
        // 封装查询条件
        Example example = getExample(spec);
        return specMapper.selectByExample(example);
    }

    /**
     * 分页查询商品规格数据
     *
     * @param pageNum 当前页码数
     * @param size    每页显示个数
     * @return 当前页面所需要的规格数据,
     */
    @Override
    public PageInfo<Spec> findByPage(Integer pageNum, Integer size) {
        // 使用第三方插件进行分页查询
        PageHelper.startPage(pageNum, size);
        List<Spec> specs = specMapper.selectAll();
        // 将查询结果进行封装,并返回
        return new PageInfo<>(specs);
    }

    /**
     * 分页+条件查询
     *
     * @param spec    封装了查询条件
     * @param pageNum 当前页码数
     * @param size    每页显示个数
     * @return 当前页面所需要的规格数据
     */
    @Override
    public PageInfo<Spec> search(Spec spec, Integer pageNum, Integer size) {
        Example example = getExample(spec);
        PageHelper.startPage(pageNum, size);
        List<Spec> specs = specMapper.selectByExample(example);
        return new PageInfo<>(specs);
    }

    /**
     * 封装查询条件的方法
     *
     * @param spec 含有查询条件的实体类对象
     * @return 封装了查询条件的对象
     */
    private Example getExample(Spec spec) {
        // 封装查询条件对象 允许表的列不存在,也允许实体类的字段不存在
        Example example = new Example(Spec.class, false, false);
        // Criteria为实际进行动态Sql语句拼接的对象
        Example.Criteria criteria = example.createCriteria();
        if(spec != null) {
            criteria.andLike("name", "%" + (spec.getName() == null ? "" : spec.getName()) + "%")
                    .andEqualTo("seq", spec.getSeq())
                    .andEqualTo("templateId", spec.getTemplateId())
                    .andEqualTo("id", spec.getId())
                    .andLike("options", "%" + (spec.getOptions() == null ? "" : spec.getOptions()) + "%");
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
        template.setSpecNum(template.getSpecNum() + count);
        templateMapper.updateByPrimaryKeySelective(template);
    }
}
