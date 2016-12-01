/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * TradeOrderRefundsItemDetailMapper.java
 * @Date 2016-11-28 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.order.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.order.entity.TradeOrderRefundsItemDetail;

/**
 * ClassName: TradeOrderRefundsItemDetailMapper 
 * @Description: 退款单项明细mapper
 * @author zengjizu
 * @date 2016年11月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface TradeOrderRefundsItemDetailMapper extends IBaseMapper {

	void batchAdd(List<TradeOrderRefundsItemDetail> tradeOrderRefundsItemDetailList);

}