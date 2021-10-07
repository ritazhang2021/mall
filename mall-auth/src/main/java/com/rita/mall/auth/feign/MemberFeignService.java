package com.rita.mall.auth.feign;

import com.rita.common.utils.R;
import com.rita.mall.auth.feign.fallback.MemberFallbackService;
import com.rita.mall.auth.vo.SocialUser;
import com.rita.mall.auth.vo.UserLoginVo;
import com.rita.mall.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "mall-member",fallback = MemberFallbackService.class)
public interface MemberFeignService {

    @RequestMapping("member/member/register")
    R register(@RequestBody UserRegisterVo registerVo);


    @RequestMapping("member/member/login")
     R login(@RequestBody UserLoginVo loginVo);

    @RequestMapping("member/member/oauth2/login")
    R login(@RequestBody SocialUser socialUser);
}
