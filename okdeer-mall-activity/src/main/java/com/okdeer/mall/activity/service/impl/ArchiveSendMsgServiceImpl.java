package com.okdeer.mall.activity.service.impl;

import com.alibaba.rocketmq.common.message.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.okdeer.archive.goods.dto.ActivityMessageParamDto;
import com.okdeer.archive.goods.dto.StoreMenuParamDto;
import com.okdeer.archive.store.dto.GoodsStoreLableParamDto;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.mall.activity.service.ArchiveSendMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.okdeer.common.consts.ELTopicTagConstants.TOPIC_GOODS_SYNC_EL;
import static com.okdeer.common.consts.StoreMenuTopicTagConstants.TOPIC_STORE_MENU;

/**
 * ClassName: ArchiveSendMsgServiceImpl
 *
 * @author wangf01
 * @Description: archive-发送MQ消息-service-impl
 * @date 2017年2月15日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
@Service
public class ArchiveSendMsgServiceImpl implements ArchiveSendMsgService {

    /**
     * mq注入
     */
    @Autowired
    private RocketMQProducer rocketMQProducer;

    @Override
    public void structureProducerELGoods(ActivityMessageParamDto paramDto, String mqTag) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(paramDto);
        Message msg = new Message(TOPIC_GOODS_SYNC_EL, mqTag, json.getBytes(Charsets.UTF_8));
        rocketMQProducer.send(msg);
    }

    @Override
    public void structureProducerStoreMenu(StoreMenuParamDto paramDto, String mqTag) throws Exception {
        if (paramDto != null && paramDto.getStoreId() != null && !paramDto.getStoreId().trim().equals("")) {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(paramDto);
            Message msg = new Message(TOPIC_STORE_MENU, mqTag, json.getBytes(Charsets.UTF_8));
            rocketMQProducer.send(msg, paramDto.getStoreId());
        }
    }

    @Override
    public void structureProducerStoreLable(GoodsStoreLableParamDto paramDto, String mqTag) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(paramDto);
        Message msg = new Message(TOPIC_GOODS_SYNC_EL, mqTag, json.getBytes(Charsets.UTF_8));
        rocketMQProducer.send(msg, paramDto.getLabelId());
    }
}
