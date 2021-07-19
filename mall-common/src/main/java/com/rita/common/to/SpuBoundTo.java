package com.rita.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: Rita
 * @Date:7/10/2021 11:22 AM
 *
 * 为了两台服务器传输数据
 */
@Data
public class SpuBoundTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
