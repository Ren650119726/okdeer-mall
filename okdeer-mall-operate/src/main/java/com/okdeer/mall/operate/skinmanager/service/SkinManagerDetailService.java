/** 
 *@Project: okdeer-mall-operate 
 *@Author: xuzq01
 *@Date: 2016年11月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.skinmanager.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.operate.entity.SkinManagerDetail;

/**
 * ClassName: ISkinManagerDetailService 
 * @author xuzq01
 * @date 2016年11月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface SkinManagerDetailService extends IBaseService {
	
	/**
	 * @Description: 添加活动皮肤详细
	 * @param detail
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月5日
	 */
	int addBatch(@Param(value = "detail") List<SkinManagerDetail> detail) throws Exception;

	/**
	 * @param detail
	 * @return
	 * @throws Exception   
	 * @author xuzq01
	 * @date 2016年11月14日
	 */
	int updateBatch(@Param(value = "detail") List<SkinManagerDetail> detail) throws Exception;



}
