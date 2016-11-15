/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2016年11月15日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.chargesetting.service;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.chargesetting.dto.UserManagerDto;
import com.okdeer.mall.chargesetting.entity.RiskUserManager;

/**
 * ClassName: IRiskManagerService 
 * @Description: 风控人员管理service
 * @author xuzq01
 * @date 2016年11月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2			2016年11月15日		xuzq01				风控人员管理service
 */

public interface IRiskManagerService extends IBaseService {

	/**
	 * @Description: TODO
	 * @param userManagerDto
	 * @param pageNumber
	 * @param pageSize
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月15日
	 */
	PageUtils<RiskUserManager> findUserList(UserManagerDto userManagerDto, Integer pageNumber, Integer pageSize);

}
