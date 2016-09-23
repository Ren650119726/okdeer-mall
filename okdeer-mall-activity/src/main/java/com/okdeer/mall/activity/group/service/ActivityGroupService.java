package com.okdeer.mall.activity.group.service;

import java.util.List;
import java.util.Map;

import com.okdeer.archive.system.entity.PsmsSmallCommunityInfo;
import com.okdeer.archive.system.entity.SysUser;
import com.okdeer.mall.activity.group.entity.ActivityGroup;
import com.okdeer.mall.activity.group.entity.ActivityGroupArea;
import com.okdeer.mall.activity.group.entity.ActivityGroupCommuntity;
import com.okdeer.mall.activity.group.entity.ActivityGroupGoods;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;

import net.sf.json.JSONObject;

/**
 * 
 * 
 * @pr mall
 * @desc 团购活动 Service
 * @author chenwj
 * @date 2016年1月6日 下午5:21:17
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 */
public interface ActivityGroupService {

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
	 * @return PageUtils
	 */
	PageUtils<ActivityGroup> findActivityGroups(ActivityGroup activityGroup, int pageNumber, int pageSize);

	/**
	 * 查询所有团购活动
	 * @param map Map
	 * @param pageNumber int
	 * @param pageSize int
	 * @return List
	 */
	PageUtils<ActivityGroup> findActivityGroupsByPrames(Map<String, Object> map, int pageNumber, int pageSize);

	/**
	 * 查询所有团购活动
	 * @param groupId String
	 * @return PageUtils
	 */
	PageUtils<ActivityGroupGoods> getActivityGroupGoods(String groupId, String storeId, String online, int pageNumber,
			int pageSize);

	/**
	 * 查询所有团购活动
	 * @param groupId String
	 * @return PageUtils
	 */
	PageUtils<ActivityGroupGoods> getActivityGroupGoods(String groupId, String online, int pageNumber, int pageSize);

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
	List<PsmsSmallCommunityInfo> findCommuntityByCityId(String cityId, String agentId);

	/**
	 * 根据状态查询店铺活动的数量 (商家中心首页用)
	 *
	 * @param storeId
	 */
	int selectUnApprovalNum(String storeId, Integer approvalStatus, Integer status);

	/**
	 * 根据ID查询小区列表
	 * @param cityId String
	 * @return List
	 */
	PsmsSmallCommunityInfo findCommuntity(String id);

	/**
	 * 发布团购活动
	 * @param formJson String
	 */
	void publishActivityGroup(JSONObject json, SysUser user, Object city) throws Exception;

	/**
	 * 
	 * 查询团购活动时间 </p>
	 * 
	 * @author yangq
	 * @param activityId
	 * @return
	 */
	ActivityGroup selectServiceTime(String activityId) throws Exception;

	/**
	 * 根据ID 查询团购活动 已经开始的
	 * @param id String
	 * @return ActivityGroup
	 */
	ActivityGroup findByPrimaryKey(String id);

	ActivityGroup selectGroupStatus(String activityId);
}
