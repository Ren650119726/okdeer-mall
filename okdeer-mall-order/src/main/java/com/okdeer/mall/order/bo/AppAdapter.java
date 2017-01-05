package com.okdeer.mall.order.bo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.order.dto.CurrentStoreInfo;
import com.okdeer.mall.order.dto.CurrentStoreSkuDto;

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

	/**
	 * @Description: 转换店铺信息给APP
	 * @param storeInfo
	 * @return   
	 * @author maojj
	 * @date 2017年1月5日
	 */
	public static CurrentStoreInfo convert(StoreInfo storeInfo){
		if(storeInfo == null){
			return null;
		}
		CurrentStoreInfo dto = BeanMapper.map(storeInfo, CurrentStoreInfo.class);
		if(storeInfo.getStoreInfoExt() != null){
			BeanMapper.copy(storeInfo.getStoreInfoExt(), dto);
		}
		if(storeInfo.getStoreInfoServiceExt() != null){
			BeanMapper.copy(storeInfo.getStoreInfoServiceExt(), dto);
		}
		return dto;
	}
	
	public static List<CurrentStoreSkuDto> convert(StoreSkuParserBo parserBo){
		if(parserBo == null || parserBo.getCurrentSkuMap() == null || CollectionUtils.isEmpty(parserBo.getCurrentSkuMap().values())){
			return null;
		}
		List<CurrentStoreSkuDto> dtoList = new ArrayList<CurrentStoreSkuDto>();
		CurrentStoreSkuDto dto = null;
		for(CurrentStoreSkuBo skuBo : parserBo.getCurrentSkuMap().values()){
			dto = new CurrentStoreSkuDto();
			dto.setId(skuBo.getId());
			dto.setName(skuBo.getName());
			dto.setOnlinePrice(skuBo.getOnlinePrice());
			dto.setActPrice(skuBo.getActPrice());
			dto.setTradeMax(skuBo.getTradeMax());
			dto.setSellable(skuBo.getSellable());
			dto.setLocked(skuBo.getLocked());
			dto.setLimitKind(skuBo.getLimitKind());
			dto.setActivityType(skuBo.getActivityType());
			dto.setOnline(skuBo.getOnline().ordinal());
			dto.setLimitBuyNum(skuBo.getLimitBuyNum());
			dto.setUpdateTime(skuBo.getUpdateTime());
			
			dtoList.add(dto);
		}
		return dtoList;
	}
}
