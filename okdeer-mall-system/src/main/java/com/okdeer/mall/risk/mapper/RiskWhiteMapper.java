/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * RiskWhiteMapper.java
 * @Date 2016-11-11 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.risk.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.risk.dto.RiskWhiteDto;
import com.okdeer.mall.risk.entity.RiskBlack;
import com.okdeer.mall.risk.entity.RiskWhite;

public interface RiskWhiteMapper extends IBaseMapper {

	/**
	 * @Description: TODO
	 * @param whiteManagerDto
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月14日
	 */
	List<RiskWhite> findWhiteList(RiskWhiteDto whiteManagerDto);

	/**
	 * @Description: TODO
	 * @param whiteManagerDto
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月14日
	 */
	int selectWhiteByAccount(@Param(value = "telephoneAccount") String telephoneAccount);

	/**
	 * @Description: TODO
	 * @param ids
	 * @param updateUserId
	 * @param updateTime   
	 * @author xuzq01
	 * @date 2016年11月16日
	 */
	void deleteBatchByIds(@Param("ids")List<String> ids,@Param("updateUserId") String updateUserId,@Param("updateTime") Date updateTime);
	
	/**
	 * @Description: TODO
	 * @param riskBlackList   
	 * @author xuzq01
	 * @date 2016年11月17日
	 */
	void addBatch(@Param(value = "riskList")List<RiskWhite> riskList);

	/**
	 * @Description: TODO
	 * @param riskWhite
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月18日
	 */
	int findCountByAccount(RiskWhite riskWhite);
}