package com.rita.common.to.es;

import lombok.Data;

/**
 * @Author: Rita
 * @Date:7/28/2021 11:58 PM
 */
@Data
public class SkuHasStockVo {
    private Long skuId;
    private Boolean hasStock;
}
