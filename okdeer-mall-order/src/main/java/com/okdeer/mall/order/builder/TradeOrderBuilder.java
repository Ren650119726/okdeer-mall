package com.okdeer.mall.order.builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.assemble.GoodsStoreSkuAssembleApi;
import com.okdeer.archive.goods.assemble.dto.GoodsStoreAssembleDto;
import com.okdeer.archive.goods.assemble.dto.GoodsStoreSkuAssembleDto;
import com.okdeer.archive.goods.spu.enums.SkuBindType;
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
import com.okdeer.mall.activity.coupons.enums.ActivityDiscountItemRelType;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountType;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.common.utils.DateUtils;
import com.okdeer.mall.common.utils.TradeNumUtil;
import com.okdeer.mall.member.mapper.MemberConsigneeAddressMapper;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.order.bo.CurrentStoreSkuBo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderItemDto;
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
import com.okdeer.mall.order.enums.OrderTypeEnum;
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
	 * 佣金方案b
	 */
	private static final Integer DELIVERY_TYPE_A = 1;
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
	
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuAssembleApi goodsStoreSkuAssembleApi;

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
		// 拆分商品项
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
		tradeOrder.setShareUserId(paramDto.getShareUserId());
		// 设置订单配送方式和佣金比率
		if(storeInfo.getStoreInfoExt() != null){
			//直接设置佣金方案为： A 
			tradeOrder.setDeliveryType(DELIVERY_TYPE_A);
			tradeOrder.setCommisionRatio(storeInfo.getStoreInfoExt().getCommisionRatio());
		}
		// 设置订单编号
		setOrderNo(tradeOrder,paramDto.getOrderType());
		// 设置订单总品项
		tradeOrder.setTotalKind(paramDto.getSkuList().size());
		// 设置订单总数量
		tradeOrder.setTotalQuantity(parserBo.getTotalQuantity());
		// 设置订单总金额  不包括N件X元 满赠 加价购优惠
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
		// 计算订单实付金额和零花钱
		setActualAmount(tradeOrder,paramDto);
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
		if(paramDto.getOrderType() == PlaceOrderTypeEnum.GROUP_ORDER){
			return;
		}
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
			tradeOrder.setStoreActivityType(parserBo.getStoreActivityType());
			ActivityDiscount activity =(ActivityDiscount) paramDto.get("storeActivity");
			if(activity != null && (activity.getType() == ActivityDiscountType.JJG || activity.getType() == ActivityDiscountType.MMS)){
				//店铺不保存 满赠 加价购优惠
				tradeOrder.setStorePreferential(parserBo.getTotalLowFavour().subtract(parserBo.getTotalStorePereferAmount()));
				tradeOrder.setTotalAmount(tradeOrder.getTotalAmount().subtract(parserBo.getTotalStorePereferAmount()));
			}else{
				tradeOrder.setStorePreferential(parserBo.getTotalLowFavour());
			}
			
			tradeOrder.setStoreActivityId(parserBo.getLowActivityId());
		} else if (paramDto.getOrderType() == PlaceOrderTypeEnum.SECKILL_ORDER){
			// 如果是秒杀订单。优惠是活动价格-商品原价。秒杀属于店铺优惠
			ActivitySeckill seckillInfo = (ActivitySeckill)paramDto.get("seckillInfo");
			BigDecimal favourAmount = parserBo.getCurrentStoreSkuBo(seckillInfo.getStoreSkuId()).getOnlinePrice().subtract(seckillInfo.getSeckillPrice());
			tradeOrder.setStorePreferential(favourAmount);
		}else {
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
	private void setActualAmount(TradeOrder tradeOrder,PlaceOrderParamDto paramDto) {
		BigDecimal totalAmount = tradeOrder.getTotalAmount();
		BigDecimal favourAmount = tradeOrder.getPreferentialPrice();
		// 如果总金额<优惠金额，则实付为0，优惠为订单总金额，否则实付金额为总金额-优惠金额，优惠为优惠金额
		if (totalAmount.compareTo(favourAmount) < 0) {
			tradeOrder.setActualAmount(BigDecimal.ZERO);
			tradeOrder.setPreferentialPrice(totalAmount);
			tradeOrder.setPinMoney(BigDecimal.ZERO);
			// Begin V2.6.1 added by maojj 2017-09-08
			// 优惠金额>总金额，重置优惠金额，同时重置平台优惠金额
			tradeOrder.setPlatformPreferential(tradeOrder.getPreferentialPrice().subtract(tradeOrder.getStorePreferential()));
			// End V2.6.1 added by maojj 2017-09-08
		} else {
			//实际支付差额
			BigDecimal actualAmount = totalAmount.subtract(favourAmount);
			// 使用零花钱
			BigDecimal usePinMoney = BigDecimal.ZERO;
			BigDecimal preferentialPrice = tradeOrder.getPreferentialPrice();
			if(PlaceOrderTypeEnum.SECKILL_ORDER != paramDto.getOrderType() && paramDto.getIsUsePinMoney()){
				BigDecimal pinMoney = (BigDecimal) paramDto.get("pinMoneyAmount");
				usePinMoney =  actualAmount.compareTo(pinMoney) >= 0 ? pinMoney : actualAmount;
				actualAmount = actualAmount.subtract(usePinMoney);
				preferentialPrice = preferentialPrice.add(usePinMoney);
			}
			tradeOrder.setPreferentialPrice(preferentialPrice);
			tradeOrder.setPinMoney(usePinMoney);
			tradeOrder.setActualAmount(actualAmount);
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
		BigDecimal income = tradeOrder.getTotalAmount().subtract(tradeOrder.getStorePreferential());
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
		// 如果店铺选择的是第三方配送，运费不计入收入。如果店铺选择的是商家自送，运费计入补贴
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
		List<TradeOrderItem> orderItemList = new ArrayList<>();
		StoreSkuParserBo parserBo = (StoreSkuParserBo)paramDto.get("parserBo");
		
		String orderId = tradeOrder.getId();
		// 订单项参与平台优惠的总金额
		BigDecimal totalAmount = parserBo.getTotalAmountHaveFavour();
		// 订单总的平台优惠 （需分摊到商品）: 平台优惠 + 红包优惠
		BigDecimal platformFavour = parserBo.getPlatformPreferential();
		BigDecimal favourSum =  BigDecimal.valueOf(0.00);
		int index = 0;
		int haveFavourItemSize = CollectionUtils.isEmpty(parserBo.getEnjoyFavourSkuIdList()) ? 0
				: parserBo.getEnjoyFavourSkuIdList().size();
		TradeOrderItem tradeOrderItem = null;

		List<CurrentStoreSkuBo> goodsItemList = new ArrayList<>();
		//拆分订单项
		goodsItemList.addAll(parserBo.splitSkuMap(paramDto));
		
		ComparatorChain chain = new ComparatorChain();
		chain.addComparator(new BeanComparator("onlinePrice"), false);
		Collections.sort(goodsItemList, chain);
		for (CurrentStoreSkuBo skuBo : goodsItemList) {
			tradeOrderItem = new TradeOrderItem();

			tradeOrderItem.setId(UuidUtils.getUuid());
			tradeOrderItem.setOrderId(orderId);
			tradeOrderItem.setStoreSpuId(skuBo.getStoreSpuId());
			tradeOrderItem.setStoreSkuId(skuBo.getId());
			tradeOrderItem.setSkuName(skuBo.getName());
			tradeOrderItem.setPropertiesIndb(skuBo.getPropertiesIndbSkuName());
			tradeOrderItem.setMainPicPrl(skuBo.getMainPicUrl());
			tradeOrderItem.setSpuType(skuBo.getSpuType());
			tradeOrderItem.setBindType(skuBo.getBindType());
			tradeOrderItem.setUnitPrice(skuBo.getOnlinePrice());
			tradeOrderItem.setQuantity(skuBo.getQuantity());
			tradeOrderItem.setActivityPrice(skuBo.getActPrice());
			tradeOrderItem.setStatus(OrderItemStatusEnum.NO_REFUND);
			tradeOrderItem.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);
			tradeOrderItem.setAppraise(AppraiseEnum.NOT_APPROPRIATE);
			tradeOrderItem.setCreateTime(new Date());
			tradeOrderItem.setServiceAssurance(
					StringUtils.isEmpty(skuBo.getGuaranteed()) ? 0 : Integer.valueOf(skuBo.getGuaranteed()));
			
			if(tradeOrder.getType() == OrderTypeEnum.GROUP_ORDER){
				// 如果是团购订单，服务保障默认为1天
				tradeOrderItem.setServiceAssurance(1);
			}
			
			// 订单项店铺优惠金额
			BigDecimal storeFavourItem = BigDecimal.valueOf(0.0);
			if(skuBo.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal() && skuBo.getSkuActQuantity() > 0){
				tradeOrderItem.setStoreActivityType(ActivityTypeEnum.enumValueOf(skuBo.getActivityType()));
				tradeOrderItem.setStoreActivityId(skuBo.getActivityId());
				tradeOrderItem.setActivityQuantity(skuBo.getSkuActQuantity());
				storeFavourItem = skuBo.getOnlinePrice().subtract(skuBo.getActPrice())
						.multiply(BigDecimal.valueOf(skuBo.getSkuActQuantity()));
			}else if(skuBo.getActivityType() == ActivityTypeEnum.SECKILL_ACTIVITY.ordinal()){
				storeFavourItem = tradeOrder.getStorePreferential();
					
			//满赠、加价购、N件x元活动计算到店铺活动中
			}else if(skuBo.getActivityType() == ActivityTypeEnum.MMS.ordinal() 
					|| skuBo.getActivityType() == ActivityTypeEnum.JJG.ordinal()
					|| skuBo.getActivityType() == ActivityTypeEnum.NJXY.ordinal()){
				//参加以上活动商品不可退
				tradeOrderItem.setStoreActivityItemId(skuBo.getStoreActivityItemId());
				tradeOrderItem.setServiceAssurance(0);
				if(skuBo.getBindType() == SkuBindType.JJG || skuBo.getBindType() == SkuBindType.MMS){
					tradeOrderItem.setUnitPrice(skuBo.getActPrice());
				}	
				tradeOrderItem.setStoreActivityType(ActivityTypeEnum.enumValueOf(skuBo.getActivityType()));
				tradeOrderItem.setStoreActivityId(skuBo.getActivityId());
			}else{
				tradeOrderItem.setStoreActivityType(ActivityTypeEnum.NO_ACTIVITY);
				tradeOrderItem.setStoreActivityId("0");
			}
			tradeOrderItem.setActivityType(paramDto.getActivityType().ordinal());
			tradeOrderItem.setActivityId(paramDto.getActivityId());
			
			tradeOrderItem.setBarCode(skuBo.getBarCode());
			tradeOrderItem.setStyleCode(skuBo.getStyleCode());
			tradeOrderItem.setUnit(skuBo.getUnit());

			// 订单项总金额 //如果为N件X元、加价购之类的优惠项，需要记录到总金额中
			BigDecimal totalAmountOfItem = skuBo.getTotalAmount();
			if(skuBo.getActivityType() == ActivityTypeEnum.NJXY.ordinal()){
				storeFavourItem = skuBo.getPreferentialPrice();
				totalAmountOfItem = totalAmountOfItem.add(storeFavourItem);
			}
			tradeOrderItem.setTotalAmount(totalAmountOfItem);
			
			// 计算订单项平台优惠金额
			BigDecimal favourItem = BigDecimal.valueOf(0.0);
			if (platformFavour.compareTo(referenceValue) > 0) {
				// 如果有优惠的商品项不包含改商品，意味着该商品不享有平台优惠  2017-12-14 添加换购商品及赠送商品不享受平台优惠
				if(!parserBo.getEnjoyFavourSkuIdList().contains(skuBo.getId()) || skuBo.getBindType() == SkuBindType.JJG 
						|| skuBo.getBindType() == SkuBindType.MMS){
					// 如果有优惠的商品项不包含改商品，意味着该商品不享有平台优惠
					favourItem = BigDecimal.valueOf(0.0);
				} else if (index++ < haveFavourItemSize - 1) {
					if (skuBo.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal()) {
						// 如果是低价商品，平台优惠只针对按照原价购买的商品进行平摊=原价购买数量*原价*总优惠/优惠总金额
						favourItem = skuBo.getOnlinePrice()
								.multiply(BigDecimal.valueOf(skuBo.getQuantity() - skuBo.getSkuActQuantity()))
								.multiply(platformFavour).divide(totalAmount, 2, BigDecimal.ROUND_FLOOR);
					} else if(skuBo.getActivityType() == ActivityTypeEnum.NJXY.ordinal()){
						favourItem = skuBo.getTotalAmount().multiply(platformFavour).divide(totalAmount, 2,
								BigDecimal.ROUND_FLOOR);
					}else {
						favourItem = totalAmountOfItem.multiply(platformFavour).divide(totalAmount, 2,
								BigDecimal.ROUND_FLOOR);
					}
					if (favourItem.compareTo(totalAmountOfItem.subtract(storeFavourItem)) > 0) {
						favourItem = totalAmountOfItem.subtract(storeFavourItem);
					}
					favourSum = favourSum.add(favourItem);
				} else {
					favourItem = platformFavour.subtract(favourSum);
					if (favourItem.compareTo(totalAmountOfItem) > 0) {
						favourItem = totalAmountOfItem;
					}
				}
			}
			// 设置优惠金额
			tradeOrderItem.setPreferentialPrice(favourItem.add(storeFavourItem));
			// 设置平台优惠金额
			tradeOrderItem.setStorePreferential(storeFavourItem);
			// 设置实付金额 = 订单总金额-订单总优惠
			tradeOrderItem.setActualAmount(totalAmountOfItem.subtract(tradeOrderItem.getPreferentialPrice()));
			// 设置实付金额
			if (paramDto.getPayType() == PayWayEnum.OFFLINE_CONFIRM_AND_PAY.ordinal()) {
				// 线下支付的实付金额为0
				tradeOrderItem.setActualAmount(BigDecimal.valueOf(0));
				tradeOrderItem.setIncome(BigDecimal.valueOf(0));
			}else{
				// 设置订单项总收入=订单项总金额-订单项店铺优惠
				tradeOrderItem.setIncome(totalAmountOfItem.subtract(storeFavourItem));
			}
			if (tradeOrderItem.getActualAmount().compareTo(BigDecimal.ZERO) == 0
					&& tradeOrderItem.getSpuType() == SpuTypeEnum.fwdDdxfSpu ) {
				// 实付金额为0的到店消费商品，设置服务保障为无
				tradeOrderItem.setServiceAssurance(0);
			}
			
			
			orderItemList.add(tradeOrderItem);
		}
		// Begin V2.6.1 added by maojj 2017-09-01
		// 分配零花钱到各个订单项
		allocatePinMoney(tradeOrder.getPinMoney(),orderItemList);
		// End V2.6.1 added by maojj 2017-09-01
		return orderItemList;
	}
	
	// Begin V2.6.1 added by maojj 2017-09-01
	/**
	 * @Description: 分配零花钱
	 * @param pinMoney
	 * @param orderItemList   
	 * @author maojj
	 * @date 2017年9月1日
	 */
	private void allocatePinMoney(BigDecimal pinMoney, List<TradeOrderItem> orderItemList) {
		if (pinMoney == null || pinMoney.compareTo(BigDecimal.ZERO) <= 0) {
			// 如果零花钱的值<=0,则无需做任何处理
			return;
		}
		// 统计订单项的总的实付金额，用于分摊零花钱
		BigDecimal totalActual = BigDecimal
				.valueOf(orderItemList.stream().mapToDouble(orderItem -> orderItem.getActualAmount().doubleValue()).sum());
		if(totalActual.compareTo(BigDecimal.ZERO) <= 0){
			// 如果实付金额为0
			return;
		}
		// 分配剩余零花钱金额
		BigDecimal unAllocateMoney = pinMoney;
		// 订单项分配的零花钱金额
		BigDecimal pinMoneyItem = null;
		// 订单项索引
		int index = 0;
		// 订单过滤实付金额为0的总数
		int size = (int)orderItemList.stream().filter(orderItem -> orderItem.getActualAmount().compareTo(BigDecimal.ZERO) > 0).count();
		// 订单项根据实付金额倒序序排列
		ComparatorChain chain = new ComparatorChain();
		chain.addComparator(new BeanComparator("actualAmount"), false);
		Collections.sort(orderItemList, chain);
		// 遍历订单项对零花钱进行分摊处理
		for(TradeOrderItem orderItem : orderItemList){
			if(orderItem.getActualAmount().compareTo(BigDecimal.ZERO) == 0){
				continue;
			}
			if(index++ < size - 1){
				pinMoneyItem = orderItem.getActualAmount().multiply(pinMoney).divide(totalActual, 2,
						BigDecimal.ROUND_FLOOR);
				unAllocateMoney = unAllocateMoney.subtract(pinMoneyItem);
			}else{
				pinMoneyItem = orderItem.getActualAmount().compareTo(unAllocateMoney) > 0?unAllocateMoney:orderItem.getActualAmount();
			}
			orderItem.setPreferentialPrice(orderItem.getPreferentialPrice().add(pinMoneyItem));
			orderItem.setActualAmount(orderItem.getActualAmount().subtract(pinMoneyItem));
			if (orderItem.getActualAmount().compareTo(BigDecimal.ZERO) == 0
					&& orderItem.getSpuType() == SpuTypeEnum.fwdDdxfSpu ) {
				// 实付金额为0的到店消费商品，设置服务保障为无
				orderItem.setServiceAssurance(0);
			}
		}
	}
	// End V2.6.1 added by maojj 2017-09-01
	
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
	 * @throws Exception 
	 * @date 2017年6月23日
	 */
	private List<TradeOrderComboSnapshot> buildComboDetailList(String orderId,PlaceOrderParamDto paramDto) throws Exception{
		List<TradeOrderComboSnapshot> comboDetailList = Lists.newArrayList();
		TradeOrderComboSnapshot comboSnapshot = null;
		StoreSkuParserBo parserBo = (StoreSkuParserBo)paramDto.get("parserBo");
		if (CollectionUtils.isNotEmpty(parserBo.getComboSkuIdList())) {
			List<GoodsStoreAssembleDto> comboDtoList = goodsStoreSkuAssembleApi
					.findByAssembleSkuIds(parserBo.getComboSkuIdList());
			parserBo.loadComboSkuList(comboDtoList);
		}
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