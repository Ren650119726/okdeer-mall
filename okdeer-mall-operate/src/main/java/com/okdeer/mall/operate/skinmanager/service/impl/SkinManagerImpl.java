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
import com.okdeer.mall.operate.dto.SkinManagerDetailDto;
import com.okdeer.mall.operate.dto.SkinManagerDto;
import com.okdeer.mall.operate.enums.SkinManagerStatus;
import com.okdeer.mall.operate.enums.SkinModule;
import com.okdeer.mall.operate.service.ISkinManagerApi;
import com.okdeer.mall.operate.skinmanager.mapper.SkinManagerMapper;

/**
 * ClassName: ISkinManagerServiceImpl 
 * @Description: 皮肤管理服务实现类
 * @author xuzq01
 * @date 2016年11月4日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
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
	public PageUtils<SkinManagerDto> findSkinList(SkinManagerDto skinManagerVo,int pageNumber,int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<SkinManagerDto> result = skinManagerMapper.findSkinList(skinManagerVo);
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
	public void addSkin(SkinManagerDto skinManagerVo,String userId) {
		String skinManagerId = UuidUtils.getUuid();
		skinManagerVo.setId(skinManagerId);
		skinManagerVo.setStatus(SkinManagerStatus.NOSTART);
		skinManagerVo.setSkinModule(SkinModule.APP);
		skinManagerVo.setCreateUserId(userId);
		skinManagerVo.setUpdateUserId(userId);
		Date date = new Date();
		skinManagerVo.setCreateTime(date);
		skinManagerVo.setUpdateTime(date);
		skinManagerMapper.addSkin(skinManagerVo);
		List<SkinManagerDetailDto> detail = skinManagerVo.getDetail();
		
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
		return skinManagerMapper.getSkinById(skinId);
	}

	/**
	 * 更新活动皮肤
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#updateSkin(com.okdeer.mall.operate.dto.SkinManagerDto)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateSkin(SkinManagerDto skinManagerVo, String userId) {
		skinManagerVo.setUpdateUserId(userId);
		Date date = new Date();
		skinManagerVo.setUpdateTime(date);
		skinManagerMapper.updateSkin(skinManagerVo);
		List<SkinManagerDetailDto> detail = skinManagerVo.getDetail();
		
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
		SkinManagerDto skinManagerVo = new SkinManagerDto();
		skinManagerVo.setUpdateUserId(userId);
		skinManagerVo.setId(skinId);
		Date date = new Date();
		skinManagerVo.setUpdateTime(date);
		skinManagerMapper.deleteSkinById(skinManagerVo);
	}

	/**
	 * 关闭活动皮肤
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#updateSkinStatus(java.lang.String)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void closeSkinById(String skinId,String userId) {
		SkinManagerDto skinManagerVo = new SkinManagerDto();
		skinManagerVo.setUpdateUserId(userId);
		skinManagerVo.setId(skinId);
		skinManagerVo.setStatus(SkinManagerStatus.COMPLETE);
		Date date = new Date();
		skinManagerVo.setUpdateTime(date);
		skinManagerMapper.closeSkinById(skinManagerVo);
	}

	/**
	 * 根据时间查询是否有满足条件的活动皮肤
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#updateSkinStatus(java.lang.String)
	 */
	@Override
	public int selectSkinByTime(SkinManagerDto skinManagerVo){
		return skinManagerMapper.selectSkinByTime(skinManagerVo);
	}

	/**
	 * 通过名称获取存在皮肤数量
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#selectSkinCountByName(com.okdeer.mall.operate.dto.SkinManagerDto)
	 */
	@Override
	public int selectSkinCountByName(SkinManagerDto skinManagerVo) {
		return skinManagerMapper.selectSkinCountByName(skinManagerVo);
	}
}
