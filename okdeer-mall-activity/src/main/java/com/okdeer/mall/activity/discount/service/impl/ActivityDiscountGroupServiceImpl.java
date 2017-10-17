/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityDiscountGroupMapper.java
 * @Date 2017-10-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.discount.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.goods.base.entity.GoodsSpuCategory;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.common.consts.StaticConstants;
import com.okdeer.mall.activity.discount.dto.ActivityGoodsGroupSkuDto;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountGroup;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountStatus;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountGroupMapper;
import com.okdeer.mall.activity.discount.service.ActivityDiscountGroupService;
/**
 * ClassName: ActivityDiscountGroupMapper 
 * @Description: 团购商品关联表实体操作类
 * @author tuzhd
 * @date 2017年10月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *	   2.6.3			2017-10-10			tuzhd			团购商品关联表实体操作类
 */

@Service
public class ActivityDiscountGroupServiceImpl extends BaseServiceImpl  implements ActivityDiscountGroupService {
	@Resource
	private ActivityDiscountGroupMapper activityDiscountGroupMapper;
	
	/**
	 * 商品图片
	 */
	@Value("${storeImagePrefix}")
	private String storeImagePrefix;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return activityDiscountGroupMapper;
	}
	
	/**
	 * @Description: 批量新增团购活动
	 * @param list   
	 * @author tuzhd
	 * @date 2017年10月12日
	 */
	public void batchAdd(List<ActivityDiscountGroup> list){
		activityDiscountGroupMapper.batchAdd(list);
	}
	
	/**
	 * @Description: 根据活动id删除团购商品记录
	 * @param activityId
	 * @return int  
	 * @author tuzhd
	 * @date 2017年10月12日
	 */
	public int deleteByActivityId(String activityId){
		return activityDiscountGroupMapper.deleteByActivityId(activityId);
	}
	
	/**
	 * @Description: 查询活动业务团购商品记录
	 * @author tuzhd
	 * @date 2017年10月12日日
	 */
	public List<ActivityDiscountGroup> findByActivityId(String activityId){
		return activityDiscountGroupMapper.findByActivityId(activityId);
	}
	
	/**
	 * @Description: 根据店铺商品Id、活动Id查询团购商品信息
	 * @param activityId
	 * @param storeSkuId
	 * @return   
	 * @author tuzhd
	 * @date 2017年10月13日
	 */
	public ActivityDiscountGroup findByActivityIdAndSkuId(String activityId,String storeSkuId){
		return activityDiscountGroupMapper.findByActivityIdAndSkuId(activityId, storeSkuId);
	}
	
	/**
	 * @Description: 根据开团状态查询团购商品的分类
	 * @param status
	 * @author tuzhd
	 * @date 2017年10月17日
	 */
	@Override
	public PageUtils<GoodsSpuCategory> findGroupGoodsCategory(Integer pageNumber,Integer pageSize){
		PageHelper.startPage(pageNumber, pageSize, true);
		List<GoodsSpuCategory> list = activityDiscountGroupMapper.findGroupGoodsCategory(ActivityDiscountStatus.ing);
		return new PageUtils<>(list);
	}
	
	/**
	 * @Description: 根据开团状态查询团购商品的列表
	 * @param status
	 * @author tuzhd
	 * @date 2017年10月17日
	 */
	@Override
	public PageUtils<ActivityGoodsGroupSkuDto> findGroupGoodsList(Map<String,Object> param,Integer pageNumber,Integer pageSize){
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ActivityGoodsGroupSkuDto> list = activityDiscountGroupMapper.findGroupGoodsList(param);
		list.forEach(e -> {
			e.setUrl(storeImagePrefix + e.getUrl()+ StaticConstants.PIC_SUFFIX_PARM_240);
		});
		return new PageUtils<>(list);
	}
}