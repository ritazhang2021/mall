package com.rita.modules.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rita.modules.mall.product.dao.BrandDao;
import com.rita.modules.mall.product.dao.CategoryDao;
import com.rita.modules.mall.product.entity.BrandEntity;
import com.rita.modules.mall.product.entity.CategoryEntity;
import com.rita.modules.mall.product.vo.BrandRespVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.rita.modules.mall.product.entity.CategoryBrandRelationEntity;
import com.rita.modules.mall.product.service.CategoryBrandRelationService;
import com.rita.common.utils.PageUtils;
import com.rita.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author Rita
 * @email rita2021.zhang@gmail.com
 * @date 2021-06-13 12:09:24
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 获取分类关联的品牌
     * /product/categorybrandrelation/brands/list
     */
    @GetMapping("/brands/list")
    public R relationBrandslist(@RequestParam(value = "catId",required = true) Long catId){
        //查到这个分类下的所有品牌，如手机下有华为，小米。。。
        //自带普通增删改查，复杂的需要自己创建方法
        List<BrandEntity> brandEntities = categoryBrandRelationService.getBrandByCatId(catId);
        List<BrandRespVo> collect = brandEntities.stream().map(brandEntity -> {
            BrandRespVo brandRespVo = new BrandRespVo();
            //BeanUtils.copyProperties(brandEntity, brandRespVo);
            brandRespVo.setBrandId(brandEntity.getBrandId());
            brandRespVo.setBrandName(brandEntity.getName());
            return brandRespVo;
        }).collect(Collectors.toList());
        return R.ok().put("data", collect);

    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获取当前品牌关联的所有分类列表
     */
    @GetMapping("/catelog/list")
    //@RequiresPermissions("product:categorybrandrelation:list")
    public R catelogList(@RequestParam("brandId") Long brandId){
        //根据前端的要求返回的字段，找到需要找到的数据库，找到它的service，建方法，返回能代表数据库中表的实体类
        //.list()是查询一个集合，在（）里传入查询条件
        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.list(
                //新建并构建查询条件
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));
        return R.ok().put("data", data);
    }
    /**
     *新增品牌与分类关联关系
     *
     * */


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
        //前端对应的这个save只会传过来品牌的id和分类的id，其它的字段会是Null
        //这样就需要后端处理一下
        //中间表中除了id还包括名字，如果没有这些字段，每次查询都要去各自的数据库查，影响数据库性能，把以中间的表尽量多存一些需要的信息
        categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
