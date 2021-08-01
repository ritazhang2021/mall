package com.rita.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.rita.common.to.es.SkuEsModel;
import com.rita.mall.search.config.MallElasticSearchConfig;
import com.rita.mall.search.constant.EsConstant;
import com.rita.mall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Rita
 * @Date:7/29/2021 7:32 AM
 */
@Slf4j
@Service("productSaveService")
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public boolean saveProductAsIndices(List<SkuEsModel> skuEsModels) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        //构造保存请求，都放到为每一个skuEsModel建立索引，index是product，现在不用type，就按key value存
        //放到bulkRequest里，进行批量操作
        for (SkuEsModel skuEsModel : skuEsModels) {
            //index是product, id 是 skuid，这样以后再添加同样的id，就是更新操作
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            //把skuEsModel转换成JSON
            String s = JSON.toJSONString(skuEsModel);
            //数据内容
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        //批量保存sku信息到es， 返回保存结果，可以查看有哪些保存失败了
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, MallElasticSearchConfig.COMMON_OPTIONS);

        //TODO 上架出错的批处理
        boolean hasFailures = bulkResponse.hasFailures();
        if(hasFailures){
            List<String> collect = Arrays.asList(bulkResponse.getItems()).stream().map(item -> {
                return item.getId();
            }).collect(Collectors.toList());
            log.error("商品上架出错：{}",collect );

        }else {
            log.info("所有商品上架完成：{}", bulkRequest.toString());
        }
        return hasFailures;
    }
}
