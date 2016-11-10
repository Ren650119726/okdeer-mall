/** 
 *@Project: okdeer-mall-operate 
 *@Author: xuzq01
 *@Date: 2016年11月4日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.skinmanager.service;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.operate.entity.SkinManagerVo;

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

public interface ISkinManagerService {
	/**
	 * 
	 * @Description: 根据条件查询皮肤列表
	 * @param skinManagerVo
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public PageUtils<SkinManagerVo> findSkinList(SkinManagerVo skinManagerVo,int pageNumber,int pageSize);
	
	/**
	 * 
	 * @Description: 增加活动皮肤
	 * @param skinManagerVo
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public int addSkin(SkinManagerVo skinManagerVo,String userId);
	
	/**
	 * 
	 * @Description: 增加活动皮肤详细
	 * @param skinManagerVo
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public int addSkinDetail(SkinManagerVo skinManagerVo);
	
	/**
	 * 
	 * @Description: 编辑修改皮肤
	 * @param skinManagerVo
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public int updateSkin(SkinManagerVo skinManagerVo,String userId);
	
	/**
	 * 
	 * @Description: 修改皮肤详情
	 * @param skinManagerVo
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public int updateSkinDetail(SkinManagerVo skinManagerVo);
	
	/**
	 * 
	 * @Description: 通过id获取皮肤
	 * @param skinId
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月4日
	 */
	public SkinManagerVo getSkinById(String skinId);
	/**
	 * 
	 * @Description: 逻辑删除活动皮肤
	 * @param skinId
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public int deleteSkinById(String skinId);
	
	/**
	 * 
	 * @Description: 修改状态 关闭活动皮肤 
	 * @param skinId 皮肤id
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public int closeSkinById(String skinId);
	
	/**
	 * 
	 * @Description: 通过时间查询皮肤 校验在同一时间内是否存在另外一个活动
	 * @param skinManagerVo
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月4日
	 */
	public int selectSkinByTime(SkinManagerVo skinManagerVo);
}
