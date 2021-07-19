package com.rita.modules.mall.product.feign;

import com.rita.common.to.SkuReductionTo;
import com.rita.common.to.SpuBoundTo;
import com.rita.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: Rita
 * @Date:7/10/2021 10:39 AM
 */
//名字要跟注册中心的一致
@FeignClient("mall-coupon")
public interface CouponFeignService {
    /**
    * 如果一个远程服务器mall-product调用了服务器mall-coupon的CouponService的saveSpuBounds，还传入了一个对象（注意是个对象）
     * springclud将会 1. FeignClient接口将这个对象转为json用@RequestBody
     *               2. 在注册中心找到mall-coupon，向映射路径/coupon/spubounds/save发送请求，
     *               因为用了RequestBody（方法签名一定要跟服务器一致）
     *               哪个服务器调用这个方法，这个方法就会将调用后的结果spuBoundTo转为json，作为请求体发送到被调用的远程服务mall-coupon的对应路径
     *               3.对方服务器mall-coupon接收到的请求，其实是mall-product的FeignClient接口返回的json数据
     *               mall-coupon 中的(@RequestBody SpuBoundsEntity spuBoundsEntity)的作用就是把接收到的json数据，转换为需要的格式
     *               这里就需要发送过来的key和value跟java bean中的一致
     * 总结：只要json数据模型是兼容的，双方服务不需要使用同一个to
     *
    * */

    //HardCodedTarget(type=CouponFeignService, name=mall-coupon, url=http://mall-coupon)
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);


    @PostMapping("/coupon/skufullreduction/saveinfo")
    //@RequestBody以json传递过去
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
