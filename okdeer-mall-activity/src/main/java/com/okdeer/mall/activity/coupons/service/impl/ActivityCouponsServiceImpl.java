
package com.okdeer.mall.activity.coupons.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.goods.base.entity.GoodsNavigateCategory;
import com.okdeer.archive.goods.base.entity.GoodsSpuCategory;
import com.okdeer.archive.store.entity.StoreAgentCommunity;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.archive.store.service.StoreAgentCommunityServiceApi;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.bdp.address.entity.Address;
import com.okdeer.bdp.address.service.IAddressService;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsArea;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsCategory;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsCommunity;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsLimitCategory;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRelationStore;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsStore;
import com.okdeer.mall.activity.coupons.entity.CouponsInfoParams;
import com.okdeer.mall.activity.coupons.entity.CouponsInfoQuery;
import com.okdeer.mall.activity.coupons.enums.CouponsType;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsCategoryMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsLimitCategoryMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRelationStoreMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsService;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsServiceApi;
import com.okdeer.mall.common.entity.AreaScTreeVo;
import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.mall.common.enums.DistrictType;

/**
 * 代金券管理实现类
 * @project yschome-mall
 * @author zhulq
 * @date 2016年1月21日 下午3:22:25
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivityCouponsServiceApi")
public class ActivityCouponsServiceImpl implements ActivityCouponsServiceApi, ActivityCouponsService {

	/**
	 * 注入代金券管理mapper
	 */
	@Autowired
	private ActivityCouponsMapper activityCouponsMapper;

	/**
	 * 注入代金券分类mapper
	 */
	@Autowired
	private ActivityCouponsCategoryMapper activityCouponsCategoryMapper;

	/**
	 * 店铺信息serviceApi注入
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoServiceApi;

	/**
	 * 代金券关联店铺Mapper注入
	 */
	@Autowired
	private ActivityCouponsRelationStoreMapper couponsRelationStoreMapper;

	/**
	 * 代金券关联限制类目Mapper注入
	 */
	@Autowired
	private ActivityCouponsLimitCategoryMapper activityCouponsLimitCategoryMapper;

	/**
	 * 导入店铺关联小区的信息
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreAgentCommunityServiceApi storeAgentCommunityServiceApi;

	/**
	 * 地址service
	 */
	@Reference(version = "1.0.0", check = false)
	private IAddressService addressService;

	@Override
	public List<AreaScTreeVo> getStoreInfoByCityId(StoreInfo parStoreInfo) {
		return activityCouponsMapper.selectStoreInfoByCityId(parStoreInfo);
	}

	@Override
	public PageUtils<CouponsInfoQuery> getCouponsInfo(CouponsInfoParams couponsInfoParams, int pageNum, int pageSize)
			throws ServiceException {
		PageHelper.startPage(pageNum, pageSize, true);
		couponsInfoParams.setDisabled(Disabled.valid);
		List<CouponsInfoQuery> couponsInfos = activityCouponsMapper.selectCoupons(couponsInfoParams);
		if (couponsInfos == null) {
			couponsInfos = new ArrayList<CouponsInfoQuery>();
		}

		// 转义字段
		for (CouponsInfoQuery c : couponsInfos) {
			if (c.getType() != null) {
				c.setTypeName(CouponsType.getName(c.getType()));
			}
		}

		PageUtils<CouponsInfoQuery> pageUtils = new PageUtils<CouponsInfoQuery>(couponsInfos);
		return pageUtils;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addCoupons(ActivityCoupons coupons) throws ServiceException {
		// 0：便利店和服务店，1：便利店 2 服务店 3话费充值
		if (coupons.getType() == CouponsType.bldfwd.getValue()) {
			activityCouponsMapper.insert(coupons);
		} else if (coupons.getType() == CouponsType.hfcz.getValue()) {
			activityCouponsMapper.insert(coupons);
		} else if (coupons.getType() == CouponsType.bld.getValue()) {
			activityCouponsMapper.insert(coupons);
			this.addRelatedInfo(coupons);
		} else if (coupons.getType() == CouponsType.fwd.getValue()) {
			activityCouponsMapper.insert(coupons);
			this.addRelatedInfo(coupons);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addCouponsLimitCategory(List<ActivityCouponsLimitCategory> activityCouponsLimitCategoryList)
			throws ServiceException {
		activityCouponsMapper.insertCouponsLimitCategory(activityCouponsLimitCategoryList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addCouponsArea(List<ActivityCouponsArea> activityCouponsAreaList) throws ServiceException {
		activityCouponsMapper.insertCouponsArea(activityCouponsAreaList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addCouponsCommunity(List<ActivityCouponsCommunity> activityCouponsCommunityList)
			throws ServiceException {
		activityCouponsMapper.insertCouponsCommunity(activityCouponsCommunityList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addCouponsStore(List<ActivityCouponsStore> activityCouponsStoreList) throws ServiceException {
		activityCouponsMapper.insertCouponsStore(activityCouponsStoreList);
	}

	@Override
	public List<GoodsSpuCategory> findSpuCategoryList(Map<String, Object> map) {
		List<String> firstCagegoryList = activityCouponsMapper.findFwdFirstSpuCategoryList(map);
		if(CollectionUtils.isNotEmpty(firstCagegoryList)){
			StringBuffer sb = new StringBuffer();
			for(String categoryId : firstCagegoryList){
				if(!"".equals(sb.toString())){
					sb.append(" or ");
				}
				sb.append(" g.level_id like '" + categoryId + "|%' ");
			}
			map.put("firstCagegoryList", " ( " + sb.toString() + " ) ");
			return activityCouponsMapper.findSpuCategoryList(map);
		}
		return new ArrayList<GoodsSpuCategory>();
	}

	@Override
	public List<GoodsNavigateCategory> findNavigateCategoryList(Map<String, Object> map) {
		return activityCouponsMapper.findNavigateCategoryList(map);
	}

	@Override
	public CouponsInfoQuery getCouponsInfoById(String id) throws ServiceException {
		return activityCouponsMapper.selectCouponsById(id);
	}

	@Override
	public int getByParame(CouponsInfoQuery coupons) throws ServiceException {
		coupons.setDisabled(Disabled.valid);
		CouponsInfoQuery info = activityCouponsMapper.selectByParams(coupons);
		if (info != null) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateCoupons(CouponsInfoQuery coupons) throws ServiceException {
		// 0：便利店和服务店，1：便利店 2 服务店 3 充值
		if (coupons.getType() == CouponsType.bldfwd.getValue() || coupons.getType() == CouponsType.hfcz.getValue()) {
			activityCouponsMapper.updateCoupons(coupons);
		} else if (coupons.getType() == CouponsType.bld.getValue() || coupons.getType() == CouponsType.fwd.getValue()) {
			activityCouponsMapper.updateCoupons(coupons);

			// 删掉老数据
			this.deleteCouponsArea(coupons.getId());
			this.deleteCouponsStroe(coupons.getId());
			this.deleteCouponsRelationStroe(coupons.getId());
			activityCouponsCategoryMapper.deleteByCouponsId(coupons.getId());

			// 批量插入新数据 (由于新增和修改 接收的对象用的不是同一个,只能重新转换一下)
			ActivityCoupons activitycoupons = new ActivityCoupons();
			activitycoupons.setId(coupons.getId());
			activitycoupons.setAreaIds(coupons.getAreaIds());
			activitycoupons.setAreaType(coupons.getAreaType());
			activitycoupons.setCategoryLimitIds(coupons.getCategoryLimitIds());
			activitycoupons.setType(coupons.getType());
			activitycoupons.setCategoryIds(coupons.getCategoryIds());
			this.addRelatedInfo(activitycoupons);
		}
	}

	@Override
	public List<ActivityCoupons> getCouponsInfoByName(ActivityCoupons coupons) throws ServiceException {
		coupons.setDisabled(Disabled.valid);
		return activityCouponsMapper.selectCouponsByName(coupons);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteByIds(String id) throws ServiceException {
		/* int category = couponsManageService.getCategory(id); */
		int area = this.getArea(id);
		int community = this.getCommunity(id);
		int store = this.getStore(id);
		int relationStores = this.getCouponsRelationStroe(id);
		/*
		 * if (category > 0) {
		 * couponsManageService.deleteCouponsLimitCategory(id); }
		 */
		if (area > 0) {
			this.deleteCouponsArea(id);
		}
		if (community > 0) {
			this.deleteCouponsCommunity(id);
		}
		if (store > 0) {
			this.deleteCouponsStroe(id);
		}
		if (relationStores > 0) {
			this.deleteCouponsRelationStroe(id);
		}
		activityCouponsMapper.deleteByIds(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteCouponsLimitCategory(String id) throws ServiceException {
		activityCouponsMapper.deleteCouponsLimitCategory(id);

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteCouponsArea(String id) throws ServiceException {
		activityCouponsMapper.deleteCouponsArea(id);

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteCouponsCommunity(String id) throws ServiceException {
		activityCouponsMapper.deleteCouponsCommunity(id);

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteCouponsStroe(String id) throws ServiceException {
		activityCouponsMapper.deleteCouponsStore(id);

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addRelatedInfo(ActivityCoupons coupons) throws ServiceException {
		// String[] categoryLimitIds = null;
		String[] areas1 = null;
		String[] areas = null;
		String[] communitys = null;
		String[] stores = null;
		List<StoreInfo> storeInfoList = new ArrayList<>();
		if (coupons.getAreaType() == AreaType.area) {
			ArrayList<ActivityCouponsArea> areaList = new ArrayList<>();
			if (StringUtils.isNotEmpty(coupons.getAreaIds())) {
				areas1 = coupons.getAreaIds().split(",");
				if (areas1 != null && areas1.length > 0) {
					for (int i = 0; i < areas1.length; i++) {
						ActivityCouponsArea activityCouponsArea = new ActivityCouponsArea();
						List<StoreInfo> storeInfos = new ArrayList<>();
						areas = areas1[i].split("-");
						if ("0".equals(areas[1])) {
							activityCouponsArea.setCouponsAreaType(DistrictType.city);
							Map<String, Object> params = new HashMap<>();
							params.put("disabled", Disabled.valid);
							params.put("cityId", areas[0]);
							// 对应枚举 CouponsType 0：便利店和服务店，1：便利店 2 服务店 3 充值
							if (coupons.getType() == 1) {
								// 查询便利店店铺
								params.put("type", StoreTypeEnum.CLOUD_STORE);
							} else if (coupons.getType() == 2) {
								// 查询服务店店铺
								params.put("type", StoreTypeEnum.SERVICE_STORE);
							}
							storeInfos = storeInfoServiceApi.getByProvinceIdAndCityId(params);
							storeInfoList.addAll(storeInfos);
						} else if ("1".equals(areas[1])) {
							activityCouponsArea.setCouponsAreaType(DistrictType.province);
							Map<String, Object> params = new HashMap<>();
							params.put("disabled", Disabled.valid);
							params.put("provinceId", areas[0]);
							// 对应枚举 CouponsType 0：便利店和服务店，1：便利店 2 服务店 3 充值
							if (coupons.getType() == 1) {
								// 查询便利店店铺
								params.put("type", StoreTypeEnum.CLOUD_STORE);
							} else if (coupons.getType() == 2) {
								// 查询服务店店铺
								params.put("type", StoreTypeEnum.SERVICE_STORE);
							}
							storeInfos = storeInfoServiceApi.getByProvinceIdAndCityId(params);
							storeInfoList.addAll(storeInfos);
						}
						activityCouponsArea.setAreaId(areas[0]);
						activityCouponsArea.setCouponsId(coupons.getId());
						activityCouponsArea.setId(UuidUtils.getUuid());
						areaList.add(activityCouponsArea);
					}
					this.addCouponsArea(areaList);
				}
			}
		} else if (coupons.getAreaType() == AreaType.community) {
			ArrayList<ActivityCouponsCommunity> communityList = new ArrayList<>();
			if (coupons.getAreaIds() != null && coupons.getAreaIds() != "") {
				communitys = coupons.getAreaIds().split(",");
				if (communitys != null && communitys.length > 0) {
					for (int i = 0; i < communitys.length; i++) {
						ActivityCouponsCommunity activityCouponsCommunity = new ActivityCouponsCommunity();
						activityCouponsCommunity.setCommunityId(communitys[i]);
						activityCouponsCommunity.setCouponsId(coupons.getId());
						activityCouponsCommunity.setId(UuidUtils.getUuid());
						communityList.add(activityCouponsCommunity);
						List<StoreAgentCommunity> storeAgentCommunityList = storeAgentCommunityServiceApi
								.getAgentCommunitysByCommunityId(communitys[i]);
						if (storeAgentCommunityList != null && storeAgentCommunityList.size() > 0) {
							for (StoreAgentCommunity storeAgentCommunity : storeAgentCommunityList) {
								StoreInfo storeInfo = new StoreInfo();
								storeInfo.setId(storeAgentCommunity.getStoreId());
								storeInfoList.add(storeInfo);
							}
						}
					}
					this.addCouponsCommunity(communityList);
				}
			}

		} else if (coupons.getAreaType() == AreaType.store) {
			ArrayList<ActivityCouponsStore> storeList = new ArrayList<>();
			if (coupons.getAreaIds() != null && coupons.getAreaIds() != "") {
				stores = coupons.getAreaIds().split(",");
				if (stores != null && stores.length > 0) {
					for (int i = 0; i < stores.length; i++) {
						StoreInfo StoreInfo = new StoreInfo();
						ActivityCouponsStore activityCouponsStore = new ActivityCouponsStore();
						activityCouponsStore.setCouponsId(coupons.getId());
						activityCouponsStore.setStoreId(stores[i]);
						activityCouponsStore.setId(UuidUtils.getUuid());
						storeList.add(activityCouponsStore);
						StoreInfo.setId(stores[i]);
						storeInfoList.add(StoreInfo);
					}
					this.addCouponsStore(storeList);
				}
			}
		}
		if (storeInfoList.size() > 0) {
			this.addRelationStore(storeInfoList, coupons);
		}

		// 代金券关联的分类 1 导航分类 2 服务店店铺商品分类
		int type = 0;
		if (coupons.getType() == CouponsType.bld.getValue()) {
			type = 1;
		} else if (coupons.getType() == CouponsType.fwd.getValue()) {
			type = 2;
		}
		if (StringUtils.isNotEmpty(coupons.getCategoryIds())) {
			String[] categoryArray = coupons.getCategoryIds().split(",");
			List<ActivityCouponsCategory> accList = new ArrayList<ActivityCouponsCategory>();
			for (String s : categoryArray) {
				ActivityCouponsCategory acc = new ActivityCouponsCategory();
				acc.setId(UuidUtils.getUuid());
				acc.setCategoryId(s);
				acc.setCouponId(coupons.getId());
				acc.setType(type);
				accList.add(acc);
			}
			activityCouponsCategoryMapper.saveBatch(accList);
		}
	}

	@Override
	public int getCategory(String id) throws ServiceException {
		return activityCouponsMapper.selectCouponsLimitCategory(id);
	}

	@Override
	public int getArea(String id) throws ServiceException {
		return activityCouponsMapper.selectCouponsArea(id);
	}

	@Override
	public int getCommunity(String id) throws ServiceException {
		return activityCouponsMapper.selectCouponsCommunity(id);
	}

	@Override
	public int getStore(String id) throws ServiceException {
		return activityCouponsMapper.selectCouponsStore(id);
	}

	@Override
	public List<ActivityCoupons> listByActivityId(String activityId, String belongType) throws ServiceException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("activityId", activityId);
		map.put("belongType", belongType);
		return activityCouponsMapper.listByActivityId(map);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addRelationStore(List<StoreInfo> StoreInfoList, ActivityCoupons coupons) throws ServiceException {
		List<ActivityCouponsRelationStore> couponsRelationStoreList = new ArrayList<>();
		if (StoreInfoList.size() > 0) {
			for (StoreInfo storeInfo : StoreInfoList) {
				ActivityCouponsRelationStore couponsRelationStore = new ActivityCouponsRelationStore();
				String id = UuidUtils.getUuid();
				couponsRelationStore.setCouponsId(coupons.getId());
				couponsRelationStore.setStoreId(storeInfo.getId());
				couponsRelationStore.setId(id);
				couponsRelationStoreList.add(couponsRelationStore);
			}
			couponsRelationStoreMapper.insertCouponsRelationStore(couponsRelationStoreList);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteCouponsRelationStroe(String id) throws ServiceException {
		couponsRelationStoreMapper.deleteCouponsRelationStore(id);

	}

	@Override
	public int getCouponsRelationStroe(String id) throws ServiceException {
		return couponsRelationStoreMapper.selectCouponsRelationStore(id);
	}

	@Override
	public List<ActivityCouponsLimitCategory> getCouponsLimitCategoryByCouponsId(String couponsId)
			throws ServiceException {
		return activityCouponsLimitCategoryMapper.selectByCouponsId(couponsId);
	}

	@Override
	public ActivityCoupons getById(String id) throws ServiceException {

		return activityCouponsMapper.selectByPrimaryKey(id);
	}

	@Override
	public Map<String, List<Map<String, Object>>> findCityRelationStoreByCouponsProvince(String couponsId,
			String provinceId) throws ServiceException {
		// 存放市及下面的店铺信息，最外层map的key是市名称，map的value是小店铺名称和店铺地址的集合list
		Map<String, List<Map<String, Object>>> resultMap = new HashMap<String, List<Map<String, Object>>>();

		// 根据省份id、代金券id，查询出市id、店铺名称和店铺名称
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("couponsId", couponsId);
		params.put("provinceId", provinceId);
		List<Map<String, Object>> cityStoreList = couponsRelationStoreMapper.selectAddressRelationStoreByParams(params);
		if (cityStoreList != null && cityStoreList.size() > 0) {
			for (Map<String, Object> map : cityStoreList) {
				if (map != null && map.get("storeId") != null && !"".equals(map.get("storeId").toString())) {
					// 根据市id，获取市信息，并将同一市下面的店铺信息存放在一个list中，且保证市名称唯一
					Address addressCity = addressService.getAddressById(Long.valueOf(map.get("cityId").toString()));
					List<Map<String, Object>> storeList = new ArrayList<Map<String, Object>>();
					if (resultMap.containsKey(addressCity.getName())) {
						storeList = resultMap.get(addressCity.getName());
					}
					// 当前查询的店铺名称和地址存放在店铺list中
					Map<String, Object> storeInfo = new HashMap<String, Object>();
					storeInfo.put("storeName", (map.get("storeName") == null ? "" : map.get("storeName")));
					storeInfo.put("storeAddress", (map.get("storeAddress") == null ? "" : map.get("storeAddress")));
					storeList.add(storeInfo);

					resultMap.put(addressCity.getName(), storeList);
				}
			}
		}
		return resultMap;
	}

	@Override
	public Map<String, Object> findCouponsProvinceByCouponsId(String couponsId) throws ServiceException {
		// 存放省信息,key是省id，value是省名称
		// 根据代金券的区域类型 查询省的名称
		Map<String, Object> provinceMap = new HashMap<String, Object>();
		ActivityCoupons activityCoupons = activityCouponsMapper.selectByPrimaryKey(couponsId);
		if (activityCoupons != null) {
			if (activityCoupons.getAreaType() == AreaType.national) {
				provinceMap.put("0", "全国");
			} else if (activityCoupons.getAreaType() == AreaType.store) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("couponsId", couponsId);
				// 根据代金券id，查询省份id
				List<Map<String, Object>> provinceIdList = couponsRelationStoreMapper
						.selectAddressRelationProvinceByParams(params);
				if (provinceIdList != null && provinceIdList.size() > 0) {
					// 循环省份id，查询出省名称，并且确定省名称唯一
					for (Map<String, Object> map : provinceIdList) {
						if (map != null && map.get("provinceId") != null
								&& !"".equals(map.get("provinceId").toString())) {
							Address addressProvince = addressService
									.getAddressById(Long.valueOf(map.get("provinceId").toString()));
							if (addressProvince != null
									&& !provinceMap.containsKey(addressProvince.getId().toString())) {
								provinceMap.put(addressProvince.getId().toString(), addressProvince.getName());
							}
						}

					}
				}
			} else if (activityCoupons.getAreaType() == AreaType.area) {
				CouponsInfoQuery couponsInfo = null;
				couponsInfo = activityCouponsMapper.selectCouponsById(couponsId);
				List<ActivityCouponsArea> areaList = couponsInfo.getActivityCouponsAreaList();
				if (CollectionUtils.isNotEmpty(areaList)) {
					for (ActivityCouponsArea area : areaList) {
						Address addressInfo = null;
						if (area.getCouponsAreaType() == DistrictType.city) {
							addressInfo = addressService.getAddressById(Long.valueOf(area.getAreaId().toString()));
							if (addressInfo != null && !provinceMap.containsKey(addressInfo.getParentId().toString())) {
								provinceMap.put(addressInfo.getParentId().toString(), addressInfo.getParentName());
							}
						} else if (area.getCouponsAreaType() == DistrictType.province) {
							addressInfo = addressService.getAddressById(Long.valueOf(area.getAreaId().toString()));
							if (addressInfo != null && !provinceMap.containsKey(addressInfo.getId().toString())) {
								provinceMap.put(addressInfo.getId().toString(), addressInfo.getName());
							}
						}
					}

				}
			}
		}
		return provinceMap;
	}

	/**
	 * 获取最近一个进行中的注册活动的代金券活动关联的代金券集合
	 * @param map 有以下key
	 * type 0代金券领取活动，1注册活动
	 */
	public List<ActivityCoupons> listCouponsByType(Map<String, Object> map) throws ServiceException {
		return activityCouponsMapper.listCouponsByType(map);
	}

	@Override
	public List<ActivityCoupons> getActivityCoupons(String activityId) {
		return activityCouponsMapper.getActivityCoupons(activityId);
	}

	@Override
	public Boolean findByIds(List<String> ids) throws ServiceException {
		int cou = activityCouponsMapper.selectByIds(ids);
		if (cou < ids.size()) {
			return false;
		} else {
			return true;
		}

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateActivityCouponsUsedNum(String activityItemId) {
		activityCouponsMapper.updateActivityCouponsUsedNum(activityItemId);

	}

	@Override
	public ActivityCoupons selectByPrimaryKey(String id) {
		return activityCouponsMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<ActivityCouponsCategory> findActivityCouponsCategoryByCouponsId(String couponId, Integer type) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("couponId", couponId);
		map.put("type", type);
		return activityCouponsCategoryMapper.findActivityCouponsCategoryByCouponsId(map);
	}

	@Override
	public Map<String, Object> findRelationCityByCouponsId(String couponsId, String provinceId)
			throws ServiceException {
		Map<String, Object> cityMap = new HashMap<String, Object>();
		CouponsInfoQuery couponsInfo = null;
		couponsInfo = activityCouponsMapper.selectCouponsById(couponsId);
		List<ActivityCouponsArea> areaList = couponsInfo.getActivityCouponsAreaList();
		if (CollectionUtils.isNotEmpty(areaList)) {
			for (ActivityCouponsArea area : areaList) {
				// List<String> cityList = null;
				Address addressInfo = null;
				if (area.getCouponsAreaType() == DistrictType.city) {
					addressInfo = addressService.getAddressById(Long.valueOf(area.getAreaId().toString()));
					if (addressInfo != null) {
						if (addressInfo.getParentId().toString().equals(provinceId)) {
							if (addressInfo != null && !cityMap.containsKey(addressInfo.getId().toString())) {
								cityMap.put(addressInfo.getId().toString(), addressInfo.getName());
							}
						}
					}
				} /*
					 * else if (area.getCouponsAreaType() ==
					 * DistrictType.province) { cityMap.put("0", "所属范围内都可以用"); }
					 */
			}

		}
		return cityMap;
	}

	@Transactional(readOnly=true)
	@Override
	public ActivityCoupons selectByActivityId(String id) throws Exception {
		ActivityCoupons result = activityCouponsMapper.selectByActivityId(id);
		return result;
	}
}
