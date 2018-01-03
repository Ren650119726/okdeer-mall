package com.okdeer.mall.activity.discount.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.store.dto.GoodsStoreActivitySkuDto;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.common.utils.JsonDateUtil;
import com.okdeer.mall.activity.coupons.enums.ActivityDiscountItemRelType;
import com.okdeer.mall.activity.discount.dto.ActivityCloudItemReultDto;
import com.okdeer.mall.activity.discount.dto.ActivityCloudStoreParamDto;
import com.okdeer.mall.activity.discount.dto.ActivityCloudStoreResultDto;
import com.okdeer.mall.activity.discount.dto.ActivityGoodsParamDto;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountItem;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountItemRel;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountMultiItem;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountType;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountItemMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountItemRelMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMultiItemMapper;
import com.okdeer.mall.activity.discount.service.ActivityCloudStoreService;
import com.okdeer.mall.activity.dto.ActivityParamDto;


/**
 * ClassName: ActivityCloudStoreServiceImpl 
 * @Description: 便利店活动服务实现类
 * @author tuzhd
 * @date 2017年12月7日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.7				2017-12-07         tuzhd			 便利店活动服务实现类
 */
@Service
public class ActivityCloudStoreServiceImpl implements ActivityCloudStoreService {
	
	/**
	 * 店铺活动梯度表
	 */
	@Resource
	private ActivityDiscountItemMapper activityDiscountItemMapper;
	
	/**
	 * 梯度 业务数据关联表
	 */
	@Resource
	private ActivityDiscountItemRelMapper activityDiscountItemRelMapper;
	
	/**
	 * N件X元三级关联表
	 */
	@Resource
	private ActivityDiscountMultiItemMapper activityDiscountMultiItemMapper;
	
	/**
	 * 活动与店铺等业务关系
	 */
	@Resource
	private ActivityDiscountMapper activityDiscountMapper;
	
	/**
	 * 店铺基本信息service
	 */
	@Reference(version = "1.0.0")
	private StoreInfoServiceApi storeInfoService;
	 
	/**
	 * 店铺商品Api
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

	/**
	 * @Description: 根据店铺及商品查询店铺活动信息，满赠、加价购、N件X元
	 * @param paramDto  
	 * @return ActivityCloudStoreResultDto  
	 * @author tuzhd
	 * @throws Exception 
	 * @date 2017年12月7日
	 */
	@Override
	public ActivityCloudStoreResultDto getCloudStoreActivity(ActivityCloudStoreParamDto paramDto) throws Exception {
		//根据店铺ID获取店铺信息
		StoreInfo store = paramDto.getStoreInfo();
		if(store == null || CollectionUtils.isEmpty(paramDto.getStoreSkuIdList())){
			paramDto.setStoreSkuIdList(Lists.newArrayList());
		}
		//1、查询符合要求的活动
		List<ActivityDiscount> actList = activityDiscountMapper.findByStoreAndArea(createActivityParam(paramDto));
		if(CollectionUtils.isEmpty(actList)){
			return null;
		}
		List<String> idList = Lists.newArrayList();
		actList.forEach(e -> idList.add(e.getId()));
		//2、根据商品查询其一级导航分类 商品id,导航分类id
		Map<String,List<String>> skuMap = goodsStoreSkuServiceApi.findCategoryBySkuIds(paramDto.getStoreSkuIdList(),paramDto.getStoreId());
		//3、根据店铺查询符合要求的活动梯度 
		List<ActivityDiscountItem> itemList = activityDiscountItemMapper.findByActivityIdList(idList);
		//4、查询业务关联及N件X元的梯度表
		List<ActivityDiscountItemRel> relList = activityDiscountItemRelMapper.findByActivityIdList(idList);
		List<ActivityDiscountMultiItem> multiList = activityDiscountMultiItemMapper.findByActivityIdList(idList);
		//5、组装出符合要求的梯度集合及其商品
		return combActivityItemList(actList, itemList, relList, multiList, paramDto.getStoreSkuIdList(), skuMap);
		
	}
	
	/**
	 * @Description: 组合梯度信息
	 * @return List<ActivityDiscountItemDto>  
	 * @author tuzhd
	 * @date 2017年12月8日
	 */
	private ActivityCloudStoreResultDto combActivityItemList(List<ActivityDiscount> actList,
			List<ActivityDiscountItem> itemList,List<ActivityDiscountItemRel> relList,
			List<ActivityDiscountMultiItem> multiList,List<String> skuList,Map<String,List<String>> skuMap){

		//满赠梯度集合
		List<ActivityCloudItemReultDto> giveItemList = Lists.newArrayList();
		//加价购梯度集合
		List<ActivityCloudItemReultDto> priceItemList = Lists.newArrayList();
		//N件X元梯度集合
		List<ActivityCloudItemReultDto> multiItemList = Lists.newArrayList();
		
		//梯度集合
		if(CollectionUtils.isEmpty(itemList)){
			return null;
		}
		for(ActivityDiscount discount : actList){
			for(ActivityDiscountItem item : itemList){
				if(item.getActivityId().equals(discount.getId())){
					//获取梯度信息
					ActivityCloudItemReultDto itemResult = BeanMapper.map(item, ActivityCloudItemReultDto.class);
					//设置活动类型
					itemResult.setType(discount.getType().ordinal());
					//组合符合梯度的购物车商品
					itemResult.setSkuIdList(combStoreSku(item, relList, skuList, skuMap));
					
					switch (discount.getType()) {
					case MMS:
						giveItemList.add(itemResult);
						break;
					case JJG:
						priceItemList.add(itemResult);
						break;
					case NJXY:
						//组装N件x元
						multiItemList.addAll(combMultiItem(itemResult, multiList));
						break;
					default:
						break;
					}
				}
			}
		}
		return new ActivityCloudStoreResultDto(giveItemList, priceItemList, multiItemList);
	}
	
	/**
	 * @Description: 组合N件X元的三级梯度
	 * @param itemResult
	 * @param multiList
	 * @author tuzhd
	 * @date 2017年12月8日
	 */
	private List<ActivityCloudItemReultDto> combMultiItem(ActivityCloudItemReultDto itemResult,
														List<ActivityDiscountMultiItem> multiList){
		List<ActivityCloudItemReultDto> items = Lists.newArrayList();
		if(CollectionUtils.isNotEmpty(multiList)){
			multiList.forEach(e -> {
				//符合该梯度 的三级梯度 生成三级梯度集合
				if(e.getActivityItemId().equals(itemResult.getId())){
					ActivityCloudItemReultDto item = new ActivityCloudItemReultDto();
					BeanMapper.copy(itemResult, item);
					item.setActivityMultiItemId(e.getId());
					item.setPiece(e.getPiece());
					item.setPrice(JsonDateUtil.priceConvertToString(e.getPrice()));
					item.setName(e.getName());
					items.add(item);
				}
			});
		}
		return items;
	}
	
	/**
	 * 
	 * @Description: 组合符合梯度的购物车商品
	 * @param item 梯度
	 * @param relList 与梯度管理的业务信息
	 * @param skuList 购物车商品
	 * @param skuMap 购物车商品与导航分类的映射
	 * @return List<>  
	 * @author tuzhd
	 * @date 2017年12月8日
	 */
	private List<String> combStoreSku(ActivityDiscountItem item,List<ActivityDiscountItemRel> relList,
										List<String> skuList,Map<String,List<String>> skuMap){
		//包含的购物车商品
		Set<String> containList = new HashSet<>();
		//如果是商品限制：0:不限，1：指定导航分类，2：指定商品
		if(item.getLimitSku() == 0){
			return new ArrayList<>();
		}
		if(CollectionUtils.isEmpty(relList)){
			return new ArrayList<>();
		}
		
		if(skuMap == null){
			return new ArrayList<>();
		}
		//导航分类是否包含0包含 1不包
		List<String> goodsSkuIds = Lists.newLinkedList();
		List<String> cateGoryIds = Lists.newLinkedList();
		//拆分提交商品的 skuId与分类  对 storeSkuId对应
		Map<String,List<String>> skuIdMap = new HashMap<>();
		Map<String,List<String>> cateGoryMap = new HashMap<>();
		for(Map.Entry<String,List<String>> entry : skuMap.entrySet()){
			String skuId = entry.getValue().get(0);
			goodsSkuIds.add(skuId);
			//skuId下的 storeSkuId对应
			if(skuIdMap.get(skuId) == null){
				skuIdMap.put(skuId, Lists.newArrayList());
			}
			skuIdMap.get(skuId).add(entry.getKey())	;
			
			if(entry.getValue().size() > 1){
				//处理多导航对应同一种分类，也就是多对多关系
				for(int i=1 ;i < entry.getValue().size() ;i++){
					String cateGory = entry.getValue().get(i);
					cateGoryIds.add(cateGory);
					//分类下的 storeSkuId对应
					if(cateGoryMap.get(cateGory) == null){
						cateGoryMap.put(cateGory, Lists.newArrayList());
					}
					cateGoryMap.get(cateGory).add(entry.getKey());
				}
			}
		}
		
		//不包含的业务id
		List<String> itemRelIds = Lists.newArrayList();
		relList.forEach(rel -> {
			//符合这个梯度的商品，购物车的skuList包含， categoryInvert为包含，type不为赠品 及加价购商品
			if(item.getId().equals(rel.getActivityItemId()) && rel.getType() == ActivityDiscountItemRelType.NORMAL_GOODS.ordinal()){
				//不包含 1
				if(item.getCategoryInvert() == 1){
					itemRelIds.add(rel.getBusinessId());
					
				}else{
				
					//如果是商品限制：0:不限，1：指定导航分类，2：指定商品 且包含 0
					if(item.getLimitSku() == 2
							&& goodsSkuIds.contains(rel.getBusinessId())){
						containList.addAll(skuIdMap.get(rel.getBusinessId()));
						
					}else if(item.getLimitSku() == 1 
							&& cateGoryIds.contains(rel.getBusinessId())){
						containList.addAll(cateGoryMap.get(rel.getBusinessId()));
					}
				}
			}
		});
		
		//被包含 且不能参加的集合
		Set<String> notConList = new HashSet<>();
		//处理不包含的情况 提交的商品类型或水库ID不在梯度的包含内，则添加进去
		if(CollectionUtils.isNotEmpty(itemRelIds)){
			//如果是商品限制：0:不限，1：指定导航分类，2：指定商品 
			if(item.getLimitSku() == 2){
				for(String skuId : skuIdMap.keySet()){
					if(!itemRelIds.contains(skuId)){
						containList.addAll(skuIdMap.get(skuId));
					}else{
						notConList.addAll(skuIdMap.get(skuId));
					}
				}
				
			}else if(item.getLimitSku() == 1 ){
				for(String cateId : cateGoryMap.keySet()){
					if(!itemRelIds.contains(cateId)){
						containList.addAll(cateGoryMap.get(cateId));
					}else{
						notConList.addAll(cateGoryMap.get(cateId));
					}
				}
			}
		}
		//移除掉不能参加的
		containList.removeAll(notConList);
		return new ArrayList<String>(containList);
	}
	
	/**
	 * @Description: 创建满赠/加价购/N件X元活动查询参数
	 * @param store   
	 * @author tuzhd
	 * @date 2017年12月7日
	 */
	private ActivityParamDto createActivityParam(ActivityCloudStoreParamDto paramDto){
		StoreInfo store = paramDto.getStoreInfo();
		ActivityParamDto actDto = new ActivityParamDto();
		actDto.setCityId(store.getCityId());
		actDto.setProvinceId(store.getProvinceId());
		actDto.setStoreId(store.getId());
		actDto.setActivityId(paramDto.getActivityId());
		List<ActivityDiscountType> typeList = Lists.newArrayList();
		typeList.add(ActivityDiscountType.JJG);
		typeList.add(ActivityDiscountType.NJXY);
		typeList.add(ActivityDiscountType.MMS);
		actDto.setTypeList(typeList);
		return actDto;
	}

	/**
	 * @Description: 获得买满送的商品列表
	 * @param param   
	 * @author tuzhd
	 * @date 2017年12月22日
	 */
	@Override
	public List<GoodsStoreActivitySkuDto> getGiveActivityGoods(ActivityGoodsParamDto param){
		//查询赠品信息并将标准库id转为店铺商品id
		return activityDiscountItemRelMapper.findGiveAddPriceGoods(param);
		
		
	}
	
	/**
	 * @Description: 获取梯度下商品列表
	 * @param param   
	 * @author tuzhd
	 * @date 2017年12月22日
	 */
	@Override
	public PageUtils<GoodsStoreActivitySkuDto> getActivityItemGoods(ActivityGoodsParamDto param){
		ActivityDiscountItem item =activityDiscountItemMapper.findById(param.getActivityItemId());
		if(item == null){
			return null;
		}
		
		param.setLimitSku(item.getLimitSku());
		param.setCategoryInvert(item.getCategoryInvert());
		//如果是商品限制：0:不限，1：指定导航分类，2：指定商品
		if(item.getLimitSku() != 0){
			List<ActivityDiscountItemRel> list = activityDiscountItemRelMapper.
					findByActivityId(param.getActivityId(), param.getActivityItemId());
			//存储梯度或正常商品id
			List<String> ids = Lists.newArrayList();
			if(CollectionUtils.isEmpty(list)){
				return null;
			}
			//组装出对应的业务id
			for(ActivityDiscountItemRel rel : list){
				if(rel.getType() == ActivityDiscountItemRelType.NORMAL_GOODS.ordinal()){
					ids.add(rel.getBusinessId());
				}
			}
			param.setIds(ids);
		}
		
		
		PageHelper.startPage(param.getPageNumber(), param.getPageSize(), true);
		List<GoodsStoreActivitySkuDto> list  = activityDiscountItemRelMapper.findActivityGoods(param);
		return new PageUtils<>(list);
		
	}
}
