package com.okdeer.mall.operate.column.service;

import java.util.List;

import com.okdeer.mall.operate.entity.ServerColumn;
import com.okdeer.mall.operate.entity.ServerColumnArea;
import com.okdeer.mall.operate.entity.ServerColumnQueryVo;
import com.okdeer.mall.operate.entity.ServerColumnStore;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;

/**
 * 
 * ClassName: ServerColumnService 
 * @Description: 服务栏目service接口
 * @author tangy
 * @date 2016年7月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构二期V4.1		  2016-07-12		 tangy			     新增
 *     重构二期V4.1		  2016-07-18		 zengj			     新增根据店铺ID查询服务栏目
 * 	        重构二期V4.1		  2016-07-18		 luosm			 新增方法
 * 	        重构二期V4.1		  2016-07-19		 luosm			 新增findByRangeType方法
 * 	        重构4.1            2016-8-3             wushp         用户app订单列表或者订单详情--再逛逛按钮
 */
public interface ServerColumnService {

	/**
	 * 
	 * @Description: 服务栏目搜索
	 * @param serverColumnQueryVo 
	 * @param pageNumber 页码
	 * @param pageSize 每页数量
	 * @throws ServiceException   
	 * @return PageUtils<ServerColumn>  
	 * @author tangy
	 * @date 2016年7月12日
	 */
	PageUtils<ServerColumn> findByServerColumn(ServerColumnQueryVo serverColumnQueryVo, int pageNumber, int pageSize)
			throws ServiceException;

	/**
	 * 
	 * @Description: 新增服务栏目
	 * @param serverColumn  服务栏目
	 * @throws ServiceException   
	 * @return void  
	 * @author tangy
	 * @date 2016年7月12日
	 */
	void addServerColumn(ServerColumn serverColumn) throws ServiceException;

	/**
	 * 
	 * @Description: 更新服务栏目
	 * @param serverColumn  服务栏目
	 * @throws ServiceException   
	 * @return void  
	 * @author tangy
	 * @date 2016年7月12日
	 */
	void updateServerColumn(ServerColumn serverColumn) throws ServiceException;

	/**
	 * 
	 * @Description: 删除服务栏目
	 * @param id  服务栏目id
	 * @param id updateUserId 操作人
	 * @throws ServiceException   
	 * @return void  
	 * @author tangy
	 * @date 2016年7月12日
	 */
	void deleteById(String id, String updateUserId) throws ServiceException;

	/**
	 * 
	 * @Description: 根据id查询服务栏目
	 * @param id  服务栏目id
	 * @throws ServiceException 
	 * @return ServerColumn  服务栏目
	 * @author tangy
	 * @date 2016年7月12日
	 */
	ServerColumn findById(String id) throws ServiceException;

	// begin add by luom 2016-07-18
	/***
	 * 
	 * @Description: 根据id查询服务栏目
	 * @param ServerColumn
	 * @return
	 * @author luosm
	 * @date 2016年7月18日
	 */
	List<ServerColumn> findUserAppById(List<String> ids) throws ServiceException;

	// end add by luosm 2016-07-18
	// Begin 重构4.1 added by zengj
	/**
	 * 
	 * @Description: 根据店铺ID查询服务栏目
	 * @param storeId 店铺名称
	 * @return ServerColumn  服务栏目
	 * @throws ServiceException 抛出异常   
	 * @author zengj
	 * @date 2016年7月18日
	 */
	ServerColumn findByStoreId(String storeId) throws ServiceException;

	// End 重构4.1 added by zengj
	
	// Begin 重构4.1 added by wushp
	/**
	 * 
	 * @Description: 根据店铺ID查询服务栏目
	 * @param storeId 店铺id
	 * @return ServerColumnStore  服务栏目
	 * @throws ServiceException 抛出异常   
	 * @author wushp
	 * @date 2016年8月3日
	 */
	public ServerColumnStore findServerStoreByStoreId(String storeId) throws ServiceException;
	// End 重构4.1 added by wushp

	// begin add by luom 2016-07-19
	/***
	 * 
	 * @Description: 根据城市id查询服务栏目
	 * @return
	 * @throws ServiceException
	 * @author luosm
	 * @date 2016年7月19日
	 */
	List<ServerColumn> findByRangeType(String cityId,String provinceId) throws ServiceException;
	// end add by luosm 2016-07-19
}
