
package com.okdeer.mall.order.builder;

import org.springframework.stereotype.Component;

import com.okdeer.api.pay.enums.RefundTypeEnum;
import com.okdeer.api.pay.pay.dto.PayRefundDto;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.mall.common.utils.TradeNumUtil;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.enums.OrderStatusEnum;

/**
 * ClassName: TradeOrderPayBuilder 
 * @Description: 订单支付构造器
 * @author zengjizu
 * @date 2017年12月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Component
public class TradeOrderPayBuilder {
	
	
	/**
	 * @Description: 创建退款信息
	 * @param order
	 * @return
	 * @author zengjizu
	 * @date 2017年12月8日
	 */
	public PayRefundDto buildPayRefundDto(TradeOrder order) {
		PayRefundDto payRefundDto = new PayRefundDto();
		if (order.getIsBreach() == WhetherEnum.whether) {
			// 如果订单需要支付违约金，则退款金额为：实付金额-收取的违约金
			payRefundDto.setTradeAmount(order.getActualAmount().subtract(order.getBreachMoney()));
		} else {
			payRefundDto.setTradeAmount(order.getActualAmount());
		}
		if (order.getStatus() == OrderStatusEnum.REFUSED || order.getStatus() == OrderStatusEnum.REFUSING) {
			// 拒收的订单不退款运费 退款金额=实付金额-实付运费。实付运费=运费金额-实际运费优惠金额
			payRefundDto.setTradeAmount(
					payRefundDto.getTradeAmount().subtract(order.getFare().subtract(order.getRealFarePreferential())));
		}
		payRefundDto.setServiceId(order.getId());
		payRefundDto.setServiceNo(order.getOrderNo());
		payRefundDto.setRemark(order.getOrderNo());
		payRefundDto.setRefundType(RefundTypeEnum.CANCEL_ORDER);
		payRefundDto.setTradeNum(order.getTradeNum());
		payRefundDto.setRefundNum(TradeNumUtil.getTradeNum());
		return payRefundDto;
	}

}
