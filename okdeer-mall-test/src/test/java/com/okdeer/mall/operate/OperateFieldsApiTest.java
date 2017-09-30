/** 
 *@Project: okdeer-mall-test 
 *@Author: xuzq01
 *@Date: 2017年9月26日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.operate.dto.OperateFieldsDto;
import com.okdeer.mall.operate.dto.OperateFieldsQueryParamDto;
import com.okdeer.mall.operate.enums.OperateFieldsType;
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
	
	private OperateFieldsDto operateFieldsDto= new OperateFieldsDto();
	
	@Before
    public void setUp() throws Exception {
        // 初始化测试用例类中由Mockito的注解标注的所有模拟对象
        MockitoAnnotations.initMocks(this);
       //ReflectionTestUtils.setField(cancelOrderApi, "tradeorderService", tradeorderService);
       //ReflectionTestUtils.setField(cancelOrderApi, "cancelOrderService", cancelOrderService);
    }
	
	@Test
	public void findListWithContentCityIdTest() {
		OperateFieldsQueryParamDto queryParamDto = new OperateFieldsQueryParamDto();
		//业务id(type=0时为cityId,type=1时为0，type=2时为店铺id) 开心小卖部
		queryParamDto.setBusinessId("291");
		//0:城市运营栏位1:默认运营栏位2:店铺运营栏位
		queryParamDto.setType(OperateFieldsType.CITY);
		List<OperateFieldsDto> list = operateFieldsApi.findListWithContent(queryParamDto);
		assertNotNull(list);
	}
	@Test
	public void findListWithContentOperateTest() {
		OperateFieldsQueryParamDto queryParamDto = new OperateFieldsQueryParamDto();
		//业务id(type=0时为cityId,type=1时为0，type=2时为店铺id) 开心小卖部
		queryParamDto.setBusinessId("0");
		//0:城市运营栏位1:默认运营栏位2:店铺运营栏位
		queryParamDto.setType(OperateFieldsType.DEFAULT);
		List<OperateFieldsDto> list = operateFieldsApi.findListWithContent(queryParamDto);
		assertNotNull(list);
	}
	@Test
	public void findListWithContentStoreIdTest() {
		OperateFieldsQueryParamDto queryParamDto = new OperateFieldsQueryParamDto();
		//业务id(type=0时为cityId,type=1时为0，type=2时为店铺id) 开心小卖部
		queryParamDto.setBusinessId("5592971b276511e6aaff00163e010eb1");
		//0:城市运营栏位1:默认运营栏位2:店铺运营栏位
		queryParamDto.setType(OperateFieldsType.STORE);
		List<OperateFieldsDto> list = operateFieldsApi.findListWithContent(queryParamDto);
		assertNotNull(list);
	}
	@Test
	public void findByIdTest() throws Exception {
		
		String id="3fcfa3de8c5a11e79f170050569e35a5";
		OperateFieldsDto operateFieldsDto = operateFieldsApi.findById(id);
		assertNotNull(operateFieldsDto);
	}
	@Test
	public void findListCityIdTest() {
		OperateFieldsQueryParamDto pp = new OperateFieldsQueryParamDto();
		pp.setType(OperateFieldsType.CITY);
		pp.setBusinessId("291");
		List<OperateFieldsDto> list = operateFieldsApi.findList(pp);
		assertNotNull(list);
	}
	@Test
	public void findListOperateTest() {
		OperateFieldsQueryParamDto pp = new OperateFieldsQueryParamDto();
		pp.setType(OperateFieldsType.DEFAULT);
		pp.setBusinessId("0");
		List<OperateFieldsDto> list = operateFieldsApi.findList(pp);
		assertNotNull(list);
	}
	@Test
	public void findListStoreIdTest() {
		OperateFieldsQueryParamDto pp = new OperateFieldsQueryParamDto();
		pp.setType(OperateFieldsType.STORE);
		pp.setBusinessId("5592971b276511e6aaff00163e010eb1");
		List<OperateFieldsDto> list = operateFieldsApi.findList(pp);
		assertNotNull(list);
	}
	
	@Test
	public void saveTest() {
		operateFieldsDto.setCreateUserId("1");
		// 上传头图，获得图片路径
		//operateFieldsDto.setHeadPic(uploadFile(headPicFile));
		//operateFieldsApi.save(operateFieldsDto, operateFieldscontentList);

	}
	
	@Test
	public void updateTest(){
		operateFieldsDto.setUpdateUserId("1");
		// 上传头图，获得图片路径
		//operateFieldsDto.setHeadPic(uploadFile(headPicFile));
		//operateFieldsApi.update(operateFieldsDto, operateFieldscontentList);
		
	}
	@Test
	public void updateSortUpTest() throws Exception{
		String id="3fcfa3de8c5a11e79f170050569e35a5";
		operateFieldsApi.updateSort(id, true);
		
	}
	
	@Test
	public void updateSortDownTest() throws Exception{
		String id="3fcfa3de8c5a11e79f170050569e35a5";
		operateFieldsApi.updateSort(id, false);
		
	}
	@Test
	public void update2Test(){
		operateFieldsDto.setUpdateUserId("1");
		// 上传头图，获得图片路径
		//operateFieldsDto.setHeadPic(uploadFile(headPicFile));
		//operateFieldsApi.update(operateFieldsDto, operateFieldscontentList);
		//int update(operateFieldsDto) throws Exception;
	}
	
	///////app接口
	@Test
	public void initStoreOperateFieldDataTest() {

	}
	
	@Test
	public void initCityOperateFieldDataTest() {

	}
	
	@Test
	public void initOperationFieldTest() {

	}
	
	@Test
	public void initOperationFieldContextTest() {

	}
	
	
	@Test
	public void getSingleGoodsOfOperateFieldTest() {

	}
	
	@Test
	public void getGoodsOfStoreActivityFieldTest() {

	}
}
