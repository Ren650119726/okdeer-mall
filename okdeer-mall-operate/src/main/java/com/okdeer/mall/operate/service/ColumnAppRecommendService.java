/** 
 *@Project: okdeer-mall-Column 
 *@Author: tangzj02
 *@Date: 2016年12月29日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.service;

import java.util.List;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.operate.dto.AppRecommendDto;
import com.okdeer.mall.operate.dto.AppRecommendParamDto;
import com.okdeer.mall.operate.entity.ColumnAppRecommend;

/**
 * ClassName: ColumnAppRecommendService 
 * @Description: APP端服务商品推荐服务
 * @author tangzj02
 * @date 2016年12月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.0		  2016年12月29日		tangzj02		  添加
 */

public interface ColumnAppRecommendService extends IBaseService {

	/**
	 * @Description: 根据app端服务商品推荐ID进行删除
	 * @param ids id集合
	 * @throws Exception   
	 * @return int 成功删除记录数 
	 * @author tangzj02
	 * @date 2016年12月29日
	 */
	int deleteByIds(List<String> ids) throws Exception;

	/**
	 * @Description: 查询App端服务商品推荐列表
	 * @param paramDto 查询参数
	 * @throws Exception   
	 * @return List<ColumnAppRecommend>  
	 * @author tangzj02
	 * @date 2016年12月29日
	 */
	List<ColumnAppRecommend> findList(AppRecommendParamDto paramDto) throws Exception;

	/**
	 * 
	 * @Description: 根据条件查询服务商品运营列表
	 * @param paramDto 入参
	 * @return PageUtils
	 * @author zhulq
	 * @throws Exception 
	 * @date 2016年12月31日
	 */
	PageUtils<AppRecommendDto> findListPage(AppRecommendParamDto paramDto) throws Exception;
}
