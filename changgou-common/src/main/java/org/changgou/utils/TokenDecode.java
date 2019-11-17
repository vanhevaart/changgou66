package org.changgou.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author:  HZ
 * <p> 解析存入OAuth框架上下文的token信息获取登录用户的相关信息
 * Create:  2019/8/24  19:50
 */
public class TokenDecode {

    //公钥
    private static final String PUBLIC_KEY = "public.key";

    private static String publicKey="";

    /**
     * 获取非对称加密公钥 Key
     *
     * @return 公钥 Key
     */
    private static String getPubKey() {
        // 引用资源服务器认证token的方法
        if(!StringUtils.isEmpty(publicKey)){
            return publicKey;
        }
        Resource resource = new ClassPathResource(PUBLIC_KEY);
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream());
            BufferedReader br = new BufferedReader(inputStreamReader);
            publicKey = br.lines().collect(Collectors.joining("\n"));
            return publicKey;
        } catch (IOException ioe) {
            return null;
        }
    }

    /**
     * 解密并解析token
     *
     * @param token
     */
    private static Map<String,String> parseToken(String token) {
        // 根据加密公钥 key 进行token的解密
        Jwt jwt = JwtHelper.decodeAndVerify(token,new RsaVerifier(getPubKey()));
        String claims = jwt.getClaims();
        return JSON.parseObject(claims, Map.class);
    }

    public static Map<String, String> getUserInfo() {
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String tokenValue = details.getTokenValue();
        return parseToken(tokenValue);
    }
}
