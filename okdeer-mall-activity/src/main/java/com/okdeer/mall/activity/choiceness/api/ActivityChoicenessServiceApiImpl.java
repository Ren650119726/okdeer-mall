package com.okdeer.mall.activity.choiceness.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.rocketmq.common.message.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.okdeer.archive.goods.dto.ActivityMessageParamDto;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.mall.activity.choiceness.entity.ActivityChoiceness;
import com.okdeer.mall.activity.choiceness.service.ActivityChoicenessService;
import com.okdeer.mall.activity.choiceness.service.ActivityChoicenessServiceApi;
import com.okdeer.mall.activity.choiceness.vo.ActivityChoicenessFilterVo;
import com.okdeer.mall.activity.choiceness.vo.ActivityChoicenessListPageVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.okdeer.common.consts.ELTopicTagConstants.TAG_GOODS_EL_UPDATE;
import static com.okdeer.common.consts.ELTopicTagConstants.TOPIC_GOODS_SYNC_EL;

/**
 * @author wangf01
 * @version 1.0.0
 * @DESC:精选服务-api-impl
 * @date 2017-1-12
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * <p>
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.choiceness.service.ActivityChoicenessServiceApi")
public class ActivityChoicenessServiceApiImpl implements ActivityChoicenessServiceApi {

    /**
     * 注入精选服务-service
     */
    private ActivityChoicenessService activityChoicenessService;

    /**
     * mq注入
     */
    @Autowired
    private RocketMQProducer rocketMQProducer;

    @Override
    public PageUtils<ActivityChoicenessListPageVo> findChoicenessListPageByFilter(ActivityChoicenessFilterVo queryFilterVo, Integer pageNumber, Integer pageSize) throws Exception {
        return activityChoicenessService.findChoicenessListPageByFilter(queryFilterVo, pageNumber, pageSize);
    }

    @Override
    public List<String> addByBatch(List<String> storeSkuIds) throws Exception {
        List<String> ids = activityChoicenessService.addByBatch(storeSkuIds);
        if (CollectionUtils.isNotEmpty(ids)) {
            structureProducer(ids);
        }
        return ids;
    }

    @Override
    public List<String> deleteByIds(List<String> choicenessIds) throws Exception {
        List<String> ids = activityChoicenessService.deleteByIds(choicenessIds);
        if (CollectionUtils.isNotEmpty(ids)) {
            structureProducer(ids);
        }
        return ids;
    }

    @Override
    public ActivityChoiceness findById(String choicenessId) throws Exception {
        return activityChoicenessService.findById(choicenessId);
    }

    @Override
    public List<String> updateChoicenessStatus(String activityId, String sortValue) throws Exception {
        List<String> ids = activityChoicenessService.updateChoicenessStatus(activityId, sortValue);
        if (CollectionUtils.isNotEmpty(ids)) {
            structureProducer(ids);
        }
        return ids;
    }

    @Override
    public Integer findCountBySkuIds(List<String> skuIds) throws Exception {
        return activityChoicenessService.findCountBySkuIds(skuIds);
    }

    @Override
    public Integer deleteBySkuIds(List<String> goodsStoreSkuIds) throws ServiceException {
        return activityChoicenessService.deleteBySkuIds(goodsStoreSkuIds);
    }

    /**
     * 发送消息同步数据到搜索引擎执行
     *
     * @param skuIds List<String>
     * @throws Exception
     */
    private void structureProducer(List<String> skuIds) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ActivityMessageParamDto paramDto = new ActivityMessageParamDto();
        paramDto.setSkuIds(skuIds);
        String json = mapper.writeValueAsString(paramDto);
        Message msg = new Message(TOPIC_GOODS_SYNC_EL, TAG_GOODS_EL_UPDATE, json.getBytes(Charsets.UTF_8));
        rocketMQProducer.send(msg);
    }
}
