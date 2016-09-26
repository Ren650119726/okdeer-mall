/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: PointsRuleService.java 
 * @Date: 2016年1月27日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */ 

package com.okdeer.mall.points.service;

import java.util.List;

import com.okdeer.mall.member.points.entity.PointsRule;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;

/**
 * 积分规则service
 * @project yschome-mall
 * @author zhongy
 * @date 2016年1月27日 上午11:36:20
 */
public interface PointsRuleService {

	/**
	 * 根据主键id查询积分规则
	 *
	 * @param id 主键
	 * @return 返回查询积分规则
	 */
	PointsRule findById(String id) throws ServiceException;
	
	/**
	 * 条件查询积分规则
	 *
	 * @return 返回查询积分规则集合
	 */
	PageUtils<PointsRule> queryByParam(Integer pageNumber,Integer pageSize) throws ServiceException;
	
	/**
	 * 查询所有积分规则
	 *
	 * @return 返回查询积分规则集合
	 */
	List<PointsRule> queryAll() throws ServiceException;
	
	/**
	 * 添加积分规则
	 *
	 * @param pointsRule 请求参数
	 */
	void add(PointsRule pointsRule) throws ServiceException;
	
	/**
	 * 按需求添加积分规则
	 *
	 * @param pointsRule 请求参数
	 */
	void addSelective(PointsRule pointsRule) throws ServiceException;
	
	/**
	 * 按需求更新积分规则
	 *
	 * @param pointsRule 请求参数
	 */
	void updateByIdSelective(PointsRule pointsRule) throws ServiceException;
	
	/**
	 * 按照主键id删除积分规则
	 *
	 * @param id 主键id
	 */
	void deleteById(String id) throws ServiceException;
	
	/**
	 *  批量修改状态
	 *
	 * @param ids 请求参数
	 * @param status 启用、禁用
	 */
	void updateStatus(List<String> ids,Integer status) throws ServiceException;
	
	
	/**
	 * 查询激活状态积分规则
	 * @author luosm
	 * @return list
	 */
	List<PointsRule> queryValidList()throws ServiceException;
	
}
