package com.okdeer.mall.order.builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.assemble.dto.GoodsStoreSkuAssembleDto;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.archive.store.entity.StoreBranches;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.entity.StoreInfoExt;
import com.okdeer.archive.store.entity.StoreInfoServiceExt;
import com.okdeer.archive.store.service.StoreBranchesServiceApi;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.bdp.address.entity.Address;
import com.okdeer.bdp.address.service.IAddressService;
import com.okdeer.common.consts.LogConstants;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.common.utils.DateUtils;
import com.okdeer.mall.common.utils.TradeNumUtil;
import com.okdeer.mall.member.mapper.MemberConsigneeAddressMapper;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.order.bo.CurrentStoreSkuBo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderComboSnapshot;
import com.okdeer.mall.order.entity.TradeOrderExtSnapshot;
import com.okdeer.mall.order.entity.TradeOrderInvoice;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderLocate;
import com.okdeer.mall.order.entity.TradeOrderLogistics;
import com.okdeer.mall.order.enums.AppraiseEnum;
import com.okdeer.mall.order.enums.CompainStatusEnum;
import com.okdeer.mall.order.enums.OrderIsShowEnum;
import com.okdeer.mall.order.enums.OrderItemStatusEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.PaymentStatusEnum;
import com.okdeer.mall.order.enums.PickUpTypeEnum;
import com.okdeer.mall.order.enums.PlaceOrderTypeEnum;
import com.okdeer.mall.order.enums.WithInvoiceEnum;
import com.okdeer.mall.order.service.GenerateNumericalService;
import com.okdeer.mall.order.utils.OrderNoUtils;
import com.okdeer.mall.system.utils.ConvertUtil;

/**
 * ClassName: AbstractTradeOrderBoBuilder 
 * @Description: 订单Bo构建者
 * @author maojj
 * @date 2016年12月22日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿1.2.5		2016年12月22日				maojj			 订单Bo构建者
 */
@Component
public class TradeOrderBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(TradeOrderBuilder.class);
	
	private static final BigDecimal referenceValue = BigDecimal.valueOf(0.00);
	/**
	 * 生成编号的service
	 */
	@Resource
	protected GenerateNumericalService generateNumericalService;
	
	/**
	 * 会员收货地址信息Mapper
	 */
	@Resource
	protected MemberConsigneeAddressMapper memberConsigneeAddressMapper;
	
	/**
	 * 机构Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreBranchesServiceApi storeBranchesApi;

	/**
	 * 代金券记录Mapper
	 */
	@Resource
	private ActivityCouponsRecordMapper activityCouponsRecordMapper;

	/**
	 * 折扣活动表（折扣、满减）Mapper
	 */
	@Resource
	private ActivityDiscountMapper activityDiscountMapper;
	
	/**
	 * 店铺信息Dubbo接口
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoServiceApi;
	
	// Begin V2.1 added by maojj 2017-02-01
	/**
	 * 省市区地址查询接口
	 */
	@Reference(version = "1.0.0", check = false)
	private IAddressService addressService;
	// End V2.1 added by maojj 2017-02-01

	/**
	 * @Description: 构建TradeOrderBo
	 * @param placeOrderDto
	 * @return   
	 * @author maojj
	 * @date 2016年12月22日
	 */
	public TradeOrder build(PlaceOrderParamDto paramDto) throws Exception{
		// 构建交易订单
		TradeOrder tradeOrder = buildTradeOrder(paramDto);
		// 构建交易订单项
		List<TradeOrderItem> orderItemList = buildOrderItemList(tradeOrder, paramDto);
		// 构建订单物流信息
		TradeOrderLogistics tradeOrderLogistics = buildTradeOrderLogistics(tradeOrder,paramDto);
		// 构建订单发票信息
		TradeOrderInvoice tradeOrderInvoice = buildTradeOrderInvoice(tradeOrder, paramDto);
		// Begin V2.1 added by maojj 2017-02-01
		// 构建订单定位信息
		TradeOrderLocate tradeOrderLocate = buildTradeOrderLocate(tradeOrder.getId(),paramDto);
		// End V2.1 added by maojj 2017-02-01
		// Begin V2.5 added by maojj 2017-02-01
		// 构建订单中使用的组合商品明细快照列表
		List<TradeOrderComboSnapshot> comboDetailList = buildComboDetailList(tradeOrder.getId(),paramDto);
		// 构建交易订单扩展快照
		TradeOrderExtSnapshot tradeOrderExt = buildTradeOrderExt(tradeOrder,tradeOrderLogistics,paramDto);
		// End V2.5 added by maojj 2017-02-01
		tradeOrder.setTradeOrderItem(orderItemList);
		tradeOrder.setTradeOrderInvoice(tradeOrderInvoice);
		tradeOrder.setTradeOrderLogistics(tradeOrderLogistics);
		tradeOrder.setTradeOrderLocate(tradeOrderLocate);
		tradeOrder.setComboDetailList(comboDetailList);
		tradeOrder.setTradeOrderExt(tradeOrderExt);
		return tradeOrder;
	}
	
	/**
	 * @Description: 构建TradeOrder
	 * @param placeOrderDto   
	 * @author maojj
	 * @date 2016年12月22日
	 */
	public TradeOrder buildTradeOrder(PlaceOrderParamDto paramDto) throws Exception{
		StoreSkuParserBo parserBo = (StoreSkuParserBo)paramDto.get("parserBo");
		StoreInfo storeInfo = (StoreInfo)paramDto.get("storeInfo");
		TradeOrder tradeOrder  = new TradeOrder();
		tradeOrder.setId(UuidUtils.getUuid());
		tradeOrder.setUserId(paramDto.getUserId());
		tradeOrder.setUserPhone(paramDto.getUserPhone());
		tradeOrder.setStoreId(paramDto.getStoreId());
		tradeOrder.setStoreName(storeInfo.getStoreName());
		tradeOrder.setSellerId(paramDto.getStoreId());
		tradeOrder.setType(paramDto.getSkuType());
		tradeOrder.setPid("0");
		// 设置参与的平台优惠类型
		tradeOrder.setActivityType(paramDto.getActivityType());
		// 设置参与的平台优惠活动Id
		tradeOrder.setActivityId(paramDto.getActivityId());
		tradeOrder.setActivityItemId(paramDto.getActivityItemId());
		tradeOrder.setRemark(paramDto.getRemark());
		tradeOrder.setInvoice(paramDto.getIsInvoice());
		tradeOrder.setOrderResource(paramDto.getChannel());
		tradeOrder.setIsShow(OrderIsShowEnum.yes);
		tradeOrder.setPaymentStatus(PaymentStatusEnum.STAY_BACK);
		tradeOrder.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);
		tradeOrder.setTradeNum(TradeNumUtil.getTradeNum());
		tradeOrder.setDisabled(Disabled.valid);
		tradeOrder.setCreateTime(new Date());
		tradeOrder.setUpdateTime(new Date());
		tradeOrder.setClientVersion(paramDto.getVersion());
		tradeOrder.setFareActivityId(parserBo.getFareActivityId());
		// 设置订单配送方式和佣金比率
		if(storeInfo.getStoreInfoExt() != null){
			tradeOrder.setDeliveryType(storeInfo.getStoreInfoExt().getDeliveryType());
			tradeOrder.setCommisionRatio(storeInfo.getStoreInfoExt().getCommisionRatio());
		}
		// 设置订单编号
		setOrderNo(tradeOrder,paramDto.getOrderType());
		// 设置订单总品项
		tradeOrder.setTotalKind(paramDto.getSkuList().size());
		// 设置订单总数量
		tradeOrder.setTotalQuantity(parserBo.getTotalQuantity());
		// 设置订单总金额
		tradeOrder.setTotalAmount(parserBo.getTotalItemAmount());
		// 设置订单状态
		setOrderStatus(tradeOrder,paramDto.getPayType());
		// 设置支付方式
		setPayWay(tradeOrder,paramDto.getPayType());
		// 设置提货类型
		setPickUpType(tradeOrder,paramDto);
		// 设置提货时间
		setPickUpTimeAndPickUpId(tradeOrder,paramDto);
		// 解析优惠活动
		parseFavour(tradeOrder, paramDto);
		// 设置订单实付金额
		setActualAmount(tradeOrder);
		// 设置店铺总收入
		setIncome(tradeOrder,paramDto);
		// 处理配送费
		processFare(tradeOrder,parserBo,paramDto);
		// 处理订单违约信息
		processBreach(tradeOrder,paramDto);
		return tradeOrder;
	}
	
	/**
	 * @Description: 设置订单编号
	 * @param tradeOrder   
	 * @author maojj
	 * @date 2016年12月22日
	 */
	public void setOrderNo(TradeOrder tradeOrder,PlaceOrderTypeEnum placeOrderType) throws Exception {
		String orderNo = null;
		if(placeOrderType == PlaceOrderTypeEnum.CVS_ORDER){
			StoreBranches storeBranches = storeBranchesApi.findBranches(tradeOrder.getStoreId());
			if (storeBranches == null || StringUtils.isEmpty(storeBranches.getBranchCode())) {
				throw new ServiceException(LogConstants.STORE_BRANCHE_NOT_EXISTS);
			}
			orderNo = generateNumericalService.generateOrderNo(OrderNoUtils.PHYSICAL_ORDER_PREFIX,
					storeBranches.getBranchCode(), OrderNoUtils.ONLINE_POS_ID);
		}else{
			orderNo = generateNumericalService.generateOrderNo(OrderNoUtils.SERV_ORDER_PREFIXE, "",
					OrderNoUtils.ONLINE_POS_ID);
		}
		tradeOrder.setOrderNo(orderNo);
		logger.info("生成订单编号：{}", orderNo);
	}
	
	/**
	 * @Description: 设置订单状态
	 * @param tradeOrder
	 * @param payType   
	 * @author maojj
	 * @date 2016年12月22日
	 */
	public void setOrderStatus(TradeOrder tradeOrder,int payType){
		// 支付方式：0：在线支付、1:货到付款、6：微信支付 4：线下确认并当面支付
		switch (payType) {
			case 0:
			case 6:
				tradeOrder.setStatus(OrderStatusEnum.UNPAID);
				break;
			case 1:
				tradeOrder.setStatus(OrderStatusEnum.DROPSHIPPING);
				break;
			case 4:
				tradeOrder.setStatus(OrderStatusEnum.WAIT_RECEIVE_ORDER);
				break;
			default:
				break;
		}
	}
	
	/**
	 * @Description: 设置支付方式
	 * @param tradeOrder
	 * @param payType   
	 * @author maojj
	 * @date 2016年12月22日
	 */
	public void setPayWay(TradeOrder tradeOrder,int payType){
		// 支付方式：0：在线支付、1:货到付款、4、线下确认并当面支付   6：微信支付
		switch (payType) {
			case 0:
			case 6:
				tradeOrder.setPayWay(PayWayEnum.PAY_ONLINE);
				break;
			case 1:
				tradeOrder.setPayWay(PayWayEnum.CASH_DELIERY);
				break;
			case 4:
				tradeOrder.setPayWay(PayWayEnum.OFFLINE_CONFIRM_AND_PAY);
				break;
			default:
				break;
		}
	}
	
	/**
	 * @Description: 设置提货类型和提货时间
	 * @param tradeOrder
	 * @param placeOrderDto   
	 * @author maojj
	 * @throws Exception 
	 * @date 2016年12月22日
	 */
	public void setPickUpType(TradeOrder tradeOrder,PlaceOrderParamDto paramDto) throws Exception{
		PickUpTypeEnum pickType = paramDto.getPickType();
		tradeOrder.setPickUpType(pickType);
	}
	
	
	/**
	 * @Description: 设置提货时间
	 * @param tradeOrder 交易订单
	 * @param req 订单请求对象 
	 * @return void  
	 * @author maojj
	 * @throws ServiceException 
	 * @date 2016年7月14日
	 */
	private void setPickUpTimeAndPickUpId(TradeOrder tradeOrder, PlaceOrderParamDto paramDto) throws ServiceException {
		String pickTime = paramDto.getPickTime();
		if (tradeOrder.getPickUpType() == PickUpTypeEnum.DELIVERY_DOOR){
			// 如果是上门
			tradeOrder.setPickUpTime(pickTime);
		}else{
			// 如果是到店
			StoreInfoExt storeInfoExt = ((StoreInfo)paramDto.get("storeInfo")).getStoreInfoExt();
			StoreInfo storeInfo = storeInfoServiceApi.selectDefaultAddressById(paramDto.getStoreId());
			// 获取默认地址
			String defaultAddressId = storeInfo.getAddressId();
			tradeOrder.setPickUpId(defaultAddressId);
			if (StringUtils.isNotEmpty(pickTime)) {
				tradeOrder.setPickUpTime(pickTime);
			} else {
				String attrTime = DateUtils.getDate();
				String pcTime = attrTime + " " + storeInfoExt.getServiceStartTime() + "-"
						+ storeInfoExt.getServiceEndTime();
				tradeOrder.setPickUpTime(pcTime);
			}
		}
			
	}
	
	/**
	 * @Description: 解析优惠设置优惠金额和收入
	 * @param tradeOrder
	 * @param placeOrderDto   
	 * @author maojj
	 * @date 2016年12月22日
	 */
	public void parseFavour(TradeOrder tradeOrder,PlaceOrderParamDto paramDto) throws ServiceException{
		StoreSkuParserBo parserBo = (StoreSkuParserBo) paramDto.get("parserBo");
		// 平台优惠
		tradeOrder.setPlatformPreferential(format(parserBo.getPlatformPreferential()));
		// 店铺优惠
		if(parserBo.isLowFavour()){
			// 如果有低价优惠.记录店铺优惠类型和店铺优惠金额
			tradeOrder.setStoreActivityType(ActivityTypeEnum.LOW_PRICE);
			tradeOrder.setStorePreferential(parserBo.getTotalLowFavour());
			tradeOrder.setStoreActivityId(parserBo.getLowActivityId());
		} else {
			tradeOrder.setStorePreferential(BigDecimal.valueOf(0.0));
		}
		// 运费优惠
		tradeOrder.setFarePreferential(format(parserBo.getFarePreferential()));
		// 实际运费优惠
		tradeOrder.setRealFarePreferential(format(parserBo.getRealFarePreferential()));
		// 设置订单优惠金额。此处不加运费优惠，到处理运费时统一进行计算
		tradeOrder.setPreferentialPrice(tradeOrder.getPlatformPreferential()
				.add(tradeOrder.getStorePreferential()));
	}
	
	private BigDecimal format(BigDecimal value){
		return value == null ? BigDecimal.valueOf(0.00) : value;
	}
	
	/**
	 * @Description: 计算订单实付金额
	 * @param tradeOrder 交易订单
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void setActualAmount(TradeOrder tradeOrder) {
		BigDecimal totalAmount = tradeOrder.getTotalAmount();
		BigDecimal favourAmount = tradeOrder.getPreferentialPrice();
		// 如果总金额<优惠金额，则实付为0，优惠为订单总金额，否则实付金额为总金额-优惠金额，优惠为优惠金额
		if (totalAmount.compareTo(favourAmount) == -1) {
			tradeOrder.setActualAmount(BigDecimal.valueOf(0.0));
			tradeOrder.setPreferentialPrice(totalAmount);
		} else {
			tradeOrder.setActualAmount(totalAmount.subtract(favourAmount));
		}
	}

	/**
	 * @Description:	设置订单总收入
	 * 	店铺收入=订单总金额-店铺优惠-佣金收取
	 * @param tradeOrder 交易订单  
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void setIncome(TradeOrder tradeOrder,PlaceOrderParamDto paramDto) {
		StoreInfoExt storeInfoExt = ((StoreInfo)paramDto.get("storeInfo")).getStoreInfoExt();
		BigDecimal income = tradeOrder.getTotalAmount().subtract(tradeOrder.getStorePreferential());
		// 订单需要收取的佣金为：
		BigDecimal referenceVal = BigDecimal.valueOf(0.00);
		BigDecimal commission =  income.multiply(storeInfoExt.getCommisionRatio()).setScale(2,BigDecimal.ROUND_HALF_UP);
		if(storeInfoExt.getCommisionRatio().compareTo(referenceVal) == 1 && commission.compareTo(referenceVal) == 0){
			commission = BigDecimal.valueOf(0.01);
		}
		paramDto.put("commission", commission);
		paramDto.put("totalAmountInCommission", income);
		tradeOrder.setIncome(income);
	}

	/**
	 * @Description: 处理运费
	 * @param tradeOrder
	 * @param fare   
	 * @author maojj
	 * @date 2017年1月6日
	 */
	public void processFare(TradeOrder tradeOrder,StoreSkuParserBo parserBo,PlaceOrderParamDto paramDto){
		BigDecimal fare = parserBo.getFare();
		StoreInfoExt storeInfoExt = ((StoreInfo)paramDto.get("storeInfo")).getStoreInfoExt();
		tradeOrder.setTotalAmount(tradeOrder.getTotalAmount().add(fare));
		tradeOrder.setActualAmount(tradeOrder.getActualAmount().add(fare.subtract(tradeOrder.getRealFarePreferential())));
		// TODO 如果店铺选择的是第三方配送，运费不计入收入。如果店铺选择的是商家自送，运费计入补贴
		if (Integer.valueOf(2).equals(storeInfoExt.getDeliveryType())) {
			tradeOrder.setIncome(tradeOrder.getIncome().add(fare));
		}
		tradeOrder.setFare(fare);
		tradeOrder.setPreferentialPrice(tradeOrder.getPreferentialPrice().add(tradeOrder.getRealFarePreferential()));
	}
	
	public void processBreach(TradeOrder tradeOrder,PlaceOrderParamDto paramDto){
		tradeOrder.setIsBreach(WhetherEnum.not);
		StoreInfoServiceExt servExt = ((StoreInfo)paramDto.get("storeInfo")).getStoreInfoServiceExt();
		if(servExt != null){
			// 店铺设置是否有违约金
			tradeOrder.setIsBreachMoney(WhetherEnum.enumOrdinalOf(servExt.getIsBreachMoney()));
			// 店铺设置收取违约金的时间限制
			tradeOrder.setBreachTime(servExt.getBreachTime());
			// 店铺设置违约金的百分比
			tradeOrder.setBreachPercent(servExt.getBreachPercent());
		}
		if(tradeOrder.getIsBreachMoney() == WhetherEnum.whether){
			// 如果店铺设置了违约金，计算订单应该收取的违约金存入订单记录中
			tradeOrder.setBreachMoney(tradeOrder.getActualAmount().multiply(BigDecimal.valueOf(tradeOrder.getBreachPercent())).divide(BigDecimal.valueOf(100),2, BigDecimal.ROUND_UP));
		}
	}
	
	/**
	 * @Description: 构建交易订单项
	 * @param tradeOrder
	 * @param reqDto
	 * @return
	 * @throws ServiceException   
	 * @author maojj
	 * @date 2017年1月5日
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<TradeOrderItem> buildOrderItemList(TradeOrder tradeOrder, PlaceOrderParamDto paramDto)
			throws ServiceException {
		List<TradeOrderItem> orderItemList = new ArrayList<TradeOrderItem>();
		StoreSkuParserBo parserBo = (StoreSkuParserBo)paramDto.get("parserBo");
		
		String orderId = tradeOrder.getId();
		// 订单项参与平台优惠的总金额
		BigDecimal totalAmount = parserBo.getTotalAmountHaveFavour();
		// 订单总的平台优惠
		BigDecimal platformFavour = parserBo.getPlatformPreferential();
		BigDecimal favourSum =  BigDecimal.valueOf(0.00);
		// 订单总的佣金费用
		BigDecimal totalcommission = (BigDecimal)paramDto.get("commission");
		int index = 0;
		int commissionIndex = 0;
		BigDecimal commissionSum = BigDecimal.valueOf(0.00);
		BigDecimal haveCommissionAmount = (BigDecimal)paramDto.get("totalAmountInCommission");
		
		int haveFavourItemSize = parserBo.getHaveFavourGoodsMap().size();
		TradeOrderItem tradeOrderItem = null;

		Collection<CurrentStoreSkuBo> skuBoSet = parserBo.getCurrentSkuMap().values();
		List<CurrentStoreSkuBo> goodsItemList = new ArrayList<CurrentStoreSkuBo>();
		goodsItemList.addAll(skuBoSet);
		ComparatorChain chain = new ComparatorChain();
		chain.addComparator(new BeanComparator("onlinePrice"), false);
		Collections.sort(goodsItemList, chain);
		int orderItemSize = goodsItemList.size();
		for (CurrentStoreSkuBo skuBo : goodsItemList) {
			tradeOrderItem = new TradeOrderItem();

			tradeOrderItem.setId(UuidUtils.getUuid());
			tradeOrderItem.setOrderId(orderId);
			tradeOrderItem.setStoreSpuId(skuBo.getStoreSpuId());
			tradeOrderItem.setStoreSkuId(skuBo.getId());
			tradeOrderItem.setSkuName(skuBo.getName());
			tradeOrderItem.setPropertiesIndb(skuBo.getPropertiesIndb());
			tradeOrderItem.setMainPicPrl(skuBo.getMainPicUrl());
			tradeOrderItem.setSpuType(skuBo.getSpuType());
			tradeOrderItem.setUnitPrice(skuBo.getOnlinePrice());
			tradeOrderItem.setQuantity(skuBo.getQuantity());
			tradeOrderItem.setActivityPrice(skuBo.getActPrice());
			tradeOrderItem.setActivityQuantity(tradeOrder.getActivityType() == ActivityTypeEnum.LOW_PRICE ? skuBo.getSkuActQuantity() : 0);
			tradeOrderItem.setStatus(OrderItemStatusEnum.NO_REFUND);
			tradeOrderItem.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);
			tradeOrderItem.setAppraise(AppraiseEnum.NOT_APPROPRIATE);
			tradeOrderItem.setCreateTime(new Date());
			tradeOrderItem.setServiceAssurance(
					StringUtils.isEmpty(skuBo.getGuaranteed()) ? 0 : Integer.valueOf(skuBo.getGuaranteed()));
			if(skuBo.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal() && skuBo.getSkuActQuantity() > 0){
				tradeOrderItem.setStoreActivityType(ActivityTypeEnum.enumValueOf(skuBo.getActivityType()));
				tradeOrderItem.setStoreActivityId(skuBo.getActivityId());
			}else{
				tradeOrderItem.setActivityType(paramDto.getActivityType().ordinal());
				tradeOrderItem.setActivityId(paramDto.getActivityId());
				tradeOrderItem.setStoreActivityType(ActivityTypeEnum.NO_ACTIVITY);
				tradeOrderItem.setStoreActivityId("0");
			}
			
			tradeOrderItem.setBarCode(skuBo.getBarCode());
			tradeOrderItem.setStyleCode(skuBo.getStyleCode());
			tradeOrderItem.setUnit(skuBo.getUnit());

			// 订单项总金额
			BigDecimal totalAmountOfItem = skuBo.getTotalAmount();
			tradeOrderItem.setTotalAmount(totalAmountOfItem);
			// 计算订单项平台优惠金额
			BigDecimal favourItem = BigDecimal.valueOf(0.0);
			// 订单项佣金收取金额
			BigDecimal commissionItem = BigDecimal.valueOf(0.0);
			// 订单项店铺优惠金额
			BigDecimal storeFavourItem = BigDecimal.valueOf(0.0);
			if(skuBo.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal() && skuBo.getSkuActQuantity() > 0){
				storeFavourItem = skuBo.getOnlinePrice().subtract(skuBo.getActPrice())
						.multiply(BigDecimal.valueOf(skuBo.getSkuActQuantity()));
			}
			if (platformFavour.compareTo(referenceValue) == 1) {
				if(!parserBo.getHaveFavourGoodsMap().containsKey(skuBo.getId())){
					favourItem = BigDecimal.valueOf(0.0);
				} else if (index++ < haveFavourItemSize - 1) {
					if (skuBo.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal()) {
						// 如果是低价商品，平台优惠只针对不享受低价活动的商品进行ch
						favourItem = skuBo.getOnlinePrice()
								.multiply(BigDecimal.valueOf(skuBo.getQuantity() - skuBo.getSkuActQuantity()))
								.multiply(platformFavour).divide(totalAmount, 2, BigDecimal.ROUND_FLOOR);
					} else {
						favourItem = totalAmountOfItem.multiply(platformFavour).divide(totalAmount, 2,
								BigDecimal.ROUND_FLOOR);
					}
					if (favourItem.compareTo(totalAmountOfItem.subtract(storeFavourItem)) == 1) {
						favourItem = totalAmountOfItem.subtract(storeFavourItem);
					}
					favourSum = favourSum.add(favourItem);
				} else {
					favourItem = platformFavour.subtract(favourSum);
					if (favourItem.compareTo(totalAmountOfItem) == 1) {
						favourItem = totalAmountOfItem;
					}
				}
			}
			if(totalcommission.compareTo(referenceValue) == 1){
				// 订单收取佣金
				if(commissionIndex++ < orderItemSize - 1){
					commissionItem = totalAmountOfItem.subtract(storeFavourItem).multiply(haveCommissionAmount).divide(totalAmount, 2, BigDecimal.ROUND_FLOOR);
				}else{
					commissionItem = totalcommission.subtract(commissionSum);
				}
			}
			// 设置优惠金额
			tradeOrderItem.setPreferentialPrice(favourItem.add(storeFavourItem));
			// 设置平台优惠金额
			tradeOrderItem.setStorePreferential(storeFavourItem);
			// 设置订单项佣金金额
			tradeOrderItem.setCommission(commissionItem);
			// 设置实付金额
			tradeOrderItem.setActualAmount(totalAmountOfItem.subtract(favourItem));
			// 设置实付金额
			if (paramDto.getPayType() == PayWayEnum.OFFLINE_CONFIRM_AND_PAY.ordinal()) {
				// 线下支付的实付金额为0
				tradeOrderItem.setActualAmount(BigDecimal.valueOf(0));
				tradeOrderItem.setIncome(BigDecimal.valueOf(0));
			}else{
				// 设置订单项总收入=订单项总金额-订单项店铺优惠-佣金金额
				tradeOrderItem.setIncome(totalAmountOfItem.subtract(storeFavourItem).subtract(commissionItem));
			}
			if (tradeOrderItem.getActualAmount().compareTo(BigDecimal.ZERO) == 0
					&& tradeOrderItem.getSpuType() == SpuTypeEnum.fwdDdxfSpu ) {
				// 实付金额为0的到店消费商品，设置服务保障为无
				tradeOrderItem.setServiceAssurance(0);
			}
			orderItemList.add(tradeOrderItem);
		}

		return orderItemList;
	}
	
	public TradeOrderLogistics buildTradeOrderLogistics(TradeOrder tradeOrder, PlaceOrderParamDto paramDto){
		if(paramDto.getOrderType() ==PlaceOrderTypeEnum.CVS_ORDER && paramDto.getPickType() == PickUpTypeEnum.TO_STORE_PICKUP){
			return null;
		}
		// 获取买家收货地址
		MemberConsigneeAddress address = memberConsigneeAddressMapper.selectAddressById(paramDto.getUserAddrId());
		paramDto.put("userUseAddr", address);
		if (address == null) {
			return null;
		}
		paramDto.put("receiverLat", address.getLatitude());
		paramDto.put("receiverLng", address.getLongitude());
		// 保存用户送货上门地址Id
		tradeOrder.setPickUpId(address.getId());
		
		TradeOrderLogistics orderLogistics = new TradeOrderLogistics();
		orderLogistics.setId(UuidUtils.getUuid());
		orderLogistics.setConsigneeName(address.getConsigneeName());
		orderLogistics.setMobile(address.getMobile());
		orderLogistics.setAddress(address.getAddress());

		StringBuilder area = new StringBuilder();
		area.append(ConvertUtil.format(address.getProvinceName())).append(ConvertUtil.format(address.getCityName()))
				.append(ConvertUtil.format(address.getAreaName())).append(ConvertUtil.format(address.getAreaExt()));
		orderLogistics.setArea(area.toString());

		orderLogistics.setOrderId(tradeOrder.getId());
		orderLogistics.setAreaId(address.getAreaId());
		orderLogistics.setProvinceId(address.getProvinceId());
		orderLogistics.setCityId(address.getCityId());
		orderLogistics.setZipCode(address.getZipCode());
		return orderLogistics;
	}
	
	public TradeOrderInvoice buildTradeOrderInvoice(TradeOrder tradeOrder, PlaceOrderParamDto paramDto){
		TradeOrderInvoice orderInvoice = null;
		if (paramDto.getIsInvoice() == WithInvoiceEnum.HAS) {
			orderInvoice = new TradeOrderInvoice();
			// 有发票
			orderInvoice.setId(UuidUtils.getUuid());
			orderInvoice.setOrderId(tradeOrder.getId());
			orderInvoice.setHead(paramDto.getInvoiceHead());
			orderInvoice.setContext(paramDto.getInvoiceContent());
			tradeOrder.setTradeOrderInvoice(orderInvoice);
		}
		return orderInvoice;
	}
	
	public TradeOrderLocate buildTradeOrderLocate(String orderId, PlaceOrderParamDto paramDto){
		TradeOrderLocate tradeOrderLocate = new TradeOrderLocate();
		// 城市名称
		String cityName = paramDto.getCityName();
		// 区域名称
		String areaName = paramDto.getAreaName();
		
		tradeOrderLocate.setId(UuidUtils.getUuid());
		tradeOrderLocate.setOrderId(orderId);
		tradeOrderLocate.setLongitude(paramDto.getLng());
		tradeOrderLocate.setLatitude(paramDto.getLat());
		tradeOrderLocate.setProviceName(paramDto.getProvinceName());
		tradeOrderLocate.setCityName(cityName);
		tradeOrderLocate.setAreaName(areaName);
		tradeOrderLocate.setAreaExt(paramDto.getAreaExt());

		if(StringUtils.isNotEmpty(paramDto.getCityName())){
			Address cityAddr = addressService.getByName(cityName);
			if(cityAddr != null){
				tradeOrderLocate.setProvinceId(String.valueOf(cityAddr.getParentId()));
				tradeOrderLocate.setCityId(String.valueOf(cityAddr.getId()));
				
				if(areaName != null){
					Address areaAddr = addressService.getByName(cityAddr.getId(), areaName);
					tradeOrderLocate.setAreaId(areaAddr == null ? "" : String.valueOf(areaAddr.getId()));
				}
			}
		}
		
		return tradeOrderLocate;
	}
	
	// Begin V2.5 added by maojj 2017-06-23
	/**
	 * @Description: 构建交易订单中使用组合商品明细列表
	 * @param orderId
	 * @param paramDto
	 * @return   
	 * @author maojj
	 * @date 2017年6月23日
	 */
	private List<TradeOrderComboSnapshot> buildComboDetailList(String orderId,PlaceOrderParamDto paramDto){
		List<TradeOrderComboSnapshot> comboDetailList = Lists.newArrayList();
		TradeOrderComboSnapshot comboSnapshot = null;
		StoreSkuParserBo parserBo = (StoreSkuParserBo)paramDto.get("parserBo");
		Map<String,List<GoodsStoreSkuAssembleDto>> comboSkuMap = parserBo.getComboSkuMap();
		if(comboSkuMap == null || comboSkuMap.size() == 0){
			return comboDetailList;
		}
		for(List<GoodsStoreSkuAssembleDto> comboSkuList : parserBo.getComboSkuMap().values()){
			for(GoodsStoreSkuAssembleDto comboSku : comboSkuList){
				comboSnapshot = BeanMapper.map(comboSku, TradeOrderComboSnapshot.class);
				comboSnapshot.setId(UuidUtils.getUuid());
				comboSnapshot.setComboSkuId(comboSku.getAssembleSkuId());
				comboSnapshot.setOrderId(orderId);
				comboDetailList.add(comboSnapshot);
			}
		}
		return comboDetailList;
	}
	
	private TradeOrderExtSnapshot buildTradeOrderExt(TradeOrder tradeOrder,TradeOrderLogistics logistics,PlaceOrderParamDto paramDto){
		TradeOrderExtSnapshot tradeOrderExt = new TradeOrderExtSnapshot();
		StoreInfo storeInfo = (StoreInfo)paramDto.get("storeInfo");
		StoreInfoExt storeInfoExt = storeInfo.getStoreInfoExt();
		
		tradeOrderExt.setId(UuidUtils.getUuid());
		tradeOrderExt.setOrderId(tradeOrder.getId());
		tradeOrderExt.setOrderNo(tradeOrder.getOrderNo());
		tradeOrderExt.setTransportName(storeInfo.getStoreName());
		tradeOrderExt.setTransportAddress(String.format("%s%s", storeInfo.getArea(),storeInfo.getAddress()));
		tradeOrderExt.setTransportLatitude(String.valueOf(storeInfo.getLatitude()));
		tradeOrderExt.setTransportLongitude(String.valueOf(storeInfo.getLongitude()));
		tradeOrderExt.setTransportTel(storeInfo.getMobile());
		if(logistics != null){
			tradeOrderExt.setReceiverName(logistics.getConsigneeName());
			tradeOrderExt.setReceiverPrimaryPhone(logistics.getMobile());
			tradeOrderExt.setReceiverAddress(String.format("%s%s",logistics.getArea(),logistics.getAddress()));
			tradeOrderExt.setReceiverLatitude(String.valueOf(paramDto.get("receiverLat")));
			tradeOrderExt.setReceiverLongitude(String.valueOf(paramDto.get("receiverLng")));
		}
		tradeOrderExt.setStartPrice(storeInfoExt.getStartPrice());
		tradeOrderExt.setFreight(storeInfoExt.getFreight());
		tradeOrderExt.setFreeFreightPrice(storeInfoExt.getFreeFreightPrice());
		tradeOrderExt.setDeliveryType(storeInfoExt.getDeliveryType());
		tradeOrderExt.setCommisionRatio(storeInfoExt.getCommisionRatio());
		
		return tradeOrderExt;
	}
	// End V2.5 added by maojj 2017-06-23
}