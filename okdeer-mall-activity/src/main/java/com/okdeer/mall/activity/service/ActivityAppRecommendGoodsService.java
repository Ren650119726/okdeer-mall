/** 
 *@Project: okdeer-mall-activity 
 *@Author: tangzj02
 *@Date: 2016年12月30日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.activity.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.dto.AppRecommendGoodsParamDto;
import com.okdeer.mall.activity.entity.ActivityAppRecommendGoods;

/**
 * ClassName: ActivityAppRecommendGoodsService 
 * @Description: APP端服务商品推荐关联商品服务
 * @author tangzj02
 * @date 2016年12月30日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0       2016-12-30        tangzj02                        添加
 */

public interface ActivityAppRecommendGoodsService extends IBaseService {

	/**
	 * @Description: 查询与APP端服务商品推荐的关联数据
	 * @param recommendId 服务商品推荐ID
	 * @return List<ActivityAppRecommendGoods>  
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月30日
	 */
	List<ActivityAppRecommendGoods> findList(AppRecommendGoodsParamDto paramDto) throws Exception;

	/**
	 * @Description: 根据推荐ID查询与APP端服务商品推荐的关联数据
	 * @param recommendId 服务商品推荐ID
	 * @return List<ActivityAppRecommendGoods>  
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月30日
	 */
	List<ActivityAppRecommendGoods> findListByRecommendId(String recommendId) throws Exception;

	/**
	 * @Description: 根据推荐ID删除数据
	 * @param id  服务商品推荐ID  
	 * @return int 成功删除数  
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月30日
	 */
	int deleteByRecommendId(String recommendId) throws Exception;

	/**
	 * @Description: 批量插入数据
	 * @param goodsList   
	 * @return int 成功插入记录数  
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月30日
	 */
	int insertMore(List<ActivityAppRecommendGoods> goodsList) throws Exception;

}
