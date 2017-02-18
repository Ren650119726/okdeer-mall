package com.okdeer.mall.activity.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.springframework.stereotype.Service;

import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.mall.activity.service.MaxFavourStrategy;
import com.okdeer.mall.order.vo.Coupons;
import com.okdeer.mall.order.vo.Discount;
import com.okdeer.mall.order.vo.Favour;
import com.okdeer.mall.order.vo.FullSubtract;

/**
 * ClassName: GenericMaxFavourStrategyImpl 
 * @Description: 通用最大优惠策略
 * @author maojj
 * @date 2017年2月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.1 			2017年2月15日				maojj		    通用最大优惠策略
 */
@Service("genericMaxFavourStrategy")
public class GenericMaxFavourStrategyImpl implements MaxFavourStrategy {
	
	/**
	 * 价格格式化标准
	 */
	private static final DecimalFormat df = new DecimalFormat("0000.00");
	
	/**
	 * 2050年1月1日的时间值。用于控制时间的正序或者倒序
	 */
	private static final long MAX_TIME = DateUtils.parseDate("2050-01-01 00:00:00").getTime();
	
	/**
	 * 时间格式化规则
	 */
	private static final String timeDiffFormat = "%014d";
	
	/**
	 * 最大上限值。用于控制一些规则属性的正序或者倒序
	 */
	private static final int MAX_VALUE = 9;

	/**
	 * 计算当前优惠优先规则：1位（首单用户限制：0：否，1：是） + 6位优惠金额（4,2。左侧不足补0） 
	 * 			 +  1位优惠类型（9-优惠类型序列） + 1位优惠来源（9-来源类型（0：平台，1：店铺））
	 * 			 +  13位距离2050年1月1日的时间差（左侧不足补0.）
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.MaxFavourStrategy#calMaxFavourRule(com.okdeer.mall.order.vo.Favour)
	 */
	@Override
	public String calMaxFavourRule(Favour favour,BigDecimal totalAmount) {
		// 首单用户限制
		int firstUserLimit = favour.getFirstUserLimit();
		// 优惠金额
		String favourAmount = "0.0";
		// 优惠类型
		int activityType = favour.getActivityType();
		// 优惠来源
		int favourSource = 0;
		// 优惠到期时间
		long expireTime = DateUtils.parseDate(favour.getIndate()).getTime();
		switch(activityType){
			case 1:
				// 代金券.代金券只能由平台发起
				Coupons coupons = (Coupons)favour;
				favourAmount = coupons.getCouponPrice();
				break;
			case 2:
				// 满减
				FullSubtract fullSubtract = (FullSubtract)favour;
				favourAmount = fullSubtract.getFullSubtractPrice();
				favourSource = fullSubtract.getType();
				break;
			case 3:
				// 满折
				Discount discount = (Discount)favour;
				favourAmount = totalAmount.subtract(totalAmount.multiply(new BigDecimal(discount.getDiscountPrice())).divide(BigDecimal.valueOf(10)).setScale(2,
						BigDecimal.ROUND_DOWN)).toString();
				break;
			default: break;
		}
		
		StringBuilder rule = new StringBuilder();
		rule.append(firstUserLimit)
		    .append(df.format(Double.parseDouble(favourAmount)))
			.append(MAX_VALUE - activityType)
			.append(MAX_VALUE - favourSource)
			.append(String.format(timeDiffFormat, MAX_TIME - expireTime));
		return rule.toString();
	}

}
