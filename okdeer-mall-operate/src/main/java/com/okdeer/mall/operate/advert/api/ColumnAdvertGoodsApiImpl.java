/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2016年12月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.advert.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.prize.service.ColumnAdvertGoodsApi;
import com.okdeer.mall.advert.entity.ColumnAdvertGoods;
import com.okdeer.mall.operate.advert.service.ColumnAdvertGoodsService;

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
@Service(version="1.0.0")
public class ColumnAdvertGoodsApiImpl implements ColumnAdvertGoodsApi {

	/**
	 * 活动商品中间表Service
	 */
	@Autowired
	ColumnAdvertGoodsService columnAdvertGoodsService;
	
	@Override
	public List<ColumnAdvertGoods> findByAdvertId(String advertId) {
		return columnAdvertGoodsService.findByAdvertId(advertId);
	}

}
