package com.rita.modules.mall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catalog2Vo {

    //父分类id
    private String catalog1Id; //一级父分类id

    private String id;

    private String name;

    private List<Catalog3Vo> catalog3List; //三级子分类
    //三级分类
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catalog3Vo{
        private String catalog2Id;
        private String id;
        private String name;
    }
}
