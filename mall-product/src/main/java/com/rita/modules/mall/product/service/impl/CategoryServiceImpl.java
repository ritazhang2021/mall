package com.rita.modules.mall.product.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.common.annotations.VisibleForTesting;
import com.rita.modules.mall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rita.common.utils.PageUtils;
import com.rita.common.utils.Query;

import com.rita.modules.mall.product.dao.CategoryDao;
import com.rita.modules.mall.product.entity.CategoryEntity;
import com.rita.modules.mall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listAsTree() {
        //1.查出所有分类
        //要查哪个表，就要找逆向生成的DAO，这里是doa层中的CategoryDao
        //或者直接用ServiceImpl中的baseMapper
        List<CategoryEntity> allEntities = baseMapper.selectList(null);//没有查询条件就是查询所有
        //2.组装成父子的树形
        List<CategoryEntity> level1Menus = allEntities.stream()
                //找到所有的一级分类
                .filter(entitie -> entitie.getParentCid() == 0)
                //找到一级分类的所有子类
                .map(level1Entitie ->{
                    level1Entitie.setChildren(getChildren(level1Entitie, allEntities));
                    //方法没有返回值，所以需要return
                    return level1Entitie;
                })
                .sorted((level1Entitie1, level1Entitie2)->{
                    return (level1Entitie1.getSort()==null?0:level1Entitie1.getSort()) - (level1Entitie2.getSort()==null?0:level1Entitie2.getSort());
                })
                .collect(Collectors.toList());
        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 1.检查当前删除的菜单，是否被别的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCateLogPath(Long catelogId) {
        //收集路径信息，因为信息不确定，所以用List收集，再转换成array返回
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新所有关联的数据
     * */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        //先更新自己的表
        this.updateById(category);
        //更新级联表
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());

    }

    public List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if(byId.getParentCid() !=0){
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }


    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all){
        List<CategoryEntity> collect = all.stream()
                //先传进来的是一级菜单，从所有的中找出一级菜单的所有子菜单
                //递归找出其它菜单的子菜单
                .filter(entity -> entity.getParentCid().equals(root.getCatId()))
                .map(entity -> {
                    //将找出的子菜单放到它的上一级菜单
                    //递归的将所有子菜单放到它的上一级菜单
                    entity.setChildren(getChildren(entity, all));
                         return entity;
                })
                .sorted((entity1, entity2)->{
                    return (entity1.getSort()==null?0:entity1.getSort()) - (entity2.getSort()==null?0:entity2.getSort());
                })
                .collect(Collectors.toList());
        return collect;
    }



}