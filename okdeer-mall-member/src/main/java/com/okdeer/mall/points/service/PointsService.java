
package com.okdeer.mall.points.service;

import com.okdeer.mall.member.points.dto.AddPointsParamDto;
import com.okdeer.mall.member.points.dto.ConsumPointParamDto;
import com.okdeer.mall.points.bo.AddPointsResult;

/**
 * ClassName: PointsService 
 * @Description: 积分处理service
 * @author zengjizu
 * @date 2016年12月30日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface PointsService {
	
	/**
	 * @Description: 添加积分
	 * @param addPointsParamDto
	 * @return
	 * @throws Exception
	 * @author zengjizu
	 * @date 2016年12月30日
	 */
	AddPointsResult addPoints(AddPointsParamDto addPointsParamDto) throws Exception;
	
	/**
	 * @Description: 消费积分
	 * @param consumPointParamDto 消费积分参数
	 * @author zengjizu
	 * @date 2017年1月5日
	 */
	void consumPoint(ConsumPointParamDto consumPointParamDto) throws Exception;

}
