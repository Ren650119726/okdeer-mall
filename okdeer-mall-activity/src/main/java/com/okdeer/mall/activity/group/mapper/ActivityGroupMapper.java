package com.okdeer.mall.activity.group.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.archive.system.entity.PsmsAgent;
import com.okdeer.archive.system.entity.PsmsSmallCommunityInfo;
import com.okdeer.mall.activity.group.entity.ActivityGroup;
import com.okdeer.mall.activity.group.entity.ActivityGroupArea;
import com.okdeer.mall.activity.group.entity.ActivityGroupCommuntity;
import com.yschome.base.common.exception.ServiceException;

/**
 * 
 * 
 * @pr mall
 * @desc 团购活动mapper
 * @author chenwj
 * @date 2016年1月6日 下午5:21:17
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 */
public interface ActivityGroupMapper {

	/**
	 * 添加团购活动
	 * @param activityGroup ActivityGroup
	 * @throws ServiceException ServiceException
	 */
	void insert(ActivityGroup activityGroup) throws ServiceException;
	
	/**
	 * 修改团购活动
	 * @param activityGroup ActivityGroup
	 * @throws ServiceException ServiceException
	 */
	void update(ActivityGroup activityGroup) throws ServiceException;
	
	/**
	 * 根据ID 删除团购活动
	 * @param id String
	 * @throws ServiceException ServiceException
	 */
	void deleteByPrimaryKey(String id) throws ServiceException;
	
	/**
	 * 根据ID 查询团购活动
	 * @param id String
	 * @return ActivityGroup
	 */
	ActivityGroup selectByPrimaryKey(String id);
	
	/**
	 * 查询所有团购活动
	 * @param activityGroup ActivityGroup
	 * @return List
	 */
	List<ActivityGroup> findActivityGroups(ActivityGroup activityGroup);
	
	/**
	 * 添加团购和区域关联
	 * @param activityGroupArea ActivityGroupArea
	 * @throws ServiceException ServiceException
	 */
	void insertActivityGroupArea(ActivityGroupArea activityGroupArea) throws ServiceException;
	
	/**
	 * 添加团购和小区关联
	 * @param activityGroupCommuntity ActivityGroupCommuntity
	 * @throws ServiceException ServiceException
	 */
	void insertActivityGroupCommuntity(ActivityGroupCommuntity activityGroupCommuntity) throws ServiceException;
	
	/**
	 * 删除团购和区域关联
	 * @param groupId String
	 * @throws ServiceException ServiceException
	 */
	void deleteActivityGroupArea(String groupId) throws ServiceException;
	
	/**
	 * 删除团购和小区关联 
	 * @param groupId String
	 * @throws ServiceException ServiceException
	 */
	void deleteActivityGroupCommuntity(String groupId) throws ServiceException;
	
	/**
	 * 根据团购ID查询团购和区域关联
	 * @param groupId String
	 * @return List
	 */
	List<ActivityGroupArea> findActivityGroupAreas(String groupId);
	
	/**
	 * 根据团购ID查询团购和小区关联
	 * @param groupId String
	 * @return List
	 */
	List<ActivityGroupCommuntity> findActivityGroupCommuntitys(String groupId);
	
	/**
	 * 根据店铺skuid查询商品信息
	 * @param storeSkuId String
	 * @return ActivityGroupGoodsVo
	 */
	/*ActivityGroupGoodsVo getActivityGroupGoodsBySkuId(String storeSkuId);*/
	
	/**
	 * 根据条件获取团购活动列表
	 * @param activityGroup ActivityGroup
	 * @return List
	 */
	List<ActivityGroup> findActivityGroupList(ActivityGroup activityGroup);
	
	/**
	 * 根据城市ID查询小区列表 
	 * @param cityId String
	 * @return List
	 */
	List<PsmsSmallCommunityInfo> findCommuntityByCityId(@Param("cityId") String cityId,@Param("agentId") String agentId);
	
	/**
	 * 根据ID查看代理商
	 * @param id String
	 * @return PsmsAgent
	 */
	PsmsAgent getPsmsAgent(String id);
	
	/**
	 * 查询所有团购活动
	 * @param map Map
	 * @return List
	 */
	List<ActivityGroup> findActivityGroupsByPrames(Map<String,Object> map);

	/**
	 * 查询待审核团购数量
	 */
	int selectUnApprovalNum(Map<String, Object> map);
	
	/**
	 * 根据ID查询小区列表
	 * @return PsmsSmallCommunityInfo
	 */
	PsmsSmallCommunityInfo findCommuntity(String id);
	
	/**
	 * 根据活动activityId、用户所在区域areaId
	 * @param activityId 请求参数
	 * @return 返回结果
	 */
	ActivityGroupArea selectAreaBygroupIdAndAreaId(@Param("activityId")String activityId,@Param("areaId")String areaId);
	
	/**
	 * 根据活动activityId、用户所在区域communityId
	 * @param activityId 请求参数
	 * @return 返回结果
	 */
	ActivityGroupCommuntity selectCommuntitysBygroupIdAndAreaId(@Param("activityId")String activityId,
	    @Param("communityId")String communityId);
	
	/**
	 * 
	 * 查询团购活动时间 </p>
	 * 
	 * @author yangq
	 * @param activityId
	 * @return
	 */
	ActivityGroup selectServiceTime(String activityId);
		
	/**
	 * 根据ID 查询团购活动 已经开始的
	 * @param id String
	 * @return ActivityGroup
	 */
	ActivityGroup selectById(String id);
	
	/**
	 * 查询团购状态 </p>
	 * @param activityId
	 * @return
	 */
	ActivityGroup selectGroupStatus(String activityId);
	
}
