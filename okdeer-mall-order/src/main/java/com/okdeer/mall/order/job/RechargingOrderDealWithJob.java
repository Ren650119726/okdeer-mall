package com.okdeer.mall.order.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.common.utils.HttpClientUtil;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.service.TradeOrderItemService;
import com.okdeer.mall.order.service.TradeOrderRefundsServiceApi;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mcm.entity.SmsVO;
import com.okdeer.mcm.service.ISmsService;

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
     * okdeer.recharge.partner
     */
    @Value("${okdeer.recharge.partner}")
    private String partner;
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
     * ofpay.userid
     */
    @Value("${ofpay.userid}")
    private String userid;
    /**
     * 充值成功短信
     */
    @Value("${recharge.success.message}")
    private String successMsg;
    /**
     * 充值失败短信
     */
    @Value("${recharge.failure.message}")
    private String failureMsg;
    /**
     * 短信接口
     */
    @Reference(version = "1.0.0", check = false)
    ISmsService smsService;
    
    @Value("${mcm.sys.code}")
    private String mcmSysCode;

    @Value("${mcm.sys.token}")
    private String mcmSysToken;
	/**
	 * 订单service
	 */
	@Autowired
	private TradeOrderService tradeOrderService;
	   /**
     * 订单项
     */
    @Autowired
    private TradeOrderItemService tradeOrderItemService;
    
	/**
	 * 退款service
	 */
	@Reference(version="1.0.0")
	private TradeOrderRefundsServiceApi tradeOrderRefundsService;
	
	@Override
	public void process(JobExecutionMultipleShardingContext arg0) {
        try {
            Map<String, List<TradeOrder>> tradeOrders = tradeOrderService.findRechargeOrdersByStatus(OrderStatusEnum.DROPSHIPPING);
            int partnerNum = Integer.parseInt(partner);
            if (partnerNum == 1) {
                //处理话费充值类充值中订单
                processRechargeOrder(tradeOrders.get("rechargeList"));
                //处理流量充值类充值中订单
                processDataplanOrder(tradeOrders.get("dataplanList"));
            } else if (partnerNum == 2) {
                //处理话费充值类充值中订单
                processOfPayRechargeOrder(tradeOrders.get("rechargeList"));
                //处理流量充值类充值中订单
                processOfPayRechargeOrder(tradeOrders.get("dataplanList"));
            }
        } catch (Exception e) {
            logger.error("查询充值中的订单失败", e);
        }
	}

	   public void processOfPayRechargeOrder(List<TradeOrder> orders) throws Exception {
	        for(TradeOrder order : orders) {
	            List<TradeOrderItem> tradeOrderItems = tradeOrderItemService.selectOrderItemByOrderId(order.getId());
	            TradeOrderItem tradeOrderItem = tradeOrderItems.get(0);
	            String sporderId = order.getTradeNum();
	            String url = "http://" + userid + ".api2.ofpay.com/api/query.do?userid=" + userid + "&spbillid=" + sporderId;
	            String stateStr = HttpClientUtil.get(url);
	            int state = Integer.parseInt(stateStr);
	            String phoneno = order.getUserPhone();
	            if (state == 1) {
	                //充值成功
	                logger.info("PHONEFEE===手机话费订单{}充值状态查询结果为充值成功，修改订单状态为充值成功！", order.getTradeNum());
	                order.setStatus(OrderStatusEnum.HAS_BEEN_SIGNED);
	                this.tradeOrderService.updateRechargeOrderByTradeNum(order);
	            
	                //发送提醒短信
	                String content = successMsg;
	                int idx = content.indexOf("#");
	                content = content.replaceFirst(String.valueOf(content.charAt(idx)), phoneno);
	                idx = content.indexOf("#");
	                content = content.replaceFirst(String.valueOf(content.charAt(idx)), tradeOrderItem.getSkuName());
	                
	                SmsVO smsVo = createSmsVo(phoneno, content);
	                this.smsService.sendSms(smsVo);
	            } else if (state == 0) {
	                //充值中
	                logger.info("手机话费充值订单{}查询结果为充值中，请继续等待。", sporderId);
	            } else if (state == 9) {
	                logger.info("PHONEFEE===手机话费订单{}充值状态查询结果为充值失败，创建话费充值失败退款！", order.getTradeNum());
	                //失败，走退款流程， 创建退款单
	                this.tradeOrderRefundsService.insertRechargeRefunds(order);
	            
	                //发送短信提醒
	                String content = failureMsg;
	                int idx = content.indexOf("#");
	                content = content.replaceFirst(String.valueOf(content.charAt(idx)), phoneno);
	                idx = content.indexOf("#");
	                content = content.replaceFirst(String.valueOf(content.charAt(idx)), tradeOrderItem.getSkuName());
	                
	                SmsVO smsVo = createSmsVo(phoneno, content);
	                this.smsService.sendSms(smsVo);
	            } else if (state == -1) {
	                //找不到此订单
	                logger.warn("***********手机话费充值订单{}查询结果为找不到订单，请进入平台查询或者联系第三方欧飞客服进行核实***************", sporderId);
	            }
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
					this.tradeOrderService.updataRechargeOrderStatus(order, sporderId);
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
							this.tradeOrderService.updataRechargeOrderStatus(order, sporderId);
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
	
   private SmsVO createSmsVo(String mobile, String content) {
        SmsVO smsVo = new SmsVO();
        smsVo.setId(UuidUtils.getUuid());
        smsVo.setUserId(mobile);
        smsVo.setIsTiming(0);
        smsVo.setToken(mcmSysToken);
        smsVo.setSysCode(mcmSysCode);
        smsVo.setMobile(mobile);
        smsVo.setContent(content);
        smsVo.setSmsChannelType(3);
        smsVo.setSendTime(DateUtils.formatDateTime(new Date()));
        return smsVo;
    }
}
