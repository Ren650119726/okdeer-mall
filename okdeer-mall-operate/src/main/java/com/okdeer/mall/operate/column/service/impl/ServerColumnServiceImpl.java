
package com.okdeer.mall.operate.column.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.operate.column.mapper.ServerColumnAreaMapper;
import com.okdeer.mall.operate.column.mapper.ServerColumnMapper;
import com.okdeer.mall.operate.column.mapper.ServerColumnStoreMapper;
import com.okdeer.mall.operate.column.service.ServerColumnService;
import com.okdeer.mall.operate.entity.ServerColumn;
import com.okdeer.mall.operate.entity.ServerColumnArea;
import com.okdeer.mall.operate.entity.ServerColumnQueryVo;
import com.okdeer.mall.operate.entity.ServerColumnStore;
import com.okdeer.mall.operate.service.IServerColumnServiceApi;

/**
 * 
 * ClassName: ServerColumnServiceImpl 
 * @Description: 服务栏目 
 * @author tangy
 * @date 2016年7月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构二期V4.1		  2016-07-12		 tangy			     新增
 *     重构二期V4.1		  2016-07-18		 zengj			     新增根据店铺ID查询服务栏目
 * 	         重构二期V4.1		  2016-07-18		 luosm			 新增方法
 * 	        重构二期V4.1		  2016-07-21		 luosm			 优化方法
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.operate.service.IServerColumnServiceApi")
public class ServerColumnServiceImpl implements ServerColumnService, IServerColumnServiceApi {

	/**
	 * 日志记录
	 */
	private static final Logger log = Logger.getLogger(ServerColumnServiceImpl.class);

	/**
	 * 服务栏目
	 */
	@Autowired
	private ServerColumnMapper serverColumnMapper;

	/**
	 * 服务栏目关联区域
	 */
	@Autowired
	private ServerColumnAreaMapper serverColumnAreaMapper;

	/**
	 * 服务栏目关联店铺
	 */
	@Autowired
	private ServerColumnStoreMapper serverColumnStoreMapper;

	@Override
	public PageUtils<ServerColumn> findByServerColumn(ServerColumnQueryVo serverColumnQueryVo, int pageNumber,
			int pageSize) throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ServerColumn> result = serverColumnMapper.findByServerColumnQueryVo(serverColumnQueryVo);
		if (result == null) {
			result = new ArrayList<ServerColumn>();
		}
		return new PageUtils<ServerColumn>(result);
	}

	@Override
	public ServerColumn findById(String id) throws ServiceException {
		return serverColumnMapper.findById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addServerColumn(ServerColumn serverColumn) throws ServiceException {
		if (serverColumn == null) {
			throw new ServiceException("服务栏目添加对象不能为空");
		}
		String serverColumnId = UuidUtils.getUuid();
		serverColumn.setId(serverColumnId);
		serverColumn.setDisabled(Disabled.valid);
		// 关联区域
		List<ServerColumnArea> serverColumnAreas = serverColumn.getServerColumnAreas();
		// 关联店铺
		List<ServerColumnStore> serverColumnStores = serverColumn.getServerColumnStores();

		serverColumnMapper.insert(serverColumn);

		// 插入关联区域
		if (serverColumnAreas != null) {
			for (ServerColumnArea serverColumnArea : serverColumnAreas) {
				serverColumnArea.setId(UuidUtils.getUuid());
				serverColumnArea.setColumnServerId(serverColumnId);
			}
			serverColumnAreaMapper.insertList(serverColumnAreas);
		}

		// 插入关联店铺
		if (serverColumnStores != null) {
			addServerColumnStore(serverColumnStores, serverColumnId);
		}

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateServerColumn(ServerColumn serverColumn) throws ServiceException {
		if (serverColumn == null) {
			throw new ServiceException("服务栏目更新对象不能为空");
		}

		// 关联区域
		List<ServerColumnArea> serverColumnAreas = serverColumn.getServerColumnAreas();
		// 关联店铺
		List<ServerColumnStore> serverColumnStores = serverColumn.getServerColumnStores();
		serverColumn.setUpdateTime(new Date());
		serverColumnMapper.updateServerColumn(serverColumn);
		serverColumnAreaMapper.deleteByServerColumnId(serverColumn.getId());
		serverColumnStoreMapper.deleteByServerColumnId(serverColumn.getId());

		// 插入关联区域
		if (serverColumnAreas != null) {
			for (ServerColumnArea serverColumnArea : serverColumnAreas) {
				serverColumnArea.setId(UuidUtils.getUuid());
				serverColumnArea.setColumnServerId(serverColumn.getId());
			}
			serverColumnAreaMapper.insertList(serverColumnAreas);
		}

		// 插入关联店铺
		if (serverColumnStores != null) {
			addServerColumnStore(serverColumnStores, serverColumn.getId());
		}
	}
	
	/**
	 * 
	 * @Description: 添加服务店铺关联
	 * @param serverColumnStores
	 * @param serverColumnId
	 * @throws ServiceException
	 * @author tangy
	 * @date 2016年8月3日
	 */
	private void addServerColumnStore(List<ServerColumnStore> serverColumnStores, String serverColumnId) throws ServiceException{
		List<String> storeIds = new ArrayList<String>();
		for (ServerColumnStore serverColumnStore : serverColumnStores) {
			serverColumnStore.setId(UuidUtils.getUuid());
			serverColumnStore.setColumnServerId(serverColumnId);
			storeIds.add(serverColumnStore.getStoreId());
		}
		int count = serverColumnStoreMapper.findByStoreIds(storeIds);
		if (count > 0) {
			throw new ServiceException("店铺已被关联"); 
		}
		serverColumnStoreMapper.insertList(serverColumnStores);		
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteById(String id, String updateUserId) throws ServiceException {
		log.info("=====删除服务栏目id：" + id + "==操作人：" + updateUserId);
		serverColumnMapper.deleteById(id, new Date(), updateUserId);
		serverColumnAreaMapper.deleteByServerColumnId(id);
		serverColumnStoreMapper.deleteByServerColumnId(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateServerColumnSortById(String id, Integer sort, String updateUserId) throws ServiceException {
		serverColumnMapper.updateServerColumnSortById(id, sort, updateUserId, new Date());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateServerStatusById(String id, Integer serverStatus, String updateUserId) throws ServiceException {
		serverColumnMapper.updateServerStatusById(id, serverStatus, updateUserId, new Date());
	}

	// begin add by luosm 2016-07-18
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.column.service.ServerColumnService#userAppFindById(java.util.List)
	 */
	@Override
	public List<ServerColumn> findUserAppById(List<String> ids) {
		return serverColumnMapper.findUserAppById(ids);
	}
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
	public ServerColumn findByStoreId(String storeId) throws ServiceException {
		// 根据店铺ID查询店铺与服务栏目关系
		ServerColumnStore serverColumnStore = serverColumnStoreMapper.findByStoreId(storeId);
		if (serverColumnStore != null) {
			// 根据服务栏目ID查询服务栏目
			return findById(serverColumnStore.getColumnServerId());
		}
		return null;
	}
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
	public ServerColumnStore findServerStoreByStoreId(String storeId) throws ServiceException {
		// 根据店铺ID查询店铺与服务栏目关系
		return serverColumnStoreMapper.findByStoreId(storeId);
	}
	// End 重构4.1 added by wushp

	// Begin 重构4.1 added by luosm 2016-07-21
	/***
	 * 
	 * @Description: 根据城市id查询服务栏目
	 * @return List<ServerColumn> 
	 * @throws ServiceException
	 * @author luosm
	 * @date 2016年7月19日
	 */
	@Override
	public List<ServerColumn> findByRangeType(String cityId,String provinceId) throws ServiceException {
		return serverColumnMapper.findByRangeType(cityId,provinceId);
	}
	// End 重构4.1 added by luosm 2016-07-21

	@Override
	public List<String> findStoreIdsByIds(List<String> storeIds) throws ServiceException {
		if (CollectionUtils.isEmpty(storeIds)) {
			return storeIds;
		}
		return serverColumnMapper.findStoreIdsByIds(storeIds);
	}
}
