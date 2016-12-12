/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2016年12月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsType;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordBeforeMapper;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeRecord;
import com.okdeer.mall.activity.prize.mapper.ActivityPrizeRecordMapper;
import com.okdeer.mall.activity.prize.service.ActivityPrizeRecordService;
import com.okdeer.mall.system.mapper.SysBuyerUserMapper;

import net.sf.json.JSONObject;

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
	
	/**
	 * 系统会员mapper
	 */
	@Autowired
	private ActivityCouponsRecordBeforeMapper activityCouponsRecordBeforeMapper;
	
	@Autowired
	private SysBuyerUserMapper sysBuyerUserMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return activityPrizeRecordMapper;
	}

	@Override
	public List<ActivityPrizeRecord> findByUserId(String userId) {
		return activityPrizeRecordMapper.findByUserId(userId);
	}

	@Override
	public List<ActivityPrizeRecord> findPrizeRecord() {
		return activityPrizeRecordMapper.findPrizeRecord();
	}

	@Override
	public int findCountByPrizeId(String prizeId) {
		return activityPrizeRecordMapper.findCountByPrizeId(prizeId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject getCoupon(ActivityCouponsRecord activityCouponsRecord) throws ParseException {
		int count = sysBuyerUserMapper.findCountByPhone(activityCouponsRecord.getCollectUserPhone());
		Map<String,Object> map =  new HashMap<String,Object>();
		//不为零说明手机号用户已经存在 为零表示新用户
		if(count>0){
			//未确定返回吗 不返回
			map.put("code", 110);
			map.put("msg", "老用户不能领取本次活动哦，请下载APP参与其他活动吧");
			
		}else{
			Date date = new Date();
			DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date endDate = dateFormat2.parse("2017-01-19 00:00:00");
			activityCouponsRecord.setId(UuidUtils.getUuid());
			activityCouponsRecord.setCollectTime(date);
			activityCouponsRecord.setValidTime(endDate);
			activityCouponsRecord.setStatus(ActivityCouponsRecordStatusEnum.UNUSED);
			activityCouponsRecord.setCollectType(ActivityCouponsType.invite_regist);
			//增加代金券预领取记录
			activityCouponsRecordBeforeMapper.insert(activityCouponsRecord);
			map.put("code", 112);
			map.put("msg", "恭喜你获得30元红包，赶快去体验吧");
		}
		return JSONObject.fromObject(map);
	}

}
