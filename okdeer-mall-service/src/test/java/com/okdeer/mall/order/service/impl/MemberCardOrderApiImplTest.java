package com.okdeer.mall.order.service.impl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;

import com.okdeer.archive.goods.base.service.GoodsNavigateCategoryServiceApi;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.archive.system.service.SysUserLoginLogServiceApi;
import com.okdeer.mall.base.MockUtils;
import com.okdeer.mall.order.api.PlaceOrderApiImplTest;
import com.okdeer.mall.order.dto.MemberCardResultDto;
import com.okdeer.mall.order.dto.MemberTradeOrderDto;
import com.okdeer.mall.order.service.GetPreferentialService;
import com.okdeer.mall.order.service.MemberCardOrderApi;

/**
 * ClassName: TradeOrderRefundsApiImplTest 
 * @Description: 退款接口类退款单元测试用例
 * @author tuzhd
 * @date 2017年7月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@RunWith(Parameterized.class)
public class MemberCardOrderApiImplTest extends BaseServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(PlaceOrderApiImplTest.class);

	@Resource
	public MemberCardOrderApi memberCardOrderApiImpl;
	
	@Mock
	private GoodsNavigateCategoryServiceApi goodsNavigateCategoryServiceApi;
	@Mock
	private SysUserLoginLogServiceApi sysUserLoginLogApi;
	
	
	private MemberTradeOrderDto memberTradeOrderDto;
	private int index;
	
	@Override
	public void initMocks(){
		// mock dubbo服务
		initMockDubbo();
		
	}
	
	private void initMockDubbo(){
		// mock dubbo服务TradeMessageServiceImpl
		GetPreferentialService getPreferentialService = this.applicationContext.getBean(GetPreferentialService.class);
		ReflectionTestUtils.setField(getPreferentialService, "goodsNavigateCategoryServiceApi", goodsNavigateCategoryServiceApi);
	}
	public MemberCardOrderApiImplTest(int index,MemberTradeOrderDto memberTradeOrderDto) {
		this.index= index;
		this.memberTradeOrderDto = memberTradeOrderDto;
	}
	
	@Parameters
	public static Collection<Object[]> initParam() throws Exception {
		Collection<Object[]> initParams = new ArrayList<Object[]>();
		List<List<MemberTradeOrderDto>> paramList = MockUtils
				.getMockData("/com/okdeer/mall/order/service/impl/mock-physical.json", MemberTradeOrderDto.class);
		
		for (int i = 0; i < paramList.size(); i++) {
			for (int j = 0; j < paramList.get(i).size(); j++) {
				initParams.add(new Object[] { j,paramList.get(i).get(j)});
			}
		}
		return initParams;
	}
	
	@Test
	@Rollback(true)
	public void pushMemberCardOrder() throws Exception {
		
		MemberCardResultDto<MemberTradeOrderDto> dto = memberCardOrderApiImpl.pushMemberCardOrder(memberTradeOrderDto);
		
		Assert.assertTrue(ResultCodeEnum.SUCCESS.getCode() == dto.getCode());
	}
	
	@Test
	public void getMemberPayNumber() throws Exception {
//		String dto = memberCardOrderApiImpl.getMemberPayNumber("141102938903bd0f97c9a9694854bd8c", "11212123123");
//		logger.info(dto);
//		Assert.assertNotNull(dto);
	}
	
	
}
