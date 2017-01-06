package com.okdeer.mall.activity.coupons.api;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.rocketmq.common.message.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.okdeer.archive.goods.dto.ActivityMessageParamDto;
import com.okdeer.base.framework.mq.RocketMQProducer;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.okdeer.mall.activity.coupons.service.ActivitySaleELServiceApi;
import com.okdeer.mall.activity.coupons.service.ActivitySaleService;

import static com.okdeer.common.consts.ELTopicTagConstants.TAG_GOODS_EL_ADD;
import static com.okdeer.common.consts.ELTopicTagConstants.TAG_LOWPRICE_EL_UPDATE;
import static com.okdeer.common.consts.ELTopicTagConstants.TOPIC_GOODS_SYNC_EL;
import static com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum.LOW_PRICE;

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivitySaleELServiceApi")
public class ActivitySaleElServiceApiImpl implements ActivitySaleELServiceApi {

	/**
	 * mq注入
	 */
	@Autowired
	private RocketMQProducer rocketMQProducer;
	
	@Autowired
	private ActivitySaleService activitySaleService;
	@Override
	public void save(ActivitySale activitySale, List<ActivitySaleGoods> asgList)
			throws Exception {
		activitySaleService.save(activitySale, asgList);
		if(activitySale.getType() == LOW_PRICE){
			List<String> list = asgList.stream().map(e -> e.getStoreSkuId()).collect(Collectors.toList());
			structureProducer(list);
		}
	}

	@Override
	public void update(ActivitySale ActivitySale,
			List<ActivitySaleGoods> asgList) throws Exception {
		activitySaleService.update(ActivitySale, asgList);
	}

	@Override
	public void updateBatchStatus(List<String> ids, int status, String storeId,
			String createUserId) throws Exception {
		activitySaleService.updateBatchStatus(ids, status, storeId, createUserId);
	}

	/**
	 * 发送消息同步数据到搜索引擎执行
	 * @param list
	 * @throws Exception
	 */
	private void structureProducer(List<String> list) throws Exception {
		ActivityMessageParamDto paramDto = new ActivityMessageParamDto();
		paramDto.setSkuIds(list);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(paramDto);
		Message msg = new Message(TOPIC_GOODS_SYNC_EL, TAG_LOWPRICE_EL_UPDATE,json.getBytes(Charsets.UTF_8));
		rocketMQProducer.send(msg);
	}
}
