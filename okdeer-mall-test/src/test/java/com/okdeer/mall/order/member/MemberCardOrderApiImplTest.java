package com.okdeer.mall.order.member;

import static org.mockito.BDDMockito.given;

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
import org.springframework.test.util.AopTestUtils;
import org.springframework.test.util.ReflectionTestUtils;

import com.okdeer.base.common.enums.CommonResultCodeEnum;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.redis.IRedisTemplateWrapper;
import com.okdeer.jxc.bill.service.HykPayOrderServiceApi;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.base.MockUtils;
import com.okdeer.mall.common.utils.TradeNumUtil;
import com.okdeer.mall.order.dto.MemberCardResultDto;
import com.okdeer.mall.order.dto.MemberTradeOrderDto;
import com.okdeer.mall.order.dto.PayInfoParamDto;
import com.okdeer.mall.order.handler.MemberCardOrderService;
import com.okdeer.mall.order.service.MemberCardOrderApi;
/**
 * ClassName: MemberCardOrderApiImplTest 
 * @Description: 会员卡订单同步测试类
 * @author tuzhd
 * @date 2017年8月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V2.5.2			2017-08-08			tuzhd				会员卡订单同步测试类
 */
@RunWith(Parameterized.class)
public class MemberCardOrderApiImplTest  extends BaseServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(MemberCardOrderApiImplTest.class);
	@Resource
	public MemberCardOrderApi memberCardOrderApi;
	private int index;
	private MemberTradeOrderDto memberTradeOrderDto;
	@Mock
	HykPayOrderServiceApi hykPayOrderServiceApi;
	
	@Resource
	private IRedisTemplateWrapper<String, Object> redisTemplateWrapper;
	/**
	 * 会员卡订单缓存后缀
	 */
	private String orderKeyStr = ":memberCardOrder";
	
	@Override
	public void initMocks(){
		// mock dubbo服务
		initMockDubbo();
		
	}
	
	private void initMockDubbo(){
		// mock dubbo服务
		MemberCardOrderService memberCardOrderServiceImpl =  AopTestUtils.getTargetObject(this.applicationContext.getBean("memberCardOrderServiceImpl"));
		ReflectionTestUtils.setField(memberCardOrderServiceImpl, "hykPayOrderServiceApi", hykPayOrderServiceApi);
	}
	public MemberCardOrderApiImplTest(int index,MemberTradeOrderDto memberTradeOrderDto) {
		this.index= index;
		this.memberTradeOrderDto = memberTradeOrderDto;
	}
	
	@Parameters
	public static Collection<Object[]> initParam() throws Exception {
		Collection<Object[]> initParams = new ArrayList<Object[]>();
		List<List<MemberTradeOrderDto>> paramList = MockUtils
				.getMockData("/com/okdeer/mall/order/member/params/mock-member.json", MemberTradeOrderDto.class);
		for (int i = 0; i < paramList.size(); i++) {
			for (int j = 0; j < 1; j++) {
				initParams.add(new Object[] { j,paramList.get(i).get(j)});
			}
		}
		return initParams;
	}
	 

	
	/**
	 * @Description: 获取会员卡信息接口
	 * @param userId  用户id
	 * @param deviceId 设备id
	 * @author tuzhd
	 * @throws Exception 
	 * @date 2017年8月9日
	 */
	@Test
	@Rollback(true)
	public void getMemberPayNumber() throws Exception{
		//1、生成
		String memberPayNum = memberCardOrderApi.getMemberPayNumber("141577260798e5eb9e1b8a0645b486c7", "Test_dev01");
		//2、根据number获得用户信息
		String userId = memberCardOrderApi.getUserIdByMemberCard(memberPayNum+"1");
		Assert.assertTrue("141577260798e5eb9e1b8a0645b486c7".equals(userId));
		String orderId = UuidUtils.getUuid();
		
		memberTradeOrderDto.setOrderId(orderId);
		memberTradeOrderDto.setOrderNo("TEST"+TradeNumUtil.getTradeNum());
		memberTradeOrderDto.getList().forEach(e->{
			e.setOrderId(orderId);
			e.setId(orderId);
		});
		//4、推送改会员订单
		memberCardOrderApi.pushMemberCardOrder(memberTradeOrderDto);
		
		MemberTradeOrderDto order = (MemberTradeOrderDto) redisTemplateWrapper.get(orderId + orderKeyStr);
		MemberCardResultDto<MemberTradeOrderDto> resultDto = new MemberCardResultDto<>();
		resultDto.setCode(CommonResultCodeEnum.SUCCESS.getCode());
		MemberTradeOrderDto newOr = BeanMapper.map(order, MemberTradeOrderDto.class);
		newOr.setTradeNum(TradeNumUtil.getTradeNum());
		resultDto.setData(newOr);
		resultDto.setMessage(CommonResultCodeEnum.SUCCESS.getDesc());
		given(hykPayOrderServiceApi.readyPayOrder(order)).willReturn(resultDto);
		//5、提交订单
		memberCardOrderApi.submitOrder(orderId);
		
		PayInfoParamDto payInfoParamDto = new PayInfoParamDto();
		payInfoParamDto.setClientType("3");
		payInfoParamDto.setIp("127.0.0.1");
		payInfoParamDto.setOrderId(orderId);
		payInfoParamDto.setPaymentType(1);
		//6、获取支付信息
		memberCardOrderApi.getPayInfo(payInfoParamDto);
		//7、取消订单
		memberCardOrderApi.cancelMemberCardOrder(orderId,userId,true);
		//3、删除
		memberCardOrderApi.removetMemberPayNumber(memberPayNum);
		
	}
}
