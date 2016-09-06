package com.okdeer.mall.points.service.impl;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.member.points.enums.PointsRuleCode;
import com.okdeer.mall.member.points.service.PointsBuriedServiceApi;
import com.yschome.api.psms.point.service.AppUserPointServiceApi;

/**
 * 
 * ClassName: PointServiceApiImpl 
 * @Description: TODO
 * @author wusw
 * @date 2016年7月23日
 *
 *  =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *    重构4.1            2016-7-23            wusw                添加dubbo调用注解
 *	      重构4.1            2016-8-5             luosm               返回值增加积分值  
 */
@Service(version = "1.0.0", interfaceName = "com.yschome.api.psms.point.service.AppUserPointServiceApi")
public class PointServiceApiImpl implements AppUserPointServiceApi {

	@Reference(version = "1.0.0", check = false)
	private PointsBuriedServiceApi pointsBuriedService; 

	@Override
	public Integer appUseraddPoints(String userId, String code) throws Exception {
		// TODO Auto-generated method stub
		//begin add by luosm 2016-08-05
		int point = pointsBuriedService.doProcessPoints(userId, PointsRuleCode.valuesOf(code));
		return point;
		//end add by luosm 2016-08-05
	}

}
