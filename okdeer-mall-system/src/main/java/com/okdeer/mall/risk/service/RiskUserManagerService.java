/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2016年11月15日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.service;

import java.util.Date;
import java.util.List;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.risk.dto.RiskUserManagerDto;
import com.okdeer.mall.risk.entity.RiskUserManager;

/**
 * ClassName: RiskUserManagerService 
 * @Description: 风控人员管理service
 * @author xuzq01
 * @date 2016年11月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2			2016年11月15日		xuzq01				风控人员管理service
 */

public interface RiskUserManagerService extends IBaseService {

	/**
	 * 查询风控管理人员列表
	 * @param userManagerDto
	 * @return   
	 * @author guocp
	 * @date 2016年11月19日
	 */
	List<RiskUserManager> findUserList(RiskUserManagerDto userManagerDto);
	
	/**
	 * @Description: 查询风控管理人员列表(分页)
	 * @param userManagerDto
	 * @param pageNumber
	 * @param pageSize
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月15日
	 */
	PageUtils<RiskUserManager> findUserList(RiskUserManagerDto userManagerDto, Integer pageNumber, Integer pageSize);

	/**
	 * @Description: 
	 * @param ids
	 * @param updateUserId
	 * @param updateTime   
	 * @author xuzq01
	 * @date 2016年11月15日
	 */
	void deleteBatchByIds(List<String> ids, String updateUserId, Date updateTime);

	/**
	 * @Description: 
	 * @param riskUserManager
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月29日
	 */
	int findCountByTelephoneOrEmail(RiskUserManager riskUserManager);

}
