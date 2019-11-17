package org.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import org.changgou.goods.pojo.Sku;
import org.changgou.search.dao.SkuSearchMapper;
import org.changgou.search.pojo.SkuInfo;
import org.changgou.search.service.SkuSearchService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Author:  HZ
 * <p> ES搜索服务业务实现类
 * Create:  2019/8/15  19:43
 */
public class SkuSearchServiceImpl {

  /*  @Autowired
    private SkuSearchMapper searchMapper;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    private Logger logger = LoggerFactory.getLogger(SkuSearchServiceImpl.class);

    *//**
     * 导入Sku数据到ES中
     *
     * @param data Sku数据集合
     *//*
    @Override
    public void importSkuData(List<Sku> data) {
         *//*将Sku数据转换成SkuInfo数据
            因为SkuInfo的字段几乎与Sku一样,只是加上了ES的注解,用于建立索引和分词等
            极个别的如specMap字段,也按照JSON解析规则,转换成Map<String, String>的结构,可以很方便的进行单独的转换
            所有此处可以直接使用JSON解析工具进行两个类型间的转换
          *//*
        String jsonString = JSON.toJSONString(data);
        List<SkuInfo> skuInfos = JSON.parseArray(jsonString, SkuInfo.class);
        // 填充specMap字段的数据
        for (SkuInfo skuInfo : skuInfos) {
            Map map = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(map);
        }
        // 进行ES数据的保存
        searchMapper.saveAll(skuInfos);
    }

    *//**
     * 搜索,具备关键字搜索,品牌分类,规格分类等完整功能
     *
     * @param searchMap 复合搜索条件
     * @return 包含前端所需的所有结果集
     *//*
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        Map<String, Object> resultMap = new HashMap<>();
        // 构建查询构建器
        NativeSearchQueryBuilder builder = buildBasicQuery(searchMap);
        // 根据查询构建器,进行基本的关键字搜索
        Map<String, Object> map = searchList(builder);
        // 根据查询构建器,进行分类搜索,如果传入了分类,则不需要进行分类搜索,因为前端不显示
        if(searchMap == null || searchMap.get("category") == null || "".equals(searchMap.get("category"))) {
            List<String> categoryList = searchCategoryName(builder);
            resultMap.put("categoryList", categoryList);
        }
        // 根据查询构建器,进行品牌搜索,如果传入了品牌,则不需要进行品牌搜索,因为前端不显示
        if(searchMap == null || searchMap.get("brand") == null || "".equals(searchMap.get("brand"))) {
            List<String> brandList = searchBrandName(builder);
            resultMap.put("brandList", brandList);
        }
        // 根据查询构建器,进行规格搜索
        Map<String, Set<String>> specMap = searchSpec(builder, (Long) map.get("total"));
        // TODO 根据查询构建器,进行价格区间搜索
        // 封装数据
        resultMap.put("rowData", map);
        resultMap.put("specMap", specMap);
        return resultMap;
    }

    *//**
     * 根据搜索条件map构建基本的查询构建器
     *
     * @param searchMap 包含查询条件的Map
     * @return 基本的关键字搜素构建器
     *//*
    private NativeSearchQueryBuilder buildBasicQuery(Map<String, String> searchMap) {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        // 构建多条件布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(searchMap != null) {
            // 关键字查询
            if(!StringUtils.isEmpty(searchMap.get("keywords"))) {
                // 常量得分查询
                ConstantScoreQueryBuilder boost = QueryBuilders.constantScoreQuery(QueryBuilders.termQuery("name", "手机配件")).boost(23f);
                boolQueryBuilder.should(boost);
//                builder.withQuery(QueryBuilders.matchQuery("name", searchMap.get("keywords")));
                boolQueryBuilder.must(QueryBuilders.matchQuery("name", searchMap.get("keywords")));
            }
            // 商品分类筛选
            if(!StringUtils.isEmpty(searchMap.get("category"))) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("categoryName", searchMap.get("category")));
            }
            // 商品品牌筛选
            if(!StringUtils.isEmpty(searchMap.get("brand"))) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("brandName", searchMap.get("brand")));
            }
            // 规格筛选

            Set<String> keySet = searchMap.keySet();
            for (String key : keySet) {
                if(key.startsWith("spec_")) {
                    // 截取拼接key使之与ES里的field名吻合
                    String fieldName = "specMap." + key.substring(5) + ".keyword";
                    boolQueryBuilder.must(QueryBuilders.matchQuery(fieldName, searchMap.get(key)));
                }
            }
            // 价格区间筛选
            String price = searchMap.get("price");
            if(!StringUtils.isEmpty(price)) {
                String[] split = price.split("-");
                if(split.length > 1) {
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lte(split[1]));
                }
                boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gte(split[0]));
            }
            // 分页查询
            Integer pageNum = pageConvert(searchMap);
            Integer pageSize = sizeConvert(searchMap);
            PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
            builder.withPageable(pageRequest);
            builder.withFilter(boolQueryBuilder);
            // 排序查询
            String sortField = searchMap.get("sortField");
            String sortRule = searchMap.get("sortRule");
            if(StringUtils.isEmpty(sortField)) {
                sortRule = sortRule != null ? sortRule : "desc";
                SortBuilders.fieldSort(sortField).order(SortOrder.valueOf(sortRule));
            }
        }
        return builder;
    }

    *//**
     * 获取页码
     * 如果正常,则进行字符串至Integer的转换
     * 如果发生异常,则默认返回1
     *
     * @param searchMap 查询条件集合
     * @return 返回页码
     *//*
    private Integer pageConvert(Map<String, String> searchMap) {
        try {
            return Integer.parseInt(searchMap.get("pageNum"));
        } catch (Exception e) {
            logger.info("返回默认分页参数");
        }
        return 1;
    }

    *//**
     * 获取没页大小
     * 如果正常,则进行字符串至Integer的转换
     * 如果发生异常,则默认返回30
     *
     * @param searchMap 查询条件集合
     * @return 返回每页size
     *//*
    private Integer sizeConvert(Map<String, String> searchMap) {
        try {
            return Integer.parseInt(searchMap.get("pageSize"));
        } catch (Exception e) {
            logger.info("返回默认分页参数");
        }
        return 30;
    }

    *//**
     * 关键字搜索查询,默认进行分页查询,关键字高亮
     * 注意此处的分页排序规则由ES根据搜索条件,自己决定.
     *
     * @param builder 查询构建器
     * @return 关键字搜素结果集, 包含页数和总条数
     *//*
    private Map<String, Object> searchList(NativeSearchQueryBuilder builder) {
        Map<String, Object> result = new HashMap<>();
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("name").preTags("<font color='red'>");
        return null;
    }

    *//**
     * 商品分类查询,查询分类列表
     *
     * @param builder 基本的查询构造器
     * @return 分类数据列表
     *//*
    private List<String> searchCategoryName(NativeSearchQueryBuilder builder) {
        List<String> list = new ArrayList<>();
        // 聚合搜索条件构建
        // 根据字段categoryName分组,进行查询,相当于mysql中的GROUP BY
        TermsAggregationBuilder terms = AggregationBuilders.terms("categoryName").field("categoryName");
        // 将聚合条件添加进builder中
        builder.addAggregation(terms);
        // 进行聚合查询
        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);
        // 结果集处理
        Aggregations aggregations = skuInfos.getAggregations();
        // 通过别名categoryName获得特定的聚合查询结果
        StringTerms stringTerms = aggregations.get("categoryName");
        // 封装结果集
        List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
        for (StringTerms.Bucket bucket : buckets) {
            String keyAsString = bucket.getKeyAsString();
            list.add(keyAsString);
        }
        return list;
    }

    *//**
     * 品牌查询,查询品牌列表
     *
     * @param builder 基本的查询构造器
     * @return 品牌数据列表
     *//*
    private List<String> searchBrandName(NativeSearchQueryBuilder builder) {
        List<String> list = new ArrayList<>();
        // 构建聚合查询条件
        TermsAggregationBuilder terms = AggregationBuilders.terms("brandName").field("brandName");
        // 将聚合条件添加进builder中
        builder.addAggregation(terms);
        // 进行聚合查询
        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);
        // 处理结果集
        Aggregations aggregations = skuInfos.getAggregations();
        // 根据别名brandName得到指定的查询结果
        StringTerms brandName = aggregations.get("brandName");
        // 封装查询结果
        List<StringTerms.Bucket> buckets = brandName.getBuckets();
        for (StringTerms.Bucket bucket : buckets) {
            list.add(bucket.getKeyAsString());
        }
        return list;
    }

    *//**
     * 规格查询, 查询产品规格列表
     *
     * @param builder 基本的查询构造器
     * @return 规格数据Map集合
     *//*
    private Map<String, Set<String>> searchSpec(NativeSearchQueryBuilder builder, Long total) {
        total = total == 0L ? 10000L : total;
        TermsAggregationBuilder terms = AggregationBuilders.terms("specList").field("spec.keyword").size(Math.toIntExact(total));
        builder.addAggregation(terms);
        // 进行聚合查询
        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);
        Aggregations aggregations = skuInfos.getAggregations();
        StringTerms specList = aggregations.get("specList");
        List<StringTerms.Bucket> buckets = specList.getBuckets();
        // 封装查询结果
        return parseSpecResult(buckets);
    }

    *//**
     * 解析规格查询的结果集,进行去重,并已适当的数据结构返回
     *
     * @param buckets 通过ES查询出的规格数据
     * @return 解析, 去重, 转换后的规格数据
     *//*
    private Map<String, Set<String>> parseSpecResult(List<StringTerms.Bucket> buckets) {
        Map<String, Set<String>> resultMap = new HashMap<>();
        for (StringTerms.Bucket bucket : buckets) {
            // 解析spec JSON串,转换成Map<String,String>结构的数据
            Map<String, String> map = JSON.parseObject(bucket.getKeyAsString(), Map.class);
            // 遍历该map,获取key和value
            Set<Map.Entry<String, String>> entries = map.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                // 获取key,根据key去resultMap中取值
                String key = entry.getKey();
                // 如果为空,则说明该key还没有添加过
                Set<String> valueSet = resultMap.get(key);
                if(valueSet == null) {
                    // 创建一个Set<String>集合,用于存入key对应的value
                    valueSet = new HashSet<>();
                }
                // 如果不为空,则说明还key已经添加过,直接将对应的value存入valueSet中即可
                valueSet.add(entry.getValue());
                // 将此key和value的Set集合存入(更新)resultMap中
                resultMap.put(key, valueSet);
            }
        }
        return resultMap;
    }*/
}
