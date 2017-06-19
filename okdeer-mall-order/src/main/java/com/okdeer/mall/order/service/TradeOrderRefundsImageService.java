package com.okdeer.mall.order.service;

import java.util.List;
import com.okdeer.mall.order.entity.TradeOrderRefundsImage;

/**
 * 
 * 
 * @pr mall
 * @desc 退款图片service
 * @author chenwj
 * @date 2016年4月1日 下午4:01:09
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 */
public interface TradeOrderRefundsImageService {

	List<TradeOrderRefundsImage> findByRefundsId(String refundsId);

}