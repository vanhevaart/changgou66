package org.changgou.goods.dao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.changgou.goods.pojo.Sku;
import tk.mybatis.mapper.common.Mapper;

/****
 * @Author:shenkunlin
 * @Description:Sku的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface SkuMapper extends Mapper<Sku> {

    @Update("update tb_sku set num = num-#{num} where id=#{skuId} and num>=#{num}")
    int decr(@Param(value = "skuId")Long skuId, @Param(value = "num") Integer num);
}
