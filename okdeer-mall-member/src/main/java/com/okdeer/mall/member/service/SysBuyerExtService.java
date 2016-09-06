/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: SysBuyerExtService.java 
 * @Date: 2015年11月26日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */

package com.okdeer.mall.member.service;

import com.okdeer.mall.member.member.entity.SysBuyerExt;
import com.yschome.base.common.exception.ServiceException;


/**
 * 用户扩展表信息查询service
 * @pr yschome-mall
 * @author zhongyong
 * @date 2015年11月20日 下午5:13:29
 */
public interface SysBuyerExtService {

	/**
	 * 根据userId查询会员扩展表
	 * @param userId 请求参数
	 * @return 返回会员扩展实体
	 * @throwsServiceException
	 */
	SysBuyerExt findByUserId(String userId) throws ServiceException;
	
	
	/**
	 * 更改用户扩张表信息
	 *@author luosm
	 * @param sysBuyerExt 请求参数
	 */
	void updateByUserId(SysBuyerExt sysBuyerExt) throws ServiceException;
	
	/**
	 * DESC: 添加
	 * @author LIU.W
	 * @param sysBuyerExt
	 * @return
	 */
	int insertSelective(SysBuyerExt sysBuyerExt) throws ServiceException;
}
