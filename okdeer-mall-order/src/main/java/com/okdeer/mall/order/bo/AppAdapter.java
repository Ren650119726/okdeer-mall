package com.okdeer.mall.order.bo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.entity.StoreInfoExt;
import com.okdeer.archive.store.entity.StoreInfoServiceExt;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.common.utils.StoreServiceInvalidDateUtil;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.utils.DateUtils;
import com.okdeer.mall.order.dto.AppStoreDto;
import com.okdeer.mall.order.dto.AppStoreServiceExtDto;
import com.okdeer.mall.order.dto.AppStoreSkuDto;
import com.okdeer.mall.order.dto.SeckillInfoDto;
import com.okdeer.mall.order.dto.TimeInterval;
import com.okdeer.mall.system.utils.ConvertUtil;

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
			StoreInfoExt storeExt = storeInfo.getStoreInfoExt();
			BeanMapper.copy(storeExt, dto);
			if(isBusiness(storeExt.getServiceStartTime(),storeExt.getServiceEndTime())){
				dto.setIsRest(1);
			}else {
				dto.setIsRest(0);
			}
			dto.setFreight(ConvertUtil.format(storeExt.getFreight()));
			dto.setStartPrice(ConvertUtil.format(storeExt.getStartPrice()));
		}
		if(storeInfo.getStoreInfoServiceExt() != null){
			BeanMapper.copy(storeInfo.getStoreInfoServiceExt(), dto);
		}
		dto.setId(storeInfo.getId());
		// 店铺详细地址
		dto.setAddress(ConvertUtil.format(storeInfo.getArea()).replaceAll(" ", "") + storeInfo.getAddress());
		return dto;
	}
	
	/**
	 * @Description: 判断当前时间是否再营业时间范围内
	 * @param servStartTime 店铺营业开始时间
	 * @param servEndTime  店铺营业结束时间
	 * @return boolean  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private static boolean isBusiness(String servStartTime, String servEndTime) {
		// 当前时间
		Date currentDate = getCurrentDate();
		// 服务开始时间
		Date startTime = DateUtils.parseDate(servStartTime);
		Date endTime = DateUtils.parseDate(servEndTime);
		if (startTime.before(endTime)) {
			// 不跨天营业
			if ((currentDate.after(startTime)) && (endTime.after(currentDate))) {
				return true;
			} else {
				return false;
			}
		} else {
			// 跨天营业
			if (currentDate.after(startTime) || endTime.after(currentDate)) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * @Description: 获取当前时间小时分钟数
	 * @return Date  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private static Date getCurrentDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		String hourMinute = hour + ":" + minute;
		return DateUtils.parseDate(hourMinute);
	}
	
	/**
	 * @Description: 转换服务店铺扩展信息
	 * @param storeInfo
	 * @return   
	 * @author maojj
	 * @date 2017年3月13日
	 */
	public static AppStoreServiceExtDto convertAppStoreServiceExtDto(StoreInfo storeInfo){
		if(storeInfo == null || storeInfo.getStoreInfoServiceExt() == null){
			return null;
		}
		StoreInfoExt storeExt = storeInfo.getStoreInfoExt();
		StoreInfoServiceExt storeServExt = storeInfo.getStoreInfoServiceExt();
		AppStoreServiceExtDto dto = BeanMapper.map(storeServExt, AppStoreServiceExtDto.class);
		dto.setStartPrice(ConvertUtil.format(storeServExt.getStartingPrice()));
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
		// 解析下单模式
		parseOrderTimeModel(dto,storeServExt);
		// 解析不可用日期
		dto.setInvalidDate(parseInvalidDate(storeServExt.getInvalidDate()));
		return dto;
	}
	
	/**
	 * @Description: 解析下单模式
	 * @param dto
	 * @param storeServExt   
	 * @author maojj
	 * @date 2017年3月13日
	 */
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
	 * @Description: 解析店铺设置的不可用日期。筛选出用户可选7天范围内的不可用日期。
	 * @param dto
	 * @param invalidDate   
	 * @author maojj
	 * @date 2017年3月13日
	 */
	private static String[] parseInvalidDate(String invalidDate) {
		if (StringUtils.isEmpty(invalidDate)) {
			return null;
		}
		// 日历控件上选的不可用日期,采用位移算法存数,格式为201703***,201704***
		String[] invalidDateArr = invalidDate.split(",");
		// 无效日期列表
		List<String> invalidDateList = new ArrayList<String>();
		// 需要判定的日期
		String determineDate = null;
		// 需要判定的年月
		String determineMonth = null;
		// 有效的天
		int validDay = 0;
		// 不可用日期的月分
		String invalidMonth = null;
		// 无效的天
		int invalidDay = 0;
		for (int addDay = 0, validDays = 0; validDays < 8; addDay++) {
			determineDate = DateUtils.formatDate(DateUtils.addDays(new Date(), addDay), "yyyy-MM-dd");
			determineMonth = determineDate.substring(0, 4) + determineDate.substring(5, 7);
			for (String invalidTime : invalidDateArr) {
				invalidMonth = invalidTime.substring(0, 6);
				if (determineMonth.compareTo(invalidMonth) == -1) {
					// 如果当前月份小于不可用日期限制的月份，则当前日期一定可用
					validDays++;
					break;
				} else if (determineMonth.compareTo(invalidMonth) == 0) {
					// 如果当前月份等于不可用日期限制的月份，则判定这天是否可用
					validDay = Integer.parseInt(determineDate.substring(8, 10));
					invalidDay = Integer.parseInt(invalidTime.substring(6));
					if (((invalidDay >> (validDay - 1)) & 1) == 1) {
						// 如果当前天数为不可用日期，保存记录用于返回给App并跳过判定下一天
						invalidDateList.add(determineDate);
						break;
					} else {
						// 如果当前天数可用，则可用天数加1
						validDays++;
						break;
					}
				} else if (determineMonth.compareTo(invalidMonth) == 1) {
					// 如果当前月份大于不可用日期限制的月份，则循环跳入下一个月份限制进行判定
					continue;
				}
			}
		}
		String[] invalidDateResult = new String[invalidDateList.size()];
		invalidDateList.toArray(invalidDateResult);
		return invalidDateResult;
	}

	/**
	 * 是否提前预约
	 */
	private static boolean isAdvance(StoreInfoExt storeExt){
		return Integer.valueOf(Constant.ONE).equals(storeExt.getIsAdvanceType());
	}
	
	/**
	 * @Description: 转换给App的商品信息
	 * @param parserBo
	 * @return   
	 * @author maojj
	 * @date 2017年3月13日
	 */
	public static List<AppStoreSkuDto> convert(StoreSkuParserBo parserBo){
		if(parserBo == null || parserBo.getCurrentSkuMap() == null || CollectionUtils.isEmpty(parserBo.getCurrentSkuMap().values())){
			return null;
		}
		List<AppStoreSkuDto> dtoList = new ArrayList<AppStoreSkuDto>();
		AppStoreSkuDto dto = null;
		for(CurrentStoreSkuBo skuBo : parserBo.getCurrentSkuMap().values()){
			dto = BeanMapper.map(skuBo, AppStoreSkuDto.class);
			dto.setOnline(skuBo.getOnline().ordinal());
			dto.setOnlinePrice(ConvertUtil.format(skuBo.getOnlinePrice()));
			dto.setActPrice(ConvertUtil.format(skuBo.getActPrice()));
			dtoList.add(dto);
		}
		return dtoList;
	}
	
	/**
	 * @Description: 转换秒杀信息
	 * @param seckill
	 * @return   
	 * @author maojj
	 * @date 2017年3月13日
	 */
	public static SeckillInfoDto convert(ActivitySeckill seckill){
		if(seckill == null){
			return null;
		}
		SeckillInfoDto seckillInfo = new SeckillInfoDto();
		seckillInfo.setId(seckill.getId());
		seckillInfo.setSeckillPrice(ConvertUtil.format(seckill.getSeckillPrice()));
		seckillInfo.setSeckillStatus(seckill.getSeckillStatus().ordinal());
		seckillInfo.setSeckillRangeType(seckill.getSeckillRangeType().ordinal());
		return seckillInfo;
	}
}
