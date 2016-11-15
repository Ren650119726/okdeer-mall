package com.okdeer.mall.order.mapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.order.entity.TradeOrderRechargeVo;
import com.okdeer.mall.order.vo.PhysicsOrderVo;

/**
 * ClassName: TradeOrderMapperTest 
 * @Description: 单元测试用例
 * @author maojj
 * @date 2016年11月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿1.2			2016年11月15日				maojj			单元测试用例
 */
@RunWith(Parameterized.class)
public class TradeOrderMapperTest extends BaseServiceTest{
	
	@Autowired
	private TradeOrderMapper tradeOrderMapper;
	
	private Map<String,Object> servOrderQry;
	
	private TradeOrderRechargeVo rechargeOrderQry;
	
	public TradeOrderMapperTest(Map<String,Object> servOrderQry,TradeOrderRechargeVo rechargeOrderQry){
		this.servOrderQry = servOrderQry;
		this.rechargeOrderQry = rechargeOrderQry;
	}
	
	@Parameters
	public static Collection<Object[]> initParam(){
		Map<String,Object> servOrderQry1 = new HashMap<String,Object>();
		servOrderQry1.put("startTime", "2016-11-14");
		servOrderQry1.put("type", "2");
		
		TradeOrderRechargeVo rechargeOrderQry1 = new TradeOrderRechargeVo();
		rechargeOrderQry1.setUserPhone("13418679094");
		
		return Arrays.asList(new Object[][]{
			{servOrderQry1,rechargeOrderQry1}
		});
	}

	@Test
	public void testSelectServiceStoreListForOperate() {
		List<PhysicsOrderVo> orderList = tradeOrderMapper.selectServiceStoreListForOperate(servOrderQry);
		Assert.assertEquals(14, orderList.size());
	}
	
	@Test
	public void testSelectRechargeOrder() {
		List<TradeOrderRechargeVo> orderList = tradeOrderMapper.selectRechargeOrder(rechargeOrderQry);
		Assert.assertEquals(rechargeOrderQry.getUserPhone(),orderList.get(0).getUserPhone());
	}

}
