package com.rita.mall.search.service;

import com.rita.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @Author: Rita
 * @Date:7/29/2021 7:30 AM
 */
public interface ProductSaveService {
    boolean saveProductAsIndices(List<SkuEsModel> skuEsModels) throws IOException;
}
