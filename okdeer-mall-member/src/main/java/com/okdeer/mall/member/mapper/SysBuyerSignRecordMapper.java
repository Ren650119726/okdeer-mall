/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * SysBuyerSignRecordMapper.java
 * @Date 2016-12-31 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.member.mapper;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.member.bo.SysBuyerSignRecordParam;

public interface SysBuyerSignRecordMapper extends IBaseMapper {

	/**
	 * @Description: 根据查询条件查询签到次数
	 * @param buyerSignRecordParam 查询参数
	 * @return
	 * @author zengjizu
	 * @date 2016年12月31日
	 */
	int findCountByParam(SysBuyerSignRecordParam buyerSignRecordParam);

}