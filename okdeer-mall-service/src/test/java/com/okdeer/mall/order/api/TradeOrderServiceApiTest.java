package com.okdeer.mall.order.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.order.bo.TradeOrderDetailBo;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.mapper.TradeOrderMapper;

public class TradeOrderServiceApiTest extends BaseServiceTest  {
	
	//@Resource
	//private TradeOrderServiceApi tradeOrderApi;
	@Resource
	private TradeOrderMapper tradeOrderMapper;
	@Test
	public void findOrderInfoTest(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", 1);
		params.put("storeId", "5592971b276511e6aaff00163e010eb1");
		// 订单来源，这里只查询线上订单(鹿管家（YSCAPP=0）,微信（WECHAT=1），友门鹿便利店（CVSAPP）)
		List<Integer> orderResource = new ArrayList<Integer>();
		orderResource.add(OrderResourceEnum.YSCAPP.ordinal());
		orderResource.add(OrderResourceEnum.WECHAT.ordinal());
		orderResource.add(OrderResourceEnum.CVSAPP.ordinal());
		// 对应页面哪个tab页签，用于排序处理（不同的页签排序条件不同）
		params.put("tabs", 1);
		//PageUtils<TradeOrderDetailBo> bo = tradeOrderApi.findOrderInfo(params, 1, 10);
		//Assert.isTrue(bo.getList().size()>0);
		//List<TradeOrderDetailBo>	list = tradeOrderMapper.findServiceOrderInfo(params);
		List<TradeOrderDetailBo>	list2 = tradeOrderMapper.findCloudOrderInfo(params);
	}
	
	
	
}
