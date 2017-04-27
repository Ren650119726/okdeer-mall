/** 
 *@Project: yschome-mall-service 
 *@Author: maojj
 *@Date: 2016年8月3日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.coupons.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.okdeer.mall.Application;
import com.okdeer.mall.activity.bo.FavourParamBO;
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
		FavourParamBO paramBo = new FavourParamBO();
		paramBo.setUserId("141102938903bd0f97c9a9694854bd8c");
		paramBo.setStoreId("141102938903bd0f97c9a9694854bd8c");
		paramBo.setTotalAmount(BigDecimal.valueOf(510));
		
		List<Coupons> coupons = activityCouponsRecordMapper.findValidCoupons(paramBo);
		System.out.println(">>>>>>>>>>>>" + JSONArray.fromObject(coupons));
		for(Coupons bean : coupons) {
			System.out.println(bean.getCouponPrice());
		}
	}

	@Test
	public void testFindTotalRewardAmount(){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userId", "8a94e4cb57d2c9e50157d2c9e5c50000");
		params.put("activityId", "8a8080a057d17cdc0157d17cdc370000");
		params.put("limitDate", new Date());
		Integer total = activityCouponsRecordMapper.findTotalRewardAmount(params);
		System.out.println("total>>>>>>>>>>>" + total);
	}
}
