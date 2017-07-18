package com.okdeer.mall.order.bo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.assemble.GoodsStoreSkuAssembleApi;
import com.okdeer.archive.goods.assemble.dto.GoodsStoreAssembleDto;
import com.okdeer.archive.goods.assemble.dto.GoodsStoreSkuAssembleDto;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.order.entity.TradeOrderComboSnapshot;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;

/**
 * ClassName: ComboSnapshotAdapter 
 * @Description: 组合快照适配器
 * @author maojj
 * @date 2017年7月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.3 		2017年7月17日				maojj
 */
@Component
public class ComboSnapshotAdapter {
	
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuAssembleApi goodsStoreSkuAssembleApi;

	/**
	 * @Description: 根据组合商品列表查询组合商品成分明细
	 * @param comboSkuIds
	 * @return
	 * @throws Exception   
	 * @author maojj
	 * @date 2017年7月17日
	 */
	public List<TradeOrderComboSnapshot> findByComboSkuIds(List<String> comboSkuIds) throws Exception{
		List<TradeOrderComboSnapshot> comboDetailList = Lists.newArrayList();
		TradeOrderComboSnapshot comboSnapshot = null;
		List<GoodsStoreAssembleDto> comboDtoList = goodsStoreSkuAssembleApi
				.findByAssembleSkuIds(comboSkuIds);
		for(GoodsStoreAssembleDto dto : comboDtoList){
			for(GoodsStoreSkuAssembleDto comboSku : dto.getGoodsStoreSkuAssembleDtos()){
				comboSnapshot = BeanMapper.map(comboSku, TradeOrderComboSnapshot.class);
				comboSnapshot.setComboSkuId(comboSku.getAssembleSkuId());
				comboDetailList.add(comboSnapshot);
			}
		}
		return comboDetailList;
	} 
	
	/**
	 * @Description: 从订单明细中获取组合成分明细
	 * @param orderItemList
	 * @return
	 * @throws Exception   
	 * @author maojj
	 * @date 2017年7月17日
	 */
	public List<TradeOrderComboSnapshot> findByTradeOrderItemList(List<TradeOrderItem> orderItemList) throws Exception{
		List<String> comboSkuIds = Lists.newArrayList();
		for(TradeOrderItem orderItem : orderItemList){
			if(orderItem.getSpuType() == SpuTypeEnum.assembleSpu){
				comboSkuIds.add(orderItem.getStoreSkuId());
			}
		}
		if(CollectionUtils.isEmpty(comboSkuIds)){
			return new ArrayList<TradeOrderComboSnapshot>();
		}else{
			return findByComboSkuIds(comboSkuIds);
		}
	}
	
	/**
	 * @Description: 根据退款单明细查询组合商品成分列表
	 * @param refundsItemList
	 * @return
	 * @throws Exception   
	 * @author maojj
	 * @date 2017年7月17日
	 */
	public List<TradeOrderComboSnapshot> findByRefundsItemList(List<TradeOrderRefundsItem> refundsItemList) throws Exception{
		List<String> comboSkuIds = Lists.newArrayList();
		for(TradeOrderRefundsItem refundsItem : refundsItemList){
			if(refundsItem.getSpuType() == SpuTypeEnum.assembleSpu){
				comboSkuIds.add(refundsItem.getStoreSkuId());
			}
		}
		if(CollectionUtils.isEmpty(comboSkuIds)){
			return new ArrayList<TradeOrderComboSnapshot>();
		}else{
			return findByComboSkuIds(comboSkuIds);
		}
	}
}
