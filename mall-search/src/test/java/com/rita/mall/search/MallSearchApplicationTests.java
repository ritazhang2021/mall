package com.rita.mall.search;

import com.alibaba.fastjson.JSON;
import com.rita.mall.search.config.MallElasticSearchConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-search.html
 *时间和空间，两者只能取其一
 * */
@RunWith(SpringRunner.class)//指定spring的驱动来跑测试
@SpringBootTest
public class MallSearchApplicationTests {
    @Autowired
    private RestHighLevelClient client;

    @Data
    @ToString
    //不可以用public，必须是静态的类  com.alibaba.fastjson.JSONException: can't create non-static inner class instance.
    static class Account {

        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }

    @Test
    public void contextLoads() {
        System.out.println("***************"+client);
    }

    /**ES中存储数据的方式*/
    @Test
    public void index() throws IOException {
        /*方式一 string*/
        IndexRequest request = new IndexRequest("posts");
        request.id("1");
        String jsonString = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        request.source(jsonString, XContentType.JSON);

        /*方式二 Map*/
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("user", "kimchy");
        jsonMap.put("postDate", new Date());
        jsonMap.put("message", "trying out Elasticsearch");
        IndexRequest indexRequest2 = new IndexRequest("posts")
                .id("1").source(jsonMap);


        /*方式三 XContentBuilder*/
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.field("user", "kimchy");
            builder.timeField("postDate", new Date());
            builder.field("message", "trying out Elasticsearch");
        }
        builder.endObject();
        IndexRequest indexRequest3 = new IndexRequest("posts")
                .id("1").source(builder);

        /*方式四 IndexRequest*/
        IndexRequest indexRequest = new IndexRequest("posts")
                .id("1")
                .source("user", "kimchy",
                        "postDate", new Date(),
                        "message", "trying out Elasticsearch");
    }

    /**测试给ES中存储数据*/
    @Test
    public void indexData() throws IOException {
        //index是users
        IndexRequest request = new IndexRequest("users");
        //id必须是string,不指定有默认
        request.id("1");
        //request.source("userName", "lanlan", "age",18);
        User user = new User();
        user.setName("娜娜");
        String jsonString = JSON.toJSONString(user);
        request.source(jsonString,XContentType.JSON);
        //执行操作
        //如果已存在，更新
        IndexResponse response = client.index(request, MallElasticSearchConfig.COMMON_OPTIONS);
        //提取响应的数据
        System.out.println(response);

    }
    @Data
    class User{
        String name;
        String gender;
        Integer age;

    }


    /**
     * GET bank/account/_search
     * {
     *   "query": {
     *     "match_all": {}
     *   },
     *   "aggs": {
     *     "age_agg": {
     *       "terms": {
     *         "field": "age",
     *         "size": 100
     *       },
     *       "aggs": {
     *         "gender_agg": {
     *           "terms": {
     *             "field": "gender.keyword",
     *             "size": 100
     *           },
     *           "aggs": {
     *             "balance_avg": {
     *               "avg": {
     *                 "field": "balance"
     *               }
     *             }
     *           }
     *         },
     *         "balance_avg": {
     *           "avg": {
     *             "field": "balance"
     *           }
     *         }
     *       }
     *     }
     *   },
     *   "size": 1000
     * }
     *
     * */
    @Test
    public void searchData() throws IOException {
        /*1.创建检索请求*/
        //SearchRequest searchRequest = new SearchRequest("posts");
        SearchRequest searchRequest = new SearchRequest();
        //指定index
        searchRequest.indices("bank");
        //指定DSL检索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构造检索条件 DSL根操作有哪些，它就有哪些
//        searchSourceBuilder.from();
//        searchSourceBuilder.aggregation();
//        searchSourceBuilder.size();
        /*a.查询一个属性中包含一些字的记录*/
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        System.out.println(searchSourceBuilder.toString());
        /*b.按照年龄聚合*/
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(ageAgg);
        System.out.println(searchSourceBuilder.toString());
        /*c.求出所有的平均薪资*/
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(balanceAvg);
        System.out.println(searchSourceBuilder.toString());

        /*d完成构建*/
        System.out.println("检索结果 ");
        searchRequest.source(searchSourceBuilder);
        /*2.执行检索*/
        //异步 client.searchAsync
        SearchResponse response = client.search(searchRequest, MallElasticSearchConfig.COMMON_OPTIONS);
        /*3.分析结果*/
        System.out.println(response);
        /*a. 把结果封装成map,需要什么就用key取*/
        //Map map = JSON.parseObject(response.toString(), Map.class);
        /*b. */
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            //可以转成map或string，转成string就是json，可以用工具转成java bean
            //searchHit.getSourceAsMap();
            String sourceAsString = searchHit.getSourceAsString();
            Account account = JSON.parseObject(sourceAsString, Account.class);
            System.out.println("account"+ account);
        }
        Aggregations aggregations = response.getAggregations();
        if(aggregations != null){
            Terms ageAgg1 = aggregations.get("ageAgg");
            for (Terms.Bucket bucket : ageAgg1.getBuckets()) {
                String keyAsString = bucket.getKeyAsString();
                System.out.println("年龄" +keyAsString + "有"+ bucket.getDocCount()+"个人");
            }
            //key要是不对就会取到Null
            Avg blanceAvg = aggregations.get("balanceAvg");
            System.out.println("balanceAvg" + blanceAvg.getValue());

        }


        /**打印结果
         * GET bank/_search
         * {
         *   "query": {
         *     "match": {
         *       "address": {
         *         "query": "mill",
         *         "operator": "OR",
         *         "prefix_length": 0,
         *         "max_expansions": 50,
         *         "fuzzy_transpositions": true,
         *         "lenient": false,
         *         "zero_terms_query": "NONE",
         *         "auto_generate_synonyms_phrase_query": true,
         *         "boost": 1
         *       }
         *     }
         *   },
         *   "aggregations": {
         *     "ageAvg": {
         *       "terms": {
         *         "field": "age",
         *         "size": 10,
         *         "min_doc_count": 1,
         *         "shard_min_doc_count": 0,
         *         "show_term_doc_count_error": false,
         *         "order": [
         *           {
         *             "_count": "desc"
         *           },
         *           {
         *             "_key": "asc"
         *           }
         *         ]
         *       }
         *     },
         *     "balanceAvg": {
         *       "avg": {
         *         "field": "balance"
         *       }
         *     }
         *   }
         * }
         * */
        /**
         * 查询结果
         *
         * {
         *   "took" : 1,
         *   "timed_out" : false,
         *   "_shards" : {
         *     "total" : 1,
         *     "successful" : 1,
         *     "skipped" : 0,
         *     "failed" : 0
         *   },
         *   "hits" : {
         *     "total" : {
         *       "value" : 4,
         *       "relation" : "eq"
         *     },
         *     "max_score" : 5.4032025,
         *     "hits" : [
         *       {
         *         "_index" : "bank",
         *         "_type" : "account",
         *         "_id" : "970",
         *         "_score" : 5.4032025,
         *         "_source" : {
         *           "account_number" : 970,
         *           "balance" : 19648,
         *           "firstname" : "Forbes",
         *           "lastname" : "Wallace",
         *           "age" : 28,
         *           "gender" : "M",
         *           "address" : "990 Mill Road",
         *           "employer" : "Pheast",
         *           "email" : "forbeswallace@pheast.com",
         *           "city" : "Lopezo",
         *           "state" : "AK"
         *         }
         *       },
         *       {
         *         "_index" : "bank",
         *         "_type" : "account",
         *         "_id" : "136",
         *         "_score" : 5.4032025,
         *         "_source" : {
         *           "account_number" : 136,
         *           "balance" : 45801,
         *           "firstname" : "Winnie",
         *           "lastname" : "Holland",
         *           "age" : 38,
         *           "gender" : "M",
         *           "address" : "198 Mill Lane",
         *           "employer" : "Neteria",
         *           "email" : "winnieholland@neteria.com",
         *           "city" : "Urie",
         *           "state" : "IL"
         *         }
         *       },
         *       {
         *         "_index" : "bank",
         *         "_type" : "account",
         *         "_id" : "345",
         *         "_score" : 5.4032025,
         *         "_source" : {
         *           "account_number" : 345,
         *           "balance" : 9812,
         *           "firstname" : "Parker",
         *           "lastname" : "Hines",
         *           "age" : 38,
         *           "gender" : "M",
         *           "address" : "715 Mill Avenue",
         *           "employer" : "Baluba",
         *           "email" : "parkerhines@baluba.com",
         *           "city" : "Blackgum",
         *           "state" : "KY"
         *         }
         *       },
         *       {
         *         "_index" : "bank",
         *         "_type" : "account",
         *         "_id" : "472",
         *         "_score" : 5.4032025,
         *         "_source" : {
         *           "account_number" : 472,
         *           "balance" : 25571,
         *           "firstname" : "Lee",
         *           "lastname" : "Long",
         *           "age" : 32,
         *           "gender" : "F",
         *           "address" : "288 Mill Street",
         *           "employer" : "Comverges",
         *           "email" : "leelong@comverges.com",
         *           "city" : "Movico",
         *           "state" : "MT"
         *         }
         *       }
         *     ]
         *   },
         *   "aggregations" : {
         *     "ageAvg" : {
         *       "doc_count_error_upper_bound" : 0,
         *       "sum_other_doc_count" : 0,
         *       "buckets" : [
         *         {
         *           "key" : 38,
         *           "doc_count" : 2
         *         },
         *         {
         *           "key" : 28,
         *           "doc_count" : 1
         *         },
         *         {
         *           "key" : 32,
         *           "doc_count" : 1
         *         }
         *       ]
         *     },
         *     "balanceAvg" : {
         *       "value" : 25208.0
         *     }
         *   }
         * }
         *
         * */



    }







}

