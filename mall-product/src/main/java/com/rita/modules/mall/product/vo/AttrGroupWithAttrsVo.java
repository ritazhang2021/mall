package com.rita.modules.mall.product.vo;

import com.rita.modules.mall.product.entity.AttrEntity;
import com.rita.modules.mall.product.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * @Author: Rita
 * @Date:7/8/2021 11:13 PM
 */
@Data
public class AttrGroupWithAttrsVo extends AttrGroupEntity {
    private List<AttrEntity> attrs;

}
