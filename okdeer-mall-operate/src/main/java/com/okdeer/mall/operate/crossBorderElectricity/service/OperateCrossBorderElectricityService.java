/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: CrossBorderElectricityService.java 
 * @Date: 2016年1月6日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */ 
package com.okdeer.mall.operate.crossBorderElectricity.service;

import java.util.List;

import com.okdeer.mall.operate.entity.OperateCrossBorderElectricity;
import com.yschome.base.common.enums.Disabled;
import com.yschome.base.common.exception.ServiceException;
import com.yschome.base.common.utils.PageUtils;

/**
 * 跨境电商设置的service接口
 * @project yschome-mall
 * @author wusw
 * @date 2016年1月6日 下午2:11:55
 */
public interface OperateCrossBorderElectricityService {

	/**
	 * 
	 *根据条件查询跨境电商设置列表信息（参数实体,分页）
	 *
	 * @param crossBorderElectricity 跨境电商设置实体（查询条件）
	 * @param pageNumber 页码
	 * @param pageSize 每页数量
	 * @return 列表信息
	 * @throws ServiceException
	 */
	PageUtils<OperateCrossBorderElectricity> findByParams(OperateCrossBorderElectricity crossBorderElectricity,
            int pageNumber,int pageSize) throws ServiceException;
	
	/**
	 * 
	 * 根据条件查询跨境电商设置列表信息（参数实体,不分页）
	 *
	 * @param crossBorderElectricity 跨境电商设置实体（查询条件）
	 * @param pageNumber 页码
	 * @param pageSize 每页数量
	 * @return 列表信息
	 * @throws ServiceException
	 */
	List<OperateCrossBorderElectricity> findListByParams() throws ServiceException;
	
	/**
	 * 
	 * 根据主键id获取跨境电商设置信息
	 *
	 * @param crossBorderElectricity 跨境电商设置实体
	 * @return 跨境电商设置详细信息
	 * @throws ServiceException
	 */
	OperateCrossBorderElectricity getById(String id) throws ServiceException;
	/**
	 * 
	 * 添加跨境电商设置信息
	 *
	 * @param crossBorderElectricity 跨境电商设置实体
	 * @param currentOperateUserId 当前登陆用户id
	 * @throws ServiceException
	 */
	void addCrossBorderElectricity(OperateCrossBorderElectricity crossBorderElectricity,String currentOperateUserId) throws ServiceException;
	
	/**
	 * 
	 * 修改跨境电商设置信息
	 *
	 * @param crossBorderElectricity 跨境电商设置实体
	 * @param currentOperateUserId 当前登陆用户id
	 * @throws ServiceException
	 */
	void updateCrossBorderElectricity(OperateCrossBorderElectricity crossBorderElectricity,String currentOperateUserId) throws ServiceException;
	
	/**
	 * 
	 * 批量逻辑删除跨境电商设置信息 
	 *
	 * @param ids 主键id集合
	 * @param currentOperateUserId 当前登陆用户id
	 * @throws ServiceException
	 */
	//void deleteByIds(List<String> ids,String currentOperateUserId) throws ServiceException;
	
	/**
	 * 
	 * 批量停用或启用
	 *
	 * @param ids
	 * @param disabled
	 * @param currentOperateUserId
	 * @throws ServiceException
	 */
	void disableByIds(List<String> ids,Disabled disabled,String currentOperateUserId) throws ServiceException;
	
	/**
	 * 
	 * 统计指定id集合的启用（或停用）状态的记录数量
	 *
	 * @param ids
	 * @param disabled
	 * @throws ServiceException
	 */
	int selectCountById(List<String> ids,Disabled disabled) throws ServiceException;
}
