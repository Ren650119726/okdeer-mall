/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2016年11月15日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.risk.dto.RiskBlackDto;
import com.okdeer.mall.risk.entity.RiskBlack;

/**
 * ClassName: RiskBlackService 
 * @Description: 黑名单管理service
 * @author xuzq01
 * @date 2016年11月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2			2016年11月15日		xuzq01				黑名单管理service
 */

public interface RiskBlackService extends IBaseService {
	
	public PageUtils<RiskBlack> findBlackList(RiskBlackDto blackManagerDto, Integer pageNumber,
			Integer pageSize);

	/**
	 * @Description: TODO   
	 * @author xuzq01
	 * @param riskBlackList 
	 * @date 2016年11月16日
	 */
	public void addBatch(List<RiskBlack> riskBlackList);
	
	/**
	 * @Description: 通过参数获取黑名单列表
	 * @param map
	 * @return
	 * @author zhangkn
	 * @date 2016年11月18日
	 */
	List<RiskBlack> findBlackListByParams(Map<String,Object> map);

	/**
	 * @Description: TODO
	 * @param ids
	 * @param updateUserId
	 * @param updateTime   
	 * @author xuzq01
	 * @date 2016年11月18日
	 */
	public void deleteBatchByIds(List<String> ids, String updateUserId, Date updateTime);
	
	/**
	 * 
	 * @Description: 所有黑名单的充值手机号
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月21日
	 */
	public Set<RiskBlack> findAllBlackMobile();
	
	/**
	 * 
	 * @Description: 所有黑名单设备
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月21日
	 */
	public Set<RiskBlack> findAllBlackDevice();
	
	/**
	 * 
	 * @Description: 所有黑名单支付账号
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月21日
	 */
	public Set<RiskBlack> findAllBlackPayAccount();
	
	/**
	 * 
	 * @Description: 所有黑名单登录账号
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月21日
	 */
	public Set<RiskBlack> findAllBlackLoginAccount();
}
