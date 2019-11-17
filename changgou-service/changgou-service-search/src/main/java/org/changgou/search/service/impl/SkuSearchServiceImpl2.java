package org.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import org.changgou.goods.pojo.Sku;
import org.changgou.search.dao.SkuSearchMapper;
import org.changgou.search.pojo.SkuInfo;
import org.changgou.search.service.SkuSearchService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/18  15:00
 */
@Service
public class SkuSearchServiceImpl2 implements SkuSearchService {

    @Autowired
    private SkuSearchMapper skuSearchMapper;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    private Logger logger = LoggerFactory.getLogger(SkuSearchServiceImpl2.class);

    private static Integer PAGE_SIZE = 30;

    private static final int TOTAL_DEFAULT = 30000;

    /**
     * sku数据导入ES中
     *
     * @param data Sku数据集合
     */
    @Override
    public void importSkuData(List<Sku> data) {
        /*  将Sku数据转换成SkuInfo数据
            因为Sku和SkuInfo在字段上几乎一致,SkuInfo只是为了加上注解与ES索引对应,并声明分词形式
            极个别的如specMap字段,也按照JSON解析规则,转换成Map<String, String>的结构,可以很方便的进行单独的转换
            所有此处可以直接使用JSON解析工具进行两个类型间的转换
         */
        List<SkuInfo> skuInfos = JSON.parseArray(JSON.toJSONString(data), SkuInfo.class);
        for (SkuInfo skuInfo : skuInfos) {
            Map map = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(map);
        }
        // 进行数据保存
        skuSearchMapper.saveAll(skuInfos);
    }

    /**
     * 根据关键字进行商品搜索,提供品牌,分类,规格,排序,分页,高亮等完整搜索功能
     *
     * @param searchMap 包含搜索条件的map
     * @return 符合所有条件的结果集Map
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        // 获取查询构建器
        NativeSearchQueryBuilder builder = buildBasicQuery(searchMap);
        // 根据查询构建器,进行关键字搜索,并进行高亮处理
        Map<String, Object> map = searchByKeyword(builder);
        Map<String, Object> resultMap = new HashMap<>(map);
        long total = (long) map.get("total");
        // 根据查询构建器,进行商品分类搜索
        List<String> categoryList = searchGroup(builder, "categoryName", total);
        resultMap.put("categoryList", categoryList);

        // 根据查询构建器,进行商品品牌搜索
        if(StringUtils.isEmpty(searchMap.get("brand"))) {
            List<String> brandList = searchGroup(builder, "brandName", total);
            resultMap.put("brandList", brandList);
        }
        // 根据查询构建器,进行商品规格搜索
        List<String> specList = searchGroup(builder, "spec.keyword", total);
        Map<String, Set<String>> specMap = parseSpecResult(specList);
        resultMap.put("specMap", specMap);
        return resultMap;
    }

    /**
     * 根据查询条件进行基本的查询构建器的构建
     *
     * @param searchMap 包含搜索条件的map
     * @return 经过层层条件附加后的查询构建器
     */
    private NativeSearchQueryBuilder buildBasicQuery(Map<String, String> searchMap) {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 关键字查询
        String keywords = searchMap.get("keywords");
        if(!StringUtils.isEmpty(keywords)) {
            builder.withQuery(QueryBuilders.matchQuery("name", keywords));
        }
        // 分类筛选
        String category = searchMap.get("category");
        if(!StringUtils.isEmpty(category)) {
            boolQuery.must(QueryBuilders.matchQuery("categoryName", category));
        }
        // 品牌筛选
        String brand = searchMap.get("brand");
        if(!StringUtils.isEmpty(brand)) {
            boolQuery.must(QueryBuilders.matchQuery("brandName", brand));
        }
        // 规格筛选,约定规格参数已spec_开头,因为可能有多个规格参数,所以需要遍历查询条件的key集合
        for (String key : searchMap.keySet()) {
            if(key.startsWith("spec_")) {
                // 处理传递过来的参数中的转义字符
                String value = searchMap.get(key).replace("\\", "");
                boolQuery.must(QueryBuilders.matchQuery("specMap." + key.substring(5) + ".keyword", value));
            }
        }
        // 价格区间筛选
        String price = searchMap.get("price");
        if(!StringUtils.isEmpty(price)) {
            String[] split = price.split("-");
            if(split.length > 1) {
                boolQuery.must(QueryBuilders.rangeQuery("price").lte(split[1]));
            }
            boolQuery.must(QueryBuilders.rangeQuery("price").gte(split[0]));
        }
        // 排序 排序字段
        String sortField = searchMap.get("sortField");
        String sortRules = searchMap.get("sortRules");
        if(!StringUtils.isEmpty(sortField)) {
            sortRules = sortRules != null ? sortRules : "DESC";
            FieldSortBuilder sortBuilder = SortBuilders.fieldSort(sortField).order(SortOrder.fromString(sortRules));
            builder.withSort(sortBuilder);
        }
        // 分页
        Integer pageNum = parsePageNum(searchMap.get("pageNum"));
        Integer pageSize = parsePageSize(searchMap.get("pageSize"));
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        builder.withPageable(pageRequest);
        // 添加boolean查询
        builder.withFilter(boolQuery);
        return builder;
    }

    /**
     * 解析页面size字符串至数字,异常则返回默认值
     *
     * @param pageSize 解析后数字或默认值
     */
    private Integer parsePageSize(String pageSize) {
        try {
            return Integer.parseInt(pageSize);
        } catch (Exception e) {
            logger.info("返回默认分页参数");
            return PAGE_SIZE;
        }
    }

    /**
     * 解析页码字符串至数字,异常则返回默认值
     *
     * @param pageNum 解析后数字或默认值
     */
    private Integer parsePageNum(String pageNum) {
        try {
            return Integer.parseInt(pageNum) - 1;
        } catch (Exception e) {
            logger.info("返回默认分页参数");
            return 0;
        }
    }

    /**
     * 基本的关键字查询,具备高亮功能
     *
     * @param builder 查询构建器
     * @return 包含查询结果集合, 元素总数和页数的Map
     */
    private Map<String, Object> searchByKeyword(NativeSearchQueryBuilder builder) {
        Map<String, Object> map = new HashMap<>();
        // 声明高亮的字段和样式
        HighlightBuilder.Field field = new HighlightBuilder.Field("name")
                .preTags("<font color='red'>")
                .postTags("</font>")
                .fragmentSize(101); // 高亮效果在指定字段上生效的有效长度
        // 添加高亮条件
        builder.withHighlightFields(field);
        // 进行查询
        NativeSearchQuery build = builder.build();
        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(build, SkuInfo.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                // 定义一个集合用来保存高亮处理后的数据
                List<T> list = new ArrayList<>();
                long totalHits = response.getHits().getTotalHits();
                for (SearchHit hit : response.getHits()) {
                    // 处理原始数据
                    SkuInfo skuInfo = JSON.parseObject(hit.getSourceAsString(), SkuInfo.class);
                    // 处理高亮数据
                    HighlightField highlightField = hit.getHighlightFields().get("name");
                    // 非空判断,处理没有搜素字的情况
                    if(highlightField != null) {
                        Text[] names = highlightField.getFragments();
                        if(names != null && names.length > 0) {
                            StringBuilder sb = new StringBuilder();
                            // 数据替换
                            for (Text name : names) {
                                sb.append(name.toString());
                            }
                            skuInfo.setName(sb.toString());
                        }
                    }
                    list.add((T) skuInfo);
                }
                return new AggregatedPageImpl<>(list, pageable, totalHits);
            }
        });
        map.put("rowData", skuInfos.getContent());
        map.put("total",skuInfos.getTotalElements());
        Pageable pageable = build.getPageable();
        map.put("pageNum",pageable.getPageNumber());
        map.put("pageSize",pageable.getPageSize());
        return map;
    }

    /**
     * ES聚合搜索
     * 一般化的分组搜索方法,查询符合关键字搜索的商品分组信息
     *
     * @param builder 查询构建器
     * @param size    关键字搜索返回数据的总条数
     * @return 商品分组列表数据
     */
    private List<String> searchGroup(NativeSearchQueryBuilder builder, String fieldName, long size) {
        List<String> list = new ArrayList<>();
        // 根据指定字段构建聚合条件,类似MySql中的GROUP BY
        TermsAggregationBuilder terms;
        if(size != 0L) {
            terms = AggregationBuilders.terms(fieldName).field(fieldName).size(Math.toIntExact(size));
        } else {
            terms = AggregationBuilders.terms(fieldName).field(fieldName).size(TOTAL_DEFAULT);
        }
        // 将聚合条件添加至查询构建器
        builder.addAggregation(terms);
        // 进行聚合查询
        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);
        // 处理结果集
        Aggregations aggregations = skuInfos.getAggregations();
        // 根据别名category得到指定的聚合查询结果
        StringTerms categoryList = aggregations.get(fieldName);
        List<StringTerms.Bucket> buckets = categoryList.getBuckets();
        // 封装结果集
        for (StringTerms.Bucket bucket : buckets) {
            list.add(bucket.getKeyAsString());
        }
        return list;
    }

    /**
     * 解析规格查询的结果集,进行去重,并已适当的数据结构返回
     *
     * @param list JSON字符串形式的商品规格数据
     * @return 解析, 去重, 转换后的规格数据
     */
    private Map<String, Set<String>> parseSpecResult(List<String> list) {
        Map<String, Set<String>> specMap = new HashMap<>();
        // 循环遍历原始规格数据集合
        for (String s : list) {
            // 将JSON串转换为Map<String,String>结构的数据
            Map<String, String> map = JSON.parseObject(s, Map.class);
            // 遍历该map的key集合
            for (String key : map.keySet()) {
                // 根据该key去返回结果的specMap中取值
                Set<String> valueSet = specMap.get(key);
                // 如果为空则说明此key还未添加进返回结果中,则创建一个Set<String>用于存入该key对应的value
                if(valueSet == null) {
                    valueSet = new HashSet<>();
                }
                // 如果不为空,则一定是一个Set<String>集合,直接进行添加
                valueSet.add(map.get(key));
                // 并将此键值对,存入或更新返回结果的Map中
                specMap.put(key, valueSet);
            }
        }
        return specMap;
    }
}
