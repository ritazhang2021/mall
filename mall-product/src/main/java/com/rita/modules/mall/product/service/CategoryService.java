package com.rita.modules.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rita.common.utils.PageUtils;
import com.rita.modules.mall.product.entity.CategoryEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author Rita
 * @email rita2021.zhang@gmail.com
 * @date 2021-06-13 12:09:24
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listAsTree();


    void removeMenuByIds(List<Long> asList);

    /**
     * 找到CateLogId的完整路径
     * [父/子/孙]
     * */
    Long[] findCateLogPath(Long catelogId);

    void updateCascade(CategoryEntity category);

}

