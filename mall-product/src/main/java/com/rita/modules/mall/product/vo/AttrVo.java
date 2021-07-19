package com.rita.modules.mall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.rita.modules.mall.product.entity.AttrEntity;
import lombok.Data;

/**
 * @Author: Rita
 * @Date:7/5/2021 12:31 PM
 */
@Data
public class AttrVo {
    //copy from AttrEntity
    //或者继承
    //或者autowaire
    //或者封装一个对象

    /**
     * 属性id copy from AttrEntity
     */
    @TableId
    private Long attrId;
    /**
     * 属性名 copy from AttrEntity
     */
    private String attrName;
    /**
     * 是否需要检索[0-不需要，1-需要]
     */
    private Integer searchType;
    /**
     * 属性图标 copy from AttrEntity
     */
    private String icon;
    /**
     * 可选值列表[用逗号分隔] copy from AttrEntity
     */
    private String valueSelect;
    /**
     * 属性类型[0-销售属性，1-基本属性，2-既是销售属性又是基本属性] copy from AttrEntity
     */
    private Integer attrType;
    /**
     * 启用状态[0 - 禁用，1 - 启用] copy from AttrEntity
     */
    private Long enable;
    /**
     * 所属分类 copy from AttrEntity
     */
    private Long catelogId;
    /**
     * 快速展示【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整 copy from AttrEntity
     */
    private Integer showDesc;

    /*增加的字段*/
    private Long attrGroupId;
}
