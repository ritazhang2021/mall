package com.rita.modules.mall.product.feign.fallback;


import com.rita.common.exception.BizCodeEnum;
import com.rita.common.utils.R;
import com.rita.modules.mall.product.feign.SeckillFeignService;
import org.springframework.stereotype.Component;

@Component
public class SeckillFallbackService implements SeckillFeignService {
    @Override
    public R getSeckillSkuInfo(Long skuId) {
        return R.error(BizCodeEnum.READ_TIME_OUT_EXCEPTION.getCode(), BizCodeEnum.READ_TIME_OUT_EXCEPTION.getMsg());
    }
}
