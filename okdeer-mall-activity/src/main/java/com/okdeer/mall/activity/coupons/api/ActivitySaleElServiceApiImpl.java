
package com.okdeer.mall.activity.coupons.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.rocketmq.common.message.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.okdeer.archive.goods.dto.ActivityMessageParamDto;
import com.okdeer.archive.goods.dto.StoreMenuParamDto;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoodsBo;
import com.okdeer.mall.activity.coupons.service.ActivitySaleELServiceApi;
import com.okdeer.mall.activity.coupons.service.ActivitySaleGoodsServiceApi;
import com.okdeer.mall.activity.coupons.service.ActivitySaleService;
import com.okdeer.mall.activity.dto.ActivitySaleGoodsParamDto;
import com.okdeer.mall.activity.service.ArchiveSendMsgService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.okdeer.common.consts.ELTopicTagConstants.*;
import static com.okdeer.common.consts.StoreMenuTopicTagConstants.TAG_STORE_MENU_UPDATE;
import static com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum.LOW_PRICE;

/**
 * 
 * ClassName: ActivitySaleElServiceApiImpl 
 * @Description: 
 * @author tangy
 * @date 2017年2月21日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     V2.1.0          2017年2月21日                               tangy           增量添加活动库存     
 *     V2.1.0          2017年2月22日                               tangy           库存变更通知搜索引擎    
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivitySaleELServiceApi")
public class ActivitySaleElServiceApiImpl implements ActivitySaleELServiceApi {

    private static final Logger logger = LoggerFactory.getLogger(ActivitySaleElServiceApiImpl.class);

    /**
     * mq注入
     */
    @Autowired
    private RocketMQProducer rocketMQProducer;

    @Autowired
    private ActivitySaleService activitySaleService;

    @Autowired
    private ActivitySaleGoodsServiceApi activitySaleGoodsServiceApi;
    
    /**
     * 消息管理-service
     */
    @Autowired
    private ArchiveSendMsgService archiveSendMsgService;

    @Override
    public void save(ActivitySale activitySale, List<ActivitySaleGoods> asgList) throws Exception {
        activitySaleService.save(activitySale, asgList);
        if (activitySale.getType() == LOW_PRICE) {
            List<String> list = asgList.stream().map(e -> e.getStoreSkuId()).collect(Collectors.toList());
            ActivityMessageParamDto paramDto = new ActivityMessageParamDto();
            paramDto.setActivityId(activitySale.getId());
            paramDto.setSkuIds(list);
            paramDto.setUpdateStatus(String.valueOf(0));
            structureProducer(paramDto, TAG_LOWPRICE_EL_UPDATE);
        }
    }

    @Override
    public void update(ActivitySale ActivitySale, List<ActivitySaleGoods> asgList) throws Exception {
        activitySaleService.update(ActivitySale, asgList);
        List<String> list = asgList.stream().map(e -> e.getStoreSkuId()).collect(Collectors.toList());
        ActivityMessageParamDto paramDto = new ActivityMessageParamDto();
        paramDto.setActivityId(ActivitySale.getId());
        paramDto.setSkuIds(list);
        paramDto.setUpdateStatus(String.valueOf(0));
        structureProducer(paramDto, TAG_LOWPRICE_EL_UPDATE);
    }

    @Override
    public void updateBatchStatus(List<String> ids, int status, String storeId, String createUserId,
                                  Integer activityType) throws Exception {
        activitySaleService.updateBatchStatus(ids, status, storeId, createUserId, activityType);
        for (String id : ids) {
            ActivitySaleGoodsParamDto param = new ActivitySaleGoodsParamDto();
            param.setActivityId(id);
            List<ActivitySaleGoodsBo> goodsBoList = activitySaleGoodsServiceApi.findSaleGoodsByParams(param);
            if (CollectionUtils.isNotEmpty(goodsBoList)) {
                ActivityMessageParamDto paramDto = new ActivityMessageParamDto();
                paramDto.setActivityId(id);
                paramDto.setUpdateStatus(String.valueOf(1));
                List<String> skuIds = goodsBoList.stream().map(m -> m.getStoreSkuId()).collect(Collectors.toList());
                paramDto.setSkuIds(skuIds);
                // 5:特惠 7:低价
                switch (activityType) {
                    case 5:
                        structureProducer(paramDto, TAG_SALE_EL_UPDATE);
                        break;
                    case 7:
                        structureProducer(paramDto, TAG_LOWPRICE_EL_UPDATE);
                        break;
                }
            }
        }
        StoreMenuParamDto paramDto = new StoreMenuParamDto();
        paramDto.setStoreId(storeId);
        archiveSendMsgService.structureProducerStoreMenu(paramDto, TAG_STORE_MENU_UPDATE);
    }

    @Override
    public void deleteActivitySaleGoods(String storeId, String createUserId, String activitySaleGoodsId,
                                        String goodsStoreSkuId) throws Exception {
        activitySaleService.deleteActivitySaleGoods(storeId, createUserId, activitySaleGoodsId, goodsStoreSkuId);
        // 发送消息，同步数据到搜索引擎
        List<String> list = Arrays.asList(goodsStoreSkuId);
        ActivityMessageParamDto paramDto = new ActivityMessageParamDto();
        paramDto.setUpdateStatus(String.valueOf(0));
        paramDto.setSkuIds(list);
        structureProducer(paramDto, TAG_SALE_EL_DEL);
        
        StoreMenuParamDto menuParamDto = new StoreMenuParamDto();
        menuParamDto.setStoreId(storeId);
        archiveSendMsgService.structureProducerStoreMenu(menuParamDto,TAG_STORE_MENU_UPDATE);
    }

    /**
     * 发送消息同步数据到搜索引擎执行
     *
     * @param paramDto ActivityMessageParamDto
     * @param tag      String
     * @throws Exception
     */
    private void structureProducer(ActivityMessageParamDto paramDto, String tag) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(paramDto);
        Message msg = new Message(TOPIC_GOODS_SYNC_EL, tag, json.getBytes(Charsets.UTF_8));
        rocketMQProducer.send(msg);
    }
    
    @Override
    public void updateSaleStock(ActivitySale activitySale, ActivitySaleGoods activitySaleGoods) throws Exception {
    	activitySaleService.updateSaleStock(activitySale, activitySaleGoods);
    	//同步库存到搜索引擎
    	ActivityMessageParamDto paramDto = new ActivityMessageParamDto();
    	List<String> skuIdList = Arrays.asList(activitySaleGoods.getGoodsSkuId());
    	paramDto.setSkuIds(skuIdList);
    	archiveSendMsgService.structureProducerELGoods(paramDto, TAG_STOCK_EL_UPDATE);
    }
}
