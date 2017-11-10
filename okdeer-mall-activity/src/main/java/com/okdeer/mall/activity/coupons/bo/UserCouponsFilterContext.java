package com.okdeer.mall.activity.coupons.bo;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Lists;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;

/**
 * ClassName: UserCouponsFilterContext 
 * @Description: 用户代金券过滤上下文
 * @author maojj
 * @date 2017年11月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.6.3 		2017年11月8日				maojj
 */
public class UserCouponsFilterContext {
	
	private FavourParamBO paramBo;

	/**
	 * 可用的代金券id列表
	 */
	private List<String> enabledCouponsIdList;

	/**
	 * 需要排除的代金券id列表
	 */
	private List<String> excludeCouponsIdList;

	/**
	 * 不可用的代金券活动id列表
	 */
	private List<String> excludeCouponsActIdList;

	/**
	 * 是否是首单用户
	 */
	private Boolean isFirstOrderUser;

	/**
	 * 享受优惠的总金额
	 */
	private BigDecimal enjoyFavourAmount;

	/**
	 * 享受优惠的店铺商品id
	 */
	private List<String> enjoyFavourSkuIdList;

	/**
	 * 缓存店铺信息
	 */
	private StoreInfo storeInfo;

	/**
	 * 缓存地址信息
	 */
	private MemberConsigneeAddress addrInfo;
	
	public void refresh(FavourParamBO favourBo){
		this.enjoyFavourAmount = favourBo.getTotalAmount();
		if(CollectionUtils.isNotEmpty(favourBo.getSkuIdList())){
			this.enjoyFavourSkuIdList = favourBo.getSkuIdList();
		}else if(CollectionUtils.isNotEmpty(favourBo.getGoodsList())){
			this.enjoyFavourSkuIdList = favourBo.getGoodsList().stream().map(e -> e.getStoreSkuId())
					.collect(Collectors.toList());
		}
	}

	/**
	 * 增加可用的代金券
	 */
	public void addEnabledCouponsId(String couponsId) {
		if (enabledCouponsIdList == null) {
			enabledCouponsIdList = Lists.newArrayList();
		}
		enabledCouponsIdList.add(couponsId);
	}

	public void addExcludeCouponsId(String couponsId) {
		if (excludeCouponsIdList == null) {
			excludeCouponsIdList = Lists.newArrayList();
		}
		excludeCouponsIdList.add(couponsId);
	}

	public void addExcludeCouponsActId(String couponsActId) {
		if (excludeCouponsActIdList == null) {
			excludeCouponsActIdList = Lists.newArrayList();
		}
		excludeCouponsActIdList.add(couponsActId);
	}

	public List<String> getEnabledCouponsIdList() {
		return enabledCouponsIdList;
	}

	public void setEnabledCouponsIdList(List<String> enabledCouponsIdList) {
		this.enabledCouponsIdList = enabledCouponsIdList;
	}

	public List<String> getExcludeCouponsIdList() {
		return excludeCouponsIdList;
	}

	public void setExcludeCouponsIdList(List<String> excludeCouponsIdList) {
		this.excludeCouponsIdList = excludeCouponsIdList;
	}

	public List<String> getExcludeCouponsActIdList() {
		return excludeCouponsActIdList;
	}

	public void setExcludeCouponsActIdList(List<String> excludeCouponsActIdList) {
		this.excludeCouponsActIdList = excludeCouponsActIdList;
	}

	public Boolean isFirstOrderUser() {
		return isFirstOrderUser;
	}

	public void setFirstOrderUser(Boolean isFirstOrderUser) {
		this.isFirstOrderUser = isFirstOrderUser;
	}

	public BigDecimal getEnjoyFavourAmount() {
		return enjoyFavourAmount;
	}

	public void setEnjoyFavourAmount(BigDecimal enjoyFavourAmount) {
		this.enjoyFavourAmount = enjoyFavourAmount;
	}

	public StoreInfo getStoreInfo() {
		return storeInfo;
	}

	public void setStoreInfo(StoreInfo storeInfo) {
		this.storeInfo = storeInfo;
	}

	public MemberConsigneeAddress getAddrInfo() {
		return addrInfo;
	}

	public void setAddrInfo(MemberConsigneeAddress addrInfo) {
		this.addrInfo = addrInfo;
	}

	public List<String> getEnjoyFavourSkuIdList() {
		return enjoyFavourSkuIdList;
	}

	public void setEnjoyFavourSkuIdList(List<String> enjoyFavourSkuIdList) {
		this.enjoyFavourSkuIdList = enjoyFavourSkuIdList;
	}

}
