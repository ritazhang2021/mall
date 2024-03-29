package com.rita.mall.ware.controller;

import com.rita.common.utils.PageUtils;
import com.rita.common.utils.R;
import com.rita.mall.ware.entity.WareSkuEntity;
import com.rita.mall.ware.service.WareSkuService;
import com.rita.mall.ware.vo.SkuHasStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 商品库存
 *
 * @author Rita
 * @email rita2021.zhang@gmail.com
 * @date 2021-06-13 15:22:25
 * •	仓库管理中有新增，可以直接新增，也可以从采购下单，再自动入库，卖出产品自动出库
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }
    //查询sku是否有库存
    @PostMapping("/hasstock")
    /*public R getSkuHasStocks(@RequestBody List<Long> skuIds) {
        List<SkuHasStockVo> vos = wareSkuService.getSkuHasStocks(skuIds);
        return R.ok().put("data", vos);
    }*/
    public  R getSkuHasStocks(@RequestBody List<Long> skuIds) {
        List<SkuHasStockVo> vos = wareSkuService.getSkuHasStocks(skuIds);
       /* R<List<SkuHasStockVo>> ok = R.ok();
        ok.setData(vos);*/
        System.out.println("***************"+R.ok().setData(vos));
        return R.ok().setData(vos);
    }

}
