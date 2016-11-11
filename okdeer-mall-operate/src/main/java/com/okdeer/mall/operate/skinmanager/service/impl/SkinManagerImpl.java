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
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.operate.dto.SkinManagerDto;
import com.okdeer.mall.operate.entity.SkinManager;
import com.okdeer.mall.operate.entity.SkinManagerDetail;
import com.okdeer.mall.operate.enums.SkinManagerStatus;
import com.okdeer.mall.operate.enums.SkinModule;
import com.okdeer.mall.operate.service.ISkinManagerApi;
import com.okdeer.mall.operate.skinmanager.mapper.SkinManagerMapper;

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
@Service(version="1.0.0", interfaceName = "com.okdeer.mall.operate.service.ISkinManagerApi")
public class SkinManagerImpl implements ISkinManagerApi {
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
		SkinManager skinManager= BeanMapper.map(skinManagerDto, SkinManager.class);
		List<SkinManagerDto> result = skinManagerMapper.findSkinList(skinManager);
		if (result == null) {
			result = new ArrayList<SkinManagerDto>();
		}
		return new PageUtils<SkinManagerDto>(result);
	}

	/**
	 * 增加皮肤
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#addSkin(com.okdeer.mall.operate.dto.SkinManagerDto)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addSkin(SkinManagerDto skinManagerDto,String userId) {
		String skinManagerId = UuidUtils.getUuid();
		skinManagerDto.setId(skinManagerId);
		skinManagerDto.setStatus(SkinManagerStatus.NOSTART);
		skinManagerDto.setSkinModule(SkinModule.APP);
		skinManagerDto.setCreateUserId(userId);
		skinManagerDto.setUpdateUserId(userId);
		Date date = new Date();
		skinManagerDto.setCreateTime(date);
		skinManagerDto.setUpdateTime(date);
		SkinManager skinManager= BeanMapper.map(skinManagerDto, SkinManager.class);
		skinManagerMapper.add(skinManager);
		
		List<SkinManagerDetail> detail = skinManagerDto.getDetail();
		
		for(int i=0;i<detail.size();i++){
			detail.get(i).setSkinManagerId(skinManagerId);
			detail.get(i).setDetailId(UuidUtils.getUuid());
			detail.get(i).setCreateTime(date);
			detail.get(i).setCreateUserId(userId);
			detail.get(i).setUpdateTime(date);
			detail.get(i).setUpdateUserId(userId);
		}
		skinManagerMapper.addSkinDetail(detail);
	}
	
	
	/**
	 * 获取活动皮肤对象
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#addSkin(com.okdeer.mall.operate.dto.SkinManagerDto)
	 */
	@Override
	public SkinManagerDto getSkinById(String skinId) {
		return skinManagerMapper.findById(skinId);
	}

	/**
	 * 更新活动皮肤
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#updateSkin(com.okdeer.mall.operate.dto.SkinManagerDto)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateSkin(SkinManagerDto skinManagerDto, String userId) {
		skinManagerDto.setUpdateUserId(userId);
		Date date = new Date();
		skinManagerDto.setUpdateTime(date);
		SkinManager skinManager= BeanMapper.map(skinManagerDto, SkinManager.class);
		skinManagerMapper.update(skinManager);
		List<SkinManagerDetail> detail = skinManagerDto.getDetail();
		
		for(int i=0;i<detail.size();i++){
			detail.get(i).setUpdateTime(date);
			detail.get(i).setUpdateUserId(userId);
		}
		skinManagerMapper.updateSkinDetail(detail);
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
	public int selectSkinByTime(SkinManagerDto skinManagerDto){
		SkinManager skinManager= BeanMapper.map(skinManagerDto, SkinManager.class);
		return skinManagerMapper.selectSkinByTime(skinManager);
	}

	/**
	 * 通过名称获取存在皮肤数量
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#selectSkinCountByName(com.okdeer.mall.operate.dto.SkinManagerDto)
	 */
	@Override
	public int selectSkinCountByName(SkinManagerDto skinManagerDto) {
		SkinManager skinManager= BeanMapper.map(skinManagerDto, SkinManager.class);
		return skinManagerMapper.selectSkinCountByName(skinManager);
	}
}
