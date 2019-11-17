package org.changgou.search.controller;

import org.changgou.entity.Result;
import org.changgou.entity.StatusCode;
import org.changgou.goods.feign.SkuFeign;
import org.changgou.goods.pojo.Sku;
import org.changgou.search.service.SkuSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Author:  HZ
 * <p> ES搜索服务Controller
 * Create:  2019/8/15  19:02
 */
@RestController
@RequestMapping("/search")
public class SkuSearchController {

    @Autowired
    private SkuSearchService searchService;
    @Autowired(required = false)
    private SkuFeign skuFeign;

    @PostMapping("/importSku")
    public Result importSkuData() {
        // feign调用goods模块,获得所有已经通过审核的商品的数据集合
        String status = "1";
        Result<List<Sku>> byStatus = skuFeign.findByStatus(status);
        // 讲得到的数据传入search服务的service模块进行ES保存
        searchService.importSkuData(byStatus.getData());
        return new Result(true, StatusCode.OK, "导入Sku数据至ES成功");
    }

    /**
     * 搜索,具备关键字搜索,品牌分类,规格分类等完整功能
     *
     * @param searchMap
     * @return
     */
    @GetMapping
    public Result<Map<String, Object>> search(@RequestParam(required = false) Map<String, String> searchMap) {
        Map<String, Object> resultMap = searchService.search(searchMap);
        return new Result<>(true, StatusCode.OK, "关键字搜索成功", resultMap);
    }
}
