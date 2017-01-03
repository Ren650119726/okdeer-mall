package com.okdeer.mall.activity.discount.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.mall.activity.coupons.enums.CashDelivery;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountArea;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountCommunity;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountCondition;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountDto;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountQueryVo;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountRelationStore;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountStore;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountVo;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountStatus;
import com.okdeer.mall.activity.discount.enums.IdentityLimit;
import com.okdeer.mall.activity.discount.enums.LimitClientType;
import com.okdeer.mall.activity.discount.service.ActivityDiscountServiceApi;
import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.mall.common.enums.DistrictType;
import com.okdeer.mall.common.utils.RobotUserUtil;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountAreaMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountCommunityMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountConditionMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountRelationStoreMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountStoreMapper;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;

/**
 * 满减(满折)活动service实现类
 * @pr yscm
 * @desc 满减(满折)活动service实现类
 * @author zengj
 * @date 2016年1月26日 下午2:23:11
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1 			2016-07-22			zengj			查询店铺有效的满减
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.discount.service.ActivityDiscountServiceApi")
public class ActivityDiscountServiceImpl implements ActivityDiscountServiceApi, ActivityDiscountService {

	/**
	 * 满减(满折)DAO
	 */
	@Autowired
	private ActivityDiscountMapper activityDiscountMapper;

	/**
	 * 满减(满折)条件DAO
	 */
	@Autowired
	private ActivityDiscountConditionMapper activityDiscountConditionMapper;

	/**
	 * 满减（满折）区域信息mapper
	 */
	@Autowired
	private ActivityDiscountAreaMapper activityDiscountAreaMapper;

	/**
	 * 满减（满折）小区信息mapper
	 */
	@Autowired
	private ActivityDiscountCommunityMapper activityDiscountCommunityMapper;

	/**
	 * 满减（满折）店铺信息mapper
	 */
	@Autowired
	private ActivityDiscountStoreMapper activityDiscountStoreMapper;

	/**
	 * 满减（满折）活动的范围关联的店铺mapper
	 */
	@Autowired
	private ActivityDiscountRelationStoreMapper activityDiscountRelationStoreMapper;

	/**
	 * 店铺基本信息mapper
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoServiceApi;

	/**
	 * 
	 * @desc 添加活动
	 *
	 * @param activityDiscount 活动对象
	 * @param activityDiscountConditionList 活动条件集合
	 */
	@Override
	public void insertActivityDiscount(ActivityDiscount activityDiscount,
			List<ActivityDiscountCondition> activityDiscountConditionList) {
		String id = UuidUtils.getUuid();
		activityDiscount.setId(id);
		int sort = 0;
		activityDiscount.setAreaType(AreaType.store);
		// 添加活动信息
		activityDiscountMapper.insertActivityDiscount(activityDiscount);
		if (activityDiscountConditionList != null && !activityDiscountConditionList.isEmpty()) {
			for (ActivityDiscountCondition activityDiscountCondition : activityDiscountConditionList) {
				activityDiscountCondition.setDiscountId(id);
				activityDiscountCondition.setId(UuidUtils.getUuid());

				activityDiscountCondition.setSort(sort++);
			}
			// 添加活动条件信息
			activityDiscountConditionMapper.insertActivityDiscountCondition(activityDiscountConditionList);
		}
		ActivityDiscountStore activityDiscountStore = new ActivityDiscountStore();
		activityDiscountStore.setId(UuidUtils.getUuid());
		activityDiscountStore.setDiscountId(activityDiscount.getId());
		activityDiscountStore.setStoreId(activityDiscount.getStoreId());

		activityDiscountStoreMapper.insert(activityDiscountStore);

	}

	/**
	 * 
	 * @desc 修改活动
	 *
	 * @param activityDiscount 活动对象
	 * @param activityDiscountConditionList 活动条件集合
	 */
	@Override
	public void updateActivityDiscount(ActivityDiscount activityDiscount,
			List<ActivityDiscountCondition> activityDiscountConditionList) {
		activityDiscount.setStatus(ActivityDiscountStatus.noStart);
		activityDiscountMapper.updateActivityDiscount(activityDiscount);
		// 先删除该活动下的所有条件
		activityDiscountConditionMapper.deleteActivityDiscountCondition(activityDiscount.getId());
		if (activityDiscountConditionList != null && !activityDiscountConditionList.isEmpty()) {
			for (ActivityDiscountCondition condition : activityDiscountConditionList) {
				condition.setId(UuidUtils.getUuid());
				condition.setDiscountId(activityDiscount.getId());
			}
			// 添加活动条件
			activityDiscountConditionMapper.insertActivityDiscountCondition(activityDiscountConditionList);
		}
	}

	/**
	 * 
	 * @desc 根据活动名称查询活动信息
	 *
	 * @param storeId 店铺ID
	 * @param name 活动名称
	 * @return 活动信息
	 */
	public List<ActivityDiscount> selectByActivityName(String storeId, String name) {
		return activityDiscountMapper.selectByActivityName(storeId, name);
	}

	/**
	 * 
	 * @desc 查询同个店铺相同时间段的同一类型活动 
	 *
	 * @param activityDiscountVo 查询条件-活动对象
	 * @return 满足条件的对象集合,如没有满足的，返回空
	 */
	@Override
	public List<ActivityDiscount> selectActivityByTime(ActivityDiscountVo activityDiscountVo) {
		return activityDiscountMapper.selectActivityByTime(activityDiscountVo);
	}

	/**
	 * 
	 * @desc 店铺活动搜索列表 
	 *
	 * @param activityDiscountVo 查询条件-活动对象
	 * @param pageNumber 当前页
	 * @param pageSize 每页展示的记录数
	 * @return
	 */
	@Override
	public PageUtils<ActivityDiscount> searchByEntityParams(ActivityDiscountVo activityDiscountVo, int pageNumber,
			int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		PageUtils<ActivityDiscount> page = new PageUtils<ActivityDiscount>(
				activityDiscountMapper.searchByEntityParams(activityDiscountVo));
		return page;
	}

	/**
	 * 
	 * @desc 店铺活动搜索列表 
	 *
	 * @param activityDiscountVo 查询条件-活动对象
	 * @param pageNumber 当前页
	 * @param pageSize 每页展示的记录数
	 * @return
	 */
	@Override
	public PageUtils<ActivityDiscount> searchByMap(Map<String, Object> map, int pageNumber, int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		return new PageUtils<ActivityDiscount>(activityDiscountMapper.searchByMap(map));
	}

	/**
	 * 
	 * @desc 关闭活动 
	 *
	 * @param map
	 */
	@Override
	public void updateCloseActivityDiscount(Map<String, Object> map) {
		activityDiscountMapper.updateCloseActivityDiscount(map);
	}

	/**
	 * @desc 根据主键ID获取对象
	 *
	 * @param id 主键ID
	 * @return 活动信息
	 */
	public ActivityDiscount selectByPrimaryKey(String id) {
		return activityDiscountMapper.selectByPrimaryKey(id);
	}

	/**
	 * 
	 * @desc 根据活动ID查询活动条件 
	 *
	 * @param discountId 活动ID
	 * @return 活动条件集合
	 */
	public List<ActivityDiscountCondition> selectByDiscountId(String discountId) {
		return activityDiscountConditionMapper.selectByDiscountId(discountId);
	}

	/**
	 * @desc 根据查询条件，获取满减（满折）活动信息列表 
	 * @author wusw
	 * @param activityDiscountVo
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public PageUtils<ActivityDiscount> findByEntity(ActivityDiscountVo activityDiscountVo, int pageNumber, int pageSize)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true);
		activityDiscountVo.setStoreId("0");
		activityDiscountVo.setDisabled(Disabled.valid);
		List<ActivityDiscount> result = activityDiscountMapper.selectByEntity(activityDiscountVo);
		if (result == null) {
			result = new ArrayList<ActivityDiscount>();
		}
		return new PageUtils<ActivityDiscount>(result);
	}

	/**
	 * @desc 根据主键id，获取满减（满折）活动详细信息（包括关联信息） 
	 * 
	 * @author wusw
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public ActivityDiscountQueryVo getActivityDiscountQueryVoById(String id) throws ServiceException {
		ActivityDiscountQueryVo discountQueryVo = activityDiscountMapper.selectDiscountAssociateById(id);;
		List<ActivityDiscountCondition> conditionList = activityDiscountConditionMapper.selectByDiscountId(id);
		discountQueryVo.setDiscountConditionList(conditionList);
		return discountQueryVo;
	}

	/**
	 * @desc 新增满减（满折）活动
	 * 
	 * @author wusw
	 * @param activityDiscountVo
	 * @param currentOperateUserId
	 * @throws ServiceException
	 */
	@Override
	public void addActivityDiscount(ActivityDiscountVo activityDiscountVo, String currentOperateUserId)
			throws ServiceException {
		activityDiscountVo.setId(UuidUtils.getUuid());
		activityDiscountVo.setDisabled(Disabled.valid);
		activityDiscountVo.setStoreId("0");
		activityDiscountVo.setStatus(ActivityDiscountStatus.noStart);
		activityDiscountVo.setCreateUserId(currentOperateUserId);
		activityDiscountVo.setUpdateUserId(currentOperateUserId);
		Date date = new Date();
		activityDiscountVo.setCreateTime(date);
		activityDiscountVo.setUpdateTime(date);

		// 后面根据产品要求，页面删掉的字段
		activityDiscountVo.setIdentityLimit(IdentityLimit.no);
		activityDiscountVo.setLimitClientType(LimitClientType.no);
		activityDiscountVo.setIsCashDelivery(CashDelivery.no);

		activityDiscountMapper.insertActivityDiscount(activityDiscountVo);

		// 插入满减活动条件信息
		this.insertCondition(activityDiscountVo);

		// 插入满减活动与区域、小区、店铺关联信息，活动与范围下的店铺关联信息
		this.insertAreaInfo(activityDiscountVo);

	}

	/**
	 * @desc 修改满减（满折）活动
	 *
	 * @author wusw
	 * @param activityDiscountVo
	 * @param currentOperateUserId
	 * @throws ServiceException
	 */
	@Override
	public void updateActivityDiscount(ActivityDiscountVo activityDiscountVo, String currentOperateUserId)
			throws ServiceException {

		activityDiscountVo.setUpdateUserId(currentOperateUserId);
		activityDiscountVo.setUpdateTime(new Date());
		activityDiscountMapper.updateActivityDiscount(activityDiscountVo);

		// 先删除该活动下的所有条件
		activityDiscountConditionMapper.deleteActivityDiscountCondition(activityDiscountVo.getId());

		// 插入满减活动条件信息
		this.insertCondition(activityDiscountVo);

		// 删除活动与区域、小区、店铺关联信息
		activityDiscountAreaMapper.deleteByDiscountId(activityDiscountVo.getId());
		activityDiscountCommunityMapper.deleteByDiscountId(activityDiscountVo.getId());
		activityDiscountStoreMapper.deleteByDiscountId(activityDiscountVo.getId());
		// 删除活动与范围下的店铺关联信息
		activityDiscountRelationStoreMapper.deleteByDiscountId(activityDiscountVo.getId());

		// 插入满减活动与区域、小区、店铺关联的信息，活动与范围下的店铺关联信息
		this.insertAreaInfo(activityDiscountVo);

	}

	@Override
	public int selectCountClosedByIds(List<String> ids) throws ServiceException {
		int count = 0;
		if (ids != null && ids.size() > 0) {
			count = activityDiscountMapper.selectCountClosedByIds(Disabled.valid, ActivityDiscountStatus.closed, ids);
		}
		return count;
	}

	/**
	 * @desc 根据主键id，关闭活动（批量）
	 * 
	 * @author wusw
	 * @param ids
	 * @param currentOperateUserId
	 * @throws ServiceException
	 */
	@Override
	public void closeByDiscountId(List<String> ids, String currentOperateUserId) throws ServiceException {
		if (ids != null && ids.size() > 0) {
			activityDiscountMapper.closeByDiscountId(ids, ActivityDiscountStatus.closed, new Date(),
					currentOperateUserId);
		}
	}

	/**
	 * @desc 查询指定名称相同的数量
	 * 
	 * @author wusw
	 * @param activityDiscountVo
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public int selectCountByName(ActivityDiscountVo activityDiscountVo) throws ServiceException {

		return activityDiscountMapper.selectCountByName(activityDiscountVo);
	}

	/**
	 * @desc 查询与指定开始结束时间有交集、指定区域有交集的记录数量 
	 *
	 * @author wusw
	 * @param activityDiscountVo
	 * @param areaIdList 区域ID（省市ID）集合
	 * @param associateIdList 省下所有市和市所在省的id集合
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public int selectCountByDistrict(ActivityDiscountVo activityDiscountVo, List<String> areaIdList,
			List<String> associateIdList) throws ServiceException {

		return activityDiscountMapper.selectCountByDistrict(activityDiscountVo.getId(), "0", Disabled.valid,
				ActivityDiscountStatus.noStart, ActivityDiscountStatus.ing, activityDiscountVo.getAreaType(),
				activityDiscountVo.getStartTime(), activityDiscountVo.getEndTime(), areaIdList, associateIdList);
	}

	@Override
	public List<ActivityDiscount> findByStoreAndLimitType(ActivityDiscountStatus status, String storeId)
			throws ServiceException {

		return activityDiscountMapper.selectByStoreAndLimitType(status, storeId);
	}

	/**
	 * 
	 * 插入满减（满折）条件
	 *
	 * @author wusw
	 * @param activityDiscountVo
	 */
	private void insertCondition(ActivityDiscountVo activityDiscountVo) {
		if (activityDiscountVo.getArrive() != null && activityDiscountVo.getArrive().length > 0) {
			List<ActivityDiscountCondition> conditionList = new ArrayList<ActivityDiscountCondition>();
			for (int i = 0; i < activityDiscountVo.getArrive().length; i++) {
				ActivityDiscountCondition condition = new ActivityDiscountCondition();
				condition.setId(UuidUtils.getUuid());
				condition.setDiscountId(activityDiscountVo.getId());
				condition.setSort(i);
				condition.setArrive(activityDiscountVo.getArrive()[i]);
				condition.setDiscount(activityDiscountVo.getDiscount()[i]);
				conditionList.add(condition);
			}
			activityDiscountConditionMapper.insertActivityDiscountCondition(conditionList);
		}
	}

	/**
	 * 
	 * 插入满减活动与区域、小区、店铺关联信息，活动与范围下的店铺关联信息
	 *
	 * @author wusw
	 * @param activityDiscountVo
	 */
	private void insertAreaInfo(ActivityDiscountVo activityDiscountVo) {

		if (activityDiscountVo.getAreaType() == AreaType.area) {
			// 区域信息字符串格式：id-level,id-level,.....
			String areaIds = activityDiscountVo.getAreaIds();
			if (StringUtils.isNotEmpty(areaIds)) {
				String[] areaArr = areaIds.split(",");
				// 所有区域关联信息list
				List<ActivityDiscountArea> areaList = new ArrayList<ActivityDiscountArea>();
				// 所有区域下面的店铺信息list
				List<StoreInfo> allStoreInfoList = new ArrayList<StoreInfo>();
				ActivityDiscountArea activityDiscountArea = null;

				for (String areaIdType : areaArr) {
					String[] areaIdTypeArr = areaIdType.split("-");
					activityDiscountArea = new ActivityDiscountArea();
					activityDiscountArea.setId(UuidUtils.getUuid());
					activityDiscountArea.setDiscountId(activityDiscountVo.getId());
					activityDiscountArea.setAreaId(areaIdTypeArr[0]);

					List<StoreInfo> storeInfoList = null;
					// 根据level判断是省还是市
					if (areaIdTypeArr[1].equals("1")) {
						activityDiscountArea.setType(DistrictType.province);
						// 获取省下面所有的便利店
						storeInfoList = storeInfoServiceApi.selectCloudStoreByProvinceId(areaIdTypeArr[0]);
					} else {
						activityDiscountArea.setType(DistrictType.city);
						// 获取市下面所有的便利店
						storeInfoList = storeInfoServiceApi.selectCloudStoreByCityId(areaIdTypeArr[0]);
					}
					areaList.add(activityDiscountArea);
					// 将省市下面的所有便利店存放起来
					if (storeInfoList != null && storeInfoList.size() > 0) {
						allStoreInfoList.addAll(storeInfoList);
					}
				}
				activityDiscountAreaMapper.insertAreaBatch(areaList);
				// 批量插入满减（满折）活动的范围关联的店铺信息
				this.insertRelationStoreBatch(allStoreInfoList, activityDiscountVo.getId());
			}

		} else if (activityDiscountVo.getAreaType() == AreaType.community) {
			// 小区信息字符串格式：id,id,.....
			String areaIds = activityDiscountVo.getAreaIds();
			if (StringUtils.isNotEmpty(areaIds)) {
				// 所有小区关联list
				List<ActivityDiscountCommunity> areaList = new ArrayList<ActivityDiscountCommunity>();
				// 所有小区下面的店铺信息list
				List<StoreInfo> allStoreInfoList = new ArrayList<StoreInfo>();
				ActivityDiscountCommunity activityDiscountCommunity = null;
				String[] areaIdArr = areaIds.split(",");
				for (String areaId : areaIdArr) {
					activityDiscountCommunity = new ActivityDiscountCommunity();
					activityDiscountCommunity.setId(UuidUtils.getUuid());
					activityDiscountCommunity.setDiscountId(activityDiscountVo.getId());
					activityDiscountCommunity.setCommunityId(areaId);
					areaList.add(activityDiscountCommunity);

					// 获取指定小区下面所有的便利店list
					List<StoreInfo> storeInfoList = storeInfoServiceApi.selectCloudStoreByCommunityId(areaId);
					// 将省市下面的所有便利店存放起来
					if (storeInfoList != null && storeInfoList.size() > 0) {
						allStoreInfoList.addAll(storeInfoList);
					}
				}
				activityDiscountCommunityMapper.insertCommunityBatch(areaList);
				// 批量插入满减（满折）活动的范围关联的店铺信息
				this.insertRelationStoreBatch(allStoreInfoList, activityDiscountVo.getId());
			}
		} else if (activityDiscountVo.getAreaType() == AreaType.store) {
			// 店铺信息字符串格式：id,id,.....
			String areaIds = activityDiscountVo.getAreaIds();
			if (StringUtils.isNotEmpty(areaIds)) {
				// 所有的店铺关联list
				List<ActivityDiscountStore> areaList = new ArrayList<ActivityDiscountStore>();
				// 所有的店铺信息list
				List<StoreInfo> allStoreInfoList = new ArrayList<StoreInfo>();
				ActivityDiscountStore activityDiscountStore = null;
				String[] areaIdArr = areaIds.split(",");
				for (String areaId : areaIdArr) {
					activityDiscountStore = new ActivityDiscountStore();
					activityDiscountStore.setId(UuidUtils.getUuid());
					activityDiscountStore.setDiscountId(activityDiscountVo.getId());
					activityDiscountStore.setStoreId(areaId);
					areaList.add(activityDiscountStore);

					// 将所有的店铺信息存放起来
					StoreInfo storeInfo = new StoreInfo();
					storeInfo.setId(areaId);
					allStoreInfoList.add(storeInfo);
				}
				activityDiscountStoreMapper.insertStoreBatch(areaList);
				// 批量插入满减（满折）活动的范围关联的店铺信息
				this.insertRelationStoreBatch(allStoreInfoList, activityDiscountVo.getId());
			}
		}
	}

	/**
	 * 
	 * 批量插入满减（满折）活动的范围关联的店铺信息
	 * 
	 * @author wusw
	 * @param storeInfoList
	 * @param discountId
	 */
	private void insertRelationStoreBatch(List<StoreInfo> storeInfoList, String discountId) {
		List<ActivityDiscountRelationStore> relationStoreList = new ArrayList<ActivityDiscountRelationStore>();
		if (storeInfoList != null && storeInfoList.size() > 0) {
			for (StoreInfo storeInfo : storeInfoList) {
				ActivityDiscountRelationStore relationStore = new ActivityDiscountRelationStore();
				relationStore.setId(UuidUtils.getUuid());
				relationStore.setDiscountId(discountId);
				relationStore.setStoreId(storeInfo.getId());
				relationStoreList.add(relationStore);
			}
			activityDiscountRelationStoreMapper.insertRelationStoreBatch(relationStoreList);
		}
	}

	/**
	 * 查询需要修改状态的集合。然后批量更新
	 *
	 * @return
	 */
	public void updateStatus() {
		// 查询需要更新状态的集合
		List<ActivityDiscount> list = activityDiscountMapper.selectNeedUpdateList(DateUtils.getSysDate());
		if (list != null && !list.isEmpty()) {
			Map<String, Object> map = new HashMap<String, Object>();
			// 需要修改为进行中的活动ID集合
			List<String> ingList = new ArrayList<String>();
			// 需要修改为已结束的活动ID集合
			List<String> endList = new ArrayList<String>();

			for (ActivityDiscount activityDiscount : list) {
				if (ActivityDiscountStatus.ing.equals(activityDiscount.getStatus())) {
					ingList.add(activityDiscount.getId());
				} else if (ActivityDiscountStatus.end.equals(activityDiscount.getStatus())) {
					endList.add(activityDiscount.getId());
				}
			}

			map.put("updateTime", DateUtils.getSysDate());
			map.put("updateUserId", RobotUserUtil.getRobotUser().getId());
			// 如果有需要修改状态为进行中的活动
			if (!ingList.isEmpty()) {
				map.put("ids", ingList);
				map.put("status", ActivityDiscountStatus.ing);

				activityDiscountMapper.updateStatus(map);
			}
			// 如果有需要修改状态为已结束的活动
			if (!endList.isEmpty()) {
				map.put("ids", endList);
				map.put("status", ActivityDiscountStatus.end);

				activityDiscountMapper.updateStatus(map);
			}

		}
	}

	@Override
	public List<ActivityDiscountDto> selectByStoreId(String storeId, Date currentDate) throws ServiceException {
		return activityDiscountMapper.selectByStoreId(storeId, currentDate);
	}

	@Override
	public List<ActivityDiscount> findActivityIndustrys(String storeId) {
		return activityDiscountMapper.findActivityIndustrys(storeId);
	}

	@Override
	public List<ActivityDiscount> findDtoByStoreId(String storeId) throws ServiceException {
		return activityDiscountMapper.selectDtoByStoreId(storeId);
	}

	@Override
	public String getDiscountConditionsId(String discountId) {
		return activityDiscountMapper.getDiscountConditionsId(discountId);
	}

	@Override
	public ActivityDiscountCondition getDiscountConditions(Map<String, String> map) {
		return activityDiscountMapper.getDiscountConditions(map);
	}

	@Override
	public List<ActivityDiscountCondition> selectByStoreReduce(ActivityDiscountStatus status, String storeId) {
		List<ActivityDiscountCondition> activity = new ArrayList<ActivityDiscountCondition>();
		activity = activityDiscountMapper.selectByStoreReduce(status, storeId);
		return activity;
	}

	@Override
	public List<ActivityDiscountCondition> selectByStoreDiscount(Map<String, String> map) {
		List<ActivityDiscountCondition> activity = new ArrayList<ActivityDiscountCondition>();
		activity = activityDiscountMapper.selectByStoreDiscount(map);
		return activity;
	}

	@Override
	public ActivityDiscountCondition getReduceConditions(Map<String, String> map) {
		ActivityDiscountCondition condition = new ActivityDiscountCondition();
		condition = activityDiscountMapper.getReduceConditions(map);
		return condition;
	}

	@Override
	public ActivityDiscount getById(String id) throws ServiceException {
		return activityDiscountMapper.selectRelaveOrderById(id);
	}

	@Override
	public List<ActivityDiscountCondition> selectByStoreReduceOff(ActivityDiscountStatus status, String storeId)
			throws Exception {
		return activityDiscountMapper.selectByStoreReduceOff(status, storeId);
	}

	@Override
	public List<ActivityDiscount> findDtoByStoreIdForNoCloud(String storeId) throws ServiceException {

		return activityDiscountMapper.selectDtoByStoreIdForNoCloud(storeId);
	}

	// begin 重构4.1 added by zhangkn
	/**
	 * @Description: 查询店铺的满减满折列表 (用户app首页用)
	 * @param map 查询参数
	 * @return 满件满折列表
	 * @author zhangkn
	 * @date 2016年7月18日
	 */
	@Override
	public List<ActivityDiscount> findActivityDiscountForUserApp(Map<String, Object> map) {
		return activityDiscountMapper.findActivityDiscountForUserApp(map);
	}
	// end 重构4.1 added by zhangkn

	// Begin 重构4.1 add by zengj
	/**
	 * 
	 * @Description: 查询店铺的满减满折活动和条件
	 * @param params 查询参数
	 * @return List 
	 * @author zengj
	 * @date 2016年7月22日
	 */
	public List<Map<String, Object>> findActivityDiscountByStoreId(Map<String, Object> params) {
		return activityDiscountMapper.findActivityDiscountByStoreId(params);
	}
	// End 重构4.1 add by zengj
}
