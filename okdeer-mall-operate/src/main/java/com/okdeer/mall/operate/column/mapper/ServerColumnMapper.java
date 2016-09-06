
package com.okdeer.mall.operate.column.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.okdeer.mall.operate.entity.ServerColumn;
import com.okdeer.mall.operate.entity.ServerColumnQueryVo;
import com.yschome.base.common.exception.ServiceException;

/**
 * 
 * ClassName: ServerColumnMapper 
 * @Description: 服务栏目
 * @author tangy
 * @date 2016年7月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构二期V4.1		  2016-07-12		 tangy			     新增
 * 	        重构二期V4.1		  2016-07-18		 luosm			 新增方法
 */
@Repository
public interface ServerColumnMapper {

	/**
	 * 
	 * @Description: 根据条件查询服务栏目列表
	 * @param serverColumnQueryVo
	 * @return List<ServerColumn>  
	 * @throws
	 * @author tangy
	 * @date 2016年7月12日
	 */
	List<ServerColumn> findByServerColumnQueryVo(ServerColumnQueryVo serverColumnQueryVo);
	
	/**
	 * 
	 * @Description: 根据id获取服务栏目信息
	 * @param id  服务栏目id
	 * @return ServerColumn  
	 * @throws
	 * @author tangy
	 * @date 2016年7月12日
	 */
	ServerColumn findById(@Param("id")String id);
	
	/**
	 * 
	 * @Description: 新增服务栏目
	 * @param serverColumn   服务栏目 
	 * @return void  
	 * @throws
	 * @author tangy
	 * @date 2016年7月12日
	 */
	int insert(ServerColumn serverColumn) throws ServiceException;
	
	/**
	 * 
	 * @Description: 更新服务栏目
	 * @param serverColumn  服务栏目
	 * @return int  
	 * @throws
	 * @author tangy
	 * @date 2016年7月12日
	 */
	int updateServerColumn(ServerColumn serverColumn) throws ServiceException;
	
	/**
	 * 
	 * @Description: 根据服务栏目id删除服务栏目 
	 * @param id  服务栏目id
	 * @param updateTime    操作时间
	 * @param updateUserId  操作人
	 * @return int  
	 * @throws
	 * @author tangy
	 * @date 2016年7月12日
	 */
	int deleteById(@Param("id")String id, @Param("updateTime")Date updateTime, @Param("updateUserId")String updateUserId) throws ServiceException;
	
	/**
	 * 
	 * @Description: 更新服务栏目排序值
	 * @param id               服务服务栏目id
	 * @param sort             排序值
	 * @param updateUserId     操作人
	 * @throws ServiceException
	 * @author tangy
	 * @date 2016年7月19日
	 */
	void updateServerColumnSortById(@Param("id")String id, @Param("sort")Integer sort, 
			@Param("updateUserId")String updateUserId, @Param("updateTime")Date updateTime) throws ServiceException;
	
	/**
	 * 
	 * @Description: 更改服务栏目服务状态
	 * @param id  服务栏目id
	 * @param serverStatus  服务状态
	 * @param updateUserId  操作用户
	 * @throws ServiceException
	 * @author tangy
	 * @date 2016年7月19日
	 */
	void updateServerStatusById(@Param("id")String id, @Param("serverStatus")Integer serverStatus, 
			@Param("updateUserId")String updateUserId, @Param("updateTime")Date updateTime) throws ServiceException;
	
	//begin add by luosm 2016-07-18
	/**
	 * 
	 * @Description: 根据服务栏目id查询服务栏目信息
	 * @param id
	 * @return
	 * @author luosm
	 * @date 2016年7月18日
	 */
	List<ServerColumn> findUserAppById(List<String> ids);
	//end add by luosm 2016-07-18
	
	//begin add by luosm 2016-07-19
	/**
	 * 
	 * @Description:根据城市id查询服务栏目
	 * @return
	 * @author luosm
	 * @date 2016年7月19日
	 */
	List<ServerColumn> findByRangeType(@Param("cityId")String cityId);
	//end add by luosm 2016-07-19
}
