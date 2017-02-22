package com.okdeer.mall.order.service.impl;

import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.kafka.producer.KafkaProducer;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.service.TradeOrderSendMessageService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * ClassName: TradeOrderSendMessageServiceImpl 
 * @Description: 订单轨迹服务实现类
 * @author zhaoqc
 * @date 2017年2月18日
 *
 * =================================================================================================
 *     Task ID            Date               Author            Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *      友门鹿2.1         2017年2月18日                           zhaoqc        便利店订单状态发生改变时发送通知
 */
@Service
public class TradeOrderSendMessageServiceImpl implements TradeOrderSendMessageService {
    
    @Autowired
    private KafkaProducer kafkaProducer;
    
    @Value("${mcm.sys.code}")
    private String msgSysCode;

    @Value("${mcm.sys.token}")
    private String msgToken;
    
    //日志管理器
    private static final Logger LOGGER = LoggerFactory.getLogger(TradeOrderSendMessageServiceImpl.class);
    
    @Override
    public void tradeSendMessage(TradeOrder tradeOrder) {
        LOGGER.info("订单状态改变发送消息");
        
        //订单状态为卖家接单，发货，派送，签收和拒收的时候，发送通知
        OrderStatusEnum status = tradeOrder.getStatus();
       
        //时间格式器
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        JSONObject msgObj = new JSONObject();
        //主键
        msgObj.put("id", UuidUtils.getUuid());
        //发送者ID
        msgObj.put("sendUserId", "8a94e42850f676fb0150f676fb140000");
        //业务类型
        msgObj.put("serviceTypes", new int[]{2});
        //消息参照业务ID
        msgObj.put("serviceFkId", tradeOrder.getId());
        //应用类型 0：用户APP
        msgObj.put("appType", 0);
        //在消息中心注册的系统编码
        msgObj.put("sysCode", msgSysCode);
        //在消息中心注册的token
        msgObj.put("token", msgToken);
        //是否使用模板方式0不是，1是
        msgObj.put("isUseTemplate", 0);
        //是否定时发送 0不是 1是
        msgObj.put("isTiming", 0);
        //消息详情类型
        msgObj.put("msgDetailType", 1);
        //备注
        msgObj.put("remark", "");
        //消息类型  0透传消息  1通知
        msgObj.put("msgType", 0);
        //表示推送的消息是系统消息 还是物业消息
        msgObj.put("userTypeSource", "systemCode");
        //推送类型
        msgObj.put("actualType", "getui");
        
        //发送对象列表userList
        JSONArray userList = new JSONArray();
        JSONObject userObj = new JSONObject();
        //用户Id
        userObj.put("userId", tradeOrder.getUserId());
        //用户手机号码
        userObj.put("mobile", tradeOrder.getUserPhone());
        userList.add(userObj);
        
        msgObj.put("userList", userList);
        
        //根据订单的不同状态组织不同的消息通知内容
        if(status == OrderStatusEnum.DROPSHIPPING) {
            //卖家已接单（待发货）
            msgObj.put("msgNotifyContent", "卖家已接单");
            msgObj.put("msgDetailContent", "卖家已接单   " + sdf.format(tradeOrder.getUpdateTime()));
        } else if (status == OrderStatusEnum.TO_BE_SIGNED) {
            //卖家已发货
            msgObj.put("msgNotifyContent", "卖家已发货");
            msgObj.put("msgDetailContent", "卖家已发货   " + sdf.format(tradeOrder.getUpdateTime()));
        } else if (status == OrderStatusEnum.CANCELED) {
            //已取消
            msgObj.put("msgNotifyContent", "订单已取消");
            msgObj.put("msgDetailContent", "订单已取消   " + sdf.format(tradeOrder.getUpdateTime()));
        } else if (status == OrderStatusEnum.REFUSED) {
            //已拒收
            msgObj.put("msgNotifyContent", "买家拒收");
            msgObj.put("msgDetailContent", "买家拒收   " + sdf.format(tradeOrder.getUpdateTime()));
        } else if (status == OrderStatusEnum.HAS_BEEN_SIGNED) {
            //订单完成
            msgObj.put("msgNotifyContent", "买家已签收");
            msgObj.put("msgDetailContent", "买家已签收   " + sdf.format(tradeOrder.getUpdateTime()));
        }
        LOGGER.info("发送消息体为：{}", msgObj.toString());
        
        //消息发送
        kafkaProducer.send(msgObj.toString());
    }

}
