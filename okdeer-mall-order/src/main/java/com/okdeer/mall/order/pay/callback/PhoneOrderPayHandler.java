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

@Service("phoneOrderPayHandler")
public class PhoneOrderPayHandler extends AbstractPhoneRechargePayHandler {
	
	@Override
	public boolean isConsumed(TradeOrder tradeOrder) {
		if (tradeOrder == null || tradeOrder.getStatus() == OrderStatusEnum.DROPSHIPPING
				|| tradeOrder.getStatus() == OrderStatusEnum.HAS_BEEN_SIGNED) {
			return true;
		}
		// 订单Id是否已生成支付记录
		int count = tradeOrderPayMapper.selectTradeOrderPayByOrderId(tradeOrder.getId());
		if (count > 0) {
			// 如果订单Id已生成支付记录，则标识该消息已被消费
			return true;
		}
		return false;
	}

	@Override
	protected void processOrderItem(TradeOrder tradeOrder, PayResponseDto respDto) throws Exception {
		List<TradeOrderItem> tradeOrderItems = tradeOrderItemService.selectOrderItemByOrderId(tradeOrder.getId());
		if (tradeOrderItems.isEmpty()) {
			return;
		}
		tradeOrder.setTradeOrderItem(tradeOrderItems);
		TradeOrderItem tradeOrderItem = tradeOrderItems.get(0);
		String phoneno = tradeOrderItem.getRechargeMobile();
		String cardnum = tradeOrderItem.getStoreSkuId();
		String orderid = tradeOrder.getTradeNum();

		try {
			// 风控验证
			if (isTrigger(tradeOrder, respDto, phoneno)) {
				refunds(tradeOrder, tradeOrderItem);
				return;
			}
		} catch (Exception e) {
			logger.error("充值风控异常", e);
			this.tradeOrderRefundsService.insertRechargeRefunds(tradeOrder);
			return;
		}

		// 1是聚合，2是欧飞
		int partnerNum = Integer.parseInt(partner);
		if (partnerNum == 1) {
			String sign = MD5.md5(openId + appKey + phoneno + cardnum + orderid);
			String url = submitOrderUrl + "?key=" + appKey + "&phoneno=" + phoneno + "&orderid=" + orderid + "&cardnum="
					+ cardnum + "&sign=" + sign;
			String resp = HttpClientUtil.get(url);
			JSONObject respJson = JSONObject.fromObject(resp);
			logger.info("************手机话费充值订单{}返回参数************{}", orderid, respJson);
			int errorCode = respJson.getInt("error_code");
			if (errorCode == 0) {
				JSONObject resultJson = respJson.getJSONObject("result");
				int gameState = Integer.parseInt(resultJson.getString("game_state"));
				if (gameState == 9) {
					// 充值失败，走退款流程
					logger.info("PHONEFEE===手机话费充值订单{}请求同步返回状态为失败，创建充值退款单！", orderid);
					this.tradeOrderRefundsService.insertRechargeRefunds(tradeOrder);
				} else if (gameState == 0) {
					// 修改订单状态
					logger.info("PHONEFEE===手机话费充值订单{}请求返回状态为充值中，修改订单状态为充值中！", orderid);
					updateTradeOrderStatus(tradeOrder);
				}
			} else {
				// 充值聚合订单提交失败，走退款流程
				logger.info("PHONEFEE===手机话费充值订单{}请求充值返回错误码为：{}，创建充值退款单！", orderid, errorCode);
				this.tradeOrderRefundsService.insertRechargeRefunds(tradeOrder);
			}
		} else if (partnerNum == 2) {
			/**
			 * md5_str检验码的计算方法:包体=userid+userpws+cardid+cardnum+sporder_id+sporder_time+ game_userid
			 *1: 对: “包体+KeyStr” 这个串进行md5 的32位值. 结果大写
			 */
			String cardid = tradeOrderItem.getStoreSpuId();
			String ordertime = orderid.substring(0, 14);
			String sign = MD5.md5(userid + userpws + cardid + cardnum + orderid + ordertime + phoneno + keyStr)
					.toUpperCase();
			String url = "http://" + userid + ".api2.ofpay.com/onlineorder.do?userid=" + userid + "&userpws=" + userpws
					+ "&cardid=" + cardid + "&cardnum=" + cardnum + "&sporder_id=" + orderid + "&sporder_time="
					+ ordertime + "&game_userid=" + phoneno + "&md5_str=" + sign + "&ret_url=" + returl + "&version="
					+ version;
			
			
			String xml = HttpClientUtil.get(url, "GB2312");
			JSONObject respJson = Xml2JsonUtil.xml2Json(xml, "GB2312");
			JSONObject orderinfo = respJson.getJSONObject("orderinfo");
			/**
			 * 充值模拟数据
			 */
//			JSONObject orderinfo = new JSONObject();
//			orderinfo.put("retcode", 1);
//			orderinfo.put("game_state", 0);
//			orderinfo.put("orderid",DateUtils.getDateRandom());
			
			logger.info("*******手机话费充值订单{}，返回参数：{}***********", orderid, orderinfo);
			int retcode = orderinfo.getInt("retcode");
			String userPhone = tradeOrder.getUserPhone();
			if (retcode == 1) {
				int gameState = orderinfo.getInt("game_state");
				if (gameState == 0) {
					// 充值请求订单生成成功，支付成功
					logger.info("PHONEFEE===手机话费充值订单{}请求返回状态为充值中，修改订单状态为充值中！", orderid);
					updateTradeOrderStatus(tradeOrder, orderinfo.getString("orderid"));
				} else if (gameState == 9) {
					// 充值请求订单失败，走退款流程
					logger.info("PHONEFEE===手机话费充值订单{}请求同步返回状态为失败，创建充值退款单！", orderid);
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
					logger.info("PHONEEFEE==手机话费订单{}返回码{}，经过手动查询充值结果为充值成功，修改订单状态为充值成功！", orderid, retcode);
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
					logger.info("PHONEEFEE==手机话费订单{}返回码{}，经过手动查询充值结果为充值中，请继续等待。", orderid, retcode);
				} else if (state == 9) {
					// 失败，走退款流程， 创建退款单
					logger.info("PHONEFEE===手机话费订单{}返回码{}，经过手动查询充值结果为充值失败，创建话费充值失败退款！", orderid, retcode);
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
					logger.warn("PHONEFEE===手机话费订单{}返回码{}，经过手动查询充值结果为找不到订单，请进入平台查询或者联系第三方欧飞客服进行核实", orderid, retcode);
				}
			} else {
				// 欧飞订单提交失败，走退款流程
				logger.info("PHONEFEE==OFPAY==手机话费充值订单{}请求充值返回码为：{}，创建充值退款单！", orderid, retcode);
				refunds(tradeOrder, tradeOrderItem);
			}
		}
	}


}
