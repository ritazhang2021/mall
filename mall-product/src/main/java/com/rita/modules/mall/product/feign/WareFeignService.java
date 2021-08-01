package com.rita.modules.mall.product.feign;

import com.rita.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author: Rita
 * @Date:7/29/2021 12:21 AM
 */
@FeignClient("mall-ware")
public interface WareFeignService {
    //查询sku是否有库存
    /**
    * 返回的R型数据，在远程调用到PRODUCT后还需要将json转为object，解决方法
     * 1. 可以将R改成泛型
     * 2.直接返回我们想要的结果
     * 3. 自己封装数据类型，在R中添加一个方法，132课，
    * */
    @PostMapping("/ware/waresku/hasstock")
     R getSkuHasStocks(@RequestBody List<Long> skuIds);
}
