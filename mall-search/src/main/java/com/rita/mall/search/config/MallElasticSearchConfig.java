package com.rita.mall.search.config;



import org.apache.http.HttpHost;
import org.elasticsearch.client.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Rita
 * @Date:7/15/2021 5:56 PM
 */
@Configuration
public class MallElasticSearchConfig {
    /**
     * 1.导入依赖，修改sprintboot默认的依赖版本
     * 2.编写配置，给容器中注入一个RestHighLevelClient
     * 3.参照官方api进行操作就行
     *
     * */
    @Bean
    public RestHighLevelClient esRestClient(){
        //方式一：
        RestClientBuilder builder = null;
        //String hostname, int port, String scheme
        //docker中容器的映射
        builder = RestClient.builder(new HttpHost("192.168.56.10", 9200, "http"));
        RestHighLevelClient client = new RestHighLevelClient(builder);

        //方式二：
       /* RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.56.10", 9200, "http")));*/
        return client;
    }

    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        //设置请求的安全信息
//        builder.addHeader("Authorization", "Bearer " + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory
//                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }
}
