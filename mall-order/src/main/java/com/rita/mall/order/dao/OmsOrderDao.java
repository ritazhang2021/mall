package com.rita.mall.order.dao;

import com.rita.mall.order.entity.OmsOrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author Rita
 * @email rita2021.zhang@gmail.com
 * @date 2021-06-13 15:13:32
 */
@Mapper
public interface OmsOrderDao extends BaseMapper<OmsOrderEntity> {
	
}
