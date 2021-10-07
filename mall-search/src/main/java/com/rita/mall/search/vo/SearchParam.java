package com.rita.mall.search.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchParam {

    /**
     * 页面传递过来的全文匹配关键字
     */
    private String keyword;

    /**
     * 三级分类id
     */
    private Long catalog3Id;

    /**
     * 排序条件：sort=price/salecount/hotscore_desc/asc
     */
    private String sort;

    /**
     * 好多的过滤条件
     * hasStock(是否有货)、skuPrice区间、brandId、catalog3Id、attrs
     * hasStock=0/1
     * skuPrice=1_500
     */
    private Integer hasStock;//是否只显示有货

    private String skuPrice;//价格区间查询

    private List<Long> brandId;//按照品牌进行查询，可以多选

    private List<String> attrs;//按照属性进行筛选

    private Integer pageNum = 1;//页码

    /**
     * 原生的所有查询条件
     */
    private String _queryString;
}
