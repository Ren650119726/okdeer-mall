package com.okdeer.mall.order.builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
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
import com.okdeer.common.consts.LogConstants;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.CouponsFindVo;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.common.utils.DateUtils;
import com.okdeer.mall.common.utils.TradeNumUtil;
import com.okdeer.mall.member.mapper.MemberConsigneeAddressMapper;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.order.bo.CurrentStoreSkuBo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderInvoice;
import com.okdeer.mall.order.entity.TradeOrderItem;
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
		
		tradeOrder.setTradeOrderItem(orderItemList);
		tradeOrder.setTradeOrderInvoice(tradeOrderInvoice);
		tradeOrder.setTradeOrderLogistics(tradeOrderLogistics);
		// 构建订单物流
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
		TradeOrder tradeOrder  = new TradeOrder();
		tradeOrder.setId(UuidUtils.getUuid());
		tradeOrder.setUserId(paramDto.getUserId());
		tradeOrder.setUserPhone(paramDto.getUserPhone());
		tradeOrder.setStoreId(paramDto.getStoreId());
		tradeOrder.setStoreName(((StoreInfo)paramDto.get("storeInfo")).getStoreName());
		tradeOrder.setSellerId(paramDto.getStoreId());
		tradeOrder.setType(paramDto.getSkuType());
		tradeOrder.setPid("0");
		tradeOrder.setActivityType(paramDto.getActivityType());
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
		setActualAmount(tradeOrder,parserBo.isLowFavour());
		// 设置店铺总收入
		setIncome(tradeOrder,parserBo.isLowFavour());
		// 处理配送费
		processFare(tradeOrder,parserBo.getFare());
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
		// 支付方式：0：在线支付、1:货到付款、6：微信支付
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
			if (!"".equals(pickTime)) {
				tradeOrder.setPickUpTime(pickTime);
			} else {
				tradeOrder.setPickUpTime("立即配送");
			}
		}else{
			// 如果是到店
			StoreInfoExt storeInfoExt = ((StoreInfo)paramDto.get("storeInfo")).getStoreInfoExt();
			StoreInfo storeInfo = storeInfoServiceApi.selectDefaultAddressById(paramDto.getStoreId());
			// 获取默认地址
			String defaultAddressId = storeInfo.getMemberConsignee().getId();
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
		StoreSkuParserBo parserBo = (StoreSkuParserBo)paramDto.get("parserBo");
		// 优惠金额
		BigDecimal favourAmount = new BigDecimal(0.0);
		// 是否有低价优惠
		boolean isLowFavour = parserBo.isLowFavour();
		if(isLowFavour){
			favourAmount = parserBo.countLowFavour();
		}else{
			favourAmount = getFavourAmount(paramDto, tradeOrder.getTotalAmount());	
		}
		// 设置订单优惠金额
		tradeOrder.setPreferentialPrice(favourAmount);
	}
	
	private BigDecimal getFavourAmount(PlaceOrderParamDto paramDto, BigDecimal totalAmount) throws ServiceException {
		StoreSkuParserBo parserBo = (StoreSkuParserBo)paramDto.get("parserBo");
		BigDecimal favourAmount = new BigDecimal(0.0);
		if(parserBo.isLowFavour()){
			return parserBo.countLowFavour();
		}
		// 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
		ActivityTypeEnum activityType = paramDto.getActivityType();
		switch (activityType) {
			case VONCHER:
				favourAmount = getCouponsFaceValue(paramDto);
				break;
			case FULL_REDUCTION_ACTIVITIES:
				favourAmount = getDiscountValue(paramDto.getActivityItemId());
				break;
			case FULL_DISCOUNT_ACTIVITIES:
				favourAmount = getDiscountFavour(paramDto.getActivityItemId(), totalAmount);
				break;
			default:
				break;
		}
		if (favourAmount == null) {
			throw new ServiceException("未查到优惠金额");
		}
		return favourAmount;
	}

	/**
	 * @Description: 获取代金券面额
	 * @param req 订单请求对象
	 * @return BigDecimal  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private BigDecimal getCouponsFaceValue(PlaceOrderParamDto paramDto) {
		// 查询代金券
		ActivityCoupons activityCoupons = activityCouponsRecordMapper
				.selectCouponsItem(buildCouponsFindVo(paramDto));
		return BigDecimal.valueOf(activityCoupons.getFaceValue());
	}

	/**
	 * @Description: 获取满减优惠金额
	 * @param req 订单请求对象
	 * @param totalAmount 订单总金额
	 * @return BigDecimal  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private BigDecimal getDiscountFavour(String activityItemId, BigDecimal totalAmount) {
		BigDecimal discountVal = getDiscountValue(activityItemId);
		BigDecimal actualAmount = totalAmount.multiply(discountVal).divide(BigDecimal.valueOf(10)).setScale(2,
				BigDecimal.ROUND_DOWN);
		return totalAmount.subtract(actualAmount);
	}

	/**
	 * @Description: 获取满减满折优惠金额
	 * @param req 订单请求对象
	 * @param type 折扣类型：满减或满折
	 * @return BigDecimal  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private BigDecimal getDiscountValue(String activityItemId) {
		return activityDiscountMapper.getDiscountValue(activityItemId);
	}
	
	/**
	 * @Description: 计算订单实付金额
	 * @param tradeOrder 交易订单
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void setActualAmount(TradeOrder tradeOrder,boolean isLowFavour) {
		if(isLowFavour){
			// 如果有低价优惠
			tradeOrder.setActualAmount(tradeOrder.getTotalAmount());
			tradeOrder.setTotalAmount(tradeOrder.getActualAmount().add(tradeOrder.getPreferentialPrice()));
			return;
		}
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
	 * 	订单未参与活动、使用代金券，店铺总收入为：订单总金额
	 * 	订单参与运营商发起的满减活动，店铺总收入为：订单总金额
	 * 	订单参与店铺发起的满减满折活动，店铺总收入为：订单总金额-优惠金额（即订单实付金额）
	 * @param tradeOrder 交易订单  
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void setIncome(TradeOrder tradeOrder,boolean isLowFavour) {

		if(isLowFavour){
			// 有低价优惠
			tradeOrder.setIncome(tradeOrder.getActualAmount());
			return;
		}
		
		ActivityTypeEnum activityType = tradeOrder.getActivityType();
		
		switch (activityType) {
			case NO_ACTIVITY:
			case VONCHER:
				tradeOrder.setIncome(tradeOrder.getTotalAmount());
				break;
			case FULL_REDUCTION_ACTIVITIES:
				if (isPublishByStore(tradeOrder.getActivityId())) {
					// 活动由店铺发起，则收入==实付金额
					tradeOrder.setIncome(tradeOrder.getActualAmount());
				} else {
					tradeOrder.setIncome(tradeOrder.getTotalAmount());
				}
				break;
			case FULL_DISCOUNT_ACTIVITIES:
				tradeOrder.setIncome(tradeOrder.getActualAmount());
				break;
			default:
				break;
		}
	}

	/**
	 * @Description: 构建代金券查询条件
	 * @param req 订单请求对象
	 * @return CouponsFindVo  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private CouponsFindVo buildCouponsFindVo(PlaceOrderParamDto paramDto) {
		CouponsFindVo findCondition = new CouponsFindVo();
		findCondition.setActivityId(paramDto.getActivityId());
		findCondition.setActivityItemId(paramDto.getActivityItemId());
		findCondition.setConponsType(paramDto.getCouponsType());
		return findCondition;
	}
	
	/**
	 * @Description: 判断活动是否由店铺发起
	 * @param activityId 活动ID
	 * @return boolean  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	public boolean isPublishByStore(String activityId) {
		boolean isPublishByStore = true;
		ActivityDiscount discount = activityDiscountMapper.selectByPrimaryKey(activityId);
		String storeId = discount.getStoreId();
		if ("0".equals(storeId)) {
			isPublishByStore = false;
		}
		return isPublishByStore;
	}
	
	/**
	 * @Description: 处理运费
	 * @param tradeOrder
	 * @param fare   
	 * @author maojj
	 * @date 2017年1月6日
	 */
	public void processFare(TradeOrder tradeOrder,BigDecimal fare){
		tradeOrder.setTotalAmount(tradeOrder.getTotalAmount().add(fare));
		tradeOrder.setActualAmount(tradeOrder.getTotalAmount().add(fare));
		tradeOrder.setIncome(tradeOrder.getTotalAmount().add(fare));
		tradeOrder.setFare(fare);
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
	private List<TradeOrderItem> buildOrderItemList(TradeOrder tradeOrder, PlaceOrderParamDto paramDto)
			throws ServiceException {
		List<TradeOrderItem> orderItemList = new ArrayList<TradeOrderItem>();
		StoreSkuParserBo parserBo = (StoreSkuParserBo)paramDto.get("parserBo");
		
		String orderId = tradeOrder.getId();
		// 订单项总金额
		BigDecimal totalAmount = parserBo.getTotalItemAmount();
		BigDecimal totalFavour = tradeOrder.getPreferentialPrice();
		BigDecimal favourSum = new BigDecimal("0.00");
		int index = 0;
		int itemSize = paramDto.getSkuList().size();
		TradeOrderItem tradeOrderItem = null;

		Collection<CurrentStoreSkuBo> skuBoSet = parserBo.getCurrentSkuMap().values();
		List<CurrentStoreSkuBo> goodsItemList = new ArrayList<CurrentStoreSkuBo>();
		goodsItemList.addAll(skuBoSet);
		ComparatorChain chain = new ComparatorChain();
		chain.addComparator(new BeanComparator("skuPrice"), false);
		Collections.sort(goodsItemList, chain);

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
			tradeOrderItem.setActivityQuantity(skuBo.getSkuActQuantity());
			tradeOrderItem.setStatus(OrderItemStatusEnum.NO_REFUND);
			tradeOrderItem.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);
			tradeOrderItem.setAppraise(AppraiseEnum.NOT_APPROPRIATE);
			tradeOrderItem.setCreateTime(new Date());
			tradeOrderItem.setServiceAssurance(
					StringUtils.isEmpty(skuBo.getGuaranteed()) ? 0 : Integer.valueOf(skuBo.getGuaranteed()));
			tradeOrderItem.setActivityType(paramDto.getActivityType().ordinal());
			tradeOrderItem.setActivityId(paramDto.getActivityId());
			tradeOrderItem.setBarCode(skuBo.getBarCode());
			tradeOrderItem.setStyleCode(skuBo.getStyleCode());

			// 订单项总金额
			BigDecimal totalAmountOfItem = skuBo.getTotalAmount();
			tradeOrderItem.setTotalAmount(totalAmountOfItem);
			// 计算订单项优惠金额
			BigDecimal favourItem = BigDecimal.valueOf(0.0);
			if (paramDto.getActivityType() != ActivityTypeEnum.NO_ACTIVITY) {
				if (index++ < itemSize - 1) {
					favourItem = totalAmountOfItem.multiply(totalFavour).divide(totalAmount, 2, BigDecimal.ROUND_FLOOR);
					if (favourItem.compareTo(totalAmountOfItem) == 1) {
						favourItem = totalAmountOfItem;
					}
					favourSum = favourSum.add(favourItem);
				} else {
					favourItem = totalFavour.subtract(favourSum);
					if (favourItem.compareTo(totalAmountOfItem) == 1) {
						favourItem = totalAmountOfItem;
					}
				}
			}
			// 设置优惠金额
			tradeOrderItem.setPreferentialPrice(favourItem);
			// 设置实付金额
			tradeOrderItem.setActualAmount(totalAmountOfItem.subtract(favourItem));
			// 设置实付金额
			if (paramDto.getPayType() == PayWayEnum.OFFLINE_CONFIRM_AND_PAY.ordinal()) {
				// 线下支付的实付金额为0
				tradeOrderItem.setActualAmount(BigDecimal.valueOf(0));
				tradeOrderItem.setIncome(BigDecimal.valueOf(0));
			}else{
				// 设置订单项收入
				setOrderItemIncome(tradeOrderItem, tradeOrder);
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
	
	public void setOrderItemIncome(TradeOrderItem tradeOrderItem, TradeOrder tradeOrder){
		ActivityTypeEnum activityType = tradeOrder.getActivityType();

		switch (activityType) {
			case NO_ACTIVITY:
			case VONCHER:
				tradeOrderItem.setIncome(tradeOrderItem.getTotalAmount());
				break;
			case FULL_REDUCTION_ACTIVITIES:
				if (isPublishByStore(tradeOrder.getActivityId())) {
					// 活动由店铺发起，则收入==实付金额
					tradeOrderItem.setIncome(tradeOrderItem.getActualAmount());
				} else {
					tradeOrderItem.setIncome(tradeOrderItem.getTotalAmount());
				}
				break;
			case FULL_DISCOUNT_ACTIVITIES:
				tradeOrderItem.setIncome(tradeOrderItem.getActualAmount());
				break;
			default:
				break;
		}
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
}