/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.api.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.fasterxml.jackson.databind.JavaType;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.mall.activity.advert.dto.ActivityAdverModelDto;
import com.okdeer.mall.activity.advert.dto.ActivityAdvertDto;
import com.okdeer.mall.activity.advert.dto.ActivityAdvertStoreSkuDto;
import com.okdeer.mall.activity.advert.entity.ActivityAdvert;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertCoupons;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertModel;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertSale;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertStore;
import com.okdeer.mall.activity.advert.entity.ColumnAdvertGoods;
import com.okdeer.mall.activity.advert.enums.ModelTypeEnum;
import com.okdeer.mall.activity.advert.service.ActivityAdvertApi;
import com.okdeer.mall.activity.advert.service.ActivityAdvertCouponsService;
import com.okdeer.mall.activity.advert.service.ActivityAdvertModelService;
import com.okdeer.mall.activity.advert.service.ActivityAdvertSaleService;
import com.okdeer.mall.activity.advert.service.ActivityAdvertService;
import com.okdeer.mall.activity.advert.service.ActivityAdvertStoreService;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsService;
import com.okdeer.mall.activity.prize.entity.ActivityAdvertDraw;
import com.okdeer.mall.activity.prize.entity.ActivityLuckDraw;
import com.okdeer.mall.activity.prize.service.ActivityAdvertDrawService;
import com.okdeer.mall.activity.prize.service.ActivityLuckDrawService;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;
import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.mall.operate.advert.bo.ActivityAdvertStoreSkuBo;
import com.okdeer.mall.operate.advert.service.ColumnAdvertGoodsService;

/**
 * ClassName: ActivityAdvertStoreMapper 
 * @Description: 关联店铺信息对外接口类
 * @author tuzhd
 * @date 2017年4月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V2.2.2			2017-4-18			tuzhd			关联店铺信息对外接口类
 */
@Service(version="1.0.0")
public class ActivityAdvertApiImpl implements ActivityAdvertApi {
	
	/**
	 * H5活动Service
	 */
	@Autowired
	ActivityAdvertService activityAdvertService;
	/**
	 * 广告活动模块表Service
	 */
	@Autowired
	ActivityAdvertModelService activityAdvertModelService;
	
	
	/**
	 * 广告活动与抽奖关联表Service
	 */
	@Autowired
	ActivityLuckDrawService activityLuckDrawService;
	
	/**
	 * 广告活动代金劵管理表Service
	 */
	@Autowired
	ActivityCollectCouponsService activityCollectCouponsService;

	
	/**
	 * 活动商品中间表Service
	 */
	@Autowired
	ColumnAdvertGoodsService columnAdvertGoodsService;
	
	@Autowired
	private ActivityAdvertSaleService activityAdvertSaleService;
	@Autowired
	private ActivityAdvertCouponsService activityAdvertCouponsService;
	@Autowired
	private ActivityAdvertStoreService activityAdvertStoreService;
	@Autowired
	private ActivityAdvertDrawService activityAdvertDrawService;
	
	@Override
	public ActivityAdvert findById(String id) throws Exception {
		return activityAdvertService.findById(id);
	}

	@Override
	public int findCountByName(String advertName) {
		return activityAdvertService.findCountByName(advertName);
	}

	@Override
	public PageUtils<ActivityAdvert> findActivityAdvertList(ActivityAdvert activityAdvert, int pageNumber,
			int pageSize) {
		return activityAdvertService.findActivityAdvertList(activityAdvert, pageNumber, pageSize);
	}

	/**
	 * @Description: 保存新增活动信息
	 * @param activityAdvertDto 活动对象
	 * @return void  
	 * @author tuzhd
	 * @throws Exception 
	 * @date 2017年4月18日
	 */
	@Transactional(rollbackFor = Exception.class)
	public void addActivityAdvert(ActivityAdvertDto activityAdvertDto) throws Exception{
		//当前操作用户
		String userId = activityAdvertDto.getCreateUserId();
		ActivityAdvert advertActivity =  BeanMapper.map(activityAdvertDto, ActivityAdvert.class);
		advertActivity.setCreateTime(new Date());
		advertActivity.setId(UuidUtils.getUuid());
		advertActivity.setStatus(SeckillStatusEnum.noStart);
		advertActivity.setUpdateTime(new Date());
		advertActivity.setUpdateUserId(userId);
		advertActivity.setDisabled(Disabled.valid);
		//保存活动信息
		activityAdvertService.addActivityAdvert(advertActivity);
		
		//选择店铺信息
		if(advertActivity.getAreaType() == AreaType.store){
			addAdvertStore(activityAdvertDto.getStoreIds(), advertActivity.getId());
		}
		//保存模块信息,模块信息存在 //添加活动模块信息
		addAdvertModels(activityAdvertDto.getModelListJson(),advertActivity,userId);
		
	}

	/**
	 * @Description: 保存活动关联店铺信息
	 * @param storeIds  店铺id 
	 * @param activityAdvertId H5活动id 
	 * @author tuzhd
	 * @date 2017年4月18日
	 */
	private void addAdvertStore(String storeIds,String activityAdvertId){
		//店铺id不为空
		if(StringUtils.isNotBlank(storeIds)){
			String[] ids = storeIds.split(",");
			List<ActivityAdvertStore> list =  new ArrayList<ActivityAdvertStore>();
			for(String storeId:ids){
				ActivityAdvertStore store =  new ActivityAdvertStore();
				store.setActivityAdvertId(activityAdvertId);
				store.setStoreId(storeId);
				store.setId(UuidUtils.getUuid());
				list.add(store);
			}
			//存在需要插入的数据
 			if(list.size() > 0){
				activityAdvertStoreService.saveBatch(list);
			}
		}
	}

	/**
	 * @Description: 添加活动模块信息
	 * @param modelListJson 模块信息 json串
	 * @param advertActivity  活动信息  
	 * @param userId   创建用户id
	 * @author tuzhd
	 * @throws Exception 
	 * @date 2017年4月17日
	 */
	public void addAdvertModels(String modelListJson,ActivityAdvert advertActivity,String userId) throws Exception{
		//不存在模块信息
		if(StringUtils.isBlank(modelListJson)){
			return;
		}
		JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();
		JavaType javaType = jsonMapper.contructCollectionType(ArrayList.class, ActivityAdvertModel.class);
		List<ActivityAdvertModel> array = jsonMapper.fromJson(modelListJson, javaType);
		if(CollectionUtils.isNotEmpty(array) ){
			//根据模块类型进行保存
			for(ActivityAdvertModel md : array){
				md.setCreateTime(new Date());
				md.setCreateUserId(userId);
				md.setActivityAdvertId(advertActivity.getId());
				md.setId(UuidUtils.getUuid());
				activityAdvertModelService.addModel(md);
				//指定便利店商品
				if(ModelTypeEnum.CLOUD_STORE_GOOD == md.getModelType()){
					addBatchGoods(md, advertActivity);
				//指定店铺活动
				}else if(ModelTypeEnum.STORE_SALE_ACTIVITY== md.getModelType()){
					ActivityAdvertSale sale = new ActivityAdvertSale();
					sale.setActivityAdvertId(advertActivity.getId());
					sale.setId(UuidUtils.getUuid());
					sale.setModelId(md.getId());
					//当为店铺促销时放的销售类型
					sale.setSaleType(ActivityTypeEnum.valueOf(md.getModelIdStr()));
					activityAdvertSaleService.addSale(sale);
				//指定服务商品	
				}else if(ModelTypeEnum.SERVICE_STORE_GOOD ==  md.getModelType()){
					addBatchGoods(md, advertActivity);
				//指定领券活动
				}else if(ModelTypeEnum.COUPON_ACTIVITY==  md.getModelType()){
					ActivityAdvertCoupons coupons = new ActivityAdvertCoupons();
					coupons.setActivityAdvertId(advertActivity.getId());
					coupons.setId(UuidUtils.getUuid());
					coupons.setModelId(md.getId());
					coupons.setCollectCouponsId(md.getModelIdStr());
					activityAdvertCouponsService.addCoupons(coupons);
				//指定抽奖活动
				}else if(ModelTypeEnum.DRAW_PRIZE_ACTIVITY ==  md.getModelType()){
					ActivityAdvertDraw draw = new ActivityAdvertDraw();
					draw.setActivityAdvertId(advertActivity.getId());
					draw.setId(UuidUtils.getUuid());
					draw.setModelId(md.getId());
					draw.setLuckDrawId(md.getModelIdStr());
					activityAdvertDrawService.add(draw);
				}
			}
		}
	}
	
	/**
	* @Description: 保存新增活动信息
	 * @param activityAdvertDto 活动对象
	 * @param ActivityAdvert   修改活动实体
	 * @return void  
	 * @author tuzhd
	 * @throws Exception 
	 * @date 2017年4月18日
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateActivityAdvert(ActivityAdvertDto activityAdvertDto,ActivityAdvert advertActivity)throws Exception{
		//当前操作用户
		String userId = activityAdvertDto.getUpdateUserId();
		//修改当前H5活动信息
		activityAdvertService.update(advertActivity);
		
		//根据活动ID删除模块信息
		activityAdvertModelService.deleteByActivityAdvertId(advertActivity.getId());
		//根据活动ID删除关联店铺信息
		activityAdvertStoreService.deleteByActivityAdvertId(advertActivity.getId());
		//根据活动促销店铺信息
		activityAdvertSaleService.deleteByActivityAdvertId(advertActivity.getId());
		//根据活动ID删除关联的代金券信息
		activityAdvertCouponsService.deleteByActivityAdvertId(advertActivity.getId());
		//根据活动ID删除关联的抽奖活动信息
		activityAdvertDrawService.deleteByActivityAdvertId(advertActivity.getId());
		//删除关联商品信息
		columnAdvertGoodsService.deleteByActivityAdvertId(advertActivity.getId());
		//选择店铺信息
		if(advertActivity.getAreaType() == AreaType.store){
			addAdvertStore(activityAdvertDto.getStoreIds(), advertActivity.getId());
		}
		//保存模块信息,模块信息存在 //添加活动模块信息
		addAdvertModels(activityAdvertDto.getModelListJson(),advertActivity,userId);
		
	}
	
	/**
	 * @Description: 批量插入商品数据
	 * @param md 模块记录
	 * @param ad 活动记录  
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月17日
	 */
	private void addBatchGoods(ActivityAdvertModel md,ActivityAdvert ad){
		//存在模块记录及商品记录
		if(md != null && StringUtils.isNotEmpty(md.getModelIdStr())){
			String[] goodsids = md.getModelIdStr().split(",");
			String[] sort = md.getModelIdSortStr().split(",");
			List<ColumnAdvertGoods> listGoods = new ArrayList<ColumnAdvertGoods>();
			for(int i=0; i< goodsids.length;i++){
				ColumnAdvertGoods goods = new ColumnAdvertGoods();
				goods.setId(UuidUtils.getUuid());
				goods.setCreateTime(new Date());
				goods.setActivityAdvertId(ad.getId());
				goods.setModelId(md.getId());
				goods.setGoodsId(goodsids[i]);
				goods.setSort(Integer.parseInt(sort[i]));
				listGoods.add(goods);
			}
			//存在需要插入的数据
 			if(listGoods.size() > 0){
 				columnAdvertGoodsService.saveBatch(listGoods);
 			}
		}
	}
	
	@Override
	public List<ActivityAdvert> findActivityListByStatus(String status) {
		String[] statusList = status.split(",");
		return activityAdvertService.findActivityListByStatus(Arrays.asList(statusList));
	}
	@Override
	public List<ActivityAdverModelDto> findAdvertByActivityAdvertId(String activityId) throws Exception {
		//获取广告活动模块表list
		ActivityAdvertModel model = new ActivityAdvertModel();
		model.setActivityAdvertId(activityId);
		List<ActivityAdvertModel> modelList = activityAdvertModelService.findModelList(model);
		List<ActivityAdverModelDto> result = new ArrayList<ActivityAdverModelDto>();
		//查询模块集合不为空
		if(CollectionUtils.isNotEmpty(modelList)){
			
			//循环各个模块的信息 并合并到result结果集中
			for(ActivityAdvertModel advertModel:modelList){
				ActivityAdverModelDto dto = BeanMapper.map(advertModel, ActivityAdverModelDto.class);
				//根据不同的模块类型获取关联的信息
				getModelInfo(dto);
				
				//将单个模块放入到集合中
				result.add(dto);
			}
		}
		return result;
	}

	/**
	 * @Description: 获取所有关联模块的信息
	 * @param advertModel   
	 * @author xuzq01
	 * @param result 
	 * @return 
	 * @date 2017年4月18日
	 */
	private ActivityAdverModelDto getModelInfo(ActivityAdverModelDto result) {
		
		//模块类型 0、指定便利店商品 1、指定店铺促销活动、2、指定服务店商品、3、指定领券活动、4、执行抽奖活动
		int modelType = result.getModelType().ordinal();
		switch(modelType){
			case 0:
				getCloudStoreInfo(result); break;
			case 1:
				getSaleInfo(result); break;
			case 2:
				getServiceStoreInfo(result); break;
			case 3:
				getCouponInfo(result);break;
			case 4:
				getDrawInfo(result);break;
			default:break;
		}
		return result;
		
	}

	/**
	 * @Description: 获取抽奖活动信息
	 * @author xuzq01
	 * @param result 
	 * @date 2017年4月18日
	 */
	private void getDrawInfo(ActivityAdverModelDto result) {
		//获取广告活动与抽奖关联信息
		ActivityLuckDraw draw = activityLuckDrawService.findLuckDrawByModelId(result.getId(), result.getActivityAdvertId());
		result.setDraw(draw);
	}

	/**
	 * @Description: 获取领券活动信息
	 * @author xuzq01
	 * @param result 
	 * @date 2017年4月18日
	 */
	private void getCouponInfo(ActivityAdverModelDto result) {
		ActivityCollectCoupons coupon = activityCollectCouponsService.findCollectCouponsByModelId(result.getId(), result.getActivityAdvertId());
		result.setCoupon(coupon);
	}

	/**
	 * @Description: 获取服务店关联商品列表
	 * @author xuzq01
	 * @param result 
	 * @date 2017年4月18日
	 */
	private void getServiceStoreInfo(ActivityAdverModelDto result) {
		List<ActivityAdvertStoreSkuBo> serviceSkuList = columnAdvertGoodsService.findServiceSkuByModelId(result.getId(), result.getActivityAdvertId());
		
		//将bo转换成dto 放入封装对象
		result.setServiceSkuList(BeanMapper.mapList(serviceSkuList, ActivityAdvertStoreSkuDto.class));
	}
	/**
	 * @Description: 获取便利店商品关联列表
	 * @author xuzq01
	 * @param result 
	 * @date 2017年4月18日
	 */
	private void getCloudStoreInfo(ActivityAdverModelDto result) {
		List<ActivityAdvertStoreSkuBo> cloudSkuList = columnAdvertGoodsService.findCloudSkuByModelId(result.getId(), result.getActivityAdvertId());
		//将bo转换成dto 放入封装对象
		result.setColoudSkuList(BeanMapper.mapList(cloudSkuList, ActivityAdvertStoreSkuDto.class));
	}
	
	/**
	 * @Description: 获取关联的优惠信息
	 * @author xuzq01
	 * @param result 
	 * @date 2017年4月18日
	 */
	private void getSaleInfo(ActivityAdverModelDto result) {
		ActivityAdvertSale sale = activityAdvertSaleService.findSaleByIdNo(result.getModelNo(), result.getActivityAdvertId());
		result.setSale(sale);
	}

	@Override
	public void updateStatus(String id, SeckillStatusEnum status,String updateUserId) {
		
		activityAdvertService.updateBatchStatus(id, status, updateUserId, new Date());
	}
	
}
