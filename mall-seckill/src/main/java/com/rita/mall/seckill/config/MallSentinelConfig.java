package com.rita.mall.seckill.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.rita.common.exception.BizCodeEnum;
import com.rita.common.utils.R;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Component
public class MallSentinelConfig implements UrlBlockHandler{
    @Override
    public void blocked(HttpServletRequest request, HttpServletResponse response, BlockException ex) throws IOException {
        R r = R.error(BizCodeEnum.SECKILL_EXCEPTION.getCode(),BizCodeEnum.SECKILL_EXCEPTION.getMsg());
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(JSON.toJSONString(r));
    }
}
