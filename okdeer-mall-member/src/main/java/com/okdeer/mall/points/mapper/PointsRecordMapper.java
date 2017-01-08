
package com.okdeer.mall.points.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.member.points.entity.PointsRecord;
import com.okdeer.mall.points.bo.StatisRecordParamBo;

/**
 * @DESC: 用户积分详细记录dao
 * @author zhongy
 * @date  2016-01-27 10:54:41
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface PointsRecordMapper {

	/**
	 * 按照参数条件查询用户积分变动记录
	 *
	 * @param params 请求参数
	 * @return 返回查询积分变动结果集
	 */
	List<PointsRecord> selectByParams(@Param("params") Map<String, Object> params);

	/**
	 * 按照参数条件查询用户积分变动记录
	 *
	 * @param params 请求参数
	 * @return 返回查询积分变动结果集
	 */
	List<PointsRecord> selectDayByParams(@Param("params") Map<String, Object> params);

	/**
	 * 条件积分统计
	 * @param params 请求参数
	 * @return 条件积分统计结果
	 */
	Integer countByParams(@Param("params") Map<String, Object> params);

	/**
	 * 条件查询当前当天积分统计
	 * @param params 请求参数
	 * @return 条件查询当前当天积分统计
	 */
	Integer currentSumByParams(@Param("params") Map<String, Object> params);

	/**
	 * 条件查询当前当天操作次数
	 * @param params 请求参数
	 * @return 条件查询当前当天操作次数
	 */
	Integer currentCountByParams(@Param("params") Map<String, Object> params);

	/**
	 * 添加积分变动记录
	 *
	 * @param pointsRecord 请求参数
	 */
	void insert(PointsRecord pointsRecord);

	/**
	 * 按需添加积分变动记录
	 *
	 * @param pointsRecord 请求参数
	 */
	void insertSelective(PointsRecord pointsRecord);

	/**
	 * @Description: 统计纪录数
	 * @param paramBo
	 * @return
	 * @author zengjizu
	 * @date 2016年12月30日
	 */
	int statisRecordCount(StatisRecordParamBo paramBo);

	/**
	 * @Description: 统计积分数
	 * @param paramBo
	 * @return
	 * @author zengjizu
	 * @date 2016年12月30日
	 */
	int statisRecordPoint(StatisRecordParamBo paramBo);
	
	/**
	 * @Description: 根据业务id 查询记录数
	 * @param referentId 业务id
	 * @return
	 * @author zengjizu
	 * @date 2017年1月5日
	 */
	int findCountByReferentId(@Param("referentId")String referentId);
	
	/**
	 * @Description: 根据业务id 查询记录
	 * @param referentId 业务id
	 * @return
	 * @author zengjizu
	 * @date 2017年1月7日
	 */
	PointsRecord findByReferentId(@Param("referentId")String referentId);
}