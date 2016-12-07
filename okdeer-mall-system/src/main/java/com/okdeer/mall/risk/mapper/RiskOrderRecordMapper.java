/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * RiskOrderRecordMapper.java
 * @Date 2016-11-17 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.risk.mapper;

import java.util.Date;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.risk.po.RiskOrderRecordPo;

/**
 * ClassName: RiskOrderRecordMapper 
 * @Description: 风控订单记录
 * @author guocp
 * @date 2016年11月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface RiskOrderRecordMapper extends IBaseMapper {

	/**
	 * 根据参数类型和值查询风控订单记录
	 * @param value
	 * @param typeName
	 * @return   
	 * @author guocp
	 * @date 2016年11月18日
	 */
	RiskOrderRecordPo findByParam(@Param("value") String value, @Param("paramType") String paramType,
			@Param("isPreferential") int isPreferential);

	/**
	 * 根据参数类型和值查询充值号码列表
	 * @param loginName   
	 * @author guocp
	 * @date 2016年11月17日
	 */
	Set<String> findTelsByParam(@Param("value") String value, @Param("paramType") String paramType,
			@Param("isPreferential") int isPreferential);

	/**
	 * 根据参数类型和值查询登入手机号码列表
	 * @param loginName   
	 * @author guocp
	 * @date 2016年11月17日
	 */
	Set<String> findLoginNamesByParam(@Param("deviceId") String deviceId, @Param("isPreferential") int isPreferential);
	
	/**
	 * @Description: TODO   
	 * @author xuzq01
	 * @date 2016年11月25日
	 */
	void deleteByTime(@Param("createTime") Date createTime);
}