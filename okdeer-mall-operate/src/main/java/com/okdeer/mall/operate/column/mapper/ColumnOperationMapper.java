package com.okdeer.mall.operate.column.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.mall.operate.entity.ColumnOperation;
import com.okdeer.mall.operate.entity.ColumnOperationQueryVo;
import com.okdeer.mall.operate.entity.ColumnOperationVo;
import com.okdeer.mall.operate.enums.ColumnOperationType;
import com.okdeer.mall.operate.enums.State;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.dal.IBaseCrudMapper;

/**
 * @DESC: 运营栏目mapper
 * @author wusw
 * @date  2016-01-13 19:13:03
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构 4.1			2016-7-19			wusw	                         修改查询运营栏目时间、地区冲突的方法
 */
@Repository
public interface ColumnOperationMapper extends IBaseCrudMapper {
	
	/**
	 * 
	 * 根据查询条件，查询运营栏目任务信息列表（参数实体类型） 
	 *
	 * @param columnOperation
	 * @return
	 */
	List<ColumnOperation> selectByEntity(ColumnOperation columnOperation);
	
	/**
	 * 
	 * 逻辑删除运营栏目任务（单个） 
	 *
	 * @param id 主键id
	 * @param disabled 是否逻辑删除
	 * @param updateTime 修改时间
	 * @param updateUserId 修改人id
	 */
	void deleteById(@Param("id")String id,@Param("disabled") Disabled disabled,
            @Param("updateTime")Date updateTime,@Param("updateUserId")String updateUserId);
	
	/**
	 * 
	 * 停用运营栏目任务 
	 *
	 * @param id 主键id
	 * @param state 状态
	 * @param updateTime 修改时间
	 * @param updateUserId 修改人id
	 */
	void disableById(@Param("id")String id,@Param("state") State state,
            @Param("updateTime")Date updateTime,@Param("updateUserId")String updateUserId);
	
	/**
	 * 
	 * 根据主键id，获取运营栏目任务详细信息（包括区域、小区信息）
	 *
	 * @param id 主键id
	 * @return 运营栏目任务详细信息（包括区域、小区信息）
	 */
	ColumnOperationVo selectOperationAssociateById(String id);
	
	/**
	 * 
	 * 查询指定名称的运营栏目任务记录数量 
	 *
	 * @param columnOperation 运营栏目任务实体
	 * @return 记录数量
	 */
	int selectCountByName(ColumnOperation columnOperation);

	// Begin 重构4.1  add by wusw  20160719
	/**
	 * 
	 * @Description: 查询指定关联栏目下指定开始结束时间有交集、指定区域有交集的记录数量
	 * @param params
	 * @return int 
	 * @author wusw
	 * @date 2016年7月19日
	 */
	int selectCountByDistrict(Map<String,Object> params);
	// End 重构4.1  add by wusw  20160719
	
	/**
	 * 
	 * 根据市id或者小区id，获取正在进行中的运营栏目信息列表 
	 * 
	 * @param params
	 * @return
	 */
	List<ColumnOperationQueryVo> selectByCityOrCommunity(Map<String,Object> params);
	
	/**
	 * 
	 * 统计在某一指定时间之后更新的记录数量
	 *
	 * @param updateTime
	 * @return
	 */
	int selectCountByUpdateTime(Date updateTime);
	
	/**
	 * 
	 *定时任务查询
	 *
	 * @param nowTime
	 * @return
	 */
	List<ColumnOperation> listByJob(Date nowTime);
	
	/**
	 * 
	 * 修改运营栏目任务状态（批量）
	 *
	 * @param ids
	 * @param state
	 * @param updateTime
	 * @param updateUserId
	 */
	void updateStateByIds(@Param("ids")List<String> ids,@Param("state") State state,
            @Param("updateTime")Date updateTime,@Param("updateUserId")String updateUserId);
	
}