/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2016年12月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.advert.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.advert.entity.ColumnAdvertGoods;

/**
 * ClassName: ColumnAdvertGoodsApiImpl 
 * @Description: 活动商品中间表
 * @author xuzq01
 * @date 2016年12月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2.3			2016年12月8日		xuzq01				活动商品中间表Service
 */

public interface ColumnAdvertGoodsService extends IBaseService {

	List<ColumnAdvertGoods> findByAdvertId(String advertId);
}
