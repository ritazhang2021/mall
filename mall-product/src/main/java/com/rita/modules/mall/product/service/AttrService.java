package com.rita.modules.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rita.common.utils.PageUtils;
import com.rita.modules.mall.product.entity.AttrEntity;
import com.rita.modules.mall.product.vo.AttrGroupRelationVo;
import com.rita.modules.mall.product.vo.AttrRespVo;
import com.rita.modules.mall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author Rita
 * @email rita2021.zhang@gmail.com
 * @date 2021-06-13 12:09:24
 */
public interface AttrService extends IService<AttrEntity> {

     void deleteRelation(AttrGroupRelationVo[] vos);

    PageUtils queryPage(Map<String, Object> params);

    void saveAttrVo(AttrVo attrVo);


    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttrVo(AttrVo attr);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    PageUtils getNoRelationAttr(Long attrgroupId, Map<String, Object> params);

    List<Long> selectSearchAttrIds(List<Long> attrIds);


}

