package org.changgou.item.service.impl;

import com.alibaba.fastjson.JSON;
import org.changgou.entity.Result;
import org.changgou.goods.feign.CategoryFeign;
import org.changgou.goods.feign.SkuFeign;
import org.changgou.goods.feign.SpuFeign;
import org.changgou.goods.pojo.Category;
import org.changgou.goods.pojo.Sku;
import org.changgou.goods.pojo.Spu;
import org.changgou.item.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/20  10:57
 */
@Service
public class PageServiceImpl implements PageService {

    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private CategoryFeign categoryFeign;
    @Autowired
    private SpuFeign spuFeign;
    @Autowired
    private SkuFeign skuFeign;
    @Value(value = "${pagePath}")
    private String pagePath;

    /**
     * 生成静态页面
     *
     * @param spuId 商品spuId
     */
    @Override
    public void createHtml(long spuId) {
        // 构建静态页上下文
        Context context = new Context();
        // 获取静态所需数据
        Map<String, Object> dataModel = buildDataModel(spuId);
        context.setVariables(dataModel);
        // 输出文件位置准备
        File dir = new File(pagePath);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        // 生成页面
        File file = new File(dir, spuId + ".html");
        try (
                PrintWriter writer = new PrintWriter(new FileWriter(file))
        ) {
            // arg0: 模板的逻辑名称  arg1:数据对象  arg2:输出流对象
            templateEngine.process("item", context, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取静态页面所需要的数据
     *
     * @param spuId 商品spuId
     */
    private Map<String, Object> buildDataModel(long spuId) {
        Map<String, Object> dataMap = new HashMap<>();
        // 远程调用feign接口方法获取 spu,sku信息
        Result<Spu> byId = spuFeign.findById(spuId);
        Spu spu = byId.getData();
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        List<Sku> skuList = skuFeign.findBySpuId(sku).getData();
        // 通过id调用feign接口方法获取category数据
        Category category1 = categoryFeign.findById(spu.getCategory1Id()).getData();
        Category category2 = categoryFeign.findById(spu.getCategory2Id()).getData();
        Category category3 = categoryFeign.findById(spu.getCategory3Id()).getData();
        // 解析spu的image信息
        String[] images = null;
        if(spu.getImages() != null) {
            images = spu.getImages().split(",");
        }
        // 解析spu的spec_items信息
        Map specMap = JSON.parseObject(spu.getSpecItems(), Map.class);
        // 封装数据
        dataMap.put("spu", spu);
        dataMap.put("skuList", skuList);
        dataMap.put("category1", category1);
        dataMap.put("category2", category2);
        dataMap.put("category3", category3);
        dataMap.put("imageList", images);
        dataMap.put("specificationList", specMap);
        return dataMap;
    }
}
