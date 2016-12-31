
package com.okdeer.mall.points.api;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.member.points.dto.AddPointsParamDto;
import com.okdeer.mall.member.points.dto.AddPointsResultDto;
import com.okdeer.mall.member.points.service.PointsApi;
import com.okdeer.mall.points.bo.AddPointsResult;
import com.okdeer.mall.points.service.PointsService;

/**
 * ClassName: PointsApi 
 * @Description: 积分api
 * @author zengjizu
 * @date 2016年12月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(interfaceName = "com.okdeer.mall.member.points.service.PointsApi", version = "1.0.0")
public class PointsApiImpl implements PointsApi {

	@Autowired
	private PointsService pointsService;

	@Override
	public AddPointsResultDto addPoints(AddPointsParamDto addPointsParamDto) throws Exception {
		AddPointsResult result = pointsService.addPoints(addPointsParamDto);
		AddPointsResultDto dto = BeanMapper.map(result, AddPointsResultDto.class);
		return dto;
	}
}
