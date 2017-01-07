
package com.okdeer.mall.point;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.member.points.dto.AddPointsParamDto;
import com.okdeer.mall.member.points.dto.PointQueryParamDto;
import com.okdeer.mall.member.points.dto.PointQueryResultDto;
import com.okdeer.mall.member.points.enums.PointsRuleCode;
import com.okdeer.mall.member.points.service.PointsApi;

/**
 * ClassName: PointApiTest 
 * @Description: 积分api单元测试类
 * @author zengjizu
 * @date 2016年12月31日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class PointApiTest extends BaseServiceTest {

	@Autowired
	private PointsApi pointsApi;

	/**
	 * @Description: 测试加积分
	 * @author zengjizu
	 * @date 2016年12月31日
	 */
	@Test
	public void testAddPoints() {

		AddPointsParamDto addPointsParamDto = new AddPointsParamDto();
		addPointsParamDto.setBusinessId(UuidUtils.getUuid());
		addPointsParamDto.setPointsRuleCode(PointsRuleCode.APP_CONSUME);
		addPointsParamDto.setUserId("8a80808d57c260880157c2afe51c0030");
		addPointsParamDto.setBusinessType(1);
		addPointsParamDto.setAmount(new BigDecimal("15.5"));
		try {
			pointsApi.addPoints(addPointsParamDto);
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testFindUserPoint() {
		try {
			PointQueryParamDto pointQueryParamDto = new PointQueryParamDto();
			pointQueryParamDto.setAmount(new BigDecimal("100"));
			pointQueryParamDto.setBusinessId(UuidUtils.getUuid());
			pointQueryParamDto.setType(2);
			pointQueryParamDto.setUserId("8a80808d57c260880157c2afe51c0030");
			PointQueryResultDto dto = pointsApi.findUserPoint(pointQueryParamDto);
			Assert.assertTrue(dto != null);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
