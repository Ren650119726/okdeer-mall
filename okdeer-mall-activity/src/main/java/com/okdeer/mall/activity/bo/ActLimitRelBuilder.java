package com.okdeer.mall.activity.bo;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.okdeer.archive.goods.base.entity.GoodsSpuCategory;
import com.okdeer.archive.goods.store.dto.StoreSkuDto;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.bdp.address.entity.Address;
import com.okdeer.mall.activity.discount.entity.ActivityBusinessRel;
import com.okdeer.mall.activity.discount.enums.ActivityBusinessType;
import com.okdeer.mall.activity.dto.ActivityBusinessRelDto;

/**
 * ClassName: ActLimitRelBuilder 
 * @Description: 活动限制关系构建者
 * @author maojj
 * @date 2017年4月20日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.3 		2017年4月20日				maojj		
 */
public class ActLimitRelBuilder {

	/**
	 * 业务关系映射。key：业务类型，value：业务Id集合
	 */
	private Map<ActivityBusinessType, List<String>> relIdMap;
	
	private List<ActivityBusinessRel> busiRelList;
	
	/**
	 * 地区映射关系
	 */
	private Map<String,Address> addressMap;
	
	/**
	 * 店铺映射关系
	 */
	private Map<String,StoreInfo> storeInfoMap;
	
	/**
	 * 商品类目映射关系
	 */
	private Map<String,GoodsSpuCategory> spuCtgMap;
	
	/**
	 * 店铺商品映射关系
	 */
	private Map<String,StoreSkuDto> storeSkuMap;
	
	/**
	 * 限制范围Id列表
	 */
	private StringBuilder limitRangeBuilder;
	
	public ActLimitRelBuilder(){
		this.relIdMap = Maps.newHashMap();
		this.addressMap = Maps.newHashMap();
		this.storeInfoMap = Maps.newHashMap();
		this.spuCtgMap = Maps.newHashMap();
		this.storeSkuMap = Maps.newHashMap();
		this.limitRangeBuilder = new StringBuilder();
	}
	
	@SuppressWarnings("unchecked")
	public List<ActivityBusinessRelDto<Object>> retrieveResult(){
		List<ActivityBusinessRelDto<Object>> relDtoList = Lists.newArrayList();
		ActivityBusinessRelDto<Object> relDto = null;
		for(ActivityBusinessRel busiRel : busiRelList){
			relDto = BeanMapper.map(busiRel, ActivityBusinessRelDto.class);
			relDto.setConcreteBusiness(getConcreteInfo(busiRel.getBusinessId(), busiRel.getBusinessType()));
			relDtoList.add(relDto);
		}
		return relDtoList;
	}
	
	public void loadBusiRelList(List<ActivityBusinessRel> busiRelList){
		List<String> busiIds = null;
		this.busiRelList = busiRelList;
		for(ActivityBusinessRel busiRel : busiRelList){
			busiIds = this.relIdMap.get(busiRel.getBusinessType());
			if(CollectionUtils.isEmpty(busiIds)){
				busiIds = Lists.newArrayList();
				this.relIdMap.put(busiRel.getBusinessType(), busiIds);
			}
			busiIds.add(busiRel.getBusinessId());
		}
	}

	
	public Map<ActivityBusinessType, List<String>> getRelIdMap() {
		return relIdMap;
	}
	
	/**
	 * @Description: 加载区域列表
	 * @param areaList   
	 * @author maojj
	 * @date 2017年4月20日
	 */
	public void loadAreaList(List<Address> areaList){
		if(CollectionUtils.isEmpty(areaList)){
			return;
		}
		for(Address addr : areaList){
			this.addressMap.put(String.valueOf(addr.getId()), addr);
			this.limitRangeBuilder.append(addr.getId()).append(",");
		}
	}
	
	/**
	 * @Description: 加载店铺列表
	 * @param storeList   
	 * @author maojj
	 * @date 2017年4月20日
	 */
	public void loadStoreList(List<StoreInfo> storeList){
		if(CollectionUtils.isEmpty(storeList)){
			return;
		}
		for(StoreInfo storeInfo : storeList){
			this.storeInfoMap.put(storeInfo.getId(), storeInfo);
			this.limitRangeBuilder.append(storeInfo.getProvinceId())
					.append(",").append(storeInfo.getCityId())
					.append(",").append(storeInfo.getId()).append(",");
		}
	}
	
	/**
	 * @Description: 加载商品类目列表
	 * @param spuCtgList   
	 * @author maojj
	 * @date 2017年4月20日
	 */
	public void loadSpuCtgList(List<GoodsSpuCategory> spuCtgList){
		if(CollectionUtils.isEmpty(spuCtgList)){
			return;
		}
		for(GoodsSpuCategory spuCtg : spuCtgList){
			this.spuCtgMap.put(spuCtg.getId(), spuCtg);
		}
	}
	
	/**
	 * @Description: 加载店铺商品列表
	 * @param storeSkuList   
	 * @author maojj
	 * @date 2017年4月20日
	 */
	public void loadStoreSkuList(List<StoreSkuDto> storeSkuList){
		if(CollectionUtils.isEmpty(storeSkuList)){
			return;
		}
		for(StoreSkuDto storeSku : storeSkuList){
			this.storeSkuMap.put(storeSku.getId(), storeSku);
		}
	}
	
	public Object getConcreteInfo(String id,ActivityBusinessType busiType){
		Object concreteInfo = null;
		switch (busiType) {
			case CITY:
			case PROVINCE:
				concreteInfo = this.addressMap.get(id);
				break;
			case STORE:
				concreteInfo = this.storeInfoMap.get(id);			
				break;
			case SKU_CATEGORY:
				concreteInfo = this.spuCtgMap.get(id);
				break;
			case SKU:
				concreteInfo = this.storeSkuMap.get(id);
				break;

			default:
				break;
		}
		return concreteInfo;
	}

	public String getLimitRangeIds() {
		return this.limitRangeBuilder.toString();
	}
}
