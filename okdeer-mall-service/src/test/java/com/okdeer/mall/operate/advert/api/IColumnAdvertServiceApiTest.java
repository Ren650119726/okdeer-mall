
package com.okdeer.mall.operate.advert.api;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.mall.advert.entity.AdvertGoodsVo;
import com.okdeer.mall.advert.service.IColumnAdvertServiceApi;
import com.okdeer.mall.base.BaseServiceTest;

/**
 * ClassName: IColumnAdvertServiceApiTest 
 * @Description: 广告服务测试类
 * @author zengjizu
 * @date 2016年11月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *   v1.2.0             2016-11-08           zengjz           增加testUpdateSort测试方法
 */
public class IColumnAdvertServiceApiTest extends BaseServiceTest {

	@Autowired
	private IColumnAdvertServiceApi advertServiceApi;

	
	//@Test
	public void testUpdateSort() {
		String id = "8a2863a556c28acc0156c2a7efcb00d7";
		int sort = 10;
		try {
			advertServiceApi.updateSort(id, sort);
			Assert.assertTrue(true);
		} catch (Exception e) {
			Assert.fail("设置广告排序出错");
		}

	}
	@Test
	public void findAdvertGoodsByAdvertIdTest() {
		String advertId = "100002";
		String storeId = "8a284fd056c2991b0156c33f1e530259";
		List<AdvertGoodsVo> voList = advertServiceApi.findAdvertGoodsByAdvertId(advertId,storeId);
		Assert.assertTrue(voList.size()>0);

	}
}
