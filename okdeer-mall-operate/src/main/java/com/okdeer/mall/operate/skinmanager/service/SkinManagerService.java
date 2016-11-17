/** 
 *@Project: okdeer-mall-operate 
 *@Author: xuzq01
 *@Date: 2016年11月4日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.skinmanager.service;

import java.util.Date;

import org.springframework.transaction.annotation.Transactional;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.operate.dto.SkinManagerDto;
import com.okdeer.mall.operate.dto.SkinManagerParamDto;
import com.okdeer.mall.operate.enums.SkinManagerStatus;

/**
 * ClassName: ISkinManagerService 
 * @Description: service接口在dubbo以后存在的意义不大 现在这个没有实现
 * @author xuzq01
 * @date 2016年11月4日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *	   V1.2.0			2016-11-16			maojj			换肤功能修改
 */

public interface SkinManagerService extends IBaseService {

	/**
	 * 
	 * @Description: 根据条件查询皮肤列表
	 * @param skinManagerVo
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public PageUtils<SkinManagerDto> findSkinList(SkinManagerParamDto paramDto);

	/**
	 * 
	 * @Description: 逻辑删除活动皮肤
	 * @param skinId
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public void deleteSkinById(String skinId, String userId);

	/**
	 * 
	 * @Description: 修改状态 关闭活动皮肤 
	 * @param skinId 皮肤id
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public void closeSkinById(String skinId, String userId);

	/**
	 * 
	 * @Description: 通过时间查询皮肤 校验在同一时间内是否存在另外一个活动
	 * @param skinManagerVo
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月4日
	 */
	public int findSkinByTime(SkinManagerDto skinManagerVo);

	/**
	 * @Description: 通过名称查询皮肤数量
	 * @param skinManagerVo
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月8日
	 */
	public int findSkinCountByName(SkinManagerDto skinManagerVo);
	
	// Begin V1.2.0 added by maojj 2016-11-16
	/**
	 * @Description: 根据换肤活动id查询活动明细
	 * @param id
	 * @return   
	 * @author maojj
	 * @date 2016年11月16日
	 */
	SkinManagerDto findSkinDetailByParam(SkinManagerParamDto paramDto);
	
	/**
	 * @Description: 修改皮肤管理
	 * @param skinDto   
	 * @author maojj
	 * @date 2016年11月16日
	 */
	void update(SkinManagerDto skinManagerDto);
	
	/**
	 * @Description: 新增皮肤活动
	 * @param skinManagerDto   
	 * @author maojj
	 * @date 2016年11月17日
	 */
	void add(SkinManagerDto skinManagerDto);
	// End V1.2.0 added by maojj 2016-11-16
	
	
	/**
	 * 执行换肤活动管理 JOB 任务
	 * @Description: TODO   
	 * @return void  
	 * @throws
	 * @author tuzhd
	 * @date 2016年11月16日
	 */
	public void processSkinActivityJob();
	
	/**
	 * @Description: 根据id换肤活动管理状态
	 * @param id
	 * @param status 活动状态 0 未开始 ，1：进行中2:已结束 
	 * @param updateUserId 修改人
	 * @param updateTime 修改时间
	 * @throws Exception
	 * @author tuzhiding
	 * @date 2016年11月12日
	 */
	public void updateStatusById(String id, SkinManagerStatus status, String updateUserId, Date updateTime) throws Exception;
}
