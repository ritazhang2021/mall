package com.rita.mall.coupon.dao;

import com.rita.mall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author Rita
 * @email rita2021.zhang@gmail.com
 * @date 2021-06-13 14:49:37
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
