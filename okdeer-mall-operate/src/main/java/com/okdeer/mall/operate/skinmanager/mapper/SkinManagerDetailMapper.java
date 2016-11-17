/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * OperateSkinManagerDetailMapper.java
 * @Date 2016-11-14 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.skinmanager.mapper;

import java.util.List;


import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.operate.entity.SkinManagerDetail;

public interface SkinManagerDetailMapper extends IBaseMapper {

	/**
	 * @Description: TODO
	 * @param detail
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月14日
	 */
	public int addBatch(List<SkinManagerDetail> detail);

	/**
	 * @Description: TODO
	 * @param detail
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月14日
	 */
	public int updateBatch(List<SkinManagerDetail> detail);
	
	/**
	 * @Description: 根据皮肤管理Id删除皮肤详情   
	 * @author maojj
	 * @date 2016年11月16日
	 */
	void deleteBySkinId(String skinId);

}