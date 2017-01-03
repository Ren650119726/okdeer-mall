/** 
 *@Project: okdeer-archive-goods 
 *@Author: wangf01
 *@Date: 2017年1月2日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.activity.service;

/**
 * ClassName: ELSkuService 
 * @Description: 搜素引擎商品-service
 * @author wangf01
 * @date 2017年1月2日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface ELSkuService {

	/**
	 * 
	 * @Description: 同步特惠活动商品数据到搜索引擎
	 * @param syncType int 0：新增 1：更新 2：删除
	 * @return boolean
	 * @throws Exception
	 * @author wangf01
	 * @date 2017年1月2日
	 */
	boolean syncSaleToEL(int syncType) throws Exception;

	/**
	 * 
	 * @Description: 同步秒杀活动商品数据到搜索引擎
	 * @param syncType int 0：新增 1：更新 2：删除
	 * @return boolean
	 * @throws Exception
	 * @author wangf01
	 * @date 2017年1月2日
	 */
	boolean syncSeckillToEL(int syncType) throws Exception;

	/**
	 * 
	 * @Description: 同步低价商品数据到搜索引擎
	 * @param syncType int 0：新增 1：更新 2：删除
	 * @return boolean
	 * @throws Exception
	 * @author wangf01
	 * @date 2017年1月2日
	 */
	boolean syncLowPriceToEL(int syncType) throws Exception;
}
