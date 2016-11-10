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

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.operate.entity.SkinManagerDetailVo;
import com.okdeer.mall.operate.entity.SkinManagerVo;
import com.okdeer.mall.operate.enums.SkinManagerStatus;
import com.okdeer.mall.operate.enums.SkinModule;
import com.okdeer.mall.operate.service.ISkinManagerServiceApi;
import com.okdeer.mall.operate.skinmanager.mapper.SkinManagerMapper;

/**
 * ClassName: ISkinManagerServiceImpl 
 * @Description: TODO
 * @author xuzq01
 * @date 2016年11月4日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version="1.0.0", interfaceName = "com.okdeer.mall.operate.service.ISkinManagerServiceApi")
public class SkinManagerServiceImpl implements ISkinManagerServiceApi {
	/**
	 * 获取皮肤列表dao
	 */
	@Autowired
	private SkinManagerMapper skinManagerMapper;
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ISkinManagerServiceApi#findSkinList(com.okdeer.mall.operate.entity.SkinManagerVo)
	 */
	@Override
	public PageUtils<SkinManagerVo> findSkinList(SkinManagerVo skinManagerVo,int pageNumber,int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<SkinManagerVo> result = skinManagerMapper.findSkinList(skinManagerVo);
		if (result == null) {
			result = new ArrayList<SkinManagerVo>();
		}
		return new PageUtils<SkinManagerVo>(result);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ISkinManagerServiceApi#addSkin(com.okdeer.mall.operate.entity.SkinManagerVo)
	 */
	@Override
	public int addSkin(SkinManagerVo skinManagerVo,String userId) {
		String skinManagerId = UuidUtils.getUuid();
		skinManagerVo.setId(skinManagerId);
		skinManagerVo.setStatus(SkinManagerStatus.noStart);
		skinManagerVo.setSkinModule(SkinModule.app);
		skinManagerVo.setCreateUserId(userId);
		skinManagerVo.setUpdateUserId(userId);
		Date date = new Date();
		skinManagerVo.setCreateTime(date);
		skinManagerVo.setUpdateTime(date);
		skinManagerMapper.addSkin(skinManagerVo);
		List<SkinManagerDetailVo> detail = skinManagerVo.getDetail();
		
		for(int i=0;i<detail.size();i++){
			detail.get(i).setSkinManagerId(skinManagerId);
			detail.get(i).setId(UuidUtils.getUuid());
			detail.get(i).setCreateTime(date);
			detail.get(i).setCreateUserId(userId);
			detail.get(i).setUpdateTime(date);
			detail.get(i).setUpdateUserId(userId);
		}
		skinManagerMapper.addSkinDetail(detail);
		return 1;
	}
	
	
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ISkinManagerServiceApi#addSkin(com.okdeer.mall.operate.entity.SkinManagerVo)
	 */
	@Override
	public SkinManagerVo getSkinById(String skinId) {
		SkinManagerVo skinManagerVo = skinManagerMapper.getSkinById(skinId);
		return skinManagerVo;
	}

	/**
	 * 更新活动皮肤
	 * @see com.okdeer.mall.operate.service.ISkinManagerServiceApi#updateSkin(com.okdeer.mall.operate.entity.SkinManagerVo)
	 */
	@Override
	public int updateSkin(SkinManagerVo skinManagerVo, String userId) {
		skinManagerVo.setUpdateUserId(userId);
		Date date = new Date();
		skinManagerVo.setUpdateTime(date);
		skinManagerMapper.updateSkin(skinManagerVo);
		return 0;
	}

	/**
	 * 逻辑删除活动皮肤
	 * @see com.okdeer.mall.operate.service.ISkinManagerServiceApi#deleteSkin(java.lang.String)
	 */
	@Override
	public int deleteSkinById(String skinId,String userId) {
		SkinManagerVo skinManagerVo = new SkinManagerVo();
		skinManagerVo.setUpdateUserId(userId);
		skinManagerVo.setId(skinId);
		Date date = new Date();
		skinManagerVo.setUpdateTime(date);
		int resultCount = skinManagerMapper.deleteSkinById(skinManagerVo);
		return resultCount;
	}

	/**
	 * 关闭活动皮肤
	 * @see com.okdeer.mall.operate.service.ISkinManagerServiceApi#updateSkinStatus(java.lang.String)
	 */
	@Override
	public int closeSkinById(String skinId,String userId) {
		SkinManagerVo skinManagerVo = new SkinManagerVo();
		skinManagerVo.setUpdateUserId(userId);
		skinManagerVo.setId(skinId);
		skinManagerVo.setStatus(SkinManagerStatus.complete);
		Date date = new Date();
		skinManagerVo.setUpdateTime(date);
		int resultCount = skinManagerMapper.closeSkinById(skinManagerVo);
		return resultCount;
	}

	/**
	 * 根据时间查询是否有满足条件的活动皮肤
	 * @see com.okdeer.mall.operate.service.ISkinManagerServiceApi#updateSkinStatus(java.lang.String)
	 */
	@Override
	public int selectSkinByTime(SkinManagerVo skinManagerVo){
		int resultCount = skinManagerMapper.selectSkinByTime(skinManagerVo);
		return resultCount;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ISkinManagerServiceApi#selectSkinCountByName(com.okdeer.mall.operate.entity.SkinManagerVo)
	 */
	@Override
	public int selectSkinCountByName(SkinManagerVo skinManagerVo) {
		// TODO Auto-generated method stub
		return 0;
	}
}
