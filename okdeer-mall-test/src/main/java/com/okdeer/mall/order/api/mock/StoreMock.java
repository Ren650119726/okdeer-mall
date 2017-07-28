package com.okdeer.mall.order.api.mock;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.entity.StoreInfoExt;
import com.okdeer.archive.store.enums.StoreStatusEnum;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.mall.base.MockUtils;
import com.okdeer.mall.common.utils.DateUtils;

public class StoreMock {

	public static List<StoreInfo> mock(){
		List<StoreInfo> storeList = Lists.newArrayList();
		// 店铺不存在
		storeList.add(null);
		// 店铺已关闭
		storeList.add(mockClosed());
		// 店铺暂停营业
		storeList.add(mockNotBusiness());
		// 店铺不跨天营业
		storeList.add(mockInDay());
		// 店铺跨天营业
		storeList.add(mockInterDay());
		// 店铺非营业时间不接单
		storeList.add(mockIsAccept());
		return storeList;
	}
	
	public static StoreInfo initCvs(){
		return MockUtils.getMockSingleData("/com/okdeer/mall/order/api/mock/mock-store.json", StoreInfo.class);
	}
	
	/**
	 * @Description: 店铺已关闭
	 * @return   
	 * @author maojj
	 * @date 2017年7月27日
	 */
	public static StoreInfo mockClosed(){
		StoreInfo storeInfo = initCvs();
		StoreInfoExt storeExt = storeInfo.getStoreInfoExt();
		storeExt.setIsClosed(StoreStatusEnum.CLOSED);
		storeInfo.setStoreInfoExt(storeExt);
		return storeInfo;
	}
	
	/**
	 * @Description: 店铺已暂停营业
	 * @return   
	 * @author maojj
	 * @date 2017年7月27日
	 */
	public static StoreInfo mockNotBusiness(){
		StoreInfo storeInfo = initCvs();
		StoreInfoExt storeExt = storeInfo.getStoreInfoExt();
		storeExt.setIsClosed(StoreStatusEnum.OPENING);
		storeExt.setIsBusiness(WhetherEnum.not);
		storeInfo.setStoreInfoExt(storeExt);
		return storeInfo;
	}
	
	/**
	 * @Description: 不跨天营业且非营业时间不接单
	 * @return   
	 * @author maojj
	 * @date 2017年7月27日
	 */
	public static StoreInfo mockInDay(){
		StoreInfo storeInfo = initCvs();
		StoreInfoExt storeExt = storeInfo.getStoreInfoExt();
		storeExt.setIsClosed(StoreStatusEnum.OPENING);
		storeExt.setIsBusiness(WhetherEnum.whether);
		// 当前时间往后30分钟
		storeExt.setServiceStartTime(addMinute(30));
		storeExt.setServiceEndTime(addMinute(2*60));
		storeExt.setIsAcceptOrder(WhetherEnum.not);
		storeInfo.setStoreInfoExt(storeExt);
		return storeInfo;
	}
	
	public static String addMinute(int minute){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, minute);
		return DateUtils.formatDate(cal.getTime(),"HH:mm");
	}
	
	/**
	 * @Description: 跨天营业
	 * @return   
	 * @author maojj
	 * @date 2017年7月27日
	 */
	public static StoreInfo mockInterDay(){
		StoreInfo storeInfo = initCvs();
		StoreInfoExt storeExt = storeInfo.getStoreInfoExt();
		storeExt.setIsClosed(StoreStatusEnum.OPENING);
		storeExt.setIsBusiness(WhetherEnum.whether);
		storeExt.setServiceStartTime(addMinute(30));
		storeExt.setServiceEndTime(addMinute(-60));
		storeExt.setIsAcceptOrder(WhetherEnum.not);
		storeInfo.setStoreInfoExt(storeExt);
		return storeInfo;
	}
	
	/**
	 * @Description: 非营业时间接单
	 * @return   
	 * @author maojj
	 * @date 2017年7月27日
	 */
	public static StoreInfo mockIsAccept(){
		StoreInfo storeInfo = initCvs();
		StoreInfoExt storeExt = storeInfo.getStoreInfoExt();
		storeExt.setIsClosed(StoreStatusEnum.OPENING);
		storeExt.setIsBusiness(WhetherEnum.whether);
		storeExt.setServiceStartTime(addMinute(-30));
		storeExt.setServiceEndTime(addMinute(2*60));
		storeExt.setIsAcceptOrder(WhetherEnum.whether);
		storeInfo.setStoreInfoExt(storeExt);
		return storeInfo;
	}
}
