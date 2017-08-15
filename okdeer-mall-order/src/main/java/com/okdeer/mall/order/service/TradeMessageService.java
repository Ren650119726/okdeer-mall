/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @项目名称: yschome-mall 
 * @文件名称: TradeMessageService.java 
 * @Date: 2016年4月27日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
package com.okdeer.mall.order.service;

import java.util.Map;

import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.SendMsgType;
import com.okdeer.mall.order.vo.SendMsgParamVo;

/**
 * 消息推送
 * @pr yschome-mall
 * @author guocp
 * @date 2016年4月27日 下午2:17:42
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *    重构4.1             2016-7-15            wusw               添加服务店派单短信
 */
public interface TradeMessageService {


	/**
	 * 订单消息推送 --POS</p>
	 * 
	 * @author zengj
	 * @param sendMsgParamVo 消息推送参数
	 * @param sendMsgType 推送消息类型
	 * @throws Exception
	 */
	public void sendPosMessage(SendMsgParamVo sendMsgParamVo, SendMsgType sendMsgType) throws Exception;

	/**
	 * 商品版App消息推送 --商家版APP</p>
	 * 
	 * @author zengj
	 * @param sendMsgParamVo 消息推送参数
	 * @param sendMsgType 推送消息类型
	 * @throws Exception
	 */
	public void sendSellerAppMessage(SendMsgParamVo sendMsgParamVo, SendMsgType sendMsgType) throws Exception;

	/**
	 * 发送短信
	 */
	void sendSms(String mobile, String content, Map<String, String> param) throws Exception;

	/**
	 * 保存商家中心消息
	 * @author zengj
	 * @param tradeOrder
	 * @param sendMsgType 消息类型
	 */
	void saveSysMsg(TradeOrder tradeOrder, SendMsgType sendMsgType);

	/**
	 * 点击发货时发送短信
	 * @author zengj
	 */
	void sendSmsByShipments(TradeOrder order);

	/**
	 * 取消订单发送短信 
	 * @param order 订单详情
	 */
	void sendSmsByCancel(TradeOrder order, OrderStatusEnum status);

	/**
	 * 商家同意退款发送短信
	 */
	void sendSmsByAgreePay(TradeOrderRefunds order, PayWayEnum payWay);

	/**
	 * 客服处理友门鹿退款
	 */
	void sendSmsByYschomePay(TradeOrderRefunds refunds);

	/**
	 * 下单时发送短信
	 * @author zengj
	 * @param tradeOrder
	 * @throws Exception 
	 */
	public void sendSmsByCreateOrder(TradeOrder tradeOrder) throws Exception;
	
	// Begin 重构4.1  add by wusw
	/**
	 * 
	 * @Description: 点击派单时发送短信（服务店）
	 * @param order   
	 * @return void  
	 * @author wusw
	 * @date 2016年7月15日
	 */
	void sendSmsByServiceStoreShipments(TradeOrder order);
	// End 重构4.1 add by wusw
	
	// Begin V1.2 added by maojj 2016-12-02
	/**
	 * @Description: 接单成功发送通知短信（服务店）
	 * @param order   
	 * @author maojj
	 * @date 2016年12月2日
	 */
	void sendSmsAfterAcceptOrder(TradeOrder order);
	// End V1.2 added by maojj 2016-12-02
}
