package com.okdeer.mall.order.mapper;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.order.vo.TradeOrderRefundsVo;

@RunWith(Parameterized.class)
public class TradeOrderRefundsMapperTest extends BaseServiceTest{
	
	@Resource
	private TradeOrderRefundsMapper tradeOrderRefundsMapper;

	private Map<String,Object> refundQry = new HashMap<String,Object>();
	
	public TradeOrderRefundsMapperTest(Map<String,Object> refundQry){
		this.refundQry = refundQry;
	}
	
	@Parameters
	public static Collection<Object[]> initParam(){
		Map<String,Object> refundQry = new HashMap<String,Object>();
		refundQry.put("refundNo", "XT16110717520555");
		
		return Arrays.asList(new Object[][]{
			{refundQry}
		});
	}
	
	@Test
	public void testSearchOrderRefundByParams() {
		List<TradeOrderRefundsVo> refundList = tradeOrderRefundsMapper.searchOrderRefundByParams(refundQry);
		assertEquals("430064", refundList.get(0).getTradeOrderRefundsItem().get(0).getArticleNo());
	}

}
