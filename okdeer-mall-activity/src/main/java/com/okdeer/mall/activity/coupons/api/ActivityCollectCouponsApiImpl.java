
package com.okdeer.mall.activity.coupons.api;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.coupons.bo.ActivityCollectAreaParamBo;
import com.okdeer.mall.activity.coupons.bo.ActivityCollectStoreParamBo;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectArea;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectStore;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsType;
import com.okdeer.mall.activity.coupons.service.ActivityCollectAreaService;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsApi;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsService;
import com.okdeer.mall.activity.coupons.service.ActivityCollectStoreService;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordService;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsService;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsServiceApi;
import com.okdeer.mall.activity.dto.ActivityCollectCouponsDto;
import com.okdeer.mall.activity.dto.ActivityCollectCouponsParamDto;
import com.okdeer.mall.activity.dto.ActivityCouponsDto;
import com.okdeer.mall.activity.dto.ActivityCouponsQueryParamDto;
import com.okdeer.mall.activity.dto.ActivityCouponsRecordQueryParamDto;
import com.okdeer.mall.activity.dto.TakeActivityCouponParamDto;
import com.okdeer.mall.activity.dto.TakeActivityCouponResultDto;
import com.okdeer.mall.common.enums.AreaType;

@Service(version = "1.0.0")
public class ActivityCollectCouponsApiImpl implements ActivityCollectCouponsApi {

	private static Logger logger = LoggerFactory.getLogger(ActivityCollectCouponsApiImpl.class);

	@Autowired
	private ActivityCollectCouponsService activityCollectCouponsService;

	@Autowired
	private ActivityCouponsServiceApi activityCouponsApi;

	@Autowired
	private ActivityCouponsService activityCouponsService;

	@Autowired
	private ActivityCouponsRecordService activityCouponsRecordService;

	@Autowired
	private ActivityCollectAreaService activityCollectAreaService;

	@Autowired
	private ActivityCollectStoreService activityCollectStoreService;

	@Override
	public ActivityCollectCouponsDto findById(String id) {
		ActivityCollectCoupons activityCollectCoupons = activityCollectCouponsService.get(id);
		return BeanMapper.map(activityCollectCoupons, ActivityCollectCouponsDto.class);
	}

	@Override
	public TakeActivityCouponResultDto takeActivityCoupon(TakeActivityCouponParamDto activityCouponParamDto) {
		try {
			return activityCollectCouponsService.takeActivityCoupon(activityCouponParamDto);
		} catch (Exception e) {
			TakeActivityCouponResultDto activityCouponResultDto = new TakeActivityCouponResultDto();
			activityCouponResultDto.setCode(110);
			activityCouponResultDto.setMsg(e.getMessage());
			return activityCouponResultDto;
		}
	}

	@Override
	public List<ActivityCollectCouponsDto> findList(ActivityCollectCouponsParamDto activityCollectCouponsParamDto) {
		List<ActivityCollectCoupons> list = activityCollectCouponsService.findList(activityCollectCouponsParamDto);
		if (CollectionUtils.isEmpty(list)) {
			return Lists.newArrayList();
		}
		// 根据区域参数过滤
		filterByArea(activityCollectCouponsParamDto, list);
		// 根据店铺id过滤
		filterByStore(activityCollectCouponsParamDto, list);

		List<ActivityCollectCouponsDto> dtoList = BeanMapper.mapList(list, ActivityCollectCouponsDto.class);

		if (activityCollectCouponsParamDto.isQueryCoupons()) {
			// 查询代金卷信息
			queryRelationCoupons(activityCollectCouponsParamDto, dtoList);
		}
		return dtoList;
	}

	private void queryRelationCoupons(ActivityCollectCouponsParamDto activityCollectCouponsParamDto,
			List<ActivityCollectCouponsDto> dtoList) {
		ActivityCouponsQueryParamDto activityCouponsQueryParamDto = new ActivityCouponsQueryParamDto();
		activityCouponsQueryParamDto.setQueryArea(false);
		activityCouponsQueryParamDto.setQueryCategory(true);

		for (ActivityCollectCouponsDto activityCollectCouponsDto : dtoList) {
			List<ActivityCoupons> couponseList = activityCouponsService
					.getActivityCoupons(activityCollectCouponsDto.getId());

			couponseList = filterByActivityType(activityCollectCouponsParamDto.getType(), couponseList);
			if (couponseList == null) {
				continue;
			}
			//代金卷排序
			sortCouponseList(couponseList);

			List<ActivityCouponsDto> couponseDtoList = BeanMapper.mapList(couponseList, ActivityCouponsDto.class);

			for (ActivityCouponsDto activityCoupons : couponseDtoList) {
				ActivityCouponsDto activityCouponsDto = activityCouponsApi.findDetailById(activityCoupons.getId(),
						activityCouponsQueryParamDto);
				BeanMapper.copy(activityCouponsDto, activityCoupons);
				activityCoupons.setActivityCouponsCategory(activityCouponsDto.getActivityCouponsCategory());
				if (activityCoupons.getRemainNum() <= 0) {
					// 剩余数量小于0 显示已领完
					activityCoupons.setIsReceive(0);
				} else {
					//查询用户是否领取过了
					queryUserIsReceiveCoupons(activityCollectCouponsParamDto.getUserId(), activityCoupons);
				}
			}
			activityCollectCouponsDto.setCouponsList(couponseDtoList);
		}
	}

	

	/**
	 * @Description: 根据活动类型过滤代金卷
	 * @param type
	 * @param couponseList
	 * @return
	 * @author zengjizu
	 * @date 2017年11月14日
	 */
	private List<ActivityCoupons> filterByActivityType(Integer type, List<ActivityCoupons> couponseList) {
		if (CollectionUtils.isEmpty(couponseList)) {
			return couponseList;
		}
		if (type == null) {
			return couponseList;
		} else if (type == 0) {
			List<ActivityCoupons> resultList = Lists.newArrayList();
			// 领卷活动
			for (ActivityCoupons activityCoupons : couponseList) {
				if (StringUtils.isNotEmpty(activityCoupons.getExchangeCode()) || activityCoupons.getIsRandCode() == 1) {
					continue;
				}
				resultList.add(activityCoupons);
			}
			return resultList;
		}
		return couponseList;
	}

	private void queryUserIsReceiveCoupons(String userId, ActivityCouponsDto activityCoupons) {
		ActivityCouponsRecordQueryParamDto activityCouponsRecord = new ActivityCouponsRecordQueryParamDto();
		activityCouponsRecord.setCouponsCollectId(activityCoupons.getActivityId());
		activityCouponsRecord.setCouponsId(activityCoupons.getId());
		activityCouponsRecord.setCollectType(ActivityCouponsType.coupons.ordinal());
		// 当前登陆用户id
		if (StringUtils.isNotEmpty(userId)) {
			activityCouponsRecord.setCollectUserId(userId);
			try {
				int currentRecordCount = activityCouponsRecordService.selectCountByParams(activityCouponsRecord);
				if (currentRecordCount >= activityCoupons.getEveryLimit().intValue()) {
					// 已领取
					activityCoupons.setIsReceive(1);
				} else {
					// 立即领取
					activityCoupons.setIsReceive(2);
				}
			} catch (ServiceException e) {
				logger.error("查询代金卷领取数量信息出错", e);
			}
		} else {
			activityCoupons.setIsReceive(2);
		}
	}

	private void filterByArea(ActivityCollectCouponsParamDto activityCollectCouponsParamDto,
			List<ActivityCollectCoupons> list) {
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		List<String> idList = getActivityCollectCouponsIds(list);
		ActivityCollectAreaParamBo activityCollectAreaParamBo = new ActivityCollectAreaParamBo();
		activityCollectAreaParamBo.setCollectCouponsIdList(idList);

		List<String> existsIdList = Lists.newArrayList();
		if (StringUtils.isNotEmpty(activityCollectCouponsParamDto.getProvinceId())) {
			activityCollectAreaParamBo.setAreaId(activityCollectCouponsParamDto.getProvinceId());
			activityCollectAreaParamBo.setType(1);
			List<ActivityCollectArea> areaList = activityCollectAreaService.findList(activityCollectAreaParamBo);
			areaList.forEach(e -> {
				if (!existsIdList.contains(e.getCollectCouponsId())) {
					existsIdList.add(e.getCollectCouponsId());
				}
			});
		}

		if (StringUtils.isNotEmpty(activityCollectCouponsParamDto.getCityId())) {
			activityCollectAreaParamBo.setAreaId(activityCollectCouponsParamDto.getCityId());
			activityCollectAreaParamBo.setType(0);
			List<ActivityCollectArea> areaList = activityCollectAreaService.findList(activityCollectAreaParamBo);
			areaList.forEach(e -> {
				if (!existsIdList.contains(e.getCollectCouponsId())) {
					existsIdList.add(e.getCollectCouponsId());
				}
			});
		}
		List<ActivityCollectCoupons> removeList = Lists.newArrayList();
		for (ActivityCollectCoupons activityCollectCoupons : list) {
			if (activityCollectCoupons.getAreaType() == AreaType.area.ordinal()
					&& !existsIdList.contains(activityCollectCoupons.getId())) {
				removeList.add(activityCollectCoupons);
			}
		}

		list.removeAll(removeList);
	}

	private void filterByStore(ActivityCollectCouponsParamDto activityCollectCouponsParamDto,
			List<ActivityCollectCoupons> list) {
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		List<String> idList = getActivityCollectCouponsIds(list);
		ActivityCollectStoreParamBo activityCollectStoreParamBo = new ActivityCollectStoreParamBo();
		activityCollectStoreParamBo.setCollectCouponsIdList(idList);

		List<String> existsIdList = Lists.newArrayList();
		if (StringUtils.isNotEmpty(activityCollectCouponsParamDto.getStoreId())) {
			activityCollectStoreParamBo.setStoreId(activityCollectCouponsParamDto.getStoreId());
			List<ActivityCollectStore> areaList = activityCollectStoreService.findList(activityCollectStoreParamBo);
			areaList.forEach(e -> {
				if (!existsIdList.contains(e.getCollectCouponsId())) {
					existsIdList.add(e.getCollectCouponsId());
				}
			});
		}
		List<ActivityCollectCoupons> removeList = Lists.newArrayList();
		for (ActivityCollectCoupons activityCollectCoupons : list) {
			if (activityCollectCoupons.getAreaType() == AreaType.store.ordinal()
					&& !existsIdList.contains(activityCollectCoupons.getId())) {
				removeList.add(activityCollectCoupons);
			}
		}
		list.removeAll(removeList);
	}

	private List<String> getActivityCollectCouponsIds(List<ActivityCollectCoupons> list) {
		List<String> idList = Lists.newArrayList();
		for (ActivityCollectCoupons activityCollectCoupons : list) {
			idList.add(activityCollectCoupons.getId());
		}
		return idList;
	}
	
	/**
	 * @Description: 代金卷按创建时间排序,时间越后的放在前面
	 * @param couponseList
	 * @author zengjizu
	 * @date 2017年11月23日
	 */
	private void sortCouponseList(List<ActivityCoupons> couponseList) {
		couponseList.sort((obj1, obj2) -> {
			if (obj1.getCreateTime().getTime() == obj2.getCreateTime().getTime()) {
				return 0;
			}
			if (obj1.getCreateTime().getTime() > obj2.getCreateTime().getTime()) {
				return -1;
			}
			return 1;
		});
	}
}
