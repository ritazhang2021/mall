package com.rita.mall.ware.dao;

import com.rita.mall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author Rita
 * @email rita2021.zhang@gmail.com
 * @date 2021-06-13 15:22:25
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
