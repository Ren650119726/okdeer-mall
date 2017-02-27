package com.okdeer.mall.activity.coupons.mq;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.mall.ApplicationTests;
import com.okdeer.mall.activity.coupons.mq.constants.SafetyStockTriggerTopic;

/**
 * 
 * ClassName: ActivitySaleRemindApiImplTest 
 * @Description: 活动安全库存消息
 * @author tangy
 * @date 2017年2月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.0.0          2017年2月23日                               tangy
 */
public class ActivitySaleRemindApiImplTest extends ApplicationTests {
	/**
	 * logger
	 */
	private static final Logger log = LoggerFactory.getLogger(ActivitySaleRemindApiImplTest.class);

	@Autowired
	private RocketMQProducer rocketMQProducer;
	
	@Test
	public void sendSafetyWarningTest(){
		Map<String, String> storeSkuIdMap = new HashMap<String, String>();
		storeSkuIdMap.put("8a98683a5669d21d01567e1a834505f7", "8a94e7ee5a5a71ea015a5a78d8b10024");
		storeSkuIdMap.put("8a9868225669d3da01569730e887629f", "8a94e7ee5a5a71ea015a5a78d8b10024");
		MQMessage anMessage = new MQMessage(SafetyStockTriggerTopic.TOPIC_SAFETY_STOCK_TRIGGER, (Serializable) storeSkuIdMap);
		try {
			rocketMQProducer.sendMessage(anMessage);
		} catch (Exception e) {
			log.error("活动安全库存发送消息异常:{}", JsonMapper.nonEmptyMapper().toJson(storeSkuIdMap), e);
		}
	}
	
}
