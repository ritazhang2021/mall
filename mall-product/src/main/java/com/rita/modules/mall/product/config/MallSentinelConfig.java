package com.rita.modules.mall.product.config;
import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.rita.common.exception.BizCodeEnum;
import com.rita.common.utils.R;
import org.springframework.context.annotation.Bean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@Configuration
public class MallSentinelConfig{
    @Bean
    public UrlBlockHandler urlBlockHandler() {
        return new UrlBlockHandler() {
            @Override
            public void blocked(HttpServletRequest request, HttpServletResponse response, BlockException ex) throws IOException {
                R r = R.error(BizCodeEnum.TO_MANY_REQUEST.getCode(), BizCodeEnum.TO_MANY_REQUEST.getMsg());
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write(JSON.toJSONString(r));
            }
        };
    }
}
