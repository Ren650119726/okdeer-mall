
package com.okdeer.mall.order.service.impl;

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
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsService;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.common.utils.RobotUserUtil;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.service.MemberConsigneeAddressService;
import com.okdeer.mall.order.bo.TradeOrderRefundContextBo;
import com.okdeer.mall.order.constant.mq.PayMessageConstant;
import com.okdeer.mall.order.dto.OrderRefundQueryParamDto;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundParamDto;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundResultDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsCertificate;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.entity.TradeOrderRefundsLogistics;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.PickUpTypeEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsCertificateMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsItemMapper;
import com.okdeer.mall.order.service.StockOperateService;
import com.okdeer.mall.order.service.TradeOrderItemService;
import com.okdeer.mall.order.service.TradeOrderPayService;
import com.okdeer.mall.order.service.TradeOrderRefundProcessCallback;
import com.okdeer.mall.order.service.TradeOrderRefundProcessService;
import com.okdeer.mall.order.service.TradeOrderRefundsCertificateService;
import com.okdeer.mall.order.service.TradeOrderRefundsItemService;
import com.okdeer.mall.order.service.TradeOrderRefundsLogisticsService;
import com.okdeer.mall.order.service.TradeOrderRefundsService;
import com.okdeer.mall.order.service.TradeOrderRefundsServiceApi;
import com.okdeer.mall.order.service.TradeOrderRefundsTraceService;
import com.okdeer.mall.order.utils.PageQueryUtils;
import com.okdeer.mall.order.vo.TradeOrderRefundsCertificateVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsChargeVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsExportVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsQueryVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsStatusVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsVo;
import com.okdeer.mall.order.vo.TradeOrderVo;
import com.okdeer.mall.system.mq.RollbackMQProducer;
import com.okdeer.mall.system.utils.ConvertUtil;

/**
 * ClassName: TradeOrderRefundsServiceImpl 
 * @Description: 退款处理
 * @author zengjizu
 * @date 2017年10月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderRefundsServiceApi")
public class TradeOrderRefundsServiceImpl extends AbstractTradeOrderRefundsService
		implements TradeOrderRefundsService, TradeOrderRefundsServiceApi, PayMessageConstant {

	private static final Logger logger = LoggerFactory.getLogger(TradeOrderRefundsServiceImpl.class);

	/**
	 * 满减满折DAO
	 */
	@Resource
	private ActivityDiscountService activityDiscountService;

	@Resource
	private TradeOrderItemService tradeOrderItemService;

	@Resource
	private TradeOrderItemMapper tradeOrderItemMapper;

	@Resource
	private TradeOrderPayService tradeOrderPayService;

	@Resource
	private TradeOrderMapper tradeOrderMapper;

	@Resource
	private TradeOrderRefundsItemMapper tradeOrderRefundsItemMapper;

	/**
	 * 代金券DAO
	 */
	@Resource
	private ActivityCollectCouponsService activityCollectCouponsService;

	@Resource
	private TradeOrderRefundsCertificateMapper tradeOrderRefundsCertificateMapper;

	@Resource
	private MemberConsigneeAddressService memberConsigneeAddressService;

	@Resource
	private TradeOrderRefundsLogisticsService tradeOrderRefundsLogisticsService;

	@Resource
	private TradeOrderRefundsCertificateService tradeOrderRefundsCertificateService;

	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoService;

	@Autowired
	private TradeOrderRefundsItemService tradeOrderRefundsItemService;

	/**
	 * 回滚消息生产者
	 */
	@Resource
	private RollbackMQProducer rollbackMQProducer;

	@Resource
	private TradeOrderRefundsTraceService tradeOrderRefundsTraceService;

	@Resource
	private StockOperateService stockOperateService;

	/**
	 * 根据主键查询退款单
	 *
	 * @param id
	 *            主键ID
	 */
	public TradeOrderRefunds getById(String id) {
		return findById(id);
	}

	/**
	 * 根据主键查询退款单
	 *
	 * @param id
	 *            主键ID
	 */
	public TradeOrderRefunds findById(String id) {
		return tradeOrderRefundsMapper.selectByPrimaryKey(id);
	}

	/**
	 * @desc 根据订单项ID判断是否已申请退款
	 */
	@Override
	public boolean isRefundOrderItemId(String orderId) {
		int count = tradeOrderRefundsItemMapper.selectCountOrderItemId(orderId);
		return count > 0;
	}

	/**
	 * 查询退款单详情
	 */
	@Override
	public TradeOrderRefunds findInfoById(String id) throws Exception {
		return tradeOrderRefundsMapper.findInfoById(id);
	}

	/**
	 * 根据主键查询退款单
	 */
	public List<TradeOrderRefunds> findByIds(List<String> ids) {
		return tradeOrderRefundsMapper.selectByPrimaryKeys(ids);
	}

	/**
	 * @desc 新增退款单
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertRefunds(TradeOrderRefunds orderRefunds, TradeOrderRefundsCertificateVo certificate)
			throws Exception {
		// 保存退款单
		tradeOrderRefundsMapper.insertSelective(orderRefunds);
		// 批量保存退款单项
		this.tradeOrderRefundsItemService.insert(orderRefunds.getTradeOrderRefundsItem());
		// 保存退款凭证
		tradeOrderRefundsCertificateService.addCertificate(certificate);
	}

	/**
	 * 更新退款单并添加凭证
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateRefunds(TradeOrderRefunds orderRefunds, TradeOrderRefundsCertificateVo certificate) {
		// 保存退款凭证
		if (certificate != null) {
			tradeOrderRefundsCertificateService.addCertificate(certificate);
		}
		// 更新退款单
		updateRefunds(orderRefunds);
	}

	/**
	 * @desc 更新退款单（根据状态判断是否需要支付）
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateRefunds(TradeOrderRefunds orderRefunds) {
		// Begin Bug:14143 added by maojj 2016-10-11
		// 更改退款单状态时，保存退款轨迹
		tradeOrderRefundsTraceService.saveRefundTrace(orderRefunds);
		// End added by maojj 2016-10-11
		return tradeOrderRefundsMapper.updateByPrimaryKeySelective(orderRefunds);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void applyCustomerIntervent(TradeOrderRefunds orderRefunds) throws Exception {
		updateTradeOrderRefund(orderRefunds.getId(), RefundsStatusEnum.APPLY_CUSTOMER_SERVICE_INTERVENE,
				tradeOrderRefundContext -> createUpdateTradeOrderRefunds(tradeOrderRefundContext,
						RefundsStatusEnum.APPLY_CUSTOMER_SERVICE_INTERVENE, orderRefunds.getOperator()));
	}

	/**
	 * 更新退款单并添加凭证
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void alterRefunds(TradeOrderRefunds orderRefunds, TradeOrderRefundsCertificateVo certificate) {
		TradeOrderRefundsCertificate oldCertificate = tradeOrderRefundsCertificateMapper
				.findFirstByRefundsId(orderRefunds.getId());
		tradeOrderRefundsCertificateMapper.deleteByPrimaryKey(oldCertificate.getId());
		// 保存退款凭证
		tradeOrderRefundsCertificateService.addCertificate(certificate);
		// 更新退款单
		updateRefunds(orderRefunds);
	}

	/**
	 * 卖家操作同意退单
	 *
	 * @param id
	 *            退货单ID
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateStatusWithAgree(String id, String addressId, String remark, String userId) throws Exception {
		updateTradeOrderRefund(id, RefundsStatusEnum.WAIT_BUYER_RETURN_GOODS, tradeOrderRefundContext -> {
			createUpdateTradeOrderRefunds(tradeOrderRefundContext, RefundsStatusEnum.WAIT_BUYER_RETURN_GOODS, userId);
			try {
				// 新增退货地址
				addRefundsLogistics(id, addressId);
				addRefundsCerticate(tradeOrderRefundContext, id, remark, userId);
			} catch (Exception e) {
				throw new MallApiException(e);
			}
		});
	}

	/**
	 * 卖家操作拒绝退单
	 * 
	 * @param id
	 *            退款单ID
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateStatusWithRefuse(String id, String remark, String userId) throws Exception {
		updateTradeOrderRefund(id, RefundsStatusEnum.SELLER_REJECT_APPLY, tradeOrderRefundContext -> {
			TradeOrderRefunds tradeOrderRefunds = createUpdateTradeOrderRefunds(tradeOrderRefundContext,
					RefundsStatusEnum.SELLER_REJECT_APPLY, userId);
			tradeOrderRefunds.setRefuseReson(remark);
			addRefundsCerticate(tradeOrderRefundContext, id, remark, userId);
		});
	}

	/**
	 * 卖家同意退款
	 * 
	 * @param id
	 *            退款单ID
	 * @param userId
	 *            卖家ID
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateAgreePayment(String id, String userId) throws Exception {
		List<String> rpcIdList = new ArrayList<>();
		try {
			updateTradeOrderRefund(id, RefundsStatusEnum.SELLER_REJECT_APPLY, tradeOrderRefundContext -> {

				TradeOrderRefunds oldTradeOrderRefunds = tradeOrderRefundContext.getTradeOrderRefunds();

				TradeOrderRefunds tradeOrderRefunds = createUpdateTradeOrderRefunds(tradeOrderRefundContext,
						RefundsStatusEnum.SELLER_REFUNDING, userId);
				if (PayWayEnum.PAY_ONLINE != tradeOrderRefundContext.getTradeOrder().getPayWay()) {
					// 非线上支付
					tradeOrderRefunds.setRefundMoneyTime(new Date());
					tradeOrderRefunds.setRefundsStatus(RefundsStatusEnum.REFUND_SUCCESS);
				}
				addRefundsCerticate(tradeOrderRefundContext, id, "同意退款", userId);
				returnStock(oldTradeOrderRefunds, tradeOrderRefundContext, rpcIdList);
				try {
					tradeOrderRefundContext
							.setSotreUserId(storeInfoService.getBossIdByStoreId(oldTradeOrderRefunds.getStoreId()));
				} catch (ServiceException e) {
					throw new MallApiException(e);
				}

			});
		} catch (Exception e) {
			rollbackMQProducer.sendStockRollbackMsg(rpcIdList);
			throw e;
		}
	}

	/**
	 * 客服处理更新订单状态
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateByCustomer(String refundsId, RefundsStatusEnum status, String userId) throws Exception {
		logger.error("客服处理更新订单状态：refundsId ={},status={},userId={}", refundsId, status.ordinal(), userId);

		List<String> rpcIdList = new ArrayList<>();
		try {
			updateTradeOrderRefund(refundsId, status, tradeOrderRefundContext -> {
				TradeOrderRefunds oldTradeOrderRefunds = tradeOrderRefundContext.getTradeOrderRefunds();
				TradeOrderRefunds tradeOrderRefunds = createUpdateTradeOrderRefunds(tradeOrderRefundContext, status,
						userId);
				if (PayWayEnum.PAY_ONLINE != tradeOrderRefundContext.getTradeOrder().getPayWay()) {
					// 非线上支付
					tradeOrderRefunds.setRefundMoneyTime(new Date());
					tradeOrderRefunds.setRefundsStatus(RefundsStatusEnum.REFUND_SUCCESS);
				}
				addRefundsCerticate(tradeOrderRefundContext, refundsId, "同意退款", userId);
				// 返还库存
				returnStock(oldTradeOrderRefunds, tradeOrderRefundContext, rpcIdList);
				try {
					tradeOrderRefundContext
							.setSotreUserId(storeInfoService.getBossIdByStoreId(oldTradeOrderRefunds.getStoreId()));
				} catch (ServiceException e) {
					throw new MallApiException(e);
				}
			});
		} catch (Exception e) {
			rollbackMQProducer.sendStockRollbackMsg(rpcIdList);
			throw e;
		}
		logger.error("客服处理更新订单状态成功");
	}

	/**
	 * @Description: 返还库存
	 * @param tradeOrderRefunds
	 * @param tradeOrderRefundContext
	 * @param rpcIdList
	 * @throws MallApiException
	 * @author zengjizu
	 * @date 2017年10月14日
	 */
	private void returnStock(TradeOrderRefunds tradeOrderRefunds, TradeOrderRefundContextBo tradeOrderRefundContext,
			List<String> rpcIdList) throws MallApiException {
		// 查询退款单项
		if (CollectionUtils.isEmpty(tradeOrderRefundContext.getTradeOrderRefundsItemList())) {
			List<TradeOrderRefundsItem> refundItemList = tradeOrderRefundsItemMapper
					.getTradeOrderRefundsItemByRefundsId(tradeOrderRefunds.getId());
			tradeOrderRefundContext.setTradeOrderRefundsItemList(refundItemList);
		}
		tradeOrderRefunds.setTradeOrderRefundsItem(tradeOrderRefundContext.getTradeOrderRefundsItemList());
		try {
			stockOperateService.recycleStockByRefund(tradeOrderRefundContext.getTradeOrder(), tradeOrderRefunds,
					rpcIdList);
		} catch (Exception e) {
			throw new MallApiException(e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateWithRevocatory(TradeOrderRefunds refunds, TradeOrderRefundsCertificateVo certificate)
			throws Exception {
		// 撤销退款申请
		updateTradeOrderRefund(refunds.getId(), RefundsStatusEnum.BUYER_REPEAL_REFUND, tradeOrderRefundContext -> {
			TradeOrderRefunds tradeOrderRefunds = tradeOrderRefundContext.getTradeOrderRefunds();
			createUpdateTradeOrderRefunds(tradeOrderRefundContext, RefundsStatusEnum.BUYER_REPEAL_REFUND, null);
			try {
				tradeOrderRefundContext
						.setSotreUserId(storeInfoService.getBossIdByStoreId(tradeOrderRefunds.getStoreId()));
			} catch (ServiceException e) {
				throw new MallApiException(e);
			}
			addRefundsCerticate(tradeOrderRefundContext, refunds.getId(), certificate.getRemark(),
					refunds.getOperator());
		});
	}

	/**
	 * 卖家拒绝退款
	 * 
	 * @param id
	 *            退款单ID
	 * @param userId
	 *            卖家ID
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateRefusePayment(String id, String userId, String reason) throws Exception {
		updateTradeOrderRefund(id, RefundsStatusEnum.SELLER_REJECT_REFUND, tradeOrderRefundContext -> {
			TradeOrderRefunds tradeOrderRefunds = createUpdateTradeOrderRefunds(tradeOrderRefundContext,
					RefundsStatusEnum.SELLER_REJECT_REFUND, userId);
			tradeOrderRefunds.setRefuseReson(reason);
			String remark = "您拒绝了退款,拒绝原因：";
			addRefundsCerticate(tradeOrderRefundContext, id, remark + reason, userId);
		});
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int delete(TradeOrderRefunds tradeOrderRefunds) throws Exception {
		return tradeOrderRefundsMapper.delete(tradeOrderRefunds);
	}

	/**
	 * 新增退货操作记录
	 * 
	 * @param id
	 *            退款单ID
	 * @param remark
	 *            操作凭证说明
	 */
	private void addRefundsCerticate(TradeOrderRefundContextBo tradeOrderRefundContextBo, String refundId,
			String remark, String userId) {
		TradeOrderRefundsCertificate certificate = new TradeOrderRefundsCertificate();
		certificate.setId(UuidUtils.getUuid());
		certificate.setOperator(userId);
		certificate.setRemark(remark);
		certificate.setCreateTime(new Date());
		certificate.setRefundsId(refundId);
		tradeOrderRefundContextBo.setTradeOrderRefundsCertificate(certificate);
	}

	/**
	 * 新增退货地址
	 * 
	 * @param id
	 *            退款单ID
	 * @param addressId
	 *            退货地址ID
	 */
	private void addRefundsLogistics(String id, String addressId) throws Exception {
		MemberConsigneeAddress address = findMemberAddress(addressId);
		TradeOrderRefundsLogistics logistics = new TradeOrderRefundsLogistics();
		logistics.setId(UuidUtils.getUuid());
		logistics.setRefundsId(id);
		logistics.setArea(address.getArea());
		logistics.setAddress(address.getAddress());
		logistics.setAreaId(address.getAreaId());
		logistics.setCityId(address.getCityId());
		logistics.setConsigneeName(address.getConsigneeName());
		logistics.setMobile(address.getMobile());
		logistics.setProvinceId(address.getProvinceId());
		logistics.setTelephone(address.getTelephone());
		logistics.setZipCode(address.getZipCode());
		tradeOrderRefundsLogisticsService.add(logistics);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveLogistics(TradeOrderRefunds refunds, TradeOrderRefundsLogistics logistics) throws Exception {
		updateTradeOrderRefund(refunds.getId(), RefundsStatusEnum.WAIT_SELLER_REFUND, tradeOrderRefundContext -> {
			TradeOrderRefunds tradeOrderRefunds = createUpdateTradeOrderRefunds(tradeOrderRefundContext,
					RefundsStatusEnum.WAIT_SELLER_REFUND, refunds.getOperator());
			tradeOrderRefunds.setLogisticsType(refunds.getLogisticsType());
			refunds.setDeliveryime(new Date());
			try {
				// 新增退货操作记录
				String remark = "买家选择：" + refunds.getLogisticsType().getValue();
				// 物流发货：保存物流信息
				if (logistics != null) {
					tradeOrderRefundsLogisticsService.modifyById(logistics);
					remark += "， 物流公司：" + logistics.getLogisticsCompanyName() + "，物流单号：" + logistics.getLogisticsNo();
				}
				addRefundsCerticate(tradeOrderRefundContext, refunds.getId(), remark, refunds.getUserId());
			} catch (Exception e) {
				throw new MallApiException(e);
			}
		});
	}

	/**
	 * @Description: 更新退款单信息
	 * @param id
	 * @param updateStaus
	 * @param tradeOrderRefundProcessCallback
	 * @throws Exception
	 * @author zengjizu
	 * @date 2017年10月14日
	 */
	private void updateTradeOrderRefund(String id, RefundsStatusEnum updateStaus,
			TradeOrderRefundProcessCallback tradeOrderRefundProcessCallback) throws Exception {
		TradeOrderRefundContextBo tradeOrderRefundContext = createTradeOrderRefundContext(id);
		TradeOrderRefundProcessService tradeOrderRefundProcessService = getTradeOrderRefundProcessService(
				tradeOrderRefundContext.getTradeOrderRefunds().getType());
		tradeOrderRefundProcessService.updateTradeOrderRefund(tradeOrderRefundContext, updateStaus,
				tradeOrderRefundProcessCallback);
	}

	/**
	 * 
	 * @desc 退款单搜索
	 * @author zengj
	 * @param map
	 *            查询条件
	 * @param pageNumber
	 *            当前页
	 * @param pageSize
	 *            每页展示记录数
	 */
	public PageUtils<TradeOrderRefundsVo> searchOrderRefundByParams(Map<String, Object> map, int pageNumber,
			int pageSize) {
		if (pageNumber > 0) {
			PageHelper.startPage(pageNumber, pageSize, true, false);
		}
		return new PageUtils<>(tradeOrderRefundsMapper.searchOrderRefundByParams(map));
	}

	/**
	 * 查询退款单导出列表
	 * 
	 * @author zengj
	 * @param map
	 *            查询条件
	 * @param maxSize
	 *            导出最大值，如为空，不限制导出数量
	 */
	public List<TradeOrderRefundsExportVo> selectExportList(Map<String, Object> map, Integer maxSize) {
		PageUtils<TradeOrderRefundsVo> pages = searchOrderRefundByParams(map, 0,
				maxSize == null ? Integer.MAX_VALUE : maxSize);
		List<TradeOrderRefundsExportVo> list = new ArrayList<>();
		if (pages != null && pages.getList() != null && !pages.getList().isEmpty()) {
			// 退款单状态Map
			Map<String, String> orderRefundsStatusMap = RefundsStatusEnum.convertViewStatus();
			// 循环退款单
			for (TradeOrderRefundsVo refundsVo : pages.getList()) {
				if (refundsVo.getTradeOrderRefundsItem() != null) {
					// 循环退款单项
					for (TradeOrderRefundsItem item : refundsVo.getTradeOrderRefundsItem()) {
						TradeOrderRefundsExportVo exportVo = new TradeOrderRefundsExportVo();
						// 退款单编号
						exportVo.setRefundNo(refundsVo.getRefundNo());
						// 实付款
						exportVo.setAmount(item.getAmount());
						// 下单时间
						exportVo.setCreateTime(DateUtils.formatDate(refundsVo.getTradeOrderVo().getCreateTime(),
								"yyyy-MM-dd HH:mm:ss"));
						// 订单编号
						exportVo.setOrderNo(refundsVo.getTradeOrderVo().getOrderNo());
						// 退的商品数量
						exportVo.setQuantity(item.getQuantity());
						// 退款金额，应该和实付款一样
						exportVo.setRefundAmount(item.getAmount());
						// 退款单状态
						exportVo.setRefundsStatus(orderRefundsStatusMap.get(refundsVo.getRefundsStatus().getName()));
						// 商品数量
						exportVo.setSkuName(item.getSkuName());
						// 单价
						exportVo.setUnitPrice(item.getUnitPrice());
						// 买家
						exportVo.setUserPhone(refundsVo.getUserPhone());
						// 货号
						exportVo.setArticleNo(ConvertUtil.format(item.getArticleNo()));
						if (!OrderStatusEnum.UNPAID.equals(refundsVo.getTradeOrderVo().getStatus())
								&& !OrderStatusEnum.BUYER_PAYING.equals(refundsVo.getTradeOrderVo().getStatus())) {
							// 支付方式
							if (refundsVo.getTradeOrderVo().getTradeOrderPay() == null) {
								exportVo.setPayType(refundsVo.getTradeOrderVo().getPayWay().getValue());
							} else {
								exportVo.setPayType(
										refundsVo.getTradeOrderVo().getTradeOrderPay().getPayType().getValue());
							}
						}
						exportVo.setOrderResource(refundsVo.getOrderResource());

						// Begin V2.1 add by wusw 20170224
						if (refundsVo.getTradeOrderVo() != null) {
							exportVo.setActivityTypeName(refundsVo.getTradeOrderVo().getActivityType().getValue());

							TradeOrderVo order = refundsVo.getTradeOrderVo();
							if (order.getPickUpType() == PickUpTypeEnum.TO_STORE_PICKUP) {
								exportVo.setAddress("到店自提");
							} else {
								if (order.getMemberConsigneeAddress() != null) {
									StringBuilder s = new StringBuilder("");
									if (StringUtils.isNotEmpty(order.getMemberConsigneeAddress().getProvinceName())) {
										s.append(order.getMemberConsigneeAddress().getProvinceName());
									}
									if (StringUtils.isNotEmpty(order.getMemberConsigneeAddress().getCityName())) {
										s.append(order.getMemberConsigneeAddress().getCityName());
									}
									if (StringUtils.isNotEmpty(order.getMemberConsigneeAddress().getAreaName())) {
										s.append(order.getMemberConsigneeAddress().getAreaName());
									}
									if (StringUtils.isNotEmpty(order.getMemberConsigneeAddress().getAreaExt())) {
										s.append(order.getMemberConsigneeAddress().getAreaExt());
									}
									exportVo.setAddress(s.toString());
								}

							}

						}
						// End V2.1 add by wusw 20170224
						list.add(exportVo);
					}
				}
			}
		}
		return list;
	}

	/**
	 * @desc 根据退款状态，查询线上退款单信息（pos--线上退货列表）
	 */
	@Override
	public List<TradeOrderRefunds> findOnlineByRefundsStatus(String refundsStatus) throws Exception {

		List<TradeOrderRefunds> result = null;
		if (StringUtils.isNotEmpty(refundsStatus)) {
			List<RefundsStatusEnum> refundsStatusList = new ArrayList<>();
			// 售后申请
			if ("1".equals(refundsStatus)) {
				refundsStatusList.add(RefundsStatusEnum.WAIT_SELLER_VERIFY);
				refundsStatusList.add(RefundsStatusEnum.BUYER_REPEAL_REFUND);

			} else if ("2".equals(refundsStatus)) {
				// 已拒绝
				refundsStatusList.add(RefundsStatusEnum.SELLER_REJECT_APPLY);
				refundsStatusList.add(RefundsStatusEnum.SELLER_REJECT_REFUND);
				refundsStatusList.add(RefundsStatusEnum.APPLY_CUSTOMER_SERVICE_INTERVENE);
				refundsStatusList.add(RefundsStatusEnum.CUSTOMER_SERVICE_CANCEL_INTERVENE);
				refundsStatusList.add(RefundsStatusEnum.YSC_REFUND);

			} else if ("3".equals(refundsStatus)) {
				// 已同意
				refundsStatusList.add(RefundsStatusEnum.WAIT_BUYER_RETURN_GOODS);
				refundsStatusList.add(RefundsStatusEnum.WAIT_SELLER_REFUND);

			} else if ("4".equals(refundsStatus)) {
				// 退款成功
				refundsStatusList.add(RefundsStatusEnum.REFUND_SUCCESS);
				refundsStatusList.add(RefundsStatusEnum.YSC_REFUND_SUCCESS);
			}

			result = tradeOrderRefundsMapper.selectOnlineByRefundsStatus(refundsStatusList, OrderResourceEnum.POS);
		}

		if (result == null) {
			result = new ArrayList<>();
		}

		return result;
	}

	/**
	 * @desc 根据退款单id，查询退款单详细信息（包括退款单、商品、订单、支付等信息）
	 * @author wusw
	 */
	@Override
	public TradeOrderRefundsQueryVo findDetailById(String id) throws Exception {

		TradeOrderRefundsQueryVo refundsVo = tradeOrderRefundsMapper.selectDetailById(id);
		// 拼接退款凭证图片前缀
		List<String> certificateImg = tradeOrderRefundsCertificateMapper.findImageByRefundsId(refundsVo.getId());
		refundsVo.setTradeOrderRefundsCertificateImgs(certificateImg);
		return refundsVo;
	}

	/**
	 * @desc 根据退款单id，查询退款单详细信息
	 * @author zengj
	 */
	@Override
	public TradeOrderRefundsVo selectRefundOrderDetailById(String id) throws Exception {
		TradeOrderRefundsVo refundsVo = tradeOrderRefundsMapper.selectRefundOrderDetailById(id);
		if (refundsVo != null && refundsVo.getTradeOrderVo() != null) {
			String activityId = refundsVo.getTradeOrderVo().getActivityId();
			// 如果有活动ID，说明该订单参与了活动
			if (StringUtils.isNotBlank(activityId) && !"0".equals(activityId)) {
				// 代金券活动
				if (ActivityTypeEnum.VONCHER.equals(refundsVo.getTradeOrderVo().getActivityType())) {
					ActivityCollectCoupons activityCollectCoupons = activityCollectCouponsService.get(activityId);
					if (activityCollectCoupons != null) {
						refundsVo.getTradeOrderVo().setActivityName(activityCollectCoupons.getName());
					}
				} else if (ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES
						.equals(refundsVo.getTradeOrderVo().getActivityType())) {
					// 满减活动
					ActivityDiscount activityDiscount = activityDiscountService.findById(activityId);
					if (activityDiscount != null) {
						refundsVo.getTradeOrderVo().setActivityName(activityDiscount.getName());
					}
				}
			}
		}
		return refundsVo;
	}

	@Override
	public Integer getTradeOrderRefundsCount(Map<String, Object> map) {
		return tradeOrderRefundsMapper.getTradeOrderRefundsCount(map);
	}

	@Override
	public PageUtils<TradeOrderRefunds> getOrderRefundByParams(Map<String, Object> map, int pageNumber, int pageSize) {
		PageHelper.startPage(pageNumber == 0 ? 1 : pageNumber, pageSize, pageNumber != 0, false);

		List<TradeOrderRefunds> list = tradeOrderRefundsMapper.getOrderRefundByParams(map);
		return new PageUtils<>(list);
	}

	/**
	 * 根据订单id查询订单退退款数量
	 * 
	 * @param orderId
	 *            String
	 * @return Integer
	 */
	@Override
	public Integer getTradeOrderRefundsCountByOrderId(String orderId) {
		Map<String, Object> map = new HashMap<>();
		map.put("orderId", orderId);
		return tradeOrderRefundsMapper.getTradeOrderRefundsCountByOrderId(map);
	}

	/**
	 * 商家版APP查询退款单信息
	 *
	 * @param map
	 *            查询条件
	 * @param pageNumber
	 *            当前页
	 * @param pageSize
	 *            每页展示数量
	 */
	public PageUtils<TradeOrderRefundsVo> selectMallAppByParams(Map<String, Object> map, int pageNumber, int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		// 先分页查询退款单信息
		List<TradeOrderRefundsVo> list = tradeOrderRefundsMapper.selectMallAppByParams(map);
		if (list != null && !list.isEmpty()) {
			for (TradeOrderRefundsVo orderRefundsVo : list) {
				// 查询退款单下的订单项
				orderRefundsVo.setTradeOrderRefundsItem(
						tradeOrderRefundsItemMapper.getTradeOrderRefundsItemByRefundsId(orderRefundsVo.getId()));
			}
		}
		return new PageUtils<>(list);
	}

	/**
	 * 查询退款单状态下对应的退款单数量
	 *
	 * @param storeId
	 *            店铺ID
	 */
	public List<TradeOrderRefundsStatusVo> getOrderRefundsCount(String storeId) {
		return tradeOrderRefundsMapper.getOrderRefundsCount(storeId);
	}

	// start added by luosm 20160927 V1.1.0
	/***
	 * 查询服务店到店消费退款单状态下对应的退款单数量
	 */
	public List<TradeOrderRefundsStatusVo> selectServiceOrderRefundsCount(String storeId) {
		return tradeOrderRefundsMapper.selectServiceOrderRefundsCount(storeId);
	}

	// end added by luosm 20160927 V1.1.0

	/**
	 * 商家版APP查询退款单信息
	 *
	 * @param map
	 *            查询条件
	 * @param pageNumber
	 *            当前页
	 * @param pageSize
	 *            每页展示数量
	 */
	public PageUtils<TradeOrderRefundsVo> selectWXRefundsOrder(Map<String, Object> map, int pageNumber, int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		return new PageUtils<>(tradeOrderRefundsMapper.selectWXRefundsOrder(map));
	}

	/**
	 * 退货单列表(pos销售查询用)
	 * 
	 * @author zhangkeneng
	 */
	@Override
	public List<TradeOrderRefunds> listForPos(Map<String, Object> map) throws Exception {
		return tradeOrderRefundsMapper.listForPos(map);
	}

	@Override
	public PageUtils<TradeOrderRefundsVo> findPageByFinance(OrderRefundQueryParamDto orderRefundQueryParamDto,
			int pageNumber, int pageSize) throws Exception {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradeOrderRefundsVo> list = tradeOrderRefundsMapper.selectRefundsByFinance(orderRefundQueryParamDto);
		return new PageUtils<>(list);
	}

	@Override
	public List<TradeOrderRefundsVo> findListByFinance(OrderRefundQueryParamDto orderRefundQueryParamDto)
			throws Exception {
		return tradeOrderRefundsMapper.selectRefundsByFinance(orderRefundQueryParamDto);
	}

	@Override
	public Integer findCountByFinance(OrderRefundQueryParamDto orderRefundQueryParamDto) throws Exception {

		return tradeOrderRefundsMapper.selectRefundsCountByFinance(orderRefundQueryParamDto);
	}

	/**
	 * 根据地址id查询会员地址
	 *
	 * @param addressId
	 *            地址ID
	 */
	private MemberConsigneeAddress findMemberAddress(String addressId) {
		return memberConsigneeAddressService.findById(addressId);
	}

	/**
	 * @desc 根据订单id，统计退款金额
	 * @author wusw
	 */
	@Override
	public Double getSumAmountByOrderId(String orderId) throws Exception {
		return tradeOrderRefundsMapper.selectSumAmountByOrderId(orderId);
	}

	/**
	 * @desc 退款单未支付统计
	 */
	@Override
	public Integer findRefundUnPayCount() {
		return tradeOrderRefundsMapper.findRefundUnPayCount();
	}

	/**
	 * @desc 投诉订单未支付统计
	 */
	@Override
	public Integer findComplainUnPayCount() {
		return tradeOrderRefundsMapper.findComplainUnPayCount();
	}

	/**
	 * @desc 根据退款交易号查询退款单
	 *
	 * @param tradeNum
	 *            退款交易号
	 * @return 退款单
	 */
	public TradeOrderRefunds getByTradeNum(String tradeNum) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("tradeNum", tradeNum);
		return getByParams(params);
	}

	/**
	 * @desc 根据订单号查询订单
	 */
	@Override
	public TradeOrderRefunds getByRefundNo(String refundNo) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("refundNo", refundNo);
		return getByParams(params);
	}

	/**
	 * @desc 根据订单号查询退款单
	 */
	@Override
	public List<TradeOrderRefunds> findByOrderNo(String orderNo) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("orderNo", orderNo);
		return tradeOrderRefundsMapper.selectByParams(params);
	}

	/**
	 * 根据条件查询退款单（）
	 */
	public TradeOrderRefunds getByParams(Map<String, Object> params) {
		List<TradeOrderRefunds> tradeOrderRefunds = tradeOrderRefundsMapper.selectByParams(params);
		if (Iterables.isEmpty(tradeOrderRefunds)) {
			return null;
		}
		return Iterables.getOnlyElement(tradeOrderRefunds);
	}

	/**
	 * zengj:查询店铺的退款数量
	 *
	 * @param storeId
	 *            店铺
	 */
	// Begin V1.1.0 add by wusw 20160928
	public Long selectRefundsCount(String storeId, OrderTypeEnum type) {
		return tradeOrderRefundsMapper.selectRefundsCount(storeId, type);
	}

	// End V1.1.0 add by wusw 20160928

	@Override
	public List<TradeOrderRefunds> getTradeOrderRefundsByOrderItemId(String orderItemId) {
		return tradeOrderRefundsMapper.getTradeOrderRefundsByOrderItemId(orderItemId);
	}

	@Override
	public PageUtils<TradeOrderRefunds> findByUserIdAndType(Map<String, Object> params, int pageNumber, int pageSize)
			throws Exception {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradeOrderRefunds> list = tradeOrderRefundsMapper.selectByUserIdAndType(params);
		return new PageUtils<>(list);
	}

	/**
	 * 商家中心首页 根据状态统计退款单数量(张克能加)
	 */
	@Override
	public Integer selectRefundsCountForIndex(String storeId, List<Integer> refundsStatusList) {
		return tradeOrderRefundsMapper.selectRefundsCountForIndex(storeId, refundsStatusList);
	}

	/**
	 * 查询pos退款单导出列表
	 * @author zengj
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> selectPosRefundExportList(Map<String, Object> params) {
		return tradeOrderRefundsMapper.selectPosRefundExportList(params);
	}

	@Transactional(readOnly = true)
	@Override
	public List<TradeOrderRefunds> findByOrderId(String orderId) {
		return tradeOrderRefundsMapper.selectByOrderId(orderId);
	}

	// Begin 重构4.1 add by wusw 20160722
	@Override
	public Integer findCountChargeForFinance() {
		// 默认订单类型为话费充值、流量充值订单
		Map<String, Object> params = new HashMap<>();
		List<OrderTypeEnum> typeList = new ArrayList<>();
		typeList.add(OrderTypeEnum.PHONE_PAY_ORDER);
		typeList.add(OrderTypeEnum.TRAFFIC_PAY_ORDER);
		params.put("type", typeList);
		// 默认状态为退款中
		params.put("refundsStatus", RefundsStatusEnum.SELLER_REFUNDING);
		// 默认查询第三方支付的充值订单
		List<PayTypeEnum> paymentMethodList = new ArrayList<>();
		paymentMethodList.add(PayTypeEnum.ALIPAY);
		paymentMethodList.add(PayTypeEnum.WXPAY);
		params.put("paymentMethod", paymentMethodList);
		params.put("disabled", Disabled.valid);
		return tradeOrderRefundsMapper.findCountChargeForFinance(params);
	}

	@Override
	public PageUtils<TradeOrderRefundsChargeVo> findeChargeRefundsByParams(Map<String, Object> params, int pageNumber,
			int pageSize) throws Exception {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradeOrderRefundsChargeVo> result = tradeOrderRefundsMapper.findeChargeRefundsByParams(params);
		return new PageUtils<>(result);
	}

	@Override
	public List<TradeOrderRefundsChargeVo> findeChargeRefundsListByParams(Map<String, Object> params) throws Exception {
		return tradeOrderRefundsMapper.findeChargeRefundsByParams(params);
	}
	// End 重构4.1 add by wusw 20160722

	// Begin add by zengjz 2016-9-14

	@Override
	public Map<String, Object> statisRefundsByParams(OrderRefundQueryParamDto orderRefundQueryParamDto)
			throws Exception {
		return tradeOrderRefundsMapper.statisRefundsByFinance(orderRefundQueryParamDto);
	}
	// End add by zengjz 2016-9-14

	@Override
	public PageUtils<TradeOrderRefundsVo> findOrderRefundByParams(Map<String, Object> map, int pageNumber,
			int pageSize) {
		if (pageNumber > 0) {
			PageHelper.startPage(pageNumber, pageSize, true, false);
		}
		List<TradeOrderRefundsVo> list = tradeOrderRefundsMapper.searchOrderRefundByParams(map);

		if (CollectionUtils.isNotEmpty(list)) {
			for (TradeOrderRefundsVo vo : list) {
				List<TradeOrderRefundsItem> itemList = vo.getTradeOrderRefundsItem();
				if (CollectionUtils.isNotEmpty(itemList)) {
					for (TradeOrderRefundsItem itemVo : itemList) {
						List<TradeOrderItemDetail> itemDetailList = itemVo.getTradeOrderItemDetails();
						if (CollectionUtils.isNotEmpty(itemDetailList)) {
							for (TradeOrderItemDetail itemDetailVo : itemDetailList) {
								if (StringUtils.isNotBlank(itemDetailVo.getConsumeCode())) {
									String first = itemDetailVo.getConsumeCode().substring(0, 2);
									String end = itemDetailVo.getConsumeCode().substring(6, 8);
									itemDetailVo.setConsumeCode(first + "****" + end);
								}
							}
						}
					}
				}

			}
		} else {
			list = new ArrayList<>();
		}
		return new PageUtils<>(list);
	}

	@Override
	public void refundSuccess(TradeOrderRefunds orderRefunds) throws Exception {
		updateTradeOrderRefund(orderRefunds.getId(), orderRefunds.getRefundsStatus(), tradeOrderRefundContext -> {
			TradeOrderRefunds tradeOrderRefunds = createUpdateTradeOrderRefunds(tradeOrderRefundContext,
					orderRefunds.getRefundsStatus(), null);
			tradeOrderRefunds.setRefundMoneyTime(new Date());
		});
	}

	@Override
	public List<TradeOrderRefunds> selectByOrderIds(List<String> orderIds) throws Exception {
		return PageQueryUtils.pageQueryByIds(orderIds, idList -> tradeOrderRefundsMapper.selectByOrderIds(idList));
	}

	@Override
	public PageUtils<TradeOrderRefundsVo> searchOrderRefundForSELLERAPP(Map<String, Object> map, int pageNumber,
			int pageSize) {
		if (pageNumber > 0) {
			PageHelper.startPage(pageNumber, pageSize, true, false);
		}
		return new PageUtils<>(tradeOrderRefundsMapper.searchOrderRefundForSELLERAPP(map));
	}

	private TradeOrderRefundProcessService getTradeOrderRefundProcessService(OrderTypeEnum type) {
		return tradeOrderRefundBuildFactory.getTradeOrderRefundProcessService(type);
	}

	/**
	 * @Description: 创建需要修改的退单信息
	 * @param tradeOrderRefundContext
	 * @param refundsStatus
	 * @param operator
	 * @return
	 * @author zengjizu
	 * @date 2017年10月14日
	 */
	private TradeOrderRefunds createUpdateTradeOrderRefunds(TradeOrderRefundContextBo tradeOrderRefundContext,
			RefundsStatusEnum refundsStatus, String operator) {
		TradeOrderRefunds tradeOrderRefunds = new TradeOrderRefunds();
		tradeOrderRefunds.setId(tradeOrderRefundContext.getTradeOrderRefunds().getId());
		tradeOrderRefunds.setRefundsStatus(refundsStatus);
		tradeOrderRefunds.setUpdateTime(new Date());
		if (operator != null) {
			tradeOrderRefunds.setOperator(operator);
		}
		tradeOrderRefundContext.setTradeOrderRefunds(tradeOrderRefunds);
		return tradeOrderRefunds;
	}

	@Override
	public void insertRechargeRefunds(TradeOrder tradeOrder) throws Exception {
		TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto = new TradeOrderApplyRefundParamDto();
		tradeOrderApplyRefundParamDto.setMemo("充值失败，系统退款");
		tradeOrderApplyRefundParamDto.setReason("充值失败，系统退款");
		tradeOrderApplyRefundParamDto.setOrderId(tradeOrder.getId());
		tradeOrderApplyRefundParamDto.setOrderItemId(tradeOrder.getTradeOrderItem().get(0).getId());
		tradeOrderApplyRefundParamDto.setOrderResource(tradeOrder.getOrderResource());
		tradeOrderApplyRefundParamDto.setUserId(RobotUserUtil.getRobotUser().getId());
		Response<TradeOrderApplyRefundResultDto> response = super.processApplyRefund(tradeOrderApplyRefundParamDto);
		if (response.getCode() != 0) {
			logger.error("退款失败:{},订单号:{}", response.getMessage(), tradeOrder.getOrderNo());
			throw new Exception("退款失败:" + response.getMessage());
		} else {
			logger.error("退款成功,订单id{},订单号:{}", tradeOrder.getId(), tradeOrder.getOrderNo());
		}
	}

}