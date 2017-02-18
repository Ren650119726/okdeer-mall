package com.okdeer.mall.order.service;

import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.discount.entity.PreferentialVo;

/**
 * 
 * ClassName: GetPreferentialService 
 * @Description: 获取优惠列表
 * @author tangy
 * @date 2016年9月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     1.1.0          2016年9月29日                               tangy             新增
 *     友门鹿V2.1		  2017年2月17日		  maojj				优化
 */
public interface GetPreferentialService {

	/**
	 * @Description: 查询用户有效的优惠列表
	 * @param paramBo 查询条件参数
	 * @return
	 * @throws Exception   
	 * @author maojj
	 * @date 2017年2月17日
	 */
	PreferentialVo findPreferentialByUser(FavourParamBO paramBo) throws Exception;
}
