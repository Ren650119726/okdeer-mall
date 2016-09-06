package com.okdeer.mall.system.mq;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.google.common.base.Charsets;
import com.okdeer.archive.stock.consts.StockConstants;
import com.okdeer.archive.stock.exception.StockException;
import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.yschome.base.framework.mq.RocketMQProducer;
import com.okdeer.mall.system.factory.MessageFactory;

/**
 * ClassName: StockMQProducer 
 * @Description: 库存消息生产者
 * @author maojj
 * @date 2016年7月26日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1			2016-07-26			maojj			库存消息生产者
 */
@Service
public class StockMQProducer {

	/**
	 * 消息生产者
	 */
	@Resource
	private RocketMQProducer rocketMQProducer;
	

	/**
	 * @Description: 发送消息 
	 * @param stockAdjustList 库存调整列表
	 * @throws Exception 抛出异常
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public void sendMessage(List<StockAdjustVo> stockAdjustList) throws Exception {
		for(StockAdjustVo stockAdjustVo : stockAdjustList) {
			this.sendMessage(stockAdjustVo);
		}
	}
	/**
	 * @Description: 发送消息
	 * @param stockAdjustVo 库存调整VO
	 * @throws Exception 抛出异常
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public void sendMessage(StockAdjustVo stockAdjustVo) throws Exception {
		if(stockAdjustVo == null) {
			return;
		}
		switch (stockAdjustVo.getStockOperateEnum()) {
			// 采购导入商品
			case PURCHASE:
				this.sendPurchaseMsg(stockAdjustVo);
				break;
			// 库存调整
			case STOCK_ADJUST:
				this.sendStockAjustMsg(stockAdjustVo);
				break;
			// 发货
			case SEND_OUT_GOODS:
				this.sendStockUpdateMq(stockAdjustVo, "DELIVER_SALE_ORDER");
				break;
			// 拒签
			case REFUSED_SIGN:
				this.sendStockUpdateMq(stockAdjustVo, "REFUSE_SALE_ODRER");
				break;
			// 退货
			case RETURN_OF_GOODS:
				this.sendStockUpdateMq(stockAdjustVo, "RETURN_SALE_ORDER");
				break;
			// 活动订单发货
			case ACTIVITY_SEND_OUT_GOODS:
				this.sendStockUpdateMq(stockAdjustVo, "DELIVER_SALE_ORDER_EVENT");
				break;
			// 活动拒签
			case ACTIVITY_REFUSED_SIGN:
				this.sendStockUpdateMq(stockAdjustVo, "REFUSE_SALE_ODRER_EVENT");
				break;
			// 活动退货
			case ACTIVITY_RETURN_OF_GOODS:
				this.sendStockUpdateMq(stockAdjustVo, "RETURN_SALE_ORDER_EVENT");
				break;
			// POS机线下下单
			case POS_PLACE_ORDER:
				this.sendStockUpdateMq(stockAdjustVo, "DELIVER_SALE_ORDER");
				break;
			default:
				break;
		}
	}

	/**
	 * @Description: 发送库存调整消息（ERP消费）
	 * @param stockAdjustVo 修改库存vo
	 * @throws Exception 抛出异常   
	 * @author maojj
	 * @date 2016年7月26日
	 */
	private void sendStockAjustMsg(StockAdjustVo stockAdjustVo) throws Exception {
		String msg = MessageFactory.buildMessage(stockAdjustVo, MessageFactory.STOCK_AJUST);
		this.send(StockConstants.MQ_TOPIC_YSCMALL_STOCK, StockConstants.MQ_TAG_STOCK_ADJUST, msg);
	}

	/**
	 * @Description:  调用erp系统生成单据（采购导入）
	 * @param stockAdjustVo 修改库存vo
	 * @throws Exception 抛出异常   
	 * @author maojj
	 * @date 2016年7月26日
	 */
	private void sendPurchaseMsg(StockAdjustVo stockAdjustVo) throws Exception {
		String msg = MessageFactory.buildMessage(stockAdjustVo, MessageFactory.PURCHASE);
		this.send(StockConstants.MQ_TOPIC_YSCMALL_STOCK, StockConstants.MQ_TAG_STOCK_PURCHASE, msg);
	}

	/**
	 * @Description: 调用erp系统生成单据（修改库存）
	 * @param stockAdjustVo 修改库存vo
	 * @param operateType erp操作类型码
	 * @throws Exception 抛出异常 
	 * @author maojj
	 * @date 2016年7月26日
	 */
	private void sendStockUpdateMq(StockAdjustVo stockAdjustVo, String operateType) throws Exception {
		String msg = MessageFactory.buildMessage(stockAdjustVo, MessageFactory.STOCK_UPDATE, operateType);
		this.send(StockConstants.MQ_TOPIC_YSCMALL_STOCK, StockConstants.MQ_TAG_STOCK_UPDATE, msg);
	}

	private void send(String topic, String tag, String msg) throws Exception {
		Message message = new Message(topic, tag, msg.getBytes(Charsets.UTF_8));
		SendResult sendResult = rocketMQProducer.send(message);
		if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
			throw new StockException("写mq数据失败");
		}
	}
}
