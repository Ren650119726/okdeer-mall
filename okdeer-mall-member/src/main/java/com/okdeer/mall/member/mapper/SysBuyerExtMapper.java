package com.okdeer.mall.member.mapper;

import com.okdeer.mall.member.member.entity.SysBuyerExt;


/**
 * 会员扩展表dao
 * @author zhongy
 * @date  2015-11-20 17:37:04
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface SysBuyerExtMapper {
	/**
	 * 根据userId查询会员职业
	 * @param userId 请求参数
	 * @return 返回会员扩展信息
	 */
	SysBuyerExt selectByUserId(String userId);
	
	/**
	 * 按需更新会员扩展实体
	 *
	 * @param sysBuyerExt 实体
	 */
	void updateByPrimaryKeySelective(SysBuyerExt sysBuyerExt);
	
	/**
	 * 根据userId更新会员扩展实体
	 *
	 * @param sysBuyerExt 实体
	 */
	void updateByUserId(SysBuyerExt sysBuyerExt);
	
	/**
	 * DESC: 添加
	 * @author LIU.W
	 * @param sysBuyerExt
	 * @return
	 */
	int insertSelective(SysBuyerExt sysBuyerExt);
}