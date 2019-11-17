package org.changgou.goods.dao;

import org.apache.ibatis.annotations.Select;
import org.changgou.goods.pojo.Brand;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 商品品牌Dao接口
 */
public interface BrandMapper extends Mapper<Brand> {

    /**
     * 根据分类id查询品牌新
     *
     * @param categoryId 分类id
     * @return 该分类下所拥有的品牌信息
     */
    @Select("SELECT tb.id,tb.name FROM tb_category_brand cb ,tb_brand tb WHERE tb.id = cb.brand_id and cb.category_id=#{categoryId}")
    List<Brand> findByCategoryId(Integer categoryId);
}
