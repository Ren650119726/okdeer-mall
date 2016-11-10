/**   
* @Title: AlipayStatusSubscriber.java 
* @Package com.okdeer.mall.trade.order.pay 
* @Description: (用一句话描述该文件做什么) 
* @author A18ccms A18ccms_gmail_com   
* @date 2016年3月30日 下午7:39:54 
* @version V1.0   
*/
package com.okdeer.mall.order.pay;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.client.producer.TransactionCheckListener;
import com.alibaba.rocketmq.client.producer.TransactionSendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.okdeer.api.pay.pay.dto.PayResponseDto;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuService;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceServiceApi;
import com.okdeer.archive.store.entity.StoreAgentCommunity;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.framework.mq.AbstractRocketMQSubscriber;
import com.okdeer.base.framework.mq.RocketMQTransactionProducer;
import com.okdeer.base.framework.mq.RocketMqResult;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsRegisteRecord;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsType;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.group.entity.ActivityGroup;
import com.okdeer.mall.activity.group.service.ActivityGroupService;
import com.okdeer.mall.common.utils.HttpClientUtil;
import com.okdeer.mall.common.utils.LockUtil;
import com.okdeer.mall.common.utils.RandomStringUtil;
import com.okdeer.mall.common.utils.Xml2JsonUtil;
import com.okdeer.mall.common.utils.security.MD5;
import com.okdeer.mall.order.constant.ExceptionConstant;
import com.okdeer.mall.order.constant.OrderMessageConstant;
import com.okdeer.mall.order.constant.PayMessageConstant;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.enums.ConsumeStatusEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayTypeEnum;
import com.okdeer.mall.order.enums.PickUpTypeEnum;
import com.okdeer.mall.order.mapper.TradeOrderItemDetailMapper;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.mapper.TradeOrderPayMapper;
import com.okdeer.mall.order.service.OrderReturnCouponsService;
import com.okdeer.mall.order.service.TradeOrderItemService;
import com.okdeer.mall.order.service.TradeOrderLogisticsService;
import com.okdeer.mall.order.service.TradeOrderPayService;
import com.okdeer.mall.order.service.TradeOrderRefundsService;
import com.okdeer.mall.order.service.TradeOrderServiceApi;
import com.okdeer.mall.order.timer.TradeOrderTimer;
import com.okdeer.mall.order.utils.JsonDateValueProcessor;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.mcm.entity.SmsVO;
import com.okdeer.mcm.service.ISmsService;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * @ClassName: AlipayStatusSubscriber
 * @Description: 订单状态写入消息,第三方支付
 * @author yangq
 * @date 2016年3月30日 下午7:39:54
 * 
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构4.1          2016年7月16日                              zengj			服务店订单支付回调
 *     12002           2016年8月5日                                zengj			增加服务店订单下单成功增加销量
 *     重构4.1          2016年8月16日                              zengj			支付成功回调判断订单状态是不是买家支付中
 *     重构4.1          2016年8月24日                              maojj			支付成功，如果订单是到店自提，则生成提货码
 *     重构4.1          2016年9月22日                              zhaoqc         从V1.0.0移动代码 
 *     V1.1.0          2016年9月29日                             zhaoqc         新增到店消费订单处理         
 *     V1.1.0			2016-10-15			   wushp				邀请注册首单返券        
 */
@Service
public class ThirdStatusSubscriber extends AbstractRocketMQSubscriber
		implements PayMessageConstant, OrderMessageConstant {

	private static final Logger logger = LoggerFactory.getLogger(ThirdStatusSubscriber.class);

	@Autowired
	public TradeOrderRefundsService tradeOrderRefundsService;

	@Resource
	private TradeOrderPayService tradeOrderPayService;

	@Reference(version = "1.0.0", check = false)
	private TradeOrderServiceApi tradeOrderService;

	@Resource
	private TradeOrderItemService tradeOrderItemService;
	
	@Resource
	private TradeOrderMapper tradeOrderMapper;

	@Resource
	private TradeOrderPayMapper tradeOrderPayMapper;

	@Resource
	private ActivityGroupService activityGroupService;

	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoService;

	@Autowired
	private TradeOrderLogisticsService tradeOrderLogisticsService;

	@Autowired
	private RocketMQTransactionProducer rocketMQTransactionProducer;

	@Autowired
	private TradeOrderTimer tradeOrderTimer;

	/**
     * okdeer.recharge.partner
     */
    @Value("${okdeer.recharge.partner}")
    private String partner;
	/**
	 * 开放平台Id
	 */
	@Value("${juhe.openId}")
	private String openId;
	/**
	 * 话费充值appKey
	 */
	@Value("${juhe.phonefee.appKey}")
	private String appKey;
	/**
	 * 流量充值appKey
	 */
	@Value("${juhe.dataplan.appKey}")
	private String dataPlanKey;
	/**
	 * 话费充值充值url
	 */
	@Value("${phonefee.onlineOrder}")
	private String submitOrderUrl;
	/**
	 * 流量套餐充值url
	 */
	@Value("${dataplan.onlineOrder}")
	private String dataplanOrderUrl;
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
     * ofpay.keyStr
     */
    @Value("${ofpay.keyStr}")
    private String keyStr;
    /**
     * ofpay.returl
     */
    @Value("${ofpay.returl}")
    private String returl;
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
     * 充值成功短信
     */
    @Value("${recharge.success.message}")
    private String successMsg;
    /**
     * 充值失败短信
     */
    @Value("${recharge.failure.message}")
    private String failureMsg;
    /**
     * 短信接口
     */
    @Reference(version = "1.0.0", check = false)
    ISmsService smsService;
    
    @Value("${mcm.sys.code}")
    private String mcmSysCode;

    @Value("${mcm.sys.token}")
    private String mcmSysToken;
    
	@Override
	public String getTopic() {
		return TOPIC_PAY;
	}

	@Override
	public String getTags() {
		return TAG_ORDER;
	}

	@Resource
	private TradeOrderItemMapper tradeOrderItemMapper;

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceServiceApi goodsStoreSkuServiceService;

	@Resource
	private TradeOrderItemDetailMapper tradeOrderItemDetailMapper;
	
	// Begin 12002 add by zengj
	/**
	 * 商品信息Service
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuService;
	// End 12002 add by zengj
	
	// begin add by wushp 20161018
	/**
	 * 订单返券service
	 */
	@Autowired
	private OrderReturnCouponsService orderReturnCouponsService;
	// end add by wushp 20161018
	
	@Override
	public ConsumeConcurrentlyStatus subscribeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		String tradeNum = null;
		try {
			String msg = new String(msgs.get(0).getBody(), Charsets.UTF_8);
			logger.info("订单支付状态消息:" + msg);
			PayResponseDto result = JsonMapper.nonEmptyMapper().fromJson(msg, PayResponseDto.class);
			tradeNum = result.getTradeNum();
			if (StringUtils.isEmpty(tradeNum)) {
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
			TradeOrder orderNum = tradeOrderMapper.selectByParamsTrade(tradeNum);
			// Begin 判断订单状态为不是待买家支付中，就过掉该消息 add by zengj 
			if (orderNum == null || (orderNum.getStatus() != OrderStatusEnum.UNPAID
					&& orderNum.getStatus() != OrderStatusEnum.BUYER_PAYING)) {
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
			// End 判断订单状态为不是待买家支付中，就过掉该消息 add by zengj 
			
			// Begin added by maojj 2016-08-24 付款成功生成提货码
			if (orderNum.getPickUpType() == PickUpTypeEnum.TO_STORE_PICKUP) {
				orderNum.setPickUpCode(RandomStringUtil.getRandomInt(6));
			}
			// Begin added by maojj 2016-08-24 付款成功生成提货码
			
			// Begin 重构4.1 add by zengj
			// 判断是否是服务店订单，如果是服务店订单走单独流程
			if (orderNum.getType() == OrderTypeEnum.SERVICE_STORE_ORDER) {
				inserts(orderNum, result);
				
				Date serviceTime = DateUtils.parseDate(orderNum.getPickUpTime(), "yyyy-MM-dd HH:mm");
				// 预约服务时间过后2小时未派单的自动取消订单
				tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_delivery_server_timeout, orderNum.getId(),
						(DateUtils.addHours(serviceTime, 2).getTime() - DateUtils.getSysDate().getTime()) / 1000);
			} else if(orderNum.getType() == OrderTypeEnum.PHONE_PAY_ORDER) {
				synchronized (LockUtil.getInitialize().synObject(orderNum.getTradeNum())) {
				  //创建订单支付记录和修改订单状态
                    if(orderNum.getStatus() == OrderStatusEnum.DROPSHIPPING || orderNum.getStatus() == OrderStatusEnum.HAS_BEEN_SIGNED) {
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    List<TradeOrderItem> tradeOrderItems = tradeOrderItemService.selectOrderItemByOrderId(orderNum.getId());
                    if(tradeOrderItems.isEmpty()) {
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    
                    //创建支付方式
                    insertTradeOrderPay(orderNum, result);
                    TradeOrderItem tradeOrderItem = tradeOrderItems.get(0);
                    String phoneno = tradeOrderItem.getRechargeMobile();
                    String cardnum = tradeOrderItem.getStoreSkuId();
                    String orderid = orderNum.getTradeNum();
                    
                    int partnerNum = Integer.parseInt(partner);
                    if (partnerNum == 1) {
                        String sign = MD5.md5(openId + appKey + phoneno + cardnum + orderid);
                        String url = submitOrderUrl + "?key=" + appKey + "&phoneno=" + phoneno + "&orderid=" + orderid + "&cardnum=" + cardnum + "&sign=" + sign;
                        String resp = HttpClientUtil.get(url);
                        JSONObject respJson = JSONObject.fromObject(resp);
                        logger.info("************手机话费充值订单{}返回参数************{}", orderid, respJson);
                        int errorCode = respJson.getInt("error_code");
                        if (errorCode == 0) {
                            JSONObject resultJson = respJson.getJSONObject("result");
                            int gameState = Integer.parseInt(resultJson.getString("game_state"));
                            if(gameState == 9) {
                                //充值失败，走退款流程
                                logger.info("PHONEFEE===手机话费充值订单{}请求同步返回状态为失败，创建充值退款单！", orderid);
                                this.tradeOrderRefundsService.insertRechargeRefunds(orderNum);
                            } else if (gameState == 0) {
                                //修改订单状态
                                logger.info("PHONEFEE===手机话费充值订单{}请求返回状态为充值中，修改订单状态为充值中！", orderid);
                                updateTradeOrderStatus(orderNum);
                            }
                        } else {
                            //充值聚合订单提交失败，走退款流程
                            logger.info("PHONEFEE===手机话费充值订单{}请求充值返回错误码为：{}，创建充值退款单！", orderid, errorCode);
                            this.tradeOrderRefundsService.insertRechargeRefunds(orderNum);
                        }
                    } else if (partnerNum == 2) {
                        /**
                         * md5_str检验码的计算方法:包体=userid+userpws+cardid+cardnum+sporder_id+sporder_time+ game_userid
                         *1: 对: “包体+KeyStr” 这个串进行md5 的32位值. 结果大写
                         */
                        String cardid = tradeOrderItem.getStoreSpuId();
                        String ordertime = orderid.substring(0, 14);
                        String sign = MD5.md5(userid + userpws + cardid + cardnum + orderid + ordertime + phoneno + keyStr).toUpperCase();
                        String url = "http://" + userid + ".api2.ofpay.com/onlineorder.do?userid=" + userid + "&userpws=" + userpws 
                                + "&cardid=" + cardid + "&cardnum=" + cardnum + "&sporder_id=" + orderid + "&sporder_time=" + ordertime
                                + "&game_userid=" + phoneno + "&md5_str=" + sign + "&ret_url=" + returl + "&version=" + version;
                        String xml = HttpClientUtil.get(url, "GB2312");
                        JSONObject respJson = Xml2JsonUtil.xml2Json(xml, "GB2312");
                        JSONObject orderinfo = respJson.getJSONObject("orderinfo");
                        logger.info("*******手机话费充值订单{}，返回参数：{}***********", orderid, orderinfo);
                        int retcode = orderinfo.getInt("retcode");
                        String userPhone = orderNum.getUserPhone();
                        if (retcode == 1) {
                            int gameState = orderinfo.getInt("game_state");
                            if (gameState == 0) {
                                //充值请求订单生成成功，支付成功
                                logger.info("PHONEFEE===手机话费充值订单{}请求返回状态为充值中，修改订单状态为充值中！", orderid);
                                updateTradeOrderStatus(orderNum, orderinfo.getString("orderid"));
                            } else if (gameState == 9) {
                                //充值请求订单失败，走退款流程
                                logger.info("PHONEFEE===手机话费充值订单{}请求同步返回状态为失败，创建充值退款单！", orderid);
                                this.tradeOrderRefundsService.insertRechargeRefunds(orderNum);
                            }
                        } else if(retcode == 9999 || retcode == 105 || retcode == 334 || retcode == 1043) {
                            /**
                             * 9999:未知错误
                             * 150:请求失败
                             * 334:订单生成超时
                             * 1043:支付超时，订单处理失败
                             * 充值请求返回这些状态，建议对订单进行手动查询
                             */
                            String queryUrl = "http://" + userid + ".api2.ofpay.com/api/query.do?userid=" + userid + "&spbillid=" + orderid;
                            String stateStr = HttpClientUtil.get(queryUrl);
                            int state = Integer.parseInt(stateStr);
                            if (state == 1) {
                                //充值成功
                                logger.info("PHONEEFEE==手机话费订单{}返回码{}，经过手动查询充值结果为充值成功，修改订单状态为充值成功！", orderid, retcode);
                                orderNum.setStatus(OrderStatusEnum.HAS_BEEN_SIGNED);
                                this.tradeOrderService.updateRechargeOrderByTradeNum(orderNum);
                                
                                //发送提醒短信
                                String content = successMsg;
                                int idx = content.indexOf("#");
                                content = content.replaceFirst(String.valueOf(content.charAt(idx)), userPhone);
                                idx = content.indexOf("#");
                                content = content.replaceFirst(String.valueOf(content.charAt(idx)), tradeOrderItem.getSkuName());
                                
                                SmsVO smsVo = createSmsVo(userPhone, content);
                                this.smsService.sendSms(smsVo);
                            } else if (state == 0) {
                                //充值中
                                logger.info("PHONEEFEE==手机话费订单{}返回码{}，经过手动查询充值结果为充值中，请继续等待。", orderid, retcode);
                            } else if (state == 9) {
                                //失败，走退款流程， 创建退款单
                                logger.info("PHONEFEE===手机话费订单{}返回码{}，经过手动查询充值结果为充值失败，创建话费充值失败退款！", orderid, retcode);
                                this.tradeOrderRefundsService.insertRechargeRefunds(orderNum);
                                
                                //发送提醒短信
                                String content = failureMsg;
                                int idx = content.indexOf("#");
                                content = content.replaceFirst(String.valueOf(content.charAt(idx)), userPhone);
                                idx = content.indexOf("#");
                                content = content.replaceFirst(String.valueOf(content.charAt(idx)), tradeOrderItem.getSkuName());
                                
                                SmsVO smsVo = createSmsVo(userPhone, content);
                                this.smsService.sendSms(smsVo);
                            } else if (state == -1) {
                                //找不到此订单
                                logger.warn("PHONEFEE===手机话费订单{}返回码{}，经过手动查询充值结果为找不到订单，请进入平台查询或者联系第三方欧飞客服进行核实", orderid, retcode);
                            }
                        }else {
                            //欧飞订单提交失败，走退款流程
                            logger.info("PHONEFEE==OFPAY==手机话费充值订单{}请求充值返回码为：{}，创建充值退款单！",orderid, retcode);
                            this.tradeOrderRefundsService.insertRechargeRefunds(orderNum);
                            
                            //发送失败短信
                            String content = failureMsg;
                            int idx = content.indexOf("#");
                            content = content.replaceFirst(String.valueOf(content.charAt(idx)), userPhone);
                            idx = content.indexOf("#");
                            content = content.replaceFirst(String.valueOf(content.charAt(idx)), tradeOrderItem.getSkuName());
                            
                            SmsVO smsVo = createSmsVo(userPhone, content);
                            this.smsService.sendSms(smsVo);
                        }
                    }
				}
			} else if(orderNum.getType() == OrderTypeEnum.TRAFFIC_PAY_ORDER) {
				synchronized (LockUtil.getInitialize().synObject(orderNum.getTradeNum())) {
				    if(orderNum.getStatus() == OrderStatusEnum.DROPSHIPPING || orderNum.getStatus() == OrderStatusEnum.HAS_BEEN_SIGNED) {
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    List<TradeOrderItem> tradeOrderItems = tradeOrderItemService.selectOrderItemByOrderId(orderNum.getId());
                    if(tradeOrderItems.isEmpty()) {
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    
                    //创建支付方式
                    insertTradeOrderPay(orderNum, result);
                    TradeOrderItem tradeOrderItem = tradeOrderItems.get(0);
                    
                    String phoneno = tradeOrderItem.getRechargeMobile();
                    String orderid = orderNum.getTradeNum();
                    String pid = tradeOrderItem.getStoreSkuId();
                    //流量充值
                    int partnerNum = Integer.parseInt(partner);
                    if (partnerNum == 1) {
                        String sign = MD5.md5(openId + dataPlanKey + phoneno + pid + orderid);
                        String url = dataplanOrderUrl + "?key=" + dataPlanKey + "&phone=" + phoneno + "&pid=" + pid + "&orderid=" + orderid + "&sign=" + sign;
                        String resp = HttpClientUtil.get(url);
                        JSONObject respJson = JSONObject.fromObject(resp);
                        logger.info("************手机流量充值订单{}返回参数************{}", orderid, respJson);
                        int errorCode = respJson.getInt("error_code");
                        if(errorCode == 0) {
                            logger.info("手机流量充值订单{}同步返回充值状态为充值中，修改订单状态为充值中！", orderid);
                            //修改订单状态
                            updateTradeOrderStatus(orderNum);
                        } else {
                            //充值聚合订单提交失败，走退款流程
                            logger.info("手机流量充值订单{}同步返回错误码{}，创建流量充值失败订单！", orderid, errorCode);
                            this.tradeOrderRefundsService.insertRechargeRefunds(orderNum);
                        }
                    } else if (partnerNum == 2) {
                        //md5Str检验码的计算方法:
                        //netType 为空的话，不参与md5验证，不为空的话参与MD5验证
                        //包体= userid + userpws + phoneno + perValue + flowValue + range + 
                        //effectStartTime + effectTime + netType+ sporderId
                        String arr[] = pid.split("\\|");
                        String perValue = arr[0];
                        String flowValue = arr[1];
                        String sign = MD5.md5(userid + userpws + phoneno + perValue + flowValue + range + effectStartTime + effectTime + orderid + keyStr).toUpperCase();
                        String url = "http://" + userid + ".api2.ofpay.com/flowOrder.do?userid=" + userid + "&userpws=" + userpws
                                + "&phoneno=" + phoneno + "&perValue=" + perValue + "&flowValue=" + flowValue + "&range=" + range
                                + "&effectStartTime=" + effectStartTime + "&effectTime=" + effectTime + "&sporderId=" + orderid
                                + "&md5Str=" + sign + "&version=" + version + "&retUrl=" + returl; 
                        String xml = HttpClientUtil.get(url, "GB2312");
                        JSONObject respJson = Xml2JsonUtil.xml2Json(xml, "GB2312");
                        JSONObject orderinfo = respJson.getJSONObject("orderinfo");
                        logger.info("***********手机流量充值订单{},返回参数{}***************", orderid, orderinfo);
                        int retcode = orderinfo.getInt("retcode");
                        String userPhone = orderNum.getUserPhone();
                        if (retcode == 1) {
                            int gameState = orderinfo.getInt("game_state");
                            if (gameState == 0) {
                                //充值订单请求成功
                                logger.info("DATAPLAN===手机流量充值订单{}请求返回状态为充值中，修改订单状态为充值中！", orderid);
                                updateTradeOrderStatus(orderNum, orderinfo.getString("orderid"));
                            } else if(gameState == 9) {
                                //充值订单请求失败
                                logger.info("DATAPLAN===手机流量充值订单{}请求同步返回状态为失败，创建充值退款单！", orderid);
                                this.tradeOrderRefundsService.insertRechargeRefunds(orderNum);
                            }
                        }  else if(retcode == 9999 || retcode == 105 || retcode == 334 || retcode == 1043) {
                            /**
                             * 9999:未知错误
                             * 150:请求失败
                             * 334:订单生成超时
                             * 1043:支付超时，订单处理失败
                             * 充值请求返回这些状态，建议对订单进行手动查询
                             */
                            String queryUrl = "http://" + userid + ".api2.ofpay.com/api/query.do?userid=" + userid + "&spbillid=" + orderid;
                            String stateStr = HttpClientUtil.get(queryUrl);
                            int state = Integer.parseInt(stateStr);
                            if (state == 1) {
                                //充值成功
                                logger.info("DATAPLAN==手机流量订单{}返回码{}，经过手动查询充值结果为充值成功，修改订单状态为充值成功！", orderid, retcode);
                                orderNum.setStatus(OrderStatusEnum.HAS_BEEN_SIGNED);
                                this.tradeOrderService.updateRechargeOrderByTradeNum(orderNum);
                                
                                //发送提醒短信
                                String content = successMsg;
                                int idx = content.indexOf("#");
                                content = content.replaceFirst(String.valueOf(content.charAt(idx)), userPhone);
                                idx = content.indexOf("#");
                                content = content.replaceFirst(String.valueOf(content.charAt(idx)), tradeOrderItem.getSkuName());
                                
                                SmsVO smsVo = createSmsVo(userPhone, content);
                                this.smsService.sendSms(smsVo);
                            } else if (state == 0) {
                                //充值中
                                logger.info("DATAPLAN==手机话费订单{}返回码{}，经过手动查询充值结果为充值中，请继续等待。", orderid, retcode);
                            } else if (state == 9) {
                                //失败，走退款流程， 创建退款单
                                logger.info("DATAPLAN===手机话费订单{}返回码{}，经过手动查询充值结果为充值失败，创建话费充值失败退款！", orderid, retcode);
                                this.tradeOrderRefundsService.insertRechargeRefunds(orderNum);
                                
                                //发送提醒短信
                                String content = failureMsg;
                                int idx = content.indexOf("#");
                                content = content.replaceFirst(String.valueOf(content.charAt(idx)), userPhone);
                                idx = content.indexOf("#");
                                content = content.replaceFirst(String.valueOf(content.charAt(idx)), tradeOrderItem.getSkuName());
                                
                                SmsVO smsVo = createSmsVo(userPhone, content);
                                this.smsService.sendSms(smsVo);
                            }else if (state == -1) {
                                //找不到此订单
                                logger.warn("DATAPLAN===手机话费订单{}返回码{}，经过手动查询充值结果为找不到订单，请进入平台查询或者联系第三方欧飞客服进行核实", orderid, retcode);
                            }
                        } else {
                            //欧飞订单提交失败，走退款流程
                            logger.info("DATAPLAN==OFPAY==手机流量充值订单{}请求充值返回码为：{}，创建充值退款单！", orderid, retcode);
                            this.tradeOrderRefundsService.insertRechargeRefunds(orderNum);
                            
                            //发送提醒短信
                            String content = failureMsg;
                            int idx = content.indexOf("#");
                            content = content.replaceFirst(String.valueOf(content.charAt(idx)), userPhone);
                            idx = content.indexOf("#");
                            content = content.replaceFirst(String.valueOf(content.charAt(idx)), tradeOrderItem.getSkuName());
                            
                            SmsVO smsVo = createSmsVo(userPhone, content);
                            this.smsService.sendSms(smsVo);
                        }
                    }
				}
			} else if (orderNum.getType() == OrderTypeEnum.STORE_CONSUME_ORDER) {
			    //到店消费订单处理
			    synchronized(LockUtil.getInitialize().synObject(orderNum.getTradeNum())) {
			       this.tradeOrderService.dealWithStoreConsumeOrder(orderNum, result.getFlowNo(), result.getPayType().ordinal());
			    }
			} else {
				inserts(orderNum, result);
				
				// End 重构4.1 add by zengj
				// 发送计时消息
				if (ActivityTypeEnum.GROUP_ACTIVITY == orderNum.getActivityType()) {
					if (orderNum.getType() == OrderTypeEnum.SERVICE_ORDER) {
						List<TradeOrderItem> orderItem = tradeOrderItemMapper.selectOrderItemListById(orderNum.getId());
						if (orderItem != null && !Iterables.isEmpty(orderItem)) {
							for (TradeOrderItem item : orderItem) {
								GoodsStoreSkuService sku = goodsStoreSkuServiceService
										.selectBySkuId(item.getStoreSkuId());
								long delayTimeMillis = (sku.getEndTime().getTime() - System.currentTimeMillis()) / 1000;
								tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_service_order_refund_timeout,
										orderNum.getId(), delayTimeMillis);
							}
						}
					} else {
						tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_delivery_group_timeout,
								orderNum.getId());
					}
				} else {
					tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_delivery_timeout, orderNum.getId());
				}
			}
		} catch (Exception e) {
			logger.error("订单支付状态消息处理失败", e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		} finally {
			if(tradeNum != null) {
				LockUtil.getInitialize().unLock(tradeNum);	
			}
		}
		
		// begin add by wushp 20161015  
		try {
			TradeOrder tradeOrder = tradeOrderMapper.selectByParamsTrade(tradeNum);
			orderReturnCouponsService.firstOrderReturnCoupons(tradeOrder);
		} catch (Exception e) {
			logger.error(ExceptionConstant.COUPONS_REGISTE_RETURN_FAIL, tradeNum, e);
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		}
		// end add by wushp 20161015 
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}
	
	private void insertTradeOrderPay(TradeOrder tradeOrder, PayResponseDto result) throws Exception {
		//新增支付方式记录
		TradeOrderPay tradeOrderPay = new TradeOrderPay();
		tradeOrderPay.setId(UuidUtils.getUuid());
		tradeOrderPay.setCreateTime(new Date());
		tradeOrderPay.setPayAmount(tradeOrder.getActualAmount());
		tradeOrderPay.setOrderId(tradeOrder.getId());
		tradeOrderPay.setPayTime(new Date());
		tradeOrderPay.setReturns(result.getFlowNo());
		
		int payType = result.getPayType().ordinal();
		if (payType == 1) {
			tradeOrderPay.setPayType(PayTypeEnum.ALIPAY);
		} else if (payType == 2) {
			tradeOrderPay.setPayType(PayTypeEnum.WXPAY);
		} else if (payType == 3) {
			tradeOrderPay.setPayType(PayTypeEnum.JDPAY);
		}
		this.tradeOrderPayService.insertSelective(tradeOrderPay);
		
	}
	
	private void updateTradeOrderStatus(TradeOrder tradeOrder) throws Exception {
		//修改订单状态为代发货
		tradeOrder.setStatus(OrderStatusEnum.DROPSHIPPING);
		tradeOrder.setUpdateTime(new Date());
		this.tradeOrderService.updateRechargeOrderByTradeNum(tradeOrder);
	}
	
   private void updateTradeOrderStatus(TradeOrder tradeOrder, String sporderId) throws ServiceException {
        //修改订单状态为代发货
        tradeOrder.setStatus(OrderStatusEnum.DROPSHIPPING);
        tradeOrder.setUpdateTime(new Date());
        
        this.tradeOrderService.updataRechargeOrderStatus(tradeOrder, sporderId);
    }
	   
	public synchronized void inserts(TradeOrder tradeOrder, PayResponseDto result) throws Exception {
		List<TradeOrderItem> orderItem = tradeOrderItemMapper.selectOrderItemListById(tradeOrder.getId());
		TradeOrderItem item = orderItem.get(0);
		String storeSkuId = item.getStoreSkuId();
		tradeOrder.setTradeOrderItem(orderItem);
		int orderType = tradeOrder.getType().ordinal(); // 订单类型(0:实物订单,1:服务订单)
		int activityType = tradeOrder.getActivityType().ordinal();
		String activityId = tradeOrder.getActivityId();
		String orderId = tradeOrder.getId();
		int count = tradeOrderPayMapper.selectTradeOrderPayByOrderId(orderId);
		if (count == 0) {
			if (activityType == 4) { // 团购活动
				int skuNum = orderItem.get(0).getQuantity(); // 购买数量
				GoodsStoreSkuService skuService = new GoodsStoreSkuService();
				ActivityGroup activityGroup = new ActivityGroup();
				if (orderType == 1) {
					skuService = goodsStoreSkuServiceService.selectByStoreSkuId(storeSkuId);
				} else if (orderType == 0) {
					activityGroup = activityGroupService.selectServiceTime(activityId);
				}
				List<TradeOrderItemDetail> orderItemDetailList = new ArrayList<TradeOrderItemDetail>();
				for (int i = 0; i < skuNum; i++) {
					String OrderItemId = orderItem.get(0).getId();
					List<TradeOrderItemDetail> itemDetail = tradeOrderItemDetailMapper
							.selectByOrderItemById(OrderItemId);//
					int re = itemDetail.size();
					System.out.println("re~~~~~~~~~~~~~" + re);
					if (itemDetail.size() < skuNum) {
						TradeOrderItemDetail orderItemDetail = new TradeOrderItemDetail();
						orderItemDetail.setConsumeCode(RandomStringUtil.getRandomInt(8));
						orderItemDetail.setCreateTime(new Date());
						if (orderType == 1) {
							orderItemDetail.setEndTime(skuService.getEndTime());
							orderItemDetail.setStartTime(skuService.getStartTime());
						} else if (orderType == 0) {
							orderItemDetail.setEndTime(activityGroup.getEndTime());
							orderItemDetail.setStartTime(activityGroup.getStartTime());
						}
						orderItemDetail.setId(UuidUtils.getUuid());
						orderItemDetail.setOrderItemId(orderItem.get(0).getId());
						orderItemDetail.setStatus(ConsumeStatusEnum.noConsume);
						orderItemDetailList.add(orderItemDetail);
					}
				}

				TradeOrderPay tradeOrderPay = new TradeOrderPay();
				tradeOrderPay.setId(UuidUtils.getUuid());
				tradeOrderPay.setCreateTime(new Date());
				tradeOrderPay.setPayTime(new Date());
				tradeOrderPay.setPayAmount(result.getTradeAmount());
				tradeOrderPay.setOrderId(orderId);

				int payType = result.getPayType().ordinal();

				if (payType == 1) {
					tradeOrderPay.setPayType(PayTypeEnum.ALIPAY);
				} else if (payType == 2) {
					tradeOrderPay.setPayType(PayTypeEnum.WXPAY);
				} else if (payType == 3) {
					tradeOrderPay.setPayType(PayTypeEnum.JDPAY);
				}

				tradeOrderPay.setReturns(result.getFlowNo());
				if (orderType == 1) {
					tradeOrder.setStatus(OrderStatusEnum.HAS_BEEN_SIGNED);
				} else {
					tradeOrder.setStatus(OrderStatusEnum.DROPSHIPPING);
				}
				tradeOrder.setTradeNum(result.getTradeNum());

				tradeOrder.setTradeOrderPay(tradeOrderPay);

				if (orderItemDetailList.size() > 0) {
					tradeOrderItemDetailMapper.insertBatch(orderItemDetailList);
				}
				updateWithApply(tradeOrder);
			} else {
				
				// Begin 12002 add by zengj
				if (tradeOrder.getType() == OrderTypeEnum.SERVICE_STORE_ORDER) {
					// 线上支付的，支付完成，销量增加
					GoodsStoreSku goodsStoreSku = this.goodsStoreSkuService.getById(item.getStoreSkuId());
					if (goodsStoreSku != null) {
						goodsStoreSku.setSaleNum(
								(goodsStoreSku.getSaleNum() == null ? 0 : goodsStoreSku.getSaleNum()) + item.getQuantity());
						goodsStoreSkuService.updateByPrimaryKeySelective(goodsStoreSku);
					}
				}
				// End 12002 add by zengj
				
				TradeOrderPay tradeOrderPay = new TradeOrderPay();
				tradeOrderPay.setId(UuidUtils.getUuid());
				tradeOrderPay.setCreateTime(new Date());
				tradeOrderPay.setPayTime(new Date());
				tradeOrderPay.setPayAmount(result.getTradeAmount());
				tradeOrderPay.setOrderId(orderId);

				int payType = result.getPayType().ordinal();

				if (payType == 1) {
					tradeOrderPay.setPayType(PayTypeEnum.ALIPAY);
				} else if (payType == 2) {
					tradeOrderPay.setPayType(PayTypeEnum.WXPAY);
				} else if (payType == 3) {
					tradeOrderPay.setPayType(PayTypeEnum.JDPAY);
				}

				tradeOrderPay.setReturns(result.getFlowNo());
				// Begin V1.2 modified by maojj 2016-11-09
				if (orderType == 1) {
					// 团购服务订单
					tradeOrder.setStatus(OrderStatusEnum.HAS_BEEN_SIGNED);
				} else if (orderType == OrderTypeEnum.SERVICE_STORE_ORDER.ordinal() ){
					// 上门服务订单
					tradeOrder.setStatus(OrderStatusEnum.WAIT_RECEIVE_ORDER);
				} else {
					tradeOrder.setStatus(OrderStatusEnum.DROPSHIPPING);
				}
				// End V1.2 modified by maojj 2016-11-09
				tradeOrder.setTradeNum(result.getTradeNum());
				tradeOrder.setTradeOrderPay(tradeOrderPay);
				updateWithApply(tradeOrder);
			}
		}
	}

	/**
	 * 根据店铺类型获取TOPIC
	 *
	 * @param storeType
	 * @return
	 */
	private String getTopicByStoreType(StoreTypeEnum storeType) {
		switch (storeType) {
			case AROUND_STORE:
				return TOPIC_ORDER_AROUND;
			case FAST_DELIVERY_STORE:
				return TOPIC_ORDER_FAST;
			case CLOUD_STORE:
				return TOPIC_ORDER_CLOUD;
			case ACTIVITY_STORE:
				return TOPIC_ORDER_ACTIVITY;
			// Begin 重构4.1 add by zengj
			case SERVICE_STORE:
				return TOPIC_ORDER_SERVICE;
			// End 重构4.1 add by zengj
			default:
				break;
		}
		return null;
	}

	private StoreInfo getStoreInfo(String storeId) throws Exception {
		return storeInfoService.getStoreBaseInfoById(storeId);
	}

	/**
	 * 订单支付并发送消息(快送同步)
	 * @param tradeOrder 订单
	 */
	public boolean updateWithApply(TradeOrder tradeOrder) throws Exception {

		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());
		tradeOrder.setTradeOrderLogistics(tradeOrderLogisticsService.findByOrderId(tradeOrder.getId()));
		StoreInfo storeInfo = getStoreInfo(tradeOrder.getStoreId());
		JSONObject json = JSONObject.fromObject(tradeOrder, jsonConfig);
		json.put("storeType", storeInfo.getType());
		List<StoreAgentCommunity> communitys = storeInfoService.getAgentCommunitysByStoreId(tradeOrder.getStoreId());
		if (!Iterables.isEmpty(communitys)) {
			json.put("storeAgentCommunity", communitys.get(0));
		}

		Message msg = new Message(getTopicByStoreType(storeInfo.getType()), TAG_ORDER_APPLY,
				json.toString().getBytes(Charsets.UTF_8));
		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, tradeOrder,
				new LocalTransactionExecuter() {

					@Override
					public LocalTransactionState executeLocalTransactionBranch(Message msg, Object object) {
						try {
							tradeOrderService.updateMyOrderStatus((TradeOrder) object);
							return LocalTransactionState.COMMIT_MESSAGE;
						} catch (Exception e) {
							logger.error("执行支付回调失败", e);
							return LocalTransactionState.ROLLBACK_MESSAGE;
						}
					}
				}, new TransactionCheckListener() {

					@Override
					public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
						return LocalTransactionState.COMMIT_MESSAGE;
					}
				});
		return RocketMqResult.returnResult(sendResult);
	}

   private SmsVO createSmsVo(String mobile, String content) {
        SmsVO smsVo = new SmsVO();
        smsVo.setId(UuidUtils.getUuid());
        smsVo.setUserId(mobile);
        smsVo.setIsTiming(0);
        smsVo.setToken(mcmSysToken);
        smsVo.setSysCode(mcmSysCode);
        smsVo.setMobile(mobile);
        smsVo.setContent(content);
        smsVo.setSmsChannelType(3);
        smsVo.setSendTime(DateUtils.formatDateTime(new Date()));
        return smsVo;
    }
   
}
