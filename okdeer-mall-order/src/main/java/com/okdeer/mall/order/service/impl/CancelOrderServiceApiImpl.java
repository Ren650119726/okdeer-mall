
package com.okdeer.mall.order.service.impl;

import static com.okdeer.common.consts.DescriptConstants.*;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.order.dto.CancelOrderReqDto;
import com.okdeer.mall.order.dto.CancelOrderRespDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.enums.OrderCancelType;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.service.CancelOrderService;
import com.okdeer.mall.order.service.CancelOrderServiceApi;
import com.okdeer.mall.order.service.TradeOrderService;

/**
 * ClassName: CancelOrderServiceApiImpl 
 * @Description: 取消订单服务api实现类
 * @author zengjizu
 * @date 2016年11月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *    v1.2.0            2016-11-10          zengjz            取消订单服务实现
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.CancelOrderServiceApi")
public class CancelOrderServiceApiImpl implements CancelOrderServiceApi {

	private static final Logger logger = LoggerFactory.getLogger(CancelOrderServiceApiImpl.class);

	@Autowired
	private TradeOrderService tradeorderService;

	@Autowired
	private CancelOrderService cancelOrderService;

	@Override
	public CancelOrderRespDto cancelOrder(CancelOrderReqDto cancelOrderReqDto) {

		CancelOrderRespDto cancelOrderRespDto = new CancelOrderRespDto();
		try {
			TradeOrder tradeOrder = tradeorderService.selectById(cancelOrderReqDto.getOrderId());
			
			//校验订单状态
			if (tradeOrder.getStatus() != OrderStatusEnum.UNPAID && tradeOrder.getStatus() != OrderStatusEnum.DROPSHIPPING && tradeOrder.getStatus() != OrderStatusEnum.WAIT_RECEIVE_ORDER) {
				//判断状态如果不是等于待支付或者待发货、待接单
				cancelOrderRespDto.setStatus(1);
				cancelOrderRespDto.setMsg(ORDER_STATUS_NOT_MATCHED);
				return cancelOrderRespDto;
			}
			
			if (cancelOrderReqDto.getCancelType() == OrderCancelType.CANCEL_BY_BUYER && !tradeOrder.getUserId().equals(cancelOrderReqDto.getUserId())) {
				//如果是用户取消，判断是否是当前用户的订单
				cancelOrderRespDto.setStatus(1);
				cancelOrderRespDto.setMsg(ORDER_NOT_EXSITS);
				return cancelOrderRespDto;
			}
			
			boolean isBuyerOperate = false;
			if (cancelOrderReqDto.getCancelType() == OrderCancelType.CANCEL_BY_BUYER) {
				// 判断是否是用户取消
				isBuyerOperate = true;
			}

			tradeOrder.setCancelType(cancelOrderReqDto.getCancelType());
			tradeOrder.setReason(cancelOrderReqDto.getReason());
			tradeOrder.setUpdateTime(new Date());
			if (cancelOrderReqDto.getCancelType() != OrderCancelType.CANCEL_BY_BUYER) {
				// 如果不是用户取消，需要设置最后更新人
				tradeOrder.setUpdateUserId(cancelOrderReqDto.getUserId());
			}
			cancelOrderService.cancelOrder(tradeOrder, isBuyerOperate);
			cancelOrderRespDto.setStatus(0);
			cancelOrderRespDto.setMsg(ORDER_CANCEL_SUCCESS);
		} catch (Exception e) {
			logger.error(ORDER_CANCEL_ERROR, e);
			cancelOrderRespDto.setStatus(1);
			cancelOrderRespDto.setMsg(ORDER_CANCEL_ERROR);
		}
		return cancelOrderRespDto;
	}

}
