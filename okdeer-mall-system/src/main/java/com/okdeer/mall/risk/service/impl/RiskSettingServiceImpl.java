/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2016年11月4日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.service.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.mall.risk.entity.RiskSetting;
import com.okdeer.mall.risk.entity.RiskSettingDetail;
import com.okdeer.mall.risk.mapper.RiskSettingDetailMapper;
import com.okdeer.mall.risk.mapper.RiskSettingMapper;
import com.okdeer.mall.risk.service.RiskSettingService;

/**
 * ClassName: RiskSettingServiceImpl 
 * @Description: 风控设置
 * @author zhangkn
 * @date 2016年11月16日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class RiskSettingServiceImpl implements RiskSettingService{

	@Autowired
	RiskSettingMapper settingMapper;
	@Autowired
	RiskSettingDetailMapper detailMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addBatch(List<RiskSetting> settingList,Integer isCoupon) throws Exception{
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("isCoupon", isCoupon);
		List<RiskSetting> oldList = settingMapper.list(params);
		if(CollectionUtils.isNotEmpty(oldList)){
			//先删除detail老数据
			for(RiskSetting setting : oldList){
				detailMapper.deleteBySettingId(setting.getId());
			}
		}
		//再删除主表setting数据
		settingMapper.deleteByIsCoupon(isCoupon);
		
		//删除完了再批量添加
		if(CollectionUtils.isNotEmpty(settingList)){
			//detail表删除记录
			for(RiskSetting setting : settingList){
				detailMapper.deleteBySettingId(setting.getId());
			}
			
			//批量插入新纪录,(后台功能,使用频率非常少,数据量也不大,循环插入性能也不会有多大问题)
			for(RiskSetting setting : settingList){
				settingMapper.add(setting);
				
				List<RiskSettingDetail> detailList = setting.getDetailList();
				if(CollectionUtils.isNotEmpty(detailList)){
					for(RiskSettingDetail detail : detailList){
						detailMapper.add(detail);
					}
				}
			}
		}
	}
	
	@Override
	public <Entity> int add(Entity entity) throws Exception {
		return 0;
	}

	@Override
	public <Entity> int update(Entity entity) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(String id) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <Entity> Entity findById(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RiskSetting> list(Map<String, Object> params) throws Exception {
		//风控setting列表以及每个setting的明细
		List<RiskSetting> settingList = settingMapper.list(params);
		if(CollectionUtils.isNotEmpty(settingList)){
			for(RiskSetting setting : settingList){
				setting.setDetailList(detailMapper.listBySettingId(setting.getId()));
			}
		}
		return settingList;
	}
	
}
