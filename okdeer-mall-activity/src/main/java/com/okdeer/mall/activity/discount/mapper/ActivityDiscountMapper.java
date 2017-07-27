/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityDiscountMapper.java
 * @Date 2017-04-17 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.discount.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.bo.ActivityParamBo;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.dto.ActivityParamDto;

public interface ActivityDiscountMapper extends IBaseMapper {
	
	/**
	 * @Description: 根据参数查询活动列表
	 * @param paramDto
	 * @return   
	 * @author maojj
	 * @date 2017年4月19日
	 */
	List<ActivityDiscount> findListByParam(ActivityParamDto paramDto);
 	
	/**
	 * @Description: 查询需要更新状态的活动列表。
	 * @param currentDate
	 * @return   
	 * @author maojj
	 * @date 2017年4月18日
	 */
	List<ActivityDiscount> findNeedUpdateList(@Param("currentDate")Date currentDate);
	
	/**
	 * @Description: 更新活动状态
	 * @param paramBo   
	 * @author maojj
	 * @date 2017年4月18日
	 */
	void updateStatus(ActivityParamBo paramBo);

	/**
	 * @Description: 查询满减满折折扣金额
	 * @param params 查询条件
	 * @return BigDecimal  
	 * @author maojj
	 * @date 2016年7月15日
	 */
	BigDecimal getDiscountValue(String id);
	
	
	// Begin 重构4.1 add by zengj
	/**
	 * 
	 * @Description: 查询店铺的满减满折活动和条件
	 * @param params 查询参数
	 * @return List 
	 * @author zengj
	 * @date 2016年7月22日
	 */
	List<Map<String, Object>> findActivityDiscountByStoreId(Map<String, Object> params);
	// End 重构4.1 add by zengj
	
	
	/**
	 * @Description: 查询店铺所拥有的满减活动列表
	 * @param paramDto
	 * @return   
	 * @author maojj
	 * @date 2017年4月20日
	 */
	List<String> findByStore(ActivityParamDto paramDto);
	
	/**
	 * @Description: 统计存在冲突的记录。即同一时间、同一地区、同一店铺只能存在一个满减、满折、零花钱活动
	 * @param paramDto
	 * @return   
	 * @author maojj
	 * @date 2017年4月21日
	 */
	int countConflict(ActivityParamBo paramBo);
	/**
	 * @Description: 根据id列表查询
	 * @param idList
	 * @return
	 * @author zengjizu
	 * @date 2017年7月27日
	 */
	List<ActivityDiscount> findByIds(List<String> idList);
}