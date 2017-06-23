package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseCrudMapper;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectXffqRelation;

/**
 * ClassName: ActivityCollectXffqRelationMapper 
 * @Description: TODO
 * @author zhangkn
 * @date 2017年6月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.3			 2017年6月23日 			zhagnkn
 */
public interface ActivityCollectXffqRelationMapper extends IBaseCrudMapper{
	
	/**
	 * @Description: 批量新增
	 * @param list
	 * @author zhangkn
	 * @date 2017年6月23日
	 */
	void saveBatch(List<ActivityCollectXffqRelation> list);
	
	/**
	 * @Description: 通过活动id查询列表
	 * @param collectId
	 * @return
	 * @author zhangkn
	 * @date 2017年6月23日
	 */
	List<ActivityCollectXffqRelation> findByCollectId(String collectId);
	
	/**
	 * @Description: 通过活动id删除
	 * @param collectId
	 * @author zhangkn
	 * @date 2017年6月23日
	 */
	void deleteByCollectId(String collectId);
}
