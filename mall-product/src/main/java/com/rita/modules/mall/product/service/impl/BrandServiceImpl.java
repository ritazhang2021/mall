package com.rita.modules.mall.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rita.common.utils.PageUtils;
import com.rita.common.utils.Query;

import com.rita.modules.mall.product.dao.BrandDao;
import com.rita.modules.mall.product.entity.BrandEntity;
import com.rita.modules.mall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {
    @Autowired
    CategoryBrandRelationServiceImpl categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        //1.拿到key的值，是个object,就是查找的字段, 有可能是数字，也有可能是string, 强转成string
        String key = (String) params.get("key");
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(key)){
            //key不是空，就是要进行检索，就要往Wrapper里放条件
            //brand_id是数据库中的字段,查找key跟数据库中brand_id相匹配的，或跟数据库中的名字模糊匹配
            wrapper.eq("brand_id", key).or().like("name", key);
        }


        IPage<BrandEntity> page = this.page( new Query<BrandEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }
    @Transactional
    @Override
    public void updateDetail(BrandEntity brand) {
        //必须保证冗余字段的数据一致
        //先更新自己的数据库
        this.updateById(brand);
        //再更新中间库中的信息
        if(!StringUtils.isEmpty(brand.getName())){
            //同步更新其它表中的数据
            categoryBrandRelationService.updateBrand(brand.getBrandId(),brand.getName());
            //TODO 更新其它关联
        }

    }

}