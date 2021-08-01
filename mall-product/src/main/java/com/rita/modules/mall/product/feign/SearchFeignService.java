package com.rita.modules.mall.product.feign;

import com.rita.common.to.es.SkuEsModel;
import com.rita.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author: Rita
 * @Date:7/29/2021 8:10 AM
 */
@FeignClient("mall-search")
public interface SearchFeignService {
    @PostMapping("/search/save/product")
    R saveProductAsIndices(@RequestBody List<SkuEsModel> skuEsModels);
}
