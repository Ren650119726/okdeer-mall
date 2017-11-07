/** 
 *@Project: okdeer-mall-test 
 *@Author: xuzq01
 *@Date: 2017年9月26日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.base.common.enums.Enabled;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.base.MockUtils;
import com.okdeer.mall.operate.dto.OperateFieldContentDto;
import com.okdeer.mall.operate.dto.OperateFieldDto;
import com.okdeer.mall.operate.dto.OperateFieldsContentDto;
import com.okdeer.mall.operate.dto.OperateFieldsDto;
import com.okdeer.mall.operate.dto.OperateFieldsQueryParamDto;
import com.okdeer.mall.operate.enums.OperateFieldsContentType;
import com.okdeer.mall.operate.enums.OperateFieldsType;
import com.okdeer.mall.operate.operatefields.entity.OperateFields;
import com.okdeer.mall.operate.operatefields.service.OperateFieldsService;
import com.okdeer.mall.operate.service.OperateFieldsApi;

/**
 * ClassName: OperateFieldsApiTest 
 * @Description: 店铺运营栏位测试api
 * @author xuzq01
 * @date 2017年9月26日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Transactional
public class OperateFieldsApiTest extends BaseServiceTest {
	
	@Autowired
	private OperateFieldsApi operateFieldsApi;
	
	@Mock
	private OperateFieldsService operateFieldsService;
	/**
	 * mock运营栏位列表
	 */
	private List<OperateFieldsDto> operateFieldsList;

	private OperateFields operateFields = new OperateFields();
	
	@Before
    public void setUp() throws Exception {
        // 初始化测试用例类中由Mockito的注解标注的所有模拟对象
        MockitoAnnotations.initMocks(this);
        //店铺运营栏位管理对象
        operateFields.setId("094d31dc276411e6aaff00163e010eb1");
        operateFields.setType(OperateFieldsType.STORE);
        operateFields.setBusinessId("2c909084562b72bf01562b72c0090001");
        operateFields.setName("店铺运营栏位测试");
        operateFields.setSort(100);
        
        ReflectionTestUtils.setField(operateFieldsApi, "operateFieldsService", operateFieldsService);
        operateFieldsList = MockUtils
				.getMockData("/com/okdeer/mall/operate/params/mock-store-operate-fields.json", OperateFieldsDto.class).get(0);
    }
	
	@Test
	public void findListWithContentTest() {
		OperateFieldsQueryParamDto queryParamDto = new OperateFieldsQueryParamDto();
		//业务id(type=0时为cityId,type=1时为0，type=2时为店铺id) 开心小卖部
		queryParamDto.setBusinessId("0");
		//0:城市运营栏位1:默认运营栏位2:店铺运营栏位
		queryParamDto.setType(OperateFieldsType.DEFAULT);
		queryParamDto.setEnabled(Enabled.NO);
		List<OperateFieldsDto> list = operateFieldsApi.findListWithContent(queryParamDto);
		assertNotNull(list);
	}

	@Test
	public void findListTest() {
		OperateFieldsQueryParamDto pp = new OperateFieldsQueryParamDto();
		pp.setType(OperateFieldsType.DEFAULT);
		pp.setBusinessId("0");
		pp.setEnabled(Enabled.NO);
		List<OperateFieldsDto> list = operateFieldsApi.findList(pp);
		assertNotNull(list);
	}
	
	@Test
	@Rollback(true)
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void updateSortUpTest() throws Exception{
		when(operateFieldsService.findById(operateFields.getId())).thenReturn(operateFields);
		//无返回值
		operateFieldsApi.updateSort("094d31dc276411e6aaff00163e010eb1", true);
		Assert.assertTrue(true);
	}
	
	@Test
	@Rollback(true)
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void updateSortDownTest() throws Exception{
		when(operateFieldsService.findById(operateFields.getId())).thenReturn(operateFields);
		//无返回值
		operateFieldsApi.updateSort("094d31dc276411e6aaff00163e010eb1", false);
		Assert.assertTrue(true);
	}
	
	///////app接口
	@Test
	public void initStoreOperateFieldDataTest() throws Exception {
		List<OperateFieldDto> storeFieldList = operateFieldsApi.initStoreOperateFieldData(operateFieldsList.get(0).getBusinessId());
		Assert.assertNotNull(ResultCodeEnum.SUCCESS.getDesc(),storeFieldList);
	}
	
	@Test
	public void initCityOperateFieldDataTest() throws Exception {
		List<OperateFieldDto> cityFieldList = operateFieldsApi.initCityOperateFieldData(operateFieldsList.get(1).getBusinessId());
		Assert.assertNotNull(ResultCodeEnum.SUCCESS.getDesc(),cityFieldList);
	}
	
	@Test
	public void initOperationFieldTest() throws Exception {
		operateFieldsApi.initOperationField(operateFieldsList.get(0).getBusinessId());
		Assert.assertTrue(true);
	}
	
	@Test
	public void initOperationFieldContextTest() throws Exception {
		operateFieldsApi.initOperationFieldContext(operateFieldsList.get(0).getBusinessId());
		Assert.assertTrue(true);
	}
	
	@Test
	public void getSingleGoodsOfOperateFieldTest() throws Exception {
		String goodsId = "";
		String storeId = "";
		for(OperateFieldsDto dto : operateFieldsList){
			List<OperateFieldsContentDto> operateFieldscontentDtoList = dto.getOperateFieldscontentDtoList();
			if(CollectionUtils.isNotEmpty(operateFieldscontentDtoList)){
				for(OperateFieldsContentDto content : operateFieldscontentDtoList){
					//栏位内容类型为单品
					if(content.getType() == OperateFieldsContentType.SINGLE_GOODS){
						//类型为单品时保存商品id
						goodsId = content.getBusinessId();
						//类型为店铺运营栏位保存的是店铺id
						storeId = dto.getBusinessId();
						break;
					}
				}
				
			}
		}
		OperateFieldContentDto fieldContentDto = new OperateFieldContentDto();
		fieldContentDto.setStoreId("2c909084562b72bf01562b72c0090001");
		given(operateFieldsService.getSingleGoodsOfOperateField(any(),any())).willReturn(fieldContentDto);
		OperateFieldContentDto contentDto = operateFieldsApi.getSingleGoodsOfOperateField(goodsId, storeId);
		Assert.assertNotNull(ResultCodeEnum.SUCCESS.getDesc(),contentDto);
	}
	
	@Test
	public void getGoodsOfStoreActivityFieldTest() throws Exception {
		String storeId= operateFieldsList.get(0).getBusinessId();
		//0或者1 0:特惠活动 1:低价活动
		int businessType= 0;
		int template= 10;
		//排序规则  0 价格从高到低  1 排序值从高到低 2 价格从低到高  3排序值从低到高 
		int sortType= 1;
		int sort= 1;
		List<OperateFieldContentDto> storeFiledContentList = 
				operateFieldsApi.getGoodsOfStoreActivityField(storeId, businessType, template, sortType, sort);
		Assert.assertNotNull(ResultCodeEnum.SUCCESS.getDesc(),storeFiledContentList);
	}
}
