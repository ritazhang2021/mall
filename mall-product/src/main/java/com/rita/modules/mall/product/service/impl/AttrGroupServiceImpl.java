package com.rita.modules.mall.product.service.impl;

import com.rita.modules.mall.product.entity.AttrEntity;
import com.rita.modules.mall.product.service.AttrService;
import com.rita.modules.mall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rita.common.utils.PageUtils;
import com.rita.common.utils.Query;

import com.rita.modules.mall.product.dao.AttrGroupDao;
import com.rita.modules.mall.product.entity.AttrGroupEntity;
import com.rita.modules.mall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {

        String key = (String) params.get("key");
        //前端有可能点击节点查询，或是搜索查询，搜索有可能按主group_id或是group_name
        //select * from pms_attr_group where catelog_id=? and (attr_group_id=key or attr_group_name like %key%)
        //SELECT attr_group_id,icon,catelog_id,sort,descript,attr_group_name FROM pms_attr_group
        // WHERE (catelog_id = ? AND ( (attr_group_id = ? OR attr_group_name LIKE ?) ))
        //Parameters: 1(Long), aa(String), %aa%(String)
        //要查哪张表就用哪个实体类作泛型，实体类关联了那个表
        //能到else，catelogId就不为空了，构造的时候可以把catelogId写入
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();

        // springmvc中有一个工具类叫StringUtils，很多框架都有这个
        if(!StringUtils.isEmpty(key)){
            //如果key不是空，继续构造
            wrapper.and(obj ->{
                obj.eq("attr_group_id",key).or().like("attr_group_name",key);

            });
        }


        //查所有
        if(catelogId == 0){
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
        }else {
            wrapper.eq("catelog_id",catelogId);
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
        }
    }

    /**
     * 根据分类Id,查出所有的分组以及这些组里面的属性
     *
     * */

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        //1.查出当前分类下的所有属性分组
        //this.baseMapper.selectList() = this.list()
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        //2. 查出每个属性分组attrgroup的所有属性attr
        List<AttrGroupWithAttrsVo> attrGroupWithAttrsVos = attrGroupEntities.stream().map(attrGroupEntity -> {
            AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(attrGroupEntity, attrGroupWithAttrsVo);
            List<AttrEntity> relationAttr = attrService.getRelationAttr(attrGroupEntity.getAttrGroupId());
            attrGroupWithAttrsVo.setAttrs(relationAttr);
            return attrGroupWithAttrsVo;
        }).collect(Collectors.toList());
        //TODO 3.回显示的属性还未完成，要判断这些属性从而显示出不出的回显示数据，或根据用户选的参数，设置下一步中的数据

        return attrGroupWithAttrsVos;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

}