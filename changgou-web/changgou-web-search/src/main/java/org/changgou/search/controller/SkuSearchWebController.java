package org.changgou.search.controller;


import org.changgou.entity.Result;
import org.changgou.search.feign.SkuSearchFeign;
import org.changgou.search.pojo.SkuInfo;
import org.changgou.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/18  21:07
 */
@Controller
@RequestMapping("/search")
public class SkuSearchWebController {

    @Autowired
    private SkuSearchFeign skuSearchFeign;

    @GetMapping("/list")
    public String search(@RequestParam(required = false) Map<String, String> searchMap, Model model) {
        // 处理url中的特殊字符
        handlerSearchMap(searchMap);
        // 远程调用获取搜索数据
        Result<Map<String, Object>> result = skuSearchFeign.search(searchMap);
        model.addAttribute("result", result);
        // 返回搜索条件
        model.addAttribute("searchMap", searchMap);
        // 返回搜素url
        String searchUrl = getSearchUrl(searchMap);
        model.addAttribute("searchUrl", searchUrl);
        // 封装Page对象,共前端分页使用
        Page<SkuInfo> page = new Page<>(
                Long.parseLong(result.getData().get("total").toString()),
                Integer.parseInt(result.getData().get("pageNum").toString()) + 1,
                Integer.parseInt(result.getData().get("pageSize").toString())
        );
        model.addAttribute("page", page);
        return "search";
    }

    /**
     * 解析拼接用户的url
     * 点击排序,需清除页码信息,从第一页开始查询,并且不需要以前的排序信息,所以需要在后台重新拼接url
     * @param searchMap
     * @return
     */
    private String getSearchUrl(Map<String, String> searchMap) {
        // 初始化url
        String result = "";
        StringBuilder sb = new StringBuilder("/search/list");
        // 非空判断
        if(searchMap != null && searchMap.keySet().size() > 0) {
            sb.append("?");
            int sortCount=0;
            // 拼接url
            for (Map.Entry<String, String> entries : searchMap.entrySet()) {
                // 分页页码参数需要跳过
                if(entries.getKey().equals("pageNum")) {
                    continue;
                }
                if(entries.getKey().equals("sortField")){
                   if(sortCount <1){
                       sortCount++;
                   }else {
                       continue;
                   }
                }
                sb.append(entries.getKey())
                        .append("=")
                        .append(entries.getValue())
                        .append("&");
            }
            result = sb.substring(0, sb.length() - 1);
        }
        return result;
    }

    /**
     * 处理url中的特殊字符,进行转义
     * @param searchMap
     */
    private void handlerSearchMap(Map<String, String> searchMap){
        if(searchMap!=null){
            for (Map.Entry<String, String> entry : searchMap.entrySet()) {
                if(entry.getKey().startsWith("spec_")){
                    entry.setValue(entry.getValue().replace("+","%2B"));
                }
            }
        }
    }
}
