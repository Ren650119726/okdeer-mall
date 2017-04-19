/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.api.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.advert.bo.ActivityAdvertStoreSkuBo;
import com.okdeer.mall.activity.advert.dto.ActivityAdverModelDto;
import com.okdeer.mall.activity.advert.dto.ActivityAdvertDto;
import com.okdeer.mall.activity.advert.dto.ActivityAdvertStoreSkuDto;
import com.okdeer.mall.activity.advert.entity.ActivityAdvert;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertModel;
import com.okdeer.mall.activity.advert.service.ActivityAdvertApi;
import com.okdeer.mall.activity.advert.service.ActivityAdvertModelService;
import com.okdeer.mall.activity.advert.service.ActivityAdvertService;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsService;
import com.okdeer.mall.activity.coupons.service.ActivitySaleService;
import com.okdeer.mall.activity.prize.entity.ActivityLuckDraw;
import com.okdeer.mall.activity.prize.service.ActivityLuckDrawService;
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
	 * 广告活动与特惠或低价关联Service
	 */
	@Autowired
	ActivitySaleService activitySaleService;
	
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
	 * @param userId    用户id
	 * @return void  
	 * @author tuzhd
	 * @throws Exception 
	 * @date 2017年4月18日
	 */
	public void addActivityAdvert(ActivityAdvertDto activityAdvertDto) throws Exception{
		activityAdvertService.addActivityAdvert(activityAdvertDto);
	}

	@Override
	public List<ActivityAdvert> findActivityListByStatus(String status) {
		String[] statusList = status.split(",");
		return activityAdvertService.findActivityListByStatus(Arrays.asList(statusList));
	}
	@Override
	public ActivityAdverModelDto findAdvertByActivityAdvertId(String activityId) throws Exception {
		ActivityAdvert advert = activityAdvertService.findById(activityId);
		//获取广告活动模块表list
		ActivityAdvertModel model = new ActivityAdvertModel();
		model.setActivityAdvertId(advert.getId());
		List<ActivityAdvertModel> modelList = activityAdvertModelService.findModelList(model);
		ActivityAdverModelDto result = new ActivityAdverModelDto();
		if(!modelList.isEmpty() && modelList.size()>0){
			for(ActivityAdvertModel advertModel:modelList){
				//根据不同的模块类型获取关联的信息
				getModelInfo(advertModel,result);
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
	private ActivityAdverModelDto getModelInfo(ActivityAdvertModel advertModel, ActivityAdverModelDto result) {
		
		//模块类型 0、指定便利店商品 1、指定店铺促销活动、2、指定服务店商品、3、指定领券活动、4、执行抽奖活动
		int modelType = advertModel.getModelType().ordinal();
		switch(modelType){
			case 0:
				getCloudStoreInfo(advertModel,result);
			case 1:
				getSaleInfo(advertModel,result);
			case 2:
				getServiceStoreInfo(advertModel,result);
			case 3:
				getCouponInfo(advertModel,result);
			case 4:
				getDrawInfo(advertModel,result);
		}
		return result;
		
	}

	/**
	 * @Description: 获取抽奖活动信息
	 * @param advertModel   
	 * @author xuzq01
	 * @param result 
	 * @date 2017年4月18日
	 */
	private void getDrawInfo(ActivityAdvertModel advertModel, ActivityAdverModelDto result) {
		//获取广告活动与抽奖关联信息
		ActivityLuckDraw draw = activityLuckDrawService.findLuckDrawByModelId(advertModel.getId(), advertModel.getActivityAdvertId());
		result.setDraw(draw);
	}

	/**
	 * @Description: 获取领券活动信息
	 * @param advertModel   
	 * @author xuzq01
	 * @param result 
	 * @date 2017年4月18日
	 */
	private void getCouponInfo(ActivityAdvertModel advertModel, ActivityAdverModelDto result) {
		ActivityCollectCoupons coupon = activityCollectCouponsService.findCollectCouponsByModelId(advertModel.getId(), advertModel.getActivityAdvertId());
		result.setCoupon(coupon);
	}

	/**
	 * @Description: 获取服务店关联商品列表
	 * @param advertModel   
	 * @author xuzq01
	 * @param result 
	 * @date 2017年4月18日
	 */
	private void getServiceStoreInfo(ActivityAdvertModel advertModel, ActivityAdverModelDto result) {
		
		List<ActivityAdvertStoreSkuBo> serviceSkuList = BeanMapper.mapList(columnAdvertGoodsService.findServiceSkuByModelId(advertModel.getId(), advertModel.getActivityAdvertId()), ActivityAdvertStoreSkuBo.class);
		//再将bo转换成dto 放入封装对象
		result.setServiceSkuList(BeanMapper.mapList(serviceSkuList, ActivityAdvertStoreSkuDto.class));
	}
	/**
	 * @Description: 获取便利店商品关联列表
	 * @param advertModel   
	 * @author xuzq01
	 * @param result 
	 * @date 2017年4月18日
	 */
	private void getCloudStoreInfo(ActivityAdvertModel advertModel, ActivityAdverModelDto result) {
		List<ActivityAdvertStoreSkuBo> cloudSkuList = BeanMapper.mapList(columnAdvertGoodsService.findCloudSkuByModelId(advertModel.getId(), advertModel.getActivityAdvertId()), ActivityAdvertStoreSkuBo.class);
		//再将bo转换成dto 放入封装对象
		result.setColoudSkuList(BeanMapper.mapList(cloudSkuList, ActivityAdvertStoreSkuDto.class));
	}
	
	/**
	 * @Description: 获取关联的优惠信息
	 * @param advertModel   
	 * @author xuzq01
	 * @param result 
	 * @date 2017年4月18日
	 */
	private void getSaleInfo(ActivityAdvertModel advertModel, ActivityAdverModelDto result) {
		ActivitySale sale = activitySaleService.findActivitySaleByModelId(advertModel.getModelNo().toString(), advertModel.getActivityAdvertId());
		result.setSale(sale);
	}
}
