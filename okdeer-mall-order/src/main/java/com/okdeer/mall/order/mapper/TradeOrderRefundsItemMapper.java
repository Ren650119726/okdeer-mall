/**
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall
 * @File: TradeOrderRefundsItemMapper.java
 * @Date: 2016年03月31日
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.order.entity.TradeOrderRefundsItem;

/**
 * 退款单项dao
 *
 * @author wangfan
 * @project yschome-mall
 * @date 2016年03月31日
 */
public interface TradeOrderRefundsItemMapper {

    /**
     * 批量插入退款单项
     * @param list  退款单项集合
     * @return int
     */
    int insert(List<TradeOrderRefundsItem> list);
    
    void insertTradeOrderRefundsItem(TradeOrderRefundsItem item);
    
    List<TradeOrderRefundsItem> getTradeOrderRefundsItemByRefundsId(@Param("refundsId") String refundsId);
    
    /**
     * 根据订单号查询退单项
     * @param orderId
     * @return
     */
    List<TradeOrderRefundsItem> selectByOrderId(String orderId);
    
    /**
     * 根据订单项ID查询数据记录数
     *
     * @param orderItemId 订单项ID
     */
    int selectCountOrderItemId(String orderItemId);
}
