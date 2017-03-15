
package com.okdeer.mall.operate.advert.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.advert.dto.ColumnAdvertDto;
import com.okdeer.mall.advert.dto.ColumnAdvertQueryParamDto;
import com.okdeer.mall.advert.entity.AdvertPosition;
import com.okdeer.mall.advert.entity.ColumnAdvert;
import com.okdeer.mall.advert.service.ColumnAdvertApi;
import com.okdeer.mall.operate.advert.service.AdvertPositionService;
import com.okdeer.mall.operate.advert.service.ColumnAdvertService;

/**
 * ClassName: ColumnAdvertApiImpl 
 * @Description: 广告api
 * @author zengjizu
 * @date 2017年1月3日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(interfaceName = "com.okdeer.mall.advert.service.ColumnAdvertApi", version = "1.0.0")
public class ColumnAdvertApiImpl implements ColumnAdvertApi {

	@Autowired
	private ColumnAdvertService columnAdvertService;

	@Autowired
	private AdvertPositionService advertPositionService;

	@Override
	public List<ColumnAdvertDto> findForApp(ColumnAdvertQueryParamDto advertQueryParamDto) {
		AdvertPosition advertPosition = advertPositionService.findByType(advertQueryParamDto.getAdvertType());
		advertQueryParamDto.setPositionId(advertPosition.getId());
		List<ColumnAdvert> list = columnAdvertService.findForApp(advertQueryParamDto);
		if (list != null) {
			List<ColumnAdvertDto> dtoList = BeanMapper.mapList(list, ColumnAdvertDto.class);
			return dtoList;
		}
		return null;
	}

	@Override
	public List<ColumnAdvertDto> findForAppV220(ColumnAdvertQueryParamDto advertQueryParamDto) {
		AdvertPosition advertPosition = advertPositionService.findByType(advertQueryParamDto.getAdvertType());
		advertQueryParamDto.setPositionId(advertPosition.getId());
		List<ColumnAdvert> list = columnAdvertService.findForAppV220(advertQueryParamDto);
		if (list != null) {
			List<ColumnAdvertDto> dtoList = BeanMapper.mapList(list, ColumnAdvertDto.class);
			return dtoList;
		}
		return null;
	}
}
