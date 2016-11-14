/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * RiskWhiteMapper.java
 * @Date 2016-11-11 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.chargesetting.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.chargesetting.dto.WhiteManagerDto;
import com.okdeer.mall.chargesetting.entity.RiskWhite;

public interface RiskWhiteMapper extends IBaseMapper {

	/**
	 * @Description: TODO
	 * @param whiteManagerDto
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月14日
	 */
	List<RiskWhite> findWhiteList(WhiteManagerDto whiteManagerDto);

	/**
	 * @Description: TODO
	 * @param whiteManagerDto
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月14日
	 */
	int selectWhiteByAccount(@Param(value = "telephoneAccount") String telephoneAccount);

}