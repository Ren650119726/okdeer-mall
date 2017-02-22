package com.okdeer.mall.activity.coupons.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.coupons.bo.ActivitySaleRemindBo;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleRemind;

/**
 * 
 * ClassName: ActivitySaleRemindService 
 * @Description: 活动商品安全库存提醒
 * @author tangy
 * @date 2017年2月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.1.0          2017年2月15日                               tangy
 */
public interface ActivitySaleRemindService extends IBaseService {

	/**
	 * 
	 * @Description: 根据活动id查询安全库存预警联系人
	 * @param saleId 活动id
	 * @return List<ActivitySaleRemindBo>  
	 * @author tangy
	 * @date 2017年2月20日
	 */
	List<ActivitySaleRemindBo> findActivitySaleRemindBySaleId(String saleId);
	
	/**
	 * 
	 * @Description: 批量插入
	 * @param list   活动商品安全库存提醒人
	 * @return int  
	 * @author tangy
	 * @date 2017年2月20日
	 */
	int insertSelectiveBatch(List<ActivitySaleRemind> list);
	
	/**
	 * 
	 * @Description: 删除活动安全库存提醒联系人
	 * @param saleId 活动id
	 * @return int  
	 * @author tangy
	 * @date 2017年2月20日
	 */
	int deleteBySaleId(String saleId);
	
}
