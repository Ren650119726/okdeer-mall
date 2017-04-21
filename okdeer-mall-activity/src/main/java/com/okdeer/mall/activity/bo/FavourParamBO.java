package com.okdeer.mall.activity.bo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.mall.activity.coupons.enums.CouponsType;
import com.okdeer.mall.common.enums.UseClientType;
import com.okdeer.mall.order.enums.OrderResourceEnum;

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
	 * 代金券类型
	 */
	private CouponsType couponsType;

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

	public CouponsType getCouponsType() {
		return couponsType;
	}

	public void setCouponsType(CouponsType couponsType) {
		this.couponsType = couponsType;
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

}
