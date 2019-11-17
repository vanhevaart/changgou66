package org.changgou.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/25  9:35
 * @author HeZheng
 */
@Controller
@RequestMapping("/oauth")
public class LoginRedirect {

    /**
     * 定向到登录页面的方法
     * @param ReturnUrl 登录后的返回路径,可以为空
     * @param model 存储返回路径
     * @return 登录页面逻辑视图
     */
    @RequestMapping("/login")
    public String loginRedirect(@RequestParam(value = "ReturnUrl",required = false) String ReturnUrl, Model model){
        model.addAttribute("ReturnUrl",ReturnUrl);
        return "login";
    }
}
