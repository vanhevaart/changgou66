package org.changgou.goods.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.changgou.goods.dao.BrandMapper;
import org.changgou.goods.pojo.Brand;
import org.changgou.goods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Author:  HZ
 * <p> 商品品牌管理service
 * Create:  2019/8/10  11:54
 */
@Service
public class BrandServiceImpl implements BrandService {

    @Autowired(required = false)
    private BrandMapper brandMapper;

    /**
     * 根据分类id查询品牌新
     * (注意此处可自定义方法进行多表关联查询,而不必使用2次工具提供的单表查询方法)
     *
     * @param categoryId 分类id
     * @return 该分类下所拥有的品牌信息
     */
    @Override
    public List<Brand> findByCategoryId(Integer categoryId) {
        return brandMapper.findByCategoryId(categoryId);
    }

    /**
     * 查询所有商品品牌数据
     *
     * @return 返回品牌数据
     */
    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }

    /**
     * 根据Id查询品牌
     *
     * @param id 品牌id
     * @return 返回品牌数据
     */
    @Override
    public Brand findById(Integer id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 增加商品品牌数据
     *
     * @param brand 封装了要新增的品牌信息
     */
    @Override
    public void add(Brand brand) {
        // 保存一个实体，null的属性不会保存，会使用数据库默认值
        brandMapper.insertSelective(brand);
    }

    /**
     * 更新商品品牌信息
     *
     * @param brand 封装了要更新的品牌信息
     */
    @Override
    public void update(Brand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    /**
     * 根据id删除商品品牌数据
     *
     * @param id 品牌id
     */
    @Override
    public void deleteById(Integer id) {
        brandMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据条件查询商品品牌数据
     *
     * @param brand 封装了查询的条件
     * @return 返回符合条件的品牌数据
     */
    @Override
    public List<Brand> findByCondition(Brand brand) {
        // 封装查询条件
        Example example = getExample(brand);
        return brandMapper.selectByExample(example);
    }

    /**
     * 分页查询商品品牌数据
     *
     * @param pageNum 当前页码数
     * @param size    每页显示个数
     * @return 当前页面所需要的品牌数据,
     */
    @Override
    public PageInfo<Brand> findByPage(Integer pageNum, Integer size) {
        // 使用第三方插件进行分页查询
        PageHelper.startPage(pageNum, size);
        List<Brand> brands = brandMapper.selectAll();
        // 将查询结果进行封装,并返回
        return new PageInfo<>(brands);
    }

    /**
     * 分页+条件查询
     *
     * @param brand   封装了查询条件
     * @param pageNum 当前页码数
     * @param size    每页显示个数
     * @return 当前页面所需要的品牌数据
     */
    @Override
    public PageInfo<Brand> search(Brand brand, Integer pageNum, Integer size) {
        Example example = getExample(brand);
        PageHelper.startPage(pageNum, size);
        List<Brand> brands = brandMapper.selectByExample(example);
        return new PageInfo<>(brands);
    }

    /**
     * 封装查询条件的方法
     *
     * @param brand 含有查询条件的实体类对象
     * @return 封装了查询条件的对象
     */
    private Example getExample(Brand brand) {
        // 封装查询条件对象 允许表的列不存在,也允许实体类的字段不存在
        Example example = new Example(Brand.class, false, false);
        // Criteria为实际进行动态Sql语句拼接的对象
        Example.Criteria criteria = example.createCriteria();
        if(brand != null) {
            criteria.andLike("name", "%" + (brand.getName() == null ? "" : brand.getName()) + "%")
                    .andEqualTo("letter", brand.getLetter())
                    .andEqualTo("seq", brand.getSeq());
        }
        return example;
    }
}
