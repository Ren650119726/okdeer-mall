/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * RiskUserManagerMapper.java
 * @Date 2016-11-11 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.risk.mapper;

import java.util.Date;
import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.risk.dto.UserManagerDto;
import com.okdeer.mall.risk.entity.RiskUserManager;

public interface RiskUserManagerMapper extends IBaseMapper {


	/**
	 * @Description: 查找列表
	 * @param userManagerDto
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月15日
	 */
	List<RiskUserManager> findUserList(UserManagerDto userManagerDto);

	/**
	 * @Description: 批量逻辑删除人员
	 * @param ids
	 * @param updateUserId
	 * @param updateTime   
	 * @author xuzq01
	 * @date 2016年11月15日
	 */
	void deleteBatchByIds(List<String> ids, String updateUserId, Date updateTime);

}