
package com.okdeer.mall.order.builder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.common.utils.TradeNumUtil;
import com.okdeer.mall.order.bo.TradeOrderRefundContextBo;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderDispute;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsCertificateImg;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.enums.DisputeStatusEnum;
import com.okdeer.mall.order.enums.OrderIsShowEnum;
import com.okdeer.mall.order.enums.OrderItemStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.service.GenerateNumericalService;
import com.okdeer.mall.order.service.TradeOrderPayService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.vo.TradeOrderRefundsCertificateVo;

@Component
public class TradeOrderRefundBuilder {

	@Autowired
	private TradeOrderService tradeOrderService;

	@Autowired
	private GenerateNumericalService generateNumericalService;

	@Autowired
	private TradeOrderPayService tradeOrderPayService;

	public TradeOrderRefundContextBo createTradeOrderRefundContextBo(
			TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto,
			TradeOrderRefundContextBo tradeOrderRefundContext) throws ServiceException {
		Assert.hasText(tradeOrderApplyRefundParamDto.getOrderId(), "订单id不能为空");
		TradeOrder tradeOrder = tradeOrderRefundContext.getTradeOrder();
		if (tradeOrder == null) {
			tradeOrder = tradeOrderService.selectById(tradeOrderApplyRefundParamDto.getOrderId());
			tradeOrderRefundContext.setTradeOrder(tradeOrder);
		}

		if (tradeOrder.getPayWay() == PayWayEnum.PAY_ONLINE) {
			TradeOrderPay tradeOrderPay = tradeOrderRefundContext.getTradeOrderPay();
			if (tradeOrderPay == null) {
				tradeOrderPay = tradeOrderPayService.selectByOrderId(tradeOrderApplyRefundParamDto.getOrderId());
				tradeOrderRefundContext.setTradeOrderPay(tradeOrderPay);
			}
		}
		return tradeOrderRefundContext;
	}

	public TradeOrderRefunds createTradeOrderRefund(TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto,
			TradeOrderRefundContextBo tradeOrderRefundContext) {
		TradeOrder tradeOrder = tradeOrderRefundContext.getTradeOrder();
		Assert.notNull(tradeOrder);
		TradeOrderRefunds tradeOrderRefunds = new TradeOrderRefunds();
		tradeOrderRefunds.setId(UuidUtils.getUuid());
		tradeOrderRefunds.setUserId(tradeOrder.getUserId());
		tradeOrderRefunds.setStoreId(tradeOrder.getStoreId());
		tradeOrderRefunds.setType(tradeOrder.getType());
		tradeOrderRefunds.setRefundNo(generateNumericalService.generateOrderNo("XT"));
		tradeOrderRefunds.setOrderId(tradeOrder.getId());
		tradeOrderRefunds.setOrderNo(tradeOrder.getOrderNo());
		tradeOrderRefunds.setTradeNum(TradeNumUtil.getTradeNum());

		// 查询支付信息
		TradeOrderPay tradeOrderPay = tradeOrderRefundContext.getTradeOrderPay();
		if (tradeOrderPay != null) {
			tradeOrderRefunds.setPaymentMethod(tradeOrderPay.getPayType());
		} else if (tradeOrder.getPayWay() == PayWayEnum.CASH_DELIERY) {
			tradeOrderRefunds.setPaymentMethod(PayTypeEnum.CASH);
		}
		tradeOrderRefunds.setIsShow(OrderIsShowEnum.yes);
		tradeOrderRefunds.setDisabled(Disabled.valid);
		tradeOrderRefunds.setOrderResource(tradeOrderApplyRefundParamDto.getOrderResource());
		tradeOrderRefunds.setRefundsStatus(RefundsStatusEnum.WAIT_SELLER_VERIFY);
		tradeOrderRefunds.setStatus(OrderItemStatusEnum.ALL_REFUND);
		tradeOrderRefunds.setCreateTime(new Date());
		tradeOrderRefunds.setUpdateTime(new Date());
		tradeOrderRefunds.setRefundsReason(tradeOrderApplyRefundParamDto.getReason());
		tradeOrderRefunds.setMemo(tradeOrderApplyRefundParamDto.getMemo());
		tradeOrderRefundContext.setTradeOrderRefunds(tradeOrderRefunds);
		return tradeOrderRefunds;
	}

	public List<TradeOrderRefundsItem> createTradeOrderRefundsItem(
			TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto,
			TradeOrderRefundContextBo tradeOrderRefundContext) {
		List<TradeOrderItem> tradeOrderItems = tradeOrderRefundContext.getTradeOrderItemList();
		Assert.notEmpty(tradeOrderItems, "订单项不能为空");
		Assert.notNull(tradeOrderRefundContext.getTradeOrderRefunds(), "请先创建退款单信息");
		List<TradeOrderRefundsItem> list = Lists.newArrayList();

		BigDecimal totalAmount = BigDecimal.ZERO;
		BigDecimal totalIncome = BigDecimal.ZERO;
		BigDecimal totalPreferentialPrice = BigDecimal.ZERO;
		BigDecimal storePreferential = BigDecimal.ZERO;

		for (TradeOrderItem item : tradeOrderItems) {
			TradeOrderRefundsItem refundsItem = new TradeOrderRefundsItem();
			refundsItem.setId(UuidUtils.getUuid());
			refundsItem.setRefundsId(tradeOrderRefundContext.getTradeOrderRefunds().getId());
			refundsItem.setOrderItemId(item.getId());
			refundsItem.setPropertiesIndb(item.getPropertiesIndb());
			refundsItem.setBarCode(item.getBarCode());
			refundsItem.setMainPicUrl(item.getMainPicPrl());
			refundsItem.setSkuName(item.getSkuName());
			refundsItem.setSpuType(item.getSpuType());
			refundsItem.setStyleCode(item.getStyleCode());
			refundsItem.setStatus(OrderItemStatusEnum.ALL_REFUND);
			refundsItem.setStoreSkuId(item.getStoreSkuId());
			refundsItem.setUnitPrice(item.getUnitPrice());
			refundsItem.setWeight(item.getWeight());
			refundsItem.setIncome(item.getIncome());
			// 计算退款金额信息
			calculationRefuntAmount(tradeOrderRefundContext, item, refundsItem);

			totalAmount = totalAmount.add(refundsItem.getAmount());
			totalIncome = totalIncome.add(refundsItem.getIncome());
			totalPreferentialPrice = totalPreferentialPrice.add(refundsItem.getPreferentialPrice());
			storePreferential = storePreferential.add(refundsItem.getStorePreferential());
			list.add(refundsItem);
		}
		// 设置退款单的退款金额信息
		TradeOrderRefunds tradeOrderRefunds = tradeOrderRefundContext.getTradeOrderRefunds();
		tradeOrderRefunds.setTotalAmount(totalAmount);
		tradeOrderRefunds.setTotalIncome(totalIncome);
		tradeOrderRefunds.setTotalPreferentialPrice(totalPreferentialPrice);
		tradeOrderRefunds.setStorePreferential(storePreferential);
		tradeOrderRefundContext.setTradeOrderRefundsItemList(list);
		return list;
	}

	/**
	 * @Description: 计算退款金额信息
	 * @param tradeOrderRefundContext
	 * @param item
	 * @param refundsItem
	 * @author zengjizu
	 * @date 2017年10月13日
	 */
	private void calculationRefuntAmount(TradeOrderRefundContextBo tradeOrderRefundContext, TradeOrderItem item,
			TradeOrderRefundsItem refundsItem) {
		TradeOrder tradeOrder = tradeOrderRefundContext.getTradeOrder();
		Assert.notNull(tradeOrder, "订单信息不能为空");
		Assert.notNull(item, "订单项不能为空");
		if (tradeOrder.getType() == OrderTypeEnum.STORE_CONSUME_ORDER) {
			// 到店消费订单
			Assert.notEmpty(tradeOrderRefundContext.getTradeOrderItemDetail(), "订单项明细信息不能为空");
			int quantity = 0;
			BigDecimal actualAmount = BigDecimal.ZERO;
			BigDecimal preferentialAmount = BigDecimal.ZERO;
			for (TradeOrderItemDetail tradeOrderItemDetail : tradeOrderRefundContext.getTradeOrderItemDetail()) {
				if (tradeOrderItemDetail.getOrderItemId().equals(item.getId())) {
					actualAmount = actualAmount.add(tradeOrderItemDetail.getActualAmount());
					preferentialAmount = preferentialAmount.add(tradeOrderItemDetail.getPreferentialPrice());
					quantity++;
				}
			}
			refundsItem.setQuantity(quantity);
			refundsItem.setAmount(actualAmount);
			refundsItem.setPreferentialPrice(preferentialAmount);
			refundsItem.setStorePreferential(BigDecimal.ZERO);
		} else {
			// 默认使用订单项退款
			refundsItem.setQuantity(item.getQuantity());
			refundsItem.setAmount(item.getActualAmount());
			refundsItem.setPreferentialPrice(item.getPreferentialPrice());
			refundsItem.setStorePreferential(item.getStorePreferential());
		}
	}

	public TradeOrderRefundsCertificateVo createCertificate(TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto,
			TradeOrderRefundContextBo tradeOrderRefundContext) {
		Assert.notNull(tradeOrderRefundContext.getTradeOrderRefunds(), "请先创建退款单信息");
		TradeOrderRefundsCertificateVo certificate = new TradeOrderRefundsCertificateVo();
		String certificateId = UuidUtils.getUuid();
		certificate.setId(certificateId);
		certificate.setRefundsId(tradeOrderRefundContext.getTradeOrderRefunds().getId());
		certificate.setCreateTime(new Date());
		certificate.setOperator(tradeOrderRefundContext.getTradeOrderRefunds().getUserId());
		String remark = "买家申请了退货退款，退款原因：" + tradeOrderApplyRefundParamDto.getReason() + "，退款说明："
				+ tradeOrderApplyRefundParamDto.getMemo();
		certificate.setRemark(remark);
		if (CollectionUtils.isNotEmpty(tradeOrderApplyRefundParamDto.getRefundPics())) {
			// 凭证图片名称
			List<TradeOrderRefundsCertificateImg> certificateImgs = Lists.newArrayList();
			TradeOrderRefundsCertificateImg certificateImg = null;
			for (String refundPic : tradeOrderApplyRefundParamDto.getRefundPics()) {
				certificateImg = new TradeOrderRefundsCertificateImg();
				certificateImg.setCertificateId(certificateId);
				certificateImg.setId(UuidUtils.getUuid());
				certificateImg.setImagePath(refundPic);
				certificateImgs.add(certificateImg);
			}
			certificate.setTradeOrderRefundsCertificateImg(certificateImgs);
		}
		return certificate;
	}
	
	public TradeOrderDispute createTradeOrderDispute(TradeOrderRefundContextBo tradeOrderRefundContext){
		TradeOrderRefunds tradeOrderRefunds =  tradeOrderRefundContext.getTradeOrderRefunds();
		Assert.notNull(tradeOrderRefunds,"创建纠纷单失败，退款单信息不能为空");
		String disputeNo = generateNumericalService.generateOrderNo("JF");
		TradeOrderDispute tradeOrderDispute = new TradeOrderDispute();
		tradeOrderDispute.setId(UuidUtils.getUuid());
		tradeOrderDispute.setUserId(tradeOrderRefunds.getUserId());
		tradeOrderDispute.setStoreId(tradeOrderRefunds.getStoreId());
		tradeOrderDispute.setDisputeNo(disputeNo);
		tradeOrderDispute.setRefundsId(tradeOrderRefunds.getId());
		tradeOrderDispute.setStatus(DisputeStatusEnum.UNPROCESSED);
		tradeOrderDispute.setCreateTime(new Date());
		return tradeOrderDispute;
	}

}
