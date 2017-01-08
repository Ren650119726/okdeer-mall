package com.okdeer.mall.order.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.entity.StoreInfoExt;
import com.okdeer.archive.store.entity.StoreInfoServiceExt;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.order.dto.AppStoreDto;
import com.okdeer.mall.order.dto.AppStoreServiceExtDto;
import com.okdeer.mall.order.dto.AppStoreSkuDto;
import com.okdeer.mall.order.dto.SeckillInfoDto;
import com.okdeer.mall.order.dto.TimeInterval;

/**
 * ClassName: StoreInfoAdapter 
 * @Description: 店铺信息适配器
 * @author maojj
 * @date 2017年1月4日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.0 			2017年1月4日				maojj
 */
public class AppAdapter {
	
	private static final Pattern pattern = Pattern.compile("(\\d{2}:\\d{2}-\\d{2}:\\d{2},*)*");

	/**
	 * @Description: 转换店铺信息给APP
	 * @param storeInfo
	 * @return   
	 * @author maojj
	 * @date 2017年1月5日
	 */
	public static AppStoreDto convert(StoreInfo storeInfo){
		if(storeInfo == null){
			return null;
		}
		AppStoreDto dto = BeanMapper.map(storeInfo, AppStoreDto.class);
		if(storeInfo.getStoreInfoExt() != null){
			BeanMapper.copy(storeInfo.getStoreInfoExt(), dto);
		}
		if(storeInfo.getStoreInfoServiceExt() != null){
			BeanMapper.copy(storeInfo.getStoreInfoServiceExt(), dto);
		}
		return dto;
	}
	
	public static AppStoreServiceExtDto convertAppStoreServiceExtDto(StoreInfo storeInfo){
		if(storeInfo == null || storeInfo.getStoreInfoServiceExt() == null){
			return null;
		}
		StoreInfoExt storeExt = storeInfo.getStoreInfoExt();
		StoreInfoServiceExt storeServExt = storeInfo.getStoreInfoServiceExt();
		AppStoreServiceExtDto dto = BeanMapper.map(storeInfo.getStoreInfoServiceExt(), AppStoreServiceExtDto.class);
		if(isAdvance(storeExt)){
			// 预约类型（0：提前多少小时下单 1：只能下当前日期多少天后的订单）
			if(Integer.valueOf(0).equals(storeExt.getAdvanceType())){
				// 如果预约类型是提前多少小时下单
				dto.setAheadTimeHours(String.valueOf(storeExt.getAdvanceTime()));
			}else if(Integer.valueOf(1).equals(storeExt.getAdvanceType())){
				// 如果预约类型是只能下当前日期多少天后的订单
				dto.setAheadTimeDay(String.valueOf(storeExt.getAdvanceTime()));
			}
		}
		parseOrderTimeModel(dto,storeServExt);
		return dto;
	}
	
	private static void parseOrderTimeModel(AppStoreServiceExtDto dto,StoreInfoServiceExt storeServExt){
		Integer orderTimeModel = storeServExt.getOrderTimeModel();
		if (orderTimeModel == null){
			return;
		}
		// 下单时间模式，0：按时间点，1：按时间段
		String timePoint = storeServExt.getTimePoint();
		if (orderTimeModel.intValue() == Constant.ZERO){
			// 按时间周期
			dto.setTimePeriod(timePoint);
		}else{
			// 按时间段
			List<TimeInterval> timeList = new ArrayList<TimeInterval>();
			TimeInterval time = null;
			if(StringUtils.isEmpty(timePoint) || !pattern.matcher(timePoint).matches()){
				return;
			}
			String[] timeIntervals = timePoint.split(",");
			for(String timeInterVal : timeIntervals){
				String[] times = timeInterVal.split("-");
				time = new TimeInterval();
				time.setStartTime(times[0]);
				time.setEndTime(times[1]);
				
				timeList.add(time);
			}
			dto.setTimesList(timeList);
		}
	}
	
	/**
	 * 是否提前预约
	 */
	private static boolean isAdvance(StoreInfoExt storeExt){
		return Integer.valueOf(Constant.ONE).equals(storeExt.getIsAdvanceType());
	}
	
	public static List<AppStoreSkuDto> convert(StoreSkuParserBo parserBo){
		if(parserBo == null || parserBo.getCurrentSkuMap() == null || CollectionUtils.isEmpty(parserBo.getCurrentSkuMap().values())){
			return null;
		}
		List<AppStoreSkuDto> dtoList = new ArrayList<AppStoreSkuDto>();
		AppStoreSkuDto dto = null;
		for(CurrentStoreSkuBo skuBo : parserBo.getCurrentSkuMap().values()){
			dto = BeanMapper.map(skuBo, AppStoreSkuDto.class);
			dto.setOnline(skuBo.getOnline().ordinal());
			dtoList.add(dto);
		}
		return dtoList;
	}
	
	public static SeckillInfoDto convert(ActivitySeckill seckill){
		if(seckill == null){
			return null;
		}
		SeckillInfoDto seckillInfo = new SeckillInfoDto();
		seckillInfo.setId(seckill.getId());
		seckillInfo.setSeckillPrice(seckill.getSeckillPrice());
		seckillInfo.setSeckillStatus(seckill.getSeckillStatus().ordinal());
		seckillInfo.setSeckillRangeType(seckill.getSeckillRangeType().ordinal());
		return seckillInfo;
	}
}
