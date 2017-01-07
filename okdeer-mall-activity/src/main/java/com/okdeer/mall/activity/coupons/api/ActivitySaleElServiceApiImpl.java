package com.okdeer.mall.activity.coupons.api;

import static com.okdeer.common.consts.ELTopicTagConstants.TAG_LOWPRICE_EL_UPDATE;
import static com.okdeer.common.consts.ELTopicTagConstants.TAG_SALE_EL_UPDATE;
import static com.okdeer.common.consts.ELTopicTagConstants.TOPIC_GOODS_SYNC_EL;
import static com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum.LOW_PRICE;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.rocketmq.common.message.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.okdeer.archive.goods.dto.ActivityMessageParamDto;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.okdeer.mall.activity.coupons.service.ActivitySaleELServiceApi;
import com.okdeer.mall.activity.coupons.service.ActivitySaleService;

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
			structureProducer(list,TAG_LOWPRICE_EL_UPDATE,0);
		}
	}

	@Override
	public void update(ActivitySale ActivitySale,
			List<ActivitySaleGoods> asgList) throws Exception {
		activitySaleService.update(ActivitySale, asgList);
	}

	@Override
	public void updateBatchStatus(List<String> ids, int status, String storeId,
			String createUserId,Integer activityType) throws Exception {
		activitySaleService.updateBatchStatus(ids, status, storeId, createUserId,activityType);
        // 5:特惠 7:低价
		switch (activityType){
			case 5:
                structureProducer(ids,TAG_SALE_EL_UPDATE,1);
				break;
			case 7:
                structureProducer(ids,TAG_LOWPRICE_EL_UPDATE,1);
				break;
		}
	}

	/**
	 * 发送消息同步数据到搜索引擎执行
	 * @param list List<String>
     * @param tag String
	 * @throws Exception
	 */
	private void structureProducer(List<String> list,String tag,Integer updateStatus) throws Exception {
		ActivityMessageParamDto paramDto = new ActivityMessageParamDto();
		paramDto.setSkuIds(list);
        paramDto.setUpdateStatus(updateStatus);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(paramDto);
		Message msg = new Message(TOPIC_GOODS_SYNC_EL, tag,json.getBytes(Charsets.UTF_8));
		rocketMQProducer.send(msg);
	}
}
