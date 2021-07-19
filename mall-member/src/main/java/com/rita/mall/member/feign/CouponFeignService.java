package com.rita.mall.member.feign;

import com.rita.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: Rita
 * @Date:6/13/2021 9:20 PM
 */
@FeignClient("mall-coupon")//要调用的远程服务的注册名
//@FeignClient(value = "mall-coupon", url = "http://127.0.0.1:7000")
public interface CouponFeignService {
    //这里的意思是，如果要调用membercoupons()方法，就会先去远程中心找到"mall-coupon"，
    // 再去这个请求地址/coupon/coupon/member/list对应的方法
    @RequestMapping("/coupon/coupon/member/list")
    public R membercoupons();
}
