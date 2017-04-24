package com.okdeer.mall.order.pay.callback;

import java.util.List;

import org.springframework.stereotype.Service;

import com.okdeer.api.pay.pay.dto.PayResponseDto;
import com.okdeer.mall.common.utils.HttpClientUtil;
import com.okdeer.mall.common.utils.Xml2JsonUtil;
import com.okdeer.mall.common.utils.security.MD5;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mcm.entity.SmsVO;

import net.sf.json.JSONObject;

@Service("trafficOrderPayHandler")
public class TrafficOrderPayHandler extends AbstractPhoneRechargePayHandler {

	@Override
	protected void processOrderItem(TradeOrder tradeOrder, PayResponseDto respDto) throws Exception {
		List<TradeOrderItem> tradeOrderItems = tradeOrderItemService.selectOrderItemByOrderId(tradeOrder.getId());
		if (tradeOrderItems.isEmpty()) {
			return;
		}
		TradeOrderItem tradeOrderItem = tradeOrderItems.get(0);
		String phoneno = tradeOrderItem.getRechargeMobile();
		String orderid = tradeOrder.getTradeNum();
		String pid = tradeOrderItem.getStoreSkuId();

		// 风控验证
//		if (isTrigger(tradeOrder, respDto, phoneno)) {
//			refunds(tradeOrder, tradeOrderItem);
//			return;
//		}

		// 流量充值
		int partnerNum = Integer.parseInt(partner);
		if (partnerNum == 1) {
			String sign = MD5.md5(openId + dataPlanKey + phoneno + pid + orderid);
			String url = dataplanOrderUrl + "?key=" + dataPlanKey + "&phone=" + phoneno + "&pid=" + pid + "&orderid="
					+ orderid + "&sign=" + sign;
			String resp = HttpClientUtil.get(url);
			JSONObject respJson = JSONObject.fromObject(resp);
			logger.info("************手机流量充值订单{}返回参数************{}", orderid, respJson);
			int errorCode = respJson.getInt("error_code");
			if (errorCode == 0) {
				logger.info("手机流量充值订单{}同步返回充值状态为充值中，修改订单状态为充值中！", orderid);
				// 修改订单状态
				updateTradeOrderStatus(tradeOrder);
			} else {
				// 充值聚合订单提交失败，走退款流程
				logger.info("手机流量充值订单{}同步返回错误码{}，创建流量充值失败订单！", orderid, errorCode);
				this.tradeOrderRefundsService.insertRechargeRefunds(tradeOrder);
			}
		} else if (partnerNum == 2) {
			// md5Str检验码的计算方法:
			// netType 为空的话，不参与md5验证，不为空的话参与MD5验证
			// 包体= userid + userpws + phoneno + perValue + flowValue + range +
			// effectStartTime + effectTime + netType+ sporderId
			String arr[] = pid.split("\\|");
			String perValue = arr[0];
			String flowValue = arr[1];
			String sign = MD5.md5(userid + userpws + phoneno + perValue + flowValue + range + effectStartTime
					+ effectTime + orderid + keyStr).toUpperCase();
			String url = "http://" + userid + ".api2.ofpay.com/flowOrder.do?userid=" + userid + "&userpws=" + userpws
					+ "&phoneno=" + phoneno + "&perValue=" + perValue + "&flowValue=" + flowValue + "&range=" + range
					+ "&effectStartTime=" + effectStartTime + "&effectTime=" + effectTime + "&sporderId=" + orderid
					+ "&md5Str=" + sign + "&version=" + version + "&retUrl=" + returl;
			
			
			String xml = HttpClientUtil.get(url, "GB2312");
			JSONObject respJson = Xml2JsonUtil.xml2Json(xml, "GB2312");
			JSONObject orderinfo = respJson.getJSONObject("orderinfo");
			
			logger.info("***********手机流量充值订单{},返回参数{}***************", orderid, orderinfo);
			int retcode = orderinfo.getInt("retcode");
			String userPhone = tradeOrder.getUserPhone();
			if (retcode == 1) {
				int gameState = orderinfo.getInt("game_state");
				if (gameState == 0) {
					// 充值订单请求成功
					logger.info("DATAPLAN===手机流量充值订单{}请求返回状态为充值中，修改订单状态为充值中！", orderid);
					updateTradeOrderStatus(tradeOrder, orderinfo.getString("orderid"));
				} else if (gameState == 9) {
					// 充值订单请求失败
					logger.info("DATAPLAN===手机流量充值订单{}请求同步返回状态为失败，创建充值退款单！", orderid);
					this.tradeOrderRefundsService.insertRechargeRefunds(tradeOrder);
				}
			} else if (retcode == 9999 || retcode == 105 || retcode == 334 || retcode == 1043) {
				/**
				 * 9999:未知错误
				 * 150:请求失败
				 * 334:订单生成超时
				 * 1043:支付超时，订单处理失败
				 * 充值请求返回这些状态，建议对订单进行手动查询
				 */
				String queryUrl = "http://" + userid + ".api2.ofpay.com/api/query.do?userid=" + userid + "&spbillid="
						+ orderid;
				String stateStr = HttpClientUtil.get(queryUrl);
				int state = Integer.parseInt(stateStr);
				if (state == 1) {
					// 充值成功
					logger.info("DATAPLAN==手机流量订单{}返回码{}，经过手动查询充值结果为充值成功，修改订单状态为充值成功！", orderid, retcode);
					tradeOrder.setStatus(OrderStatusEnum.HAS_BEEN_SIGNED);
					this.tradeOrderService.updateRechargeOrderByTradeNum(tradeOrder);

					// 发送提醒短信
					String content = successMsg;
					int idx = content.indexOf("#");
					content = content.replaceFirst(String.valueOf(content.charAt(idx)), userPhone);
					idx = content.indexOf("#");
					content = content.replaceFirst(String.valueOf(content.charAt(idx)), tradeOrderItem.getSkuName());

					SmsVO smsVo = createSmsVo(userPhone, content);
					this.smsService.sendSms(smsVo);
				} else if (state == 0) {
					// 充值中
					logger.info("DATAPLAN==手机话费订单{}返回码{}，经过手动查询充值结果为充值中，请继续等待。", orderid, retcode);
				} else if (state == 9) {
					// 失败，走退款流程， 创建退款单
					logger.info("DATAPLAN===手机话费订单{}返回码{}，经过手动查询充值结果为充值失败，创建话费充值失败退款！", orderid, retcode);
					this.tradeOrderRefundsService.insertRechargeRefunds(tradeOrder);

					// 发送提醒短信
					String content = failureMsg;
					int idx = content.indexOf("#");
					content = content.replaceFirst(String.valueOf(content.charAt(idx)), userPhone);
					idx = content.indexOf("#");
					content = content.replaceFirst(String.valueOf(content.charAt(idx)), tradeOrderItem.getSkuName());

					SmsVO smsVo = createSmsVo(userPhone, content);
					this.smsService.sendSms(smsVo);
				} else if (state == -1) {
					// 找不到此订单
					logger.warn("DATAPLAN===手机话费订单{}返回码{}，经过手动查询充值结果为找不到订单，请进入平台查询或者联系第三方欧飞客服进行核实", orderid, retcode);
				}
			} else {
				// 欧飞订单提交失败，走退款流程
				logger.info("DATAPLAN==OFPAY==手机流量充值订单{}请求充值返回码为：{}，创建充值退款单！", orderid, retcode);
				refunds(tradeOrder, tradeOrderItem);
			}
		}
	}
}
