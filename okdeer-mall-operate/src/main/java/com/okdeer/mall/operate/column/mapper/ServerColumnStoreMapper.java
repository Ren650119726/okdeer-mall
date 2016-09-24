package com.okdeer.mall.operate.column.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.okdeer.mall.operate.entity.ServerColumnStore;
import com.okdeer.base.common.exception.ServiceException;

/**
 * 
 * ClassName: ServerColumnStoreMapper 
 * @Description: 服务栏目店铺关联
 * @author tangy
 * @date 2016年7月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构二期V4.1		  2016-07-12		 tangy			     新增
 *     重构二期V4.1		  2016-07-18		 zengj			     新增根据店铺ID查询服务栏目与店铺关系
 * 
 */
@Repository
public interface ServerColumnStoreMapper {

	/**
	 * 
	 * @Description: 根据服务栏目id查询关联店铺
	 * @param serverColumnId  服务栏目id
	 * @return List<ServerColumnStore>  
	 * @throws
	 * @author tangy
	 * @date 2016年7月12日
	 */
	List<ServerColumnStore> findByServerColumnId(@Param("serverColumnId")String serverColumnId);
	
	/**
	 * 
	 * @Description: 新增服务栏目关联区域
	 * @param serverColumnStore 关联区域
	 * @return int  
	 * @throws ServiceException
	 * @author tangy
	 * @date 2016年7月12日
	 */
	int insert(ServerColumnStore serverColumnStore) throws ServiceException;
	
	/**
	 * 
	 * @Description: 批量新增服务栏目关联区域
	 * @param serverColumnStores 关联区域集
	 * @return  int
	 * @throws ServiceException
	 * @author tangy
	 * @date 2016年7月20日
	 */
	int insertList(@Param(value = "serverColumnStores") List<ServerColumnStore> serverColumnStores) throws ServiceException;
	
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
	
	// Begin 重构4.1 add by zengj
	/**
	 * 
	 * @Description: 根据店铺ID查询服务栏目信息
	 * @param storeId 店铺ID
	 * @return ServerColumnStore 服务栏目与店铺关系  
	 * @author zengj
	 * @date 2016年7月18日
	 */
	ServerColumnStore findByStoreId(@Param("storeId")String storeId);
	
	// End 重构4.1 add by zengj
	
	/**
	 * 
	 * @Description: 根据店铺id查询是否已被关联
	 * @param storeIds  店铺id集
	 * @return int
	 * @throws ServiceException
	 * @author tangy
	 * @date 2016年8月3日
	 */
	int findByStoreIds(@Param("storeIds")List<String> storeIds) throws ServiceException;
	
}
