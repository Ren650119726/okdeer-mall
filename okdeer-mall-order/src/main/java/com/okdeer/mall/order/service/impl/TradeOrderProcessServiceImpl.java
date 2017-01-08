package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.archive.system.entity.SysDict;
import com.okdeer.archive.system.service.ISysDictServiceApi;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.enums.CouponsType;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.common.utils.HttpClientUtil;
import com.okdeer.mall.common.utils.TradeNumUtil;
import com.okdeer.mall.common.utils.Xml2JsonUtil;
import com.okdeer.mall.order.constant.text.OrderTipMsgConstant;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderThirdRelation;
import com.okdeer.mall.order.enums.AppraiseEnum;
import com.okdeer.mall.order.enums.CompainStatusEnum;
import com.okdeer.mall.order.enums.OrderComplete;
import com.okdeer.mall.order.enums.OrderIsShowEnum;
import com.okdeer.mall.order.enums.OrderItemStatusEnum;
import com.okdeer.mall.order.enums.OrderOptTypeEnum;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.PickUpTypeEnum;
import com.okdeer.mall.order.enums.WithInvoiceEnum;
import com.okdeer.mall.order.handler.FavourCheckService;
import com.okdeer.mall.order.handler.FavourSearchService;
import com.okdeer.mall.order.handler.GoodsCheckService;
import com.okdeer.mall.order.handler.ProcessHandler;
import com.okdeer.mall.order.handler.ProcessHandlerChain;
import com.okdeer.mall.order.handler.StockCheckService;
import com.okdeer.mall.order.handler.StoreCheckService;
import com.okdeer.mall.order.handler.StoreExtProcessService;
import com.okdeer.mall.order.handler.TradeOrderAddService;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.mapper.TradeOrderThirdRelationMapper;
import com.okdeer.mall.order.service.GenerateNumericalService;
import com.okdeer.mall.order.service.TradeOrderProcessService;
import com.okdeer.mall.order.service.TradeOrderProcessServiceApi;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.timer.TradeOrderTimer;
import com.okdeer.mall.order.utils.CodeStatistical;
import com.okdeer.mall.order.vo.RechargeCouponVo;
import com.okdeer.mall.order.vo.RechargeOrderReqDto;
import com.okdeer.mall.order.vo.TradeOrderReq;
import com.okdeer.mall.order.vo.TradeOrderReqDto;
import com.okdeer.mall.order.vo.TradeOrderResp;
import com.okdeer.mall.order.vo.TradeOrderRespDto;
import com.okdeer.mall.system.mapper.SysBuyerUserMapper;

import net.sf.json.JSONObject;

/**
 * ClassName: TradeOrderProcessServiceImpl 
 * @Description: 订单处理Service
 * @author maojj
 * @date 2016年7月22日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1          2016年7月16日                               maojj			订单处理Service
 *		重构4.1          2016年7月16日                               maojj			订单结算时增加优惠活动查询处理流程
 *		Bug:12572	    2016-08-10		 	 maojj			添加结算校验失败的提示语
 *		Bug:12808		2016-08-16		 	 maojj			结算失败时返回店铺的相关信息
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderProcessServiceApi")
public class TradeOrderProcessServiceImpl implements TradeOrderProcessService, TradeOrderProcessServiceApi {

	private static final Logger logger = LoggerFactory.getLogger(TradeOrderProcessServiceImpl.class);
    
	/*************欧飞网充值配置*********************/
    /**
     * ofpay.userid
     */
    @Value("${ofpay.userid}")
    private String userid;
    /**
     * ofpay.userpws
     */
    @Value("${ofpay.userpws}")
    private String userpws;
    /**
     * ofpay.version
     */
    @Value("${ofpay.version}")
    private String version;
    /**
     * ofpay.dataplan.range
     */
    @Value("${ofpay.dataplan.range}")
    private String range;
    /**
     * ofpay.dataplan.effectStartTime
     */
    @Value("${ofpay.dataplan.effectStartTime}")
    private String effectStartTime;
    /**
     * ofpay.dataplan.effectTime
     */
    @Value("${ofpay.dataplan.effectTime}")
    private String effectTime;
    /**
     * 中国移动折扣
     */
    @Value("${ofpay.cmcc.discount}")
    private String cmccDiscount;
    /**
     * 中国联通折扣
     */
    @Value("${ofpay.cucc.discount}")
    private String cuccDiscount;
    /**
     * 中国电信折扣
     */
    @Value("${ofpay.ctcc.discount}")
    private String ctccDiscount;
    
	/**
	 * 店铺校验Service
	 */
	@Resource
	private StoreCheckService storeCheckService;

	/**
	 * 商品校验service
	 */
	@Resource
	private GoodsCheckService goodsCheckService;

	/**
	 * 店铺校验service
	 */
	@Resource
	private StockCheckService stockCheckService;

	/**
	 * 优惠校验service
	 */
	@Resource
	private FavourCheckService favourCheckService;

	/**
	 * 用户有效优惠活动查询Service
	 */
	@Resource
	private FavourSearchService favourSearchService;

	/**
	 * 生成订单Service
	 */
	@Resource
	private TradeOrderAddService tradeOrderAddService;

	/**
	 * 店铺运费和起订金额处理service
	 */
	@Resource
	private StoreExtProcessService storeExtProcessService;

	/**
	 * 生成编号的service
	 */
	@Resource
	private GenerateNumericalService generateNumericalService;

	/**
	 * 买家用户Mapper
	 */
	@Resource
	private SysBuyerUserMapper sysBuyerUserMapper;

	/**
	 * 交易订单service
	 */
	@Resource
	private TradeOrderService tradeOrderSerive;
	
	/**
	 * 订单和第三方平台订单 
	 */
	@Resource
	private TradeOrderThirdRelationMapper tradeOrderThirdRelationMapper;
	
	@Autowired
	private TradeOrderMapper tradeOrderMapper;
	
	@Autowired
	private TradeOrderItemMapper tradeOrderItemMapper;
	
	@Autowired
	private ActivityCouponsRecordMapper couponsRecordMapper;
	/**
     * 数据字典service
     */
    @Reference(version="1.0.0", check=false)
    private ISysDictServiceApi sysDictService;
	
    /**
     * 订单超时计时器
     */
    @Resource
    private TradeOrderTimer tradeOrderTimer;
    /**
     * 代金券Mapper
     */
    @Resource
    private ActivityCouponsMapper activityCouponsMapper;
    /**
     * 代金券记录Mapper
     */
    @Resource
    private ActivityCouponsRecordMapper activityCouponsRecordMapper;
    
	/**
	 * 结算操作时的数据校验
	 */
	@Override
	public TradeOrderRespDto validateStoreSkuStock(TradeOrderReqDto reqDto) throws Exception {
		// Begin added by maojj 2016-08-10 Bug:12572
		reqDto.setOrderOptType(OrderOptTypeEnum.ORDER_SETTLEMENT);
		// End added by maojj 2016-08-190
		// 交易订单响应
		TradeOrderRespDto respDto = new TradeOrderRespDto();
		// 流程处理链
		List<ProcessHandler> handlerChain = new ArrayList<ProcessHandler>();
		// 第一步：校验店铺是否可以接单
		handlerChain.add(storeCheckService);
		// 第二步：校验商品信息是否发生变化
		handlerChain.add(goodsCheckService);
		// 第三步：校验商品库存是否足够
		handlerChain.add(stockCheckService);
		// 第四步：查询用户有效的优惠信息
		handlerChain.add(favourSearchService);
		// 创建处理链对象
		ProcessHandlerChain chain = ProcessHandlerChain.getInstance(handlerChain);
		// 处理上述四步流程，任何一个流程返回false，则流程中断
		chain.process(reqDto, respDto);
		// 第五步：返回店铺最新信息
		storeExtProcessService.process(reqDto, respDto);
		return respDto;
	}

	/**
	 * 提交订单处理流程
	 */
	@Override
	public TradeOrderRespDto addTradeOrder(String requestStr) throws Exception {
		// 交易订单请求
		TradeOrderReqDto reqDto = JsonMapper.nonDefaultMapper().fromJson(requestStr, TradeOrderReqDto.class);
		// Begin added by maojj 2016-08-10 Bug:12572
		reqDto.setOrderOptType(OrderOptTypeEnum.ORDER_SUBMIT);
		// End added by maojj 2016-08-190
		// 交易订单响应
		TradeOrderRespDto respDto = new TradeOrderRespDto();
		// 流程处理链
		List<ProcessHandler> handlerChain = new ArrayList<ProcessHandler>();
		// 第一步：校验店铺是否可以接单
		handlerChain.add(storeCheckService);
		// 第二步：校验商品信息是否发生变化
		handlerChain.add(goodsCheckService);
		// 第三步：校验商品库存是否足够
		handlerChain.add(stockCheckService);
		// 第四步：校验用户选择的优惠是否有效
		handlerChain.add(favourCheckService);
		// 第五步：订单新增处理
		handlerChain.add(tradeOrderAddService);
		// 创建处理链对象
		ProcessHandlerChain chain = ProcessHandlerChain.getInstance(handlerChain);
		// 处理上述五步流程，任何一个流程返回false，则流程中断
		chain.process(reqDto, respDto);
		return respDto;
	}
	
	/**
     * @Description: 生成手机充值订单
     * @param reqJsonStr 请求json串
     * @return 下单结果
     * @throws Exception 异常   
     * @author zhaoqc
     * @date 2016年7月21日
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TradeOrderRespDto addRechargeTradeOrderV100(String reqJsonStr) throws Exception {
        TradeOrderRespDto respDto = new TradeOrderRespDto();
        boolean flag = false;
        RechargeOrderReqDto reqDto = JsonMapper.nonDefaultMapper().fromJson(reqJsonStr, RechargeOrderReqDto.class);
        //创建订单
        TradeOrder tradeOrder = new TradeOrder();
        TradeOrderItem tradeOrderItem = new TradeOrderItem();
        String orderId = UuidUtils.getUuid();
        tradeOrder.setId(orderId);
        tradeOrder.setStatus(OrderStatusEnum.UNPAID);
        setOrderNo(tradeOrder);
        
        BigDecimal totalAmount = reqDto.getTotalAmount();
        OrderTypeEnum orderType = OrderTypeEnum.enumValueOf(reqDto.getRechargeType());
        String rechargeMobile = reqDto.getRechargeMobile();
        String inprice = null;
        //充值订单金额第三方平台查询
        if (OrderTypeEnum.PHONE_PAY_ORDER.equals(orderType)) {
            String url = "http://" + userid +".api2.ofpay.com/telquery.do?userid=" + userid + "&userpws=" + userpws
                    + "&phoneno=" + rechargeMobile + "&version=" + version + "&pervalue=" + totalAmount.intValue();
            //请求欧飞平台查询商品价格
            String xml = HttpClientUtil.get(url, "GB2312");
            JSONObject respJson = Xml2JsonUtil.xml2Json(xml, "GB2312"); 
            JSONObject cardInfo = respJson.getJSONObject("cardinfo");
            int retCode = cardInfo.getInt("retcode");
            if(retCode == 1) {
                inprice = String.format("%.2f", cardInfo.getDouble("inprice"));
                logger.info("订单生成===订单Id：{}，订单号：{}，向欧飞平台查询话费充值面值结果{}", orderId, tradeOrder.getOrderNo(), inprice);            
                //将从第三方平台上查到的商品信息赋值给订单项
                tradeOrderItem.setSkuName(cardInfo.getString("cardname"));
                //记录充值话费的面额
                tradeOrderItem.setStoreSkuId(String.valueOf(totalAmount.intValue()));
                //将cardid赋值给cardid
                tradeOrderItem.setStoreSpuId(cardInfo.getString("cardid"));
                flag = true;
            } 
        } else if(OrderTypeEnum.TRAFFIC_PAY_ORDER.equals(orderType)) {
            String url = "http://" + userid + ".api2.ofpay.com/mobinfo.do?mobilenum=" + rechargeMobile.substring(0, 7);
            String resp = HttpClientUtil.get(url, "GB2312");
            String discount = null;
            if(!StringUtils.isNullOrEmpty(resp)) {
                logger.info("流量充值号码{}归属地运营商查询返回：{}", rechargeMobile, resp);
                String respArr[] = resp.split("\\|");
                try {
                    if ("移动".equals(respArr[2])) {
                        discount = cmccDiscount;
                    } else if ("联通".equals(respArr[2])) {
                        discount = cuccDiscount;
                    } else if ("电信".equals(respArr[2])) {
                        discount = ctccDiscount;
                    }
                    
                    String pid = reqDto.getPid();
                    String arr[] = pid.split("\\|");
                    String perValue = arr[0];
                    String flowValue = arr[1];
                    url = "http://" + userid + ".api2.ofpay.com/flowCheck.do?userid=" + userid + "&userpws=" + userpws + "&phoneno=" + rechargeMobile
                             + "&range=" + range + "&effectStartTime=" + effectStartTime + "&effectTime=" + effectTime + "&version=" + version 
                             + "&perValue=" + perValue + "&flowValue=" + flowValue;
                    String xml = HttpClientUtil.get(url, "GB2312");
                    JSONObject respJson = Xml2JsonUtil.xml2Json(xml, "GB2312"); 
                    JSONObject queryinfo = respJson.getJSONObject("queryinfo");
                    int retCode = queryinfo.getInt("retcode");
                    if(retCode == 1) {
                        inprice = stringMultiply(perValue, discount);
                        tradeOrderItem.setStoreSkuId(perValue + "|" + flowValue);
                        tradeOrderItem.setSkuName(queryinfo.getString("productname"));
                        flag = true;
                    } 
                } catch (Exception e) {
                    logger.error("订单生成===订单:{}，订单号{}，手机号{}获取欧飞平台商品信息出现异常{}！",orderId, tradeOrder.getOrderNo(), rechargeMobile, e);
                }
            } 
        }
        
        //检查商品信息是否发生变化
        if(!flag) {
            respDto.setFlag(false);
            respDto.setMessage(OrderTipMsgConstant.GOODS_IS_CHANGE);
            return respDto;
        }
        
        BigDecimal actualAmount = new BigDecimal(inprice);
        
        tradeOrder.setTotalAmount(totalAmount);
        tradeOrder.setPreferentialPrice(BigDecimal.ZERO);
        tradeOrder.setActualAmount(actualAmount);
        tradeOrderItem.setUnitPrice(totalAmount);
        tradeOrderItem.setTotalAmount(totalAmount);
        tradeOrderItem.setActualAmount(actualAmount);
        tradeOrderItem.setPreferentialPrice(BigDecimal.ZERO);
        
        tradeOrder.setFare(new BigDecimal(0.00));
        tradeOrder.setUserId(reqDto.getUserId());
        setUserPhone(tradeOrder, reqDto.getUserId());
        tradeOrder.setStoreName("友门鹿欧飞手机充值店");
        tradeOrder.setSellerId("0");
        tradeOrder.setStoreId("0");
        tradeOrder.setPid("0");
        tradeOrder.setType(orderType);
        tradeOrder.setPickUpType(PickUpTypeEnum.DELIVERY_DOOR);
        tradeOrder.setPickUpCode("");
        tradeOrder.setActivityType(ActivityTypeEnum.NO_ACTIVITY);
        tradeOrder.setActivityId(reqDto.getActivityId());
        tradeOrder.setInvoice(WithInvoiceEnum.NONE);
        tradeOrder.setDisabled(Disabled.valid);
        tradeOrder.setCreateTime(new Date());
        tradeOrder.setUpdateTime(new Date());
        tradeOrder.setOrderResource(OrderResourceEnum.YSCAPP);
        tradeOrder.setPayWay(PayWayEnum.PAY_ONLINE);
        tradeOrder.setIsComplete(OrderComplete.NO);
        tradeOrder.setTradeNum(TradeNumUtil.getTradeNum());
        tradeOrder.setActivityItemId(reqDto.getCouponId());
        tradeOrder.setIsShow(OrderIsShowEnum.yes);
        tradeOrderMapper.insertSelective(tradeOrder);
        
        // 创建订单项
        tradeOrderItem.setId(UuidUtils.getUuid());
        tradeOrderItem.setOrderId(orderId);
        tradeOrderItem.setQuantity(1);
        tradeOrderItem.setSpuType(SpuTypeEnum.serviceSpu);
        tradeOrderItem.setStatus(OrderItemStatusEnum.NO_REFUND);
        tradeOrderItem.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);
        tradeOrderItem.setAppraise(AppraiseEnum.NOT_APPROPRIATE);
        tradeOrderItem.setCreateTime(new Date());
        tradeOrderItem.setServiceAssurance(0);
        tradeOrderItem.setRechargeMobile(rechargeMobile);
        tradeOrderItemMapper.insertSelective(tradeOrderItem);
        
        TradeOrderResp resp = respDto.getResp();
        resp.setOrderId(orderId);
        resp.setOrderNo(tradeOrder.getOrderNo());
        resp.setOrderPrice(tradeOrder.getActualAmount());
        resp.setTradeNum(tradeOrder.getTradeNum());
        resp.setOrderType(orderType.ordinal());
        respDto.setResp(resp);
        respDto.setFlag(true);
        respDto.setMessage(OrderTipMsgConstant.ORDER_SUCESS);
        return respDto; 
    }

	/**
	 * @Description: 生成手机充值订单
	 * @param reqJsonStr 请求json串
	 * @return 下单结果
	 * @throws Exception 异常   
	 * @author zhaoqc
	 * @date 2016年7月21日
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public TradeOrderRespDto addRechargeTradeOrderV110(String reqJsonStr) throws Exception {
		TradeOrderRespDto respDto = new TradeOrderRespDto();
        boolean flag = false;
        RechargeOrderReqDto reqDto = JsonMapper.nonDefaultMapper().fromJson(reqJsonStr, RechargeOrderReqDto.class);
        //创建订单
        TradeOrder tradeOrder = new TradeOrder();
        TradeOrderItem tradeOrderItem = new TradeOrderItem();
        String orderId = UuidUtils.getUuid();
        tradeOrder.setId(orderId);
        tradeOrder.setStatus(OrderStatusEnum.UNPAID);
        setOrderNo(tradeOrder);
        
        //查询话费或者流量套餐的字典配置
        String packageId = reqDto.getPackageId();
        SysDict sysDict = this.sysDictService.loadById(packageId);
        String[] sysTypes = {"phoneFeePackage", "cmccdataplan", "cuccdataplan", "ctccdataplan"};
        List<String> sysTypeList = Arrays.asList(sysTypes);
        if(sysDict == null || !sysTypeList.contains(sysDict.getType())) {
            respDto.setFlag(false);
            respDto.setMessage(OrderTipMsgConstant.GOODS_IS_CHANGE);
            return respDto;
        }
        
        OrderTypeEnum orderType = OrderTypeEnum.enumValueOf(reqDto.getRechargeType());
        String rechargeMobile = reqDto.getRechargeMobile();
        String inprice = null;
        //充值订单金额第三方平台查询
        if (OrderTypeEnum.PHONE_PAY_ORDER.equals(orderType)) {
            String url = "http://" + userid +".api2.ofpay.com/telquery.do?userid=" + userid + "&userpws=" + userpws
                    + "&phoneno=" + rechargeMobile + "&version=" + version + "&pervalue=" + sysDict.getValue();
            //请求欧飞平台查询商品价格
            String xml = HttpClientUtil.get(url, "GB2312");
            JSONObject respJson = Xml2JsonUtil.xml2Json(xml, "GB2312"); 
            JSONObject cardInfo = respJson.getJSONObject("cardinfo");
            int retCode = cardInfo.getInt("retcode");
            if(retCode == 1) {
                inprice = String.format("%.2f", cardInfo.getDouble("inprice"));
                logger.info("订单生成===订单Id：{}，订单号：{}，向欧飞平台查询话费充值面值结果{}", orderId, tradeOrder.getOrderNo(), inprice);            
                //将从第三方平台上查到的商品信息赋值给订单项
                tradeOrderItem.setSkuName(cardInfo.getString("cardname"));
                //记录充值话费的面额
                tradeOrderItem.setStoreSkuId(sysDict.getValue());
                //将cardid赋值给cardid
                tradeOrderItem.setStoreSpuId(cardInfo.getString("cardid"));
                flag = true;
            } 
        } else if(OrderTypeEnum.TRAFFIC_PAY_ORDER.equals(orderType)) {
            String url = "http://" + userid + ".api2.ofpay.com/mobinfo.do?mobilenum=" + rechargeMobile.substring(0, 7);
            String resp = HttpClientUtil.get(url, "GB2312");
            String discount = null;
            if(!StringUtils.isNullOrEmpty(resp)) {
                logger.info("流量充值号码{}归属地运营商查询返回：{}", rechargeMobile, resp);
                String respArr[] = resp.split("\\|");
                try {
                    if ("移动".equals(respArr[2])) {
                        discount = cmccDiscount;
                    } else if ("联通".equals(respArr[2])) {
                        discount = cuccDiscount;
                    } else if ("电信".equals(respArr[2])) {
                        discount = ctccDiscount;
                    }
                    
                    String perValue = sysDict.getValue();
                    String flowValue = sysDict.getLabel();
                    url = "http://" + userid + ".api2.ofpay.com/flowCheck.do?userid=" + userid + "&userpws=" + userpws + "&phoneno=" + rechargeMobile
                             + "&range=" + range + "&effectStartTime=" + effectStartTime + "&effectTime=" + effectTime + "&version=" + version 
                             + "&perValue=" + perValue + "&flowValue=" + flowValue;
                    String xml = HttpClientUtil.get(url, "GB2312");
                    JSONObject respJson = Xml2JsonUtil.xml2Json(xml, "GB2312"); 
                    JSONObject queryinfo = respJson.getJSONObject("queryinfo");
                    int retCode = queryinfo.getInt("retcode");
                    if(retCode == 1) {
                        inprice = stringMultiply(perValue, discount);
                        tradeOrderItem.setStoreSkuId(perValue + "|" + flowValue);
                        tradeOrderItem.setSkuName(queryinfo.getString("productname"));
                        flag = true;
                    } 
                } catch (Exception e) {
                    logger.error("订单生成===订单:{}，订单号{}，手机号{}获取欧飞平台商品信息出现异常{}！",orderId, tradeOrder.getOrderNo(), rechargeMobile, e);
                }
            } 
        }
        
        //检查商品信息是否发生变化
        if(!flag) {
            respDto.setFlag(false);
            respDto.setMessage(OrderTipMsgConstant.GOODS_IS_CHANGE);
            return respDto;
        }
        
        BigDecimal totalAmount = new BigDecimal(inprice);
        //校验优惠信息是否可用
        ActivityTypeEnum activityType = reqDto.getActivityType();
        RechargeCouponVo couponVo = null;
        if(activityType != ActivityTypeEnum.NO_ACTIVITY) {
            couponVo = validateCoupon(reqDto, respDto, totalAmount);
            if(!respDto.isFlag()) {
                return respDto;
            }
        }
        
        BigDecimal preferentialPrice = new BigDecimal("0.00");
        if(couponVo != null) {
            preferentialPrice = new BigDecimal(couponVo.getCouponPrice());
            
            //更新优惠券使用信息
            ActivityCouponsRecord couponRecord = this.couponsRecordMapper.selectByPrimaryKey(reqDto.getRecordId());
            couponRecord.setOrderId(orderId);
            couponRecord.setStatus(ActivityCouponsRecordStatusEnum.USED);
            // 两次更新导致使用代金券充值失败
//            this.couponsRecordMapper.updateByPrimaryKeySelective(couponRecord);
            
            //增加优惠券已使用数量
            TradeOrderReq req = new TradeOrderReq();
            req.setActivityType(activityType.ordinal());
            req.setCouponsType(CouponsType.hfcz.ordinal());
            req.setRecordId(couponRecord.getId());
            req.setActivityItemId(reqDto.getCouponId());
            
            updateActivityCoupons(tradeOrder, req);
        }
        
        tradeOrder.setTotalAmount(totalAmount);
        tradeOrder.setPreferentialPrice(preferentialPrice);
        BigDecimal actualAmount = totalAmount.subtract(preferentialPrice);
        if(actualAmount.compareTo(BigDecimal.ZERO) < 1) {
            //优惠金额大于订单金额，生成订单失败
            respDto.setFlag(false);
            respDto.setMessage(OrderTipMsgConstant.RECHARGE_COUPON_VALUE_TOOMUCH);
            return respDto;
        }
        
        tradeOrder.setActualAmount(actualAmount);
        
        tradeOrderItem.setUnitPrice(totalAmount);
        tradeOrderItem.setTotalAmount(totalAmount);
        tradeOrderItem.setActualAmount(actualAmount);
        tradeOrderItem.setPreferentialPrice(preferentialPrice);
        
        tradeOrder.setFare(new BigDecimal(0.00));
        tradeOrder.setUserId(reqDto.getUserId());
        setUserPhone(tradeOrder, reqDto.getUserId());
        tradeOrder.setStoreName("友门鹿欧飞手机充值店");
        tradeOrder.setSellerId("0");
        tradeOrder.setStoreId("0");
        tradeOrder.setPid("0");
        tradeOrder.setType(orderType);
        tradeOrder.setPickUpType(PickUpTypeEnum.DELIVERY_DOOR);
        tradeOrder.setPickUpCode("");
        tradeOrder.setActivityType(activityType);
        tradeOrder.setActivityId(reqDto.getActivityId());
        tradeOrder.setInvoice(WithInvoiceEnum.NONE);
        tradeOrder.setDisabled(Disabled.valid);
        tradeOrder.setCreateTime(new Date());
        tradeOrder.setUpdateTime(new Date());
        tradeOrder.setOrderResource(OrderResourceEnum.YSCAPP);
        tradeOrder.setPayWay(PayWayEnum.PAY_ONLINE);
        tradeOrder.setIsComplete(OrderComplete.NO);
        tradeOrder.setTradeNum(TradeNumUtil.getTradeNum());
        tradeOrder.setActivityItemId(reqDto.getCouponId());
        tradeOrder.setIsShow(OrderIsShowEnum.yes);
        tradeOrderMapper.insertSelective(tradeOrder);
        
        // 创建订单项
        tradeOrderItem.setId(UuidUtils.getUuid());
        tradeOrderItem.setOrderId(orderId);
        tradeOrderItem.setQuantity(1);
        tradeOrderItem.setSpuType(SpuTypeEnum.serviceSpu);
        tradeOrderItem.setStatus(OrderItemStatusEnum.NO_REFUND);
        tradeOrderItem.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);
        tradeOrderItem.setAppraise(AppraiseEnum.NOT_APPROPRIATE);
        tradeOrderItem.setCreateTime(new Date());
        tradeOrderItem.setServiceAssurance(0);
        tradeOrderItem.setRechargeMobile(rechargeMobile);
        tradeOrderItemMapper.insertSelective(tradeOrderItem);
        
        //发送定时消息，10分钟未支付取消订单
        this.tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_recharge_pay_timeout, orderId);
        
        TradeOrderResp resp = respDto.getResp();
        resp.setOrderId(orderId);
        resp.setOrderNo(tradeOrder.getOrderNo());
        resp.setOrderPrice(tradeOrder.getActualAmount());
        resp.setTradeNum(tradeOrder.getTradeNum());
        resp.setOrderType(orderType.ordinal());
        respDto.setResp(resp);
        respDto.setFlag(true);
        respDto.setMessage(OrderTipMsgConstant.ORDER_SUCESS);
        return respDto;
	}
	
	/**
     * @Description: 更新代金券
     * @param tradeOrder 交易订单
     * @param req 请求对象
     * @return void  
     * @author maojj
     * @date 2016年7月14日
     */
    private void updateActivityCoupons(TradeOrder tradeOrder, TradeOrderReq req) throws Exception{
        ActivityTypeEnum activityType = req.getActivityType();
        int couponsType = req.getCouponsType();

        if (activityType == ActivityTypeEnum.VONCHER ) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("orderId", tradeOrder.getId());
            params.put("id", req.getRecordId());
            params.put("collectUserId", tradeOrder.getUserPhone());
            params.put("couponsId", req.getActivityItemId());
            params.put("collectType", couponsType);
            // 更新代金券状态
            int updateResult = activityCouponsRecordMapper.updateActivityCouponsStatus(params);
            if(updateResult == 0){
                throw new Exception("代金券已使用或者已过期");
            }
            // 修改代金券使用数量
            activityCouponsMapper.updateActivityCouponsUsedNum(req.getActivityItemId());
        }
    }
	
	/**
	 * 
	 * @param reqDto 订单请求DTO
	 * @param respDto 订单返回DTO
	 * @param orderAmount 订单金额
	 * @return 优惠券VO
	 */
	private RechargeCouponVo validateCoupon(RechargeOrderReqDto reqDto, TradeOrderRespDto respDto, BigDecimal orderAmount) {
	    //活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
	    ActivityTypeEnum activityType = reqDto.getActivityType();
	    String recordId = reqDto.getRecordId();
	    String couponId = reqDto.getCouponId();
	    String userId = reqDto.getUserId();
	    Map<String, String> map = new HashMap<String, String>();
	    map.put("recordId", recordId);
	    map.put("couponId", couponId);
	    map.put("userId", userId);
	    RechargeCouponVo couponVo = this.couponsRecordMapper.findRechargeCouponInfo(map);
	        
	    boolean isValid = false;
	    switch (activityType) {
	        case VONCHER : 
	            isValid = checkRechargeCoupons(couponVo, orderAmount);
	            break;
	        default :
	            break;
	    }
	    
	    if(!isValid) {
	        respDto.setFlag(false);
	        respDto.setMessage("优惠券已过期或者不能使用，提交订单失败。");
	    }
	    return couponVo;
	}
	
	/**
	 * 
	 * @param couponVo 优惠券Vo
	 * @param orderAmount 订单金额
	 * @return 优惠券有效性验证结果
	 */
	private boolean checkRechargeCoupons(RechargeCouponVo couponVo, BigDecimal orderAmount) {
	    boolean isValid = false;
	    
	    if (couponVo != null) {
	       String arrivalStr = couponVo.getArrive();
	       BigDecimal arrival = new BigDecimal(arrivalStr);
	       if (orderAmount.compareTo(arrival) == 1) {
	          if(couponVo.getStatus() == ActivityCouponsRecordStatusEnum.UNUSED) {
	              isValid = true;
	          }
	       }
	    }
	    
	    return isValid;
	}
	
	/**
	 * @Description: 设置充值订单编号
	 * @param tradeOrder 交易订单
	 * @return void  无
	 * @throws ServiceException 自定义异常
	 * @author zhaoqc
	 * @date 2016年7月21日
	 */
	private void setOrderNo(TradeOrder tradeOrder) throws ServiceException {
		String orderNo = generateNumericalService.generateNumberAndSave("CZ");
		if (orderNo == null) {
			throw new ServiceException("充值订单编号生成失败");
		}
		logger.info("生成充值订单编号：{}", orderNo);
		tradeOrder.setOrderNo(orderNo);
	}
	
	private void setUserPhone(TradeOrder tradeOrder, String userId) throws ServiceException {
		SysBuyerUser buyerUser = sysBuyerUserMapper.selectByPrimaryKey(userId);
		if (buyerUser == null) {
			logger.error("根据{}查询买家信息，buyerUser 为空-------->{}", userId, CodeStatistical.getLineInfo());
			throw new ServiceException("查询查询买家信息异常：buyerUser 为空-------->" + CodeStatistical.getLineInfo());
		}
		tradeOrder.setUserPhone(buyerUser.getPhone());
	}

	/**
	 * @Description: 创建充值订单和第三方平台关系
	 * @param reqJsonStr 关系实体
	 * @return 
	 * @throws Exception 异常   
	 * @author zhaoqc
	 * @date 2016年7月21日
	 */
	@Override
	public void addTradeOrderThirdRelation(TradeOrderThirdRelation tradeOrderThirdRelation) {
		tradeOrderThirdRelationMapper.insertSelective(tradeOrderThirdRelation);
	}

   private String stringMultiply(String... args) {
        if(args.length == 0) {
            throw new IllegalArgumentException("错误的传入参数");
        } else if(args.length == 1) {
            double arg = Double.parseDouble(args[0]);
            return String.format("%.2f", arg);
        }
        
        BigDecimal multiplys = new BigDecimal(args[0]);
        for(int i = 1 ; i < args.length ; i++) {
            multiplys = multiplys.multiply(new BigDecimal(args[i]));
        }
            
        return String.format("%.2f", multiplys.doubleValue());
    }
}
