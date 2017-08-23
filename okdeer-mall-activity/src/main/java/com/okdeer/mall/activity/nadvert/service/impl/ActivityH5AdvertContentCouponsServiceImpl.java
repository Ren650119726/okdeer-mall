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
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContentCoupons;
import com.okdeer.mall.activity.nadvert.mapper.ActivityH5AdvertContentCouponsMapper;
import com.okdeer.mall.activity.nadvert.service.ActivityH5AdvertContentCouponsService;

/**
 * ClassName: ActivityH5AdvertContentCouponsServiceImpl 
 * @Description: H5活动内容>>代金券活动
 * @author mengsj
 * @date 2017年8月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
 
@Service
public class ActivityH5AdvertContentCouponsServiceImpl
		implements ActivityH5AdvertContentCouponsService {
	
	@Autowired
	private ActivityH5AdvertContentCouponsMapper mapper;

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void batchSave(List<ActivityH5AdvertContentCoupons> entitys) throws Exception {
		if(CollectionUtils.isNotEmpty(entitys)){
			entitys.forEach(obj -> {
				obj.setId(UuidUtils.getUuid());
				obj.setCreateTime(new Date());
			});
			mapper.batchSave(entitys);
		}		
	}

	@Override
	public List<ActivityH5AdvertContentCoupons> findByActId(String activityId,
			String contentId) {
		if(StringUtils.isNotBlank(activityId)){
			return mapper.findByActId(activityId, contentId);
		}
		return new ArrayList<>();
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void deleteByActId(String activityId, String contentId)
			throws Exception {
		mapper.deleteByActId(activityId, contentId);
	}

}
