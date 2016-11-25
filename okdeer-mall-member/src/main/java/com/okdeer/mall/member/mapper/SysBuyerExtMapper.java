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
	
	/**
	 * @Description: 重置已经抽奖机会为0的用户，将抽奖机会重置为1次
	 * @throws
	 * @author tuzhd
	 * @date 2016年11月22日
	 */
	void updateUserPrizeCount();
	
	/**
	 * @Description: 根据用户id 抽奖之后将其抽奖机会-1
	 * @throws
	 * @author tuzhd
	 * @date 2016年11月22日
	 */
	void updateCutPrizeCount(String userId);
	
	
	
}