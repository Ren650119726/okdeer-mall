/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @项目名称: okdeer-mall 
 * @文件名称: OperateFieldsSubscriber.java 
 * @Date: 2017年4月17日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
package com.okdeer.mall.operate.operatefields.mq;

import static com.okdeer.mall.operate.contants.OperateFieldContants.TAG_ADDEDIT_LOWPRICE_ACTIVITY;
import static com.okdeer.mall.operate.contants.OperateFieldContants.TAG_ADDEDIT_ONSALE_ACTIVITY;
import static com.okdeer.mall.operate.contants.OperateFieldContants.TAG_ADDEDIT_OPERATE_FIELD;
import static com.okdeer.mall.operate.contants.OperateFieldContants.TAG_CLOSED_LOWPRICE_ACTIVITY;
import static com.okdeer.mall.operate.contants.OperateFieldContants.TAG_CLOSED_ONSALE_ACTIVITY;
import static com.okdeer.mall.operate.contants.OperateFieldContants.TAG_EDIT_GOODS;
import static com.okdeer.mall.operate.contants.OperateFieldContants.TAG_GOODS_OFFSHELF;
import static com.okdeer.mall.operate.contants.OperateFieldContants.TAG_GOODS_ONSHELF;
import static com.okdeer.mall.operate.contants.OperateFieldContants.TAG_RANK_OPERATE_FIELD;
import static com.okdeer.mall.operate.contants.OperateFieldContants.TOPIC_OPERATE_FIELD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.annotation.RocketMQListener;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.mall.operate.dto.GoodsChangedMsgDto;
import com.okdeer.mall.operate.operatefields.service.OperateFieldsService;

/**
 * 订阅商品属性发生变化时出发的更新运营栏位消息
 * 
 * @pr yschome-mall
 * @author zhaoqc
 * @date 2017年4月17日 下午3:25:11
 * =================================================================================================
 *     Task ID            Date               Author           Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     V2.3.0          2017年4月17日                              zhaoqc             新建
 *     
 */
@Service
public class OperateFieldsSubscriber {
    /**
     * 日志管理类
     */
    private static final Logger logger = LoggerFactory.getLogger(OperateFieldsSubscriber.class);
    
    /**
     * reids接入
     */
    @Autowired
    private OperateFieldsService operateFieldsService;
    
    /**
     * 运营栏位消息订阅
     * 
     * @param enMessage
     * @return
     * @author zhaoqc
     * @date 2017-4-17
     */
    @RocketMQListener(topic = TOPIC_OPERATE_FIELD, tag = "*")
    public ConsumeConcurrentlyStatus subscribeMessage(MQMessage enMessage) {
        String tags =  enMessage.getTags();
        logger.info("APP首页运营栏位订阅消息TAG：{}", tags);
        try {
            ConsumeConcurrentlyStatus status = ConsumeConcurrentlyStatus.RECONSUME_LATER;
            switch (tags) {
            case TAG_EDIT_GOODS:
                status = editGoodsMsg(enMessage);
                break;
            case TAG_GOODS_ONSHELF:
                status = goodsOnShelfMsg(enMessage);
                break;
            case TAG_GOODS_OFFSHELF:
                status = goodsOffShelfMsg(enMessage);
                break;
            case TAG_ADDEDIT_ONSALE_ACTIVITY:
                status = onsaleActivityMsg(enMessage);
                break;
            case TAG_CLOSED_ONSALE_ACTIVITY:
                status = closeOnsaleActivityMsg(enMessage);
                break;
            case TAG_ADDEDIT_LOWPRICE_ACTIVITY:
                status = lowPriceActivityMsg(enMessage);
                break;
            case TAG_CLOSED_LOWPRICE_ACTIVITY:
                status = closeLowPriceActivityMsg(enMessage);
                break;
            case TAG_ADDEDIT_OPERATE_FIELD:
                status = addEditOperateField(enMessage);
              break;
            case TAG_RANK_OPERATE_FIELD:
                status = rankOperateField(enMessage);
              break;
            default:
                break;
            }
            return status;
        } catch (Exception e) {
            logger.error("新增商品时，消息处理失败：{}", JsonMapper.nonEmptyMapper().toJson(enMessage), e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
    
    private void initOperateField(GoodsChangedMsgDto msgDto) throws Exception {
        
        String storeId = msgDto.getStoreId();
        if(StringUtils.isNotEmpty(storeId)) {
            this.operateFieldsService.initStoreOperateFieldData(storeId);
        }
        
        String cityId = msgDto.getCityId();
        if(StringUtils.isNotEmpty(cityId)) {
            this.operateFieldsService.initCityOperateFieldData(cityId);
        }
    }
     
    /**
     * 编辑商品时的消息订阅
     * 
     * @param enMessage
     * @return
     * @author zhaoqc
     * @date 2017-4-17
     */
    public ConsumeConcurrentlyStatus editGoodsMsg(MQMessage enMessage) {
        GoodsChangedMsgDto msgDto = (GoodsChangedMsgDto)enMessage.getContent();
        logger.info("编辑商品时，消息处理：{}", JsonMapper.nonEmptyMapper().toJson(msgDto));
        try {
            initOperateField(msgDto);
            
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            logger.error("编辑商品时，消息处理失败：{}", JsonMapper.nonEmptyMapper().toJson(msgDto), e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
    
    
    /**
     * 商品上架时的消息订阅
     * 
     * @param enMessage
     * @return
     * @author zhaoqc
     * @date 2017-4-17
     */
    public ConsumeConcurrentlyStatus goodsOnShelfMsg(MQMessage enMessage) {
        GoodsChangedMsgDto msgDto = (GoodsChangedMsgDto)enMessage.getContent();
        logger.info("商品上架时，消息处理：{}", JsonMapper.nonEmptyMapper().toJson(msgDto));
        try {
            initOperateField(msgDto);
            
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            logger.error("商品上架时，消息处理失败：{}", JsonMapper.nonEmptyMapper().toJson(msgDto), e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
    
    /**
     * 商品下架时的消息订阅
     * 
     * @param enMessage
     * @return
     * @author zhaoqc
     * @date 2017-4-17
     */
    public ConsumeConcurrentlyStatus goodsOffShelfMsg(MQMessage enMessage) {
        GoodsChangedMsgDto msgDto = (GoodsChangedMsgDto)enMessage.getContent();
        logger.info("商品下架时，消息处理：{}", JsonMapper.nonEmptyMapper().toJson(msgDto));
        try {
            initOperateField(msgDto);
            
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            logger.error("商品下架时，消息处理失败：{}", JsonMapper.nonEmptyMapper().toJson(msgDto), e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
    
    /**
     * 新增、编辑商品特惠活动时的消息订阅
     * 
     * @param enMessage
     * @return
     * @author zhaoqc
     * @date 2017-4-17
     */
    public ConsumeConcurrentlyStatus onsaleActivityMsg(MQMessage enMessage) {
        GoodsChangedMsgDto msgDto = (GoodsChangedMsgDto)enMessage.getContent();
        logger.info("新增、编辑商品特惠活动时，消息处理：{}", JsonMapper.nonEmptyMapper().toJson(msgDto));
        try {
            initOperateField(msgDto);
            
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            logger.error("新增、编辑商品特惠活动时，消息处理失败：{}", JsonMapper.nonEmptyMapper().toJson(msgDto), e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
    
    /**
     * 关闭商品特惠活动时的消息订阅
     * 
     * @param enMessage
     * @return
     * @author zhaoqc
     * @date 2017-4-17
     */
    public ConsumeConcurrentlyStatus closeOnsaleActivityMsg(MQMessage enMessage) {
        GoodsChangedMsgDto msgDto = (GoodsChangedMsgDto)enMessage.getContent();
        logger.info("关闭商品特惠活动时，消息处理：{}", JsonMapper.nonEmptyMapper().toJson(msgDto));
        try {
            initOperateField(msgDto);
            
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            logger.error("关闭商品特惠活动时，消息处理失败：{}", JsonMapper.nonEmptyMapper().toJson(msgDto), e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
    
    /**
     * 新增编辑低价活动时的消息订阅
     * 
     * @param enMessage
     * @return
     * @author zhaoqc
     * @date 2017-4-17
     */
    public ConsumeConcurrentlyStatus lowPriceActivityMsg(MQMessage enMessage) {
        GoodsChangedMsgDto msgDto = (GoodsChangedMsgDto)enMessage.getContent();
        logger.info("新增编辑低价活动时，消息处理：{}", JsonMapper.nonEmptyMapper().toJson(msgDto));
        try {
            initOperateField(msgDto);
            
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            logger.error("新增编辑低价活动时，消息处理失败：{}", JsonMapper.nonEmptyMapper().toJson(msgDto), e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
    
    /**
     * 关闭低价活动时的消息订阅
     * 
     * @param enMessage
     * @return
     * @author zhaoqc
     * @date 2017-4-17
     */
    public ConsumeConcurrentlyStatus closeLowPriceActivityMsg(MQMessage enMessage) {
        GoodsChangedMsgDto msgDto = (GoodsChangedMsgDto)enMessage.getContent();
        logger.info("关闭低价活动时，消息处理：{}", JsonMapper.nonEmptyMapper().toJson(msgDto));
        try {
            initOperateField(msgDto);
            
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            logger.error("关闭低价活动时，消息处理失败：{}", JsonMapper.nonEmptyMapper().toJson(msgDto), e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }   
    
    /**
     * 运营栏位新增编辑时消息订阅
     * 
     * @param enMessage
     * @return
     * @author zhaoqc
     * @date 2017-4-21
     */
    public ConsumeConcurrentlyStatus addEditOperateField(MQMessage enMessage) {
        GoodsChangedMsgDto msgDto = (GoodsChangedMsgDto)enMessage.getContent();
        logger.info("新增编辑运营栏位时，消息处理：{}", JsonMapper.nonEmptyMapper().toJson(msgDto)); 
        try {
            initOperateField(msgDto);
            
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            logger.error("新增编辑运营栏位时，消息处理失败：{}", JsonMapper.nonEmptyMapper().toJson(msgDto), e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
    
    /**
     * 运营栏位位置上移下移时消息订阅
     * 
     * @param enMessage
     * @return
     * @author zhaoqc
     * @date 2017-4-21
     */
    public ConsumeConcurrentlyStatus rankOperateField(MQMessage enMessage) {
        GoodsChangedMsgDto msgDto = (GoodsChangedMsgDto)enMessage.getContent();
        logger.info("运营栏位排序变化时，消息处理：{}", JsonMapper.nonEmptyMapper().toJson(msgDto));
        try {
            initOperateField(msgDto);
            
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            logger.error("运营栏位排序变化时，消息处理失败：{}", JsonMapper.nonEmptyMapper().toJson(msgDto), e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
    
}
