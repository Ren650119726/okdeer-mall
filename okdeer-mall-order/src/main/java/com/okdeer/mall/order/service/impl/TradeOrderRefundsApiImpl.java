/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @项目名称: yschome-mall 
 * @文件名称: TradeOrderRefundsApiImpl.java 
 * @Date: 2016年3月12日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsCertificateImg;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.service.TradeOrderActivityService;
import com.okdeer.mall.order.service.TradeOrderPayService;
import com.okdeer.mall.order.service.TradeOrderRefundsCertificateService;
import com.okdeer.mall.order.service.TradeOrderRefundsService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.vo.TradeOrderRefundsCertificateVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsChargeVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsVo;
import com.okdeer.mall.system.service.SysBuyerUserService;
import com.okdeer.mall.order.dto.OrderRefundsDetailDto;
import com.okdeer.mall.order.dto.OrderRefundsDto;
import com.okdeer.mall.order.dto.RefundsCertificateDto;
import com.okdeer.mall.order.dto.RefundsMoneyDto;
import com.okdeer.mall.order.dto.TradeOrderItemDto;
import com.okdeer.mall.order.exception.ExceedRangeException;
import com.okdeer.mall.order.service.ITradeOrderRefundsApi;
import com.okdeer.mall.common.vo.PageResultVo;

/**
 * 售后单接口
 * 
 * @pr yschome-mall
 * @author guocp
 * @date 2016年3月12日 上午10:47:50
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *    重构4.1            2016-7-13            wusw              添加退款中、第三方支付的充值退款记录数方法、查询充值订单列表方法（用于财务系统）
 *    重构4.1            2016-7-13            wusw              修改财务系统接口的关于退款状态的枚举值
 *    重构4.1            2016-8-5            wusw               修改退款凭证图片的七牛二级域名
 *    重构4.1            2016-8-18            zengj             如果是商家的优惠活动，就不需要传递优惠金额字段
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.ITradeOrderRefundsApi")
public class TradeOrderRefundsApiImpl implements ITradeOrderRefundsApi {

    private static final Logger logger = LoggerFactory.getLogger(TradeOrderRefundsApiImpl.class);

    /** 记录数 */
    private static final Integer RECORD_NUM = 10000;

    /**
     * 云存储订单图片路径二级域名
     */
    @Value("${orderImagePrefix}")
    private String orderImagePrefix;
    
    //Begin  重构4.1  add  by  wusw  20160805
  	/**
  	 * 云存储退款凭证、评价、投诉图片路径二级域名
  	 */
  	@Value("${realOrderImagePrefix}")
  	private String realOrderImagePrefix;
  	//End  重构4.1  add  by  wusw  20160805

    /**
     * 售后单service
     */
    @Autowired
    private TradeOrderRefundsService tradeOrderRefundsService;

    @Reference(version = "1.0.0", check = false)
    private StoreInfoServiceApi storeInfoService;

    @Autowired
    private TradeOrderRefundsCertificateService tradeOrderRefundsCertificateService;

    @Resource
    private TradeOrderService tradeOrderService;

    @Resource
    private TradeOrderPayService tradeOrderPayService;

    @Resource
    private SysBuyerUserService sysBuyerUserService;

    @Autowired
    private TradeOrderActivityService tradeOrderActivityService;

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
    public PageResultVo<OrderRefundsDto> orderRefund(Map<String, Object> params) throws Exception {
        try {
            int pageNum = Integer.valueOf(params.get("pageNum").toString());
            int pageSize = Integer.valueOf(params.get("pageSize").toString());
            List<OrderRefundsDto> orderRefunds = Lists.newArrayList();
            PageUtils<TradeOrderRefundsVo> pageList = tradeOrderRefundsService.findPageByFinance(params, pageNum,
                    pageSize);
            for (TradeOrderRefundsVo refundsVo : pageList.getList()) {
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
            PageResultVo<OrderRefundsDto> orderRefundPage = new PageResultVo<OrderRefundsDto>(pageList.getPageNum(),
                    pageList.getPageSize(), pageList.getTotal(), orderRefunds);
            return orderRefundPage;
        } catch (ServiceException e) {
            logger.error("查询退款单异常", e);
            throw new Exception("查询退款单异常", e);
        }
    }

    @Override
    public List<OrderRefundsDto> orderRefundExport(Map<String, Object> params) throws ExceedRangeException, Exception {

        if (tradeOrderRefundsService.findCountByFinance(params) > RECORD_NUM) {
            throw new ExceedRangeException("查询导出退款单异常", new Throwable());
        }

        try {
            List<OrderRefundsDto> orderRefunds = Lists.newArrayList();
            List<TradeOrderRefundsVo> list = tradeOrderRefundsService.findListByFinance(params);
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
            refundsMoney.setTotalAmount(order.getActualAmount().multiply(new BigDecimal("100")).intValue());
            refundsMoney.setRefundAmount(refunds.getTotalAmount().multiply(new BigDecimal("100")).intValue());
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
        refundsMoney.setTotalAmount(order.getActualAmount().multiply(new BigDecimal("100")).intValue());
        refundsMoney.setRefundAmount(refunds.getTotalAmount().multiply(new BigDecimal("100")).intValue());
        refundsMoney.setReason(refunds.getRefundsReason());
        if (refunds.getPaymentMethod() != null) {
            refundsMoney.setPaymentMethod(refunds.getPaymentMethod().ordinal());
        }
        refundsMoney.setApplicant(refunds.getOperator());
        refundsMoney.setBuyerUserId(refunds.getUserId());
        refundsMoney.setBuyerUserName(getBuyserName(refunds.getUserId()));
        refundsMoney.setStoreUserId(storeInfoService.getBossIdByStoreId(refunds.getStoreId()));
        refundsMoney.setCreateTime(refunds.getCreateTime());
        refundsMoney.setOutTradeNo(refunds.getTradeNum());
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
    	//判断参数是否为空
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

            for (TradeOrderRefundsItem item : refunds.getTradeOrderRefundsItem()) {

                TradeOrderItemDto itemDto = new TradeOrderItemDto();
                itemDto.setMainPicPrl(orderImagePrefix + item.getMainPicUrl());
                itemDto.setSkuName(item.getSkuName());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setUnitPrice(item.getUnitPrice());
                orderRefundsDto.setOrderItems(Lists.newArrayList(itemDto));
            }

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
                        	//Begin  重构4.1  update  by  wusw  20160805
                        	certificateImages.add(realOrderImagePrefix + img.getImagePath());
                        	//End  重构4.1  update  by  wusw  20160805
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

    //Begin 重构4.1      add by wusw  20160722
    /***********************************财务系统*****************************************************/
	/**
	 * (non-Javadoc)
	 * @see com.yschome.api.mall.order.service.ITradeOrderRefundsApi#findCountCharge()
	 */
	@Override
	public Integer findCountCharge()  throws Exception{
		return tradeOrderRefundsService.findCountChargeForFinance();
	}
	

	/**
	 * (non-Javadoc)
	 * @see com.yschome.api.mall.order.service.ITradeOrderRefundsApi#findeChargeRefundsByParams(java.util.Map)
	 */
	@Override
	public PageResultVo<OrderRefundsDto> findeChargeRefundsByParams(Map<String, Object> params) throws Exception {
		int pageSize = Integer.valueOf(params.get("pageSize").toString());
        int pageNumber = Integer.valueOf(params.get("pageNumber").toString());
        PageUtils<TradeOrderRefundsChargeVo> page = tradeOrderRefundsService.findeChargeRefundsByParams(params, pageNumber, pageSize);
        List<OrderRefundsDto> dtoList = new ArrayList<OrderRefundsDto>();
        for (TradeOrderRefundsChargeVo vo:page.getList()) {
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
        	//Begin 重构4.1  add by wusw  20160723
        	dto.setOrderId(vo.getOrderId());
        	if (vo.getRefundsStatus() != null) {
        		if (vo.getRefundsStatus() == RefundsStatusEnum.SELLER_REFUNDING) {
        			dto.setRefundStatus(0);
        		} else {
        			dto.setRefundStatus(1);
        		}
        	}
        	//End 重构4.1  add by wusw  20160723
        	dtoList.add(dto);
        }
        PageResultVo<OrderRefundsDto> result = new PageResultVo<OrderRefundsDto>(page.getPageNum(),
                page.getPageSize(), page.getTotal(), dtoList);
		return result;
	}

	/**
	 * (non-Javadoc)
	 * @see com.yschome.api.mall.order.service.ITradeOrderRefundsApi#findeChargeRefundsListByParams(java.util.Map)
	 */
	@Override
	public List<OrderRefundsDto> findeChargeRefundsListByParams(Map<String, Object> params) throws Exception {
		List<TradeOrderRefundsChargeVo> list = tradeOrderRefundsService.findeChargeRefundsListByParams(params);
		if (list != null && list.size() > RECORD_NUM) {
            throw new ExceedRangeException("查询导出充值退款单超过一万条", new Throwable());
        }
        List<OrderRefundsDto> result = new ArrayList<OrderRefundsDto>();
        for (TradeOrderRefundsChargeVo vo:list) {
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
        	//Begin 重构4.1  add by wusw  20160723
        	dto.setOrderId(vo.getOrderId());
        	if (vo.getRefundsStatus() != null) {
        		if (vo.getRefundsStatus() == RefundsStatusEnum.SELLER_REFUNDING) {
        			dto.setRefundStatus(0);
        		} else {
        			dto.setRefundStatus(1);
        		}
        	}
        	//End 重构4.1  add by wusw  20160723
        	result.add(dto);
        }
		return result;
	}
	
	//End 重构4.1  add by wusw  20160722
	
	// Begin v1.1.0 add by zengjz 20160917 统计订单退款金额、数量
	@Override
	public Map<String, Object> statisRefundsByParams(Map<String, Object> params) throws Exception {
		
		return tradeOrderRefundsService.statisRefundsByParams(params);
	}
	// End v1.1.0 add by zengjz 20160917 统计订单退款金额、数量
}
