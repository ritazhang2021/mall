package com.rita.mall.ware.feign;

import com.rita.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: Rita
 * @Date:7/12/2021 5:34 PM
 */
//远程服务在注册中心的名字
@FeignClient("mall-product")
//@FeignClient("mall-gateway")不走路由了，查询直接访问服务器，可以在服务器上做相应配置，提高访问速度
public interface ProductFeignService {

    /**
     * 这种写法是把请求直接发到mall-product
    * "/product/spuinfo/info/{id}"
     * 这种写法是通过路由转发给mall-product
     * "/api/product/spuinfo/info/{id}"
    * */

    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
