package com.okdeer.mall.order.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.service.TradeOrderSendMessageService;
import com.okdeer.mcm.constant.MsgConstant;
import com.okdeer.mcm.dto.PushMsgDto;
import com.okdeer.mcm.dto.PushUserDto;

/**
 * ClassName: TradeOrderSendMessageServiceImpl 
 * @Description: 订单轨迹服务实现类
 * @author zhaoqc
 * @date 2017年2月18日
 *
 * =================================================================================================
 *     Task ID            Date               Author            Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     友门鹿2.1         2017年2月18日                            zhaoqc         便利店订单状态发生改变时发送通知
 */
@Service
public class TradeOrderSendMessageServiceImpl implements TradeOrderSendMessageService {
    
    @Value("${mcm.sys.code}")
    private String msgSysCode;

	private static final String TOPIC = "topic_mcm_msg";
    
	@Autowired
	private RocketMQProducer rocketMQProducer;
    
    @Autowired
    private TradeOrderMapper tradeOrderMapper;
    
    //日志管理器
    private static final Logger LOGGER = LoggerFactory.getLogger(TradeOrderSendMessageServiceImpl.class);
    
    @Override
    public void tradeSendMessage(TradeOrder tradeOrder, TradeOrderRefunds orderRefunds) {
        LOGGER.info("订单状态改变发送消息");
        
        PushMsgDto msgDto = new PushMsgDto();
        if(tradeOrder != null) {
            //订单状态为卖家接单，发货，派送，签收和拒收的时候，发送通知
            OrderStatusEnum status = tradeOrder.getStatus();
            
            //消息参照业务ID
            msgDto.setServiceFkId(tradeOrder.getId());
            //发送对象列表userList
            List<PushUserDto> userList = new ArrayList<PushUserDto>();
            PushUserDto userDto = new PushUserDto();
            //用户Id
            userDto.setUserId(tradeOrder.getUserId());
            //用户手机号码
            userDto.setMobile(tradeOrder.getUserPhone());
            userList.add(userDto);
            msgDto.setUserList(userList);
            
            //根据订单的不同状态组织不同的消息通知内容
            if(status == OrderStatusEnum.DROPSHIPPING) {
                //卖家已接单（待发货）
                msgDto.setMsgNotifyContent("卖家已接单");
                msgDto.setMsgDetailContent("卖家已接单   " + DateUtils.formatDateTime(tradeOrder.getUpdateTime()));          
            } else if (status == OrderStatusEnum.TO_BE_SIGNED) {
                //卖家已发货
                msgDto.setMsgNotifyContent("卖家已发货");
                msgDto.setMsgDetailContent("卖家已发货   " + DateUtils.formatDateTime(tradeOrder.getUpdateTime()));
            } else if (status == OrderStatusEnum.CANCELED) {
                //已取消
                msgDto.setMsgNotifyContent("订单已取消");
                msgDto.setMsgDetailContent("订单已取消   " + DateUtils.formatDateTime(tradeOrder.getUpdateTime()));
            } else if (status == OrderStatusEnum.HAS_BEEN_SIGNED) {
                //订单完成
                OrderStatusEnum currentStatus = tradeOrder.getCurrentStatus();
                if(currentStatus != null && currentStatus != tradeOrder.getStatus()) {
                    msgDto.setMsgNotifyContent("买家已签收");
                    msgDto.setMsgDetailContent("买家已签收   " + DateUtils.formatDateTime(tradeOrder.getUpdateTime()));
                } else {
                    return;
                }
            } else {
                return;
            }
        } 
        
        if (orderRefunds != null) {
            RefundsStatusEnum refudnsStatus = orderRefunds.getRefundsStatus();
            
            //消息参照业务ID
            msgDto.setServiceFkId(orderRefunds.getOrderId());
            //发送对象列表userList
            List<PushUserDto> userList = new ArrayList<PushUserDto>();
            PushUserDto userDto = new PushUserDto();
            //用户Id
            userDto.setUserId(orderRefunds.getUserId());
            //用户手机号码
            TradeOrder order = null;
            try {
                order = this.tradeOrderMapper.selectByPrimaryKey(orderRefunds.getOrderId());
            } catch (Exception e) {
                LOGGER.error("根据交易流水号查询订单异常", e);
            }
            
            if(order != null) {
                userDto.setMobile(order.getUserPhone());
            } else {
                userDto.setMobile("");
            }
            userList.add(userDto);
            msgDto.setUserList(userList);
            
            if (refudnsStatus == RefundsStatusEnum.WAIT_SELLER_VERIFY) {
                //用户申请退款
                msgDto.setMsgNotifyContent("订单退款中");
                msgDto.setMsgDetailContent("订单退款中   " + DateUtils.formatDateTime(orderRefunds.getUpdateTime()));
            } else if(refudnsStatus == RefundsStatusEnum.REFUND_SUCCESS) {
                //卖家退款成功
                msgDto.setMsgNotifyContent("退款成功");
                msgDto.setMsgDetailContent("退款成功   " + DateUtils.formatDateTime(orderRefunds.getUpdateTime()));
            } else {
                return;
            }
        }
        
        //主键
        msgDto.setId(UuidUtils.getUuid());
        //发送者ID
        msgDto.setSendUserId("8a94e42850f676fb0150f676fb140000");
        //业务类型
        msgDto.setServiceTypes(new Integer[] {2});
        //应用类型 0：用户APP
        msgDto.setAppType(0);
        //在消息中心注册的系统编码
        msgDto.setSysCode(msgSysCode);
<<<<<<< HEAD
=======
        //在消息中心注册的token
//        msgDto.setToken(msgToken);
>>>>>>> refs/remotes/upstream/master_V2.6.0
        //是否使用模板方式0不是，1是
        msgDto.setIsUseTemplate(0);
<<<<<<< HEAD
=======
        //是否定时发送 0不是 1是
//        msgDto.setIsTiming(0);
>>>>>>> refs/remotes/upstream/master_V2.6.0
        //消息详情类型
        msgDto.setMsgDetailType(1);
<<<<<<< HEAD
=======
        //备注
//        msgDto.setRemark("");
>>>>>>> refs/remotes/upstream/master_V2.6.0
        //消息类型  0透传消息  1通知
        msgDto.setMsgType(MsgConstant.MsgType.NO_PUSH);
<<<<<<< HEAD
=======
        //表示推送的消息是系统消息 还是物业消息
//        msgDto.setUserTypeSource("systemCode");
        //推送类型
//        msgDto.setActualType("getui");
>>>>>>> refs/remotes/upstream/master_V2.6.0
        //消息发送
        try {
			sendMessage(msgDto);
		} catch (Exception e) {
			LOGGER.error("发送消息异常：{}", JsonMapper.nonDefaultMapper().toJson(msgDto),e);
		}
    }
    
	private void sendMessage(Object entity) throws Exception {
		MQMessage<String> anMessage = new MQMessage<String>(TOPIC, JsonMapper.nonDefaultMapper().toJson(entity));
		rocketMQProducer.sendMessage(anMessage);
	}
    
}
