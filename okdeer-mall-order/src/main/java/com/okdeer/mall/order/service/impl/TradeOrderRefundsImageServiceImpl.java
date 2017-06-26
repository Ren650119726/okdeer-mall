
package com.okdeer.mall.order.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.mall.order.entity.TradeOrderRefundsImage;
import com.okdeer.mall.order.mapper.TradeOrderRefundsImageMapper;
import com.okdeer.mall.order.service.TradeOrderRefundsImageService;

/**
 * ClassName: TradeOrderRefundsImageServiceImpl 
 * @Description: 订单图片
 * @author zengjizu
 * @date 2016年11月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     v1.2.0            2016-11-11          zengjz           重写取消订单代码
 *     V2.3 			 2017-04-22			 maojj			  取消时释放用户满减记录
 */
@Service
public class TradeOrderRefundsImageServiceImpl implements TradeOrderRefundsImageService {

	private static final Logger logger = LoggerFactory.getLogger(TradeOrderRefundsImageServiceImpl.class);

	@Autowired
	private TradeOrderRefundsImageMapper tradeOrderRefundsImageMapper;


	@Override
	public List<TradeOrderRefundsImage> findByRefundsId(String refundsId) {
		return tradeOrderRefundsImageMapper.findByRefundsId(refundsId);
	}
}