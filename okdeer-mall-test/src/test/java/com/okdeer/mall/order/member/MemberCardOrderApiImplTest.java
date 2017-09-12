package com.okdeer.mall.order.member;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.dto.MemberCardResultDto;
import com.okdeer.mall.order.dto.MemberTradeOrderDto;
import com.okdeer.mall.order.dto.PayInfoDto;
import com.okdeer.mall.order.dto.PayInfoParamDto;
import com.okdeer.mall.order.dto.PlaceOrderDto;
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
	public MemberCardOrderApi MemberCardOrderApi;
	

}
