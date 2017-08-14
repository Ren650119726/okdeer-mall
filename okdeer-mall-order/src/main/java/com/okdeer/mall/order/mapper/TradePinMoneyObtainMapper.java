/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * TradePinMoneyObtainMapper.java
 * @Date 2017-08-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.order.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.order.bo.TradePinMoneyObtainBo;
import com.okdeer.mall.order.dto.TradePinMoneyQueryDto;
import com.okdeer.mall.order.entity.TradePinMoneyObtain;

public interface TradePinMoneyObtainMapper extends IBaseMapper {

	/**
	 * @Description: 查找我的零花钱余额
	 * @param userId
	 * @param status 
	 * @return   
	 * @author guocp
	 * @date 2017年8月10日
	 */
	BigDecimal findMyUsableTotal(@Param("userId") String userId, @Param("nowDate") Date nowDate);

	/**
	 * @Description: 查询列表
	 * @param userId
	 * @param date
	 * @param status
	 * @return   
	 * @author guocp
	 * @date 2017年8月11日
	 */
	List<TradePinMoneyObtain> findList(@Param("userId") String userId, @Param("nowDate") Date nowDate,
			@Param("status") Integer status);
	/**
	 * @Description: 获取列表
	 * @param paramDto
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月11日
	 */
	List<TradePinMoneyObtainBo> findPageList(TradePinMoneyQueryDto paramDto);

	/**
	 * @Description: 获取零花钱领取记录数量
	 * @param paramDto
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月12日
	 */
	Integer findObtainListCount(TradePinMoneyQueryDto paramDto);
}