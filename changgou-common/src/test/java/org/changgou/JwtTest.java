package org.changgou;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/20  21:45
 */
public class JwtTest {

    /**
     * 创建JWT测试
     */
    @Test
    public void createJwtTest(){
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DAY_OF_MONTH,7);
        JwtBuilder builder = Jwts.builder()
                                .setId("888") // 设置ID
                                .setSubject("小白") // 设置主题
                                .setIssuedAt(new Date()) // 设置签发日期
                                .signWith(SignatureAlgorithm.HS256,"itcast") // 设置签名 使用H256算法,并设置SecretKey字符串
                                .setExpiration(instance.getTime()); // 设置过期时间
        // 自定义Claims
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name","张三");
        userInfo.put("age",18);
        userInfo.put("address","深圳黑马66期");
        builder.addClaims(userInfo);
        System.out.println(builder.compact()); // 构建JWT
    }

    @Test
    public void parseJwtTest(){
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4ODgiLCJzdWIiOiLlsI_nmb0iLCJpYXQiOjE1NjYzMTA1MjYsImV4cCI6MTU2NjkxNTMyNiwiYWRkcmVzcyI6Iua3seWcs-m7kemprDY25pyfIiwibmFtZSI6IuW8oOS4iSIsImFnZSI6MTh9.59VqwbXoTRnRnewHHoJ2LROAFbxb8sq0yAbiwsuWGo4";
        JwtParser parser = Jwts.parser();
        Claims itcast = parser.setSigningKey("itcast").parseClaimsJws(jwt).getBody();
        System.out.println(itcast);

    }
}
