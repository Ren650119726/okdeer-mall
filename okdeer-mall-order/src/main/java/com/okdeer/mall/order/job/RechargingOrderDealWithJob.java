package com.okdeer.mall.order.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.mall.common.utils.HttpClientUtil;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.service.TradeOrderRefundsServiceApi;
import com.okdeer.mall.order.service.TradeOrderServiceApi;
import com.okdeer.mall.order.service.TradeOrderService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * ClassName: RechargingOrderDealWithJob 
 * @Description: 充值中订单处理Job
 * @author zhaoqc
 * @date 2016年7月30日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构 4.1         2016年7月30日                              zhaoqc              新增
 * 
 */
@Service
public class RechargingOrderDealWithJob extends AbstractSimpleElasticJob {

	/**
	 * 日志管理器
	 */
	private static final Logger logger = LoggerFactory.getLogger(RechargingOrderDealWithJob.class);
	
	/**
	 * 话费充值appKey
	 */
	@Value("${juhe.phonefee.appKey}")
	private String appKey;
	/**
	 * 流量充值appKey
	 */
	@Value("${juhe.dataplan.appKey}")
	private String dataPlanKey;
	/**
	 * 充值订单结果查询地址
	 */
	@Value("${phonefee.orderstaUrl}")
	private String orderstaUrl;
	/**
	 * 流量订单结果批量查询地址
	 */
	@Value("${dataplan.orderstaUrl}")
	private String dataOrderStaUrl;
	
	/**
	 * 订单service
	 */
	@Autowired
	private TradeOrderService tradeOrderService;
	
	/**
	 * 退款service
	 */
	@Reference(version="1.0.0")
	private TradeOrderRefundsServiceApi tradeOrderRefundsService;
	
	/**
	 * 订单service
	 */
	@Reference(version="1.0.0", check=false)
	private TradeOrderServiceApi tradeOrderServiceApi;
	
	@Override
	public void process(JobExecutionMultipleShardingContext arg0) {
		try {
			Map<String, List<TradeOrder>> tradeOrders = tradeOrderService.findRechargeOrdersByStatus(OrderStatusEnum.DROPSHIPPING);
			
			//处理话费充值类充值中订单
			processRechargeOrder(tradeOrders.get("rechargeList"));
			//处理流量充值类充值中订单
			processDataplanOrder(tradeOrders.get("dataplanList"));
		} catch (Exception e) {
			logger.error("查询充值中的订单失败", e);
		}
	}

	private void processRechargeOrder(List<TradeOrder> orders) throws Exception {
		String searchUrl = orderstaUrl + "?key=" + appKey;
		
		for(TradeOrder order : orders) {
			String url = searchUrl + "&orderid=" + order.getTradeNum();
			
			//发起查询请求
			String resp = HttpClientUtil.get(url);
			JSONObject respJson = JSONObject.fromObject(resp);
			logger.info("PHONEFEE===手机话费订单{}充值状态查询返回结果：{}", order.getTradeNum(), respJson);
			if(respJson.getInt("error_code") == 0) {
				//获取查询结果
				JSONObject result = respJson.getJSONObject("result");
				int gameState = result.getInt("game_state");
				String sporderId = result.getString("sporder_id");
				if (gameState == 1) {
					//充值成功
					logger.info("PHONEFEE===手机话费订单{}充值状态查询结果为充值成功，修改订单状态为充值成功！", order.getTradeNum());
					order.setStatus(OrderStatusEnum.HAS_BEEN_SIGNED);
					this.tradeOrderServiceApi.updataRechargeOrderStatus(order, sporderId);
				} else if (gameState == 9) {
					logger.info("PHONEFEE===手机话费订单{}充值状态查询结果为充值失败，创建话费充值失败退款！", order.getTradeNum());
					//失败，走退款流程， 创建退款单
					this.tradeOrderRefundsService.insertRechargeRefunds(order);
				}
			}
		}
	}
	
	private void processDataplanOrder(List<TradeOrder> orders) throws Exception {
		String searchUrl = dataOrderStaUrl + "?key=" + dataPlanKey;
		Map<String, TradeOrder> orderMap = new HashMap<String, TradeOrder>();
		for(TradeOrder order : orders){
			orderMap.put(order.getTradeNum(), order);
		}
		
		List<String> tradeNumList = new ArrayList<>(orderMap.keySet());
		List<List<String>> tradeNumSplitList = listSplit(tradeNumList, 50);
		
		if(!tradeNumSplitList.isEmpty()) {
			for(List<String> tradeNums : tradeNumSplitList) {
				String orderid = linkTradeNums(tradeNums);
				String url =  searchUrl + "&orderid=" + orderid;
				
				//发起请求
				String resp = HttpClientUtil.get(url);
				JSONObject respJson = JSONObject.fromObject(resp);
				logger.info("DATAPLAN===手机流量充值订单{}状态查询返回结果：{}", orderid, respJson);
				//请求成功，请求不成功不作处理等待下一次请求
				if(respJson.getInt("error_code") == 0) {
					JSONArray resultArr = respJson.getJSONArray("result");
					for(int i = 0 ; i < resultArr.size() ; i++) {
						JSONObject result = resultArr.getJSONObject(i);
						int gameState = result.getInt("game_state");
						String sporderId = result.getString("sporder_id");
						String uorderid = result.getString("uorderid");
						TradeOrder order = orderMap.get(uorderid);
						if (gameState == 1) {
							//充值成功
							logger.info("DATAPLAN===订单号：{}手机流量充值状态查询结果为充值成功，修改订单状态为充值成功！", uorderid);
							order.setStatus(OrderStatusEnum.HAS_BEEN_SIGNED);
							this.tradeOrderServiceApi.updataRechargeOrderStatus(order, sporderId);
						} else if (gameState == 9) {
							//失败，走退款流程， 创建退款单
							logger.info("DATAPLAN===订单号：{}手机流量充值状态查询结果为充值失败，创建流量充值失败退款！", uorderid);
							this.tradeOrderRefundsService.insertRechargeRefunds(order);
						}
					}
				}
			}
		}
	}
	
	private List<List<String>> listSplit(List<String> origin, int length) {
		if(length <= 0) {
			return new ArrayList<List<String>>();
		}
		List<List<String>> resultList = new ArrayList<List<String>>();
		List<String> singleList = null;
		int n = origin.size() % length == 0 ? origin.size() / length : origin.size()/ length + 1;
		for(int i = 0 ; i < n ; i++) {
			if(i != n - 1) {
				singleList = origin.subList(i * length, (i + 1) * length );
			} else {
				singleList = origin.subList(i * length, origin.size());
			}
			resultList.add(singleList);
		}
		return resultList;
	}
	
	private String linkTradeNums(List<String> tradeNums) {
		StringBuffer sb = new StringBuffer();
		for(String tradeNum : tradeNums) {
			sb.append(tradeNum + ",");
		}
		return sb.substring(0, sb.length() - 1);
	}
	
}
