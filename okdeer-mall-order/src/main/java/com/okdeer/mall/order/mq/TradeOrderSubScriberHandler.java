package com.okdeer.mall.order.mq;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordService;
import com.okdeer.mall.activity.prize.entity.ActivityLuckDraw;
import com.okdeer.mall.activity.prize.entity.ActivityLuckDrawVo;
import com.okdeer.mall.activity.prize.service.ActivityDrawRecordService;
import com.okdeer.mall.activity.prize.service.ActivityLuckDrawService;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;
import com.okdeer.mall.member.member.entity.SysBuyerExt;
import com.okdeer.mall.member.service.SysBuyerExtService;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.service.TradeOrderService;

/**
 * @Description: 订单消息接收处理新类 
 * @author tuzhd
 * @date 2016年12月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V2.4			2017-7-6			tuzhd			  订单消息处理 
 */
@Service
public class TradeOrderSubScriberHandler {
	private static final Logger logger = LoggerFactory.getLogger(TradeOrderSubScriberHandler.class);
	
	@Autowired
	private SysBuyerExtService sysBuyerExtService;
	
	@Autowired
	private ActivityLuckDrawService activityLuckDrawService;
	
	@Autowired
	ActivityDrawRecordService  activityDrawRecordService;
	
	@Autowired
	ActivityCouponsRecordService activityCouponsRecordService;
	
	@Autowired
	private TradeOrderService tradeOrderService;
	
	/**
	 * @Description: 下单赠送抽奖活动的抽奖次数
	 * @param tradeOrder   
	 * @return void  
	 * @author tuzhd
	 * @date 2017年1月11日
	 */
	public void activityAddPrizeCcount(TradeOrder tradeOrder)throws Exception{
		SysBuyerExt user = sysBuyerExtService.findByUserId(tradeOrder.getUserId());
		if(user != null ){
			//获得用户每日订单排序值，属为前三单可以抽奖
			int orderNo = getUserOrderByDay(tradeOrder, user.getId());
			if(orderNo > 5){
				return;
			}
	 		
			//查询当前进行中的抽奖活动 
			ActivityLuckDrawVo vo =new ActivityLuckDrawVo();
			vo.setStatus(SeckillStatusEnum.ing);
			List<ActivityLuckDraw> al = activityLuckDrawService.findLuckDrawList(vo);
			if(CollectionUtils.isEmpty(al)){
				return;
			}
			
			//获取当前抽奖活动id集合
			List<String> ids = new ArrayList<String>();
			al.forEach(e -> {
				ids.add(e.getId());
			});
			// xuzq 12月套鹿活动 不计算抽奖次数 
			//int count = activityDrawRecordService.findCountByUserIdAndIds(tradeOrder.getUserId(), ids);
			//查询剩余的抽奖次数
			//if((count+user.getPrizeCount()) < 15){
				//执行充值人送代金劵及抽奖次数 1
				sysBuyerExtService.updateAddPrizeCount(tradeOrder.getUserId(), 1);
			//}
		}
			
	}
	
	/**
	 * @Description: 获得用户每日订单排序值，获得抽奖机会为前三单
	 * @param order
	 * @param userId   
	 * @return void  
	 * @author tuzhd
	 * @date 2017年8月30日
	 */
	private int getUserOrderByDay(TradeOrder order,String userId){
		Map<String,Object> param  = new HashMap<>();
 		param.put("userId", userId);
 		param.put("status", OrderStatusEnum.HAS_BEEN_SIGNED);
 		param.put("startReceivedTime", DateUtils.getDateStart(new Date()));
 		param.put("endReceivedTime", DateUtils.getDateEnd(new Date()));
 		param.put("orderBy", "received_time");
 		List<TradeOrder> list = tradeOrderService.selectByParams(param);
 		int index = 0;
 		if(CollectionUtils.isEmpty(list)){
			for(TradeOrder tradeOrder : list){
				index++;
				if(tradeOrder.getId().equals(order.getId())){
					break;
				}
			}
 		}
 		return index;
	}
	
	/**
	 * @Description: 邀新活动 被邀用户下单完成后给 邀请人送代金劵及抽奖次数   
	 * @param tradeOrder  订单信息
	 * @throws
	 * @author tuzhd
	 * @date 2016年12月12日
	 */
	public void activityInviteHandler(TradeOrder tradeOrder) throws Exception{
		//修改订单状态为完成时 进行业务处理
		if(tradeOrder.getStatus() == OrderStatusEnum.HAS_BEEN_SIGNED){
			//邀请人获得的代金劵奖励id, 每层id中逗号隔开
			String[] collectCouponsId ={""};
			activityCouponsRecordService.addInviteUserHandler(tradeOrder.getUserId(),collectCouponsId);
		}
	}
}
