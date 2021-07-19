package com.rita.modules.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.rita.modules.mall.product.entity.ProductAttrValueEntity;
import com.rita.modules.mall.product.service.ProductAttrValueService;
import com.rita.modules.mall.product.vo.AttrGroupRelationVo;
import com.rita.modules.mall.product.vo.AttrRespVo;
import com.rita.modules.mall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.rita.modules.mall.product.entity.AttrEntity;
import com.rita.modules.mall.product.service.AttrService;
import com.rita.common.utils.PageUtils;
import com.rita.common.utils.R;



/**
 * 商品属性
 *
 * @author Rita
 * @email rita2021.zhang@gmail.com
 * @date 2021-06-13 12:09:24
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;

    ///product/attr/sale/list/{catelogId}


    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String,Object> params,
                          @PathVariable("catelogId") Long catelogId,
                          @PathVariable("attrType") String attrType){
        if(attrType.equals("1")){

        }
        System.out.println("catelogId"+catelogId);
        //分页查询，最终返回PageUtils page做展示
        //返回的page中，用vo中专门用来作为返回的对象
        PageUtils page = attrService.queryBaseAttrPage(params, catelogId, attrType);
        return R.ok().put("page",page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     * 回显，包括完整路径
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
		AttrRespVo respVo = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", respVo);
    }

    /**
     * 保存
     * 加入事务
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attrVo){
        //逆向生成默认的方法，只能保存AttrEntity，不能同时更新关联表，所以需要重写
        //可以在AttrEntity中封闭中间表的id，并用@TableField（exist=false）排除在本表中
        //或者再建一个分层，建立一个新的class，收集原有的entity信息，和增加的字段
		//attrService.save(attr);

        attrService.saveAttrVo(attrVo);


        return R.ok();
    }

    /**
     * 修改
     * 加入事务
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateAttrVo(attr);

        return R.ok();
    }

    @PostMapping("/update/{spuId}")
    //@RequiresPermissions("product:attr:update")
    //前端传过来json数据，后端用@RequestBody 接收，前端是数组，后端是List
    public R updateBySpuId(@PathVariable("spuId") Long spuId, @RequestBody List<ProductAttrValueEntity> list){
        productAttrValueService.updateSpuAttr(spuId, list);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

    ///product/attr/base/listforspu/{spuId}

    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrListForSpu(@PathVariable ("spuId") Long spuId){
        List<ProductAttrValueEntity> List = productAttrValueService.baseAttrListForSpu(spuId);
        return R.ok().put("data", List);
    }


}
