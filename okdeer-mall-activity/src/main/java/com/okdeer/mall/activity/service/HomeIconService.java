/** 
 *@Project: okdeer-mall-operate 
 *@Author: tangzj02
 *@Date: 2016年12月28日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.activity.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.dto.HomeIconParamDto;
import com.okdeer.mall.activity.entity.HomeIcon;

/**
 * ClassName: HomeIconService 
 * @Description: 首页ICON与商品关联服务
 * @author tangzj02
 * @date 2016年12月30日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 		  1 		   2016-12-30        tangzj02                     添加
 */

public interface HomeIconService extends IBaseService {


	/**
	 * @Description: 查询首页ICON列表
	 * @param paramDto 查询参数
	 * @return List<HomeIconDto> 首页ICON记录列表
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	List<HomeIcon> findList(HomeIconParamDto paramDto);

}
