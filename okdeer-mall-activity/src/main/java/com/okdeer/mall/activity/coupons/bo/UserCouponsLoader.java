package com.okdeer.mall.activity.coupons.bo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;

/**
 * ClassName: UserCouponsLoader 
 * @Description: 用户代金券加载器
 * @author maojj
 * @date 2017年11月7日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.6.3 		2017年11月7日				maojj
 */
public class UserCouponsLoader {
	
	private List<UserCouponsBo> userCouponsList;
	
	private Set<String> couponsIdList;
	
	private Set<String> couponsActIdList;
	
	public UserCouponsLoader(){
		this.userCouponsList = Lists.newArrayList();
		this.couponsIdList = Sets.newHashSet();
		this.couponsActIdList = Sets.newHashSet();
	}

	public List<UserCouponsBo> retrieveResult(){
		return this.userCouponsList;
	}
	
	public Set<String> extraCouponsIdList(){
		return this.couponsIdList;
	}
	
	public Set<String> extraCouponsActIdList(){
		return this.couponsActIdList;
	}
	/**
	 * @Description: 装载领取记录列表
	 * @param collectRecList   
	 * @author maojj
	 * @date 2017年11月7日
	 */
	public void loadCollectRecList(List<ActivityCouponsRecord> collectRecList){
		collectRecList.forEach(collectRec -> {
			UserCouponsBo userCouponsBo = new UserCouponsBo();
			userCouponsBo.setCollectRecord(collectRec);
			userCouponsList.add(userCouponsBo);
			couponsIdList.add(collectRec.getCouponsId());
			couponsActIdList.add(collectRec.getCouponsCollectId());
		});
	}
	
	/**
	 * @Description: 装载代金券列表
	 * @param couponsList   
	 * @author maojj
	 * @date 2017年11月7日
	 */
	public void loadCouponsList(List<ActivityCoupons> couponsList){
		if(CollectionUtils.isEmpty(couponsList)){
			return;
		}
		for(UserCouponsBo userCouponsBo : this.userCouponsList){
			ActivityCouponsRecord collectRec = userCouponsBo.getCollectRecord();
			Optional<ActivityCoupons> couponsOpt = couponsList.stream()
					.filter(coupons -> collectRec.getCouponsId().equals(coupons.getId())).findFirst();
			if(couponsOpt.isPresent()){
				userCouponsBo.setCouponsInfo(couponsOpt.get());
			}
		}
	}
	
	/**
	 * @Description: 装载活动列表
	 * @param couponsActList   
	 * @author maojj
	 * @date 2017年11月7日
	 */
	public void loadCouponsActList(List<ActivityCollectCoupons> couponsActList) {
		if (CollectionUtils.isEmpty(couponsActList)) {
			return;
		}
		for (UserCouponsBo userCouponsBo : this.userCouponsList) {
			ActivityCouponsRecord collectRec = userCouponsBo.getCollectRecord();
			Optional<ActivityCollectCoupons> couponsActOpt = couponsActList.stream()
					.filter(couponsAct -> collectRec.getCouponsCollectId().equals(couponsAct.getId())).findFirst();
			if (couponsActOpt.isPresent()) {
				userCouponsBo.setCouponsActInfo(couponsActOpt.get());
			}
		}
	}
}
