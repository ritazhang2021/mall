package com.rita.modules.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rita.common.utils.PageUtils;
import com.rita.modules.mall.product.entity.BrandEntity;
import com.rita.modules.mall.product.entity.CategoryBrandRelationEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author Rita
 * @email rita2021.zhang@gmail.com
 * @date 2021-06-13 12:09:24
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateCategory(Long catId, String name);

    List<BrandEntity> getBrandByCatId(Long catId);
}

