package com.rita.modules.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rita.common.utils.PageUtils;
import com.rita.modules.mall.product.entity.SkuInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author Rita
 * @email rita2021.zhang@gmail.com
 * @date 2021-06-13 12:09:24
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    List<SkuInfoEntity> getSkusBySpuId(Long spuId);
}

