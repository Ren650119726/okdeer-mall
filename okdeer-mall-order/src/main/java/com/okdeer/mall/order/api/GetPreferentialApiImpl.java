package com.okdeer.mall.order.api;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.common.utils.EnumAdapter;
import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.discount.entity.PreferentialVo;
import com.okdeer.mall.activity.discount.service.GetPreferentialApi;
import com.okdeer.mall.activity.dto.FavourParamDto;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.service.MemberConsigneeAddressService;
import com.okdeer.mall.order.service.GetPreferentialService;

/**
 * ClassName: GetPreferentialApiImpl 
 * @Description: 获取优惠列表实现类
 * @author maojj
 * @date 2017年2月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.1 			2017年2月17日				maojj
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.discount.service.GetPreferentialApi")
public class GetPreferentialApiImpl implements GetPreferentialApi {
	
	@Resource
	private GetPreferentialService getPreferentialService;
	
	/**
	 * 店铺商品Api
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;
	
	/**
	 * 收货地址服务接口
	 */
	@Resource
	private MemberConsigneeAddressService memberConsigneeAddressService;

	@Override
	public PreferentialVo findPreferentialByUser(FavourParamDto paramDto) throws Exception {
		FavourParamBO paramBo = BeanMapper.map(paramDto, FavourParamBO.class);
		paramBo.setCouponsType(EnumAdapter.convert(paramDto.getStoreType()));
		paramBo.setClientType(EnumAdapter.convert(paramDto.getChannel()));
		// 请求商品列表
		List<String> skuIdList = paramDto.getSkuIdList();
		//到店服务商品根据店铺地址查询
		if(paramDto.getStoreType() == StoreTypeEnum.SERVICE_STORE && CollectionUtils.isNotEmpty(skuIdList)&& skuIdList.size() == 1){
			// 注：到店消费商品一次只能购买一件。所以判定是否为到店商品的前提条件是:请求时只有一件商品
			GoodsStoreSku sku = goodsStoreSkuServiceApi.getById(skuIdList.get(0));
			if (SpuTypeEnum.fwdDdxfSpu == sku.getSpuTypeEnum()) {
				MemberConsigneeAddress address = memberConsigneeAddressService.findByStoreId(paramDto.getStoreId());
				if (address != null) {
					paramBo.setAddressId(address.getId());
				}
			}
		}
		
		return getPreferentialService.findPreferentialByUser(paramBo);
	}

}
