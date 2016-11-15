/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2016年11月15日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.chargesetting.service;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.chargesetting.dto.BlackManagerDto;
import com.okdeer.mall.chargesetting.entity.RiskBlack;

/**
 * ClassName: IBlackListService 
 * @Description: 黑名单管理service
 * @author xuzq01
 * @date 2016年11月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2			2016年11月15日		xuzq01				黑名单管理service
 */

public interface IBlackListService extends IBaseService {
	
	public PageUtils<RiskBlack> findBlackList(BlackManagerDto blackManagerDto, Integer pageNumber,
			Integer pageSize);
}
