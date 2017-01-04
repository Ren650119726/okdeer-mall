/** 
 *@Project: okdeer-mall-operate 
 *@Author: tangzj02
 *@Date: 2016年12月28日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.operate.dto.HomeIconDto;
import com.okdeer.mall.operate.dto.HomeIconParamDto;
import com.okdeer.mall.operate.entity.ColumnHomeIcon;
import com.okdeer.mall.operate.enums.ColumnType;
import com.okdeer.mall.operate.mapper.ColumnHomeIconMapper;
import com.okdeer.mall.operate.service.ColumnHomeIconService;
import com.okdeer.mall.operate.service.ColumnSelectAreaService;

/**
 * ClassName: ColumnHomeIconServiceImpl 
 * @Description: 首页ICON的服务实现
 * @author tangzj02
 * @date 2016年12月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * * 	        友门鹿2.0        2016-12-28        tangzj02                     添加
 */

@Service
public class ColumnHomeIconServiceImpl extends BaseServiceImpl implements ColumnHomeIconService {

	@Autowired
	private ColumnHomeIconMapper activityhomeIconMapper;

	@Autowired
	private ColumnSelectAreaService selectAreaService;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return activityhomeIconMapper;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnHomeIconService#findList(com.okdeer.mall.operate.dto.HomeIconParamDto)
	 */
	@Override
	public List<ColumnHomeIcon> findList(HomeIconParamDto paramDto) throws Exception {
		return activityhomeIconMapper.findList(paramDto);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnHomeIconService#findListByCityId(java.lang.String, java.lang.String)
	 */
	@Override
	public List<HomeIconDto> findListByCityId(String provinceId, String cityId) throws Exception {
		if (!StringUtils.isNotEmptyAll(provinceId, cityId)) {
			return new ArrayList<HomeIconDto>();
		}
		// 根据城市查询相应的首页ICON栏位
		List<String> ids = selectAreaService.findColumnIdsByCity(cityId, provinceId, ColumnType.homeIcon.ordinal());
		if (null == ids || ids.size() == 0) {
			return new ArrayList<HomeIconDto>();
		}

		// 设置首页ICON查询参数
		HomeIconParamDto paramDto = new HomeIconParamDto();
		paramDto.setIds(ids);
		// 查询首页ICON列表
		List<ColumnHomeIcon> sourceList = findList(paramDto);
		List<HomeIconDto> dtoList = null;
		if (null == sourceList) {
			dtoList = new ArrayList<HomeIconDto>();
		} else {
			dtoList = BeanMapper.mapList(sourceList, HomeIconDto.class);
		}
		return dtoList;
	}

}
