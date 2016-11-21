/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * RiskBlackMapper.java
 * @Date 2016-11-11 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.risk.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.risk.dto.RiskBlackDto;
import com.okdeer.mall.risk.entity.RiskBlack;
/**
 * 
 * ClassName: RiskBlackMapper 
 * @Description: 黑名单的dao接口类
 * @author xuzq01
 * @date 2016年11月21日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.2			2016年11月21日	xuzq01				黑名单的dao接口类
 */
public interface RiskBlackMapper extends IBaseMapper {

	/**
	 * @Description: TODO
	 * @param blackManagerDto
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月15日
	 */
	List<RiskBlack> findBlackList(RiskBlackDto blackManagerDto);
	
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
	void addBatch(@Param(value = "riskList")List<RiskBlack> riskList);
	
	/**
	 * @Description: 通过参数获取黑名单列表
	 * @param map
	 * @return
	 * @author zhangkn
	 * @date 2016年11月18日
	 */
	List<RiskBlack> findBlackListByParams(Map<String,Object> map);

	/**
	 * @Description: TODO
	 * @param riskBlack
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月18日
	 */
	int findCountByAccount(RiskBlack riskBlack);

	/**
	 * @Description: TODO
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月21日
	 */
	Set<RiskBlack> findAllBlackMobile();

	/**
	 * @Description: TODO
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月21日
	 */
	Set<RiskBlack> findAllBlackDevice();

	/**
	 * @Description: TODO
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月21日
	 */
	Set<RiskBlack> findAllBlackPayAccount();

	/**
	 * @Description: TODO
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月21日
	 */
	Set<RiskBlack> findAllBlackLoginAccount();
	
	

}