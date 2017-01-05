package com.okdeer.mall.order.builder;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.store.entity.StoreBranches;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.entity.StoreInfoExt;
import com.okdeer.archive.store.service.StoreBranchesServiceApi;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
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
import com.okdeer.mall.member.mapper.MemberConsigneeAddressMapper;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderLogistics;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.PickUpTypeEnum;
import com.okdeer.mall.order.utils.OrderNoUtils;
import com.okdeer.mall.system.utils.ConvertUtil;

/**
 * ClassName: CvsTradeOrderBoBuilder 
 * @Description: 便利店交易订单构建者
 * @author maojj
 * @date 2016年12月22日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿1.2.5		2016年12月22日				maojj
 */
@Component
public class CvsTradeOrderBuilder extends AbstractTradeOrderBuilder {

	private static final Logger logger = LoggerFactory.getLogger(CvsTradeOrderBuilder.class);

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
	 * 会员收货地址信息Mapper
	 */
	@Resource
	private MemberConsigneeAddressMapper memberConsigneeAddressMapper;
	
	/**
	 * 店铺信息Dubbo接口
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoServiceApi;

	@Override
	public void setOrderNo(TradeOrder tradeOrder) throws Exception {
		StoreBranches storeBranches = storeBranchesApi.findBranches(tradeOrder.getStoreId());
		if (storeBranches == null || StringUtils.isEmpty(storeBranches.getBranchCode())) {
			throw new ServiceException(LogConstants.STORE_BRANCHE_NOT_EXISTS);
		}
		String orderNo = generateNumericalService.generateOrderNo(OrderNoUtils.PHYSICAL_ORDER_PREFIX,
				storeBranches.getBranchCode(), OrderNoUtils.ONLINE_POS_ID);
		logger.info("生成订单编号：{}", orderNo);
		tradeOrder.setOrderNo(orderNo);

	}

	@Override
	public void setOrderStatus(TradeOrder tradeOrder, int payType) {
		// 支付方式：0：在线支付、1:货到付款、6：微信支付
		switch (payType) {
			case 0:
			case 6:
				tradeOrder.setStatus(OrderStatusEnum.UNPAID);
				break;
			case 1:
				tradeOrder.setStatus(OrderStatusEnum.DROPSHIPPING);
				break;
			default:
				break;
		}
	}

	@Override
	public void setPayWay(TradeOrder tradeOrder, int payType) {
		// 支付方式：0：在线支付、1:货到付款、6：微信支付
		switch (payType) {
			case 0:
			case 6:
				tradeOrder.setPayWay(PayWayEnum.PAY_ONLINE);
				break;
			case 1:
				tradeOrder.setPayWay(PayWayEnum.CASH_DELIERY);
				break;
			default:
				break;
		}
	}

	@Override
	public void setPickUpTypeAndTime(TradeOrder tradeOrder, PlaceOrderParamDto paramDto) throws Exception {
		PickUpTypeEnum pickType = paramDto.getPickType();
		tradeOrder.setPickUpType(pickType);
		switch (pickType) {
			case DELIVERY_DOOR:
				processDelivery(tradeOrder, paramDto);
				break;
			case TO_STORE_PICKUP:
				processPickUp(tradeOrder, paramDto);
				break;
			default:
				break;
		}
	}
	
	/**
	 * @Description: 送货上门处理
	 * @param tradeOrder 交易订单
	 * @param reqDto 订单请求对象  
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void processDelivery(TradeOrder tradeOrder, PlaceOrderParamDto paramDto) {
		StoreInfoExt storeInfoExt = ((StoreInfo)paramDto.get("storeInfo")).getStoreInfoExt();

		// 店铺起送价格
		BigDecimal startPrice = storeInfoExt.getStartPrice();
		// 店铺运费
		BigDecimal freight = storeInfoExt.getFreight() == null ? new BigDecimal(0.0) : storeInfoExt.getFreight();

		if (startPrice != null) {
			// 判断商品总金额是否达到起送金额 后台判断
			// 如果商品总金额没有达到起送金额,则订单总金额=订单总金额+运费
			if (tradeOrder.getTotalAmount().compareTo(startPrice) == -1) {
				// 运费
				tradeOrder.setFare(freight);
				tradeOrder.setTotalAmount(tradeOrder.getTotalAmount().add(freight));
				tradeOrder.setActualAmount(tradeOrder.getActualAmount().add(freight));
				tradeOrder.setIncome(tradeOrder.getIncome().add(freight));
			} else {
				// 运费
				tradeOrder.setFare(new BigDecimal(0.0));
			}
		}
		// 设置TradeOrderLogistics
		setTradeOrderLogistics(tradeOrder, paramDto.getUserAddrId());
		// 设置提货时间
		setPickUpTime(tradeOrder, paramDto);
	}
	
	/**
	 * @Description: 设置TradeOrderLogistics
	 * @param tradeOrder 交易订单 
	 * @param addressId 店铺地址  
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void setTradeOrderLogistics(TradeOrder tradeOrder, String addressId) {
		// 获取买家收货地址
		MemberConsigneeAddress address = memberConsigneeAddressMapper.selectAddressById(addressId);

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

		tradeOrder.setTradeOrderLogistics(orderLogistics);
	}
	
	/**
	 * @Description: 设置提货时间
	 * @param tradeOrder 交易订单
	 * @param req 订单请求对象 
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void setPickUpTime(TradeOrder tradeOrder, PlaceOrderParamDto paramDto) {
		String pickTime = paramDto.getPickTime();
		// 支付方式：1:货到付款、0：在线支付
		int payType = paramDto.getPayType();

		if (payType == 1 || payType == 0 || payType == 6) {
			if (!"".equals(pickTime)) {
				tradeOrder.setPickUpTime(pickTime);
			} else {
				tradeOrder.setPickUpTime("立即配送");
			}
		}
	}

	/**
	 * @Description: 处理到店自提
	 * @param tradeOrder 交易订单
	 * @param reqDto 订单请求对象
	 * @return void  
	 * @throws ServiceException 自定义异常
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void processPickUp(TradeOrder tradeOrder, PlaceOrderParamDto paramDto) throws ServiceException {
		setPickUpIdAndTime(tradeOrder, paramDto);
		tradeOrder.setFare(new BigDecimal("0.00"));
	}

	/**
	 * @Description: 设置提货时间
	 * @param tradeOrder 交易订单
	 * @param reqDto 订单请求对象
	 * @return void  
	 * @throws ServiceException 自定义异常
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void setPickUpIdAndTime(TradeOrder tradeOrder, PlaceOrderParamDto paramDto) throws ServiceException {
		String receiveTime = paramDto.getReceiveTime();

		StoreInfoExt storeInfoExt = ((StoreInfo)paramDto.get("storeInfo")).getStoreInfoExt();

		StoreInfo storeInfo = storeInfoServiceApi.selectDefaultAddressById(paramDto.getStoreId());
		// 获取默认地址
		String defaultAddressId = storeInfo.getMemberConsignee().getId();
		tradeOrder.setPickUpId(defaultAddressId);
		if (!StringUtils.isEmpty(receiveTime)) {
			tradeOrder.setPickUpTime(receiveTime);
		} else {
			String attrTime = DateUtils.getDate();
			String pcTime = attrTime + " " + storeInfoExt.getServiceStartTime() + "-"
					+ storeInfoExt.getServiceEndTime();
			tradeOrder.setPickUpTime(pcTime);
		}
	}

	@Override
	public void setPickUpId(TradeOrder tradeOrder, PlaceOrderParamDto paramDto) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseFavour(TradeOrder tradeOrder, PlaceOrderParamDto paramDto) throws ServiceException {
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
		// 设置订单实付金额
		setActualAmount(tradeOrder,isLowFavour);
		// 设置店铺总收入
		setIncome(tradeOrder,isLowFavour);

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
		//findCondition.setConponsType(paramDto.getCouponsType());
		return findCondition;
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

	@Override
	/**
	 * @Description: 设置订单项收入
	 * @param tradeOrderItem 交易订单项
	 * @param tradeOrder 交易订单
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	public void setOrderItemIncome(TradeOrderItem tradeOrderItem, TradeOrder tradeOrder) {
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
}
