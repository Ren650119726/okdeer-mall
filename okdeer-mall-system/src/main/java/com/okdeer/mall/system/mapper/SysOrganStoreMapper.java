/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * SysOrganStoreMapper.java
 * @Date 2017-03-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */

package com.okdeer.mall.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.system.entity.SysOrganStore;

/**
 * ClassName: SysOrganStoreMapper 
 * @Description: 组织关联店铺Mapper
 * @author zengjizu
 * @date 2017年3月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface SysOrganStoreMapper extends IBaseMapper {

	/**
	 * @Description: 根据组织id查询关联列表
	 * @param orgId 组织id
	 * @return
	 * @author zengjizu
	 * @date 2017年3月10日
	 */
	List<SysOrganStore> findByOrgId(@Param("orgId") String orgId);

	/**
	 * @Description: 批量保存
	 * @param list  保存list
	 * @return
	 * @author zengjizu
	 * @date 2017年3月10日
	 */
	int batchAdd(@Param("list") List<SysOrganStore> list);

}