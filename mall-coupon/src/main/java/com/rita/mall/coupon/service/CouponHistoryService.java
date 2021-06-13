package com.rita.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rita.common.utils.PageUtils;
import com.rita.mall.coupon.entity.CouponHistoryEntity;

import java.util.Map;

/**
 * 优惠券领取历史记录
 *
 * @author Rita
 * @email rita2021.zhang@gmail.com
 * @date 2021-06-13 14:49:37
 */
public interface CouponHistoryService extends IService<CouponHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

