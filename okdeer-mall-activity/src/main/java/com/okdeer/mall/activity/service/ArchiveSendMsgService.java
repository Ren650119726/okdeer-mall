package com.okdeer.mall.activity.service;

import com.okdeer.archive.goods.dto.ActivityMessageParamDto;
import com.okdeer.archive.goods.dto.StoreMenuParamDto;
import com.okdeer.archive.store.dto.GoodsStoreLableParamDto;

/**
 * ClassName: ArchiveSendMsgService
 *
 * @author wangf01
 * @Description: archive-发送MQ消息-service
 * @date 2017年2月15日
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public interface ArchiveSendMsgService {

    /**
     * 发送信息同步商品到搜索引擎
     *
     * @param paramDto ActivityMessageParamDto
     * @param mqTag    String 消息tag(ELTopicTagConstants)
     * @throws Exception
     */
    void structureProducerELGoods(ActivityMessageParamDto paramDto, String mqTag) throws Exception;

    /**
     * 发送消息更新店铺菜单is_show_app状态
     *
     * @param paramDto StoreMenuParamDto
     * @param mqTag    String 消息tag(StoreMenuTopicTagConstants)
     * @throws Exception
     */
    void structureProducerStoreMenu(StoreMenuParamDto paramDto, String mqTag) throws Exception;

    /**
     * 发送消息更新店铺菜单商品数据
     *
     * @param paramDto StoreMenuParamDto
     * @param mqTag    String 消息tag(StoreMenuTopicTagConstants)
     * @throws Exception
     */
    void structureProducerStoreLable(GoodsStoreLableParamDto paramDto, String mqTag) throws Exception;
}
