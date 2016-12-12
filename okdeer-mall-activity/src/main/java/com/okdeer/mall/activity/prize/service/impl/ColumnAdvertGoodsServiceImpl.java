/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2016年12月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.prize.entity.ColumnAdvertGoods;
import com.okdeer.mall.activity.prize.mapper.ColumnAdvertGoodsMapper;
import com.okdeer.mall.activity.prize.service.ColumnAdvertGoodsService;

/**
 * ClassName: ColumnAdvertGoodsApiImpl 
 * @Description: 活动商品中间表Service实现类
 * @author xuzq01
 * @date 2016年12月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2.3			2016年12月8日		xuzq01				活动商品中间表Service实现类
 */
@Service
public class ColumnAdvertGoodsServiceImpl extends BaseServiceImpl implements ColumnAdvertGoodsService {

	/**
	 * 活动商品中间表mapper
	 */
	@Autowired
	ColumnAdvertGoodsMapper columnAdvertGoodsMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return columnAdvertGoodsMapper;
	}

	@Override
	public List<ColumnAdvertGoods> findByAdvertId(String advertId) {
		return columnAdvertGoodsMapper.findByAdvertId(advertId);
	}


}
