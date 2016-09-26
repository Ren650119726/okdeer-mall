package com.okdeer.mall.order.service;

import java.util.Map;

import com.okdeer.base.common.exception.ServiceException;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-17 15:22:36
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *    重构V4.1			2016-07-14			maojj			添加新的编号生成规则
 *    1.0.Z			2016-09-06			zengj			调整订单编号生成规则 
 */
public interface GenerateNumericalService{

	String generateNumericalNumber(Map<String, String> map) throws ServiceException;

	String generateNumber(String numberType) throws ServiceException;

	String generateRandOrderNo(String numberType) throws ServiceException;

	// Begin added by maojj 2016-07-14
	/**
	 * @Description: 生成编号并保存
	 * @param numberType 编号前缀
	 * @return   
	 * @return String  
	 * @throws
	 * @author maojj
	 * @date 2016年7月14日
	 */
	String generateNumberAndSave(String numberType);
	// End added by maojj

	// Begin 1.0.Z 调整订单编号生成规则 add by zengj
	/**
	 * 
	 * @Description: 生成订单编号
	 * @param prefix 订单编号前缀
	 * @param branchCode 店铺机构编码
	 * @param posId POS机ID
	 * @return   新的订单编号
	 * @author zengj
	 * @date 2016年9月5日
	 */
	String generateOrderNo(String prefix, String branchCode, String posId);
	// End 1.0.Z 调整订单编号生成规则 add by zengj
}