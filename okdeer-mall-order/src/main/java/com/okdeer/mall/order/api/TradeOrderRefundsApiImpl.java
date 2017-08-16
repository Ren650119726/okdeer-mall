
package com.okdeer.mall.order.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.common.consts.DescriptConstants;
import com.okdeer.mall.common.vo.PageResultVo;
import com.okdeer.mall.order.dto.OrderRefundQueryParamDto;
import com.okdeer.mall.order.dto.OrderRefundsDetailDto;
import com.okdeer.mall.order.dto.OrderRefundsDto;
import com.okdeer.mall.order.dto.PhysOrderApplyRefundParamDto;
import com.okdeer.mall.order.dto.PhysicalOrderApplyDto;
import com.okdeer.mall.order.dto.PhysicalOrderApplyParamDto;
import com.okdeer.mall.order.dto.RefundsCertificateDto;
import com.okdeer.mall.order.dto.RefundsMoneyDto;
import com.okdeer.mall.order.dto.StoreConsumerApplyDto;
import com.okdeer.mall.order.dto.StoreConsumerApplyParamDto;
import com.okdeer.mall.order.dto.TradeOrderItemDetailDto;
import com.okdeer.mall.order.dto.TradeOrderItemDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsCertificateImg;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.enums.ConsumeStatusEnum;
import com.okdeer.mall.order.enums.OrderItemStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.RefundOrderTypeEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.exception.ExceedRangeException;
import com.okdeer.mall.order.service.GenerateNumericalService;
import com.okdeer.mall.order.service.StoreConsumeOrderService;
import com.okdeer.mall.order.service.TradeOrderActivityService;
import com.okdeer.mall.order.service.TradeOrderItemDetailService;
import com.okdeer.mall.order.service.TradeOrderItemService;
import com.okdeer.mall.order.service.TradeOrderPayService;
import com.okdeer.mall.order.service.TradeOrderRefundsApi;
import com.okdeer.mall.order.service.TradeOrderRefundsCertificateService;
import com.okdeer.mall.order.service.TradeOrderRefundsService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.vo.TradeOrderRefundsCertificateVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsChargeVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsVo;
import com.okdeer.mall.system.service.SysBuyerUserService;

/**
 * ClassName: TradeOrderRefundsApiImpl
 * 
 * @Description: 退款服务api实现
 * @author zengjizu
 * @date 2016年11月14日
 *
 *       =======================================================================
 *       ========================== Task ID Date Author Description
 *       ----------------+----------------+-------------------+-----------------
 *       -------------------------- v1.2.0 2016-11-16 zengjz 退款接口重新优化
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderRefundsApi")
public class TradeOrderRefundsApiImpl implements TradeOrderRefundsApi {

	private static final Logger logger = LoggerFactory.getLogger(TradeOrderRefundsApiImpl.class);

	/** 记录数 */
	private static final Integer RECORD_NUM = 10000;

	/**
	 * 凭证说明前缀
	 */
	private static final String MSG = "买家申请了退货退款，退款原因：";

	/**
	 * 云存储退款凭证、评价、投诉图片路径二级域名
	 */
	@Value("${realOrderImagePrefix}")
	private String realOrderImagePrefix;

	/**
	 * 云存储订单图片路径二级域名
	 */
	@Value("${orderImagePrefix}")
	private String orderImagePrefix;

	@Autowired
	private GenerateNumericalService generateNumericalService;

	@Autowired
	private TradeOrderService tradeOrderService;

	@Autowired
	private TradeOrderItemService tradeOrderItemService;

	@Autowired
	private TradeOrderItemDetailService tradeOrderItemDetailService;

	@Autowired
	private TradeOrderRefundsService tradeOrderRefundsService;

	@Autowired
	private StoreConsumeOrderService storeConsumeOrderService;

	@Autowired
	private TradeOrderActivityService tradeOrderActivityService;

	@Autowired
	private TradeOrderPayService tradeOrderPayService;

	@Resource
	private SysBuyerUserService sysBuyerUserService;

	@Autowired
	private TradeOrderRefundsCertificateService tradeOrderRefundsCertificateService;

	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoService;

	@Override
	public StoreConsumerApplyDto storeConsumerApplyRefunds(StoreConsumerApplyParamDto storeConsumerApplyParamDto) {
		Assert.hasText(storeConsumerApplyParamDto.getOrderId());
		Assert.hasText(storeConsumerApplyParamDto.getUserId());
		Assert.hasText(storeConsumerApplyParamDto.getOrderItemId());
		Assert.notEmpty(storeConsumerApplyParamDto.getConsumerIds());

		StoreConsumerApplyDto consumerApplyDto = new StoreConsumerApplyDto();
		try {

			String orderId = storeConsumerApplyParamDto.getOrderId();
			// 订单项ID
			String orderItemId = storeConsumerApplyParamDto.getOrderItemId();
			// 消费码项id
			List<String> detailIds = storeConsumerApplyParamDto.getConsumerIds();

			List<TradeOrderItemDetail> waitRefundDetailList = Lists.newArrayList();
			// 退款金额
			BigDecimal refundAmount = BigDecimal.ZERO;
			// 退款优惠金额
			BigDecimal refundPrefeAmount = BigDecimal.ZERO;
			// 退款数量
			int quantity = 0;

			TradeOrderItemDetail tradeOrderItemDetail = null;
			int invalidCount = 0;
			// 已消费的消费码id集合
			List<String> consumedIds = new ArrayList<String>();
			for (String detailId : detailIds) {
				tradeOrderItemDetail = tradeOrderItemDetailService.findById(detailId);

				if (!tradeOrderItemDetail.getOrderItemId().equals(orderItemId)) {
					// 不是该订单项的id则不处理
					consumerApplyDto.setMsg(DescriptConstants.ACTIVITY_RECOMMEND_REQ_PARAM_ERROR);
					consumerApplyDto.setStatus(2);
					return consumerApplyDto;
				}
				if (tradeOrderItemDetail.getStatus() != ConsumeStatusEnum.noConsume) {
					// 如果状态不是未消费，invalidCount＋1；
					invalidCount++;
					// 如果状态是已消费，则加入id集合
					if (tradeOrderItemDetail.getStatus() == ConsumeStatusEnum.consumed) {
						consumedIds.add(detailId);
					}
				}
				waitRefundDetailList.add(tradeOrderItemDetail);
				refundAmount = refundAmount.add(tradeOrderItemDetail.getActualAmount());
				refundPrefeAmount = refundPrefeAmount.add(tradeOrderItemDetail.getPreferentialPrice());
				quantity++;
				tradeOrderItemDetail = null;
			}
			// 封装ids
			consumerApplyDto.setConsumedIds(consumedIds);

			if (invalidCount > 0) {
				// 判断失效数量
				if (invalidCount == detailIds.size()) {
					// 如果消费码全部失效，返回特殊状态,方便app端做判断跳转页面
					consumerApplyDto.setMsg(DescriptConstants.CONSUME_CODE_INVALID);
					consumerApplyDto.setStatus(3);
					return consumerApplyDto;
				} else {
					// 部分失效
					consumerApplyDto.setMsg(DescriptConstants.CONSUME_CODE_INVALID);
					consumerApplyDto.setStatus(2);
					return consumerApplyDto;
				}
			}

			// 订单详情
			TradeOrder order = tradeOrderService.selectById(orderId);
			TradeOrderPay tradeOrderPay = tradeOrderPayService.selectByOrderId(orderId);
			order.setTradeOrderPay(tradeOrderPay);
			// 订单项详情
			TradeOrderItem tradeOrderItem = tradeOrderItemService.selectByPrimaryKey(orderItemId);

			// if (!order.getUserId().equals(buyerUserId)) {
			// //如果订单的购买者不是当前申请退款的用户，则不让退款
			// return resultDataMap(DescriptConstants.ORDER_NOT_EXSITS,
			// PublicResultCodeEnum.FAIL);
			// }

			TradeOrderRefunds orderRefunds = buildRefund(order, tradeOrderItem, refundAmount, refundPrefeAmount,
					quantity);
			// 退款单来源
			orderRefunds.setOrderResource(storeConsumerApplyParamDto.getOrderResource());
			orderRefunds.setOperator(storeConsumerApplyParamDto.getUserId());
			orderRefunds.setRefundsReason("消费码未消费退款");
			orderRefunds.setMemo("消费码未消费退款");
			orderRefunds.setRefundsStatus(RefundsStatusEnum.SELLER_REFUNDING);
			// 退款凭证信息
			TradeOrderRefundsCertificateVo certificate = buildCertificate(orderRefunds.getId(),
					storeConsumerApplyParamDto.getUserId(), null, MSG + "消费码未消费退款" + "，退款说明：" + "消费码未消费退款");

			storeConsumeOrderService.refundConsumeCode(order, orderRefunds, certificate, waitRefundDetailList);
			// 返回结果
			consumerApplyDto.setRefundId(orderRefunds.getId());

			consumerApplyDto.setStatus(0);
			consumerApplyDto.setMsg("申请退款成功");
			return consumerApplyDto;
		} catch (Exception e) {
			logger.error(DescriptConstants.SYS_ERROR, e);
			consumerApplyDto.setStatus(1);
			consumerApplyDto.setMsg(DescriptConstants.SYS_ERROR);
		}
		return consumerApplyDto;
	}

	@Override
	public PhysicalOrderApplyDto physicalOrderApplyRefunds(PhysicalOrderApplyParamDto physicalOrderApplyParamDto) {
		Assert.hasText(physicalOrderApplyParamDto.getOrderId());
		Assert.hasText(physicalOrderApplyParamDto.getUserId());
		Assert.hasText(physicalOrderApplyParamDto.getOrderItemId());

		PhysicalOrderApplyDto physicalOrderApplyDto = new PhysicalOrderApplyDto();
		try {

			String orderId = physicalOrderApplyParamDto.getOrderId();
			// 订单项ID
			String orderItemId = physicalOrderApplyParamDto.getOrderItemId();

			// 订单详情
			TradeOrder order = tradeOrderService.selectById(orderId);
			TradeOrderPay tradeOrderPay = tradeOrderPayService.selectByOrderId(orderId);
			order.setTradeOrderPay(tradeOrderPay);
			// 订单项详情
			TradeOrderItem tradeOrderItem = tradeOrderItemService.selectByPrimaryKey(orderItemId);

			// if (!order.getUserId().equals(buyerUserId)) {
			// //如果订单的购买者不是当前申请退款的用户，则不让退款
			// return resultDataMap(DescriptConstants.ORDER_NOT_EXSITS,
			// PublicResultCodeEnum.FAIL);
			// }

			TradeOrderRefunds orderRefunds = buildRefund(order, tradeOrderItem, tradeOrderItem.getActualAmount(),
					tradeOrderItem.getPreferentialPrice(), tradeOrderItem.getQuantity());
			// 退款单来源
			orderRefunds.setOrderResource(physicalOrderApplyParamDto.getOrderResource());
			orderRefunds.setOperator(physicalOrderApplyParamDto.getUserId());
			orderRefunds.setRefundsReason(physicalOrderApplyParamDto.getReason());
			orderRefunds.setMemo(physicalOrderApplyParamDto.getMemo());
			orderRefunds.setRefundsStatus(RefundsStatusEnum.WAIT_SELLER_VERIFY);
			// 退款凭证信息
			TradeOrderRefundsCertificateVo certificate = buildCertificate(orderRefunds.getId(),
					physicalOrderApplyParamDto.getUserId(), physicalOrderApplyParamDto.getRefundPics(),
					MSG + physicalOrderApplyParamDto.getReason() + "，退款说明：" + physicalOrderApplyParamDto.getMemo());

			tradeOrderRefundsService.insertRefunds(orderRefunds, certificate);
			// 返回结果
			physicalOrderApplyDto.setRefundId(orderRefunds.getId());

			physicalOrderApplyDto.setStatus(0);
			physicalOrderApplyDto.setMsg("申请退款成功");
			return physicalOrderApplyDto;
		} catch (Exception e) {
			logger.error(DescriptConstants.SYS_ERROR, e);
			physicalOrderApplyDto.setStatus(1);
			physicalOrderApplyDto.setMsg(DescriptConstants.SYS_ERROR);
		}
		return physicalOrderApplyDto;
	}

	/**
	 * @Description: 构建退款信息
	 * @param order
	 *            订单信息
	 * @param item
	 *            订单项信息
	 * @param refundAmount
	 *            退款金额
	 * @param refundPrefeAmount
	 *            退款优惠金额
	 * @param quantity
	 *            退款数量
	 * @return 退款对象
	 * @throws ServiceException
	 *             异常
	 * @author zengjizu
	 * @date 2016年11月14日
	 */
	private TradeOrderRefunds buildRefund(TradeOrder order, TradeOrderItem item, BigDecimal refundAmount,
			BigDecimal refundPrefeAmount, int quantity) throws ServiceException {
		TradeOrderRefunds orderRefunds = new TradeOrderRefunds();
		String refundsId = UuidUtils.getUuid();
		orderRefunds.setId(refundsId);
		orderRefunds.setRefundNo(generateNumericalService.generateOrderNo("XT"));
		orderRefunds.setOrderId(order.getId());
		orderRefunds.setOrderNo(order.getOrderNo());
		orderRefunds.setStoreId(order.getStoreId());
		// orderRefunds.setOperator(buyerUserId);
		// orderRefunds.setRefundsReason("消费码未消费退款");
		// orderRefunds.setMemo("消费码未消费退款");
		// orderRefunds.setRefundsStatus(RefundsStatusEnum.SELLER_REFUNDING);
		orderRefunds.setStatus(OrderItemStatusEnum.ALL_REFUND);
		orderRefunds.setType(order.getType());
		// 退款单来源
		// orderRefunds.setOrderResource(OrderResourceEnum.YSCAPP);
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
		BigDecimal totalIncome = BigDecimal.ZERO;

		TradeOrderRefundsItem refundsItem = new TradeOrderRefundsItem();
		refundsItem.setId(UuidUtils.getUuid());
		refundsItem.setRefundsId(refundsId);
		refundsItem.setOrderItemId(item.getId());
		refundsItem.setPropertiesIndb(item.getPropertiesIndb());
		refundsItem.setQuantity(quantity);
		refundsItem.setAmount(refundAmount);
		refundsItem.setBarCode(item.getBarCode());
		refundsItem.setMainPicUrl(item.getMainPicPrl());
		refundsItem.setSkuName(item.getSkuName());
		refundsItem.setSpuType(item.getSpuType());
		refundsItem.setStyleCode(item.getStyleCode());
		refundsItem.setPreferentialPrice(refundPrefeAmount);
		// Begin V2.5 added by maojj 2017-06-28
		refundsItem.setStorePreferential(item.getStorePreferential());
		// End V2.5 added by maojj 2017-06-28
		refundsItem.setStatus(OrderItemStatusEnum.ALL_REFUND);
		refundsItem.setStoreSkuId(item.getStoreSkuId());
		refundsItem.setUnitPrice(item.getUnitPrice());
		refundsItem.setWeight(item.getWeight());
		refundsItem.setIncome(item.getIncome());
		totalIncome = totalIncome.add(item.getIncome());
		List<TradeOrderRefundsItem> items = Lists.newArrayList(refundsItem);
		orderRefunds.setTradeOrderRefundsItem(items);
		orderRefunds.setTotalAmount(refundAmount);
		orderRefunds.setTotalPreferentialPrice(refundPrefeAmount);
		orderRefunds.setTotalIncome(totalIncome);
		// Begin V2.5 added by maojj 2017-06-28
		orderRefunds.setStorePreferential(item.getStorePreferential());
		// End V2.5 added by maojj 2017-06-28
		return orderRefunds;
	}

	/**
	 * @Description: 构建退款项信息
	 * @param refundsId
	 * @param reqJson
	 * @param remark
	 * @return
	 * @author zengjizu
	 * @date 2016年11月14日
	 */
	private TradeOrderRefundsCertificateVo buildCertificate(String refundsId, String userId, List<String> refundPics,
			String remark) {

		TradeOrderRefundsCertificateVo certificate = new TradeOrderRefundsCertificateVo();
		String certificateId = UuidUtils.getUuid();
		certificate.setId(certificateId);
		certificate.setRefundsId(refundsId);
		certificate.setCreateTime(new Date());
		// 买家用户ID buyerUserId
		certificate.setOperator(userId);
		certificate.setRemark(remark);

		if (CollectionUtils.isNotEmpty(refundPics)) {
			// 凭证图片名称
			List<TradeOrderRefundsCertificateImg> certificateImgs = Lists.newArrayList();
			TradeOrderRefundsCertificateImg certificateImg = null;
			for (String refundPic : refundPics) {
				certificateImg = new TradeOrderRefundsCertificateImg();
				certificateImg.setCertificateId(certificateId);
				certificateImg.setId(UuidUtils.getUuid());
				certificateImg.setImagePath(refundPic);
				certificateImgs.add(certificateImg);
				certificateImg = null;
			}
			certificate.setTradeOrderRefundsCertificateImg(certificateImgs);
		}
		return certificate;
	}

	/**
	 * 更新退款单状态
	 */
	@Override
	public boolean updateRefundsStatus(String refundsId, String status, String userId) {
		logger.info("售后单同步状态" + "，refundsId:" + refundsId + "，status:" + status + "，userId:" + userId);
		try {
			tradeOrderRefundsService.updateByCustomer(refundsId, RefundsStatusEnum.enumValueOf(Integer.valueOf(status)),
					userId);
		} catch (Exception e) {
			logger.error("更新退款单状态异常", e);
			return false;
		}
		return true;
	}

	/**
	 * 投诉、退款订单列表
	 * 
	 * @param params
	 *            params.orderNo 订单号 params.refundNo 退单号 params.shopName 店铺名
	 *            params.buyerUserId 买家用户ID params.buyerUserName 买家用户名
	 *            params.paymentMethod 支付方式 params.startTime 开始时间 params.endTime
	 *            结束时间
	 */
	@Override
	public PageUtils<OrderRefundsDto> orderRefund(OrderRefundQueryParamDto orderRefundQueryParamDto) throws Exception {
		try {
			PageUtils<TradeOrderRefundsVo> pageList = tradeOrderRefundsService.findPageByFinance(
					orderRefundQueryParamDto, orderRefundQueryParamDto.getpNum(), orderRefundQueryParamDto.getpSize());

			List<OrderRefundsDto> orderRefundsDtos = Lists.newArrayList();
			List<TradeOrderRefundsVo> refundList = pageList.getList();
			for (TradeOrderRefundsVo tradeOrderRefundsVo : refundList) {
				OrderRefundsDto dto = BeanMapper.map(tradeOrderRefundsVo, OrderRefundsDto.class);
				dto.setRefundAmount(tradeOrderRefundsVo.getTotalAmount());
				dto.setBuyerUserName(tradeOrderRefundsVo.getUserPhone());
				if (tradeOrderRefundsVo.getRefundsStatus() != null) {
					dto.setRefundStatus(tradeOrderRefundsVo.getRefundsStatus().ordinal());
				}
				orderRefundsDtos.add(dto);
			}
			PageUtils<OrderRefundsDto> dtoPage = new PageUtils<>(orderRefundsDtos);
			dtoPage.setPageSize(pageList.getPageSize());
			dtoPage.setPageNum(pageList.getPageNum());
			dtoPage.setTotal(pageList.getTotal());
			return dtoPage;
		} catch (ServiceException e) {
			logger.error("查询退款单异常", e);
			throw new Exception("查询退款单异常", e);
		}
	}

	@Override
	public List<OrderRefundsDto> orderRefundExport(OrderRefundQueryParamDto orderRefundQueryParamDto)
			throws ExceedRangeException, Exception {
		if (tradeOrderRefundsService.findCountByFinance(orderRefundQueryParamDto) > RECORD_NUM) {
			throw new ExceedRangeException("查询导出退款单异常", new Throwable());
		}

		try {
			List<OrderRefundsDto> orderRefunds = Lists.newArrayList();
			List<TradeOrderRefundsVo> list = tradeOrderRefundsService.findListByFinance(orderRefundQueryParamDto);
			for (TradeOrderRefundsVo refundsVo : list) {
				OrderRefundsDto dto = new OrderRefundsDto();
				dto.setId(refundsVo.getId());
				dto.setOrderNo(refundsVo.getOrderNo());
				dto.setRefundNo(refundsVo.getRefundNo());
				if (refundsVo.getPaymentMethod() != null) {
					dto.setPaymentMethod(refundsVo.getPaymentMethod().ordinal());
				}
				dto.setRefundStatus(refundsVo.getRefundsStatus().ordinal());
				dto.setTotalAmount(refundsVo.getTotalAmount());
				dto.setRefundAmount(refundsVo.getTotalAmount());
				dto.setActualAmount(refundsVo.getActualAmount());
				dto.setPreferentialAmount(refundsVo.getTotalPreferentialPrice());
				dto.setRefundMoneyTime(refundsVo.getRefundMoneyTime());
				dto.setBuyerUserId(refundsVo.getUserId());
				dto.setBuyerUserName(refundsVo.getUserPhone());
				dto.setCreateTime(refundsVo.getCreateTime());
				dto.setAgentName(null);
				dto.setStoreName(refundsVo.getStoreName());
				dto.setThirdTransNo(refundsVo.getThirdTransNo());
				orderRefunds.add(dto);
			}
			return orderRefunds;
		} catch (Exception e) {
			logger.error("查询导出退款单异常", e);
			throw new Exception("查询导出退款单异常", e);
		}
	}

	@Override
	public List<RefundsMoneyDto> refundsInfo(List<String> refundsIds) throws Exception {
		List<RefundsMoneyDto> refundsMoneys = Lists.newArrayList();
		List<TradeOrderRefunds> refundsList = tradeOrderRefundsService.findByIds(refundsIds);
		for (TradeOrderRefunds refunds : refundsList) {
			RefundsMoneyDto refundsMoney = new RefundsMoneyDto();
			TradeOrder order = tradeOrderService.selectById(refunds.getOrderId());
			if (refunds.getTotalPreferentialPrice().compareTo(BigDecimal.ZERO) > 0) {
				refundsMoney.setActivityUserId(tradeOrderActivityService.findActivityUserId(order));
				// Begin add by zengj
				// 如果是商家的优惠活动，就不需要该字段,收入和实际金额不一致，说明是平台优惠
				if (refunds.getTotalAmount() != null && refunds.getTotalIncome() != null
						&& refunds.getTotalAmount().compareTo(refunds.getTotalIncome()) != 0) {
					refundsMoney.setPreferentialAmount(
							refunds.getTotalPreferentialPrice().multiply(new BigDecimal("100")).intValue());
				}
				// End add by zengj
			}
			refundsMoney.setRefundId(refunds.getId());
			refundsMoney.setOrderNo(refunds.getOrderNo());
			refundsMoney.setRefundNo(refunds.getRefundNo());
			refundsMoney.setRefundStatus(refunds.getRefundsStatus().ordinal());
			refundsMoney.setOrderResource(order.getOrderResource().ordinal());
			// 元*100 = 分
			refundsMoney.setTotalAmount(order.getActualAmount());
			refundsMoney.setRefundAmount(refunds.getTotalAmount());
			refundsMoney.setReason(refunds.getRefundsReason());
			refundsMoney.setPaymentMethod(refunds.getPaymentMethod().ordinal());
			// refundsMoney.setApplicant(refunds.getOperator());
			refundsMoney.setStoreUserId(storeInfoService.getBossIdByStoreId(refunds.getStoreId()));
			refundsMoney.setBuyerUserId(refunds.getUserId());
			refundsMoney.setBuyerUserName(getBuyserName(refunds.getUserId()));
			refundsMoney.setCreateTime(refunds.getCreateTime());
			refundsMoney.setOutTradeNo(order.getTradeNum());
			// refundsMoney.setTransactionId(transactionId);
			TradeOrderPay orderPay = tradeOrderPayService.selectByOrderId(refunds.getOrderId());
			refundsMoney.setTransactionNo(orderPay.getReturns());
			refundsMoneys.add(refundsMoney);
		}
		return refundsMoneys;
	}

	@Override
	public RefundsMoneyDto refundsInfo(String refundsId) throws Exception {
		RefundsMoneyDto refundsMoney = new RefundsMoneyDto();
		TradeOrderRefunds refunds = tradeOrderRefundsService.getById(refundsId);
		TradeOrder order = tradeOrderService.selectById(refunds.getOrderId());
		if (refunds.getTotalPreferentialPrice().compareTo(BigDecimal.ZERO) > 0) {
			refundsMoney.setActivityUserId(tradeOrderActivityService.findActivityUserId(order));
			// Begin add by zengj
			// 如果是商家的优惠活动，就不需要该字段,收入和实际金额不一致，说明是平台优惠
			if (refunds.getTotalAmount() != null && refunds.getTotalIncome() != null
					&& refunds.getTotalAmount().compareTo(refunds.getTotalIncome()) != 0) {
				refundsMoney.setPreferentialAmount(
						refunds.getTotalPreferentialPrice().multiply(new BigDecimal("100")).intValue());
			}
			// End add by zengj
		}
		refundsMoney.setRefundId(refunds.getId());
		refundsMoney.setOrderNo(refunds.getOrderNo());
		refundsMoney.setRefundNo(refunds.getRefundNo());
		refundsMoney.setRefundStatus(refunds.getRefundsStatus().ordinal());
		refundsMoney.setOrderResource(order.getOrderResource().ordinal());
		// 元*100 = 分
		refundsMoney.setTotalAmount(order.getActualAmount());
		refundsMoney.setRefundAmount(refunds.getTotalAmount());
		refundsMoney.setReason(refunds.getRefundsReason());
		if (refunds.getPaymentMethod() != null) {
			refundsMoney.setPaymentMethod(refunds.getPaymentMethod().ordinal());
		}
		refundsMoney.setApplicant(refunds.getOperator());
		refundsMoney.setBuyerUserId(refunds.getUserId());
		refundsMoney.setBuyerUserName(getBuyserName(refunds.getUserId()));
		refundsMoney.setStoreUserId(storeInfoService.getBossIdByStoreId(refunds.getStoreId()));
		refundsMoney.setCreateTime(refunds.getCreateTime());
		refundsMoney.setOutTradeNo(order.getTradeNum());
		// refundsMoney.setTransactionId(transactionId);
		TradeOrderPay orderPay = tradeOrderPayService.selectByOrderId(refunds.getOrderId());
		refundsMoney.setTransactionNo(orderPay.getReturns());
		return refundsMoney;
	}

	/**
	 * 获取买家用户名
	 *
	 * @param userId
	 *            用户ID
	 */
	private String getBuyserName(String userId) throws ServiceException {
		// 判断参数是否为空
		if (StringUtils.isEmpty(userId)) {
			return "";
		}
		SysBuyerUser sysBuyerUser = sysBuyerUserService.loadById(userId);
		return sysBuyerUser != null ? sysBuyerUser.getLoginName() : "";
	}

	/**
	 * @desc 退款订单详情
	 */
	@Override
	public OrderRefundsDetailDto refundsDetail(String refundsId) {
		OrderRefundsDetailDto orderRefundsDto = new OrderRefundsDetailDto();
		TradeOrderRefundsVo refunds;
		try {
			refunds = tradeOrderRefundsService.findDetailByFinance(refundsId);
			orderRefundsDto.setRefundsReason(refunds.getRefundsReason());
			orderRefundsDto.setId(refunds.getId());
			orderRefundsDto.setTotalAmount(refunds.getTotalAmount());
			orderRefundsDto.setActualAmount(refunds.getActualAmount());
			orderRefundsDto.setRefundAmount(refunds.getTotalAmount());
			orderRefundsDto.setPreferentialAmount(refunds.getTotalPreferentialPrice());
			orderRefundsDto.setRefundMoneyTime(refunds.getRefundMoneyTime());
			orderRefundsDto.setPayTime(refunds.getTradeOrderVo().getTradeOrderPay().getPayTime());
			orderRefundsDto.setRefundNo(refunds.getRefundNo());
			orderRefundsDto.setRefundStatus(refunds.getRefundsStatus().ordinal());
			if (refunds.getPaymentMethod() != null) {
				orderRefundsDto.setPaymentMethod(refunds.getPaymentMethod().ordinal());
			}
			orderRefundsDto.setBuyerUserId(refunds.getUserId());
			orderRefundsDto.setBuyerUserName(refunds.getUserPhone());
			orderRefundsDto.setOrderNo(refunds.getOrderNo());
			// 回款金额
			orderRefundsDto.setApplyTime(refunds.getRefundMoneyTime());
			if (refunds.getTradeOrderVo().getTradeOrderPay() != null) {
				orderRefundsDto.setThirdTransNo(refunds.getTradeOrderVo().getTradeOrderPay().getReturns());
			}
			orderRefundsDto.setCreateTime(refunds.getCreateTime());

			orderRefundsDto.setDeliveryTime(refunds.getTradeOrderVo().getDeliveryTime());
			orderRefundsDto.setCreateOrderTime(refunds.getTradeOrderVo().getCreateTime());
			// orderRefundsDto.setAgentName(agentName);
			// orderRefundsDto.setAddress(address);
			orderRefundsDto.setDiscountName(refunds.getTradeOrderVo().getActivityType().getValue());
			orderRefundsDto.setSendTime(refunds.getTradeOrderVo().getDeliveryTime());
			if (refunds.getTradeOrderVo().getTradeOrderInvoice() != null) {
				orderRefundsDto.setInvoiceContent(refunds.getTradeOrderVo().getTradeOrderInvoice().getContext());
				orderRefundsDto.setInvoiceTile(refunds.getTradeOrderVo().getTradeOrderInvoice().getHead());
			}

			List<TradeOrderItemDto> itemList = Lists.newArrayList();
			for (TradeOrderRefundsItem item : refunds.getTradeOrderRefundsItem()) {

				TradeOrderItemDto itemDto = new TradeOrderItemDto();
				itemDto.setMainPicPrl(orderImagePrefix + item.getMainPicUrl());
				itemDto.setSkuName(item.getSkuName());
				itemDto.setQuantity(item.getQuantity());
				itemDto.setUnitPrice(item.getUnitPrice());
				itemDto.setTotalAmount(item.getAmount());
				//到店消费订单，还要查询退款码
				if(refunds.getType()==OrderTypeEnum.STORE_CONSUME_ORDER){
					List<TradeOrderItemDetail> detailList = tradeOrderItemDetailService.selectByOrderItemById(item.getOrderItemId());
					itemDto.setItemDetailList(BeanMapper.mapList(detailList, TradeOrderItemDetailDto.class));
				}
				itemList.add(itemDto);
			}
			orderRefundsDto.setOrderItems(itemList);

			List<TradeOrderRefundsCertificateVo> certificateVos = tradeOrderRefundsCertificateService
					.findByRefundsId(refundsId);
			if (!CollectionUtils.isEmpty(certificateVos)) {
				List<RefundsCertificateDto> certificates = Lists.newArrayList();
				for (TradeOrderRefundsCertificateVo certificateVo : certificateVos) {
					RefundsCertificateDto certificateDto = new RefundsCertificateDto();
					certificateDto.setCreateTime(certificateVo.getCreateTime());
					certificateDto.setRemark(certificateVo.getRemark());
					if (!CollectionUtils.isEmpty(certificateVo.getTradeOrderRefundsCertificateImg())) {
						List<String> certificateImages = Lists.newArrayList();
						for (TradeOrderRefundsCertificateImg img : certificateVo.getTradeOrderRefundsCertificateImg()) {
							// Begin 重构4.1 update by wusw 20160805
							certificateImages.add(realOrderImagePrefix + img.getImagePath());
							// End 重构4.1 update by wusw 20160805
						}
						certificateDto.setCertificateImage(certificateImages);
					}
					certificates.add(certificateDto);
				}
				orderRefundsDto.setCertificates(certificates);
			}
			StoreInfo storeInfo = storeInfoService.findById(refunds.getStoreId());
			orderRefundsDto.setStoreAddress(storeInfo.getAddress());
			orderRefundsDto.setStoreName(storeInfo.getStoreName());
			orderRefundsDto.setStoreTel(storeInfo.getMobile());
			orderRefundsDto.setStoreType(storeInfo.getType().getValue());
			return orderRefundsDto;

		} catch (Exception e) {
			logger.error("财务系统调用订单详情异常", e);
			return null;
		}
	}

	/**
	 * 退款订单待退款数统计
	 */
	@Override
	public Integer findUnRefundSum() {
		return tradeOrderRefundsService.findRefundUnPayCount();
	}

	/**
	 * 投诉订单待退款数统计
	 */
	@Override
	public Integer findComplainUnRefundSum() {
		return tradeOrderRefundsService.findComplainUnPayCount();
	}

	/*********************************** 财务系统 *****************************************************/

	@Override
	public Integer findCountCharge() throws Exception {
		return tradeOrderRefundsService.findCountChargeForFinance();
	}

	@Override
	public PageResultVo<OrderRefundsDto> findeChargeRefundsByParams(Map<String, Object> params) throws Exception {
		// 参数处理（例如设置默认参数等）
		this.convertParamsForFinance(params);
		int pageSize = Integer.valueOf(params.get("pageSize").toString());
		int pageNumber = Integer.valueOf(params.get("pageNumber").toString());
		PageUtils<TradeOrderRefundsChargeVo> page = tradeOrderRefundsService.findeChargeRefundsByParams(params,
				pageNumber, pageSize);
		List<OrderRefundsDto> dtoList = new ArrayList<OrderRefundsDto>();
		for (TradeOrderRefundsChargeVo vo : page.getList()) {
			OrderRefundsDto dto = new OrderRefundsDto();
			dto.setId(vo.getId());
			dto.setRefundNo(vo.getRefundNo());
			dto.setRefundAmount(vo.getTotalAmount());
			dto.setCreateTime(vo.getCreateTime());
			dto.setRefundMoneyTime(vo.getUpdateTime());
			dto.setPaymentMethod(vo.getPaymentMethod().ordinal());
			dto.setBuyerUserName(vo.getUserPhone());
			dto.setThirdTransNo(vo.getThirdOrderNo());
			dto.setOrderNo(vo.getOrderNo());
			// Begin 重构4.1 add by wusw 20160723
			dto.setOrderId(vo.getOrderId());
			if (vo.getRefundsStatus() != null) {
				if (vo.getRefundsStatus() == RefundsStatusEnum.SELLER_REFUNDING) {
					dto.setRefundStatus(0);
				} else {
					dto.setRefundStatus(1);
				}
			}
			// End 重构4.1 add by wusw 20160723
			dtoList.add(dto);
		}
		PageResultVo<OrderRefundsDto> result = new PageResultVo<OrderRefundsDto>(page.getPageNum(), page.getPageSize(),
				page.getTotal(), dtoList);
		return result;
	}

	@Override
	public List<OrderRefundsDto> findeChargeRefundsListByParams(Map<String, Object> params) throws Exception {
		List<OrderRefundsDto> result = new ArrayList<OrderRefundsDto>();
		// 参数处理（例如设置默认参数等）
		this.convertParamsForFinance(params);
		List<TradeOrderRefundsChargeVo> list = tradeOrderRefundsService.findeChargeRefundsListByParams(params);
		if(CollectionUtils.isEmpty(list)){
			return result;
		}
		if (list.size() > RECORD_NUM) {
			throw new ExceedRangeException("查询导出充值退款单超过一万条", new Throwable());
		}
		for (TradeOrderRefundsChargeVo vo : list) {
			OrderRefundsDto dto = new OrderRefundsDto();
			dto.setId(vo.getId());
			dto.setRefundNo(vo.getRefundNo());
			dto.setRefundAmount(vo.getTotalAmount());
			dto.setCreateTime(vo.getCreateTime());
			dto.setRefundMoneyTime(vo.getUpdateTime());
			dto.setPaymentMethod(vo.getPaymentMethod().ordinal());
			dto.setBuyerUserName(vo.getUserPhone());
			dto.setThirdTransNo(vo.getThirdOrderNo());
			dto.setOrderNo(vo.getOrderNo());
			// Begin 重构4.1 add by wusw 20160723
			dto.setOrderId(vo.getOrderId());
			if (vo.getRefundsStatus() != null) {
				if (vo.getRefundsStatus() == RefundsStatusEnum.SELLER_REFUNDING) {
					dto.setRefundStatus(0);
				} else {
					dto.setRefundStatus(1);
				}
			}
			// End 重构4.1 add by wusw 20160723
			result.add(dto);
		}
		return result;
	}

	// End 重构4.1 add by wusw 20160722

	// Begin v1.1.0 add by zengjz 20160917 统计订单退款金额、数量
	@Override
	public Map<String, Object> statisRefundsByParams(OrderRefundQueryParamDto orderRefundQueryParamDto)
			throws Exception {
		return tradeOrderRefundsService.statisRefundsByParams(orderRefundQueryParamDto);
	}
	// End v1.1.0 add by zengjz 20160917 统计订单退款金额、数量

	/**
	 * 
	 * @Description: 充值订单列表参数处理（用于财务系统）
	 * @param params
	 * @return void
	 * @author wusw
	 * @date 2016年7月22日
	 */
	private void convertParamsForFinance(Map<String, Object> params) {
		if (params == null) {
			params = new HashMap<String, Object>();
		}
		// 如果没有选择支付方式，默认查询第三方支付的充值订单
		if (params.get("paymentMethod") == null || StringUtils.isBlank(params.get("paymentMethod").toString())) {
			List<Integer> paymentMethodList = new ArrayList<Integer>();
			paymentMethodList.add(PayTypeEnum.ALIPAY.ordinal());
			paymentMethodList.add(PayTypeEnum.WXPAY.ordinal());
			params.put("paymentMethod", paymentMethodList);
		} else {
			List<Integer> paymentMethodList = new ArrayList<Integer>();
			paymentMethodList.add(new Integer(params.get("paymentMethod").toString()));
			params.put("paymentMethod", paymentMethodList);
		}
		if (params.get("startTime") == null || StringUtils.isBlank(params.get("startTime").toString())) {
			params.remove("startTime");
		}
		if (params.get("endTime") == null || StringUtils.isBlank(params.get("endTime").toString())) {
			params.remove("endTime");
		}
		// 默认订单类型为话费充值、流量充值订单
		List<OrderTypeEnum> typeList = new ArrayList<OrderTypeEnum>();
		typeList.add(OrderTypeEnum.PHONE_PAY_ORDER);
		typeList.add(OrderTypeEnum.TRAFFIC_PAY_ORDER);
		params.put("type", typeList);
		// 默认退款状态为退款中和退款完成
		List<RefundsStatusEnum> refundsStatusList = new ArrayList<RefundsStatusEnum>();
		refundsStatusList.add(RefundsStatusEnum.SELLER_REFUNDING);
		refundsStatusList.add(RefundsStatusEnum.REFUND_SUCCESS);
		params.put("refundsStatus", refundsStatusList);
		params.put("disabled", Disabled.valid);
	}

	@Override
	public <T> String applyRefund(T request, RefundOrderTypeEnum refundtype) throws Exception {
		switch (refundtype) {
			case PHYSICAL_ORDER:
				// 实物订单处理
				PhysOrderApplyRefundParamDto applyRefundParamDto = (PhysOrderApplyRefundParamDto) request;
				return physOrderApplyRefund(applyRefundParamDto);
			case STORE_CONSUMER:
				// 到店消费订单处理

			default:
				break;
		}
		return null;

	}

	/**
	 * @Description: 实物订单退款
	 * @param applyRefundParamDto
	 *            退款申请参数
	 * @return
	 * @throws Exception
	 * @author zengjizu
	 * @date 2017年2月27日
	 */
	private String physOrderApplyRefund(PhysOrderApplyRefundParamDto applyRefundParamDto) throws Exception {
		Assert.notNull(applyRefundParamDto.getTradeOrder());
		Assert.notNull(applyRefundParamDto.getTradeOrderItem());
		try {
			// 订单详情
			TradeOrder order = applyRefundParamDto.getTradeOrder();
			TradeOrderPay tradeOrderPay = tradeOrderPayService.selectByOrderId(order.getId());
			order.setTradeOrderPay(tradeOrderPay);
			// 订单项详情
			TradeOrderItem tradeOrderItem = applyRefundParamDto.getTradeOrderItem();

			TradeOrderRefunds orderRefunds = buildRefund(order, tradeOrderItem, tradeOrderItem.getActualAmount(),
					tradeOrderItem.getPreferentialPrice(), tradeOrderItem.getQuantity());
			// 退款单来源
			orderRefunds.setOrderResource(applyRefundParamDto.getOrderResource());
			orderRefunds.setOperator(order.getUserId());
			orderRefunds.setRefundsReason(applyRefundParamDto.getReason());
			orderRefunds.setMemo(applyRefundParamDto.getMemo());
			orderRefunds.setRefundsStatus(RefundsStatusEnum.WAIT_SELLER_VERIFY);
			// 退款凭证信息
			TradeOrderRefundsCertificateVo certificate = buildCertificate(orderRefunds.getId(), order.getUserId(),
					applyRefundParamDto.getRefundPics(),
					MSG + applyRefundParamDto.getReason() + "，退款说明：" + applyRefundParamDto.getMemo());

			tradeOrderRefundsService.insertRefunds(orderRefunds, certificate);
			// 返回结果
			return orderRefunds.getId();
		} catch (Exception e) {
			logger.error(DescriptConstants.SYS_ERROR, e);
			throw new Exception(DescriptConstants.SYS_ERROR, e);
		}

	}

}
