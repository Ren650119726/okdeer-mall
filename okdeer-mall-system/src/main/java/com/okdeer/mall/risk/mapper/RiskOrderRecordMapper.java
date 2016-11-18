/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * RiskOrderRecordMapper.java
 * @Date 2016-11-17 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.risk.mapper;

import java.util.List;
import java.util.Set;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.risk.po.RiskOrderRecordPo;

/**
 * ClassName: RiskOrderRecordMapper 
 * @Description: 风控订单记录
 * @author guocp
 * @date 2016年11月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface RiskOrderRecordMapper extends IBaseMapper {

	
	RiskOrderRecordPo findByLoginName(String loginName);

	/**
	 * @Description: TODO
	 * @param loginName   
	 * @author guocp
	 * @date 2016年11月17日
	 */
	Set<String> findTelsByLoginName(String loginName);
}