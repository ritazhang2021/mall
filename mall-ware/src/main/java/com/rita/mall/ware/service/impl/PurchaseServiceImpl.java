package com.rita.mall.ware.service.impl;

import com.rita.common.constant.WareConstant;
import com.rita.common.utils.Constant;
import com.rita.mall.ware.entity.PurchaseDetailEntity;
import com.rita.mall.ware.service.PurchaseDetailService;
import com.rita.mall.ware.service.WareSkuService;
import com.rita.mall.ware.vo.MergeVo;
import com.rita.mall.ware.vo.PurchaseDoneVo;
import com.rita.mall.ware.vo.PurchaseItemDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rita.common.utils.PageUtils;
import com.rita.common.utils.Query;

import com.rita.mall.ware.dao.PurchaseDao;
import com.rita.mall.ware.entity.PurchaseEntity;
import com.rita.mall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.soap.DetailEntry;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Autowired
    WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);
    }
    @Transactional
    @Override
    public void mergerPurchase(MergeVo mergeVo) {
        //先判断如果已经选择了已创建订单，就更新，否则就新建
        Long purchaseId = mergeVo.getPurchaseId();
        if(purchaseId == null){
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            //再加一些默认值
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        //TODO 确认采购单状态是0，1，才能被合并
        //采购需求的id
        List<Long> items = mergeVo.getItems();
        //Variable used in lambda expression should be final or effectively final
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> purchaseDetailEntityList = items.stream().map(i -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(i);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(purchaseDetailEntityList);
        //如果不是新增，更新purchaseEntity的更改时间
        //新建一个Entity，把id设置好，再将更新时间，这样只对服务器发送一次请求
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);


    }
    /**
     * 更新采购单
     *
     * */
    @Transactional
    @Override
    public void receivedPurchase(List<Long> ids) {
        //1.确定当前采购单是新建或者已分配状态，就是还没有被采购，细化只能看到自己的采购单
        List<PurchaseEntity> purchaseEntityList = ids.stream().map(id -> {
            PurchaseEntity PurchaseEntity = this.getById(id);
            return PurchaseEntity;
        }).filter(PurchaseEntity -> {
            if (PurchaseEntity.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                    PurchaseEntity.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
                //return true是想要的
                return true;
            }
            return false;
        }).map(purchaseEntity -> {
            //2.改变采购单的状态
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            purchaseEntity.setUpdateTime(new Date());
            return purchaseEntity;
        }).collect(Collectors.toList());

        //2.改变采购单的状态后批量保存
        this.updateBatchById(purchaseEntityList);

        //3.改变采购单的detail中的状态
        purchaseEntityList.forEach(purchaseEntity -> {
            List<PurchaseDetailEntity> purchaseDetailEntityList = purchaseDetailService.listDetailByPurchaseId(purchaseEntity.getId());
            List<PurchaseDetailEntity> detailEntities = purchaseDetailEntityList.stream().map(purchaseDetailEntityLoop -> {
                //只发一次请求，只更新Status
                PurchaseDetailEntity purchaseDetailEntityNew = new PurchaseDetailEntity();
                purchaseDetailEntityNew.setId(purchaseDetailEntityLoop.getId());
                purchaseDetailEntityNew.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return purchaseDetailEntityNew;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(detailEntities);
        });

    }

    @Transactional
    @Override
    public void done(PurchaseDoneVo purchaseDoneVo) {
        //TODO 采购需求的list应该根据人员，状态，分页显示
        // 防止从POSTMAN直接发请求，已完成的订单，不能再被选择


        //1.改变采购项(采购需求单)的状态
        //TODO 采购失败的数据库表需要完善，采购失败分几种原因，每种处理方式
        Boolean flag = true;
        List<PurchaseItemDoneVo> items = purchaseDoneVo.getItems();
        List<PurchaseDetailEntity> updates = new ArrayList<>();
        for (PurchaseItemDoneVo item: items){
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            //从前端拿到的items中，按状态进行区分
            if(item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()){
                flag = false;
                purchaseDetailEntity.setStatus(item.getStatus());
            }else {
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISHED.getCode());
                //3. 将成功采购的进行入库
                //需要三个参数，sku_id, 产品id, ware_id，仓库id, scock 入库多少
                PurchaseDetailEntity detailEntity = purchaseDetailService.getById(item.getItemId());

                wareSkuService.addStock(detailEntity.getSkuId(),detailEntity.getWareId(), detailEntity.getSkuNum());
            }
            purchaseDetailEntity.setId(item.getItemId());
            updates.add(purchaseDetailEntity);
        }
        purchaseDetailService.updateBatchById(updates);

        //1.改变采购单状态
        Long id = purchaseDoneVo.getId();
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag? WareConstant.PurchaseStatusEnum.FINISHED.getCode()
                : WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);

    }

}