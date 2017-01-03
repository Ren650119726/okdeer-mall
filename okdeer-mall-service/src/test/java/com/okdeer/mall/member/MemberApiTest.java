
package com.okdeer.mall.member;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.member.member.dto.SysBuyerExtDto;
import com.okdeer.mall.member.member.service.MemberApi;

/**
 * ClassName: MemberApiTest 
 * @Description: 会员api测试类
 * @author zengjizu
 * @date 2017年1月3日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class MemberApiTest extends BaseServiceTest {

	@Autowired
	private MemberApi memberApi;
	
	/**
	 * @Description: 测试查询用户扩展信息
	 * @author zengjizu
	 * @date 2017年1月3日
	 */
	@Test
	public void testFindSysUserExtInfo() {
		String userId = "8a80808d57c260880157c2afe51c0030";
		try {
			SysBuyerExtDto dto = memberApi.findSysUserExtInfo(userId);
			Assert.assertTrue(dto != null);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
