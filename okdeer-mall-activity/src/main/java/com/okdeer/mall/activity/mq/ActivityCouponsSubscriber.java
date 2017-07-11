package com.okdeer.mall.activity.mq;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.okdeer.archive.system.enums.MQConsumeStatusEnum;
import com.okdeer.base.framework.mq.annotation.RocketMQListener;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.common.enums.DataBaseDmlEnum;
import com.okdeer.common.system.dto.MQConsumeLogDto;
import com.okdeer.common.system.service.MQConsumeLogApi;
import com.okdeer.jxc.common.utils.JsonMapper;
import com.okdeer.mall.activity.coupons.bo.ActivityCouponsBo;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.mq.constants.ActivityCouponsTopic;
import com.okdeer.retail.facade.stock.constant.SyncConstant;

/**
 * ClassName: ActivityCouponsSubscriber 
 * @Description: 代金券消费代金券使用或者剩余统计
 * @author maojj
 * @date 2017年2月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.1 			2017年2月23日				maojj
 */
@Service
public class ActivityCouponsSubscriber {
	
	private static final Logger logger = LoggerFactory.getLogger(ActivityCouponsSubscriber.class);
	
	private static final String LOCK_KEY_FORMAT = "%s%s";

	@Resource
	private ActivityCouponsMapper activityCouponsMapper;
	
	@Resource
	private RedisLockRegistry redisLockRegistry;
	
	@Reference(version="1.0.0",check=false)
	private MQConsumeLogApi mqConsumeLogApi;
	
	@RocketMQListener(topic = ActivityCouponsTopic.TOPIC_COUPONS_COUNT, tag = "*")
	public ConsumeConcurrentlyStatus trigger(MQMessage enMessage) throws Exception {
		// 对消息key加锁。加锁规则：topic:msgKey
		Lock lock = redisLockRegistry
				.obtain(String.format(LOCK_KEY_FORMAT, ActivityCouponsTopic.TOPIC_COUPONS_COUNT, enMessage.getKey()));
		try {
			if (lock != null && lock.tryLock(10, TimeUnit.SECONDS)) {
				return consumeMsg(enMessage);
			} else {
				return ConsumeConcurrentlyStatus.RECONSUME_LATER;
			}

		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}

	}
	
	@Transactional
	public ConsumeConcurrentlyStatus consumeMsg(MQMessage enMessage){
		// 消息key
		String msgKey = enMessage.getKey();
		// 根据消息topic+消息key查询消费日志
		MQConsumeLogDto mqLogDto = mqConsumeLogApi.findByTopicAndKey(ActivityCouponsTopic.TOPIC_COUPONS_COUNT, msgKey);
		// 消息幂等性检查
		if(mqLogDto != null && mqLogDto.getStatus() == MQConsumeStatusEnum.SUCCESS){
			// 如果消息记录存在且成功，则标识消息已经被消费
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		}
		// 消息内容
		ActivityCouponsBo couponsBo = (ActivityCouponsBo)enMessage.getContent();
		try {
			// 更新代金券使用数量
			activityCouponsMapper.updateCouponsNum(couponsBo);
			// 保存成功消费记录
			saveOrUpdateBySuccess(enMessage,mqLogDto);
		} catch (Exception e) {
			logger.error("修改代金券使用或剩余数量时失败，{}",e);
			return saveOrUpdateByFail(enMessage, mqLogDto, e.toString());
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}
	
	private void saveOrUpdateBySuccess(MQMessage enMessage,MQConsumeLogDto mqLogDto){
		try {
			if(mqLogDto == null){
				mqLogDto = new MQConsumeLogDto();
				mqLogDto.setOptFlag(DataBaseDmlEnum.INSERT);
				String msgContent = JsonMapper.nonDefaultMapper().toJson(enMessage.getContent());
				mqLogDto.setMsgId(enMessage.getMsgId());
				mqLogDto.setMsgKey(enMessage.getKey()== null ?"": enMessage.getKey());
				mqLogDto.setMsgTopic(ActivityCouponsTopic.TOPIC_COUPONS_COUNT); 
				mqLogDto.setMsgTag("*");
				mqLogDto.setMsgContent(msgContent);
				mqLogDto.setRepeatTime(0);
				mqLogDto.setStatus(MQConsumeStatusEnum.SUCCESS);
				mqLogDto.setCreateTime(new Date());
				mqLogDto.setUpdateTime(new Date());
			}else{
				// 如果消息日志不为空，说明当前是重试成功。
				Integer repeatTime = mqLogDto.getRepeatTime() + 1;
				mqLogDto.setOptFlag(DataBaseDmlEnum.UPDATE);
				mqLogDto.setRepeatTime(repeatTime);
				mqLogDto.setStatus(MQConsumeStatusEnum.SUCCESS);
				mqLogDto.setUpdateTime(new Date());
			}
			mqConsumeLogApi.saveOrUpdate(mqLogDto);
		} catch (Exception e) {
			logger.error("修改代金券使用或剩余数量消息：{},保存或更新成功消息日志异常：{}",enMessage.getContent(),e.getMessage());
		}
	}
	
	private ConsumeConcurrentlyStatus saveOrUpdateByFail(MQMessage enMessage,MQConsumeLogDto mqLogDto,String errMsg){
		ConsumeConcurrentlyStatus consumeStatus = ConsumeConcurrentlyStatus.RECONSUME_LATER;
		if(mqLogDto != null){
			mqLogDto.setOptFlag(DataBaseDmlEnum.UPDATE);
			// 如果消息日志不为空，说明当前是重试失败。
			Integer repeatTime = mqLogDto.getRepeatTime() + 1;
			mqLogDto.setRepeatTime(repeatTime);
			if(repeatTime >= 10){
				mqLogDto.setStatus(MQConsumeStatusEnum.FAIL);
				consumeStatus = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
			mqLogDto.setErrorInfo(errMsg);
			mqLogDto.setUpdateTime(new Date());
		} else {
			mqLogDto = new MQConsumeLogDto();
			mqLogDto.setOptFlag(DataBaseDmlEnum.INSERT);
			String msgContent = JsonMapper.nonDefaultMapper().toJson(enMessage.getContent());
			mqLogDto.setMsgId(enMessage.getMsgId());
			mqLogDto.setMsgKey(enMessage.getKey()== null ?"": enMessage.getKey());
			mqLogDto.setMsgTopic(SyncConstant.TOPIC_STOCKFLOW); 
			mqLogDto.setMsgTag("*");
			mqLogDto.setMsgContent(msgContent);
			mqLogDto.setRepeatTime(0);
			mqLogDto.setStatus(MQConsumeStatusEnum.RETRY);
			mqLogDto.setErrorInfo(errMsg);
			mqLogDto.setCreateTime(new Date());
			mqLogDto.setUpdateTime(new Date());
		}
		try {
			mqConsumeLogApi.saveOrUpdate(mqLogDto);
		} catch (Exception e) {
			logger.error("保存或更新失败消息日志发生异常：{},消息内容：{}",e.toString(),enMessage.getContent());
		}
		return consumeStatus;
	}
}
