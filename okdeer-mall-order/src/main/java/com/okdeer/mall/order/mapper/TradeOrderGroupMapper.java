/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * TradeOrderGroupMapper.java
 * @Date 2017-10-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.order.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.order.bo.TradeOrderGroupParamBo;
import com.okdeer.mall.order.dto.TradeOrderGroupGoodsDto;
import com.okdeer.mall.order.dto.TradeOrderGroupDto;
import com.okdeer.mall.order.dto.TradeOrderGroupParamDto;

public interface TradeOrderGroupMapper extends IBaseMapper {

	/**
	 * @Description: 统计成团的商品总数
	 * @param paramBo
	 * @return   
	 * @author maojj
	 * @date 2017年10月12日
	 */
	int countGroupSkuNum(TradeOrderGroupParamBo paramBo);
	
	/**
	 * @Description: 根据查询条件动态查询
	 * @param param
	 * @return
	 * @author zhangkn
	 * @date 2017年10月16日
	 */
	List<TradeOrderGroupDto> findByParam(TradeOrderGroupParamDto param);
	
	/**
	 * @Description: 查询开团信息集合
	 * @param paramBo
	 * @author tuzhd
	 * @date 2017年10月16日
	 */
	List<TradeOrderGroupGoodsDto> findOrderGroupList(TradeOrderGroupParamBo paramBo);
	
	/**
	 * @Description: 统计成团总数
	 * @param paramBo 
	 * @author tuzhd
	 * @date 2017年10月16日
	 */
	int countGroupNum(TradeOrderGroupParamBo paramBo);
	
}