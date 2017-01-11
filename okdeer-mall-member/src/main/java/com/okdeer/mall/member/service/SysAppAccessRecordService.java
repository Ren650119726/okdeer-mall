/** 
 *@Project: okdeer-mall-member 
 *@Author: tangzj02
 *@Date: 2017年1月10日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.member.service;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.member.entity.SysAppAccessRecord;

/**
 * ClassName: SysAppAccessRecordService 
 * @Description: APP设备访问记录接口服务
 * @author tangzj02
 * @date 2017年1月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *       V2.0		  2017年1月10日                   tangzj02                     添加
 */

public interface SysAppAccessRecordService extends IBaseService {

	/**
	 * @Description: 添加或更新APP设备访问记录
	 * @param entity 设备访问记录信息
	 * @return int 添加或更新记录数
	 * @throws Exception   
	 * @author tangzj02
	 * @date 2017年1月10日
	 */
	int save(SysAppAccessRecord entity);

}
