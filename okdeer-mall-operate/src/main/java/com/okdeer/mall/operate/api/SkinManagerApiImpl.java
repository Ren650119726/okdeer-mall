/** 
 *@Project: okdeer-mall-operate 
 *@Author: xuzq01
 *@Date: 2016年11月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.api;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.operate.dto.SkinManagerDto;
import com.okdeer.mall.operate.entity.SkinManager;
import com.okdeer.mall.operate.entity.SkinManagerDetail;
import com.okdeer.mall.operate.enums.AppSkinType;
import com.okdeer.mall.operate.enums.SkinManagerStatus;
import com.okdeer.mall.operate.service.ISkinManagerApi;
import com.okdeer.mall.operate.skinmanager.service.ISkinManagerDetailService;
import com.okdeer.mall.operate.skinmanager.service.ISkinManagerService;

/**
 * ClassName: SkinManagerApiImpl 
 * @Description: TODO
 * @author xuzq01
 * @date 2016年11月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version = "1.0.0")
public class SkinManagerApiImpl implements ISkinManagerApi {

	@Autowired
	ISkinManagerService skinManagerService;

	@Autowired
	ISkinManagerDetailService skinManagerDetailService;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#findSkinList(com.okdeer.mall.operate.dto.SkinManagerDto, int, int)
	 */
	@Override
	public PageUtils<SkinManagerDto> findSkinList(SkinManagerDto skinManagerDto, int pageNumber, int pageSize) {
		return skinManagerService.findSkinList(skinManagerDto, pageNumber, pageSize);
	}

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#addSkin(com.okdeer.mall.operate.dto.SkinManagerDto, java.lang.String)
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void addSkin(SkinManagerDto skinManagerDto, String userId) throws Exception {
		String skinManagerId = UuidUtils.getUuid();
		skinManagerDto.setId(skinManagerId);
		skinManagerDto.setStatus(SkinManagerStatus.NOSTART);
		skinManagerDto.setCreateUserId(userId);
		skinManagerDto.setUpdateUserId(userId);
		Date date = new Date();
		skinManagerDto.setCreateTime(date);
		skinManagerDto.setUpdateTime(date);
		SkinManager skinManager = BeanMapper.map(skinManagerDto, SkinManager.class);
		skinManagerService.add(skinManager);
		List<SkinManagerDetail> detailList = skinManagerDto.getDetailList();

		for(SkinManagerDetail detail : detailList){
			detail.setId(UuidUtils.getUuid());
			detail.setSkinManagerId(skinManagerId);
			detail.setCreateTime(date);
			detail.setCreateUserId(userId);
			detail.setUpdateTime(date);
			detail.setUpdateUserId(userId);
			detail.setDisabled(Disabled.valid);
		}
		skinManagerDetailService.addBatch(detailList);
	}

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#updateSkin(com.okdeer.mall.operate.dto.SkinManagerDto, java.lang.String)
	 */
	@Override
	public void updateSkin(SkinManagerDto skinManagerDto, String userId) throws Exception {
		skinManagerDto.setUpdateUserId(userId);
		Date date = new Date();
		skinManagerDto.setUpdateTime(date);
		SkinManager skinManager = BeanMapper.map(skinManagerDto, SkinManager.class);
		skinManagerService.update(skinManager);
		List<SkinManagerDetail> detailList = skinManagerDto.getDetailList();

		for (SkinManagerDetail detail : detailList) {
			detail.setUpdateTime(date);
			detail.setUpdateUserId(userId);
		}

		skinManagerDetailService.updateBatch(detailList);

	}

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#getSkinById(java.lang.String)
	 */
	@Override
	public SkinManagerDto findSkinById(String skinId) throws Exception {
		return skinManagerService.findById(skinId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#deleteSkinById(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteSkinById(String skinId, String userId) {
		skinManagerService.deleteSkinById(skinId, userId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#closeSkinById(java.lang.String, java.lang.String)
	 */
	@Override
	public void closeSkinById(String skinId, String userId) {
		skinManagerService.closeSkinById(skinId, userId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#selectSkinCountByName(com.okdeer.mall.operate.dto.SkinManagerDto)
	 */
	@Override
	public int findSkinCountByName(SkinManagerDto skinManagerDto) {
		return skinManagerService.findSkinCountByName(skinManagerDto);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ISkinManagerApi#selectSkinByTime(com.okdeer.mall.operate.dto.SkinManagerDto)
	 */
	@Override
	public int findSkinByTime(SkinManagerDto skinManagerDto) {
		return skinManagerService.findSkinByTime(skinManagerDto);
	}

}
