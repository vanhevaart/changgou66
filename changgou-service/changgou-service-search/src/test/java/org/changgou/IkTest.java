package org.changgou;

import org.changgou.search.dao.SkuSearchMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/16  17:11
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class IkTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void fieldAnnotationTest(){
        User user1 = new User(1L,"小明",18,"湖南长沙","赵毅来自广州白云区，从事电子商务8年！");
        User user2 = new User(2L,"小红",28,"广州深圳","武汉赵哈哈，在深圳打工已有半年了，月薪7500！");
        User user3 = new User(3L,"小强",38,"浙江杭州","赵子龙来自深圳宝安，但是在广州工作！");
        List<User> list = new ArrayList<>();
        list.add(user1);
        list.add(user2);
        list.add(user3);
        userMapper.saveAll(list);
    }
}
