/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2016年11月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.chargesetting.api.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.chargesetting.entity.RiskSetting;
import com.okdeer.mall.chargesetting.service.IRiskSettingApi;
import com.okdeer.mall.chargesetting.service.IRiskSettingService;


/**
 * ClassName: RiskSettingApiImpl 
 * @Description: 风控设置apiimpl
 * @author zhangkn
 * @date 2016年11月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version="1.0.0")
public class RiskSettingApiImpl implements IRiskSettingApi {
	
	@Autowired
	IRiskSettingService settingService;

	@Override
	public List<RiskSetting> list(Map<String, Object> params) throws Exception {
		return settingService.list(params);
	}

	@Override
	public void addBatch(List<RiskSetting> settingList,Integer isCoupon) throws Exception {
		settingService.addBatch(settingList,isCoupon);
	}
	
	

}
