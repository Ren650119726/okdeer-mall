/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * RiskWhiteMapper.java
 * @Date 2016-11-11 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.risk.mapper;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.risk.dto.RiskWhiteDto;
import com.okdeer.mall.risk.entity.RiskWhite;

public interface RiskWhiteMapper extends IBaseMapper {

	/**
	 * @param whiteManagerDto
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月14日
	 */
	List<RiskWhite> findWhiteList(RiskWhiteDto whiteManagerDto);


	/**
	 * @param ids
	 * @param updateUserId
	 * @param updateTime   
	 * @author xuzq01
	 * @date 2016年11月16日
	 */
	void deleteBatchByIds(@Param("ids")List<String> ids,@Param("updateUserId") String updateUserId,@Param("updateTime") Date updateTime);
	
	/**
	 * @param riskBlackList   
	 * @author xuzq01
	 * @date 2016年11月17日
	 */
	void addBatch(@Param(value = "riskList")List<RiskWhite> riskList);

	/**
	 * @param riskWhite
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月18日
	 */
	int findCountByAccount(RiskWhite riskWhite);


	/**
	 * @Description: 返回所有白名单
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月21日
	 */
	Set<RiskWhite> findAllWhite();


	/**
	 * @Description: 根据账号更新白名单
	 * @param riskWhite   
	 * @author xuzq01
	 * @date 2016年11月23日
	 */
	void updateByAccount(RiskWhite riskWhite);
}