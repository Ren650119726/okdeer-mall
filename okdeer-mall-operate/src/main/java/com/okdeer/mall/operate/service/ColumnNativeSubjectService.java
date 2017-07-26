/** 
 *@Project: okdeer-mall-api 
 *@Author: tangzj02
 *@Date: 2016年12月30日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.service;

import java.util.List;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.operate.dto.ColumnNativeSubjectParamDto;
import com.okdeer.mall.operate.entity.ColumnNativeSubject;
import com.okdeer.mall.operate.entity.ColumnNativeSubjectGoods;

/**
 * ClassName: ColumnNativeSubjectApi 
 * @Description: 原生专题
 * @author zhangkn
 * @date 2017-04-13    
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.3       2017-04-13         		zhangkn                     添加
 */

public interface ColumnNativeSubjectService extends IBaseService {

	/**
	 * @Description: 查询原生专题列表
	 * @param paramDto 查询参数
	 * @return List<HomeIconDto> 原生专题记录列表
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月30日
	 */
	List<ColumnNativeSubject> findList(ColumnNativeSubjectParamDto param) throws Exception;

	/**
	 * @Description: 分页查询原生专题列表
	 * @param paramDto 查询参数
	 * @return PageUtils<HomeIconDto> 原生专题记录列表
	 * @throws Exception
	 * @author tangzj02
	 * @date 2017年01月05日
	 */
	PageUtils<ColumnNativeSubject> findListPage(ColumnNativeSubjectParamDto param,Integer pageNumber,Integer pageSize) throws Exception;

	/**
	 * @Description: 根据ID查询原生专题记录信息
	 * @param homeIconId 原生专题记录ID
	 * @throws Exception   
	 * @return HomeIconDto  
	 * @author tangzj02
	 * @date 2016年12月30日
	 */
	ColumnNativeSubject findById(String id) throws Exception;

	/**
	 * @Description: 根据ID删除原生专题记录信息
	 * @param homeIconId 原生专题记录ID
	 * @throws Exception   
	 * @return BaseResult  
	 * @author tangzj02
	 * @date 2016年12月30日
	 */
	void deleteById(String id) throws Exception;

	/**
	 * @Description: 保存或者删除原生专题记录信息
	 * @param dto 原生专题记录信息
	 * @throws Exception   
	 * @return BaseResult  
	 * @author tangzj02
	 * @date 2016年12月30日
	 */
	void add(ColumnNativeSubject obj,List<ColumnNativeSubjectGoods> goodsList) throws Exception;
	
	/**
	 * @Description: 保存或者删除原生专题记录信息
	 * @param dto 原生专题记录信息
	 * @throws Exception   
	 * @return BaseResult  
	 * @author tangzj02
	 * @date 2016年12月30日
	 */
	void update(ColumnNativeSubject obj,List<ColumnNativeSubjectGoods> goodsList) throws Exception;
	
	
}
