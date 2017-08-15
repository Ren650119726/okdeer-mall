/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @项目名称: yschome-mall 
 * @文件名称: OrderTimerSubscriber.java 
 * @Date: 2016年5月11日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */

package com.okdeer.mall.order.timer;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuService;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceServiceApi;
import com.okdeer.archive.store.dto.StoreOrderCommentDto;
import com.okdeer.mall.common.utils.DateUtils;
import com.okdeer.mall.common.utils.RobotUserUtil;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.enums.OrderCancelType;
import com.okdeer.mall.order.enums.OrderComplete;
import com.okdeer.mall.order.enums.OrderItemStatusEnum;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.vo.TradeOrderCommentVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsCertificateVo;
import com.okdeer.base.common.constant.LoggerConstants;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.framework.mq.AbstractRocketMQSubscriber;
import com.okdeer.mall.member.service.MemberConsigneeAddressService;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
import com.okdeer.mall.order.service.CancelOrderService;
import com.okdeer.mall.order.service.GenerateNumericalService;
import com.okdeer.mall.order.service.TradeOrderCommentService;
import com.okdeer.mall.order.service.TradeOrderItemDetailService;
import com.okdeer.mall.order.service.TradeOrderRefundsService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.timer.constant.TimerMessageConstant;
import com.okdeer.base.common.utils.mapper.JsonMapper;

/**
 * 订单流程超时处理
 * 
 * @pr yschome-mall
 * @author guocp
 * @date 2016年5月11日 上午11:46:03
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *   添加判断是否服务订单超时                2016-08-15         wangf01          服务订单超时规则判断，发货超时（服务时间+2小时），收货超时（服务时间+24小时）
 *   13166和13165          2016-08-30         wusw             修改服务店订单自动取消的取消原因的后台文案和短信文案
 *     V2.1.0           2017-02-16           wusw           订单超时，店铺评分默认五颗星
 *   
 */
@Service
public class TradeOrderTimerSubscriber extends AbstractRocketMQSubscriber implements TimerMessageConstant {

	private static final Logger logger = LoggerFactory.getLogger(TradeOrderTimerSubscriber.class);

	/**
	 * 订单service
	 */
	@Autowired
	private TradeOrderService tradeOrderService;

	/**
	 * 售后service
	 */
	@Autowired
	private TradeOrderRefundsService tradeOrderRefundsService;

	/**
	 * 定时消息发送
	 */
	@Autowired
	private TradeOrderTimer tradeOrderTimer;

	/**
	 * 订单评价service
	 */
	@Autowired
	private TradeOrderCommentService tradeOrderCommentService;

	/**
	 * 地址service
	 */
	@Autowired
	private MemberConsigneeAddressService memberConsigneeAddressService;

	/**
	 * 单号生成器
	 */
	@Resource
	private GenerateNumericalService generateNumericalService;

	/**
	 * 订单详情
	 */
	@Autowired
	private TradeOrderItemDetailService tradeOrderItemDetailService;

	/**
	 * 
	 */
	@Resource
	private TradeOrderItemMapper tradeOrderItemMapper;
	
	
	/**
	 * 
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceServiceApi goodsStoreSkuServiceService;

	
	@Autowired
	private CancelOrderService cancelOrderService;
	
	
	@Override
	public String getTopic() {
		return TOPIC_ORDER_TIMER;
	}

	@Override
	public String getTags() {
		return WILDCARD;
	}

	/**
	 * 接受消息处理
	 */
	@Override
	public ConsumeConcurrentlyStatus subscribeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		MessageExt message = msgs.get(0);
		Tag tag = Tag.enumNameOf(message.getTags());
		if (tag == null) {
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		}
		String content = new String(message.getBody(), Charsets.UTF_8);
		logger.info(content);
		switch (tag) {
			case tag_pay_timeout:
				return processPayTimeout(content, tag);
			case tag_delivery_timeout:
			case tag_delivery_group_timeout:
			case tag_accept_server_timeout:
			case tag_take_goods_timeout:	
				return processDeliveryTimeout(content, tag);
			// begin add by wangf01 2016.08.08
			// 服务店铺发货超时自动取消
			case tag_delivery_server_timeout:
				logger.info(LoggerConstants.LOGGER_DEBUG_INCOMING_METHOD, "服务店铺发货超时自动取消");
				return processDeliveryTimeout(content, tag);
			// 服务店铺收货超时自动确收
			case tag_confirm_server_timeout:
				logger.info(LoggerConstants.LOGGER_DEBUG_INCOMING_METHOD, "服务店铺收货超时自动确收");
				return processConfirmTimeout(content, tag);
			// end add by wangf01 2016.08.08
			case tag_confirm_group_timeout:
			case tag_confirm_timeout:
				return processConfirmTimeout(content, tag);
			case tag_finish_timeout:
			case tag_finish_group_timeout:
				return processFinishTimeout(content, tag);
			case tag_finish_evaluate_timeout:
				return processEvaluateTimeout(content, tag);
			case tag_refund_agree_timeout:
				return processRefundAgreeTimeout(content, tag);
			case tag_refund_cancel_by_refuse_apply_timeout:
				return processRefundCancelTimeout(content, tag);
			case tag_refund_cancel_by_refuse_timeout:
				return processRefundCancelTimeout(content, tag);
			case tag_refund_cancel_by_agree_timeout:
				return processRefundCancelByAgreeTimeout(content, tag);
			case tag_refund_confirm_timeout:
			case tag_refund_confirm_group_timeout:
				return processRefundConfirmTimeout(content, tag);
			case tag_service_order_refund_timeout:
				return processServiceRefundTimeout(content, tag);
			case tag_recharge_pay_timeout:
			    return processRechargePayTimeout(content, tag);
			default:
				break;
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	/**
	 * 支付超时处理
	 */
	public ConsumeConcurrentlyStatus processPayTimeout(String content, Tag tag) {
		TimeoutMessage timeoutMsg = JsonMapper.nonEmptyMapper().fromJson(content, TimeoutMessage.class);
		try {
			TradeOrder order = tradeOrderService.selectById(timeoutMsg.getKey());
			if (order == null) {
				return ConsumeConcurrentlyStatus.RECONSUME_LATER;
			}
			if (order.getStatus() == OrderStatusEnum.UNPAID) {
				logger.info("订单支付超时取消订单,订单号：" + order.getOrderNo());
				order.setUpdateTime(new Date());
				order.setUpdateUserId(RobotUserUtil.getRobotUser().getId());
				order.setReason("超时未支付，系统取消订单");
				
				//begin add by zengjz 取消订单换接口类
				order.setCancelType(OrderCancelType.CANCEL_BY_SYSTEM);
				if(order.getOrderResource() == OrderResourceEnum.SWEEP){
					//modify by mengsj begin 扫码购超时取消订单
					TradeOrder tradeOrder = new TradeOrder();
					tradeOrder.setId(order.getId());
					tradeOrder.setStatus(OrderStatusEnum.CANCELED);
					tradeOrder.setCancelType(order.getCancelType());
					tradeOrder.setUpdateTime(new Date());
					tradeOrder.setOrderResource(OrderResourceEnum.SWEEP);
					
					tradeOrderService.updateOrderStatus(tradeOrder);
					//modify by mengsj end 扫码购超时取消订单
				}else{
					cancelOrderService.cancelOrder(order, false);
				}
				//end add by zengjz 取消订单换接口类
			}
		} catch (Exception e) {
			logger.error("订单支付超时取消订单异常", e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	/**
	 * 充值订单未支付超时处理
	 */
	public ConsumeConcurrentlyStatus processRechargePayTimeout(String content, Tag tag) {
	    TimeoutMessage timeoutMsg = JsonMapper.nonEmptyMapper().fromJson(content, TimeoutMessage.class);
	    try {
	        TradeOrder order = tradeOrderService.selectById(timeoutMsg.getKey());
	        if (order == null) {
	            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
	        }
	        
	        if(order.getStatus() == OrderStatusEnum.UNPAID) {
	            logger.info("订单支付超时取消订单，订单号：" + order.getOrderNo());
	            order.setUpdateTime(new Date());
	            order.setUpdateUserId(RobotUserUtil.getRobotUser().getId());
	            order.setReason("充值订单支付超时，系统取消订单");
	            
	            this.tradeOrderService.updateCancelRechargeOrder(order);
	        }
        } catch (Exception e) {
            logger.error("充值订单支付超时取消订单异常", e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
	    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}
	
	/**
	 * 发货超时
	 * 
	 * @param content
	 *            消息内容
	 */
	public ConsumeConcurrentlyStatus processDeliveryTimeout(String content, Tag tag) {
		TimeoutMessage timeoutMsg = JsonMapper.nonEmptyMapper().fromJson(content, TimeoutMessage.class);
		Long currentTime = System.currentTimeMillis();
		try {
			// begin 判断是否是服务订单的发货超时 add by wangf01 2016.08.15
			if (tag == Tag.tag_delivery_server_timeout || tag == Tag.tag_accept_server_timeout) {
				// 根据id查询订单信息，获取服务时间用于计算超时时间
				TradeOrder order = tradeOrderService.selectById(timeoutMsg.getKey());
				// 服务订单服务时间
				Date serverTime = DateUtils.parseDate(order.getPickUpTime().substring(0,16), "yyyy-MM-dd HH:mm");
				// 服务时间+2小时为发货超时时间
				serverTime = DateUtils.addHours(serverTime, 2);
				// 服务超时时间大于当前时间，则发送推迟执行消息，直到超时时间达到再进行消费
				if (serverTime.getTime() - currentTime > MIN_INTERVAL) {
					// 时间未到重新发送消息sendAfreshTimerMessage(tag，消息内容，当前时间，超时时间)
					tradeOrderTimer.sendAfreshTimerMessage(tag, timeoutMsg, currentTime, serverTime.getTime());
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}
			} else {
				if (tradeOrderTimer.isTimeUnDue(tag.getValue(), timeoutMsg.getSendDate(), currentTime)) {
					tradeOrderTimer.sendAfreshTimerMessage(tag, timeoutMsg, currentTime);
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}
			}
			// end 判断是否是服务订单的发货超时 add by wangf01 2016.08.15

			TradeOrder order = tradeOrderService.selectById(timeoutMsg.getKey());
			if (order == null) {
				logger.warn(timeoutMsg.getKey() + "：订单不存在，发货超时不处理");
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}

			if (order.getStatus() == OrderStatusEnum.DROPSHIPPING) {
				logger.info("发货超时自动取消订单,订单号：" + order.getOrderNo());
				order.setUpdateTime(new Date());
				order.setUpdateUserId(RobotUserUtil.getRobotUser().getId());
				//Begin 13166和13165    update by wusw  20160830
				if (order.getType() == OrderTypeEnum.SERVICE_STORE_ORDER) {
					order.setReason("商家超时未派单，系统取消");
				} else {
					order.setReason("超时未发货，系统取消订单");
				}
				//End 13166和13165    update by wusw  20160830
				//begin add by zengjz 取消订单换接口类
				order.setCancelType(OrderCancelType.CANCEL_BY_SYSTEM);
				cancelOrderService.cancelOrder(order, false);
				//end add by zengjz 取消订单换接口类
			}else if (order.getStatus() == OrderStatusEnum.WAIT_RECEIVE_ORDER) {
				logger.info("接单超时自动取消订单,订单号：" + order.getOrderNo());
				order.setUpdateTime(new Date());
				order.setUpdateUserId(RobotUserUtil.getRobotUser().getId());
				order.setReason("商家超时未接单，系统取消");
				order.setCancelType(OrderCancelType.CANCEL_BY_SYSTEM);
				cancelOrderService.cancelOrder(order, false);
			}
		} catch (Exception e) {
			logger.error("发货超时自动取消订单异常", e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	/**
	 * 确认收货超时 
	 */
	public ConsumeConcurrentlyStatus processConfirmTimeout(String content, Tag tag) {

		TimeoutMessage timeoutMsg = JsonMapper.nonEmptyMapper().fromJson(content, TimeoutMessage.class);
		Long currentTime = System.currentTimeMillis();
		try {
			// begin 判断是否是服务订单的确认收货超时 add by wangf01 2016.08.15
			if (tag == Tag.tag_confirm_server_timeout) {
				// 根据id查询订单信息，获取服务时间用于计算超时时间
				TradeOrder order = tradeOrderService.selectById(timeoutMsg.getKey());
				// 服务订单服务时间
				Date serverTime = DateUtils.parseDate(order.getPickUpTime().substring(0,16), "yyyy-MM-dd HH:mm");
				// 服务时间+24小时为确认收货超时时间
				serverTime = DateUtils.addHours(serverTime, 24);
				// 服务超时时间大于当前时间，则发送推迟执行消息，直到超时时间达到再进行消费
				if (serverTime.getTime() - currentTime > MIN_INTERVAL) {
					// 时间未到重新发送消息sendAfreshTimerMessage(tag，消息内容，当前时间，超时时间)
					tradeOrderTimer.sendAfreshTimerMessage(tag, timeoutMsg, currentTime, serverTime.getTime());
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}
			} else {
				if (tradeOrderTimer.isTimeUnDue(tag.getValue(), timeoutMsg.getSendDate(), currentTime)) {
					tradeOrderTimer.sendAfreshTimerMessage(tag, timeoutMsg, currentTime);
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}
			}
			// end 判断是否是服务订单的确认收货超时 add by wangf01 2016.08.15

			TradeOrder order = tradeOrderService.findOrderDetail(timeoutMsg.getKey());
			if (order.getStatus() == OrderStatusEnum.TO_BE_SIGNED) {

				logger.info("超时自动确认收货,订单号：" + order.getOrderNo());
				order.setCurrentStatus(order.getStatus());
				order.setStatus(OrderStatusEnum.HAS_BEEN_SIGNED);
				order.setUpdateUserId(RobotUserUtil.getRobotUser().getId());
				order.setUpdateTime(new Date());
				order.setReceivedTime(order.getUpdateTime());
				tradeOrderService.updateWithConfirm(order);
			}
		} catch (Exception e) {
			logger.error("超时自动确认收货异常", e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	/**
	 * 订单完成：赠送积分 \ 冻结余额进入可用余额（有售后保障）
	 */
	public ConsumeConcurrentlyStatus processFinishTimeout(String content, Tag tag) {
		TimeoutMessage timeoutMsg = JsonMapper.nonEmptyMapper().fromJson(content, TimeoutMessage.class);
		Long currentTime = System.currentTimeMillis();
		try {
			if (tradeOrderTimer.isTimeUnDue(tag.getValue(), timeoutMsg.getSendDate(), currentTime)) {
				tradeOrderTimer.sendAfreshTimerMessage(tag, timeoutMsg, currentTime);
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}

			TradeOrder order = tradeOrderService.selectById(timeoutMsg.getKey());
			if (order.getStatus() != OrderStatusEnum.HAS_BEEN_SIGNED || order.getIsComplete() == OrderComplete.YES) {
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}

			// 操作订单完成
			logger.info("订单完成自动赠送积分,订单号：" + order.getOrderNo());
			tradeOrderService.updateWithComplete(order);
		} catch (Exception e) {
			logger.error("订单完成自动赠送积分异常", e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	/**
	 * 订单完成：超时自动好评
	 */
	private ConsumeConcurrentlyStatus processEvaluateTimeout(String content, Tag tag) {
		TimeoutMessage timeoutMsg = JsonMapper.nonEmptyMapper().fromJson(content, TimeoutMessage.class);
		Long currentTime = System.currentTimeMillis();
		try {
			if (tradeOrderTimer.isTimeUnDue(tag.getValue(), timeoutMsg.getSendDate(), currentTime)) {
				tradeOrderTimer.sendAfreshTimerMessage(tag, timeoutMsg, currentTime);
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}

			TradeOrder order = tradeOrderService.findOrderDetail(timeoutMsg.getKey());
			if (order.getStatus() == OrderStatusEnum.HAS_BEEN_SIGNED) {
				Date createTime = new Date();
				List<TradeOrderCommentVo> list = tradeOrderCommentService.findListByOrderId(order.getId());
				// 判断是否已评价
				if (list != null && !Iterables.isEmpty(list)) {
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}

				List<TradeOrderCommentVo> commentList = Lists.newArrayList();
				for (TradeOrderItem item : order.getTradeOrderItem()) {
					TradeOrderCommentVo comment = new TradeOrderCommentVo();
					comment.setId(UuidUtils.getUuid());
					comment.setUserId(order.getUserId());
					comment.setCreateTime(createTime);
					comment.setOrderId(order.getId());
					comment.setOrderItemId(item.getId());
					comment.setStoreSkuId(item.getStoreSkuId());
					comment.setPicUrl(item.getMainPicPrl());
					// 5星好评
					comment.setStar(5);
					comment.setContent("好评");
					comment.setStatus(WhetherEnum.whether);
					commentList.add(comment);
				}
				// Begin V2.1 add by wusw 20170216
				//订单超时，店铺评分默认五颗星
				StoreOrderCommentDto storeCommentDto = new StoreOrderCommentDto();
				storeCommentDto.setId(UuidUtils.getUuid());
				storeCommentDto.setStoreId(order.getStoreId());
				storeCommentDto.setUserId(order.getUserId());
				storeCommentDto.setOrderId(order.getId());
				storeCommentDto.setDeliverySpeed("5");
				storeCommentDto.setGoodsQuality("5");
				storeCommentDto.setServiceAttitude("5");
				storeCommentDto.setDisabled(Disabled.valid);
				storeCommentDto.setCreateTime(createTime);
				logger.info("订单完成自动好评,订单号：" + order.getOrderNo());
				tradeOrderCommentService.updateUserEvaluate(commentList,storeCommentDto);
				// End V2.1 add by wusw 20170216
			}
		} catch (Exception e) {
			logger.error("订单完成自动好评异常", e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	/**
	 * 商家同意退款超时
	 */
	private ConsumeConcurrentlyStatus processRefundAgreeTimeout(String content, Tag tag) {
		TimeoutMessage timeoutMsg = JsonMapper.nonEmptyMapper().fromJson(content, TimeoutMessage.class);
		Long currentTime = System.currentTimeMillis();
		try {
			if (tradeOrderTimer.isTimeUnDue(tag.getValue(), timeoutMsg.getSendDate(), currentTime)) {
				tradeOrderTimer.sendAfreshTimerMessage(tag, timeoutMsg, currentTime);
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}

			TradeOrderRefunds refund = tradeOrderRefundsService.findById(timeoutMsg.getKey());
			if (refund.getRefundsStatus() != RefundsStatusEnum.WAIT_SELLER_VERIFY) {
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
			logger.info("超时未处理，系统默认同意退货申请,订单号：" + refund.getOrderNo());
			String remark = "超时未处理，系统默认同意退货申请";
			tradeOrderRefundsService.updateStatusWithAgree(refund.getId(), getStoreDefaultAddress(refund.getStoreId()),
					remark, RobotUserUtil.getRobotUser().getId());
		} catch (Exception e) {
			logger.error("超时未处理，系统默认同意退货申请异常", e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	/**
	 * 获取店铺默认地址 ID
	 */
	private String getStoreDefaultAddress(String storeId) throws Exception {
		return memberConsigneeAddressService.findByStoreId(storeId).getId();
	}

	private TradeOrderRefundsCertificateVo buildCertificate(String refundsId, String remark) {
		// 操作凭证
		TradeOrderRefundsCertificateVo certificate = new TradeOrderRefundsCertificateVo();
		String certificateId = UuidUtils.getUuid();
		certificate.setId(certificateId);
		certificate.setRefundsId(refundsId);
		certificate.setCreateTime(new Date());
		// 系统用户ID
		certificate.setOperator(RobotUserUtil.getRobotUser().getId());
		certificate.setRemark(remark);
		return certificate;
	}

	/**
	 * 用户超时未撤销或未申请客服介入by商家拒绝申请
	 */
	private ConsumeConcurrentlyStatus processRefundCancelTimeout(String content, Tag tag) {
		TimeoutMessage timeoutMsg = JsonMapper.nonEmptyMapper().fromJson(content, TimeoutMessage.class);
		Long currentTime = System.currentTimeMillis();
		try {
			TradeOrderRefunds refund = tradeOrderRefundsService.findById(timeoutMsg.getKey());
			if ((tag == Tag.tag_refund_cancel_by_refuse_apply_timeout
					&& refund.getRefundsStatus() == RefundsStatusEnum.SELLER_REJECT_APPLY)
					|| (tag == Tag.tag_refund_cancel_by_refuse_timeout
							&& refund.getRefundsStatus() == RefundsStatusEnum.SELLER_REJECT_REFUND)) {

				if (tradeOrderTimer.isTimeUnDue(tag.getValue(), timeoutMsg.getSendDate(), currentTime)) {
					tradeOrderTimer.sendAfreshTimerMessage(tag, timeoutMsg, currentTime);
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}

				logger.info("超时未处理系统取消退款申请,订单号：" + refund.getOrderNo());
				// 更新退款单
				refund.setRefundsStatus(RefundsStatusEnum.BUYER_REPEAL_REFUND);
				refund.setUpdateTime(new Date());
				TradeOrderRefundsCertificateVo certificate = buildCertificate(refund.getId(), "用户超时未处理，系统默认撤销退款申请");
				tradeOrderRefundsService.updateWithRevocatory(refund, certificate);
			}
		} catch (Exception e) {
			logger.error("超时未处理系统取消退款申请异常", e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	/**
	 * 用户退款超时未发货by商家同意
	 */
	private ConsumeConcurrentlyStatus processRefundCancelByAgreeTimeout(String content, Tag tag) {
		TimeoutMessage timeoutMsg = JsonMapper.nonEmptyMapper().fromJson(content, TimeoutMessage.class);
		Long currentTime = System.currentTimeMillis();
		try {
			TradeOrderRefunds refund = tradeOrderRefundsService.findById(timeoutMsg.getKey());
			if (refund.getRefundsStatus() == RefundsStatusEnum.WAIT_BUYER_RETURN_GOODS) {
				if (tradeOrderTimer.isTimeUnDue(tag.getValue(), timeoutMsg.getSendDate(), currentTime)) {
					tradeOrderTimer.sendAfreshTimerMessage(tag, timeoutMsg, currentTime);
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}

				logger.info("超时未归还商品，系统自动撤销退款申请异常,订单号：" + refund.getOrderNo());
				// 更新退款单
				refund.setRefundsStatus(RefundsStatusEnum.BUYER_REPEAL_REFUND);
				refund.setUpdateTime(new Date());
				TradeOrderRefundsCertificateVo certificate = buildCertificate(refund.getId(), "用户超时未归还商品，系统默认撤销退款申请");
				tradeOrderRefundsService.updateWithRevocatory(refund, certificate);
			}
		} catch (Exception e) {
			logger.error("超时未归还商品，系统自动撤销退款申请异常", e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	/**
	 * 商家超时未退款(普通订单和团购)
	 */
	private ConsumeConcurrentlyStatus processRefundConfirmTimeout(String content, Tag tag) {
		TimeoutMessage timeoutMsg = JsonMapper.nonEmptyMapper().fromJson(content, TimeoutMessage.class);
		Long currentTime = System.currentTimeMillis();
		try {
			TradeOrderRefunds refund = tradeOrderRefundsService.findById(timeoutMsg.getKey());
			if (refund.getRefundsStatus() == RefundsStatusEnum.WAIT_SELLER_REFUND) {
				if (tradeOrderTimer.isTimeUnDue(tag.getValue(), timeoutMsg.getSendDate(), currentTime)) {
					tradeOrderTimer.sendAfreshTimerMessage(tag, timeoutMsg, currentTime);
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}

				logger.info("超时未归还商品，系统自动撤销退款申请,订单号：" + refund.getOrderNo());
				// 更新状态并退款
				tradeOrderRefundsService.updateAgreePayment(refund.getId(), RobotUserUtil.getRobotUser().getId());
			}
		} catch (Exception e) {
			logger.error("超时未归还商品，系统默认同意退货申请异常", e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	/**
	 * 服务订单活动结束自动退款
	 * 
	 * @param content
	 * @param tag
	 * @return
	 */
	private ConsumeConcurrentlyStatus processServiceRefundTimeout(String content, Tag tag) {
		TimeoutMessage timeoutMsg = JsonMapper.nonEmptyMapper().fromJson(content, TimeoutMessage.class);
		Long currentTime = System.currentTimeMillis();
		try {
			TradeOrder order = tradeOrderService.findOrderDetail(timeoutMsg.getKey());
			if (order.getStatus() != OrderStatusEnum.HAS_BEEN_SIGNED || order.getIsComplete() == OrderComplete.YES) {
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
			List<TradeOrderItem> orderItem = tradeOrderItemMapper.selectOrderItemListById(order.getId());
			if (orderItem != null && !Iterables.isEmpty(orderItem)) {
				GoodsStoreSkuService sku = goodsStoreSkuServiceService.selectBySkuId(orderItem.get(0).getStoreSkuId());
				Long endTime = sku.getEndTime().getTime();
				if (endTime - currentTime >= TimerMessageConstant.MIN_INTERVAL) {
					tradeOrderTimer.sendAfreshTimerMessage(tag, timeoutMsg, currentTime, endTime);
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}

				for (TradeOrderItem item : order.getTradeOrderItem()) {
					if (item.getServiceAssurance() != null && item.getServiceAssurance() > 0) {
						logger.info("超时未消费，系统自动退款,订单号：" + order.getOrderNo());
						// 超时未消费，系统自动申请退款
						refundServiceOrder(order);
					} else {
						expireServiceOrder(order);
					}
				}
			} else {
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		} catch (Exception e) {
			logger.error("超时未消费，系统自动退款异常", e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	private void expireServiceOrder(TradeOrder order) throws Exception {
		// 设置消费码为已退款
		for (TradeOrderItem item : order.getTradeOrderItem()) {
			tradeOrderItemDetailService.updateStatusWithExpire(item.getId());
		}
		order.setIsComplete(OrderComplete.YES);
		tradeOrderService.updateOrderStatus(order);
	}

	private void refundServiceOrder(TradeOrder order) throws Exception {
		List<TradeOrderRefunds> list = tradeOrderRefundsService.findByOrderNo(order.getOrderNo());
		if (list != null && !Iterables.isEmpty(list)) {
			return;
		}
		TradeOrderRefunds orderRefunds = new TradeOrderRefunds();
		String refundsId = UuidUtils.getUuid();
		orderRefunds.setId(refundsId);
		orderRefunds.setRefundNo(generateNumericalService.generateOrderNo("XT"));
		orderRefunds.setOrderId(order.getId());
		orderRefunds.setOrderNo(order.getOrderNo());
		orderRefunds.setStoreId(order.getStoreId());
		orderRefunds.setOperator(RobotUserUtil.getRobotUser().getId());
		// 退款原因
		orderRefunds.setRefundsReason("服务订单超时未消费，系统自动退款");
		// 说明
		orderRefunds.setRefundsStatus(RefundsStatusEnum.WAIT_SELLER_VERIFY);
		orderRefunds.setStatus(OrderItemStatusEnum.ALL_REFUND);
		orderRefunds.setType(order.getType());
		// 退款单来源
		orderRefunds.setOrderResource(OrderResourceEnum.YSCAPP);
		orderRefunds.setOrderNo(order.getOrderNo());
		// 支付类型
		if (order.getTradeOrderPay() != null) {
			orderRefunds.setPaymentMethod(order.getTradeOrderPay().getPayType());
		} else if (order.getPayWay() == PayWayEnum.CASH_DELIERY) {
			orderRefunds.setPaymentMethod(PayTypeEnum.CASH);
		}
		orderRefunds.setUserId(order.getUserId());
		orderRefunds.setCreateTime(new Date());
		orderRefunds.setUpdateTime(new Date());
		BigDecimal totalIncome = new BigDecimal("0.00");
		for (TradeOrderItem item : order.getTradeOrderItem()) {
			TradeOrderRefundsItem refundsItem = new TradeOrderRefundsItem();
			refundsItem.setId(UuidUtils.getUuid());
			refundsItem.setRefundsId(refundsId);
			refundsItem.setOrderItemId(item.getId());
			refundsItem.setPropertiesIndb(item.getPropertiesIndb());
			refundsItem.setQuantity(item.getQuantity());
			refundsItem.setAmount(item.getActualAmount());
			refundsItem.setBarCode(item.getBarCode());
			refundsItem.setMainPicUrl(item.getMainPicPrl());
			refundsItem.setSkuName(item.getSkuName());
			refundsItem.setSpuType(item.getSpuType());
			refundsItem.setStyleCode(item.getStyleCode());
			refundsItem.setPreferentialPrice(item.getPreferentialPrice());
			// Begin V2.5 added by maojj 2017-06-28
			refundsItem.setStorePreferential(item.getStorePreferential());
			// End V2.5 added by maojj 2017-06-28
			refundsItem.setStatus(OrderItemStatusEnum.ALL_REFUND);
			refundsItem.setStoreSkuId(item.getStoreSkuId());
			refundsItem.setUnitPrice(item.getUnitPrice());
			refundsItem.setWeight(item.getWeight());
			refundsItem.setIncome(item.getIncome());
			if (item.getIncome() != null) {
				totalIncome = totalIncome.add(item.getIncome());
			}

			List<TradeOrderRefundsItem> items = Lists.newArrayList(refundsItem);
			orderRefunds.setTradeOrderRefundsItem(items);
			orderRefunds.setTotalAmount(item.getActualAmount());
			orderRefunds.setTotalPreferentialPrice(item.getPreferentialPrice());
			// Begin V2.5 added by maojj 2017-06-28
			orderRefunds.setStorePreferential(item.getStorePreferential());
			// End V2.5 added by maojj 2017-06-28
		}
		orderRefunds.setTotalIncome(totalIncome);
		// 退款凭证信息
		tradeOrderRefundsService.insertRefunds(orderRefunds, buildRefundCertificate(refundsId));
		order.setIsComplete(OrderComplete.YES);
		tradeOrderService.updateOrderStatus(order);
	}

	private TradeOrderRefundsCertificateVo buildRefundCertificate(String refundsId) {
		TradeOrderRefundsCertificateVo certificate = new TradeOrderRefundsCertificateVo();
		String certificateId = UuidUtils.getUuid();
		certificate.setId(certificateId);
		certificate.setRefundsId(refundsId);
		certificate.setCreateTime(new Date());
		// 买家用户ID buyerUserId
		certificate.setOperator(RobotUserUtil.getRobotUser().getId());
		certificate.setRemark("服务商品超时未消费，系统自动退款");
		return certificate;
	}
	
	

}
