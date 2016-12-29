/** 
 *@Project: okdeer-mall-operate 
 *@Author: tangzj02
 *@Date: 2016年12月28日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.activity.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.entity.HomeIconArea;

/**
 * ClassName: HomeIconService 
 * @Description: 首页ICON服务
 * @author tangzj02
 * @date 2016年12月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 		  1 		   2016-12-28        tangzj02                     添加
 */

public interface HomeIconAreaService extends IBaseService {

	/**
	 * @Description: 根据首页ICONID查询数据
	 * @param iconId  
	 * @return List<HomeIconArea>  
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	List<HomeIconArea> findListByHomeIcon(String iconId) throws Exception;

	/**
	 * @Description: 根据首页ICONID删除数据
	 * @param iconId   
	 * @return int 删除记录  
	 * @author tangzj02
	 * @throws Exception
	 * @date 2016年12月28日
	 */
	int deleteByHomeIconId(String iconId) throws Exception;

	/**
	 * @Description: 批量插入数据
	 * @param scopeList   
	 * @return int 成功插入记录数  
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	int insertMore(List<HomeIconArea> scopeList) throws Exception;

}
