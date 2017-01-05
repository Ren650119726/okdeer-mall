package com.okdeer.mall.order.handler.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillRecordService;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillService;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.constant.text.ExceptionConstant;
import com.okdeer.mall.order.enums.OrderOptTypeEnum;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.vo.ServiceOrderReq;
import com.okdeer.mall.order.vo.ServiceOrderResp;
import com.okdeer.mall.system.utils.ConvertUtil;

/**
 * ClassName: SecKillCheckServiceImpl 
 * @Description: 秒杀活动检查
 * @author maojj
 * @date 2016年9月21日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构1.1			2016年9月21日				maojj			秒杀活动检查
 */
@Service("secKillCheckService")
public class SecKillCheckServiceImpl implements RequestHandler<ServiceOrderReq,ServiceOrderResp> {

	/**
	 * 秒杀活动Service
	 */
	@Resource
	private ActivitySeckillService activitySeckillService;

	/**
	 * 秒杀活动记录service
	 */
	@Resource
	private ActivitySeckillRecordService activitySeckillRecordService;

	@Override
	public void process(Request<ServiceOrderReq> req, Response<ServiceOrderResp> resp) throws Exception {
		ServiceOrderReq reqData = req.getData();
		ServiceOrderResp respData = resp.getData();
		// 查询秒杀活动信息
		ActivitySeckill activitySeckill = activitySeckillService.findSeckillById(reqData.getSeckillId());
		// 活动不存在
		if (activitySeckill == null) {
			resp.setResult(ResultCodeEnum.ACTIVITY_NOT_EXISTS);
			req.setComplete(true);
			return;
		}
		// 活动已关闭
		if (activitySeckill.getSeckillStatus() == SeckillStatusEnum.closed) {
			resp.setResult(ResultCodeEnum.ACTIVITY_IS_CLOSED);
			req.setComplete(true);
			return;
		}
		// 活动已结束
		if (activitySeckill.getSeckillStatus() == SeckillStatusEnum.end) {
			resp.setResult(ResultCodeEnum.ACTIVITY_IS_END);
			req.setComplete(true);
			return;
		}
		// 活动商品和欲购买商品不一致
		if (!activitySeckill.getStoreSkuId().equals(reqData.getSkuId())) {
			resp.setResult(ResultCodeEnum.ACTIVITY_GOODS_NOT_SUPPORT);
			req.setComplete(true);
			return;
		}
		// 秒杀活动限购一件。判断是否超出限购
		if (isOutOfLimitBuy(reqData)) {
			resp.setResult(ResultCodeEnum.ACTIVITY_LIMIT_NUM);
			req.setComplete(true);
			return;
		}
		
		// 设置响应结果
		respData.setSeckillStatus(activitySeckill.getSeckillStatus().ordinal());
		if (req.getOrderOptType() == OrderOptTypeEnum.ORDER_SETTLEMENT) {
			respData.setSeckillPrice(ConvertUtil.format(activitySeckill.getSeckillPrice()));
			respData.setSeckillId(activitySeckill.getId());
			respData.setSeckillRangeType(activitySeckill.getSeckillRangeType().ordinal());
		}
		
		req.getContext().put("seckillPrice", activitySeckill.getSeckillPrice());
		req.getContext().put("seckillRangeType", activitySeckill.getSeckillRangeType().ordinal());
	}

	/**
	 * @Description: 检查购买请求是否超出限购
	 * @param req
	 * @return   
	 * @author maojj
	 * @date 2016年9月22日
	 */
	private boolean isOutOfLimitBuy(ServiceOrderReq reqData){
		if(reqData.getSkuNum().compareTo(Integer.parseInt(ExceptionConstant.ONE)) == 1){
			return true;
		}
		// 统计该用户是否参与过该秒杀活动
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("activitySeckillId", reqData.getSeckillId());
		params.put("buyerUserId", reqData.getUserId());
		// 查询用户参与该秒杀活动的次数
		int userBuyNum = activitySeckillRecordService.findSeckillCount(params);
		// 判断该用户是否参与过该秒杀活动，如果参与过，不能再次参与
		if (userBuyNum > 0) {
			return true;
		}
		return false;
	}
	
}
