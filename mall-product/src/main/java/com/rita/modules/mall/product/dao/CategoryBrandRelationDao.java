package com.rita.modules.mall.product.dao;

import com.rita.modules.mall.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌分类关联
 * 
 * @author Rita
 * @email rita2021.zhang@gmail.com
 * @date 2021-06-13 12:09:24
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {
    //自动生成sql语句
    //用@Param可以用#{}获取，如果不用@Param，在xml文件中需要用${}来获取，还要指定参数位置
    void updateCategory(@Param("catId") Long catId, @Param("name") String name);

}
