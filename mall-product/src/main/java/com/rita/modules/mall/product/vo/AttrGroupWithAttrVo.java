package com.rita.modules.mall.product.vo;


import com.rita.modules.mall.product.entity.AttrEntity;
import com.rita.modules.mall.product.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

@Data
public class AttrGroupWithAttrVo extends AttrGroupEntity {
    private List<AttrEntity> attrs;
}
