
package com.okdeer.mall.order.api;

import static com.okdeer.common.consts.DescriptConstants.ORDER_CANCEL_ERROR;
import static com.okdeer.common.consts.DescriptConstants.ORDER_CANCEL_SUCCESS;
import static com.okdeer.common.consts.DescriptConstants.ORDER_NOT_EXSITS;
import static com.okdeer.common.consts.DescriptConstants.ORDER_STATUS_NOT_MATCHED;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.common.consts.DescriptConstants;
import com.okdeer.mall.order.dto.CancelOrderDto;
import com.okdeer.mall.order.dto.CancelOrderParamDto;
import com.okdeer.mall.order.dto.UserRefuseDto;
import com.okdeer.mall.order.dto.UserRefuseParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.enums.OrderCancelType;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.service.CancelOrderApi;
import com.okdeer.mall.order.service.CancelOrderService;
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
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.CancelOrderApi")
public class CancelOrderApiImpl implements CancelOrderApi {

	private static final Logger logger = LoggerFactory.getLogger(CancelOrderApiImpl.class);

	@Autowired
	private TradeOrderService tradeorderService;

	@Autowired
	private CancelOrderService cancelOrderService;

	@Override
	public CancelOrderDto cancelOrder(CancelOrderParamDto cancelOrderParamDto) {

		CancelOrderDto cancelOrderDto = new CancelOrderDto();
		try {
			TradeOrder tradeOrder = tradeorderService.selectById(cancelOrderParamDto.getOrderId());
			// 校验订单状态
			if (tradeOrder.getStatus() != OrderStatusEnum.UNPAID
					&& tradeOrder.getStatus() != OrderStatusEnum.DROPSHIPPING
					&& tradeOrder.getStatus() != OrderStatusEnum.WAIT_RECEIVE_ORDER) {
				// 判断状态如果不是等于待支付或者待发货、待接单
				cancelOrderDto.setStatus(1);
				cancelOrderDto.setMsg(ORDER_STATUS_NOT_MATCHED);
				return cancelOrderDto;
			}

			if (cancelOrderParamDto.getCancelType() == OrderCancelType.CANCEL_BY_BUYER
					&& !tradeOrder.getUserId().equals(cancelOrderParamDto.getUserId())) {
				// 如果是用户取消，判断是否是当前用户的订单
				cancelOrderDto.setStatus(1);
				cancelOrderDto.setMsg(ORDER_NOT_EXSITS);
				return cancelOrderDto;
			}

			boolean isBuyerOperate = false;
			if (cancelOrderParamDto.getCancelType() == OrderCancelType.CANCEL_BY_BUYER) {
				// 判断是否是用户取消
				isBuyerOperate = true;
			}

			tradeOrder.setCancelType(cancelOrderParamDto.getCancelType());
			tradeOrder.setReason(cancelOrderParamDto.getReason());
			tradeOrder.setUpdateTime(new Date());
			if (cancelOrderParamDto.getCancelType() != OrderCancelType.CANCEL_BY_BUYER) {
				// 如果不是用户取消，需要设置最后更新人
				tradeOrder.setUpdateUserId(cancelOrderParamDto.getUserId());
			}
			cancelOrderService.cancelOrder(tradeOrder, isBuyerOperate);
			cancelOrderDto.setStatus(0);
			cancelOrderDto.setMsg(ORDER_CANCEL_SUCCESS);
		} catch (Exception e) {
			logger.error(ORDER_CANCEL_ERROR, e);
			cancelOrderDto.setStatus(1);
			cancelOrderDto.setMsg(ORDER_CANCEL_ERROR);
		}
		return cancelOrderDto;
	}

	@Override
	public UserRefuseDto userRefuse(UserRefuseParamDto userRefuseParamDto) {
		
		UserRefuseDto userRefuseDto = new UserRefuseDto();
		try {
			// 根据订单ID查询订单信息
			TradeOrder tradeOrder = tradeorderService.selectById(userRefuseParamDto.getOrderId());
			tradeOrder.setUpdateTime(new Date());
			tradeOrder.setUpdateUserId(userRefuseParamDto.getUserId());
			tradeOrder.setStatus(OrderStatusEnum.REFUSED);
			tradeOrder.setReason(userRefuseParamDto.getReason());
			tradeOrder.setSellerId(userRefuseParamDto.getUserId());
			cancelOrderService.updateWithUserRefuse(tradeOrder);
			userRefuseDto.setStatus(0);
			userRefuseDto.setMsg("拒收成功");
		} catch (Exception e) {
			logger.error(DescriptConstants.SYS_ERROR, e);
			userRefuseDto.setStatus(1);
			userRefuseDto.setMsg(DescriptConstants.SYS_ERROR);
		}
		return userRefuseDto;
	}

	@Override
	public boolean isBreach(String orderId) throws Exception {
		Assert.hasText(orderId);
		return cancelOrderService.isBreach(orderId) ;
	}

}
