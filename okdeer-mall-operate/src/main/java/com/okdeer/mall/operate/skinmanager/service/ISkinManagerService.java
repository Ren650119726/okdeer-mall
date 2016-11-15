/** 
 *@Project: okdeer-mall-operate 
 *@Author: xuzq01
 *@Date: 2016年11月4日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.skinmanager.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.operate.dto.SkinManagerDto;
import com.okdeer.mall.operate.entity.SkinManager;
import com.okdeer.mall.operate.entity.SkinManagerDetail;

/**
 * ClassName: ISkinManagerService 
 * @Description: service接口在dubbo以后存在的意义不大 现在这个没有实现
 * @author xuzq01
 * @date 2016年11月4日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface ISkinManagerService extends IBaseService {

	/**
	 * 
	 * @Description: 根据条件查询皮肤列表
	 * @param skinManagerVo
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public PageUtils<SkinManagerDto> findSkinList(SkinManagerDto skinManagerDto,int pageNumber,int pageSize);
	
	
	/**
	 * 
	 * @Description: 逻辑删除活动皮肤
	 * @param skinId
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public void deleteSkinById(String skinId,String userId);
	
	/**
	 * 
	 * @Description: 修改状态 关闭活动皮肤 
	 * @param skinId 皮肤id
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public void closeSkinById(String skinId,String userId);
	/**
	 * 
	 * @Description: 通过时间查询皮肤 校验在同一时间内是否存在另外一个活动
	 * @param skinManagerVo
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月4日
	 */
	public int selectSkinByTime(SkinManagerDto skinManagerVo);

	/**
	 * @Description: 通过名称查询皮肤数量
	 * @param skinManagerVo
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月8日
	 */
	public int selectSkinCountByName(SkinManagerDto skinManagerVo);


	/**
	 * @Description: TODO
	 * @param skinManager
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月14日
	 */
	public int add(SkinManager skinManager);


	/**
	 * @Description: TODO
	 * @param skinManager
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月14日
	 */
	public int update(SkinManager skinManager);


}
