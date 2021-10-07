package com.rita.modules.mall.product.app;

import com.rita.common.group.AddGroup;
import com.rita.common.group.UpdateGroup;
import com.rita.common.utils.PageUtils;
import com.rita.common.utils.R;
import com.rita.modules.mall.product.entity.BrandEntity;
import com.rita.modules.mall.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 品牌
 *
 * @author Rita
 * @date 2020-05-27 15:38:37
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
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@Validated(value = {AddGroup.class}) @RequestBody BrandEntity brand/*, BindingResult result*/){
		brandService.save(brand);
        return R.ok();
//        if (result.hasErrors()){
//            Map<String, String> map = new HashMap<>();
//            result.getFieldErrors().forEach((item)->{
//                String message = item.getDefaultMessage();
//                String field = item.getField();
//                map.put(message, field);
//            });
//            return R.error(400, "提交的数据不合法").put("data", map);
//        }else {
//            brandService.save(brand);
//            return R.ok();
//        }
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@Validated(value = UpdateGroup.class) @RequestBody BrandEntity brand){
//		brandService.updateById(brand);
        //级联更新所有数据
        brandService.updateCascade(brand);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
