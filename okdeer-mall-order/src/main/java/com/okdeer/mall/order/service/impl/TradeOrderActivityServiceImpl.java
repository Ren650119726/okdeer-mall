/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @项目名称: yschome-mall 
 * @文件名称: TradeOrderActivityServiceImpl.java 
 * @Date: 2016年3月31日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
package com.okdeer.mall.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.system.entity.PsmsAgent;
import com.okdeer.archive.system.service.IPsmsAgentServiceApi;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.enums.ActivityBelongType;
import com.okdeer.mall.order.service.TradeOrderActivityServiceApi;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsService;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.order.service.TradeOrderActivityService;

/**
 * @pr yschome-mall
 * @author guocp
 * @date 2016年3月31日 下午2:06:29
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderActivityServiceApi")
public class TradeOrderActivityServiceImpl implements TradeOrderActivityService, TradeOrderActivityServiceApi {

	/**
	 * 友门鹿云钱包账户
	 */
	@Value("${yscWalletAccount}")
	private String yscWalletAccount;

	@Autowired
	private ActivityCollectCouponsService activityCollectCouponsService;

	@Autowired
	private ActivityDiscountService activityDiscountService;

	@Reference(version = "1.0.0", check = false)
	private IPsmsAgentServiceApi psmsAgentServiceApi;

	/**
	 * 查询活动创建者userId
	 *
	 * @param order 订单
	 * @throws Exception 
	 */
	@Override
	public String findActivityUserId(TradeOrder order) throws Exception{
		String activityUserId = null;
		if (ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES == order.getActivityType()
				|| ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES == order.getActivityType()) {
			// 满折:店铺；满减活动：店铺、运营商
			ActivityDiscount discount = activityDiscountService.findById(order.getActivityId());
			if (!ActivityCollectCoupons.OPERATOR_CODE.equals(discount.getStoreId())) {
				activityUserId = discount.getStoreId();
			} else {
				// 运营商
				activityUserId = yscWalletAccount;
			}
		} else if (ActivityTypeEnum.VONCHER == order.getActivityType()) {
			// 代金卷：运营商、代理商
			ActivityCollectCoupons coupons = activityCollectCouponsService.get(order.getActivityId());
			// 判断是否代理商发的
			if (!ActivityCollectCoupons.OPERATOR_CODE.equals(coupons.getBelongType())) {
				PsmsAgent agent = psmsAgentServiceApi.selectByPrimaryKey(coupons.getBelongType());
				if (agent != null) {
					activityUserId = agent.getAdminUserId();
				}
			} else {
				// 运营商
				activityUserId = yscWalletAccount;
			}
		}
		return activityUserId;
	}

	/**
	 * 返回活动归属
	 * @throws Exception 
	 */
	public ActivityBelongType findActivityType(TradeOrder order) throws Exception {
		if (ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES == order.getActivityType()
				|| ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES == order.getActivityType()) {
			// 满折:店铺；满减活动：店铺、运营商
			ActivityDiscount discount = activityDiscountService.findById(order.getActivityId());
			if (!ActivityCollectCoupons.OPERATOR_CODE.equals(discount.getStoreId())) {
				return ActivityBelongType.SELLER;
			} else {
				// 运营商
				return ActivityBelongType.OPERATOR;
			}
		} else if (ActivityTypeEnum.VONCHER == order.getActivityType()) {
			// 代金卷：运营商、代理商
			ActivityCollectCoupons coupons = activityCollectCouponsService.get(order.getActivityId());
			// 判断是否代理商发的
			if (!ActivityCollectCoupons.OPERATOR_CODE.equals(coupons.getBelongType())) {
				return ActivityBelongType.AGENT;
			} else {
				// 运营商
				return ActivityBelongType.OPERATOR;
			}
		}
		// 团购、特惠活动、未参加活动返回卖家
		return ActivityBelongType.SELLER;
	}
}
