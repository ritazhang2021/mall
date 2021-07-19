package com.rita.mall.coupon.service.impl;

import com.rita.common.to.MemberPrice;
import com.rita.common.to.SkuReductionTo;
import com.rita.mall.coupon.entity.MemberPriceEntity;
import com.rita.mall.coupon.entity.SkuLadderEntity;
import com.rita.mall.coupon.service.MemberPriceService;
import com.rita.mall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rita.common.utils.PageUtils;
import com.rita.common.utils.Query;

import com.rita.mall.coupon.dao.SkuFullReductionDao;
import com.rita.mall.coupon.entity.SkuFullReductionEntity;
import com.rita.mall.coupon.service.SkuFullReductionService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {
    @Autowired
    SkuLadderService skuLadderService;
    @Autowired
    SkuFullReductionService skuFullReductionService;
    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSkuReducTion(SkuReductionTo skuReductionTo) {
        /*1.优惠信息（阶梯价位）mall_sms.sms_sku_ladder*/
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        //BeanUtils
        skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
        skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
        skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
        //TODO 价格需要通过一定的逻辑算出
        if(skuReductionTo.getFullCount() > 0){
            skuLadderService.save(skuLadderEntity);
        }

        /*2. 满减信息 mall_sms.sms_sku_full_reduction*/
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
        if(skuFullReductionEntity.getFullPrice().compareTo(new BigDecimal("0")) == 1){
            skuFullReductionService.save(skuFullReductionEntity);
        }


        /*3. 会员价 mall_sms.sms_member_price;*/
        List<MemberPrice> memberPriceList = skuReductionTo.getMemberPrice();
        List<MemberPriceEntity> memberPriceEntities = memberPriceList.stream().map(memberPrice -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
            memberPriceEntity.setMemberLevelId(memberPrice.getId());
            memberPriceEntity.setMemberLevelName(memberPrice.getName());
            memberPriceEntity.setMemberPrice(memberPrice.getPrice());
            //给个默认值
            //要取页面值就从调用方法的service里传过来
            memberPriceEntity.setAddOther(1);
            return memberPriceEntity;

        }).filter(memberPriceEntity -> {
            return memberPriceEntity.getMemberPrice().compareTo(new BigDecimal("0")) == 1;
        }).collect(Collectors.toList());

        memberPriceService.saveBatch(memberPriceEntities);




    }

}