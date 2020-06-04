package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.BrandMapper;
import com.qingcheng.dao.SpecMapper;
import com.qingcheng.service.goods.SkuSearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: qingchengAdmin
 * @description: 关键字查询
 * @author: 张梦雅
 * @create: 2020-04-21 17:14
 */
@Service
public class SkuSearchServiceImpl implements SkuSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //为什么用Map<String,String>而不是Map<String,Object>，因为关键字查询只有一个input查询框
    public Map search(Map<String,String> searchMap) {
        //2.封装查询请求,相当于GET /sku/
        SearchRequest searchRequest = new SearchRequest("sku");
        //设置查询的类型，相当于GET /sku/doc/
        searchRequest.types("doc");
        //相当于query
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //相当于"match":{"name":"手机"}(关键字搜索)
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", searchMap.get("keywords"));
        //装入"query": {
        //"match":{"name":"手机"}
        //} 括号中
        searchSourceBuilder.query(matchQueryBuilder);
        //装入 GET /sku/doc/_search
        //{"query": {
        //"match":{"name":"手机"}
        //}}大括号中
        searchRequest.source(searchSourceBuilder);

        Map resultMap = new HashMap();
        try {
            //3.获取查询结果
            //search相当于_search
            SearchResponse searchResponse  = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //获取查询结果，查询结果在hits里
            SearchHits searchHits = searchResponse.getHits();
            //获取hits下的total记录数
            long totalHits = searchHits.getTotalHits();
            System.out.println("记录数：" + totalHits);
            //获取hits下的hits数组
            SearchHit[] hits = searchHits.getHits();

            //列表查询
            List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
            for (SearchHit hit : hits) {
                //遍历数组，获取hits数组中的数据
                Map<String, Object> skuMap = hit.getSourceAsMap();
                resultList.add(skuMap);
            }
            resultMap.put("rows",resultList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultMap;
    }
}
