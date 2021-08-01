package com.rita.modules.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rita.modules.mall.product.entity.AttrEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author Rita
 * @email rita2021.zhang@gmail.com
 * @date 2021-06-13 12:09:24
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {


    List<Long> selectSearchAttrIds(@Param("attrIds") List<Long> attrIds);
}
