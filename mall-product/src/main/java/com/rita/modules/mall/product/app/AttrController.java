package com.rita.modules.mall.product.app;

import com.rita.common.utils.PageUtils;
import com.rita.common.utils.R;
import com.rita.modules.mall.product.entity.ProductAttrValueEntity;
import com.rita.modules.mall.product.service.AttrService;
import com.rita.modules.mall.product.vo.AttrRespVo;
import com.rita.modules.mall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;




/**
 * 商品属性
 *
 * @author Rita
 * @date 2020-05-27 15:38:37
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    /**
     * 列表
     */
//    @RequestMapping("/list")
//    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = attrService.queryPage(params, catelogId, attrType);
//
//        return R.ok().put("page", page);
//    }
    @GetMapping("/hello")
    public String helloTest() {
        return "hello";
    }

    @GetMapping("/base/listforspu/{spuId}")
    public R listAttrsforSpu(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> productAttrValueEntities=attrService.listAttrsforSpu(spuId);
        return R.ok().put("data", productAttrValueEntities);
    }

    @PostMapping("/update/{spuId}")
    public R updateSpuAttrs(@PathVariable("spuId") Long spuId, @RequestBody List<ProductAttrValueEntity> attrValueEntities) {
        attrService.updateSpuAttrs(spuId, attrValueEntities);
        return R.ok();
    }


    @RequestMapping("/{attrType}/list/{catelogId}")
    public R infoCatelog(@RequestParam Map<String, Object> params,
                         @PathVariable("catelogId") long catelogId,
                         @PathVariable("attrType") String attrType) {
        PageUtils page = attrService.queryPage(params,catelogId,attrType);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
        AttrRespVo respVo=attrService.getAttrInfo(attrId);
        return R.ok().put("attr", respVo);
    }




    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVo attr){
//		attrService.updateById(attr);
        attrService.updateAttr(attr);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
