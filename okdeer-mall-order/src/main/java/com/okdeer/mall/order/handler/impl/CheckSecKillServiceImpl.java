package com.okdeer.mall.order.handler.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillRecordService;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillService;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderItemDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.handler.RequestHandler;

/**
 * ClassName: CheckSecKillServiceImpl 
 * @author maojj
 * @date 2017年1月7日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.0 			2017年1月7日				maojj
 */
@Service("checkSecKillService")
public class CheckSecKillServiceImpl implements RequestHandler<PlaceOrderParamDto, PlaceOrderDto> {
	
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
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		PlaceOrderParamDto paramDto = req.getData();
		// 查询秒杀活动信息
		ActivitySeckill activitySeckill = activitySeckillService.findSeckillById(paramDto.getSeckillId());
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
		if (!activitySeckill.getStoreSkuId().equals(paramDto.getSkuList().get(0).getStoreSkuId())) {
			resp.setResult(ResultCodeEnum.ACTIVITY_GOODS_NOT_SUPPORT);
			req.setComplete(true);
			return;
		}
		// 秒杀活动限购一件。判断是否超出限购
		if (isOutOfLimitBuy(paramDto)) {
			resp.setResult(ResultCodeEnum.ACTIVITY_LIMIT_NUM);
			req.setComplete(true);
			return;
		}
		// 秒杀限制设备。判断是否超出限购
		if (isOutOfLimitByDevice(paramDto)) {
			resp.setResult(ResultCodeEnum.ACTIVITY_DEVICE_LIMIT_NUM);
			req.setComplete(true);
			return;
		}
		paramDto.put("seckillInfo", activitySeckill);
		
	}

	/**
	 * @Description: 设备限制
	 * @param paramDto
	 * @return   
	 * @author guocp
	 * @throws Exception 
	 * @date 2017年8月17日
	 */
	private boolean isOutOfLimitByDevice(PlaceOrderParamDto paramDto) throws Exception {
		if(StringUtils.isNullOrEmpty(paramDto.getSeckillId()) || StringUtils.isNullOrEmpty(paramDto.getDeviceId())){
			return false;
		}
		// 统计该用户是否参与过该秒杀活动
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("activitySeckillId", paramDto.getSeckillId());
		params.put("buyerDeviceId", paramDto.getDeviceId());
		// 查询用户参与该秒杀活动的次数 
		int userBuyNum = activitySeckillRecordService.findSeckillCount(params);
		ActivitySeckill seckill = activitySeckillService.findSeckillById(paramDto.getSeckillId());
		// 判断该设备参与该秒杀的次数是否大于限制次数
		if (seckill != null && seckill.getDailyMaxNum() != null && seckill.getDailyMaxNum().intValue() > 0
				&& userBuyNum >= seckill.getDailyMaxNum().intValue()) {
			return true;
		}
		return false;
	}

	/**
	 * @Description: 检查购买请求是否超出限购
	 * @param req
	 * @return   
	 * @author maojj
	 * @date 2016年9月22日
	 */
	private boolean isOutOfLimitBuy(PlaceOrderParamDto paramDto){
		List<PlaceOrderItemDto> itemList = paramDto.getSkuList();
		if(itemList.size() > 1 || itemList.get(0).getQuantity() > 1){
			return true;
		}
		// 统计该用户是否参与过该秒杀活动
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("activitySeckillId", paramDto.getSeckillId());
		params.put("buyerUserId", paramDto.getUserId());
		// 查询用户参与该秒杀活动的次数
		int userBuyNum = activitySeckillRecordService.findSeckillCount(params);
		// 判断该用户是否参与过该秒杀活动，如果参与过，不能再次参与
		if (userBuyNum > 0) {
			return true;
		}
		return false;
	}
	
}
