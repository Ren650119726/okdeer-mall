/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * TradeOrderGroupMapper.java
 * @Date 2017-10-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.order.mapper;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.order.bo.TradeOrderGroupParamBo;

public interface TradeOrderGroupMapper extends IBaseMapper {

	/**
	 * @Description: 统计成团的商品总数
	 * @param paramBo
	 * @return   
	 * @author maojj
	 * @date 2017年10月12日
	 */
	int countGroupSkuNum(TradeOrderGroupParamBo paramBo);
}