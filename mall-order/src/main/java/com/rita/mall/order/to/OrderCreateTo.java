package com.rita.mall.order.to;

import com.rita.mall.order.entity.OmsOrderEntity;
import com.rita.mall.order.entity.OmsOrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateTo {

    private OmsOrderEntity order;

    private List<OmsOrderItemEntity> orderItems;

    /** 订单计算的应付价格 **/
    private BigDecimal payPrice;

    /** 运费 **/
    private BigDecimal fare;

}
