package com.okdeer.mall.activity.coupons.service.receive.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRandCode;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRandCodeMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.coupons.service.receive.AbstractCouponsReceive;
import com.okdeer.mall.activity.coupons.service.receive.bo.CouponsReceiveBo;
import com.okdeer.mall.common.entity.ResultMsg;

@Service
public class GetCouponsReceive extends AbstractCouponsReceive {
	private static final Logger LOGGER = LoggerFactory.getLogger(GetCouponsReceive.class);
	
	/**
	 * 代金券记录
	 */
	@Resource
	private ActivityCouponsRecordMapper activityCouponsRecordMapper;
	
	/**
	 *随机码处理类
	 */
	@Resource
	private ActivityCouponsRandCodeMapper activityCouponsRandCodeMapper;

	
	/**
	 * @Description: 检验优惠码及随机码检查的领取
	 * @param userId 用户id
	 * @param coupons
	 * @author tuzhd
	 * @date 2017年11月24日
	 */
	@Override
	public boolean checkRecordPubilc(ActivityCoupons coupons,String userId,ResultMsg result) {
		boolean tempFlag = super.checkRecordPubilc(coupons, userId, result);
		// 父类校验成功继续校验下面 && 判断该随机码的代金卷是否已经被领取了
		if (tempFlag && StringUtils.isNotBlank(coupons.getRandCode()) && 
				activityCouponsRecordMapper.selectCountByRandCode(coupons.getRandCode()) >= 1) {
			// 已领取
			result.setCode(106);
			result.setMsg("相同随机码的代金券已经被领取了!");
			tempFlag = false;
		}
		return tempFlag;
	}
	
	/**
	 * @Description: 更新随机码表记录
	 * @author tuzhd
	 * @date 2017年11月24日
	 */
	@Override
	public void updateOtherRecode(CouponsReceiveBo bo) {
		// 如果领取成功更新随机码表
		if (StringUtils.isNotBlank(bo.getRandCode())) {
			ActivityCouponsRandCode couponsRandCode = activityCouponsRandCodeMapper.selectByRandCode(bo.getRandCode());
			if (couponsRandCode != null) {
				couponsRandCode.setIsExchange(1);
				couponsRandCode.setUpdateTime(new Date());
				couponsRandCode.setUpdateUserId(bo.getUserId());
				activityCouponsRandCodeMapper.updateByPrimaryKey(couponsRandCode);
			}
		}
	}

}
