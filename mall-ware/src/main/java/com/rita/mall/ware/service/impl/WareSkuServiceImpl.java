package com.rita.mall.ware.service.impl;

import com.rita.common.utils.R;
import com.rita.mall.ware.feign.ProductFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rita.common.utils.PageUtils;
import com.rita.common.utils.Query;

import com.rita.mall.ware.dao.WareSkuDao;
import com.rita.mall.ware.entity.WareSkuEntity;
import com.rita.mall.ware.service.WareSkuService;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    WareSkuDao wareSkuDao;
    //这是个接口
    @Autowired
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        /*发过来的参数
        * skuId:
          wareId: */
        String skuId =(String) params.get("skuId");
        if(!StringUtils.isEmpty(skuId) ){
            wrapper.eq("sku_id", skuId);
        }

        String wareId =(String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId) ){
            wrapper.eq("ware_id",wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //this.baseMapper = wareSkuDao
        //先判断，如果没有这个库存记录就新增，否则更新
        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if(wareSkuEntities.size() == 0 || wareSkuEntities == null){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            // 远程查询sku的名字
            // 因为调用远程服务器有可能因为服务器不稳定失败，如果是一个事物调用的话就会发生回滚，因为一个字段发生回滚有些浪费资源
            //我们或者用异步，或者用try catch，如果失败无需回滚
            //TODO 高级部分，使异常不回滚 99
            try {
                R info = productFeignService.info(skuId);
                //Object data = info.get("skuInfo"); R extends HashMap<String, Object>
                Map<String, Object> data = (Map<String, Object>)info.get("skuInfo");

                if(info.getCode() == 0){
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch (Exception e){
                //可以手动抛异常
                //打印log
                //打印控制台
            }


            wareSkuDao.insert(wareSkuEntity);
        }
        wareSkuDao.updateStock(skuId, wareId, skuNum);

    }

}