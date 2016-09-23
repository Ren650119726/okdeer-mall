/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: ColumnOperationService.java 
 * @Date: 2016年1月14日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */ 
package com.okdeer.mall.operate.column.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.okdeer.mall.operate.entity.ColumnOperation;
import com.okdeer.mall.operate.entity.ColumnOperationQueryVo;
import com.okdeer.mall.operate.entity.ColumnOperationVo;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;

/**
 * 运营栏目service接口
 * @project yschome-mall
 * @author wusw
 * @date 2016年1月14日 上午9:32:13
 */
public interface ColumnOperationService {
	
	/**
	 * 
	 * 根据条件获取运营栏目任务列表（参数类型实体）
	 *
	 * @param columnOperation 查询条件（实体）
	 * @param pageNumber 页码
	 * @param pageSize 每页数量
	 * @return 运营栏目列表
	 * @throws ServiceException
	 */
	PageUtils<ColumnOperation> findByEntity(ColumnOperation columnOperation,int pageNumber,int pageSize) throws ServiceException;
	
	
	/**
	 * 
	 * 根据主键id获取运营栏目任务详细信息 （包括区域、小区信息）
	 *
	 * @param id 主键id
	 * @return 运营栏目任务详细信息 （包括区域、小区信息）
	 * @throws ServiceException
	 */
	ColumnOperationVo getColumnOperationVoById(String id) throws ServiceException;
	
	/**
	 * 
	 * 根据主键id获取运营栏目任务信息
	 *
	 * @param id 主键id
	 * @return 运营栏目任务信息
	 * @throws ServiceException
	 */
	ColumnOperation getById(String id) throws ServiceException;
	
	/**
	 * 
	 * 添加运营栏目任务
	 *
	 * @param columnOperation 运营栏目任务
	 * @param currentOperateUserId 当前登陆用户ID
	 * @throws ServiceException
	 */
	void addColumnOperation(ColumnOperation columnOperation,String currentOperateUserId) throws ServiceException;
	
	/**
	 * 
	 * 修改运营栏目任务
	 *
	 * @param columnOperation 运营栏目任务
	 * @param currentOperateUserId 当前登陆用户ID
	 * @throws ServiceException
	 */
	void updateColumnOperation(ColumnOperation columnOperation,String currentOperateUserId) throws ServiceException;
	
	/**
	 * 
	 * 逻辑删除运营栏目任务（单个） 
	 *
	 * @param id 主键id
	 * @param currentOperateUserId 当前登陆用户ID
	 * @throws ServiceException
	 */
	void deleteColumnOperation(String id,String currentOperateUserId) throws ServiceException;
	
	/**
	 * 
	 * 停用运营栏目任务
	 *
	 * @param id 主键id
	 * @param currentOperateUserId 当前登陆用户ID
	 * @throws ServiceException
	 */
	void disableColumnOperation(String id,String currentOperateUserId) throws ServiceException;
	
	/**
	 * 
	 * 查询指定名称相同的运营栏目任务记录数量  
	 *
	 * @param columnOperation
	 * @return
	 * @throws ServiceException
	 */
	int selectCountByName(ColumnOperation columnOperation) throws ServiceException;
	
	/**
	 * 
	 * 查询与指定开始结束时间有交集、指定区域有交集的运营栏目任务记录数量  
	 *
	 * @param columnOperation
	 * @param areaIdList 区域ID（省市ID）集合
	 * @param associateIdList 省下所有市和市所在省的id集合
	 * @return
	 * @throws ServiceException
	 */
	int selectCountByDistrict(ColumnOperation columnOperation,List<String> areaIdList,List<String> associateIdList) throws ServiceException;
	
	/**
	 * 
	 * 根据市id或者小区id，获取正在进行中的运营栏目信息列表 
	 *
	 * @param params
	 * @return
	 * @throws ServiceException
	 */
	List<ColumnOperationQueryVo> findByCityOrCommunity(Map<String,Object> params)throws ServiceException;
	
	/**
	 * 
	 * 统计在某一指定时间之后更新的记录数量
	 *
	 * @param updateTime
	 * @return
	 * @throws ServiceException
	 */
	int selectCountByUpdateTime(Date updateTime)throws ServiceException;
	
	void updateByJob();
}
