
package com.okdeer.mall.order.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.common.vo.PageResultVo;
import com.okdeer.mall.order.dto.OrderRefundQueryParamDto;
import com.okdeer.mall.order.dto.OrderRefundsDetailDto;
import com.okdeer.mall.order.dto.OrderRefundsDto;
import com.okdeer.mall.order.dto.RefundsCertificateDto;
import com.okdeer.mall.order.dto.RefundsMoneyDto;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundParamDto;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundResultDto;
import com.okdeer.mall.order.dto.TradeOrderDto;
import com.okdeer.mall.order.dto.TradeOrderItemDetailDto;
import com.okdeer.mall.order.dto.TradeOrderItemDto;
import com.okdeer.mall.order.dto.TradeOrderRefundsDto;
import com.okdeer.mall.order.dto.TradeOrderRefundsItemDto;
import com.okdeer.mall.order.dto.TradeOrderRefundsLogisticsDto;
import com.okdeer.mall.order.dto.TradeOrderRefundsParamDto;
import com.okdeer.mall.order.dto.TradeOrderRefundsQueryCdtDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderInvoice;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsCertificateImg;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.entity.TradeOrderRefundsLogistics;
import com.okdeer.mall.order.enums.ConsumeStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayTypeEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.exception.ExceedRangeException;
import com.okdeer.mall.order.service.TradeOrderActivityService;
import com.okdeer.mall.order.service.TradeOrderInvoiceService;
import com.okdeer.mall.order.service.TradeOrderItemDetailService;
import com.okdeer.mall.order.service.TradeOrderPayService;
import com.okdeer.mall.order.service.TradeOrderRefundsApi;
import com.okdeer.mall.order.service.TradeOrderRefundsCertificateService;
import com.okdeer.mall.order.service.TradeOrderRefundsItemService;
import com.okdeer.mall.order.service.TradeOrderRefundsLogisticsService;
import com.okdeer.mall.order.service.TradeOrderRefundsService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.vo.TradeOrderRefundsCertificateVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsChargeVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsVo;
import com.okdeer.mall.system.service.SysBuyerUserService;

/**
 * 
 * ClassName: TradeOrderRefundsApiImpl 
 * @Description: 退款服务api实现
 * @author zengjizu
 * @date 2017年10月26日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderRefundsApi")
public class TradeOrderRefundsApiImpl implements TradeOrderRefundsApi {

	private static final Logger logger = LoggerFactory.getLogger(TradeOrderRefundsApiImpl.class);

	/** 记录数 */
	private static final Integer RECORD_NUM = 10000;


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
	private TradeOrderService tradeOrderService;


	@Autowired
	private TradeOrderItemDetailService tradeOrderItemDetailService;

	@Autowired
	private TradeOrderRefundsService tradeOrderRefundsService;

	@Autowired
	private TradeOrderActivityService tradeOrderActivityService;

	@Autowired
	private TradeOrderPayService tradeOrderPayService;

	@Resource
	private SysBuyerUserService sysBuyerUserService;

	@Autowired
	private TradeOrderRefundsCertificateService tradeOrderRefundsCertificateService;

	@Autowired
	private TradeOrderInvoiceService tradeOrderInvoiceService;

	@Autowired
	private TradeOrderRefundsItemService tradeOrderRefundsItemService;

	@Autowired
	private TradeOrderRefundsLogisticsService tradeOrderRefundsLogisticsService;
	

	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoService;

	/**
	 * 更新退款单状态
	 */
	@Override
	public boolean updateRefundsStatus(String refundsId, String status, String userId) {
		logger.info("售后单同步状态refundsId:{}status:{}userId:{}" ,refundsId,status,userId);
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

		try {

			TradeOrderRefunds refunds = tradeOrderRefundsService.findById(refundsId);
			TradeOrder tradeOrder = tradeOrderService.selectById(refunds.getOrderId());
			TradeOrderPay tradeOrderPay = tradeOrderPayService.selectByOrderId(refunds.getOrderId());

			orderRefundsDto.setRefundsReason(refunds.getRefundsReason());
			orderRefundsDto.setId(refunds.getId());
			orderRefundsDto.setTotalAmount(refunds.getTotalIncome());
			orderRefundsDto.setActualAmount(tradeOrder.getActualAmount());
			orderRefundsDto.setRefundAmount(refunds.getTotalAmount());
			orderRefundsDto.setPreferentialAmount(refunds.getTotalPreferentialPrice());
			orderRefundsDto.setRefundMoneyTime(refunds.getRefundMoneyTime());
			if (tradeOrderPay != null) {
				orderRefundsDto.setPayTime(tradeOrderPay.getPayTime());
				orderRefundsDto.setThirdTransNo(tradeOrderPay.getReturns());
			}

			orderRefundsDto.setRefundNo(refunds.getRefundNo());
			orderRefundsDto.setRefundStatus(refunds.getRefundsStatus().ordinal());
			if (refunds.getPaymentMethod() != null) {
				orderRefundsDto.setPaymentMethod(refunds.getPaymentMethod().ordinal());
			}
			orderRefundsDto.setBuyerUserId(refunds.getUserId());
			orderRefundsDto.setBuyerUserName(tradeOrder.getUserPhone());
			orderRefundsDto.setOrderNo(refunds.getOrderNo());
			// 回款金额
			orderRefundsDto.setApplyTime(refunds.getRefundMoneyTime());
			orderRefundsDto.setCreateTime(refunds.getCreateTime());

			orderRefundsDto.setDeliveryTime(tradeOrder.getDeliveryTime());
			orderRefundsDto.setCreateOrderTime(tradeOrder.getCreateTime());
			orderRefundsDto.setDiscountName(tradeOrder.getActivityType().getValue());
			orderRefundsDto.setSendTime(tradeOrder.getDeliveryTime());

			TradeOrderInvoice tradeOrderInvoice = tradeOrderInvoiceService.selectByOrderId(refunds.getOrderId());

			if (tradeOrderInvoice != null) {
				orderRefundsDto.setInvoiceContent(tradeOrderInvoice.getContext());
				orderRefundsDto.setInvoiceTile(tradeOrderInvoice.getHead());
			}

			List<TradeOrderRefundsItem> tradeOrderRefundsItems = tradeOrderRefundsItemService
					.getTradeOrderRefundsItemByRefundsId(refundsId);

			List<TradeOrderItemDto> itemList = Lists.newArrayList();
			for (TradeOrderRefundsItem item : tradeOrderRefundsItems) {

				TradeOrderItemDto itemDto = new TradeOrderItemDto();
				itemDto.setMainPicPrl(orderImagePrefix + item.getMainPicUrl());
				itemDto.setSkuName(item.getSkuName());
				itemDto.setQuantity(item.getQuantity());
				itemDto.setUnitPrice(item.getUnitPrice());
				itemDto.setTotalAmount(item.getIncome());
				// 到店消费订单，还要查询退款码
				if (refunds.getType() == OrderTypeEnum.STORE_CONSUME_ORDER) {
					List<TradeOrderItemDetail> detailList = tradeOrderItemDetailService
							.selectByOrderItemById(item.getOrderItemId());
					List<TradeOrderItemDetailDto> dtoList = Lists.newArrayList();
					for (TradeOrderItemDetail tradeOrderItemDetail : detailList) {
						if (tradeOrderItemDetail.getStatus() == ConsumeStatusEnum.refund) {
							dtoList.add(BeanMapper.map(tradeOrderItemDetail, TradeOrderItemDetailDto.class));
						}
					}
					itemDto.setItemDetailList(dtoList);
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
		if (CollectionUtils.isEmpty(list)) {
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
	public Response<TradeOrderApplyRefundResultDto> applyRefund(
			TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto) throws MallApiException {
		return tradeOrderRefundsService.processApplyRefund(tradeOrderApplyRefundParamDto);
	}

	@Override
	@SuppressWarnings("unchecked")
	public PageUtils<TradeOrderRefundsDto> findList(TradeOrderRefundsParamDto tradeOrderRefundsParam, int pageNum,
			int pageSize) throws MallApiException {
		PageUtils<TradeOrderRefunds> pages = tradeOrderRefundsService.findList(tradeOrderRefundsParam, pageNum,
				pageSize);
		PageUtils<TradeOrderRefundsDto> dtoPages = pages.toBean(TradeOrderRefundsDto.class);
		if (tradeOrderRefundsParam.isJoinOrder()) {
			List<TradeOrderRefundsDto> tradeOrderRefundsList = dtoPages.getList();
			if (CollectionUtils.isEmpty(tradeOrderRefundsList)) {
				return dtoPages;
			}
			try {
				for (TradeOrderRefundsDto tradeOrderRefundsDto : tradeOrderRefundsList) {
					TradeOrder tradeOrder = tradeOrderService.selectById(tradeOrderRefundsDto.getOrderId());
					if (tradeOrder != null) {
						tradeOrderRefundsDto.setTradeOrderDto(BeanMapper.map(tradeOrder, TradeOrderDto.class));
					}
				}
			} catch (ServiceException e) {
				throw new MallApiException(e);
			}
		}
		return dtoPages;
	}

	@Override
	public TradeOrderRefundsDto findById(String refundsId, TradeOrderRefundsQueryCdtDto tradeOrderRefundsQueryCdtDto)
			throws MallApiException {
		TradeOrderRefunds tradeOrderRefunds = tradeOrderRefundsService.findById(refundsId);
		if (tradeOrderRefunds == null) {
			return null;
		}
		TradeOrderRefundsDto tradeOrderRefundsDto = BeanMapper.map(tradeOrderRefunds, TradeOrderRefundsDto.class);

		if (tradeOrderRefundsQueryCdtDto.isQueryRefundsItem()) {
			//查询退款单项信息
			List<TradeOrderRefundsItem> refundItemList = tradeOrderRefundsItemService
					.getTradeOrderRefundsItemByRefundsId(refundsId);
			if (refundItemList != null) {
				tradeOrderRefundsDto.setRefundItemList(BeanMapper.mapList(refundItemList, TradeOrderRefundsItemDto.class));
			}
		}
		if (tradeOrderRefundsQueryCdtDto.isQueryOrder()) {
			//查询订单信息
			try {
				TradeOrder tradeOrder = tradeOrderService.selectById(tradeOrderRefunds.getOrderId());
				if (tradeOrder != null) {
					tradeOrderRefundsDto.setTradeOrderDto(BeanMapper.map(tradeOrder, TradeOrderDto.class));
				}
			} catch (ServiceException e) {
				logger.error("查询订单信息出错", e);
				throw new MallApiException(e);
			}
		}

		if (tradeOrderRefundsQueryCdtDto.isQueryRefundsLogistics()) {
			// 查询物流信息
			TradeOrderRefundsLogistics tradeOrderRefundsLogistics = tradeOrderRefundsLogisticsService
					.findByRefundsId(tradeOrderRefunds.getId());
			if (tradeOrderRefundsLogistics != null) {
				tradeOrderRefundsDto.setTradeOrderRefundsLogistics(
						BeanMapper.map(tradeOrderRefundsLogistics, TradeOrderRefundsLogisticsDto.class));
			}
		}
		
		if(tradeOrderRefundsQueryCdtDto.isQueryCertificate()){
			//查询凭证信息
			List<TradeOrderRefundsCertificateVo> certificateList = Lists.newArrayList();
			List<TradeOrderRefundsCertificateVo> certificateVos = tradeOrderRefundsCertificateService.findByRefundsId(refundsId);
			if(StringUtils.isEmpty(tradeOrderRefundsQueryCdtDto.getCertificateUserId())){
				tradeOrderRefundsDto.setCertificateList(certificateVos);
			}else{
				for (TradeOrderRefundsCertificateVo tradeOrderRefundsCertificateVo : certificateVos) {
					if(tradeOrderRefundsCertificateVo.getOperator().equals(tradeOrderRefundsQueryCdtDto.getCertificateUserId())){
						certificateList.add(tradeOrderRefundsCertificateVo);
					}
				}
				tradeOrderRefundsDto.setCertificateList(certificateList);
			}
		}
		return tradeOrderRefundsDto;
	}

}
