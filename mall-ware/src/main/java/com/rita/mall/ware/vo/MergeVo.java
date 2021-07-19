package com.rita.mall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: Rita
 * @Date:7/11/2021 8:59 PM
 */
@Data
public class MergeVo {
    //必须写包装类型Long,因为有可能不提交，不提交就是空值，不在页面显示，如果有long会有默认值
    private Long purchaseId;
    private List<Long> items;
}
