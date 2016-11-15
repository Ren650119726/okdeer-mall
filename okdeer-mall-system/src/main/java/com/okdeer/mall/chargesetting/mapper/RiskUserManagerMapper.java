/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * RiskUserManagerMapper.java
 * @Date 2016-11-11 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.chargesetting.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.chargesetting.dto.UserManagerDto;
import com.okdeer.mall.chargesetting.entity.RiskUserManager;

public interface RiskUserManagerMapper extends IBaseMapper {


	/**
	 * @Description: TODO
	 * @param userManagerDto
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月15日
	 */
	List<RiskUserManager> findUserList(UserManagerDto userManagerDto);

}