/** 
 *@Project: yschome-mall-service 
 *@Author: maojj
 *@Date: 2016年8月3日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.coupons.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.okdeer.mall.Application;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.order.vo.Coupons;
import com.okdeer.mall.order.vo.Discount;
import com.okdeer.mall.order.vo.FullSubtract;
import com.okdeer.mall.system.mapper.SysUserInvitationCodeMapper;

import net.sf.json.JSONArray;


/**
 * ClassName: ActivityCouponsRecordMapperTest 
 * @Description: 查询用户有效的代金券测试用例
 * @author maojj
 * @date 2016年8月3日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *	   	 重构V4.1       2016-08-03			maojj			查询用户有效的代金券测试用例
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ActivityCouponsRecordMapperTest {
	@Resource 
	SysUserInvitationCodeMapper mapper;
	
	@Resource
	ActivityCouponsRecordMapper activityCouponsRecordMapper;

	@Resource
	ActivityDiscountMapper activityDiscountMapper;
	/**
	 * Test method for {@link com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper#findValidCoupons(java.util.Map)}.
	 */
	@Test
	public void testFind(){
		mapper.findByQueryVo(null);
	}
	
	@Test
	public void testFindValidCoupons() {
		Map<String,Object> queryCondition = new HashMap<String,Object>();
		queryCondition.put("userId","141102938903bd0f97c9a9694854bd8c");
		queryCondition.put("storeId", "141102938903bd0f97c9a9694854bd8c");
		queryCondition.put("totalAmount", 510);
		
		List<Coupons> coupons = activityCouponsRecordMapper.findValidCoupons(queryCondition);
		System.out.println(">>>>>>>>>>>>" + JSONArray.fromObject(coupons));
		for(Coupons bean : coupons) {
			System.out.println(bean.getCouponPrice());
		}
	}

	@Test
	public void testFindValidDiscount() {
		Map<String,Object> queryCondition = new HashMap<String,Object>();
		queryCondition.put("userId","8a94e4dd55df05130155df0999f80004");
		queryCondition.put("storeId", "8a94e4dd55df05130155df0999f80004");
		queryCondition.put("totalAmount", 510);
		
		List<Discount> discountList = activityDiscountMapper.findValidDiscount(queryCondition);
		System.out.println(">>>>>>>>>>>>" + JSONArray.fromObject(discountList));
	}
	
	@Test
	public void testFindValidFullSubtract() {
		Map<String,Object> queryCondition = new HashMap<String,Object>();
		queryCondition.put("userId","8a94e4dd55df05130155df0999f80004");
		queryCondition.put("storeId", "2c91c0865639a2f2015639b10d800039");
		queryCondition.put("totalAmount", 510);
		
		List<FullSubtract> discountList = activityDiscountMapper.findValidFullSubtract(queryCondition);
		System.out.println(">>>>>>>>>>>>" + JSONArray.fromObject(discountList));
	}
}
