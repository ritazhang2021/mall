package com.rita.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rita.common.utils.PageUtils;
import com.rita.mall.member.entity.MemberCollectSubjectEntity;

import java.util.Map;

/**
 * 会员收藏的专题活动
 *
 * @author Rita
 * @email rita2021.zhang@gmail.com
 * @date 2021-06-13 15:00:53
 */
public interface MemberCollectSubjectService extends IService<MemberCollectSubjectEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

