package com.okdeer.mall.operate.column.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.okdeer.mall.operate.entity.ServerColumnArea;
import com.yschome.base.common.exception.ServiceException;

/**
 * 
 * ClassName: ServerColumnAreaMapper 
 * @Description: 服务栏目链接范围区域关联
 * @author tangy
 * @date 2016年7月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构二期V4.1		  2016-07-12		 tangy			     新增
 *     重构4.1			  2016-07-19		luosm		                优化findByCityName方法
 */
@Repository
public interface ServerColumnAreaMapper {

	/**
	 * 
	 * @Description: 根据服务栏目id查询范围区域
	 * @param serverColumnId  服务栏目id
	 * @return List<ServerColumnArea>  
	 * @throws ServiceException
	 * @author tangy
	 * @date 2016年7月12日
	 */
	List<ServerColumnArea> findByServerColumnId(@Param("serverColumnId")String serverColumnId) throws ServiceException;
	
	/**
	 * 
	 * @Description: 批量新增服务栏目关联区域
	 * @param serverColumnArea 服务栏目
	 * @return int  
	 * @throws ServiceException
	 * @author tangy
	 * @date 2016年7月12日
	 */
	int insert(ServerColumnArea serverColumnArea) throws ServiceException;
	
	/**
	 * 
	 * @Description: 新增服务栏目关联区域
	 * @param serverColumnArea 服务栏目
	 * @return int
	 * @throws ServiceException
	 * @author tangy
	 * @date 2016年7月20日
	 */
	int insertList(@Param(value = "serverColumnAreas") List<ServerColumnArea> serverColumnAreas) throws ServiceException;
	
	/**
	 * 
	 * @Description: 根据服务栏目id删除关联区域
	 * @param serverColumnId
	 * @return int  
	 * @throws ServiceException
	 * @author tangy
	 * @date 2016年7月12日
	 */
	int deleteByServerColumnId(@Param("serverColumnId")String serverColumnId) throws ServiceException;
	
	
	/***
	 * 
	 * @Description:  根据城市名查询服务栏目id
	 * @param cityName
	 * @return 
	 * @author luosm
	 * @date 2016年7月15日
	 */
	List<String> findByCityName(@Param("cityName")String cityName,@Param("provinceName")String provinceName);
	
}
