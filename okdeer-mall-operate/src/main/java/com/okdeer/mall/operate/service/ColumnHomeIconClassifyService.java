/** 
 *@Project: okdeer-mall-operate 
 *@Author: xuzq01
 *@Date: 2017年8月15日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.operate.entity.ColumnHomeIconClassify;

/**
 * ClassName: ColumnHomeIconClassifyService 
 * @Description: icon分类选择导航菜单service
 * @author xuzq01
 * @date 2017年8月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface ColumnHomeIconClassifyService extends IBaseService {

	/**
	 * @Description: 通过iconid查询关联的导航分类列表
	 * @param homeIconId
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月15日
	 */
	List<ColumnHomeIconClassify> findListByHomeIconId(String homeIconId);

	/**
	 * @Description: 通过iconid删除原有关联分类信息
	 * @param homeIconId   
	 * @author xuzq01
	 * @date 2017年8月16日
	 */
	void deleteByHomeIconId(String homeIconId);

	/**
	 * @Description: 批量添加分类信息
	 * @param homeIconId
	 * @param selectcategoryIds   
	 * @author xuzq01
	 * @date 2017年8月16日
	 */
	void addClassifyBatch(String homeIconId, String selectcategoryIds);

}
