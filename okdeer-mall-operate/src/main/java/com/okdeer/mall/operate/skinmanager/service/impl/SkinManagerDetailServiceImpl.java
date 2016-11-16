/** 
 *@Project: okdeer-mall-operate 
 *@Author: xuzq01
 *@Date: 2016年11月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.skinmanager.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.operate.entity.SkinManagerDetail;
import com.okdeer.mall.operate.skinmanager.mapper.SkinManagerDetailMapper;
import com.okdeer.mall.operate.skinmanager.service.ISkinManagerDetailService;


/**
 * ClassName: SkinManagerServiceImpl 
 * @Description: 活动皮肤详细service实现类
 * @author xuzq01
 * @date 2016年11月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2           2016年11月14日                          xuzq01                            活动皮肤详细service实现类
 */
@Service
public class SkinManagerDetailServiceImpl implements ISkinManagerDetailService {
	/**
	 * 获取皮肤详细mapper
	 */
	@Autowired
	private SkinManagerDetailMapper skinManagerDetailMapper;
	
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.IBaseService#add(java.lang.Object)
	 */
	@Override
	public int addBatch(List<SkinManagerDetail> detail) throws Exception {
		return skinManagerDetailMapper.addBatch(detail);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.IBaseService#update(java.lang.Object)
	 */
	@Override
	public  int updateBatch(List<SkinManagerDetail> detail) throws Exception {
		return skinManagerDetailMapper.updateBatch(detail);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.IBaseService#add(java.lang.Object)
	 */
	@Override
	public <Entity> int add(Entity entity) throws Exception {
		return 0;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.IBaseService#update(java.lang.Object)
	 */
	@Override
	public <Entity> int update(Entity entity) throws Exception {
		return 0;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.IBaseService#delete(java.lang.String)
	 */
	@Override
	public int delete(String id) throws Exception {
		return 0;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.IBaseService#findById(java.lang.String)
	 */
	@Override
	public <Entity> Entity findById(String id) throws Exception {
		return null;
	}

	
}
