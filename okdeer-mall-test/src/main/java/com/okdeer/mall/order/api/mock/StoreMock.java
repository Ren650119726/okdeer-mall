package com.okdeer.mall.order.api.mock;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.entity.StoreInfoExt;
import com.okdeer.archive.store.enums.StoreStatusEnum;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.base.common.enums.WhetherEnum;
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
		StoreInfo storeInfo = new StoreInfo();
		storeInfo.setId("4028b1e05cbf666b015cc3723f7d40a8");
		storeInfo.setStoreNo("Y003789");
		storeInfo.setStoreName("好邻居便利店");
		storeInfo.setContacts("13412341234");
		storeInfo.setMobile("13412341234");
		storeInfo.setAddress("荆州博物馆");
		storeInfo.setArea("湖北省荆州市荆州区");
		storeInfo.setLongitude(112.186803);
		storeInfo.setLatitude(30.359302);
		storeInfo.setType(StoreTypeEnum.AROUND_STORE);
		
		StoreInfoExt storeExt = new StoreInfoExt();
		storeExt.setServiceStartTime("08:00");
		storeExt.setServiceEndTime("23:30");
		storeExt.setIsAcceptOrder(WhetherEnum.whether);
		storeExt.setIsBusiness(WhetherEnum.whether);
		storeExt.setStartPrice(BigDecimal.valueOf(15));
		storeExt.setFreight(BigDecimal.valueOf(6));
		storeExt.setFreeFreightPrice(BigDecimal.valueOf(30));
		storeExt.setIsPickUp(0);
		
		storeInfo.setStoreInfoExt(storeExt);
		return storeInfo;
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
