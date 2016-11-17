/** 
 *@Project: okdeer-mall-operate 
 *@Author: xuzq01
 *@Date: 2016年11月4日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.skinmanager.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.operate.dto.SkinManagerDto;
import com.okdeer.mall.operate.entity.SkinManager;
import com.okdeer.mall.operate.enums.SkinManagerStatus;
import com.okdeer.mall.operate.skinmanager.mapper.SkinManagerMapper;
import com.okdeer.mall.operate.skinmanager.service.ISkinManagerService;

/**
 * ClassName: SkinManagerImpl 
 * @Description: 皮肤管理服务实现类
 * @author xuzq01
 * @date 2016年11月4日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *	    V1.2开发			2016-11-4			xuzq01	                        皮肤管理服务实现类
 *
 */
@Service
public class SkinManagerServiceImpl extends BaseServiceImpl implements ISkinManagerService {

	/**
	 * 获取皮肤mapper
	 */
	@Autowired
	private SkinManagerMapper skinManagerMapper;
	/**
	 * 获取列表
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#findSkinList(com.okdeer.mall.operate.dto.SkinManagerDto)
	 */
	@Override
	public PageUtils<SkinManagerDto> findSkinList(SkinManagerDto skinManagerDto,int pageNumber,int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<SkinManagerDto> result = skinManagerMapper.findSkinList(skinManagerDto);
		if (result == null) {
			result = new ArrayList<SkinManagerDto>();
		}
		return new PageUtils<SkinManagerDto>(result);
	}
	
	/**
	 * 获取活动皮肤对象
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#addSkin(com.okdeer.mall.operate.dto.SkinManagerDto)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SkinManagerDto findById(String skinId) {
		return skinManagerMapper.findById(skinId);
	}

	/**
	 * 逻辑删除活动皮肤
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#deleteSkin(java.lang.String)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteSkinById(String skinId,String userId) {
		SkinManagerDto skinManagerDto = new SkinManagerDto();
		skinManagerDto.setUpdateUserId(userId);
		skinManagerDto.setId(skinId);
		Date date = new Date();
		skinManagerDto.setUpdateTime(date);
		SkinManager skinManager= BeanMapper.map(skinManagerDto, SkinManager.class);
		skinManagerMapper.deleteSkinById(skinManager);
	}

	/**
	 * 关闭活动皮肤
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#updateSkinStatus(java.lang.String)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void closeSkinById(String skinId,String userId) {
		SkinManagerDto skinManagerDto = new SkinManagerDto();
		skinManagerDto.setUpdateUserId(userId);
		skinManagerDto.setId(skinId);
		skinManagerDto.setStatus(SkinManagerStatus.COMPLETE);
		Date date = new Date();
		skinManagerDto.setUpdateTime(date);
		SkinManager skinManager= BeanMapper.map(skinManagerDto, SkinManager.class);
		skinManagerMapper.closeSkinById(skinManager);
	}

	/**
	 * 根据时间查询是否有满足条件的活动皮肤
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#updateSkinStatus(java.lang.String)
	 */
	@Override
	public int findSkinByTime(SkinManagerDto skinManagerDto){
		SkinManager skinManager= BeanMapper.map(skinManagerDto, SkinManager.class);
		return skinManagerMapper.findSkinByTime(skinManager);
	}

	/**
	 * 通过名称获取存在皮肤数量
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#selectSkinCountByName(com.okdeer.mall.operate.dto.SkinManagerDto)
	 */
	@Override
	public int findSkinCountByName(SkinManagerDto skinManagerDto) {
		SkinManager skinManager= BeanMapper.map(skinManagerDto, SkinManager.class);
		return skinManagerMapper.findSkinCountByName(skinManager);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return skinManagerMapper;
	}

	
}
