/** 
 *@Project: okdeer-mall-operate 
 *@Author: tangzj02
 *@Date: 2016年12月28日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.activity.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.dto.ActivityHomeIconParamDto;
import com.okdeer.mall.activity.entity.ActivityHomeIcon;

/**
 * ClassName: HomeIconService 
 * @Description: 首页ICON与商品关联服务
 * @author tangzj02
 * @date 2016年12月30日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0        2016-12-30        tangzj02                     添加
 */

public interface ActivityHomeIconService extends IBaseService {

	/**
	 * @Description: 查询首页ICON列表
	 * @param paramDto 查询参数
	 * @return List<ActivityHomeIcon> 首页ICON记录列表
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	List<ActivityHomeIcon> findList(ActivityHomeIconParamDto paramDto);

}
