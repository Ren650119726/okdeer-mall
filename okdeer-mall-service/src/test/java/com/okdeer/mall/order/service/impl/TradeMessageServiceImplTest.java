package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.api.pay.luzgorder.dto.PayLuzgOrderDto;
import com.okdeer.api.pay.service.PayLuzgOrderApi;
import com.okdeer.archive.store.entity.StoreMemberRelation;
import com.okdeer.archive.store.service.IStoreMemberRelationServiceApi;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.enums.SendMsgType;
import com.okdeer.mall.order.service.CancelOrderApi;
import com.okdeer.mall.order.service.TradeMessageService;
import com.okdeer.mall.order.service.TradeOrderRefundsService;
import com.okdeer.mall.order.service.TradeOrderServiceApi;
import com.okdeer.mall.order.vo.SendMsgParamVo;

//@RunWith(Parameterized.class)
public class TradeMessageServiceImplTest extends BaseServiceTest{
	
	@Resource
	private TradeMessageService tradeMessageService;
//	@Autowired
	@Reference(version = "1.0.0", check = false)
	private PayLuzgOrderApi payLuzgOrderApi;
	@Reference(version = "1.0.0", check = false)
	private TradeOrderServiceApi tradeOrderServiceApi;
	@Reference(version = "1.0.0", check = false)
	private IStoreMemberRelationServiceApi storeMemberRelationApi;
	
	
	@Test
	public void testUpdateByCustomer() {
		try {
			PayLuzgOrderDto lzgOrderDto = payLuzgOrderApi.findByTradeNum("2017052308525285136847836");
			if(lzgOrderDto != null){
				SendMsgParamVo sendMsgParamVo = new SendMsgParamVo();
				//鹿掌柜的金额
				sendMsgParamVo.setLzgAmount(new BigDecimal("0.01"));
				//店老板用户id
				sendMsgParamVo.setUserId(lzgOrderDto.getPayeeUserId());
				//订单id
				sendMsgParamVo.setOrderId(lzgOrderDto.getId());
				sendMsgParamVo.setSendMsgType(SendMsgType.lzgGathering.ordinal());
				//店铺id
//				TradeOrder order = tradeOrderServiceApi.selectById(lzgOrderDto.getId());
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("sysUserId",sendMsgParamVo.getUserId());
				map.put("memberType",0);
				List<StoreMemberRelation> smrList = storeMemberRelationApi.findByParams(map);
				if(CollectionUtils.isNotEmpty(smrList)){
//					sendMsgParamVo.setStoreId("5592971b276511e6aaff00163e010eb1");
					sendMsgParamVo.setStoreId(smrList.get(0).getStoreId());
					tradeMessageService.sendSellerAppMessage(sendMsgParamVo, SendMsgType.lzgGathering);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}