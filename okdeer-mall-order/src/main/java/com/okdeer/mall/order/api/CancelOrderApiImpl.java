
package com.okdeer.mall.order.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.common.consts.DescriptConstants;
import com.okdeer.mall.ele.service.ExpressService;
import com.okdeer.mall.order.bo.TradeOrderContext;
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
import com.okdeer.mall.order.service.TradeorderProcessLister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;

import static com.okdeer.common.consts.DescriptConstants.*;

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
	
	@Autowired
	@Qualifier(value="jxcSynTradeorderProcessLister")
	private TradeorderProcessLister tradeorderProcessLister;

	/**
	 * 注入配送-service
	 */
	@Autowired
	private ExpressService expressService;

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
			
			//add by  zhangkeneng  和左文明对接丢消息
			TradeOrderContext tradeOrderContext = new TradeOrderContext();
			tradeOrderContext.setTradeOrder(tradeOrder);
			tradeOrderContext.setTradeOrderPay(tradeOrder.getTradeOrderPay());
			tradeOrderContext.setItemList(tradeOrder.getTradeOrderItem());
			tradeOrderContext.setTradeOrderLogistics(tradeOrder.getTradeOrderLogistics());
			tradeorderProcessLister.tradeOrderStatusChange(tradeOrderContext);

			// begin V2.5.0 add by wangf01 20170629
			expressService.cancelExpressOrder(tradeOrder.getOrderNo());
			// end V2.5.0 add by wangf01 20170629
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

	/**
	 * @Description: 取消订单
	 * @param order 订单
	 * @author tuzhd
	 * @throws ServiceException 
	 * @date 2016年11月10日
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean cancelOrder(TradeOrder tradeOrder, boolean isBuyerOperate) throws Exception {
		return cancelOrderService.cancelOrder(tradeOrder, isBuyerOperate);
	}
}
