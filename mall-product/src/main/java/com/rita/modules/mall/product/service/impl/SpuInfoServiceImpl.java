package com.rita.modules.mall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rita.common.constant.ProductConstant;
import com.rita.common.to.SkuReductionTo;
import com.rita.common.to.SpuBoundTo;
import com.rita.common.to.es.SkuEsModel;
import com.rita.common.to.es.SkuHasStockVo;
import com.rita.common.utils.PageUtils;
import com.rita.common.utils.Query;
import com.rita.common.utils.R;
import com.rita.modules.mall.product.dao.SpuInfoDao;
import com.rita.modules.mall.product.entity.*;
import com.rita.modules.mall.product.feign.CouponFeignService;
import com.rita.modules.mall.product.feign.SearchFeignService;
import com.rita.modules.mall.product.feign.WareFeignService;
import com.rita.modules.mall.product.service.*;
import com.rita.modules.mall.product.vo.spu_save_vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 跨服务器需要,远程调用
 * 1.保证远程服务器必须在服务中心上线，比如product找coupon，这两台服务器必须上线
 * 2.必须都开始注册发现功能
 * 3.声明一个接口类，做配置，@FeignClient（指定要远程连接的服务器名），发送那台服务器上controller中的映射路径和映射方法
 * 4.在当前服务开启远程调用功能@EnableFeignClient
 *
 * */

@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SpuImagesService imagesService;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    AttrService attrService;
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    //远程调用
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    WareFeignService wareFeignService;
    @Autowired
    SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }
    //TODO 还需要完善很多，各种场景的回滚，显示信息，报错等 高级部分会再讲
    //因为涉及到很多表的保存，所以加Transactional
    @Transactional
    @Override
    public void saveSpuSaveVo(SpuSaveVo spuSaveVo) {
        /*1、保存spu基本信息 pms_spu_info    mall_pms.pms_spu_info;*/
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        //比较两个表中的字段不没有不一样的地方
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseInfo(spuInfoEntity);

       /* 2、保存Spu的描述图片  mall_pms.pms_spu_info_desc  */
        List<String> decriptList = spuSaveVo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        //分布式下的自增不用这样写,在service里保存时可能会更新这个Id
        //这里需要把entity的自增改为input，否则报错
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",",decriptList ));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);

        /*3、保存spu的图片集  mall_pms.pms_spu_images; */
        //因为图片很多，所以是个批量保存
        List<String> imagesList = spuSaveVo.getImages();
        //或者直接调用savebatch
        imagesService.saveImages(imagesList, spuInfoEntity.getId());


        /*4、保存spu的规格参数;pms_product_attr_value， mall_pms.pms_product_attr_value;*/
        // 这张表跟ProductAttrValueEntity的值不太对应，需要用map转成自己需要的
        List<BaseAttrs> baseAttrsList = spuSaveVo.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrValueEntityList = baseAttrsList.stream().map(
                baseAttr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(baseAttr.getAttrId());
            AttrEntity attrEntity = attrService.getById(baseAttr.getAttrId());
            productAttrValueEntity.setAttrName(attrEntity.getAttrName());
            productAttrValueEntity.setAttrValue(baseAttr.getAttrValues());
            productAttrValueEntity.setQuickShow(baseAttr.getShowDesc());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        /*productAttrValueService.saveBatch(productAttrValueEntityList);*/
        productAttrValueService.saveProductAttrValue(productAttrValueEntityList);


        /*5、保存spu的积分信息；gulimall_sms->sms_spu_bounds（需要跨服务器）*/
        Bounds bounds = spuSaveVo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if(r.getCode() != 0){
            log.error("远程保存spu积分信息失败");
        }

        /*6、保存当前spu对应的所有sku信息
         private List<Skus> skus; 这里又需要分表保存
            a. mall_pms.pms_sku_info;基本信息
            b. mall_pms.pms_sku_images;图片信息
           c. mall_pms.pms_sku_sale_attr_value;销售属性
           d. mall_sms.sms_spu_bounds;优惠信息，这些表需要跨服务器在coupon服务器
             d.1  mall_sms.sms_sku_ladder;
            d.2  mall_sms.sms_sku_full_reduction;
            d.3  mall_sms.sms_member_price;*/


        List<Skus> skusList = spuSaveVo.getSkus();
//        因为sku的id和每个分表中的id都有关联属性，所以用for each, 否则用map
        if(skusList !=null && skusList.size() != 0){
            skusList.forEach(sku ->{
                String defaultImg = "";
                for(Images image : sku.getImages()){
                    if(image.getDefaultImg() == 1){
                        defaultImg = image.getImgUrl();
                    }
                }
                /* a. mall_pms.pms_sku_info;基本信息*/
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
//                完成a. mall_pms.pms_sku_info;基本信息
                skuInfoService.saveSkuInfo(skuInfoEntity);

                /*b. mall_pms.pms_sku_images;图片信息*/
                //完成skuInfoEntity的保存，才能拿到它的id
                Long skuId = skuInfoEntity.getSkuId();

                List<SkuImagesEntity> skuImagesEntityList = sku.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;

                }).filter(skuImagesEntity -> {
                    //返回true就是需要，返回false就是剔除
                    return !StringUtils.isEmpty(skuImagesEntity.getImgUrl());
                }).collect(Collectors.toList());
//              完成 b. mall_pms.pms_sku_images的图片信息保存

                skuImagesService.saveBatch(skuImagesEntityList);
                //TODO 图片必须在商品新增的时候SKU环节的时候选择一下，还不太明白逻辑
                //TODO 这里还应该过虑一下imgurl为空的

                /*c. mall_pms.pms_sku_sale_attr_value;销售属性*/
                List<Attr> attrList = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList = attrList.stream().map(attr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());

//                完成 c. mall_pms.pms_sku_sale_attr_value;销售属性
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntityList);

               /*  d. sku的优惠、满减等信息；这些表需要跨服务器在coupon服务器
             d.1  mall_sms.sms_sku_ladder;
            d.2  mall_sms.sms_sku_full_reduction;
            d.3  mall_sms.sms_member_price*/
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku,skuReductionTo);
                skuReductionTo.setSkuId(skuId);

                if(skuReductionTo.getFullCount() >0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1){
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if(r1.getCode() != 0){
                        log.error("远程保存sku优惠信息失败");
                    }
                }

            });

        }




    }

    @Override
    public void saveBaseInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
      /*  //逆向生成的查询只是简单查询，复杂查询需要构造查询条件
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        //如果检索没有关键字，就来按照key模糊查询
        String key = (String) params.get("key");
        //      前端请求数据含有这些,需要依次匹配条件   status: 1 key: 华为 brandId: 9 catelogId: 225
        if(!StringUtils.isEmpty(key)){
            //因为这个条件要高于以后的，所以用一个and，把这个条件包裹在一个条件中
            //这里的拼接条件是在and里面，其它都是拼接关系
            wrapper.and((w)->{
                w.eq("id",key).or().like("spu_name",key);
            });

        }


        //查询数据库不用跟数据库的类型一致，都转行string就行
        //Integer status = Integer.parseInt(params.get("status").toString());
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);
        }


        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catelog_id",catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);*/

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("id",key).or().like("spu_name",key);
            });
        }
        // status=1 and (id=1 or spu_name like xxx)
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);
        }

        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }

//        *
//         * status: 2
//         * key:
//         * brandId: 9
//         * catelogId: 225


        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void upSpuForSearch(Long spuId) {
        //知道了上架模型，128课中，就是显示一个页面需要的所有信息，要将它放到ES中
        //需要建一个TO来收集信息，因为从produect中组装好，还要发给search上架，所以就放到common中
        //实际开发中，因为涉及到权限问题，所以需要在product中search中分别放两个to
        //组装完后需要发送一个list
        List<SkuEsModel> list = new ArrayList<>();

        //1、查出当前spuId对应的所有sku信息,品牌的名字
        List<SkuInfoEntity> skuInfoEntities=skuInfoService.getSkusBySpuId(spuId);
        //TODO 4、查出当前sku的所有可以被用来检索的规格属性
        //收集所有商品的所有属性的id
        List<ProductAttrValueEntity> productAttrValueList = productAttrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        List<Long> attrIds = productAttrValueList.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        //再去属性表中，拿到所有可以被检索的id
        List<Long> searchIds=attrService.selectSearchAttrIds(attrIds);
        //使用contains方法查询元素是否存在HashSet要比ArrayList快的多。主要原因是 List 底层是通过遍历的方式去作比较，而 Set 是算key的hash值的形式与集合内元素比较
        Set<Long> ids = new HashSet<>(searchIds);
        List<SkuEsModel.Attr> searchAttrs = productAttrValueList.stream().filter(entity -> {
            return ids.contains(entity.getAttrId());
        }).map(entity -> {
            SkuEsModel.Attr attr = new SkuEsModel.Attr();
            BeanUtils.copyProperties(entity, attr);
            return attr;
        }).collect(Collectors.toList());

        //TODO 1、发送远程调用，库存系统查询是否有库存， 放在循环里会请求8次，所以放在这里，请求一次
        //先在库存controller中填加远程调用
        //sku的id，和它有没有库存，组成的 map
        Map<Long, Boolean> stockMap = null;
        //因为是远程调用，网络会不稳定
        try {
            List<Long> skuIdList = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
            //经过改造R的getdata方法，不用处理json换对象了
            R r = wareFeignService.getSkuHasStocks(skuIdList);
            //被保护类的写法
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() { };
            stockMap = r.getData(typeReference).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
        }catch (Exception e){
            log.error("远程调用库存服务失败,原因{}",e);
        }

        //2、封装每个sku的信息
        //Map<Long, Boolean> finalStockMap = stockMap;
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> skuEsModels = skuInfoEntities.stream().map(sku -> {
            //组装需要的数据
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, skuEsModel);
            //对比不一样的数据，再手动填加
            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());
            //这里还应该考虑网络异常的处理
            if(finalStockMap == null){
                skuEsModel.setHasStock(true);
            }else{
                skuEsModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }
            //TODO 2、热度评分。0
            skuEsModel.setHotScore(0L);
            //TODO 3、查询品牌和分类的名字信息
            BrandEntity brandEntity = brandService.getById(sku.getBrandId());
            skuEsModel.setBrandName(brandEntity.getName());
            skuEsModel.setBrandImg(brandEntity.getLogo());
            CategoryEntity categoryEntity = categoryService.getById(sku.getCatalogId());
            skuEsModel.setCatalogName(categoryEntity.getName());
            //设置可搜索属性 在todo 4中拿到
            skuEsModel.setAttrs(searchAttrs);
            //设置是否有库存 在todo 1中
            skuEsModel.setHasStock(finalStockMap==null?false:finalStockMap.get(sku.getSkuId()));
            return skuEsModel;
        }).collect(Collectors.toList());

        //TODO 5、将数据发给es进行保存：mall-search
        R r = searchFeignService.saveProductAsIndices(skuEsModels);
        if (r.getCode()==0){
            //远程调用成功，上架成功
            //TODO 6、 修改当前spu状态
            this.baseMapper.upSpuStatus(spuId, ProductConstant.StatusEnum.New_UP.getCode());

        }else {
            log.error("商品远程es保存失败");
            //TODO 7、重复调用问题，接口幂等性，重试机制等
            //feign的调用流程
            /*
            * 1. 构造请求数据，将对象转为json
            * RequestTemplate
            *2. 发送请洁度，进行执行
            *excuteAndDEcode
            * 3. 执行请求会有重试机制
            * while(true){
            *   try{
            *       excuteAndDEcode
            *   }catch
                * {
                *  try retryer.
                *   catch throw ex;
                *       continue
                * }
            * }
            * */
        }

    }


}