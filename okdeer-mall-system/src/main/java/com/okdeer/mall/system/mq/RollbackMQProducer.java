
package com.okdeer.mall.system.mq;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.common.message.Message;
import com.google.common.base.Charsets;
import com.okdeer.archive.stock.consts.RollBackConstant;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.message.MQMessage;

/**
 * ClassName: RollbackMQProducer 
 * @Description: 回滚消息生产者
 * @author maojj
 * @date 2016年7月26日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1			2016-07-26			maojj			回滚消息生产者
 */
@Service
public class RollbackMQProducer {

	/**
	 * 消息生产者
	 */
	@Resource
	private RocketMQProducer rocketMQProducer;

	/**
	 * @Description: 发送回滚库存消息
	 * @param rpcIdList rpcID列表
	 * @throws Exception  抛出异常
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public void sendStockRollbackMsg(List<String> rpcIdList) throws Exception {
		if(CollectionUtils.isEmpty(rpcIdList)){
			return;
		}
		for (String rpcId : rpcIdList) {
			this.sendStockRollbackMsg(rpcId);
		}
	}

	/**
	 * @Description: 发送回滚库存消息
	 * @param rpcId rpcID
	 * @throws Exception 抛出异常  
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public void sendStockRollbackMsg(String rpcId) throws Exception {
		 if (rpcId == null) {
			 return;
		 }
	     MQMessage msg = new MQMessage(RollBackConstant.TOPIC_STOCK_ROLLBACK,rpcId);
	     rocketMQProducer.sendMessage(msg);
	}

	/**
	 * @Description: 发送回滚商品消息
	 * @param rpcIdList rpcID列表
	 * @throws Exception 抛出异常   
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public void sendSkuRollbackMsg(List<String> rpcIdList) throws Exception {
		for (String rpcId : rpcIdList) {
			this.sendSkuRollbackMsg(rpcId);
		}
	}

	/**
	 * @Description: 发送回滚商品消息
	 * @param rpcId rpcID
	 * @throws Exception 抛出异常   
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public void sendSkuRollbackMsg(String rpcId) throws Exception {
		if (rpcId == null){
			return;
		}
		this.send(rpcId, RollBackConstant.TOPIC_ACTIVITY_SKU_STATUS_ROLLBACK,
				RollBackConstant.TAGS_ACTIVITY_SKU_STATUS_ROLLBACK);
	}

	/**
	 * @Description: 发送回滚商品消息
	 * @param rpcIdList rpcID列表
	 * @throws Exception 抛出异常   
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public void sendSkuBatchRollbackMsg(List<String> rpcIdList) throws Exception {
		for (String rpcId : rpcIdList) {
			this.sendSkuRollbackMsg(rpcId);
		}
	}

	/**
	 * @Description: 发送回滚商品消息
	 * @param rpcId rpcID
	 * @throws Exception 抛出异常   
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public void sendSkuBatchRollbackMsg(String rpcId) throws Exception {
		if (rpcId == null){
			return;
		}
		this.send(rpcId, RollBackConstant.TOPIC_ACTIVITY_SKU_STATUS_BATCH_ROLLBACK,
				RollBackConstant.TAGS_ACTIVITY_SKU_STATUS_BATCH_ROLLBACK);
	}

	/**
	 * @Description: 发送消息
	 * @param rpcId rpcID
	 * @param topic 消息topic
	 * @param tag 消息tag
	 * @throws Exception 抛出异常  
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public void send(String rpcId, String topic, String tag) throws Exception {
		Message msg = new Message(topic, tag, rpcId.getBytes(Charsets.UTF_8));
		rocketMQProducer.send(msg);
	}
}
