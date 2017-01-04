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
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.operate.entity.ColumnSelectArea;
import com.okdeer.mall.operate.mapper.ColumnSelectAreaMapper;
import com.okdeer.mall.operate.service.ColumnSelectAreaService;

/**
 * ClassName: ColumnSelectAreaServiceImpl 
 * @Description: 栏目与城市关联服务
 * @author tangzj02
 * @date 2016年12月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0       2016-12-28        tangzj02                     添加
 */

@Service
public class ColumnSelectAreaServiceImpl extends BaseServiceImpl implements ColumnSelectAreaService {

	@Autowired
	private ColumnSelectAreaMapper selectAreaMapper;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return selectAreaMapper;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnSelectAreaService#findListByColumnId(java.lang.String)
	 */
	@Override
	public List<ColumnSelectArea> findListByColumnId(String columnId) throws Exception {
		return selectAreaMapper.findListByColumnId(columnId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnSelectAreaService#findListByColumnIds(java.util.List)
	 */
	@Override
	public List<ColumnSelectArea> findListByColumnIds(List<String> columnIds) throws Exception {
		if (null == columnIds || columnIds.size() == 0) {
			return new ArrayList<>();
		}
		return selectAreaMapper.findListByColumnIds(columnIds);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnSelectAreaService#deleteByColumnId(java.lang.String)
	 */
	@Override
	public int deleteByColumnId(String columnId) throws Exception {
		return selectAreaMapper.deleteByColumnId(columnId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnSelectAreaService#insertMore(java.util.List)
	 */
	@Override
	public int insertMore(List<ColumnSelectArea> areaList) throws Exception {
		return selectAreaMapper.insertMore(areaList);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnSelectAreaService#findColumnIdsByCity(java.lang.String, java.lang.String, java.lang.Integer)
	 */
	@Override
	public List<String> findColumnIdsByCity(String provinceId, String cityId, Integer columnType) throws Exception {
		return selectAreaMapper.findColumnIdsByCity(provinceId, cityId, columnType);
	}
}
