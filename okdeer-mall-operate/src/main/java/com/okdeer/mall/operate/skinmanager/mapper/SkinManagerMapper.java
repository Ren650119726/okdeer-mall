/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * OperateSkinManagerMapper.java
 * @Date 2016-11-14 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.skinmanager.mapper;

import java.util.List;
import java.util.Map;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.operate.dto.SkinManagerDto;
import com.okdeer.mall.operate.dto.SkinManagerParamDto;
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
	public List<SkinManagerDto> findSkinList(SkinManagerParamDto paramDto);

	/**
	 * @Description: 逻辑删除
	 * @param skinManager   
	 * @author xuzq01
	 * @date 2016年11月14日
	 */
	public void deleteSkinById(SkinManager skinManager);

	/**
	 * @Description: 关闭皮肤活动
	 * @param skinManager   
	 * @author xuzq01
	 * @date 2016年11月14日
	 */
	public void closeSkinById(SkinManager skinManager);

	/**
	 * @Description: 根据时间查找皮肤活动
	 * @param skinManager
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月14日
	 */
	public int findSkinByTime(SkinManager skinManager);

	/**
	 * @Description: 根据名称查询皮肤活动
	 * @param skinManager
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月14日
	 */
	public int findSkinCountByName(SkinManager skinManager);
	
	
	/**
	 * @Description: 根据换肤活动Id查询换肤活动明细
	 * @param id
	 * @return   
	 * @author maojj
	 * @date 2016年11月16日
	 */
	SkinManagerDto findSkinDetailByParam(SkinManagerParamDto param);
	
	/**
	 * 1、查询活动未开始，开始时间小于当前的数据 即为要设置开始，2、活动开始、结束时间小于当前的数据 即为要设置结束
	 * @author tuzhd
	 * @param map 传递参数
	 * @return
	 */
	public List<SkinManager> listByJob(Map<String,Object> map) ;
}