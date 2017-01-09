
package com.okdeer.mall.activity.coupons.api;

import static com.okdeer.common.consts.ELTopicTagConstants.TAG_LOWPRICE_EL_UPDATE;
import static com.okdeer.common.consts.ELTopicTagConstants.TAG_SALE_EL_DEL;
import static com.okdeer.common.consts.ELTopicTagConstants.TAG_SALE_EL_UPDATE;
import static com.okdeer.common.consts.ELTopicTagConstants.TOPIC_GOODS_SYNC_EL;
import static com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum.LOW_PRICE;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.rocketmq.common.message.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.okdeer.archive.goods.dto.ActivityMessageParamDto;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoodsBo;
import com.okdeer.mall.activity.coupons.service.ActivitySaleELServiceApi;
import com.okdeer.mall.activity.coupons.service.ActivitySaleGoodsServiceApi;
import com.okdeer.mall.activity.coupons.service.ActivitySaleService;
import com.okdeer.mall.activity.dto.ActivitySaleGoodsParamDto;

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivitySaleELServiceApi")
public class ActivitySaleElServiceApiImpl implements ActivitySaleELServiceApi {

	private static final Logger logger = LoggerFactory.getLogger(ActivitySaleElServiceApiImpl.class);

	/**
	 * mq注入
	 */
	@Autowired
	private RocketMQProducer rocketMQProducer;

	@Autowired
	private ActivitySaleService activitySaleService;

	@Autowired
	private ActivitySaleGoodsServiceApi activitySaleGoodsServiceApi;

	@Override
	public void save(ActivitySale activitySale, List<ActivitySaleGoods> asgList) throws Exception {
		activitySaleService.save(activitySale, asgList);
		if (activitySale.getType() == LOW_PRICE) {
			List<String> list = asgList.stream().map(e -> e.getStoreSkuId()).collect(Collectors.toList());
			ActivityMessageParamDto paramDto = new ActivityMessageParamDto();
			paramDto.setActivityId(activitySale.getId());
			paramDto.setSkuIds(list);
			paramDto.setUpdateStatus(String.valueOf(0));
			structureProducer(paramDto, TAG_LOWPRICE_EL_UPDATE);
		}
	}

	@Override
	public void update(ActivitySale ActivitySale, List<ActivitySaleGoods> asgList) throws Exception {
		activitySaleService.update(ActivitySale, asgList);
	}

	@Override
	public void updateBatchStatus(List<String> ids, int status, String storeId, String createUserId,
			Integer activityType) throws Exception {
		activitySaleService.updateBatchStatus(ids, status, storeId, createUserId, activityType);
		for (String id : ids) {
			ActivitySaleGoodsParamDto param = new ActivitySaleGoodsParamDto();
			param.setActivityId(id);
			List<ActivitySaleGoodsBo> goodsBoList = activitySaleGoodsServiceApi.findSaleGoodsByParams(param);
			if (goodsBoList != null && goodsBoList.size() > 0) {
				ActivityMessageParamDto paramDto = new ActivityMessageParamDto();
				paramDto.setUpdateStatus(String.valueOf(1));
				paramDto.setActivityId(id);
				List<String> skuIds = goodsBoList.stream().map(m -> m.getStoreSkuId()).collect(Collectors.toList());
				paramDto.setSkuIds(skuIds);
				// 5:特惠 7:低价
				switch (activityType) {
					case 5:
						structureProducer(paramDto, TAG_SALE_EL_UPDATE);
						break;
					case 7:
						structureProducer(paramDto, TAG_LOWPRICE_EL_UPDATE);
						break;
				}
			}
		}
	}

	@Override
	public void deleteActivitySaleGoods(String storeId, String createUserId, String activitySaleGoodsId,
			String goodsStoreSkuId) throws Exception {
		activitySaleService.deleteActivitySaleGoods(storeId, createUserId, activitySaleGoodsId, goodsStoreSkuId);
		// 发送消息，同步数据到搜索引擎
		List<String> list = Arrays.asList(goodsStoreSkuId);
		ActivityMessageParamDto paramDto = new ActivityMessageParamDto();
		paramDto.setUpdateStatus(String.valueOf(0));
		paramDto.setSkuIds(list);
		structureProducer(paramDto, TAG_SALE_EL_DEL);
	}

	/**
	 * 发送消息同步数据到搜索引擎执行
	 *
	 * @param paramDto ActivityMessageParamDto
	 * @param tag  String
	 * @throws Exception
	 */
	private void structureProducer(ActivityMessageParamDto paramDto, String tag) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(paramDto);
		Message msg = new Message(TOPIC_GOODS_SYNC_EL, tag, json.getBytes(Charsets.UTF_8));
		logger.info("便利店活动消息：msg{}", mapper.writeValueAsString(msg));
		rocketMQProducer.send(msg);
	}
}
