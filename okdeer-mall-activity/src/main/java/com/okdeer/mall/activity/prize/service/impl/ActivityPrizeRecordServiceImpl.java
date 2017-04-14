/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2016年12月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeRecord;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeRecordVo;
import com.okdeer.mall.activity.prize.mapper.ActivityPrizeRecordMapper;
import com.okdeer.mall.activity.prize.service.ActivityPrizeRecordService;

/**
 * ClassName: ActivityPrizeRecordApiImpl 
 * @Description: 中奖记录表Service实现类
 * @author xuzq01
 * @date 2016年12月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2.3			2016年12月8日		xuzq01				中奖记录表Service实现类
 */
@Service
public class ActivityPrizeRecordServiceImpl extends BaseServiceImpl implements ActivityPrizeRecordService{
	
	/**
	 * 中奖记录表mapper
	 */
	@Autowired
	ActivityPrizeRecordMapper activityPrizeRecordMapper;
	
	
	@Override
	public IBaseMapper getBaseMapper() {
		return activityPrizeRecordMapper;
	}

	/**
	 * @Description: TODO
	 * @param userId
	 * @param activityId 活动id H5活动id 以后会是对应
	 * @return   
	 * @return List<ActivityPrizeRecord>  
	 * @throws
	 * @author tuzhd
	 * @date 2016年12月15日
	 */
	@Override
	public List<ActivityPrizeRecordVo> findByUserId(String userId,String activityId) {
		return activityPrizeRecordMapper.findByUserId(userId,activityId);
	}

	@Override
	public List<ActivityPrizeRecord> findPrizeRecord() {
		return activityPrizeRecordMapper.findPrizeRecord();
	}

	@Override
	public int findCountByPrizeId(String prizeId) {
		return activityPrizeRecordMapper.findCountByPrizeId(prizeId);
	}
	
	/**
	 * 写入代金劵记录 当为抽奖插入奖品id
	 * 否则写入代金劵id
	 * (non-Javadoc)
	 */
	@Override
	public int addPrizeRecord(String collectId,String userId,String activityId,String prizeId) {
		ActivityPrizeRecord rec = new ActivityPrizeRecord();
		rec.setId(UuidUtils.getUuid());
		rec.setPrizeId(prizeId);
		rec.setActivityCollectId(collectId);
		rec.setUserId(userId);
		rec.setLuckDrawId(activityId);
		rec.setCreateTime(new Date());
		rec.setIsOffer(0);
		rec.setDisabled(Disabled.valid);
		return activityPrizeRecordMapper.add(rec);
	}

	@Override
	public PageUtils<ActivityPrizeRecordVo> findPrizeRecordList(ActivityPrizeRecordVo activityPrizeRecordVo,
			int pageNumber, int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ActivityPrizeRecordVo> result = activityPrizeRecordMapper.findPrizeRecordList(activityPrizeRecordVo);
		return new PageUtils<ActivityPrizeRecordVo>(result);
	}

}
