
package com.okdeer.mall.member;

import java.util.List;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.common.dto.BaseResultDto;
import com.okdeer.mall.member.points.dto.PointsTeshProductDto;
import com.okdeer.mall.member.points.dto.PointsTeshProductQueryDto;
import com.okdeer.mall.member.points.service.PointsTeshProductApi;

/**
 * ClassName: PointsTeshProductApiTest 
 * @Description: 积分商品单元测试类
 * @author zengjizu
 * @date 2016年12月19日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class PointsTeshProductApiTest extends BaseServiceTest {

	@Autowired
	private PointsTeshProductApi pointsTeshProductApi;

	/**
	 * @Description: 测试查询积分商品列表
	 * @param pointsTeshProductQueryDto 查询参数
	 * @return 积分商品列表
	 * @author zengjizu
	 * @date 2016年12月19日
	 */
	public void testFindList() {
		PointsTeshProductQueryDto pointsTeshProductQueryDto = new PointsTeshProductQueryDto();
		PageUtils<PointsTeshProductDto> page = pointsTeshProductApi.findList(pointsTeshProductQueryDto, 1, 10);
		Assert.assertNotNull(page);
	}

	/**
	 * @Description: 测试查询积分商品列表
	 * @author zengjizu
	 * @date 2016年12月19日
	 */
	public void testFindListForExport() {
		PointsTeshProductQueryDto pointsTeshProductQueryDto = new PointsTeshProductQueryDto();
		List<PointsTeshProductDto> list = pointsTeshProductApi.findList(pointsTeshProductQueryDto);
		Assert.assertNotNull(list);
	}

	/**
	 * @Description: 测试更新积分商品状态
	 * @author zengjizu
	 * @date 2016年12月19日
	 */
	public void testUpdateStatus() {
		List<String> ids = Lists.newArrayList();
		ids.add("");
		try {
			BaseResultDto result = pointsTeshProductApi.updateStatus(ids, 1, "admin");
			Assert.assertNotNull(result);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

	}

	/**
	 * @Description: 测试编辑积分商品
	 * @author zengjizu
	 * @date 2016年12月19日
	 */
	public void testEdit() {
		PointsTeshProductDto pointsTeshProductDto = new PointsTeshProductDto();
		pointsTeshProductDto.setScores(100);
		pointsTeshProductDto.setUpdateUserId("admin");
		try {
			pointsTeshProductApi.edit(pointsTeshProductDto, true);
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * @Description: 测试查询积分商品详情
	 * @author zengjizu
	 * @date 2016年12月19日
	 */
	public void testFindDetail() {
		PointsTeshProductDto pointsTeshProductDto;
		try {
			pointsTeshProductDto = pointsTeshProductApi.findDetail("");
			Assert.assertNotNull(pointsTeshProductDto);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
