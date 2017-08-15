/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * TradePinMoneyUseMapper.java
 * @Date 2017-08-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.order.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.order.bo.TradePinMoneyUseBo;
import com.okdeer.mall.order.dto.TradePinMoneyQueryDto;
import com.okdeer.mall.order.entity.TradePinMoneyUse;

public interface TradePinMoneyUseMapper extends IBaseMapper {
	/**
	 * @Description: 获取零花钱使用列表
	 * @param paramDto
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月12日
	 */
	List<TradePinMoneyUseBo> fingUsePageList(TradePinMoneyQueryDto paramDto);

	/**
	 * @Description: 获取使用记录数量
	 * @param paramDto
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月12日
	 */
	Integer findUseListCount(TradePinMoneyQueryDto paramDto);

	/**
	 * @Description: 查询我的零花钱记录
	 * @param userId
	 * @param date
	 * @param object
	 * @return   
	 * @author guocp
	 * @date 2017年8月14日
	 */
	List<TradePinMoneyUse> findList(@Param("userId") String userId);

}