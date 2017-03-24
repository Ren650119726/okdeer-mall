/** 
 *@Project: okdeer-mall-operate 
 *@Author: tangzj02
 *@Date: 2017年3月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.operate.entity.ColumnAdvertVersionDto;

/**
 * ClassName: ColumnAdvertVersionService 
 * @Description: 广告与APP版本关联信息
 * @author tangzj02
 * @date 2017年3月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.2       2017-03-14        tangzj02                        添加
 */

public interface ColumnAdvertVersionService extends IBaseService {

	/**
	 * @Description: 根据广告ID查询与APP版本关联信息列表
	 * @param advertId 广告ID
	 * @return List<ColumnAdvertVersion>  
	 * @throws Exception
	 * @author tangzj02
	 * @date 2017年3月14日
	 */
	List<ColumnAdvertVersionDto> findListByAdvertId(String advertId) throws Exception;

}
