package com.rita.modules.mall.product.vo;

import lombok.Data;

/**
 * @Author: Rita
 * @Date:7/5/2021 2:18 PM
 */
@Data
public class AttrRespVo extends AttrVo{
    //"catelogName": "手机/数码/手机", //所属分类名字
    //			"groupName": "主体", //所属分组名字
    private String catelogName;
    private String groupName;
    private Long[] catelogPath;

    @Override
    public String toString() {
        return "AttrRespVo{" +super.toString()+'\''+
                "catelogName='" + catelogName + '\'' +
                ", groupName='" + groupName + '\'' +
                '}';
    }
}
