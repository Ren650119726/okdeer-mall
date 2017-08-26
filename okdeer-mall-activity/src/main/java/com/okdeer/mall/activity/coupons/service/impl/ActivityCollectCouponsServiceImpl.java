
package com.okdeer.mall.activity.coupons.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.okdeer.archive.system.entity.PsmsAgent;
import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.archive.system.entity.SysUser;
import com.okdeer.archive.system.service.IPsmsAgentServiceApi;
import com.okdeer.archive.system.service.ISysUserServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectArea;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCommunity;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsOrderVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsRecordVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsSimpleVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectOrderType;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectStore;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectXffqRelation;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsCategory;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.enums.ActivityCollectCouponsApprovalStatus;
import com.okdeer.mall.activity.coupons.enums.ActivityCollectCouponsStatus;
import com.okdeer.mall.activity.coupons.enums.ActivityCollectCouponsType;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsType;
import com.okdeer.mall.activity.coupons.mapper.ActivityCollectAreaMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCollectCommunityMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCollectCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCollectOrderTypeMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCollectStoreMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCollectXffqRelationMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsOrderRecordMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordBeforeMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsService;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsServiceApi;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsService;
import com.okdeer.mall.activity.dto.ActivityCouponsRecordBeforeParamDto;
import com.okdeer.mall.activity.dto.ActivityCouponsRecordQueryParamDto;
import com.okdeer.mall.activity.dto.TakeActivityCouponParamDto;
import com.okdeer.mall.activity.dto.TakeActivityCouponResultDto;
import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.mall.system.mapper.SysBuyerUserMapper;
import com.okdeer.mall.system.mapper.SysUserMapper;
import com.okdeer.mcm.entity.SmsVO;
import com.okdeer.mcm.service.ISmsService;

/**
 * 
 * ClassName: ActivityCollectCouponsServiceImpl 
 * @Description: 活动管理实现类
 * @author zengjizu
 * @date 2016年12月16日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *    v1.3.0            2016-12-16           zengjz          开门领取代金券增加小区参数   
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsServiceApi")
public class ActivityCollectCouponsServiceImpl
		implements ActivityCollectCouponsService, ActivityCollectCouponsServiceApi {

	private static final Logger log = Logger.getLogger(ActivityCollectCouponsServiceImpl.class);

	@Autowired
	private ActivityCollectCouponsMapper activityCollectCouponsMapper;

	@Autowired
	private ActivityCollectAreaMapper activityCollectAreaMapper;

	@Autowired
	private ActivityCollectCommunityMapper activityCollectCommunityMapper;

	@Autowired
	private ActivityCollectStoreMapper activityCollectStoreMapper;

	@Autowired
	private ActivityCouponsMapper activityCouponsMapper;

	@Autowired
	private ActivityCouponsService activityCouponsService;

	@Autowired
	private ActivityCollectOrderTypeMapper activityCollectOrderTypeMapper;

	@Autowired
	private ActivityCollectXffqRelationMapper activityCollectXffqRelationMapper;

	@Resource
	private ActivityCouponsOrderRecordMapper activityCouponsOrderRecordMapper;

	@Value("${mcm.sys.code}")
	private String msgSysCode;

	@Value("${mcm.sys.token}")
	private String msgToken;

	@Autowired
	private SysUserMapper sysUserMapper;

	@Autowired
	private SysBuyerUserMapper buyerUserMapper;

	/**
	 * 短信接口
	 */
	@Reference(version = "1.0.0", check = false)
	ISmsService smsService;

	/**
	 * 代金券领取记录mapper
	 */
	@Autowired
	private ActivityCouponsRecordMapper activityCouponsRecordMapper;

	/**
	 * 代金券预录取记录mapper
	 */
	@Autowired
	private ActivityCouponsRecordBeforeMapper activityCouponsRecordBeforeMapper;

	/**
	 * userservice
	 */
	@Reference(version = "1.0.0", check = false)
	ISysUserServiceApi iSysUserServiceApi;

	/**
	 * userservice
	 */
	@Reference(version = "1.0.0", check = false)
	IPsmsAgentServiceApi iPsmsAgentServiceApi;

	@Transactional(rollbackFor = Exception.class)
	public void save(ActivityCollectCoupons activityCollectCoupons) {
		activityCollectCouponsMapper.save(activityCollectCoupons);
	}

	@Transactional(rollbackFor = Exception.class)
	public void save(ActivityCollectCoupons activityCollectCoupons, List<String> couponsIds, String areaIds,
			List<ActivityCollectXffqRelation> xffqRelationList) throws Exception {
		// 先保存活动主对象
		activityCollectCouponsMapper.save(activityCollectCoupons);

		// 保存关联的代金券,把activityId改为当前对象的id
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("couponsIds", couponsIds);
		map.put("activityId", activityCollectCoupons.getId());
		activityCouponsMapper.updateBatchActivityId(map);

		// 代金卷范围类型：0全国，1区域，2小区 , 3店铺
		// 如果是区域
		if (ActivityCollectCouponsType.OPEN_DOOR.getValue().equals(activityCollectCoupons.getType())
				|| ActivityCollectCouponsType.consume_return.getValue().equals(activityCollectCoupons.getType())
				|| ActivityCollectCouponsType.get.getValue().equals(activityCollectCoupons.getType())
				|| ActivityCollectCouponsType.lzg.getValue().equals(activityCollectCoupons.getType())
				|| ActivityCollectCouponsType.red_packet.getValue().equals(activityCollectCoupons.getType())) {

			// 如果是消费返券,要批量插入梯度表
			if (ActivityCollectCouponsType.consume_return.getValue().equals(activityCollectCoupons.getType())) {
				activityCollectXffqRelationMapper.saveBatch(xffqRelationList);
			}

			if (activityCollectCoupons.getAreaType().intValue() == AreaType.area.ordinal()) {
				// 批量添加新记录
				String[] array = areaIds.split(",");

				List<ActivityCollectArea> areaList = new ArrayList<ActivityCollectArea>();
				for (String str : array) {
					ActivityCollectArea a = new ActivityCollectArea();
					a.setId(UuidUtils.getUuid());
					a.setCollectCouponsId(activityCollectCoupons.getId());
					a.setType(Integer.parseInt(str.split("-")[1]));
					a.setAreaId(str.split("-")[0]);
					areaList.add(a);
				}
				activityCollectAreaMapper.saveBatch(areaList);
			}

			// 如果是小区
			if (activityCollectCoupons.getAreaType() == AreaType.community.ordinal()) {
				// 批量添加新记录
				String[] array = areaIds.split(",");
				List<ActivityCollectCommunity> areaList = new ArrayList<ActivityCollectCommunity>();
				for (String str : array) {
					ActivityCollectCommunity a = new ActivityCollectCommunity();
					a.setId(UuidUtils.getUuid());
					a.setCollectCouponsId(activityCollectCoupons.getId());
					a.setCommunityId(str);
					areaList.add(a);
				}
				activityCollectCommunityMapper.saveBatch(areaList);
			}

			// 如果是小区
			if (activityCollectCoupons.getAreaType() == AreaType.store.ordinal()) {
				// 批量添加新记录
				String[] array = areaIds.split(",");
				List<ActivityCollectStore> storeList = new ArrayList<ActivityCollectStore>();
				for (String str : array) {
					ActivityCollectStore a = new ActivityCollectStore();
					a.setId(UuidUtils.getUuid());
					a.setCollectCouponsId(activityCollectCoupons.getId());
					a.setStoreId(str);
					storeList.add(a);
				}
				activityCollectStoreMapper.saveBatch(storeList);
			}
		}

		// 批量插入ActivityCollectOrderType表
		if (activityCollectCoupons.getOrderTypes() != null && activityCollectCoupons.getOrderTypes().length > 0) {
			List<ActivityCollectOrderType> otList = new ArrayList<ActivityCollectOrderType>();
			for (String orderTypeId : activityCollectCoupons.getOrderTypes()) {
				ActivityCollectOrderType ot = new ActivityCollectOrderType();
				ot.setId(UuidUtils.getUuid());
				ot.setCollectCouponsId(activityCollectCoupons.getId());
				ot.setOrderType(Integer.parseInt(orderTypeId));
				otList.add(ot);
			}
			activityCollectOrderTypeMapper.saveOrderTypeBatch(otList);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateDynamic(ActivityCollectCoupons activityCollectCoupons) {
		activityCollectCouponsMapper.updateDynamic(activityCollectCoupons);
	}

	@Transactional(readOnly = true)
	public ActivityCollectCoupons get(String id) {
		return activityCollectCouponsMapper.get(id);
	}

	@Override
	@Transactional(readOnly = true)
	public PageUtils<ActivityCollectCoupons> list(Map<String, Object> map, int pageNumber, int pageSize)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ActivityCollectCoupons> result = activityCollectCouponsMapper.list(map);
		String belongType = "";
		if (map.containsKey("belongType")) {
			belongType = map.get("belongType").toString();
		}
		// 转义一些字段
		if (result != null && result.size() > 0) {
			for (ActivityCollectCoupons a : result) {
				// 类型
				a.setTypeName(ActivityCollectCouponsType.getName(a.getType()));
				// 状态
				a.setStatusName(ActivityCollectCouponsStatus.getName(a.getStatus()));
				// 审核状态
				a.setApprovalStatusName(ActivityCollectCouponsApprovalStatus.getName(a.getApprovalStatus()));

				// 运营商要展示创建者名字
				if ("0".equals(belongType)) {
					if (StringUtils.isNotBlank(a.getCreateUserId())) {
						// 创建者是运营商
						if ("0".equals(a.getBelongType())) {
							SysUser su = iSysUserServiceApi.findSysUserById(a.getCreateUserId());
							a.setUpdateUserId(su == null ? "" : su.getUserName());
						} else {
							// 创建者是代理商
							PsmsAgent pa = iPsmsAgentServiceApi.loadById(a.getBelongType());
							a.setUpdateUserId(pa == null ? "" : pa.getFullName());
						}
					}
				}
			}
		}

		return new PageUtils<ActivityCollectCoupons>(result);
	}

	// 以单个活动为事务单元,一个活动失败,不影响其他活动
	@Transactional(rollbackFor = Exception.class)
	public void updateBatchStatus(String id, int status, String updateUserId, Date updateTime, String belongType)
			throws Exception {
		ActivityCollectCoupons c = new ActivityCollectCoupons();
		c.setId(id);
		c.setStatus(status);
		c.setUpdateTime(updateTime);
		activityCollectCouponsMapper.updateDynamic(c);

		// 关闭,已结束,已失效,都要把每个代理商的活动返还金额
		if (status == ActivityCollectCouponsStatus.closed.getValue()
				|| status == ActivityCollectCouponsStatus.end.getValue()
				|| status == ActivityCollectCouponsStatus.disabled.getValue()) {
			ActivityCollectCoupons acc = activityCollectCouponsMapper.get(id);
			// 代理商的数据 才同步金额或者发短信
			if (acc != null && acc.getBelongType() != null && !"0".equals(acc.getBelongType())) {
				// 某个活动已经使用过的总金额

				SysUser anentUser = sysUserMapper.getUserById(acc.getCreateUserId());
				if (anentUser != null && StringUtils.isNotEmpty(anentUser.getPhone())) {
					// 运营商关闭代理商的活动 或者 定时器失效给代理商发短信

					SmsVO smsVo = new SmsVO();
					smsVo.setId(UuidUtils.getUuid());
					smsVo.setIsTiming(0);
					smsVo.setToken(msgToken);
					smsVo.setSysCode(msgSysCode);
					smsVo.setMobile(anentUser.getPhone());
					smsVo.setSmsChannelType(3);
					smsVo.setSendTime(DateUtils.formatDateTime(new Date()));
					// 运营商关闭代理商的活动
					if ("0".equals(belongType) && status == ActivityCollectCouponsStatus.closed.getValue()) {
						try {
							smsVo.setContent("你的" + acc.getName() + "代金券活动已被运营商关闭");
							smsService.sendSms(smsVo);
						} catch (Exception e) {
							log.error("运营商关闭代理商的活动,发短信错误", e);
						}
					}

					// 定时器失效给代理商发短信
					if ("job".equals(belongType) && status == ActivityCollectCouponsStatus.disabled.getValue()) {
						try {
							smsVo.setContent("你的" + acc.getName() + "代金券活动超时没有通过审核，现已失效，请重新提交");
							smsService.sendSms(smsVo);
						} catch (Exception e) {
							log.error("定时器失效给代理商发短信,发短信错误", e);
						}
					}
				}
			}
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void update(ActivityCollectCoupons activityCollectCoupons, List<String> couponsIds, String areaIds,
			List<ActivityCollectXffqRelation> xffqRelationList) {
		// 修改活动对象
		activityCollectCouponsMapper.updateDynamic(activityCollectCoupons);

		// 保存关联的代金券,把activityId改为当前对象的id
		activityCouponsMapper.updateActivityIdNull(activityCollectCoupons.getId());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("couponsIds", couponsIds);
		map.put("activityId", activityCollectCoupons.getId());
		activityCouponsMapper.updateBatchActivityId(map);

		// 先删除老记录
		activityCollectAreaMapper.deleteByCollectCouponsId(activityCollectCoupons.getId());
		activityCollectCommunityMapper.deleteByCollectCouponsId(activityCollectCoupons.getId());
		activityCollectStoreMapper.deleteByCollectCouponsId(activityCollectCoupons.getId());

		if (ActivityCollectCouponsType.OPEN_DOOR.getValue().equals(activityCollectCoupons.getType())
				|| ActivityCollectCouponsType.consume_return.getValue().equals(activityCollectCoupons.getType())
				|| ActivityCollectCouponsType.get.getValue().equals(activityCollectCoupons.getType())
				|| ActivityCollectCouponsType.lzg.getValue().equals(activityCollectCoupons.getType())
				|| ActivityCollectCouponsType.red_packet.getValue().equals(activityCollectCoupons.getType())) {

			// 如果是消费返券,要批量插入梯度表
			if (ActivityCollectCouponsType.consume_return.getValue().equals(activityCollectCoupons.getType())) {
				// 修改相比添加,要先删除老数据
				activityCollectXffqRelationMapper.deleteByCollectId(activityCollectCoupons.getId());
				activityCollectXffqRelationMapper.saveBatch(xffqRelationList);
			}

			// 代金卷范围类型：0全国，1区域，2小区 , 3店铺
			// 如果是区域
			if (activityCollectCoupons.getAreaType() == 1) {

				// 批量添加新记录
				String[] array = areaIds.split(",");

				List<ActivityCollectArea> areaList = new ArrayList<ActivityCollectArea>();
				for (String str : array) {
					ActivityCollectArea a = new ActivityCollectArea();
					a.setId(UuidUtils.getUuid());
					a.setCollectCouponsId(activityCollectCoupons.getId());
					a.setType(Integer.parseInt(str.split("-")[1]));
					a.setAreaId(str.split("-")[0]);
					areaList.add(a);
				}
				activityCollectAreaMapper.saveBatch(areaList);
			}

			// 如果是小区
			if (activityCollectCoupons.getAreaType() == 2) {

				// 批量添加新记录
				String[] array = areaIds.split(",");
				List<ActivityCollectCommunity> areaList = new ArrayList<ActivityCollectCommunity>();
				for (String str : array) {
					ActivityCollectCommunity a = new ActivityCollectCommunity();
					a.setId(UuidUtils.getUuid());
					a.setCollectCouponsId(activityCollectCoupons.getId());
					a.setCommunityId(str);
					areaList.add(a);
				}
				activityCollectCommunityMapper.saveBatch(areaList);
			}

			// 如果是小区
			if (activityCollectCoupons.getAreaType() == AreaType.store.ordinal()) {
				// 批量添加新记录
				String[] array = areaIds.split(",");
				List<ActivityCollectStore> storeList = new ArrayList<ActivityCollectStore>();
				for (String str : array) {
					ActivityCollectStore a = new ActivityCollectStore();
					a.setId(UuidUtils.getUuid());
					a.setCollectCouponsId(activityCollectCoupons.getId());
					a.setStoreId(str);
					storeList.add(a);
				}
				activityCollectStoreMapper.saveBatch(storeList);
			}
		}

		// 先删掉老数据
		activityCollectOrderTypeMapper.deleteOrderTypeByCollectCouponsId(activityCollectCoupons.getId());

		// 批量插入ActivityCollectOrderType表
		if (activityCollectCoupons.getOrderTypes() != null && activityCollectCoupons.getOrderTypes().length > 0) {

			List<ActivityCollectOrderType> otList = new ArrayList<ActivityCollectOrderType>();
			for (String orderTypeId : activityCollectCoupons.getOrderTypes()) {
				ActivityCollectOrderType ot = new ActivityCollectOrderType();
				ot.setId(UuidUtils.getUuid());
				ot.setCollectCouponsId(activityCollectCoupons.getId());
				ot.setOrderType(Integer.parseInt(orderTypeId));
				otList.add(ot);
			}
			activityCollectOrderTypeMapper.saveOrderTypeBatch(otList);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityCollectArea> getAreaList(String collectCouponsId) {
		return activityCollectAreaMapper.listByCollectCouponsId(collectCouponsId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getAreaIds(String collectCouponsId) {
		List<ActivityCollectArea> list = this.getAreaList(collectCouponsId);

		List<String> ids = new ArrayList<String>();
		if (list != null && list.size() > 0) {
			for (ActivityCollectArea a : list) {
				ids.add(a.getAreaId());
			}
		}
		return ids;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityCollectCommunity> getCommunityList(String collectCouponsId) {
		return activityCollectCommunityMapper.listByCollectCouponsId(collectCouponsId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityCollectStore> getStoreList(String collectCouponsId) {
		return activityCollectStoreMapper.listByCollectCouponsId(collectCouponsId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getCommunityIds(String collectCouponsId) {
		List<ActivityCollectCommunity> list = this.getCommunityList(collectCouponsId);

		List<String> ids = new ArrayList<String>();
		if (list != null && list.size() > 0) {
			for (ActivityCollectCommunity a : list) {
				ids.add(a.getCommunityId());
			}
		}
		return ids;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityCollectCouponsVo> findByStoreAndLimitType(Map<String, Object> params) throws ServiceException {

		List<ActivityCollectCouponsVo> result = activityCollectCouponsMapper.selectByStoreAndLimitType(params);
		int currentRecordCount = 0;
		// 判断指定用户是否领取指定代金券
		if (result != null && result.size() > 0) {
			for (ActivityCollectCouponsVo vo : result) {
				ActivityCouponsRecordQueryParamDto activityCouponsRecord = new ActivityCouponsRecordQueryParamDto();
				activityCouponsRecord.setCouponsCollectId(vo.getId());
				List<ActivityCoupons> activityCouponsList = new ArrayList<>();
				activityCouponsList = vo.getActivityCoupons();
				if (activityCouponsList != null && activityCouponsList.size() > 0) {
					for (ActivityCoupons activityCoupons : activityCouponsList) {
						activityCouponsRecord.setCouponsId(activityCoupons.getId());
						activityCouponsRecord.setCollectType(ActivityCouponsType.coupons.ordinal());
						// 当前登陆用户id
						if (StringUtils.isNotEmpty(params.get("currentOperatUserId").toString())) {
							activityCouponsRecord.setCollectUserId(params.get("currentOperatUserId").toString());
							currentRecordCount = activityCouponsRecordMapper.selectCountByParams(activityCouponsRecord);
						}
						if (activityCoupons.getRemainNum() <= 0) {
							// 剩余数量小于0 显示已领完
							activityCoupons.setIsReceive(0);
						} else {
							if (currentRecordCount >= activityCoupons.getEveryLimit().intValue()) {
								// 已领取
								activityCoupons.setIsReceive(1);
							} else {
								// 立即领取
								activityCoupons.setIsReceive(2);
							}
						}
						// 根据代金卷类型判断使用的分类

						if (activityCoupons.getType() == 1) {
							if (activityCoupons.getIsCategory() == 1) {
								String categoryNames = "";
								// CouponsInfoQuery couponsInfo =
								// activityCouponsMapper.findNavCategoryByCouponsId(activityCoupons.getId());
								List<ActivityCouponsCategory> cates = activityCoupons.getActivityCouponsCategory();
								if (cates != null) {
									for (ActivityCouponsCategory category : cates) {
										if (StringUtils.isNotBlank(categoryNames)) {
											categoryNames = categoryNames + "、";
										}
										categoryNames = categoryNames + category.getCategoryName();
									}
								}
								activityCoupons.setCategoryNames(categoryNames);
							}
						} else if (activityCoupons.getType() == 2) {
							if (activityCoupons.getIsCategory() == 1) {
								String categoryNames = "";
								// CouponsInfoQuery couponsInfo =
								// activityCouponsMapper.findSpuCategoryByCouponsId(activityCoupons.getId());
								List<ActivityCouponsCategory> cates = activityCoupons.getActivityCouponsCategory();
								if (cates != null) {
									for (ActivityCouponsCategory category : cates) {
										if (StringUtils.isNotBlank(categoryNames)) {
											categoryNames = categoryNames + "、";
										}
										categoryNames = categoryNames + category.getCategoryName();
									}
								}
								activityCoupons.setCategoryNames(categoryNames);
							}

						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * @desc 用于判断某个时间段内活动是否冲突
	 * @param map
	 * @return
	 */
	@Transactional(readOnly = true)
	public int countTimeQuantum(Map<String, Object> map) {
		return activityCollectCouponsMapper.countTimeQuantum(map);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityCollectCouponsRecordVo> findByUnusedOrExpires(Map<String, Object> params) {
		return activityCollectCouponsMapper.selectByUnusedOrExpires(params);
	}

	/**
	 * @desc 查询出需要跑job的活动
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ActivityCollectCoupons> listByJob() {
		return activityCollectCouponsMapper.listByJob();
	}


	@Override
	@Transactional(readOnly = true)
	public int selectCountByStoreAndLimitType(Map<String, Object> params) throws ServiceException {
		int count = activityCollectCouponsMapper.selectCountByStoreAndLimitType(params);
		return count;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsServiceApi#findCollectCouponsAreaList(java.util.Map)
	 */
	@Override
	public int findCollectCouponsAreaList(Map<String, Object> map) throws ServiceException {
		return activityCollectCouponsMapper.findCollectCouponsAreaList(map);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> saveCouponInfo(String userId, String provinceId, String cityId, String communityId)
			throws ServiceException {
		Map<String, String> resultMap = new HashMap<String, String>();
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(cityId)) {
			throw new ServiceException("请求参数为空!");
		}

		// begin modify by zengjz 2016-12-16 开门领取代金券增加小区参数
		ActivityCollectCouponsVo activityCollectCouponsVo = activityCollectCouponsMapper
				.findActivityCollectCouponsByCity(provinceId, cityId, communityId);
		// end modify by zengjz 2016-12-16 开门领取代金券增加小区参数

		if (activityCollectCouponsVo != null && activityCollectCouponsVo.getActivityCoupons() != null
				&& activityCollectCouponsVo.getActivityCoupons().size() > 0) {
			ActivityCoupons activityCoupon = activityCollectCouponsVo.getActivityCoupons().get(0);
			if (activityCoupon.getRemainNum() <= 0) {
				return resultMap;
			}
			// 设置代金券领取记录的代金券id、代金券领取活动id、活动类型，以便后面代码中的数量判断查询
			ActivityCouponsRecord activityCouponsRecord = new ActivityCouponsRecord();
			Date collectTime = DateUtils.getDateStart(new Date());
			activityCouponsRecord.setCouponsId(activityCoupon.getId());
			activityCouponsRecord.setCouponsCollectId(activityCoupon.getActivityId());
			activityCouponsRecord.setCollectType(ActivityCouponsType.openthedoor);
			activityCouponsRecord.setCollectTime(collectTime);
			// 每日领取数

			ActivityCouponsRecordQueryParamDto activityCouponsRecordQueryParamDto = new ActivityCouponsRecordQueryParamDto();
			BeanMapper.copy(activityCouponsRecord, activityCouponsRecordQueryParamDto);
			activityCouponsRecordQueryParamDto.setCollectType(ActivityCouponsType.openthedoor.ordinal());

			int dailyCirculation = activityCouponsRecordMapper.selectCountByParams(activityCouponsRecordQueryParamDto);
			// 每日发行量是否已完
			if (dailyCirculation >= activityCollectCouponsVo.getDailyCirculation().intValue()) {
				return resultMap;
			}
			activityCouponsRecord.setCollectTime(null);
			activityCouponsRecord.setCollectUserId(userId);

			activityCouponsRecordQueryParamDto.setCollectTime(null);
			activityCouponsRecordQueryParamDto.setCollectUserId(userId);
			// 总领取数
			int total = activityCouponsRecordMapper.selectCountByParams(activityCouponsRecordQueryParamDto);
			// 每人限领数量
			if (activityCoupon.getEveryLimit() > 0 && total >= activityCoupon.getEveryLimit().intValue()) {
				return resultMap;
			}
			// 每人每天只能领一张代金券
			activityCouponsRecord.setCouponsCollectId(null);
			activityCouponsRecord.setCouponsId(null);
			activityCouponsRecord.setCollectTime(collectTime);

			activityCouponsRecordQueryParamDto.setCouponsCollectId(null);
			activityCouponsRecordQueryParamDto.setCouponsId(null);
			activityCouponsRecordQueryParamDto.setCollectTime(collectTime);

			// 今天是否已领取
			int currentRecordCount = 0;
			currentRecordCount = activityCouponsRecordMapper.selectCountByParams(activityCouponsRecordQueryParamDto);
			if (currentRecordCount > 0) {
				return resultMap;
			} else {
				log.info("userId:" + userId + "==activityCouponId:" + activityCoupon.getId());
				activityCouponsRecord.setId(UuidUtils.getUuid());
				Date date = new Date();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0,
						0, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				activityCouponsRecord.setCouponsId(activityCoupon.getId());
				activityCouponsRecord.setCouponsCollectId(activityCoupon.getActivityId());
				activityCouponsRecord.setCollectTime(calendar.getTime());
				activityCouponsRecord.setStatus(ActivityCouponsRecordStatusEnum.UNUSED);
				calendar.add(Calendar.DAY_OF_YEAR, activityCoupon.getValidDay());
				activityCouponsRecord.setValidTime(calendar.getTime());

				activityCouponsRecordMapper.insertSelective(activityCouponsRecord);
				// 更新代金券剩余数量
				int rows = activityCouponsMapper.updateRemainNum(activityCoupon.getId());
				if (rows == 0) {
					throw new ServiceException("没有更新代金券剩余数量");
				}

				// 代金券金额
				resultMap.put("couponPrice", String.valueOf(activityCoupon.getFaceValue()));
				// 使用范围
				String usableRange = "";
				// Begin 开门红包代金券根据类型返回文案 added by tangy 2016-10-20
				switch (activityCoupon.getType().intValue()) {
					case 0:
						usableRange = "友门鹿代金券";
						break;
					case 1:
						usableRange = "限便利店专用";
						break;
					case 2:
						usableRange = "限服务店专用";
						break;
					case 3:
						usableRange = "限话费充值";
						break;
					default:
						break;
				}
				resultMap.put("usableRange", usableRange);
			}

		}
		return resultMap;
	}

	@Override
	public List<ActivityCoupons> findCouponsByParams(Map<String, Object> map) {
		return activityCollectCouponsMapper.findCouponsByParams(map);
	}

	/**
	 * @Description: 根据活动id查询列表
	 * @param collectCouponsId 代金券活动id
	 * @author zhangkn
	 * @date 2016年9月17日
	 */
	@Override
	public List<ActivityCollectOrderType> findOrderTypeListByCollectCouponsId(String collectCouponsId) {
		return activityCollectOrderTypeMapper.findOrderTypeListByCollectCouponsId(collectCouponsId);
	}

	/**
	 * @Description: 消费返券：活动代金券查询 添加梯度
	 * @param map 参数map
	 * @return list
	 * @throws ServiceException 异常
	 * @author tuzhd
	 * @date 2017年6月26日
	 */
	@Override
	public ActivityCollectCouponsOrderVo findCollCouponsLinks(Map<String, Object> map) throws ServiceException {
		List<ActivityCollectCouponsOrderVo> list = activityCollectCouponsMapper.findCollCouponsLinks(map);
		// 如果未查询到记录 或
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		// 默认第一个 梯度
		String relationId = list.get(0).getRelationId();
		BigDecimal limitAmout = list.get(0).getLimitAmount();
		ActivityCollectCouponsOrderVo curr = list.get(0);

		// 筛选符合梯度的 返券梯度
		for (ActivityCollectCouponsOrderVo li : list) {
			// 大于当前梯度 则为返回该梯度
			if (li.getLimitAmount() != null && li.getLimitAmount().compareTo(limitAmout) > 0) {
				limitAmout = li.getLimitAmount();
				relationId = li.getRelationId();
			}
		}

		// 根据筛选的梯度查询优惠券
		List<ActivityCoupons> couponsList = activityCollectCouponsMapper.findCouponsByReleaID(relationId);
		curr.setActivityCoupons(couponsList);

		return curr;
	}

	@Override
	public ActivityCollectCouponsSimpleVo findRecommendAcvitity() {
		List<ActivityCollectCouponsSimpleVo> simpleVos = this.activityCollectCouponsMapper.findRecommendAcvititys();
		ActivityCollectCouponsSimpleVo simpleVo = null;
		if (CollectionUtils.isEmpty(simpleVos)) {
			simpleVo = new ActivityCollectCouponsSimpleVo();
			simpleVo.setVoucher(false);
		} else {
			simpleVo = simpleVos.get(0);
			simpleVo.setVoucher(true);
		}
		return simpleVo;
	}

	@Override
	public ActivityCollectCouponsVo findRandCodeVoucherList(Map<String, Object> params) throws ServiceException {
		List<ActivityCollectCouponsVo> result = activityCollectCouponsMapper.selectAdvertVoucher(params);
		if (result != null && result.size() > 0) {
			ActivityCollectCouponsVo vo = result.get(0);
			params.put("collectId", vo.getId());
			if (activityCollectCouponsMapper.selectCountByUserId(params) >= 1) {
				vo.setIsReceive(true);
			} else {
				vo.setIsReceive(false);
			}
			if (vo.getStartTime() != null) {
				vo.setStartTimeStr(DateUtils.formatDate(vo.getStartTime(), "yyyy年MM月dd日"));
			}
			if (vo.getEndTime() != null) {
				vo.setEndTimeStr(DateUtils.formatDate(vo.getEndTime(), "yyyy年MM月dd日"));
			}
			return vo;
		}
		return null;
	}

	@Override
	public ActivityCollectCoupons findCollectCouponsByModelId(String modelId, String activityAdvertId) {
		return activityCollectCouponsMapper.findCollectCouponsByModelId(modelId, activityAdvertId);

	}

	/**
	 * @Description: 根据代金券活动类型及店铺区域查询代金券活动信息
	 * @param map
	 * @return List<ActivityCollectCoupons>  
	 * @author tuzhd
	 * @date 2017年6月30日
	 */
	public List<ActivityCollectCoupons> findCollectCouponsByType(Map<String, Object> map) {
		return activityCollectCouponsMapper.findCollectCouponsByType(map);
	}

	@Override
	public List<ActivityCollectCoupons> findByIds(List<String> idList) {
		if (CollectionUtils.isEmpty(idList)) {
			return Lists.newArrayList();
		}
		return activityCollectCouponsMapper.findByIds(idList);
	}

	@Override
	public Boolean isShareRedPackage(ActivityCollectCoupons coupon) {
		ActivityCouponsRecord record = new ActivityCouponsRecord();
		record.setCollectTime(DateUtils.getDateStart(new Date()));
		record.setCollectType(ActivityCouponsType.red_packet);
		record.setCouponsCollectId(coupon.getId());
		int drawAmount = getDaliyDrawAmount(record);

		// 0表示不限制 每日最大发行量大于领取数量
		if (Integer.valueOf(coupon.getDailyCirculation()) == 0
				|| Integer.valueOf(coupon.getDailyCirculation()) > drawAmount) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @Description: 获取代金券每日已领取数量
	 * @param record
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月22日
	 */
	private int getDaliyDrawAmount(ActivityCouponsRecord record) {
		ActivityCouponsRecordQueryParamDto activityCouponsRecordQueryParamDto = new ActivityCouponsRecordQueryParamDto();
		BeanMapper.copy(record, activityCouponsRecordQueryParamDto);
		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		String startTime = DateUtils.formatDate(DateUtils.getDateStart(new Date()), dateFormat);
		String endTime = DateUtils.formatDate(DateUtils.getDateEnd(new Date()), dateFormat);
		activityCouponsRecordQueryParamDto.setCollectStartTime(startTime);
		activityCouponsRecordQueryParamDto.setCollectEndTime(endTime);
		ActivityCouponsRecordBeforeParamDto activityCouponsRecordBeforeParamDto = new ActivityCouponsRecordBeforeParamDto();
		BeanMapper.copy(activityCouponsRecordQueryParamDto, activityCouponsRecordBeforeParamDto);
		// 当前日期已经领取的数量
		int dailyCirculation = activityCouponsRecordMapper.selectCountByParams(activityCouponsRecordQueryParamDto);
		// 当前代金劵日已经预领取领取的数量
		int dailyBefore = activityCouponsRecordBeforeMapper.getCountByDayParams(activityCouponsRecordBeforeParamDto);

		return dailyCirculation + dailyBefore;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public TakeActivityCouponResultDto takeActivityCoupon(TakeActivityCouponParamDto activityCouponParamDto) {
		TakeActivityCouponResultDto resultDto = new TakeActivityCouponResultDto();
		SysBuyerUser sysBuyerUser = buyerUserMapper.selectByPrimaryKey(activityCouponParamDto.getUserId());
		if (sysBuyerUser == null) {
			setTakeActivityCouponResult(resultDto, 103, "用户不存在");
			return resultDto;
		}
		List<SysBuyerUser> sysBuyerUserList = buyerUserMapper.selectUserByPhone(activityCouponParamDto.getMobile());
		if (CollectionUtils.isNotEmpty(sysBuyerUserList)) {
			activityCouponParamDto.setUserId(sysBuyerUserList.get(0).getId());
		}

		ActivityCollectCoupons activityCollectCoupons = activityCollectCouponsMapper
				.get(activityCouponParamDto.getActivityId());
		if (activityCollectCoupons == null) {
			setTakeActivityCouponResult(resultDto, 101, "活动已经结束");
			return resultDto;
		}
		// 校验活动是否结束
		boolean isEnd = isActivityEnd(activityCollectCoupons);
		if (isEnd) {
			setTakeActivityCouponResult(resultDto, 101, "活动已经结束");
			return resultDto;
		}
		// 校验领取次数是否已经超过 每日发行量
		int drawCount = getActivityRecordCount(activityCollectCoupons);
		if (Integer.valueOf(activityCollectCoupons.getDailyCirculation()) >= drawCount) {
			setTakeActivityCouponResult(resultDto, 102, "今日奖品已经被抽完啦，请明日再来");
			return resultDto;
		}
		// 校验用户的领取次数是否已经超过每日最多领取次数
		int userDrawTimes = getActivityDrawTimesByActivity(activityCollectCoupons, activityCouponParamDto);

		if (userDrawTimes >= activityCollectCoupons.getUserDayTimesLimit()) {
			setTakeActivityCouponResult(resultDto, 104, "您已经超过今日领取次数，请明日再来!");
			return resultDto;
		}
		// 校验是否需要限制订单数
		if (activityCouponParamDto.isLimitOrder()) {
			int drawTimes = getActivityDrawTimesByOrder(activityCollectCoupons, activityCouponParamDto);
			if(drawTimes > 0){
				setTakeActivityCouponResult(resultDto, 105, "您已经领取过了!");
			}
		}
		try {
			activityCouponsService.takeCoupons(activityCollectCoupons, activityCouponParamDto);
			resultDto.setCode(0);
			resultDto.setMsg("领取成功");
		} catch (Exception e) {
			log.error("领取代金劵失败", e);
			setTakeActivityCouponResult(resultDto, 106, e.getMessage());
		}
		return resultDto;
	}

	private int getActivityDrawTimesByOrder(ActivityCollectCoupons activityCollectCoupons,
			TakeActivityCouponParamDto activityCouponParamDto) {
		ActivityCouponsRecordQueryParamDto activityCouponsRecordQueryParamDto = new ActivityCouponsRecordQueryParamDto();
		activityCouponsRecordQueryParamDto.setCouponsCollectId(activityCollectCoupons.getId());
		activityCouponsRecordQueryParamDto.setCollectUserId(activityCouponParamDto.getUserId());
		activityCouponsRecordQueryParamDto.setOrderId(activityCouponParamDto.getOrderId());
		if (StringUtils.isNotEmpty(activityCouponParamDto.getUserId())) {
			return activityCouponsRecordMapper.selectActivityCountByParams(activityCouponsRecordQueryParamDto);
		} else {
			ActivityCouponsRecordBeforeParamDto activityCouponsRecordBeforeParamDto = new ActivityCouponsRecordBeforeParamDto();
			BeanMapper.copy(activityCouponsRecordQueryParamDto, activityCouponsRecordBeforeParamDto);
			activityCouponsRecordBeforeParamDto.setCollectUser(activityCouponParamDto.getMobile());
			return activityCouponsRecordBeforeMapper.selectActivityCountByParams(activityCouponsRecordBeforeParamDto);
		}
	}

	private int getActivityDrawTimesByActivity(ActivityCollectCoupons activityCollectCoupons,
			TakeActivityCouponParamDto activityCouponParamDto) {
		ActivityCouponsRecordQueryParamDto activityCouponsRecordQueryParamDto = new ActivityCouponsRecordQueryParamDto();
		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		String startTime = DateUtils.formatDate(DateUtils.getDateStart(new Date()), dateFormat);
		String endTime = DateUtils.formatDate(DateUtils.getDateEnd(new Date()), dateFormat);
		activityCouponsRecordQueryParamDto.setCollectStartTime(startTime);
		activityCouponsRecordQueryParamDto.setCollectEndTime(endTime);
		activityCouponsRecordQueryParamDto.setCouponsCollectId(activityCollectCoupons.getId());
		activityCouponsRecordQueryParamDto.setCollectUserId(activityCouponParamDto.getUserId());
		if (StringUtils.isNotEmpty(activityCouponParamDto.getUserId())) {
			return activityCouponsRecordMapper.selectActivityCountByParams(activityCouponsRecordQueryParamDto);
		} else {
			ActivityCouponsRecordBeforeParamDto activityCouponsRecordBeforeParamDto = new ActivityCouponsRecordBeforeParamDto();
			BeanMapper.copy(activityCouponsRecordQueryParamDto, activityCouponsRecordBeforeParamDto);
			activityCouponsRecordBeforeParamDto.setCollectUser(activityCouponParamDto.getMobile());
			return activityCouponsRecordBeforeMapper.selectActivityCountByParams(activityCouponsRecordBeforeParamDto);
		}
	}

	/**
	 * @Description: 获取活动抽奖次数
	 * @param activityCollectCoupons
	 * @return
	 * @author zengjizu
	 * @date 2017年8月25日
	 */
	private int getActivityRecordCount(ActivityCollectCoupons activityCollectCoupons) {
		ActivityCouponsRecord record = new ActivityCouponsRecord();
		record.setCollectTime(DateUtils.getDateStart(new Date()));
		record.setCollectType(ActivityCouponsType.enumValueOf(activityCollectCoupons.getType()));
		record.setCouponsCollectId(activityCollectCoupons.getId());
		return getDaliyDrawAmount(record);
	}

	/**
	 * @Description: 判断活动是否结束了
	 * @param activityCollectCoupons
	 * @return
	 * @author zengjizu
	 * @date 2017年8月25日
	 */
	private boolean isActivityEnd(ActivityCollectCoupons activityCollectCoupons) {
		return activityCollectCoupons.getStatus() != 1
				|| System.currentTimeMillis() < activityCollectCoupons.getStartTime().getTime()
				|| System.currentTimeMillis() > activityCollectCoupons.getEndTime().getTime();
	}

	/**
	 * @Description: 设置抽奖结果
	 * @param resultDto
	 * @param code
	 * @param msg
	 * @author zengjizu
	 * @date 2017年8月25日
	 */
	private void setTakeActivityCouponResult(TakeActivityCouponResultDto resultDto, int code, String msg) {
		resultDto.setCode(code);
		resultDto.setMsg(msg);
	}

}