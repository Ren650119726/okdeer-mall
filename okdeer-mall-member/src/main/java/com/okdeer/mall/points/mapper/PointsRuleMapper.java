package com.okdeer.mall.points.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.member.points.entity.PointsRule;


/**
 * @DESC: 积分规则dao
 * @author zhongy
 * @date  2016-01-27 10:54:41
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface PointsRuleMapper{
	
	/**
	 * 根据主键id查询积分规则
	 *
	 * @param id 主键
	 * @return 返回查询积分规则
	 */
	PointsRule selectByPrimaryKey(String id);
	
	/**
	 * 根据code查询积分规则
	 * @param pointsRule pointsRule
	 * @return 返回查询积分规则
	 */
	PointsRule selectByCode(@Param("code") String code);
	
	/**
	 * 查询所有积分规则
	 *
	 * @return 返回查询积分规则集合
	 */
	List<PointsRule> queryByParam();
	
	/**
	 * 添加积分规则
	 *
	 * @param pointsRule 请求参数
	 */
	void insert(PointsRule pointsRule);
	
	/**
	 * 按需求添加积分规则
	 *
	 * @param pointsRule 请求参数
	 */
	void insertSelective(PointsRule pointsRule);
	
	/**
	 * 按需求更新积分规则
	 *
	 * @param pointsRule 请求参数
	 */
	void updateByPrimaryKeySelective(PointsRule pointsRule);
	
	/**
	 * 按照主键id删除积分规则
	 *
	 * @param id 主键id
	 */
	void deleteByPrimaryKey(String id);
	
	/**
	 *  批量修改状态
	 *
	 * @param ids 请求参数
	 * @param status 启用、禁用
	 * @param updatTime 更新时间
	 */
	void batchUpdateStatus(@Param("ids") List<String> ids,
			  @Param("status") Integer status,
              @Param("updateTime") Date updatTime);
	
	/**
	 * 查询有效积分规则列表
	 * @author luosm
	 * @return list
	 */
	List<PointsRule> findValidList();
}