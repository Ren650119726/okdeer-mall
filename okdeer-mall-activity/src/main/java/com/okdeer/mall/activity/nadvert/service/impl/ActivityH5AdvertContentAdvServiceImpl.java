package com.okdeer.mall.activity.nadvert.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContentAdv;
import com.okdeer.mall.activity.nadvert.mapper.ActivityH5AdvertContentAdvMapper;
import com.okdeer.mall.activity.nadvert.service.ActivityH5AdvertContentAdvService;

/**
 * ClassName: ActivityH5AdvertContentAdvServiceImpl 
 * @Description: H5活动内容>>广告图片
 * @author mengsj
 * @date 2017年8月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

@Service
public class ActivityH5AdvertContentAdvServiceImpl
		implements ActivityH5AdvertContentAdvService {
	
	@Autowired
	private ActivityH5AdvertContentAdvMapper mapper;

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void batchSave(List<ActivityH5AdvertContentAdv> entitys)
			throws Exception {
		if(CollectionUtils.isNotEmpty(entitys)){
			entitys.forEach(obj -> {
				obj.setId(UuidUtils.getUuid());
				obj.setCreateTime(new Date());
			});
			mapper.batchSave(entitys);
		}
	}

	@Override
	public List<ActivityH5AdvertContentAdv> findByActId(String activityId,
			String contentId) {
		if(StringUtils.isNotBlank(activityId)){
			return mapper.findByActId(activityId, contentId);
		}
		return new ArrayList<>();
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void deleteByActId(String activityId, String contentId) throws Exception {
		mapper.deleteByActId(activityId, contentId);
	}
}
