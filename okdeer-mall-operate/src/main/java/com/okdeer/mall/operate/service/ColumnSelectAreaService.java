/** 
 *@Project: okdeer-mall-operate 
 *@Author: tangzj02
 *@Date: 2016年12月28日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.operate.entity.ColumnSelectArea;

/**
 * ClassName: ColumnSelectAreaService 
 * @Description: 栏目与城市关联服务实现
 * @author tangzj02
 * @date 2016年12月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0       2016-12-28        tangzj02                     添加
 */
@Service
public interface ColumnSelectAreaService extends IBaseService {

	/**
	 * @Description: 根据栏目ID查询数据
	 * @param columnId  栏目ID
	 * @return List<ColumnSelectArea>  
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	List<ColumnSelectArea> findListByColumnId(String columnId) throws Exception;
	
	/**
	 * @Description: 根据栏目ID集合查询数据
	 * @param columnIds  栏目ID集合
	 * @return List<ColumnSelectArea>  
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	List<ColumnSelectArea> findListByColumnIds(List<String> columnIds) throws Exception;

	/**
	 * @Description: 根据栏目ID删除数据
	 * @param columnId 栏目ID 
	 * @return int 删除记录  
	 * @author tangzj02
	 * @throws Exception
	 * @date 2016年12月28日
	 */
	int deleteByColumnId(String columnId) throws Exception;

	/**
	 * @Description: 批量插入数据
	 * @param areaList   
	 * @return int 成功插入记录数  
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	int insertMore(List<ColumnSelectArea> areaList) throws Exception;

	/**
	 * @Description: 根据城市查询对应栏位的数据
	 * @param provinceId  省ID
	 * @param cityId  城市ID
	 * @param columnType   栏位类型 1:首页ICON 2:APP端服务商品推荐
	 * @return int 成功插入记录数  
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月30日
	 */
	List<String> findColumnIdsByCity(String provinceId, String cityId, Integer columnType) throws Exception ;

}
