package com.rita.mall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * @Author: Rita
 * @Date:7/12/2021 9:40 AM
 */
@Data
public class PurchaseItemDoneVo {
    //itemId:1,status:4,reason:""
    @NotNull
    private Long itemId;
    private Integer status;
    private String reason;
}
