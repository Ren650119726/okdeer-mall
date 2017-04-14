/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.staticFile.service;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.staticFile.entity.ActivityStaticFile;
import com.okdeer.mall.activity.staticFile.entity.ActivityStaticFileVo;

/**
 * ClassName: ActivityStaticFileService 
 * @Description: TODO
 * @author xuzq01
 * @date 2017年4月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface ActivityStaticFileService extends IBaseService {

	/**
	 * @Description: TODO
	 * @param activityStaticFileVo
	 * @param pageNumber
	 * @param pageSize
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月12日
	 */
	PageUtils<ActivityStaticFile> findStaticFileList(ActivityStaticFileVo activityStaticFileVo, int pageNumber,
			int pageSize);

	/**
	 * @Description: TODO
	 * @param activityStaticFile
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月12日
	 */
	int findCountByName(ActivityStaticFile activityStaticFile);

}
