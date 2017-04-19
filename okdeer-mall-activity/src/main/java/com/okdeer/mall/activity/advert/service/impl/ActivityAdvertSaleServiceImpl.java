/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertSale;
import com.okdeer.mall.activity.advert.mapper.ActivityAdvertSaleMapper;
import com.okdeer.mall.activity.advert.service.ActivityAdvertSaleService;

/**
 * ClassName: ActivityAdvertSaleServiceImpl 
 * @Description: 销售活动及H5活动关联实现类
 * @author tuzhd
 * @date 2017年4月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 		V2.2.0			2017-4-13			tuzhd			 销售活动及H5活动关联实现类
 */
@Service
public class ActivityAdvertSaleServiceImpl extends BaseServiceImpl implements ActivityAdvertSaleService {
	
	@Autowired
	ActivityAdvertSaleMapper activityAdvertSaleMapper;
	
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return activityAdvertSaleMapper;
	}
	
	/**
	 * @Description: 根据活动id及模板编号查询关联的销售类型 
	 * @return ActivityAdvertSale  
	 * @author tuzhd
	 * @date 2017年4月13日
	 */
    public ActivityAdvertSale findSaleByIdNo(String modelNo,String activityAdvertId){
    	return activityAdvertSaleMapper.findSaleByIdNo(modelNo, activityAdvertId);
    }
    
    /**
	 * @Description: 新增销售类型 
	 * @param ActivityAdvertSale 店铺销售活动
	 * @author tuzhd
	 * @date 2017年4月13日
	 */
    @Transactional(rollbackFor = Exception.class)
	public int addSale(ActivityAdvertSale sale){
		return activityAdvertSaleMapper.add(sale);
	}
    
    /**
	 * @Description: 删除关联店铺促销信息by活动id
	 * @param activityAdvertId 活动id
	 * @return int  
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月19日
	 */
	public int deleteByActivityAdvertId(String activityAdvertId){
		return activityAdvertSaleMapper.deleteByActivityAdvertId(activityAdvertId);
	}
    
}
