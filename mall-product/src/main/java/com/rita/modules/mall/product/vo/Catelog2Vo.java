package com.rita.modules.mall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Rita
 * @Date:7/31/2021 8:53 AM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catelog2Vo {
    //二级分类
    private String catalog1Id;

    private String id;

    private String name;

    private List<Catalog3Vo> catalog3List;


    /**
    * 静态内部类
     * 三级分类vo
     * private的内部类，除了Catalog2Vo的作用域内，外面的都不能访问，如果public就可以通过Catelog2Vo的实例来访问
     * 如果static，可以不通过Catelog2Vo的实例来访问
     *catelog2Vo.new Catalog3Vo();
     *new Catelog2Vo.Catalog3Vo();
    * */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catalog3Vo{
        private String catalog2Id;
        private String id;
        private String name;

    }


}
