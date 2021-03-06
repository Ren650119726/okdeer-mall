package com.okdeer.mall.order.handler.impl;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.base.common.enums.CommonResultCodeEnum;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.redis.IRedisTemplateWrapper;
import com.okdeer.common.utils.JsonDateUtil;
import com.okdeer.jxc.bill.service.HykPayOrderServiceApi;
import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.discount.entity.PreferentialVo;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.common.enums.UseClientType;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.MemberCardResultDto;
import com.okdeer.mall.order.dto.MemberTradeOrderDto;
import com.okdeer.mall.order.dto.PayInfoDto;
import com.okdeer.mall.order.dto.PayInfoParamDto;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderItemDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.enums.OrderCancelType;
import com.okdeer.mall.order.enums.OrderIsShowEnum;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.handler.MemberCardOrderService;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.service.GetPreferentialService;
import com.okdeer.mall.order.service.ScanOrderService;
import com.okdeer.mall.order.service.TradeOrderPayService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.service.TradePinMoneyObtainService;
import com.okdeer.mall.order.service.TradePinMoneyUseService;
import com.okdeer.mall.order.vo.Coupons;
import com.okdeer.mall.order.vo.TradeOrderItemVo;
import com.okdeer.mall.system.service.SysBuyerUserServiceApi;

import scala.util.Random;

/**
 * ClassName: MemberCardOrderService 
 * @Description: 会员卡服务处理实现类
 * @author tuzhd
 * @date 2017年8月9日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V2.5.2			2017-08-09			tuzhd			会员卡服务处理实现类
 */
@Service
public class MemberCardOrderServiceImpl implements MemberCardOrderService {
	private static final Logger logger = LoggerFactory.getLogger(MemberCardOrderServiceImpl.class);
	
	//注入redis缓存记录
	@Resource
	private IRedisTemplateWrapper<String, Object> redisTemplateWrapper;
	
	//优惠信息查询类注入
	@Resource
	GetPreferentialService getPreferentialService;
	
	@Reference(version = "1.0.0", check = false)
    private SysBuyerUserServiceApi buyserUserService;
	 /**
     * 导入 goodsStoreSkuApi服务接口
     */
    @Reference(version = "1.0.0", check = false)
    private GoodsStoreSkuServiceApi goodsStoreSkuApi;
    
   /**
	 * 优惠信息校验
	 */
	@Resource
	RequestHandler<PlaceOrderParamDto, PlaceOrderDto>  checkFavourService;
	
	@Autowired
	private ScanOrderService scanOrderService;
	/**
     * 导入tradeOrder服务接口
     */
	@Resource
    private TradeOrderService tradeOrderService;
	
	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoService;
	/**
	 * 会员卡零售调用api
	 */
	@Reference(version = "1.0.0", check = false)
	HykPayOrderServiceApi hykPayOrderServiceApi;
	
	/**
	 * 支付服务类
	 */
	@Resource
	private  TradeOrderPayService tradeOrderPayService;
	
	@Autowired
	private TradePinMoneyObtainService tradePinMoneyObtainService;
	
	@Autowired
	private TradePinMoneyUseService tradePinMoneyUseService;
	
 	/**
 	 * 优惠信息校验
 	 */
 	@Resource
 	RequestHandler<PlaceOrderParamDto, PlaceOrderDto>  checkPinMoneyService;
    
	
	/**
	 * 会员卡订单缓存后缀
	 */
	private String orderKeyStr = ":memberCardOrder";
	
	/**
	 * 会员卡缓存后缀
	 */
	private String cardKeyStr = ":memberCard";
	
	private int  timeOutMinutes = 30;
	
	/**
	 * @Description: 会员卡订单同步
	 * @param vo 同步记录
	 * @return MemberCardResultDto  返回结果
	 * @author tuzhd
	 * @throws Exception 
	 * @date 2017年8月8日
	 */
	public MemberCardResultDto<MemberTradeOrderDto> pushMemberCardOrder(MemberTradeOrderDto vo) throws Exception{
		MemberCardResultDto<MemberTradeOrderDto> dto = new MemberCardResultDto<>();
		dto.setData(vo);
		//获取及检查用户信息
		if(!checkUseInfo(vo)){
			dto.setCode(CommonResultCodeEnum.FAIL.getCode());
			dto.setMessage("会员卡信息已失效");
			return dto;
		}
		
		//记录订单信息存在返回，不存在存到缓存中
		String key = vo.getOrderId() + orderKeyStr;
		if(redisTemplateWrapper.get(key) != null){
			dto.setCode(CommonResultCodeEnum.FAIL.getCode());
			dto.setMessage("订单已经推送，请与用户确认");
			return dto;
		}

		//默认无活动
		vo.setActivityType(ActivityTypeEnum.NO_ACTIVITY);
		//设置商品主图及店铺商品信息
		setGoodsStoreSkuInfo(vo);
		//设置为使用才进行查询优惠信息  并设置优惠信息
		if(vo.isUserDiscount()){
			//可以使用的优惠金额  即是 排除掉  不可使用优惠的   商品金额
			BigDecimal platDiscountAmount = BigDecimal.ZERO;
			BigDecimal paymentAmount = vo.getPaymentAmount();
			FavourParamBO parambo = createFavourParamBo(vo);
			PreferentialVo preferentialVo = getPreferentialService.findPreferByCard(parambo);
			Coupons coupons = (Coupons) preferentialVo.getMaxFavourOnline();
			if(coupons != null){
				//设置优惠金额
				vo.setCouponsFaceValue(BigDecimal.valueOf(Double.parseDouble(coupons.getDiscountAmount())));
				vo.setCouponsId(coupons.getCouponId());
				vo.setRecordId(coupons.getRecordId());
				vo.setCouponsActivityId(coupons.getId());
				vo.setActivityType(ActivityTypeEnum.VONCHER);
				
				//可以使用优惠金额
				BigDecimal canDiscountAmount = vo.getCanDiscountAmount();
				//面额
				BigDecimal faceValue = vo.getCouponsFaceValue();
				// 如果可以使用优惠金额 < 面额，则实付为0，优惠为可以优惠金额，否则为可以优惠金额-优惠金额，优惠为优惠金额
				if (canDiscountAmount.compareTo(faceValue) < 0) {
					platDiscountAmount = canDiscountAmount;
					paymentAmount = vo.getPaymentAmount().subtract(canDiscountAmount);
				} else {
					platDiscountAmount = faceValue;
					paymentAmount = vo.getPaymentAmount().subtract(faceValue);
				}
			}
			//使用零花钱
			BigDecimal usePinMoney = BigDecimal.ZERO;
			BigDecimal myUsable = tradePinMoneyObtainService.findMyUsableTotal(vo.getUserId(),new Date());
			if (myUsable.compareTo(BigDecimal.ZERO) > 0 && paymentAmount.compareTo(BigDecimal.ZERO) > 0) {
				usePinMoney = paymentAmount.compareTo(myUsable) >= 0 ? myUsable : paymentAmount;
				paymentAmount = paymentAmount.subtract(usePinMoney);
			}
			vo.setPlatDiscountAmount(platDiscountAmount);
			vo.setPaymentAmount(paymentAmount);
			vo.setPinMoneyAmount(usePinMoney);
		}
		vo.setOrderResource(OrderResourceEnum.MEMCARD);
		//不存在存到缓存中
    	redisTemplateWrapper.set(key, vo, timeOutMinutes, TimeUnit.MINUTES);
    	//去掉优惠码位存储，利于前端提取
    	String cardKey = vo.getMemberPayNum().substring(0, vo.getMemberPayNum().length()-1) + cardKeyStr;
    	redisTemplateWrapper.set(cardKey,key, timeOutMinutes, TimeUnit.MINUTES);
    	
    	//返回结果信息
    	dto.setCode(CommonResultCodeEnum.SUCCESS.getCode());
		dto.setMessage(CommonResultCodeEnum.SUCCESS.getDesc());
		return dto;
	}
	
	//设置商品主图及店铺商品信息
	private void setGoodsStoreSkuInfo(MemberTradeOrderDto vo){
		List<TradeOrderItemVo> items = vo.getList();
		if(CollectionUtils.isNotEmpty(items)){
			for(TradeOrderItemVo itemVo : items){
				//在线上查找是否有对应商品，如果有，将对应信息设置进去
				GoodsStoreSku goodsStoreSku = goodsStoreSkuApi.selectByStoreIdAndSkuId(vo.getBranchId(), itemVo.getSkuId());
				if(goodsStoreSku != null && StringUtils.isNotBlank(goodsStoreSku.getId())){
					itemVo.setStoreSkuId(goodsStoreSku.getId());
					itemVo.setMainPicPrl(goodsStoreSku.getContent());
				}
			}
		}
	}
	/**
	 * @Description: 提交会员卡订单
	 * @param memberPayNum
	 * @throws Exception   
	 * @author tuzhd
	 * @date 2017年8月9日
	 */
	@Transactional(rollbackFor = Exception.class)
	public Response<PlaceOrderDto> submitOrder(String orderId,Response<PlaceOrderDto> resp) throws Exception{
		resp.setResult(ResultCodeEnum.SUCCESS);
		//获取会员卡信息对应的订单
    	MemberTradeOrderDto order = (MemberTradeOrderDto) redisTemplateWrapper.get(orderId + orderKeyStr);
    	if(order != null){
    		StoreSkuParserBo bo= new StoreSkuParserBo(Lists.newArrayList());
	    	Request<PlaceOrderParamDto> req = new Request<>();
	    	PlaceOrderParamDto paramDto= createPlaceOrderParamDto(order);
	    	paramDto.getCacheMap().put("parserBo", bo);
	    	req.setData(paramDto);
	    	//共用实物订单代金券校验
	    	checkFavourService.process(req, resp);
	    	//如果检查结果为false 且为代金券类型
	    	if(!resp.isSuccess()){
	    		return resp;
	    	}
	    	//检查零花钱
	    	checkPinMoneyService.process(req, resp);
	    	if(!resp.isSuccess()){
	    		return resp;
	    	}
    		//优惠为0需要提交优惠信息
	    	MemberCardResultDto<MemberTradeOrderDto> result=  hykPayOrderServiceApi.readyPayOrder(order);
	    	if(result.getCode() == CommonResultCodeEnum.SUCCESS.getCode()){
	    		//保存订单信息 及设置返回信息
	    		saveCardOrder(result.getData(),resp);
	    		//移除会员卡信息
	    		removetMemberPayNumber(order.getMemberPayNum());
	    	}
    		
    	}else{
    		resp.setResult(ResultCodeEnum.TRADE_CANCEL_OUT);
    	}
    	return resp;
	}
    	
    
	/**
	 * @Description: 获取支付信息
	 * @param dto
	 * @throws ServiceException 
	 * @throws
	 * @author tuzhd
	 * @date 2017年8月10日
	 */
	public MemberCardResultDto<PayInfoDto> getPayInfo(PayInfoParamDto dto) throws ServiceException{
		MemberCardResultDto<PayInfoDto> payResult =  null;
		//获取会员卡信息对应的订单
    	MemberTradeOrderDto order = (MemberTradeOrderDto) redisTemplateWrapper.get(dto.getOrderId() + orderKeyStr);
    	if(order == null){
    		
    		//订单取消不能支付将 生成的立即订单取消
    		TradeOrder tradeOrder = new TradeOrder();
			tradeOrder.setId(dto.getOrderId());
			tradeOrder.setStatus(OrderStatusEnum.CANCELED);
			tradeOrder.setCancelType(OrderCancelType.CANCEL_BY_BUYER);
			tradeOrder.setUpdateTime(new Date());
			tradeOrder.setOrderResource(OrderResourceEnum.MEMCARD);
			//释放所有代金卷
			tradeOrderService.updateOrderStatus(tradeOrder);
    		payResult = new MemberCardResultDto<>();
    		payResult.setCode(ResultCodeEnum.TRADE_CANCEL_OUT.getCode());
    		payResult.setMessage(ResultCodeEnum.TRADE_CANCEL_OUT.getDesc());
    		redisTemplateWrapper.del(dto.getOrderId() + orderKeyStr);
    		return payResult;
    	}
    	order.setIp(dto.getIp());
		order.setPayType(PayTypeEnum.enumValueOf(dto.getPaymentType()));
		TradeOrder tradeOrder = tradeOrderService.selectById(order.getOrderId());
		order.setTradeNum(tradeOrder.getTradeNum());
		payResult = hykPayOrderServiceApi.payOrder(order);
		return payResult;
	}
	
	
	/**
	 * @Description: 执行保存订单
	 * @param vo
	 * @throws Exception   
	 * @throws
	 * @author tuzhd
	 * @date 2017年8月9日
	 */
	private void saveCardOrder(MemberTradeOrderDto vo,Response<PlaceOrderDto> resp) throws Exception{
		//转化结果集
		TradeOrder persity = BeanMapper.map(vo, TradeOrder.class);
		//设置id值
		persity.setId(vo.getOrderId());
		persity.setActivityType(vo.getActiviType());
		if(vo.getActiviType() != ActivityTypeEnum.NO_ACTIVITY){
			persity.setActivityId(vo.getCouponsActivityId());
		}
		//设置返回值
		resp.getData().setOrderId(vo.getOrderId());
		resp.getData().setOrderNo(vo.getOrderNo());
		
		resp.getData().setTradeNum(vo.getTradeNum());
		resp.getData().setOrderPrice(JsonDateUtil.priceConvertToString(vo.getPaymentAmount(),2,3));
		resp.getData().setLimitTime(timeOutMinutes*60);
		resp.getData().setCurrentTime(System.currentTimeMillis());
		resp.getData().setPaymentMode(0);
		resp.getData().setIsReachPrice(1);
		//平台优惠
		resp.getData().setFavour(JsonDateUtil.priceConvertToString(vo.getPlatDiscountAmount(),2,3));
		resp.getData().setUsablePinMoney(JsonDateUtil.priceConvertToString(vo.getPinMoneyAmount(),2,3));
		//店铺优惠金额
		resp.getData().setStoreFavour(JsonDateUtil.priceConvertToString(vo.getDiscountAmount(),2,3));
		resp.getData().setOrderResource(OrderResourceEnum.MEMCARD.ordinal());
		//设置店铺id
		persity.setStoreId(vo.getBranchId());
		//实付金额
		persity.setActualAmount(vo.getPaymentAmount());
		//优惠金额
		BigDecimal prefer = vo.getPlatDiscountAmount() != null ? vo.getPlatDiscountAmount():BigDecimal.ZERO;
		BigDecimal discount = vo.getDiscountAmount() != null ? vo.getDiscountAmount():BigDecimal.ZERO;
		BigDecimal pinMoney = vo.getPinMoneyAmount()!=null?vo.getPinMoneyAmount():BigDecimal.ZERO;
		//店铺优惠
		persity.setStorePreferential(discount);
		//总优惠
		persity.setPreferentialPrice(discount.add(prefer).add(pinMoney));
		//平台优惠字段
		persity.setPlatformPreferential(prefer);
		//零花钱优惠
		persity.setPinMoney(pinMoney);
		persity.setPayWay(PayWayEnum.PAY_ONLINE);
		persity.setCreateTime(new Date());
		persity.setUpdateTime(persity.getCreateTime());
		//设置显示
		persity.setIsShow(OrderIsShowEnum.yes);
		//设置店铺信息
		StoreInfo storeInfo = storeInfoService.findById(vo.getBranchId());
		persity.setStoreName(storeInfo != null ?storeInfo.getStoreName() : "");
		//设置用户信息
		SysBuyerUser user = buyserUserService.findByPrimaryKey(vo.getUserId());
		
		persity.setUserPhone(user != null ? user.getPhone() : "");
		if(CollectionUtils.isEmpty(vo.getList())){
			resp.setResult(ResultCodeEnum.FAIL);
			return;
		}
		vo.getList().forEach(e -> {
			//称重商品值
			if(e.getPricingType() == 1){
				e.setQuantity(null);
			}
		});
		
		//在线上查找是否有对应商品，在推送的时候已经放入
//		persity.setTradeOrderItem(BeanMapper.mapList(vo.getList(),TradeOrderItem.class));
		List<TradeOrderItem> items = Lists.newArrayList();
		vo.getList().forEach((itemVo)->{
			TradeOrderItem item = BeanMapper.map(itemVo,TradeOrderItem.class);
			item.setIncome(itemVo.getActualAmount());
			item.setServiceAssurance(0);
			items.add(item);
		});
		persity.setTradeOrderItem(items);
		//将订单状态标记为：等待买家付款
		persity.setStatus(OrderStatusEnum.UNPAID);
		
		logger.info("会员卡下单信息:{}",JsonMapper.nonEmptyMapper().toJson(persity));
		//保存订单
		tradeOrderService.insertTradeOrder(persity);
		
		if(vo.getActiviType() == ActivityTypeEnum.VONCHER){
			//更新优惠券信息
			scanOrderService.updateActivityCoupons(vo.getOrderId(), vo.getRecordId(), vo.getCouponsId(), vo.getDeviceId());
		}
		// 保存零花钱记录
		if (vo.getPinMoneyAmount() != null && vo.getPinMoneyAmount().compareTo(BigDecimal.ZERO) > 0) {
			//保存零花钱记录
			tradePinMoneyUseService.orderOccupy(vo.getUserId(), vo.getOrderId(), persity.getTotalAmount(),
					vo.getPinMoneyAmount());
		}
		resp.setResult(ResultCodeEnum.SUCCESS);
		
		//支付0元直接改为支付完成
		if(vo.getPaymentAmount().compareTo(BigDecimal.ZERO) == 0){
			tradeOrderPayService.wlletPay(BigDecimal.ZERO.toString(), persity);
		}
	}
	

	/**
	 * @Description: 组装查询优惠券条件
	 * @param vo
	 * @return FavourParamBO  
	 * @author tuzhd
	 * @date 2017年8月8日
	 */
	private PlaceOrderParamDto createPlaceOrderParamDto(MemberTradeOrderDto vo){
		PlaceOrderParamDto paramDto =new PlaceOrderParamDto();
		paramDto.setRecordId(vo.getRecordId());
		paramDto.setUserId(vo.getUserId());
		paramDto.setStoreId(vo.getBranchId());
		paramDto.setActivityId(vo.getCouponsActivityId());
		paramDto.setActivityItemId(vo.getCouponsId());
		paramDto.setDeviceId(vo.getDeviceId());
		paramDto.setChannel(String.valueOf(vo.getOrderResource().ordinal()));
		paramDto.setActivityType(String.valueOf(vo.getActiviType().ordinal()));
		PlaceOrderItemDto item = new PlaceOrderItemDto();
		//设置可优惠金额 用于代金券校验，设置可以优惠金额到订单项中，由于优惠校验
		item.setTotalAmount(vo.getCanDiscountAmount());
		item.setSkuPrice(vo.getCanDiscountAmount());
		item.setQuantity(1);
		item.setSkuActType(ActivityTypeEnum.NO_ACTIVITY.ordinal());
		item.setSkuActType(0);
		List<PlaceOrderItemDto> list= Lists.newArrayList();
		list.add(item);
		paramDto.setSkuList(list);
		return paramDto;
	}
    	
	/**
	 * @Description: 组装查询优惠券条件
	 * @param vo
	 * @return FavourParamBO  
	 * @author tuzhd
	 * @date 2017年8月8日
	 */
	private FavourParamBO createFavourParamBo(MemberTradeOrderDto vo){
		FavourParamBO parambo =new FavourParamBO();
		parambo.setUserId(vo.getUserId());
		parambo.setChannel(OrderResourceEnum.MEMCARD);
		parambo.setClientType(UseClientType.ONlY_APP_USE);
		parambo.setStoreType(StoreTypeEnum.CLOUD_STORE);
		parambo.setOrderType(OrderTypeEnum.PHYSICAL_ORDER);
		parambo.setStoreId(vo.getBranchId());
		List<String> goods = Lists.newArrayList();
		vo.getList().forEach(e -> goods.add(e.getGoodsSkuId()));
		parambo.setSkuIdList(goods);
		parambo.setDeviceId(vo.getDeviceId());
		//设置可优惠金额
		parambo.setTotalAmount(vo.getCanDiscountAmount());
		return parambo;
	}
	/**
	 * @Description: 检查用户会员卡信息
	 * @param vo
	 * @return boolean  
	 * @author tuzhd
	 * @date 2017年8月8日
	 */
	private boolean  checkUseInfo(MemberTradeOrderDto vo){
		boolean result = false;
		//获取会员卡信息
		String memberPayNum =  vo.getMemberPayNum();
		if(StringUtils.isNotBlank(memberPayNum)){
			//获取优惠信息 
			String isHadDiscount = memberPayNum.substring(memberPayNum.length()-1, memberPayNum.length());
			memberPayNum = memberPayNum.substring(0, memberPayNum.length()-1);
			
			//设置是否使用优惠
			vo.setUserDiscount("1".equals(isHadDiscount));
			
			//获取会员卡信息获取用户信息
			String userInfo = (String) redisTemplateWrapper.get(memberPayNum);
			
			//存在会员卡信息获取用户信息
			if(StringUtils.isNotBlank(userInfo)){
				String[] userArr= userInfo.split(":");
				vo.setUserId(userArr[0]);
				vo.setDeviceId(userArr.length > 2 ? userArr[2] : null);
				result = true;
			}
		}
		return result;
	}
	
	
	 /**
	  * @Description: 获取会员卡信息接口
	  * @param userId  用户id
	  * @param deviceId 设备id
	  * @author tuzhd
	  * @date 2017年8月9日
	  */
    public String getMemberPayNumber(String userId,String deviceId){
		//会员卡缓存标识key 用户id+设备号
		String key = userId + ":cardUser:" + deviceId;
		//生成随机会员卡信息36开头+12位随机数
		String card = createMemberCard(12, "36");
		
		//获取旧的会员卡信息
		String oldCard = (String) redisTemplateWrapper.get(key);
		//存在旧的card信息进行剔除
		if(StringUtils.isNotBlank(oldCard)){
			redisTemplateWrapper.del(oldCard);
		}
		
		//设置timeOutMinutes分钟过期  会员卡标识及 用户id+设备号 相互为key 进行存储
		redisTemplateWrapper.set(key,card, timeOutMinutes, TimeUnit.MINUTES);
		redisTemplateWrapper.set(card,key, timeOutMinutes, TimeUnit.MINUTES);
		
		return card;    	
    }
    
   /**
     * @Description: 获取会员卡信息接口
     * @param memberPayNum  会员卡信息
	 * @author tuzhd
	 * @date 2017年8月9日
	 */
   public String getUserIdByMemberCard(String memberPayNum){
	   	if(StringUtils.isBlank(memberPayNum)){
		   return null;
	   	}
		//获取会员卡信息获取用户信息
		String userInfo = (String) redisTemplateWrapper.get(memberPayNum.substring(0, memberPayNum.length()-1));
		//存在会员卡信息获取用户信息
		if(StringUtils.isBlank(userInfo)){
			return null;
		}
		return userInfo.split(":")[0];
   }
    
	/**
    * @Description: 根据订单id取消订单,已提交订单不能清除，会导致用户支付无法对上账
    * @param orderId 订单id
    * @paran userId 用户id
    * @param isAppUser 是否是app用户   
    * @return void  
    * @author tuzhd
    * @date 2017年8月10日
    */
   public boolean cancelMemberCardOrder(String orderId,String userId,boolean isAppUser){
    	boolean result = false;
    	if(StringUtils.isNotBlank(orderId)){
    		//定单缓存key
    		String key = orderId + orderKeyStr;
    		//零售取消失败返回false,不管pos是否失败，避免app失败卡死在页面中 app用户不传id
    		hykPayOrderServiceApi.cancelOrder(orderId,(isAppUser ? null : userId));
	    	//清除商城这边订单redis记录
	    	MemberTradeOrderDto order = (MemberTradeOrderDto) redisTemplateWrapper.get(key);
	    	//订单不存在标识清楚成功返回
	    	if(order == null){
	    		return true;
	    	}
	    	//删除会员卡信息
	    	removetMemberPayNumber(order.getMemberPayNum());
	    	//清除订单信息
			redisTemplateWrapper.del(key);
			result =  true;
    	}
    	return result;
    }
    
    /**
	  * @Description: 移除会员卡信息接口  
	  * @param userId  用户id
	  * @param deviceId 设备id
	  * @author tuzhd
	  * @date 2017年8月9日
	  */
   public void removetMemberPayNumber(String memberPayNum){
	   if(StringUtils.isNotBlank(memberPayNum)){
		   //去除优惠位
		   String newNum = memberPayNum.substring(0, memberPayNum.length()-1);
		   //获取会员卡对应用户信息
		   String userInfo = (String) redisTemplateWrapper.get(newNum);
		   //剔除会员卡缓存信息
		   redisTemplateWrapper.del(newNum);
		   //移除会员卡与订单信息 不带优惠位
		   redisTemplateWrapper.del(newNum + cardKeyStr);
		   //会员卡标识及 用户id+设备号 相互为key 进行存储
		   if(StringUtils.isNotBlank(userInfo)){
			   redisTemplateWrapper.del(userInfo);
		   }
	   }
   }
    
    /**
     * @Description: 生成随机会员卡信息
     * @return String  
     * @author tuzhd
     * @date 2017年8月7日
     */
    private String createMemberCard(int length,String start){
    	StringBuilder cards = new StringBuilder(start);
    	//循环长度进行随机生成
    	for(int i= 0; i < length ; i++){
    		cards.append(new Random().nextInt(10));
    	}
    	String newCard = cards.toString();
    	//如果存在会员卡信息则重新生成
    	if(redisTemplateWrapper.get(newCard) != null){
    		return createMemberCard(length, start);
    	}
    	return newCard;
    }

}
