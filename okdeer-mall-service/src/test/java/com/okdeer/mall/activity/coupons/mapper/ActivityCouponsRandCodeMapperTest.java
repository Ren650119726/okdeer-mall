package com.okdeer.mall.activity.coupons.mapper;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.okdeer.mall.Application;

/**
 * ClassName: ActivityCouponsRandCodeMapperTest 
 * @Description: 代金券随机码Mapper测试用例
 * @author maojj
 * @date 2016年10月25日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构1.1			2016年10月25日				maojj		代金券随机码Mapper测试用例
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ActivityCouponsRandCodeMapperTest {

	@Resource
	private ActivityCouponsRandCodeMapper activityCouponsRandCodeMapper;
	
	@Test
	public void testFindExistCodeSet() {
		Set<String> randCodeSet = new HashSet<String>();
		randCodeSet.add("abcdefgh");
		Set<String> findResult = activityCouponsRandCodeMapper.findExistCodeSet(randCodeSet);
		System.out.println(findResult.size());
	}

}
