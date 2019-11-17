package org.changgou.user.dao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.changgou.user.pojo.User;
import tk.mybatis.mapper.common.Mapper;

/****
 * @Author:shenkunlin
 * @Description:User的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface UserMapper extends Mapper<User> {

    /**
     * 下订单后的增加积分操作
     * @param username 用户名
     * @param score 增加的积分
     * @return
     */
    @Update("update tb_user set points=points+#{score} where username = #{username}")
    void points(@Param("username") String username, @Param("score") Integer score);
}
