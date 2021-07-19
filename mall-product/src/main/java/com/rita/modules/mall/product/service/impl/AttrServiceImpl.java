package com.rita.modules.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.rita.common.constant.ProductConstant;
import com.rita.common.utils.R;
import com.rita.modules.mall.product.dao.AttrAttrgroupRelationDao;
import com.rita.modules.mall.product.dao.AttrGroupDao;
import com.rita.modules.mall.product.dao.CategoryDao;
import com.rita.modules.mall.product.entity.*;
import com.rita.modules.mall.product.service.CategoryService;
import com.rita.modules.mall.product.vo.AttrGroupRelationVo;
import com.rita.modules.mall.product.vo.AttrRespVo;
import com.rita.modules.mall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rita.common.utils.PageUtils;
import com.rita.common.utils.Query;

import com.rita.modules.mall.product.dao.AttrDao;
import com.rita.modules.mall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Autowired
    AttrAttrgroupRelationDao relationDao;
    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    CategoryService categoryService;

    @Transactional
    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        //单次删除
        //relationDao.delete(new QueryWrapper<>().eq("attr_id",1L).eq("attr_group_id",1L));
        //批量删除
        //SQL语句 ：DELETE FROM pms_attr_attrgroup_relation WHERE (attr_id=1 AND attr_group_id=1)
        //OR (attr_id=2 AND attr_group_id=3)
        //转成list或map....relationDao.deleteByMap()
        //因为这个是按键值对删除，基本的操作方法不能满足，所以需要编写Dao和XML文件
        List<AttrAttrgroupRelationEntity> entities = Arrays.asList(vos).stream().map((item) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        relationDao.deleteBatchRelation(entities);


    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }
    @Transactional
    @Override
    public void saveAttrVo(AttrVo attrVo) {
        //将页面的信息封装进对象
        AttrEntity attrEntity = new AttrEntity();
        //这样一个一个的取太麻烦了
        //attrEntity.setAttrName(attrVo.getAttrName());
        BeanUtils.copyProperties(attrVo, attrEntity);
        //保存基本数据
        this.save(attrEntity);
        //保存关联关系
        //判断只有在”base“，基本属性时，才做关联操作，sale属性不需要
        if(attrVo.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attrVo.getAttrGroupId()!=null){
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            //不能用this.baseMapper，因为this.baseMapper操作的是AttrEntity
            relationDao.insert(relationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType) {
        //先判断，attr_type是base，就是0，否则就是1，它只有两个属性，一个是base，另一个是sale
        QueryWrapper<AttrEntity> queryWrapper =
                new QueryWrapper<AttrEntity>()
                        .eq("attr_type","base".equalsIgnoreCase(attrType)?ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode():ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        if(catelogId != 0){
            //如果catelogId不等于0,就按catelogId查询

            queryWrapper.eq("catelog_id", catelogId);
        }
        //如果params中有数据，再按params中进行检索
        //获取检索条件
        String key = (String)params.get("key");
        if(!StringUtils.isEmpty(key)){
            //在上面的查询条件catelogId中继续拼装
            queryWrapper.and((wrapper)->{
                wrapper.eq("attr_id", key).or().like("attr_name",key);

            });
        }
        //this.page先将分页条件params，封装成一个Ipage参数，再根据attrWrapper的查询条件进行分页查询
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),queryWrapper

        );
        //返回的是AttrEntity的信息，我们还希望增加一些信息返回，同时又不想进行连表查询
        //我们就可以在这里对PageUtils进行改变，将返回的值AttrEntity变成AttrRespVo,对应的值分别从各自的数据库拿到
        //return new PageUtils(page);
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();

        List<AttrRespVo> respVos = records.stream().map(attrEntity -> {
            //这里每次必须新增
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            //给分类和分组赋值
            if("base".equalsIgnoreCase(attrType) && attrEntity.getAttrId() != null){
                //有AttrEntity的id，对能对AttrAttrgroupRelationEntity进行查询，就能拿到attrGroupId
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                //这样会造成空指针异常 因为id不能为空
                //Long attrGroupId = attrAttrgroupRelationEntity.getAttrGroupId();
                if(attrAttrgroupRelationEntity !=null && attrAttrgroupRelationEntity.getAttrGroupId() != null ){
                    //拿到attrGroupId就可以对它的数据库进行查询
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());

                }
            }


            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }else {
                System.out.println("Category 没有数据");
            }
            return attrRespVo;

        }).collect(Collectors.toList());

        pageUtils.setList(respVos);
        return pageUtils;
    }
    //不用写事务
    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        //查询需要的信息，封装到AttrRespVo中
        //先将attrEntity的信息copy到attrRespVo
        AttrRespVo attrRespVo = new AttrRespVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, attrRespVo);
        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            //获取分组信息值，groupName和catelogName，catelogPath
            AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
            if(relationEntity != null){
                //attrRespVo.setAttrGroupId(relationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                if(attrGroupEntity != null){
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }

            }

        }
        //获取catelogName
        CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
        if (categoryEntity != null){
            attrRespVo.setGroupName(categoryEntity.getName());
        }

        //获取分类路径
        Long[] cateLogPath = categoryService.findCateLogPath(attrEntity.getCatelogId());
        attrRespVo.setCatelogPath(cateLogPath);


        return attrRespVo;
    }

    @Transactional
    @Override
    public void updateAttrVo(AttrVo attr) {
        //完成基本修改，AttrEntity
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);

        //判断只有在”base“，基本属性时，才做关联操作，sale属性不需要
        if(attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            //完成关联修改
            //update pms_attr_attrgroup_relation SET catelog_name=? where catelog_id=?
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(attr, relationEntity);
            //如果count大于0，更新，否则，添加
            Integer count = relationDao.selectCount(new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if(count > 0){
                relationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId()));
            }else {
                relationDao.insert(relationEntity);
            }
       /* CategoryEntity categoryEntity = new CategoryEntity();
        categoryDao.update(categoryEntity, new UpdateWrapper<CategoryEntity>().eq("cat_id",attr.getCatelogId()));*/

        }
    }
    /**
     * 根据分组id，查找关联的所有属性（都是在AttrEntity的属性，不用创建vo实例）
     * */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        //进行分步查询，不是连表查询
        //查询一个属性相等的所有记录
        List<AttrAttrgroupRelationEntity> entities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        System.out.println("entities*************"+entities);
        Collection<AttrEntity> attrEntities = new ArrayList<>();
        //这是个坑，要用&&，不能用||
        if(entities != null && entities.size() != 0){
            System.out.println("进入第一个If");
            //从attrgroups中只收集AttrId
            List<Long> attrIds = entities.stream().map((entitie) -> {
                return entitie.getAttrId();
            }).collect(Collectors.toList());

            if(attrIds != null || attrIds.size() != 0){
                System.out.println("进入第二个if");
                attrEntities = this.listByIds(attrIds);
                return (List<AttrEntity>)attrEntities;
            }else {
                System.out.println("没有分组信息");
                return null;
            }

        }else {
            System.out.println("没有分组信息");
            return null;
        }

    }
    /**
    * 获取当前分组没有关联的所有属性
    * */

    @Override
    public PageUtils getNoRelationAttr(Long attrgroupId, Map<String, Object> params) {
        //考虑当前分组只能关联自己所属的分类里面的属性
        //查出当前分组的信息
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        //从当前分组的id，我们可以获取到当前分类的id
        Long catelogId = attrGroupEntity.getCatelogId();
        //当前分组只能关联别的分组没有引用的属性
        //1.查出当前分类下的其它属性(不包括当前属性)
        List<AttrGroupEntity> groupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId)/*.ne("attr_group_id", attrgroupId)*/);

        List<Long> attrGroupIds = groupEntities.stream().map(item -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());
        //2.这些分组关联的属性,查找一个集合的attr_group_id在AttrAttrgroupRelationEntity表中的所有引用属性
        List<AttrAttrgroupRelationEntity> attrgroupRelationEntities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", attrGroupIds));
        List<Long> attrIds = attrgroupRelationEntities.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        //3. 从当前分类的所有属性中移除这些属性
        //this.baseMapper = AttrDao
        //这个是返回entity
        //List<AttrEntity> attrEntities = this.baseMapper.selectList(new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).notIn("attr_id", attrIds));
        //以下是返回分页对象
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type",ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if(attrIds != null && attrIds.size() > 0){
            attrEntityQueryWrapper.notIn("attr_id", attrIds);
        }


        //还要进行模糊查询
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            attrEntityQueryWrapper.and((w)->{
                w.eq("attr_id",key).or().like("attr_name", key);
            });
        }
        //构造返回的page,先传进一个Ipage对象，调用工具类中的Query,再传进一个Wrapper
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), attrEntityQueryWrapper);
        PageUtils pageUtils = new PageUtils(page);
        return pageUtils;
    }




}