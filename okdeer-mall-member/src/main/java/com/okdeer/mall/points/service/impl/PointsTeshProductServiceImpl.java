package com.okdeer.mall.points.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.points.mapper.PointsTeshProductMapper;
import com.okdeer.mall.points.service.PointsTeshProductService;


/**
 * ClassName: PointsTeshProductServiceImpl 
 * @Description: 积分商品service业务处理
 * @author zengjizu
 * @date 2016年12月19日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class PointsTeshProductServiceImpl extends BaseServiceImpl implements PointsTeshProductService {

	@Autowired
	private PointsTeshProductMapper pointsTeshProductMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		
		return pointsTeshProductMapper;
	}

}
