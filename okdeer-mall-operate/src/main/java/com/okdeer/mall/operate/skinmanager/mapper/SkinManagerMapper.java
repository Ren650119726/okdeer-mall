/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2016年11月2日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.skinmanager.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseCrudMapper;
import com.okdeer.mall.operate.dto.SkinManagerDetailDto;
import com.okdeer.mall.operate.dto.SkinManagerDto;

/**
 * ClassName: SkinManagerMapper 
 * @Description: 对于活动皮肤的增删改查操作接口类
 * @author xuzq01
 * @date 2016年11月2日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface SkinManagerMapper extends IBaseCrudMapper{
	
	/**
	 * 
	 * @Description: 根据条件查询皮肤列表
	 * @param skinManagerVo
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public List<SkinManagerDto> findSkinList(SkinManagerDto skinManagerVo);
	
	/**
	 * 
	 * @Description: 增加活动皮肤
	 * @param skinManagerVo
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public int addSkin(SkinManagerDto skinManagerVo);
	/**
	 * 
	 * @Description: 编辑修改皮肤
	 * @param skinManagerVo
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public int updateSkin(SkinManagerDto skinManagerVo);
	
	/**
	 * 
	 * @Description: 修改皮肤详情
	 * @param detail
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public int updateSkinDetail(@Param(value = "detail") List<SkinManagerDetailDto> detail);
	/**
	 * 
	 * @Description: 逻辑删除活动皮肤
	 * @param skinManagerVo
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月3日
	 */
	public int deleteSkinById(SkinManagerDto skinManagerVo);
	
	/**
	 * @Description: 修改状态 关闭活动皮肤
	 * @param skinManagerVo
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月5日
	 */
	public int closeSkinById(SkinManagerDto skinManagerVo);
	
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
	 * @Description: 添加活动皮肤详细
	 * @param detail
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月5日
	 */
	public int addSkinDetail(@Param(value = "detail") List<SkinManagerDetailDto> detail);

	/**
	 * @Description: 通过id获取活动皮肤 用于修改功能
	 * @param skinId   
	 * @author xuzq01
	 * @date 2016年11月9日
	 */
	public SkinManagerDto getSkinById(String skinId);

	/**
	 * @Description: 通过活动皮肤名称查询 用于校验
	 * @param skinManagerVo
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月10日
	 */
	public int selectSkinCountByName(SkinManagerDto skinManagerVo);

}
