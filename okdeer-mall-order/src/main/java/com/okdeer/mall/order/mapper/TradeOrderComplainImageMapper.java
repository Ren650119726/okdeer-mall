package com.okdeer.mall.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.order.entity.TradeOrderComplainImage;
import com.yschome.base.dal.IBaseCrudMapper;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	重构4.1.0			2016-07-18   	wushp				重构4.1.0
 */
public interface TradeOrderComplainImageMapper extends IBaseCrudMapper {
	
	// begin add by wushp
	/**
	 * 
	 * @Description: 批量插入投诉图片
	 * @param tradeOrderComplainImageList 图片list
	 * @return int  受影响的行数
	 * @author wushp
	 * @date 2016年7月18日
	 */
	int insertByBatch(@Param("tradeOrderComplainImageList") List<TradeOrderComplainImage> 
		tradeOrderComplainImageList);
	// end add by wushp
}