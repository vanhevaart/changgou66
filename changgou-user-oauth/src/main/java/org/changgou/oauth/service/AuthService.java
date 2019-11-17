package org.changgou.oauth.service;

import org.changgou.oauth.util.AuthToken;

/*****
 * @Author: shenkunlin
 * @Date: 2019/7/7 16:23
 * @Description: com.changgou.oauth.service
 ****/
public interface AuthService {

    /***
     * 授权认证方法
     */
    AuthToken login(String username, String password, String clientId, String clientSecret);

    void getGitHubToken(String code);

}
