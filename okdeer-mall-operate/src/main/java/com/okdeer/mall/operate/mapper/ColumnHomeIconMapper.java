/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityHomeIconMapper.java
 * @Date 2016-12-30 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.operate.dto.HomeIconParamDto;
import com.okdeer.mall.operate.entity.ColumnHomeIcon;

public interface ColumnHomeIconMapper extends IBaseMapper {

	/**
	 * @Description: 查询首页ICON列表
	 * @param paramDto 查询参数
	 * @return List<ActivityHomeIcon> 首页ICON记录列表
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	List<ColumnHomeIcon> findList(HomeIconParamDto paramDto);
}