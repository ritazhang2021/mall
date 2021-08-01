package com.rita.modules.mall.product.app;

import java.util.Arrays;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rita.modules.mall.product.entity.CategoryEntity;
import com.rita.modules.mall.product.service.CategoryService;
import com.rita.common.utils.R;



/**
 * 商品三级分类
 *
 * @author Rita
 * @email rita2021.zhang@gmail.com
 * @date 2021-06-13 12:09:24
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查出所有分类以及子分类,以树型结构组装起来
     */
    @RequestMapping("/list/tree")
    //@RequiresPermissions("product:category:list")
    public R list(){
       List<CategoryEntity> entities = categoryService.listAsTree();
        return R.ok().put("data", entities);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }
    /**
     * 批量修改
     * 为空的字段不会更新，发过来的对象有属性不想修改，就置成null
     */
    @RequestMapping("/update/sort")
    public R updateSort(@RequestBody CategoryEntity[] category){
        //前端传过来的是一个数组，每个数组中有一个json对象，用requestBody, spring mvc可以帮我们转换成CategoryEntity对象
        //传进来的字段进行更新，没传进来的字段就不会更新
        categoryService.updateBatchById(Arrays.asList(category));
        return R.ok();
    }

    /**
     * 修改
     * 为空的字段不会更新，发过来的对象有属性不想修改，就置成null
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:category:update")
    public R updateDetail(@RequestBody CategoryEntity category){
		categoryService.updateCascade(category);

        return R.ok();
    }

    /**
     * 删除
     * @RequestBody只有post请求有请求体
     * Springmvc会将请求体（一般是json）转为对应的对象
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
        //检察当前删除的菜单，是否被别的地方引用，如果被引用了，就不能删除
		//categoryService.removeByIds(Arrays.asList(catIds));
        //alt+enter创建这个方法
		categoryService.removeMenuByIds(Arrays.asList(catIds));

        return R.ok();
    }

}
