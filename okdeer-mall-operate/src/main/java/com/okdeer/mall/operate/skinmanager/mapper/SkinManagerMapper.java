/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * OperateSkinManagerMapper.java
 * @Date 2016-11-14 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.skinmanager.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.operate.dto.SkinManagerDto;
import com.okdeer.mall.operate.entity.SkinManager;

public interface SkinManagerMapper extends IBaseMapper {
	/**
	 * 
	 * @Description: 根据条件查询皮肤列表
	 * @param skinManager
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public List<SkinManagerDto> findSkinList(SkinManagerDto skinManager);

	/**
	 * @Description: TODO
	 * @param skinManager   
	 * @author xuzq01
	 * @date 2016年11月14日
	 */
	public void deleteSkinById(SkinManager skinManager);

	/**
	 * @Description: TODO
	 * @param skinManager   
	 * @author xuzq01
	 * @date 2016年11月14日
	 */
	public void closeSkinById(SkinManager skinManager);

	/**
	 * @Description: TODO
	 * @param skinManager
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月14日
	 */
	public int findSkinByTime(SkinManager skinManager);

	/**
	 * @Description: TODO
	 * @param skinManager
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月14日
	 */
	public int findSkinCountByName(SkinManager skinManager);
	
}