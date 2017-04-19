/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.fasterxml.jackson.databind.JavaType;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.advert.dto.ActivityAdvertDto;
import com.okdeer.mall.activity.advert.entity.ActivityAdvert;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertCoupons;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertModel;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertSale;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertStore;
import com.okdeer.mall.activity.advert.entity.ColumnAdvertGoods;
import com.okdeer.mall.activity.advert.enums.ModelTypeEnum;
import com.okdeer.mall.activity.advert.mapper.ActivityAdvertMapper;
import com.okdeer.mall.activity.advert.service.ActivityAdvertCouponsService;
import com.okdeer.mall.activity.advert.service.ActivityAdvertModelService;
import com.okdeer.mall.activity.advert.service.ActivityAdvertSaleService;
import com.okdeer.mall.activity.advert.service.ActivityAdvertService;
import com.okdeer.mall.activity.advert.service.ActivityAdvertStoreService;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.prize.entity.ActivityAdvertDraw;
import com.okdeer.mall.activity.prize.service.ActivityAdvertDrawService;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;
import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.mall.operate.advert.service.ColumnAdvertGoodsService;

/**
 * ClassName: ActivityAdvertServiceImpl 
 * @Description: 
 * @author xuzq01
 * @date 2017年4月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class ActivityAdvertServiceImpl extends BaseServiceImpl implements ActivityAdvertService {
	
	/**
	 * H5活动Service
	 */
	@Autowired
	ActivityAdvertMapper activityAdvertMapper;
	
	@Autowired
	private ActivityAdvertModelService activityAdvertModelService;
	@Autowired
	private ActivityAdvertSaleService activityAdvertSaleService;
	@Autowired
	private ActivityAdvertCouponsService activityAdvertCouponsService;
	@Autowired
	private ActivityAdvertStoreService activityAdvertStoreService;
	@Autowired
	private ColumnAdvertGoodsService columnAdvertGoodsService;
	@Autowired
	private ActivityAdvertDrawService activityAdvertDrawService;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return activityAdvertMapper;
	}

	@Override
	public int findCountByName(String advertName) {
		return activityAdvertMapper.findCountByName(advertName);
	}

	@Override
	public PageUtils<ActivityAdvert> findActivityAdvertList(ActivityAdvert activityAdvert, int pageNumber,
			int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ActivityAdvert> result = activityAdvertMapper.findActivityAdvertList(activityAdvert);
		return new PageUtils<ActivityAdvert>(result);
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
	@Transactional(rollbackFor = Exception.class)
	public void addActivityAdvert(ActivityAdvertDto activityAdvertDto) throws Exception{
		
		String userId = activityAdvertDto.getCreateUserId();
		ActivityAdvert advertActivity =  BeanMapper.map(activityAdvertDto, ActivityAdvert.class);
		advertActivity.setCreateTime(new Date());
		advertActivity.setId(UuidUtils.getUuid());
		advertActivity.setStatus(SeckillStatusEnum.noStart);
		advertActivity.setCreateTime(new Date());
		advertActivity.setUpdateTime(new Date());
		advertActivity.setDisabled(Disabled.valid);
		//保存活动信息
		activityAdvertMapper.add(advertActivity);
		
		//选择店铺信息
		if(advertActivity.getAreaType() == AreaType.store){
			addAdvertStore(activityAdvertDto.getStoreIds(), advertActivity.getId());
		}
		//保存模块信息,模块信息存在
		String modelListJson = activityAdvertDto.getModelListJson();
		//存在模块信息
		if(StringUtils.isNotBlank(modelListJson)){
			JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();
			JavaType javaType = jsonMapper.contructCollectionType(ArrayList.class, ActivityAdvertModel.class);
			List<ActivityAdvertModel> array = jsonMapper.fromJson(modelListJson, javaType);
			//添加活动模块信息
			addAdvertModels(array, advertActivity,userId);
		}
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
	 * @param array 模块集合
	 * @param advertActivity  活动信息  
	 * @param userId   创建用户id
	 * @author tuzhd
	 * @throws Exception 
	 * @date 2017年4月17日
	 */
	public void addAdvertModels(List<ActivityAdvertModel> array,ActivityAdvert advertActivity,String userId) throws Exception{
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
					sale.setSaleType(ActivityTypeEnum.enumValueOf(Integer.parseInt(md.getModelIdStr())));
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
	public List<ActivityAdvert> findActivityListByStatus(List<String> statusList) {
		return activityAdvertMapper.findActivityListByStatus(statusList);
	}

}
