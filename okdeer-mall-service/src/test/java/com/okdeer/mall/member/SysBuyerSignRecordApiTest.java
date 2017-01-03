
package com.okdeer.mall.member;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.member.member.dto.SignResultDto;
import com.okdeer.mall.member.member.service.SysBuyerSignRecordApi;

/**
 * ClassName: SysBuyerSignRecordApiTest 
 * @Description: 用户签到api
 * @author zengjizu
 * @date 2017年1月3日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class SysBuyerSignRecordApiTest extends BaseServiceTest {

	@Autowired
	private SysBuyerSignRecordApi sysBuyerSignRecordApi;

	/**
	 * @Description: 测试签到
	 * @author zengjizu
	 * @date 2017年1月3日
	 */
	@Test
	public void testSign() {
		String userId = "8a80808d57c260880157c2afe51c0030";
		try {
			SignResultDto signResultDto = sysBuyerSignRecordApi.sign(userId);
			System.out.println(JsonMapper.nonDefaultMapper().toJson(signResultDto));
			Assert.assertTrue(signResultDto != null);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
