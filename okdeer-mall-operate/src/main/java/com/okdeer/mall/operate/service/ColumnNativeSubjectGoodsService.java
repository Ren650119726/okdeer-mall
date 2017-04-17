/** 
 *@Project: okdeer-mall-api 
 *@Author: tangzj02
 *@Date: 2016年12月30日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.service;

import java.util.List;
import com.okdeer.base.service.IBaseService;
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

public interface ColumnNativeSubjectGoodsService extends IBaseService {

	/**
	 * @Description: 查询原生专题商品列表
	 * @param paramDto 查询参数
	 * @return List<ColumnNativeSubjectGoods> 原生专题商品列表
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月30日
	 */
	List<ColumnNativeSubjectGoods> findByColumnNativeSubjectId(String columnNativeSubjectId) throws Exception;
}
