package com.okdeer.mall.activity.nadvert.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertRole;
import com.okdeer.mall.activity.nadvert.mapper.ActivityH5AdvertRoleMapper;
import com.okdeer.mall.activity.nadvert.service.ActivityH5AdvertRoleService;

/**
 * ClassName: ActivityH5AdvertRoleServiceImpl 
 * @Description: h5活动规则管理
 * @author mengsj
 * @date 2017年8月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
 
@Service
public class ActivityH5AdvertRoleServiceImpl
		implements ActivityH5AdvertRoleService {
	
	@Autowired
	private ActivityH5AdvertRoleMapper mapper;

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void saveBatch(List<ActivityH5AdvertRole> entitys) throws Exception {
		if(CollectionUtils.isNotEmpty(entitys)){
			List<ActivityH5AdvertRole> temps = new ArrayList<ActivityH5AdvertRole>();
			entitys.forEach(obj -> {
				if(StringUtils.isNotBlank(obj.getContent())){
					obj.setId(UuidUtils.getUuid());
					temps.add(obj);
				}
			});
			mapper.batchSave(temps);
		}
	}

	@Override
	public List<ActivityH5AdvertRole> findByActId(String activityId) {
		if(StringUtils.isNotBlank(activityId)){
			return mapper.findByActId(activityId);
		}
		return new ArrayList<ActivityH5AdvertRole>();
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void deleteByActId(String activityId) throws Exception {
		if(StringUtils.isNotBlank(activityId)){
			mapper.deleteByActId(activityId);
		}
	}
}
