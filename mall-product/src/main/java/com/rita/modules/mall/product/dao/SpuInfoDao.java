package com.rita.modules.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rita.modules.mall.product.entity.SpuInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author Rita
 * @email rita2021.zhang@gmail.com
 * @date 2021-06-13 12:09:24
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    void upSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);
}
