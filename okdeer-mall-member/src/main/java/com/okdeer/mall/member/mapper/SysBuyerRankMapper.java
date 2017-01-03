/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * SysBuyerRankMapper.java
 * @Date 2016-12-30 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.member.mapper;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.member.entity.SysBuyerRank;

public interface SysBuyerRankMapper extends IBaseMapper {

	/**
	 * @Description: 根据rankCode查询会员等级信息
	 * @param rankCode
	 * @return
	 * @author zengjizu
	 * @date 2016年12月30日
	 */
	SysBuyerRank findByRankCode(@Param("rankCode") String rankCode);
	/**
	 * @Description: 根据成长值获取会员等级
	 * @param growth
	 * @return
	 * @author zengjizu
	 * @date 2016年12月30日
	 */
	SysBuyerRank findByGrowth(@Param("growth")int growth);
}