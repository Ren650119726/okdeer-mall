/** 
 *@Project: okdeer-archive-goods 
 *@Author: wangf01
 *@Date: 2017年1月2日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.activity.service.impl;

import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.client.producer.TransactionCheckListener;
import com.alibaba.rocketmq.client.producer.TransactionSendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.base.Charsets;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.RocketMQTransactionProducer;
import com.okdeer.base.framework.mq.RocketMqResult;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillService;
import com.okdeer.mall.activity.service.ELSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.okdeer.common.consts.ELTopicTagConstants.*;

/**
 * ClassName: ELSkuServiceImpl 
 * @Description: 搜素引擎商品-service-impl
 * @author wangf01
 * @date 2017年1月2日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class ELSkuServiceImpl implements ELSkuService {

	/**
	 * 注入秒杀活动service
	 */
	@Autowired
	ActivitySeckillService activitySeckillService;

	/**
	 * 事务消息注入
	 */
	@Resource
	private RocketMQTransactionProducer rocketMQTransactionProducer;

	@Override
	public boolean syncSaleToEL(int syncType) throws Exception {
		String json = JsonMapper.nonEmptyMapper().toJson("");
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
		Message msg = new Message(TOPIC_GOODS_SYNC_EL, tag, json.getBytes(Charsets.UTF_8));

		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, null, new LocalTransactionExecuter() {

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

	@Override
	public boolean syncSeckillToEL(ActivitySeckill activity, SeckillStatusEnum status, int syncType) throws Exception {
		String json = JsonMapper.nonEmptyMapper().toJson("");
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
		Message msg = new Message(TOPIC_GOODS_SYNC_EL, tag, json.getBytes(Charsets.UTF_8));

		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, null, new LocalTransactionExecuter() {

			@Override
			public LocalTransactionState executeLocalTransactionBranch(Message msg, Object object) {
				// 业务方法
				try {
					switch (status){
						case ing:
								activitySeckillService.updateSeckillStatus(activity.getId(), status);
							break;
						case end:
								activitySeckillService.updateSeckillByEnd(activity);
							break;
					}
				} catch (Exception e) {
					return LocalTransactionState.ROLLBACK_MESSAGE;
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
		String json = JsonMapper.nonEmptyMapper().toJson("");
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
		Message msg = new Message(TOPIC_GOODS_SYNC_EL, tag, json.getBytes(Charsets.UTF_8));

		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, null, new LocalTransactionExecuter() {

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
