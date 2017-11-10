package com.okdeer.mall.activity.bo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Maps;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.mall.activity.coupons.bo.ActivityRecordBo;
import com.okdeer.mall.activity.coupons.enums.RecordCountRuleEnum;
import com.okdeer.mall.common.enums.UseClientType;
import com.okdeer.mall.order.dto.PlaceOrderItemDto;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;

/**
 * ClassName: FavourParamBo 
 * @Description: 查询用户有效优惠参数Bo
 * @author maojj
 * @date 2017年2月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.1 			2017年2月15日				maojj		查询用户有效优惠参数Bo
 */
public class FavourParamBO {

	/**
	 * 用户Id
	 */
	private String userId;

	/**
	 * 店铺Id
	 */
	private String storeId;

	/**
	 * 店铺类型
	 */
	private StoreTypeEnum storeType;

	/**
	 * 订单总金额
	 */
	private BigDecimal totalAmount;

	/**
	 * 用户地址Id
	 */
	private String addressId;

	/**
	 * 客户端类型（下单渠道）
	 */
	private UseClientType clientType;

	/**
	 * 商品集合
	 */
	private List<String> skuIdList;

	/**
	 * 商品类目集合
	 */
	private Set<String> spuCategoryIds;

	/**
	 * 下单渠道
	 */
	private OrderResourceEnum channel;

	/**
	 * 是否是首单用户
	 */
	private Boolean isFirstOrderUser;

	/**
	 * 下单的商品列表
	 */
	private List<PlaceOrderItemDto> goodsList;

	/**
	 * 设备Id
	 */
	private String deviceId;

	/**
	 * 活动统计列表
	 */
	private Map<RecordCountRuleEnum, List<ActivityRecordBo>> activityCounter;

	// Begin V2.6.4 added by maojj 2017-11-08
	/**
	 * 订单类型 
	 */
	private OrderTypeEnum orderType;

	/**
	 * app版本号
	 */
	private String clientVersion;
	// End V2.6.4 added by maojj 2017-11-08

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public StoreTypeEnum getStoreType() {
		return storeType;
	}

	public void setStoreType(StoreTypeEnum storeType) {
		this.storeType = storeType;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getAddressId() {
		return addressId;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}

	public UseClientType getClientType() {
		return clientType;
	}

	public void setClientType(UseClientType clientType) {
		this.clientType = clientType;
	}

	public Set<String> getSpuCategoryIds() {
		return spuCategoryIds;
	}

	public void setSpuCategoryIds(Set<String> spuCategoryIds) {
		this.spuCategoryIds = spuCategoryIds;
	}

	public List<String> getSkuIdList() {
		return skuIdList;
	}

	public void setSkuIdList(List<String> skuIdList) {
		this.skuIdList = skuIdList;
	}

	public OrderResourceEnum getChannel() {
		return channel;
	}

	public void setChannel(OrderResourceEnum channel) {
		this.channel = channel;
	}

	public Boolean getIsFirstOrderUser() {
		return isFirstOrderUser;
	}

	public void setIsFirstOrderUser(Boolean isFirstOrderUser) {
		this.isFirstOrderUser = isFirstOrderUser;
	}

	public List<PlaceOrderItemDto> getGoodsList() {
		return goodsList;
	}

	public void setGoodsList(List<PlaceOrderItemDto> goodsList) {
		this.goodsList = goodsList;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public void putActivityCounter(RecordCountRuleEnum countRule, List<ActivityRecordBo> recordCountList) {
		if (this.activityCounter == null) {
			this.activityCounter = Maps.newHashMap();
		}
		if (CollectionUtils.isNotEmpty(recordCountList)) {
			this.activityCounter.put(countRule, recordCountList);
		}
	}

	public Integer findCountNum(RecordCountRuleEnum countRule, String pkId) {
		List<ActivityRecordBo> recList = this.activityCounter.get(countRule);
		if (CollectionUtils.isEmpty(recList)) {
			return 0;
		}
		ActivityRecordBo findResult = null;
		for (ActivityRecordBo recBo : recList) {
			if (pkId.equals(recBo.getPkId())) {
				findResult = recBo;
				break;
			}
		}
		return findResult == null ? 0 : findResult.getTotalNum();
	}

	public OrderTypeEnum getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderTypeEnum orderType) {
		this.orderType = orderType;
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

}
