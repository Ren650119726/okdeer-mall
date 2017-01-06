/**
 * @Project: okdeer-archive-goods
 * @Author: wangf01
 * @Date: 2017年1月2日
 * @Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved.
 */

package com.okdeer.mall.activity.service.impl;

import static com.okdeer.common.consts.ELTopicTagConstants.TAG_LOWPRICE_EL_ADD;
import static com.okdeer.common.consts.ELTopicTagConstants.TAG_LOWPRICE_EL_DEL;
import static com.okdeer.common.consts.ELTopicTagConstants.TAG_LOWPRICE_EL_UPDATE;
import static com.okdeer.common.consts.ELTopicTagConstants.TAG_SALE_EL_ADD;
import static com.okdeer.common.consts.ELTopicTagConstants.TAG_SALE_EL_DEL;
import static com.okdeer.common.consts.ELTopicTagConstants.TAG_SALE_EL_UPDATE;
import static com.okdeer.common.consts.ELTopicTagConstants.TAG_SECKILL_EL_ADD;
import static com.okdeer.common.consts.ELTopicTagConstants.TAG_SECKILL_EL_DEL;
import static com.okdeer.common.consts.ELTopicTagConstants.TAG_SECKILL_EL_UPDATE;
import static com.okdeer.common.consts.ELTopicTagConstants.TOPIC_GOODS_SYNC_EL;

import java.util.List;

import javax.annotation.Resource;

import com.okdeer.mall.activity.el.service.ELSkuApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.client.producer.TransactionCheckListener;
import com.alibaba.rocketmq.client.producer.TransactionSendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.base.Charsets;
import com.okdeer.archive.goods.dto.ActivityMessageParamDto;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.RocketMQTransactionProducer;
import com.okdeer.base.framework.mq.RocketMqResult;
import com.okdeer.mall.activity.coupons.service.ActivitySaleService;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillService;
import com.okdeer.mall.activity.service.ELSkuService;

/**
 * ClassName: ELSkuServiceImpl
 *
 * @author wangf01
 * @Description: 搜素引擎商品-service-impl
 * @date 2017年1月2日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.el.service.ELSkuApi")
public class ELSkuServiceImpl implements ELSkuService, ELSkuApi {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(ELSkuServiceImpl.class);

	/**
	 * 注入秒杀活动service
	 */
	@Autowired
	ActivitySeckillService activitySeckillService;

	/**
	 * 注入特惠活动service
	 */
	@Autowired
	private ActivitySaleService activitySaleService;

	/**
	 * 事务消息注入
	 */
	@Resource
	private RocketMQTransactionProducer rocketMQTransactionProducer;

	@Override
	public boolean syncSaleToEL(List<String> activityIds, int status, String storeId, String createUserId, int syncType)
			throws Exception {
		String tag = "";
		switch (syncType) {
			case 0:
				tag = TAG_SALE_EL_ADD;
				break;
			case 1:
				tag = TAG_SALE_EL_UPDATE;
				break;
			case 2:
				tag = TAG_SALE_EL_DEL;
				break;
		}

		ActivityMessageParamDto activityMessageParamDto = new ActivityMessageParamDto();
		activityMessageParamDto.setActivityIds(activityIds);
		if (status == 1) {
			activityMessageParamDto.setUpdateStatus(0);
		} else if (status == 2) {
			activityMessageParamDto.setUpdateStatus(1);
		}
		String json = JsonMapper.nonEmptyMapper().toJson(activityMessageParamDto);
		Message msg = new Message(TOPIC_GOODS_SYNC_EL, tag, json.getBytes(Charsets.UTF_8));

		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, activityMessageParamDto,
				new LocalTransactionExecuter() {

					@Override
					public LocalTransactionState executeLocalTransactionBranch(Message msg, Object object) {
						// 业务方法
						try {
							activitySaleService.updateBatchStatus(activityIds, status, storeId, createUserId);
						} catch (Exception e) {
							logger.error("业务发生异常", e);
						}
						return LocalTransactionState.COMMIT_MESSAGE;
					}
				}, new TransactionCheckListener() {

					public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
						return LocalTransactionState.COMMIT_MESSAGE;
					}
				});
		return RocketMqResult.returnResult(sendResult);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public boolean syncSeckillToEL(ActivitySeckill activity, SeckillStatusEnum status, int syncType) throws Exception {
		String tag = "";
		switch (syncType) {
			case 0:
				tag = TAG_SECKILL_EL_ADD;
				break;
			case 1:
				tag = TAG_SECKILL_EL_UPDATE;
				break;
			case 2:
				tag = TAG_SECKILL_EL_DEL;
				break;
		}
		ActivityMessageParamDto activityMessageParamDto = new ActivityMessageParamDto();
		activityMessageParamDto.setActivityId(activity.getId());

		switch (status) {
			case ing:
				// 0为开始 1为关闭
				activityMessageParamDto.setUpdateStatus(0);
				break;
			case end:
				// 0为开始 1为关闭
				activityMessageParamDto.setUpdateStatus(1);
				break;
		}

		String json = JsonMapper.nonEmptyMapper().toJson(activityMessageParamDto);
		Message msg = new Message(TOPIC_GOODS_SYNC_EL, tag, json.getBytes(Charsets.UTF_8));

		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, null, new LocalTransactionExecuter() {

			@Override
			public LocalTransactionState executeLocalTransactionBranch(Message msg, Object object) {
				// 业务方法
				try {
					switch (status) {
						case ing:
							// 未开始活动，时间开始之后变更状态为已开始
							activitySeckillService.updateSeckillStatus(activity.getId(), status);
							break;
						case end:
							// 已开始活动，时间到期之后变更状态为已结束
							activitySeckillService.updateSeckillByEnd(activity);
							break;
					}
				} catch (Exception e) {
					logger.error("业务发生异常", e);
				}
				return LocalTransactionState.COMMIT_MESSAGE;
			}
		}, new TransactionCheckListener() {

			public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
				return LocalTransactionState.COMMIT_MESSAGE;
			}
		});
		return RocketMqResult.returnResult(sendResult);
	}

	@Override
	public boolean syncLowPriceToEL(int syncType) throws Exception {
		String tag = "";
		switch (syncType) {
			case 0:
				tag = TAG_LOWPRICE_EL_ADD;
				break;
			case 1:
				tag = TAG_LOWPRICE_EL_UPDATE;
				break;
			case 2:
				tag = TAG_LOWPRICE_EL_DEL;
				break;
		}

		ActivityMessageParamDto activityMessageParamDto = new ActivityMessageParamDto();

		String json = JsonMapper.nonEmptyMapper().toJson(activityMessageParamDto);
		Message msg = new Message(TOPIC_GOODS_SYNC_EL, tag, json.getBytes(Charsets.UTF_8));

		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, activityMessageParamDto,
				new LocalTransactionExecuter() {

					@Override
					public LocalTransactionState executeLocalTransactionBranch(Message msg, Object object) {
						// 业务方法
						return LocalTransactionState.COMMIT_MESSAGE;
					}
				}, new TransactionCheckListener() {

					public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
						return LocalTransactionState.COMMIT_MESSAGE;
					}
				});
		return RocketMqResult.returnResult(sendResult);
	}

}
