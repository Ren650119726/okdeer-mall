/** 
 *@Project: okdeer-mall-operate 
 *@Author: xuzq01
 *@Date: 2016年11月2日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.skinmanager.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.operate.dto.SkinManagerDto;
import com.okdeer.mall.operate.entity.SkinManager;
import com.okdeer.mall.operate.entity.SkinManagerDetail;

/**
 * ClassName: SkinManagerMapper 
 * @Description: 对于活动皮肤的增删改查操作接口类
 * @author xuzq01
 * @date 2016年11月2日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *	    V1.2开发			2016-11-2			xuzq01	                        对于活动皮肤的增删改查操作接口类
 */

public interface SkinManagerMapper extends IBaseMapper{
	
	/**
	 * 
	 * @Description: 根据条件查询皮肤列表
	 * @param skinManager
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public List<SkinManagerDto> findSkinList(SkinManager skinManager);
	

	/**
	 * 
	 * @Description: 修改皮肤详情
	 * @param detail
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public int updateSkinDetail(@Param(value = "detail") List<SkinManagerDetail> detail);
	/**
	 * 
	 * @Description: 逻辑删除活动皮肤
	 * @param skinManager
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public int deleteSkinById(SkinManager skinManager);
	
	/**
	 * @Description: 修改状态 关闭活动皮肤
	 * @param skinManager
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月5日
	 */
	public int closeSkinById(SkinManager skinManager);
	
	/**
	 * 
	 * @Description: 通过时间查询皮肤 校验在同一时间内是否存在另外一个活动
	 * @param skinManager
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月4日
	 */
	public int selectSkinByTime(SkinManager skinManager);

	/**
	 * @Description: 添加活动皮肤详细
	 * @param detail
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月5日
	 */
	public int addSkinDetail(@Param(value = "detail") List<SkinManagerDetail> detail);

	/**
	 * @Description: 通过id获取活动皮肤 用于修改功能
	 * @param skinId   
	 * @author xuzq01
	 * @date 2016年11月9日
	 */
	public SkinManager getSkinById(String skinId);

	/**
	 * @Description: 通过活动皮肤名称查询 用于校验
	 * @param skinManager
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月10日
	 */
	public int selectSkinCountByName(SkinManager skinManager);

}
