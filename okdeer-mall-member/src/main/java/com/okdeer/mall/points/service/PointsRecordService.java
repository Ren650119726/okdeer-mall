/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: PointsRecordService.java 
 * @Date: 2016年1月27日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */ 

package com.okdeer.mall.points.service;

import java.util.List;
import java.util.Map;

import com.okdeer.mall.member.points.entity.PointsRecord;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;

/**
 * 用户积分变动记录service
 * @project yschome-mall
 * @author zhongy
 * @date 2016年1月27日 上午11:38:55
 */
public interface PointsRecordService {

	/**
	 * 按照参数条件查询用户积分变动记录
	 *
	 * @param param 请求参数
	 * @return 返回查询积分变动结果集
	 */
	PageUtils<PointsRecord> findByParams(Map<String,Object> param,Integer pageNumber,Integer pageSize) throws ServiceException;
	
	/**
	 * 条件积分统计
	 * @param param 请求参数
	 * @return 条件积分统计结果
	 */
	Integer countByParams(Map<String,Object> param) throws ServiceException;
	
	/**
	 * 添加积分变动记录
	 *
	 * @param pointsRecord 请求参数
	 */
	void add(PointsRecord pointsRecord) throws ServiceException;
	
	/**
	 * 按需添加积分变动记录
	 *
	 * @param pointsRecord 请求参数
	 */
	void addSelective(PointsRecord pointsRecord) throws ServiceException;
	
	
	/**
	 * 根据条件查询积分记录（不分页）
	 * @author luosm
	 * @return list
	 */
	List<PointsRecord> findByUserId(Map<String,Object> param)throws ServiceException;
	
}
