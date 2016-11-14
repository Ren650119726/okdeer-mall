/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2016年11月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.chargesetting.service;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.chargesetting.dto.WhiteManagerDto;
import com.okdeer.mall.chargesetting.entity.RiskWhite;

/**
 * ClassName: IWhiteListService 
 * @Description: TODO
 * @author xuzq01
 * @date 2016年11月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface IWhiteListService extends IBaseService{
	
	public PageUtils<RiskWhite> findWhiteList(WhiteManagerDto whiteManagerDto, Integer pageNumber,
			Integer pageSize);

	/**
	 * @Description: TODO
	 * @param whiteManagerDto
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月14日
	 */
	public int selectWhiteByAccount(WhiteManagerDto whiteManagerDto);
	
}
