package com.okdeer.mall.activity.discount.service;

import java.util.List;
import java.util.Map;

import com.okdeer.base.service.IBaseService;
import com.okdeer.common.entity.ReturnInfo;
import com.okdeer.mall.activity.bo.ActivityParamBo;
import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.dto.ActivityInfoDto;
import com.okdeer.mall.activity.dto.ActivityParamDto;
import com.okdeer.mall.activity.service.FavourFilterStrategy;
import com.okdeer.mall.order.vo.Discount;
import com.okdeer.mall.order.vo.Favour;
import com.okdeer.mall.order.vo.FullSubtract;

/**
 * 满减(满折)Service
 * @pr yscm
 * @desc 满减(满折)Service
 * @author zengj
 * @date 2016年1月26日 下午2:25:34
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1 			2016-07-22			zengj			查询店铺有效的满减
 *		友门鹿V2.1 		2017-02-17			maojj			查询店铺有效的满减
 *		友门鹿V2.3 		2017-02-17			maojj			满减活动优化
 */
public interface ActivityDiscountService extends IBaseService{
	
	/**
	 * @Description: 新增活动信息
	 * @param actInfoDto
	 * @return   
	 * @author maojj
	 * @date 2017年4月17日
	 */
	ReturnInfo add(ActivityInfoDto actInfoDto);
	
	/**
	 * @Description: 编辑活动信息
	 * @param actInfoDto
	 * @return   
	 * @author maojj
	 * @date 2017年4月20日
	 */
	ReturnInfo update(ActivityInfoDto actInfoDto);
	
	/**
	 * @Description: 定时调度更改活动状态   
	 * @author maojj
	 * @date 2017年4月18日
	 */
	void updateStatus();
	
	/**
	 * @Description: 查询用户有效的折扣优惠
	 * @param params 查询用户有效优惠请求对象
	 * @return List
	 * @author maojj
	 * @date 2016年7月16日
	 */
	List<Discount> findValidDiscount(FavourParamBO paramBo,FavourFilterStrategy favourFilter) throws Exception;
	
	/**
	 * @Description: 查询用户有效的满减优惠
	 * @param params 查询用户有效优惠请求对象
	 * @return List
	 * @author maojj
	 * @date 2016年7月16日
	 */
	List<FullSubtract> findValidFullSubtract(FavourParamBO paramBo,FavourFilterStrategy favourFilter) throws Exception;
	
	/**
	 * 
	 * @Description: 查询店铺的满减满折活动和条件
	 * @param params 查询参数
	 * @return List 
	 * @author zengj
	 * @date 2016年7月22日
	 */
	List<Map<String, Object>> findActivityDiscountByStoreId(Map<String, Object> params);
	
	/**
	 * @Description: 根据参数查询活动列表
	 * @param paramDto
	 * @return   
	 * @author maojj
	 * @date 2017年4月19日
	 */
	List<ActivityDiscount> findListByParam(ActivityParamDto paramDto);
	
	/**
	 * @Description: 批量关闭活动
	 * @param paramBo
	 * @return   
	 * @author maojj
	 * @date 2017年4月19日
	 */
	ReturnInfo batchClose(ActivityParamBo paramBo);
	
	/**
	 * @Description: 根据活动Id查找活动完整信息
	 * @param id
	 * @param isLoadDetail 是否加载具体的限制信息，如限制店铺，是否需要加载店铺明细
	 * @return
	 * @throws Exception   
	 * @author maojj
	 * @date 2017年4月21日
	 */
	ActivityInfoDto findInfoById(String id,boolean isLoadDetail) throws Exception;
	
	/**
	 * @Description: 查询店铺所拥有的满减活动列表
	 * @param paramDto
	 * @return   
	 * @author maojj
	 * @date 2017年4月20日
	 */
	List<ActivityInfoDto> findByStore(ActivityParamDto paramDto) throws Exception;
	
	/**
	 * @Description: 查询有效的优惠信息
	 * @param paramBo
	 * @param favourFilter
	 * @return
	 * @throws Exception   
	 * @author maojj
	 * @date 2017年4月21日
	 */
	List<? extends Favour> findValidFavour(FavourParamBO paramBo,FavourFilterStrategy favourFilter) throws Exception;
}
