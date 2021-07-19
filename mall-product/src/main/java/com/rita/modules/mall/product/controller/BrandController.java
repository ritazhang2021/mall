package com.rita.modules.mall.product.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


import com.rita.common.valid.AddGroup;
import com.rita.common.valid.UpdateGroup;
import com.rita.common.valid.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rita.modules.mall.product.entity.BrandEntity;
import com.rita.modules.mall.product.service.BrandService;
import com.rita.common.utils.PageUtils;
import com.rita.common.utils.R;

import javax.validation.Valid;


/**
 * 品牌
 *
 * @author Rita
 * @email rita2021.zhang@gmail.com
 * @date 2021-06-13 12:09:24
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     * validation 测试及返回结果
     * {"name":""}
     * {
     *     "msg": "提交的数据不合法",
     *     "code": 400,
     *     "data": {
     *         "name": "must not be empty"
     *     }
     * }
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:brand:save")
    public R save(@RequestBody /*@Valid*/@Validated({AddGroup.class}) BrandEntity brand/*, BindingResult result*/){
        //BindingResult必须紧跟着校验的对象
//        if(result.hasErrors()){
//            Map<String,String> map = new HashMap<>();
//            //获取校验结果
//            result.getFieldErrors().forEach(item ->{
//                //获取校验提示
//                String message = item.getDefaultMessage();
//                //获取错误的属性的名字
//                String field = item.getField();
//                map.put(field, message);
//            });
//            return R.error(400, "提交的数据不合法").put("data",map);
//
//        }else {
//            brandService.save(brand);
//        }
        //启用全局异常处理，程序会自动抛异常，全局的处理异常的controller会自动接收,如果没有全局的处理，错误就会出现在控制台或页面
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:brand:update")
    public R update(@RequestBody @Validated({UpdateGroup.class}) BrandEntity brand){
		brandService.updateDetail(brand);

        return R.ok();
    }

    /**
     * 修改状态
     */
    @RequestMapping("/update/status")
    //@RequiresPermissions("product:brand:update")
    public R updateStatus(@RequestBody @Validated({UpdateStatusGroup.class}) BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
