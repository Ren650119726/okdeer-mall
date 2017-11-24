package com.okdeer.mall.activity.coupons.service.receive.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordBeforeMapper;
import com.okdeer.mall.activity.coupons.service.receive.AbstractCouponsReceive;
import com.okdeer.mall.activity.coupons.service.receive.bo.CouponsReceiveBo;
import com.okdeer.mall.common.enums.GetUserType;
import com.okdeer.mall.system.entity.SysUserInvitationCode;
import com.okdeer.mall.system.mapper.SysUserInvitationCodeMapper;
import com.okdeer.mall.system.service.InvitationCodeService;

@Service
public class AdvertCouponsReceive extends AbstractCouponsReceive {
	private static final Logger LOGGER = LoggerFactory.getLogger(AdvertCouponsReceive.class);
	@Autowired
	private InvitationCodeService invitationCodeService;

	/**
	 * 邀请码mapper
	 */
	@Autowired
	private SysUserInvitationCodeMapper sysUserInvitationCodeMapper;
	
	@Autowired
	private ActivityCouponsRecordBeforeMapper activityCouponsRecordBeforeMapper;

	/**
	 * @Description: 插入要求人信息
	 * @param invitaUserId 邀请人信息
	 * @param userId 被邀请人id
	 * @author tuzhd
	 * @date 2017年11月23日
	 */
	@Override
	public void updateOtherRecode(CouponsReceiveBo bo){
		try {
			// 存在邀请人用户id需要确认是否记录邀请人
			if (StringUtils.isNotBlank(bo.getInvitaUserId())) {
				// 根据用户id或用户邀请码，所以InviteUserId 可以存储用户id或邀请码
				List<SysUserInvitationCode> listCode = sysUserInvitationCodeMapper
						.findInvitationByIdCode(bo.getInvitaUserId(), bo.getInvitaUserId());
				// 存在邀请码及添加第一个进去，防止数据库中存在多个
				if (CollectionUtils.isNotEmpty(listCode)) {
					// 内部会判断 不存在邀请记录 则将给该用户userId记录 邀请记录
					invitationCodeService.saveInvatationRecord(listCode.get(0), bo.getUserId(), "");
				}
			}
		} catch (Exception e) {
			LOGGER.error("邀请人信息插入失败,邀请人："+bo.getInvitaUserId()+",被邀请人："+ bo.getUserId(),e);
		}
	}
	
	
	/**
	 * @DESC 校验预领取记录 1、存在未使用的新人代金券则 不能领取返回false 2、持续的活动领取过不能再领取
	 * @param phone 手机号
	 * @param collectId 代金券活动id
	 * @return
	 */
	@Override
	public boolean checkBeforeCoupons(CouponsReceiveBo bo) {
		// 如果领取为限新人使用 则校验是否领取过新人代金券
		ActivityCollectCoupons coll = bo.getColl();
		if (coll.getGetUserType() == GetUserType.ONlY_NEW_USER) {
			// 查询该用户已领取， 新人限制， 未使用，的代金劵活动的代金劵数量
			int hadNewCount = activityCouponsRecordBeforeMapper.countCouponsByType(GetUserType.ONlY_NEW_USER, bo.getPhone(),
					new Date());
			// 存在未使用的新人代金券则 返回true
			if (hadNewCount > 0) {
				return true;
			}
		}
		// 根据代金劵活动id代金劵预领取统计 持续的活动领取过不能再领取
		return activityCouponsRecordBeforeMapper.countCouponsAllId(bo.getPhone(), coll.getId()) > 0;
	}
}
