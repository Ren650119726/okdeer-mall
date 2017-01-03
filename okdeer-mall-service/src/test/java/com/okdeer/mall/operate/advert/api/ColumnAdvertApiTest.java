package com.okdeer.mall.operate.advert.api;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.mall.advert.dto.ColumnAdvertDto;
import com.okdeer.mall.advert.dto.ColumnAdvertQueryParamDto;
import com.okdeer.mall.advert.enums.AdvertTypeEnum;
import com.okdeer.mall.advert.service.ColumnAdvertApi;
import com.okdeer.mall.base.BaseServiceTest;

/**
 * ClassName: ColumnAdvertApiTest 
 * @Description: 广告api
 * @author zengjizu
 * @date 2017年1月3日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class ColumnAdvertApiTest extends BaseServiceTest{

	@Autowired
	private ColumnAdvertApi advertApi;
	
	@Test
	public void testFindForApp(){
		ColumnAdvertQueryParamDto advertQueryParamDto = new ColumnAdvertQueryParamDto();
		advertQueryParamDto.setAdvertType(AdvertTypeEnum.USER_APP_INDEX_PARTITION);
		advertQueryParamDto.setCityId("10000023");
		advertQueryParamDto.setProvinceId("18");
		List<ColumnAdvertDto> list = advertApi.findForApp(advertQueryParamDto);
		Assert.assertTrue(true);
	}
	
}
