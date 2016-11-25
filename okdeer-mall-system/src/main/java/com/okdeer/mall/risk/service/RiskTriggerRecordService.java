/** 
 *@Project: okdeer-mall-system 
 *@Author: guocp
 *@Date: 2016年11月17日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.risk.entity.RiskTriggerRecord;


/**
 * ClassName: RiskTriggerRecord 
 * @Description: 触发记录接口
 * @author guocp
 * @date 2016年11月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface RiskTriggerRecordService extends IBaseService {

	/**
	 * @Description: 查询风控记录列表
	 * @param params
	 * @return
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月18日
	 */
	List<RiskTriggerRecord> list(Map<String,Object> params) throws Exception;
	
	/**
	 * @Description: 批量删除
	 * @param ids
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月17日
	 */
	void deleteBatch(List<String> ids) throws Exception; 
	
	/**
	 * 
	 * @Description: 根据时间直接删除动作记录
	 * @throws Exception   
	 * @author xuzq01
	 * @param date 
	 * @date 2016年11月25日
	 */
	void deleteByTime(Date createTime) throws Exception;
}
