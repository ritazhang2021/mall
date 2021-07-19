package com.rita.modules.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.rita.modules.mall.product.dao.BrandDao;
import com.rita.modules.mall.product.dao.CategoryDao;
import com.rita.modules.mall.product.entity.BrandEntity;
import com.rita.modules.mall.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rita.common.utils.PageUtils;
import com.rita.common.utils.Query;

import com.rita.modules.mall.product.dao.CategoryBrandRelationDao;
import com.rita.modules.mall.product.entity.CategoryBrandRelationEntity;
import com.rita.modules.mall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private BrandDao brandDao;
    //最好通过service调用别的业务的dao,dao都是maper自动生成的，想后来再增加一些业务逻辑就不好加了
    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        //前端对应的这个save只会传过来品牌的id和分类的id，其它的字段会是Null
        //这样就需要后端处理一下
        //中间表中除了id还包括名字，如果没有这些字段，每次查询都要去各自的数据库查，影响数据库性能，把以中间的表尽量多存一些需要的信息
        //如果从两个表拿来的数据要是发生改变，就需要在各自的service里添加方法，当字段改变的时候，也要更新中间表

        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();
        String brandName = brandDao.selectById(brandId).getName();
        String categoryName = categoryDao.selectById(catelogId).getName();
        categoryBrandRelation.setBrandName(brandName);
        categoryBrandRelation.setCatelogName(categoryName);
        //CategoryBrandRelationServiceImpl用了@Service("categoryBrandRelationService")，
        // 就是categoryBrandRelationService，它有自己的增删改查方法
        //调用自己的save方法
        this.save(categoryBrandRelation);


    }

    @Override
    public void updateCategory(Long catId, String name) {
        //调用category对应的dao，用this.baseMapper，或者用@Autowired CategoryDao
        this.baseMapper.updateCategory(catId, name);
    }

    @Override
    public List<BrandEntity> getBrandByCatId(Long catId) {
        //this.baseMapper = CategoryBrandRelationDao
        List<CategoryBrandRelationEntity> relationEntityList = this.baseMapper.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
        List<BrandEntity> brandEntityList = relationEntityList.stream().map(relationEntity -> {
            Long brandId = relationEntity.getBrandId();
            BrandEntity brandEntity = brandService.getById(brandId);
            return brandEntity;
        }).collect(Collectors.toList());

        return brandEntityList;
    }

    public void updateBrand(Long brandId, String name) {
        CategoryBrandRelationEntity relationEntity = new CategoryBrandRelationEntity();
        //更新数据库，可以用entiry,也可以写sql
        relationEntity.setBrandId(brandId);
        relationEntity.setBrandName(name);
        //查询用QueryWrapper，update用UpdateWrapper
        //将数据库中品牌Id相同的都更新
        this.update(relationEntity,new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));

    }
}