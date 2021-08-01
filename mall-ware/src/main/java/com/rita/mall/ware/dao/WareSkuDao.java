package com.rita.mall.ware.dao;

import com.rita.mall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 * 
 * @author Rita
 * @email rita2021.zhang@gmail.com
 * @date 2021-06-13 15:22:25
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
    //多个参数，一定要用@Param，右键能自动生成
    void updateStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);
    //只有一个参数可以不用@Param,多个必须用
    Long getSkuStock(Long skuId);
}
