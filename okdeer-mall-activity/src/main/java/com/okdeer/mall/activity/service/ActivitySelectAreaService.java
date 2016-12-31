/** 
 *@Project: okdeer-mall-operate 
 *@Author: tangzj02
 *@Date: 2016年12月28日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.activity.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.entity.ActivitySelectArea;

/**
 * ClassName: ActivitySelectAreaService 
 * @Description: 活动与城市关联服务实现
 * @author tangzj02
 * @date 2016年12月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0       2016-12-28        tangzj02                     添加
 */
@Service
public interface ActivitySelectAreaService extends IBaseService {

	/**
	 * @Description: 根据活动ID查询数据
	 * @param activityId  活动ID
	 * @return List<ActivitySelectArea>  
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	List<ActivitySelectArea> findListByActivityId(String activityId) throws Exception;

	/**
	 * @Description: 根据活动ID删除数据
	 * @param activityId 活动ID   
	 * @return int 删除记录  
	 * @author tangzj02
	 * @throws Exception
	 * @date 2016年12月28日
	 */
	int deleteByActivityId(String activityId) throws Exception;

	/**
	 * @Description: 批量插入数据
	 * @param scopeList   
	 * @return int 成功插入记录数  
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	int insertMore(List<ActivitySelectArea> scopeList) throws Exception;

}
