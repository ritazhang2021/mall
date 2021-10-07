package com.rita.mall.order.vo;


import com.rita.mall.order.entity.OmsOrderEntity;
import lombok.Data;

@Data
public class SubmitOrderResponseVo {

    private OmsOrderEntity order;

    /** 错误状态码 **/
    private Integer code;
}
