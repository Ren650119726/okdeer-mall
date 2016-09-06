package com.okdeer.mall.order.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.order.vo.TradeOrderComplainQueryVo;
import com.okdeer.mall.order.vo.TradeOrderComplainVo;
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
 *    重构4.1           2016-7-16           wusw               1、添加根据店铺id，查询投诉单和订单信息的方法；2、添加根据投诉单id，获取投诉内容和图片的方法
 */
public interface TradeOrderComplainMapper extends IBaseCrudMapper {
	/**
	 * 
	 * 查询订单下的投诉单
	 * @param orderId 订单ID
	 * @return 投诉单信息
	 */
	public List<TradeOrderComplainVo> findOrderComplainByParams(@Param("orderId") String orderId);
	
	//Begin  重构4.1  add by wusw
	/**
	 * 
	 * @Description: 根据店铺id，查询投诉单和订单信息
	 * @param storeId 
	 * @return List<TradeOrderComplainQueryVo> 
	 * @author wusw
	 * @date 2016年7月16日
	 */
    List<TradeOrderComplainQueryVo> selectComplainByStoreId(Map<String,Object> params);
    
    /**
     * 
     * @Description: 根据投诉单id，获取投诉单内容和图片
     * @param id
     * @return TradeOrderComplainVo
     * @author wusw
     * @date 2016年7月16日
     */
    TradeOrderComplainVo selectComplainContentById(String id);
    
    /**
     * 
     * @Description: 获取指定店铺的未读投诉消息数量
     * @param params
     * @return int  
     * @author wusw
     * @date 2016年7月16日
     */
    int selectCountUnReadByStoreId(Map<String,Object> params);
    
    /**
     * 
     * @Description: 根据指定店铺的未读投诉单为已读
     * @param params   
     * @return void
     * @author wusw
     * @date 2016年7月16日
     */
    void updateReadByStoreId(Map<String,Object> params);
	//End  重构4.1  add by wusw
}