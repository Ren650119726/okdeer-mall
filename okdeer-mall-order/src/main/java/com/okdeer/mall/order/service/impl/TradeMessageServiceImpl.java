/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @项目名称: yschome-mall 
 * @文件名称: TradeMessageServiceImpl.java 
 * @Date: 2016年4月27日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */

package com.okdeer.mall.order.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.rocketmq.common.message.Message;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuService;
import com.okdeer.archive.goods.store.enums.IsAppointment;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceServiceApi;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.archive.store.service.IStoreMemberRelationServiceApi;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.archive.system.entity.SysMsg;
import com.okdeer.archive.system.entity.SysUser;
import com.okdeer.archive.system.entity.SysUserLoginLog;
import com.okdeer.archive.system.pos.service.PosShiftExchangeServiceApi;
import com.okdeer.archive.system.pos.vo.PosShiftExchangeVo;
import com.okdeer.archive.system.service.SysUserLoginLogServiceApi;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.common.utils.VersionUtils;
import com.okdeer.mall.common.enums.IsRead;
import com.okdeer.mall.common.enums.MsgType;
import com.okdeer.mall.order.constant.text.OrderMsgConstant;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.PickUpTypeEnum;
import com.okdeer.mall.order.enums.SendMsgType;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
import com.okdeer.mall.order.service.TradeMessageService;
import com.okdeer.mall.order.service.TradeMessageServiceApi;
import com.okdeer.mall.order.service.TradeOrderComplainService;
import com.okdeer.mall.order.service.TradeOrderItemDetailService;
import com.okdeer.mall.order.service.TradeOrderItemService;
import com.okdeer.mall.order.vo.PushMsgVo;
import com.okdeer.mall.order.vo.PushUserVo;
import com.okdeer.mall.order.vo.SendMsgParamVo;
import com.okdeer.mall.order.vo.TradeOrderComplainVo;
import com.okdeer.mall.system.mapper.SysMsgMapper;
import com.okdeer.mall.system.mapper.SysUserMapper;
import com.okdeer.mall.system.service.SysBuyerUserService;
import com.okdeer.mcm.constant.MsgConstant;
import com.okdeer.mcm.entity.BaseResponse;
import com.okdeer.mcm.entity.SmsVO;
import com.okdeer.mcm.service.ISmsService;

/**
 * 订单消息推送
 * @pr yschome-mall
 * @author guocp
 * @date 2016年4月27日 下午2:18:11
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *    重构4.1             2016-7-15            wusw               添加服务店派单发送短信、修改拒收、取消发送短信及短信模板（添加服务店情况）
 *    重构4.1             2016-7-15            wusw               修改服务店派单发送短信的线下确认价格并当面支付的支付方式判断
 *    重构4.1             2016-8-11            maojj              消息内容去除商品名称信息
 *    重构4.1             2016-8-11            maojj              订单消息推送 --POS时，修改json转换方式
 *    Bug:13029          2016-8-22            maojj              修改推送的详细内容
 *    V1.2				 2016-12-02		      maojj				   添加服务店接单通知短信
 *    商业系统对接			 2016-12-19			  maojj				 POS消息同时推送到商业系统
 *    V2.2.0             2017-3-30            zhaoqc             发送消息根据APP版本区分消息类型
 *    V2.3.0			20170419				wangf01			新增推送语音提示文件名
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeMessageServiceApi")
public class TradeMessageServiceImpl implements TradeMessageService, TradeMessageServiceApi {

	private static final Logger logger = LoggerFactory.getLogger(TradeMessageServiceImpl.class);

	/**短信发送成功码*/
	private static final String SUCCESS_STATUS = "0";
	
	private static final String TOPIC = "topic_mcm_msg";

	/**
	 * 消息系统CODE
	 */
	@Value("${mcm.sys.code}")
	private String msgSysCode;

	/**
	 * 消息token
	 */
	@Value("${mcm.sys.token}")
	private String msgToken;
	
	@Autowired
	private RocketMQProducer rocketMQProducer;

	
	@Autowired
	private TradeMessageProperties tradeMessageProperties;

	/**
	 * 默认通知样式：0
	 */
	private static final Integer defaultNotificationBuilderId = 0;

	/**
	 * 默认:响铃+震动+可清除
	 */
	private static final Integer defaultNotificationBasicStyle1 = 7;

	/**
	 * 默认:震动+可清除
	 */
	private static final Integer defaultNotificationBasicStyle2 = 3;
	

	@Reference(version = "1.0.0", check = false)
	private ISmsService smsService;

	@Reference(version = "1.0.0", check = false)
	private PosShiftExchangeServiceApi posShiftExchangeService;

	@Reference(version = "1.0.0", check = false)
	private IStoreMemberRelationServiceApi storeMemberRelationService;

	@Autowired
	private SysBuyerUserService sysBuyerUserService;

	/**
	 * 店铺信息Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoService;

	/**
	 * 订单项Service
	 */
	@Autowired
	private TradeOrderItemService tradeOrderItemService;

	/**
	 * 订单项详情Service
	 */
	@Autowired
	private TradeOrderItemDetailService tradeOrderItemDetailService;

	/**
	 * 服务商品Mapper(不能引用到service)
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceServiceApi goodsStoreSkuServiceService;

	@Autowired
	private SysUserMapper sysUserMapper;

	@Autowired
	private TradeOrderItemMapper tradeOrderItemMapper;

	@Resource
	private SysMsgMapper sysMsgMapper;

	@Autowired
	private TradeOrderComplainService tradeOrderComplainService;
	
	// Begin 商业系统POS推送对接  added by maojj 2016-12-19
	@Reference(version = "1.0.0", check = false)
	private SysUserLoginLogServiceApi sysUserLoginLogApi;
	
	private static final String TOPIC_ONLINE_ORDER_TOPOS = "topic_online_order_topos";
	
	private static final String TAG_ONLINE_ORDER_TOPOS = "tag_online_order_topos";
	// End added by maojj 2016-12-19

	private void sendMessage(Object entity) throws Exception {
		MQMessage anMessage = new MQMessage(TOPIC, (Serializable)JsonMapper.nonDefaultMapper().toJson(entity));
		rocketMQProducer.sendMessage(anMessage);
	}

	/**
	 * 保存商家中心消息
	 * @author zengj
	 * @param tradeOrder
	 * @param sendMsgType 消息类型
	 */
	public void saveSysMsg(TradeOrder tradeOrder, SendMsgType sendMsgType) {
		SysMsg sysMsg = new SysMsg();

		// 订单信息不存在，不做保存
		if (tradeOrder == null || StringUtils.isBlank(tradeOrder.getId())) {
			return;
		}

		sysMsg.setId(UuidUtils.getUuid()); // ID
		// 消息标题
		String title = null;
		// 消息内容
		String context = null;
		// 消息类型
		MsgType msgType = null;
		switch (sendMsgType) {
			// 下单消息
			case createOrder:
				List<TradeOrderItem> tradeOrderItemList = tradeOrder.getTradeOrderItem();
				// 如果参数中的订单项不存在，从数据库查询
				if (tradeOrderItemList == null || tradeOrderItemList.isEmpty()) {
					tradeOrderItemList = tradeOrderItemMapper.selectOrderItemListById(tradeOrder.getId());
				}
				// Begin modified by maojj 2016-08-11:消息内容去除商品名称信息
				title = "下单通知消息";
				context = "您有一条新的订单需要处理，订单号【" + tradeOrder.getOrderNo() + "】";
				// End modified by maojj 2016-08-11
				msgType = MsgType.ORDER_NOTICE_MSG;
				break;
			// 申请退款
			case applyReturn:

				break;
			// 投诉单消息
			case complainOrder:
				title = "用户投诉";
				try {
					// 查询投诉内容
					List<TradeOrderComplainVo> list = tradeOrderComplainService.findByOrderId(tradeOrder.getId());
					String complainContext = null;
					// 如果存在投诉内容
					if (list != null && !list.isEmpty()) {
						int listSize = list.size() - 1;
						complainContext = list.get(listSize) == null ? null : list.get(listSize).getContent();
					}

					context = "您有一条投诉消息，来自用户" + tradeOrder.getUserPhone() + "，投诉订单" + tradeOrder.getOrderNo();
					if (StringUtils.isNotBlank(complainContext)) {
						context += "，投诉内容：" + complainContext;
					}
					msgType = MsgType.USER_COMPLAIN_NOTICE;
					break;
				} catch (ServiceException e) {
					logger.error("查询投诉信息失败", e);
					// 没有投诉内容，消息不展示投诉内容
					context = "您有一条投诉消息，来自用户" + tradeOrder.getUserPhone() + "，投诉订单" + tradeOrder.getOrderNo();
					msgType = MsgType.USER_COMPLAIN_NOTICE;
					break;
				}

			default:
				break;
		}
		sysMsg.setTitle(title); // 标题
		sysMsg.setContext(context); // 消息内容
		sysMsg.setCreateTime(new Date()); // 创建时间
		sysMsg.setDisabled(Disabled.valid); // 是否失效：0未失效，1失效

		sysMsg.setFromUserId(""); // 消息发送人Id
		sysMsg.setIsRead(IsRead.UNREAD); // 是否已读：0未读，1已读
		sysMsg.setLink(""); // 消息超链接
		sysMsg.setStoreId(tradeOrder.getStoreId()); // 店铺ID
		sysMsg.setTargetId(tradeOrder.getId()); // 链接目标id,如订单ID
		sysMsg.setType(msgType); // 消息类型：0提现通知，1下单通知，2退款申请，4退款准备超时未处理 5补货通知
									// 6用户投诉通知 7运营商下发公告
		sysMsgMapper.insertSelective(sysMsg);
	}

	/**
	 * 订单消息推送 --POS</p>
	 * 
	 * @author zengj
	 * @param sendMsgParamVo 消息推送参数
	 * @param sendMsgType 推送消息类型
	 * @throws Exception
	 */
	@Deprecated
	@Override
	public void sendPosMessage(SendMsgParamVo sendMsgParamVo, SendMsgType sendMsgType) throws Exception {
		PushMsgVo pushMsgVo = new PushMsgVo();
		pushMsgVo.setSysCode(msgSysCode);
		pushMsgVo.setToken(msgToken);
		pushMsgVo.setSendUserId(sendMsgParamVo.getUserId());
		pushMsgVo.setServiceFkId(sendMsgParamVo.getOrderId());
		pushMsgVo.setServiceTypes(new Integer[] { 2 });
		// 2:商家APP,3POS机
		pushMsgVo.setAppType(3);
		pushMsgVo.setIsUseTemplate(0);
		pushMsgVo.setMsgType(0);
		pushMsgVo.setMsgTypeCustom(OrderMsgConstant.MESSAGE_BUY);

		// 推送消息标题
		String msgTitle = null;
		switch (sendMsgType) {
			// 下单消息
			case createOrder:
				msgTitle = "您有一条新订单";
				break;
			// 申请退款消息
			case applyReturn:
				msgTitle = "您有一条新的售后申请";
				break;
			// 卖家同意退款后，买家退货(物流形式)给卖家
			case returnShipments:
				msgTitle = "您有一条新的售后申请";
				break;
			// 投诉单
			case complainOrder:
				msgTitle = "您有一条投诉单";
				break;
			default:
				break;
		}
		// 不使用模板
		pushMsgVo.setMsgNotifyContent(msgTitle);
		pushMsgVo.setMsgDetailType(1);
		// Begin modified by maojj 2016-08-22
		pushMsgVo.setMsgDetailContent("");
		// End modified by maojj 2016-08-22
		// 设置是否定时发送
		pushMsgVo.setIsTiming(0);
		// 发送用户
		// String[] users = userIds.split(",");
		List<PushUserVo> userList = new ArrayList<PushUserVo>();
		Map<String, Object> params = Maps.newHashMap();
		params.put("storeId", sendMsgParamVo.getStoreId());
		params.put("isFinish", 0);
		List<PosShiftExchangeVo> posShifts = posShiftExchangeService.selectByParams(params);
		for (PosShiftExchangeVo posShift : posShifts) {
			PushUserVo pushUser = new PushUserVo();
			pushUser.setUserId(posShift.getUserId());
			pushUser.setMobile(posShift.getPhone());
			// posShift.getPosCode();
			pushUser.setMsgType(0);
			userList.add(pushUser);
		}

		pushMsgVo.setUserList(userList);
		//发送消息
		sendMessage(pushMsgVo);
		
		// Begin added by maojj 2016-12-19 商业系统POS推送对接
		sendMsgParamVo.setSendMsgType(sendMsgType.ordinal());
		Message posMsg = new Message(TOPIC_ONLINE_ORDER_TOPOS,TAG_ONLINE_ORDER_TOPOS,sendMsgParamVo.getOrderId(),JsonMapper.nonDefaultMapper().toJson(sendMsgParamVo).getBytes(Charsets.UTF_8));
		rocketMQProducer.send(posMsg);
		// End added by maojj 2016-12-19 商业系统POS推送对接
	}

	/**
	 * 商品版App消息推送 --商家版APP</p>
	 * 
	 * @author zengj
	 * @param sendMsgParamVo 消息推送参数
	 * @param sendMsgType 推送消息类型
	 * @throws Exception
	 */
	@Override
	public void sendSellerAppMessage(SendMsgParamVo sendMsgParamVo, SendMsgType sendMsgType) throws Exception {
		//查询店铺的用户信息
	    List<SysUser> sysUserList = sysUserMapper.selectUserByStoreId(sendMsgParamVo.getStoreId());
	    
	    if(CollectionUtils.isEmpty(sysUserList)){
	    	return;
	    }
	    //版本号比较器类
	    VersionUtils compare = new VersionUtils();	   
	    
	    //内容消息推送用户列表
	    List<PushUserVo> oriMsgUserList = new ArrayList<PushUserVo>();
	    //链接消息推送列表
	    List<PushUserVo> linkedMsgUserList = new ArrayList<>();
	    
        sysUserList.forEach(sysUser -> {
            WhetherEnum whetherEnum = sysUser.getIsAccept();
            if(whetherEnum == WhetherEnum.not) {
                return;
            }
            
            //查看当前登录的设备APP版本
            List<SysUserLoginLog> sysUserLoginLogs = this.sysUserLoginLogApi.findAllByUserId(sysUser.getId(), 1, null, null);
            if(sysUserLoginLogs != null && !sysUserLoginLogs.isEmpty()) {
                sysUserLoginLogs.forEach(sysUserLoginLog -> {
                    PushUserVo pushUser = createPushUserVo(sysUser,sendMsgType);
                    
                    String version = sysUserLoginLog.getVersion();
                    if(StringUtils.isEmpty(version)){
                    	linkedMsgUserList.add(pushUser);
                    }else{
	                    int compareRes = compare.compare(version, "2.1.0");
	                    if(compareRes == 1) {
	                        //APP跳转原生页面，发送内容消息
	                        oriMsgUserList.add(pushUser);
	                    } else {
	                        //APP调整H5页面，发送链接消息
	                        linkedMsgUserList.add(pushUser);
	                    }
                    }
                });
            }
        });  
	    
	    //发送内容消息--2.1.0及之后版本
		if(CollectionUtils.isNotEmpty(oriMsgUserList)) {
		    PushMsgVo pushOriMsgVo = createPushMsgVo(sendMsgParamVo, sendMsgType, 1);
		    pushOriMsgVo.setUserList(oriMsgUserList);
			sendMessage(pushOriMsgVo);
		}
		//发送链接消息--2.1.0之前版本
		if(CollectionUtils.isNotEmpty(linkedMsgUserList)) {
		    PushMsgVo pushLinkedMsgVo = createPushMsgVo(sendMsgParamVo, sendMsgType, 0);
		    pushLinkedMsgVo.setUserList(linkedMsgUserList);
		    sendMessage(pushLinkedMsgVo);
        }
	}
	
	/**
	 * 构建消息推送用户VO
	 * @param sysUser
	 * @return
	 */
	private PushUserVo createPushUserVo(SysUser sysUser, SendMsgType sendMsgType) {
	    PushUserVo pushUser = new PushUserVo();
	    pushUser.setUserId(sysUser.getId());
        pushUser.setMobile(sysUser.getPhone());
        pushUser.setMsgType(MsgConstant.MsgType.THROUGH);
        // begin V2.3.0 新增语音播放文件名 add by wangf01 20170419
		pushUser.setSoundStyle("order.wav");
		//判断是否是申请退款||卖家同意退款后，如果是，则提示默认声音，针对IOS
		if(sendMsgType == SendMsgType.applyReturn || sendMsgType == SendMsgType.returnShipments ){
			pushUser.setSoundStyle("default");
		}
		// end add by wangf01 20170419
        
        try {
            pushUser.setNotificationBuilderId(Integer.valueOf(tradeMessageProperties.notificationBuilderId));
         // 消息信息提示
            if (WhetherEnum.whether.equals(sysUser.getIsAccept())) {
                // 有声音
                pushUser.setIsexitsSound(0);
                pushUser.setNotificationBasicStyle(Integer.valueOf(tradeMessageProperties.notificationBasicStyle1));
            } else {
                // 无声音
                pushUser.setNotificationBasicStyle(Integer.valueOf(tradeMessageProperties.notificationBasicStyle2));
                pushUser.setIsexitsSound(1);
            }
        } catch (Exception e) {
            // 没有配置zookeeper，取默认的
            pushUser.setNotificationBuilderId(defaultNotificationBuilderId);
            // 消息信息提示
            if (WhetherEnum.whether.equals(sysUser.getIsAccept())) {
                // 有声音
                pushUser.setIsexitsSound(0);
                pushUser.setNotificationBasicStyle(defaultNotificationBasicStyle1);
            } else {
                // 无声音
                pushUser.setNotificationBasicStyle(defaultNotificationBasicStyle2);
                pushUser.setIsexitsSound(1);
            }
        }
        
        return pushUser;
	}	
	
	/**
	 * 
	 * @param sendMsgParamVo
	 * @param sendMsgType
	 * @param msgContentType 0链接，1内容
	 * @return
	 */
	private PushMsgVo createPushMsgVo(SendMsgParamVo sendMsgParamVo, SendMsgType sendMsgType, int msgContentType) {
		
        // 推送消息标题
        String msgTitle = null;
        String serviceFkId = null;
        // 业务消息标识
        String msgTypeCustom = null;
        String linkUrl = tradeMessageProperties.orderDetailLink + "/" + sendMsgParamVo.getOrderId();
        switch (sendMsgType) {
            // 下单消息
            case createOrder:
                msgTitle = "您有一条新订单需要处理";
                msgTypeCustom = OrderMsgConstant.SELLER_MESSAGE_BUY;
                serviceFkId = sendMsgParamVo.getOrderId();
                break;
            // 申请退款消息
            case applyReturn:
                msgTitle = "您有一条新的售后申请";
                linkUrl = tradeMessageProperties.orderRefundsDetailLink + "/" + sendMsgParamVo.getRefundsId();
                msgTypeCustom = OrderMsgConstant.SELLER_MESSAGE_RETURN;
                serviceFkId = sendMsgParamVo.getRefundsId();
                break;
            // 卖家同意退款后，买家退货(物流形式)给卖家
            case returnShipments:
                msgTitle = "您有一条新的售后申请";
                linkUrl = tradeMessageProperties.orderRefundsDetailLink + "/" + sendMsgParamVo.getRefundsId();
                serviceFkId = sendMsgParamVo.getRefundsId();
                msgTypeCustom = OrderMsgConstant.SELLER_MESSAGE_RETURN;
                break;
            // 投诉单
            case complainOrder:
                msgTitle = "您有一条投诉单";
                msgTypeCustom = OrderMsgConstant.SELLER_MESSAGE_COMPLAIN;
                serviceFkId = sendMsgParamVo.getOrderId();
                break;
            default:
                break;
        }
        PushMsgVo pushMsgVo = new PushMsgVo();
        pushMsgVo.setMsgTypeCustom(msgTypeCustom);
        pushMsgVo.setServiceFkId(serviceFkId);
        if(msgContentType == 0) {
            pushMsgVo.setMsgDetailLinkUrl(linkUrl);
        } /*else if(msgContentType == 1) {
            pushMsgVo.setMsgDetailContent(serviceFkId);
        }*/
        pushMsgVo.setMsgNotifyContent(msgTitle);
        pushMsgVo.setMsgDetailType(msgContentType);
        
        pushMsgVo.setSysCode(msgSysCode);
        pushMsgVo.setToken(msgToken);
        pushMsgVo.setSendUserId(sendMsgParamVo.getUserId());       
        pushMsgVo.setServiceTypes(new Integer[] { 2 });
        // 2:商家APP,3POS机
        pushMsgVo.setAppType(2);
        pushMsgVo.setIsUseTemplate(0);
        if(msgContentType==0){
        	pushMsgVo.setMsgType(MsgConstant.MsgType.NOTICE);
        }else{
        	pushMsgVo.setMsgType(MsgConstant.MsgType.THROUGH);
        }
        // 设置是否定时发送
        pushMsgVo.setIsTiming(0);
        return pushMsgVo;
	}


	/**
	 * 点击发货时发送短信
	 * @author zengj
	 */
	public void sendSmsByShipments(TradeOrder order) {
		Map<String, String> params = Maps.newHashMap();
		params.put("#1", order.getOrderNo());
		// 查询用户电话号码
		String mobile = sysBuyerUserService.selectMemberMobile(order.getUserId());
		if (StringUtils.isNotBlank(mobile)) {
			this.sendSms(mobile, tradeMessageProperties.smsShipmentsStyle1, params);
		} else {
			logger.error("订单号：[" + order.getOrderNo() + "]的用户ID:[" + order.getUserId() + "]手机号码为空");
		}
	}

	/**
	 * 发送短信 
	 * @param order 订单详情
	 */
	@Override
	public void sendSmsByCancel(TradeOrder order, OrderStatusEnum status) {

		// 取消订单发送短信
		if (status == OrderStatusEnum.DROPSHIPPING || status == OrderStatusEnum.WAIT_RECEIVE_ORDER) {
			Map<String, String> params = Maps.newHashMap();
			params.put("#1", order.getOrderNo());
			params.put("#2", order.getReason());
			// 实付金额
			BigDecimal actualAmount = order.getActualAmount();
			// 扣除违约金
			if (WhetherEnum.whether.equals(order.getIsBreach())) {
				actualAmount = actualAmount.subtract(order.getBreachMoney());
				params.put("#4", order.getBreachMoney().toString());
			}
			params.put("#3", actualAmount.toString());

			// 查询用户电话号码
			String mobile = sysBuyerUserService.selectMemberMobile(order.getUserId());
			// Begin 重构4.1 add by wusw
			StoreInfo storeInfo = storeInfoService.findById(order.getStoreId());
			// 服务店
			if (StoreTypeEnum.SERVICE_STORE.equals(storeInfo.getType())) {
				if (PayWayEnum.PAY_ONLINE == order.getPayWay()) {
					TradeOrderPay payment = order.getTradeOrderPay();
					if (PayTypeEnum.ALIPAY == payment.getPayType() || PayTypeEnum.WXPAY == payment.getPayType()) {
						
						//add by zengjz 判断是有违约金 2016-11-25
						if (WhetherEnum.whether == order.getIsBreach()) {
							this.sendSms(mobile, tradeMessageProperties.smsServiceStoreCancelStyle4, params);
						} else {
							this.sendSms(mobile, tradeMessageProperties.smsServiceStoreCancelStyle1, params);
						}
						//end by zengjz 判断是有违约金 2016-11-25
						
					} else if (PayTypeEnum.WALLET == payment.getPayType()
							|| PayTypeEnum.JDPAY == payment.getPayType()) {
						
						//add by zengjz 判断是有违约金 2016-11-25
						if (WhetherEnum.whether == order.getIsBreach()) {
							this.sendSms(mobile, tradeMessageProperties.smsServiceStoreCancelStyle5, params);
						} else {
							this.sendSms(mobile, tradeMessageProperties.smsServiceStoreCancelStyle2, params);
						}
						//end by zengjz 判断是有违约金 2016-11-25

					} // Begin 重构4.1 add by wusw 20160720
				} else if (PayWayEnum.OFFLINE_CONFIRM_AND_PAY == order.getPayWay()) {
					this.sendSms(mobile, tradeMessageProperties.smsServiceStoreCancelStyle3, params);
				}
				// End 重构4.1 add by wusw 20160720
			} else {// End 重构4.1 add by wusw
				if (PayWayEnum.CASH_DELIERY == order.getPayWay()) {
					this.sendSms(mobile, tradeMessageProperties.smsCancalOrderStyle3, params);
				} else if (PayWayEnum.PAY_ONLINE == order.getPayWay()) {
					TradeOrderPay payment = order.getTradeOrderPay();
					if (PayTypeEnum.ALIPAY == payment.getPayType() || PayTypeEnum.WXPAY == payment.getPayType()) {
						this.sendSms(mobile, tradeMessageProperties.smsCancalOrderStyle1, params);
					} else if (PayTypeEnum.WALLET == payment.getPayType()
							|| PayTypeEnum.JDPAY == payment.getPayType()) {
						this.sendSms(mobile, tradeMessageProperties.smsCancalOrderStyle2, params);
					}
				}
			}

		} else if (status == OrderStatusEnum.TO_BE_SIGNED) {
			// 用户拒收发送短信
			Map<String, String> params = Maps.newHashMap();
			params.put("#1", order.getOrderNo());
			params.put("#2", order.getActualAmount().toString());
			params.put("#3", order.getReason());

			// 查询用户电话号码
			String mobile = sysBuyerUserService.selectMemberMobile(order.getUserId());
			// Begin 重构4.1 add by wusw
			StoreInfo storeInfo = storeInfoService.findById(order.getStoreId());
			// 服务店
			if (StoreTypeEnum.SERVICE_STORE.equals(storeInfo.getType())) {
				if (PayWayEnum.PAY_ONLINE == order.getPayWay()) {
					TradeOrderPay payment = order.getTradeOrderPay();
					if (PayTypeEnum.ALIPAY == payment.getPayType() || PayTypeEnum.WXPAY == payment.getPayType()) {
						this.sendSms(mobile, tradeMessageProperties.smsServiceStoreRefuseStyle1, params);
					} else if (PayTypeEnum.WALLET == payment.getPayType()
							|| PayTypeEnum.JDPAY == payment.getPayType()) {
						this.sendSms(mobile, tradeMessageProperties.smsServiceStoreRefuseStyle2, params);
					} // Begin 重构4.1 add by wusw 20160720
				} else if (PayWayEnum.OFFLINE_CONFIRM_AND_PAY == order.getPayWay()) {
					this.sendSms(mobile, tradeMessageProperties.smsServiceStoreRefuseStyle3, params);
				} // End 重构4.1 add by wusw 20160720
			} else {// End 重构4.1 add by wusw
				if (PayWayEnum.CASH_DELIERY == order.getPayWay()) {
					this.sendSms(mobile, tradeMessageProperties.smsUserRefuseUnPay, params);
				} else if (PayWayEnum.PAY_ONLINE == order.getPayWay()) {
					TradeOrderPay payment = order.getTradeOrderPay();
					if (PayTypeEnum.ALIPAY == payment.getPayType() || PayTypeEnum.WXPAY == payment.getPayType()) {
						this.sendSms(mobile, tradeMessageProperties.smsUserRefuseThirdPay, params);
					} else if (PayTypeEnum.WALLET == payment.getPayType()
							|| PayTypeEnum.JDPAY == payment.getPayType()) {
						this.sendSms(mobile, tradeMessageProperties.smsUserRefuseWalletPay, params);
					}
				}
			}
		}
	}

	/**
	 * 商家同意退款发送短信 
	 * @param order 订单详情
	 */
	@Override
	public void sendSmsByAgreePay(TradeOrderRefunds refunds, PayWayEnum payWay) {
		// 发送短信
		Map<String, String> params = Maps.newHashMap();
		params.put("#1", refunds.getOrderNo());
		params.put("#2", refunds.getTotalAmount().toString());

		// 查询用户电话号码
		String mobile = sysBuyerUserService.selectMemberMobile(refunds.getUserId());
		if (PayWayEnum.CASH_DELIERY == payWay) {
			this.sendSms(mobile, tradeMessageProperties.smsAgreeRefundPayStyle3, params);
		} else if (PayWayEnum.PAY_ONLINE == payWay) {
			PayTypeEnum payType = refunds.getPaymentMethod();
			if (PayTypeEnum.ALIPAY == payType || PayTypeEnum.WXPAY == payType) {
				this.sendSms(mobile, tradeMessageProperties.smsAgreeRefundPayStyle1, params);
			} else if (PayTypeEnum.WALLET == payType || PayTypeEnum.JDPAY == payType) {
				this.sendSms(mobile, tradeMessageProperties.smsAgreeRefundPayStyle2, params);
			}
		}
	}

	/**
	 * 商家同意退款发送短信 
	 * @param order 订单详情
	 */
	@Override
	public void sendSmsByYschomePay(TradeOrderRefunds refunds) {
		// 发送短信
		Map<String, String> params = Maps.newHashMap();
		params.put("#1", refunds.getOrderNo());
		params.put("#2", refunds.getTotalAmount().toString());

		// 查询用户电话号码
		String mobile = sysBuyerUserService.selectMemberMobile(refunds.getUserId());
		this.sendSms(mobile, tradeMessageProperties.smsAgreeRefundPayStyle1, params);
	}

	/**
	 * 下单时发送短信
	 * @author zengj
	 * @param tradeOrder
	 * @throws Exception 
	 */
	public void sendSmsByCreateOrder(TradeOrder tradeOrder) throws Exception {
		// 短信发送模板
		String smsTempate = null;

		// 订单信息不存在， 直接返回
		if (tradeOrder == null || StringUtils.isBlank(tradeOrder.getId())) {
			return;
		}
		Map<String, String> params = Maps.newHashMap();
		// 到店自提订单下单短信
		if (PickUpTypeEnum.TO_STORE_PICKUP.equals(tradeOrder.getPickUpType())) {
			smsTempate = tradeMessageProperties.smsOrderStorePickupStyle1;
			// 订单编号
			params.put("#1", tradeOrder.getOrderNo());
			// 提货码
			params.put("#2", tradeOrder.getPickUpCode());
		} else if (OrderTypeEnum.SERVICE_ORDER.equals(tradeOrder.getType())) {
			// 团购服务型商品下单短信
			smsTempate = tradeMessageProperties.smsServerOrderStyle1;
			// 团购型服务订单只能买一款商品
			List<TradeOrderItem> tradeOrderItems = tradeOrder.getTradeOrderItem();
			// 如果订单项为空，去数据库直接查询
			if (tradeOrderItems == null || tradeOrderItems.isEmpty()) {
				tradeOrderItems = tradeOrderItemService.selectOrderItemByOrderId(tradeOrder.getId());
			}
			if (tradeOrderItems != null && !tradeOrderItems.isEmpty()) {
				// 商品名称
				params.put("#1", tradeOrderItems.get(0).getSkuName());

				// 服务订单消费码信息
				List<TradeOrderItemDetail> tradeOrderItemDetails = tradeOrderItems.get(0).getTradeOrderItemDetails();
				logger.error("参数中的detailSize=" + (tradeOrderItemDetails == null ? 0 : tradeOrderItemDetails.size()));
				// 订单消费码信息，多个以逗号隔开
				StringBuffer consumeCode = new StringBuffer();
				// 如果订单项详情为空，去数据库查询一次
				if (tradeOrderItemDetails == null || tradeOrderItemDetails.isEmpty()) {
					logger.error("查询detail,orderItemId=" + (tradeOrderItems.get(0).getId()));
					tradeOrderItemDetails = tradeOrderItemDetailService
							.selectByOrderItemById(tradeOrderItems.get(0).getId());
				}
				logger.error("最终的detailSize=" + (tradeOrderItemDetails == null ? 0 : tradeOrderItemDetails.size()));
				if (tradeOrderItemDetails != null && !tradeOrderItemDetails.isEmpty()) {
					for (TradeOrderItemDetail detail : tradeOrderItemDetails) {
						consumeCode.append(detail.getConsumeCode()).append(",");
					}
				}
				logger.error("消费码==" + consumeCode.toString());
				// 将最后一个逗号去掉
				if (consumeCode.toString().endsWith(",")) {
					consumeCode = new StringBuffer(consumeCode.substring(0, consumeCode.length() - 1));
				}
				// 消费码，多个以逗号隔开
				params.put("#2", consumeCode.toString());

				// 获取服务商品信息
				GoodsStoreSkuService goodsStoreSkuService = goodsStoreSkuServiceService
						.selectBySkuId(tradeOrderItems.get(0).getStoreSkuId());
				if (goodsStoreSkuService != null) {
					// 有效期
					StringBuffer serviceTime = new StringBuffer(
							DateUtils.formatDate(goodsStoreSkuService.getStartTime(), "yyyy-MM-dd HH:mm") + " 至 "
									+ DateUtils.formatDate(goodsStoreSkuService.getEndTime(), "yyyy-MM-dd HH:mm"));
					// 如果有不可用日期
					if (StringUtils.isNotBlank(goodsStoreSkuService.getInvalidDate())) {
						serviceTime.append("(").append(goodsStoreSkuService.getInvalidDate()).append("不可用)");
					}
					// 有效期
					params.put("#5", serviceTime.toString());

					String isAppointment = "无需预约";
					// 是否需要提前预约--需提前#6小时预约（当不需要预约时则显示无需预约）
					if (IsAppointment.NEED.equals(goodsStoreSkuService.getIsAppointment())) {
						// 是否需要预约，将小数点后的0去除
						String s = goodsStoreSkuService.getAppointmentHour() == null ? isAppointment
								: goodsStoreSkuService.getAppointmentHour().toString();
						try {
							if (s.indexOf(".") > 0) {
								s = s.replaceAll("0+?$", "");// 去掉多余的0
								s = s.replaceAll("[.]$", "");// 如最后一位是.则去掉
							}
						} catch (Exception e) {
							s = goodsStoreSkuService.getAppointmentHour() == null ? isAppointment
									: goodsStoreSkuService.getAppointmentHour().toString();
						}
						isAppointment = "需提前" + s + "小时预约";
					}
					params.put("#6", isAppointment);
				}

			}
			// 获取店铺信息
			StoreInfo storeInfo = storeInfoService.getStoreBaseInfoById(tradeOrder.getStoreId());
			if (storeInfo != null) {
				// 商家地址
				params.put("#3", storeInfo.getArea() + storeInfo.getAddress());
				// 店铺电话
				String servicePhone = storeInfo.getStoreInfoExt() == null ? null
						: storeInfo.getStoreInfoExt().getServicePhone();
				// 如果客服电话为空，直接给店铺电话
				if (StringUtils.isBlank(servicePhone)) {
					servicePhone = storeInfo.getMobile();
				}
				// 商家电话
				params.put("#4", servicePhone);
			}

		}
		// 如果没有短信模板，直接返回
		if (StringUtils.isBlank(smsTempate)) {
			return;
		}
		// 发送用户的手机号码取订单的手机号码
		String mobile = tradeOrder.getUserPhone();
		// 如果为空，查询数据库中买家的手机号码
		if (StringUtils.isBlank(mobile)) {
			mobile = sysBuyerUserService.selectMemberMobile(tradeOrder.getUserId());
		}
		if (StringUtils.isNotBlank(mobile)) {
			this.sendSms(mobile, smsTempate, params);
		} else {
			logger.error("订单号：[" + tradeOrder.getOrderNo() + "]的用户ID:[" + tradeOrder.getUserId() + "]手机号码为空");
		}
	}

	/**
	 * 发送短信
	 */
	@Override
	public void sendSms(String mobile, String content, Map<String, String> param) {
		if (StringUtils.isNullOrEmpty(mobile) || StringUtils.isNullOrEmpty(content)) {
			logger.error("发短信失败，电话号码错误货内容为空", content);
		}

		Iterator<Entry<String, String>> it = param.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			content = content.replace(entry.getKey(), entry.getValue());
		}
		SmsVO sms = new SmsVO();
		sms.setContent(content);
		sms.setSysCode(msgSysCode);
		sms.setId(UuidUtils.getUuid());
		sms.setMobile(mobile);
		sms.setToken(msgToken);
		sms.setIsTiming(0);
		sms.setSmsChannelType(3);
		sms.setSendTime(DateUtils.formatDateTime(new Date()));
		BaseResponse res = smsService.sendSms(sms);
		if (!SUCCESS_STATUS.equals(res.getResult())) {
			logger.error("发短信失败，" + res.getMessage());
		}
	}

	// Begin 重构4.1 add by wusw
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeMessageService#sendSmsByServiceStoreShipments(com.okdeer.mall.order.entity.TradeOrder)
	 */
	@Override
	public void sendSmsByServiceStoreShipments(TradeOrder order) {
		Map<String, String> params = Maps.newHashMap();
		params.put("#1", order.getOrderNo());
		params.put("#2", order.getPickUpTime());
		// 查询用户电话号码
		String mobile = sysBuyerUserService.selectMemberMobile(order.getUserId());
		if (StringUtils.isNotBlank(mobile)) {
			this.sendSms(mobile, tradeMessageProperties.smsServiceStoreShipmentsStyle1, params);
		} else {
			logger.error("订单号：[" + order.getOrderNo() + "]的用户ID:[" + order.getUserId() + "]手机号码为空");
		}
	}
	// End 重构4.1 add by wusw

	@Override
	public void sendSmsAfterAcceptOrder(TradeOrder order) {
		Map<String, String> params = Maps.newHashMap();
		params.put("#1", order.getOrderNo());
		params.put("#2", order.getPickUpTime());
		// 查询用户电话号码
		String mobile = sysBuyerUserService.selectMemberMobile(order.getUserId());
		if (StringUtils.isNotBlank(mobile)) {
			this.sendSms(mobile, tradeMessageProperties.smsAcceptOrderStyle, params);
		} else {
			logger.error("订单号：[" + order.getOrderNo() + "]的用户ID:[" + order.getUserId() + "]手机号码为空");
		}
		
	}
}