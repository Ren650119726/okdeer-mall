/** 
 *@Project: okdeer-mall-operate 
 *@Author: lijun
 *@Date: 2016年9月23日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */  
package com.okdeer.mall.operate.column.mapper;

import com.okdeer.mall.operate.entity.SysIconManager;

/**
 * ClassName: SysIconManagerMapper 
 * @Description: 系统图标管理（服务栏目更多）Mapper接口
 * @author lijun
 * @date 2016年9月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构1.1需求                               2016年9月23日                                 lijun               新增
 * 
 */
public interface SysIconManagerMapper {
	
	/**
	 * @Description: 获取更多服务栏目信息（只会有一条记录）
	 * @return SysIconManager 更多服务栏目信息
	 * @author lijun
	 * @date 2016年9月23日
	 */
	SysIconManager findSysIcon();
	
	/**
	 * @Description: 初始化添加更多服务栏目
	 * @param sysIconManager 实体类
	 * @author lijun
	 * @date 2016年9月23日
	 */
	void insertSelective(SysIconManager sysIconManager);
	
	/**
	 * @Description: 更新更多服务栏目
	 * @param sysIconManager 实体类
	 * @author lijun
	 * @date 2016年9月23日
	 */
	void updateSelective(SysIconManager sysIconManager);
}
