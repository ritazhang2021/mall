package com.rita.modules.mall.product.app;

import com.rita.common.utils.PageUtils;
import com.rita.common.utils.R;
import com.rita.modules.mall.product.entity.AttrEntity;
import com.rita.modules.mall.product.entity.AttrGroupEntity;
import com.rita.modules.mall.product.service.AttrAttrgroupRelationService;
import com.rita.modules.mall.product.service.AttrGroupService;
import com.rita.modules.mall.product.service.AttrService;
import com.rita.modules.mall.product.service.CategoryService;
import com.rita.modules.mall.product.vo.AttrGroupRelationVo;
import com.rita.modules.mall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 属性分组
 * 对应操作的是pms_attr_group
 *
 * @author Rita
 * @email rita2021.zhang@gmail.com
 * @date 2021-06-13 12:09:24
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService  categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService relationService;

    ///product/attrgroup/{catelogId}/withattr
    @GetMapping("/{catelogId}/withattr")
    //获取三级分类下的所有分组的属性
    public R getAttrGroupWithAttrsByCatelogId(@PathVariable("catelogId") Long catelogId){
        //1.查出当前分类下的所有属性分组
        //2. 查出每个属性分组的所有属性
        List<AttrGroupWithAttrsVo> attrGroupWithAttrsVos = attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        return R.ok().put("data",attrGroupWithAttrsVos);
    }


    @PostMapping("/attr/relation")
    //传过来的json可以直接被封闭成数组或List,
    public R addRelation(@RequestBody List<AttrGroupRelationVo> relationVos){
        //因为是个List, 所以建立批量保存
        relationService.saveBatchRelationVos(relationVos);

        return R.ok();
    }

    @GetMapping("/{attrgroupId}/attr/relation")
    //查询所有关联属性
    public  R attrRelation(@PathVariable("attrgroupId") Long attrgroupId){
        List<AttrEntity> attrEntities = attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data",attrEntities);
    }
    ///product/attrgroup/{attrgroupId}/noattr/relation
    @GetMapping("/{attrgroupId}/noattr/relation")
    //查询所有关联属性
    public  R attrNoRelation(@PathVariable("attrgroupId") Long attrgroupId, @RequestParam Map<String, Object> params){
        //创建一个分页查询方法，反回
        PageUtils page = attrService.getNoRelationAttr(attrgroupId, params);
        return R.ok().put("page",page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogId") Long catelogId ){
        //queryPage,默认的分页查询
        //PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params, catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.findCateLogPath(catelogId);
        attrGroup.setCatelogPath(path);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }
    /**
     * 删除关系，需要同时更新两个表，需要写事务
     * /product/attrgroup/attr/relation/delete
     *也中以发Post
     * */
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] vos){
        attrService.deleteRelation(vos);
        return R.ok();

    }

}
