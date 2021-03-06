
package com.okdeer.mall.activity.coupons.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.base.dto.GoodsNavigateCategoryDto;
import com.okdeer.archive.goods.base.dto.GoodsNavigateCategoryParamDto;
import com.okdeer.archive.goods.base.dto.GoodsSpuCategoryParamDto;
import com.okdeer.archive.goods.base.entity.GoodsNavigateCategory;
import com.okdeer.archive.goods.base.entity.GoodsSpuCategory;
import com.okdeer.archive.goods.base.service.GoodsNavigateCategoryServiceApi;
import com.okdeer.archive.goods.base.service.GoodsSpuCategoryServiceApi;
import com.okdeer.archive.store.entity.StoreAgentCommunity;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.archive.store.service.StoreAgentCommunityServiceApi;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.bdp.address.entity.Address;
import com.okdeer.bdp.address.service.IAddressService;
import com.okdeer.mall.activity.coupons.bo.ActivityCouponsBo;
import com.okdeer.mall.activity.coupons.bo.ActivityCouponsCategoryParamBo;
import com.okdeer.mall.activity.coupons.bo.CouponsRelationStoreParamBo;
import com.okdeer.mall.activity.coupons.dto.ActivityCouponsAreaDto;
import com.okdeer.mall.activity.coupons.dto.ActivityCouponsRelationStoreDto;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsArea;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsCategory;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsCommunity;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsLimitCategory;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRandCode;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecordBefore;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRelationStore;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsStore;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsThirdCode;
import com.okdeer.mall.activity.coupons.entity.CouponsInfoParams;
import com.okdeer.mall.activity.coupons.entity.CouponsInfoQuery;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsTermType;
import com.okdeer.mall.activity.coupons.enums.CouponsType;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsAreaMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsCategoryMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRandCodeMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRelationStoreMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsThirdCodeMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsReceiveStrategy;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordBeforeService;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordService;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsService;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsServiceApi;
import com.okdeer.mall.activity.coupons.vo.AddressCityVo;
import com.okdeer.mall.activity.dto.ActivityCouponsDto;
import com.okdeer.mall.activity.dto.ActivityCouponsQueryParamDto;
import com.okdeer.mall.activity.dto.ActivityCouponsRecordBeforeParamDto;
import com.okdeer.mall.activity.dto.ActivityCouponsRecordQueryParamDto;
import com.okdeer.mall.activity.dto.TakeActivityCouponParamDto;
import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.mall.common.enums.AuditStatusEnum;
import com.okdeer.mall.common.enums.DistrictType;
import com.okdeer.mall.common.utils.RandomStringUtil;

/**
 * 代金券管理实现类
 * 
 * @project yschome-mall
 * @author zhulq
 * @date 2016年1月21日 下午3:22:25
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivityCouponsServiceApi")
public class ActivityCouponsServiceImpl implements ActivityCouponsServiceApi, ActivityCouponsService {

	private static Logger logger = LoggerFactory.getLogger(ActivityCouponsServiceImpl.class);

	/**
	 * 注入代金券管理mapper
	 */
	@Autowired
	private ActivityCouponsMapper activityCouponsMapper;

	/**
	 * 注入代金券管理mapper
	 */
	@Autowired
	private ActivityCouponsThirdCodeMapper activityCouponsThirdCodeMapper;

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

	@Autowired
	private ActivityCouponsAreaMapper activityCouponsAreaMapper;

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

	@Reference(version = "1.0.0", check = false, timeout = 5000)
	private GoodsNavigateCategoryServiceApi goodsNavigateCategoryServiceApi;

	@Reference(version = "1.0.0", check = false)
	private GoodsSpuCategoryServiceApi goodsSpuCategoryServiceApi;

	// Begin added by maojj 2016-10-25
	/**
	 * 保存数据库的最大值。用于随机码生成
	 */
	@Resource
	private ActivityCouponsRandCodeMapper activityCouponsRandCodeMapper;

	@Resource
	private ActivityCouponsRecordService activityCouponsRecordService;

	@Resource
	private ActivityCouponsRecordBeforeService activityCouponsRecordBeforeService;

	@Resource
	private ActivityCouponsReceiveStrategy activityCouponsReceiveStrategy;

	private static final int MAX_NUM = 1000;

	/**
	 * 随机数长度
	 */
	private static final int RAND_CODE_LENGTH = 8;
	// End added by maojj 2016-10-25

	@Override
	public PageUtils<CouponsInfoQuery> getCouponsInfo(CouponsInfoParams couponsInfoParams, int pageNum, int pageSize)
			throws ServiceException {
		PageHelper.startPage(pageNum, pageSize, true);
		couponsInfoParams.setDisabled(Disabled.valid);
		List<CouponsInfoQuery> couponsInfos = activityCouponsMapper.selectCoupons(couponsInfoParams);
		if (couponsInfos == null) {
			couponsInfos = new ArrayList<>();
		}

		// 转义字段
		for (CouponsInfoQuery c : couponsInfos) {
			if (c.getType() != null) {
				c.setTypeName(CouponsType.getName(c.getType()));
			}
		}

		PageUtils<CouponsInfoQuery> pageUtils = new PageUtils<>(couponsInfos);
		return pageUtils;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addCoupons(ActivityCoupons coupons) throws ServiceException {
		// 0：便利店和服务店，1：便利店 2 服务店 3话费充值
		if (CouponsType.bldfwd.getValue().equals(coupons.getType())) {
			activityCouponsMapper.insert(coupons);
		} else if (CouponsType.hfcz.getValue().equals(coupons.getType())) {
			activityCouponsMapper.insert(coupons);
		} else if (CouponsType.bldyf.getValue().equals(coupons.getType())) {
			activityCouponsMapper.insert(coupons);
		} else if (CouponsType.bld.getValue().equals(coupons.getType())) {
			activityCouponsMapper.insert(coupons);
			this.addRelatedInfo(coupons);
		} else if (CouponsType.fwd.getValue().equals(coupons.getType())) {
			activityCouponsMapper.insert(coupons);
			this.addRelatedInfo(coupons);
		} else if (CouponsType.yyhz.getValue().equals(coupons.getType())) {
			activityCouponsMapper.insert(coupons);
			// 批量插入excel导入的兑换码
			if (CollectionUtils.isNotEmpty(coupons.getYiyeCodeList())) {
				List<ActivityCouponsThirdCode> acList = new ArrayList<ActivityCouponsThirdCode>();
				for (String code : coupons.getYiyeCodeList()) {
					ActivityCouponsThirdCode a = new ActivityCouponsThirdCode();
					a.setCode(code);
					a.setCouponsId(coupons.getId());
					a.setId(UuidUtils.getUuid());
					// 默认为未领取
					a.setStatus(0);
					acList.add(a);
				}
				activityCouponsThirdCodeMapper.saveBatch(acList);
			}
		} else if (CouponsType.film.getValue().equals(coupons.getType())) {
			activityCouponsMapper.insert(coupons);
			this.addRelatedInfo(coupons);
		} else if (CouponsType.bldty.getValue().equals(coupons.getType())) {
			activityCouponsMapper.insert(coupons);
			this.addRelatedInfo(coupons);
		} else if (CouponsType.tyzkq.getValue().equals(coupons.getType())) {
			activityCouponsMapper.insert(coupons);
			this.addRelatedInfo(coupons);
		}
		// Begin added by maojj 2016-10-25
		// 如果代金券设置了需要生成随机码，则根据代金券的发行量生成随机码
		if (coupons.getIsRandCode() == WhetherEnum.whether.ordinal()) {
			// 需要生成随机码，则生成随机码并保存
			generateRandCodeAndSave(coupons);
		}
		// End added by maojj 2016-10-25
	}

	/**
	 * @Description: 根据发行量生成随机码并保存到数据库
	 * @param coupons
	 * @author maojj
	 * @date 2016年10月25日
	 */
	private void generateRandCodeAndSave(ActivityCoupons coupons) {
		// 随机码
		Set<String> randCodeSet = null;
		List<ActivityCouponsRandCode> couponsRandCodeList = null;
		ActivityCouponsRandCode couponsRandCode = null;
		// 总发行量
		int totalNum = coupons.getTotalNum().intValue();
		// 根据总发行量，1000条处理一次的规则生成随机码并保存
		// 循环处理次数
		int loopNum = totalNum % MAX_NUM == 0 ? totalNum / MAX_NUM : totalNum / MAX_NUM + 1;
		// 生成随机码的数量
		int randCodeNum = 0;
		for (int i = 0; i < loopNum; i++) {
			randCodeNum = (i + 1) * MAX_NUM > totalNum ? (totalNum - i * MAX_NUM) : MAX_NUM;
			randCodeSet = new HashSet<String>();
			// 增加随机数
			addRandCode(randCodeSet, randCodeNum);
			couponsRandCodeList = new ArrayList<ActivityCouponsRandCode>();
			for (String randCode : randCodeSet) {
				couponsRandCode = new ActivityCouponsRandCode();
				couponsRandCode.setId(UuidUtils.getUuid());
				couponsRandCode.setCouponsId(coupons.getId());
				couponsRandCode.setRandCode(randCode);
				couponsRandCode.setIsExchange(WhetherEnum.not.ordinal());
				couponsRandCode.setCreateUserId(coupons.getCreateUserId());
				couponsRandCode.setCreateTime(coupons.getCreateTime());
				couponsRandCode.setUpdateUserId(coupons.getUpdateUserId());
				couponsRandCode.setUpdateTime(coupons.getUpdateTime());

				couponsRandCodeList.add(couponsRandCode);
			}
			activityCouponsRandCodeMapper.batchInsert(couponsRandCodeList);
		}
	}

	/**
	 * @Description: 根据发行量生成随机码并保存到数据库(增量)
	 * @param coupons
	 * @author maojj
	 * @date 2016年10月25日
	 */
	private void generateRandCodeAndSaveIncrement(ActivityCoupons coupons, Integer totalNum) {
		// 随机码
		Set<String> randCodeSet = null;
		List<ActivityCouponsRandCode> couponsRandCodeList = null;
		ActivityCouponsRandCode couponsRandCode = null;
		// 总发行量
		// int totalNum = coupons.getTotalNum().intValue();
		// 根据总发行量，1000条处理一次的规则生成随机码并保存
		// 循环处理次数
		int loopNum = totalNum % MAX_NUM == 0 ? totalNum / MAX_NUM : totalNum / MAX_NUM + 1;
		// 生成随机码的数量
		int randCodeNum = 0;
		for (int i = 0; i < loopNum; i++) {
			randCodeNum = (i + 1) * MAX_NUM > totalNum ? (totalNum - i * MAX_NUM) : MAX_NUM;
			randCodeSet = new HashSet<String>();
			// 增加随机数
			addRandCode(randCodeSet, randCodeNum);
			couponsRandCodeList = new ArrayList<ActivityCouponsRandCode>();
			for (String randCode : randCodeSet) {
				couponsRandCode = new ActivityCouponsRandCode();
				couponsRandCode.setId(UuidUtils.getUuid());
				couponsRandCode.setCouponsId(coupons.getId());
				couponsRandCode.setRandCode(randCode);
				couponsRandCode.setIsExchange(WhetherEnum.not.ordinal());
				couponsRandCode.setCreateUserId(coupons.getCreateUserId());
				couponsRandCode.setCreateTime(coupons.getCreateTime());
				couponsRandCode.setUpdateUserId(coupons.getUpdateUserId());
				couponsRandCode.setUpdateTime(coupons.getUpdateTime());

				couponsRandCodeList.add(couponsRandCode);
			}
			activityCouponsRandCodeMapper.batchInsert(couponsRandCodeList);
		}
	}

	/**
	 * @Description: 随机N个不重复的随机数
	 * @param randCodeSet
	 *            随机数的Set集合。利用set的无重复特性
	 * @param randCodeNum
	 *            需要生成的随机数的数量
	 * @author maojj
	 * @date 2016年10月25日
	 */
	private void addRandCode(Set<String> randCodeSet, int randCodeNum) {
		for (int i = 0; i < randCodeNum - randCodeSet.size(); i++) {
			randCodeSet.add(RandomStringUtil.getRandomNumStr(RAND_CODE_LENGTH));
		}
		int setSize = randCodeSet.size();
		// 如果存入Set的数量小于指定生成的个数，则递归调用，直到达到指定数值。
		if (setSize < randCodeNum) {
			addRandCode(randCodeSet, randCodeNum);
		}
		// 查询activity_coupons_rand_code中已经存在的随机数，从集合中剔除，并继续添加随机数
		Set<String> existRandCode = activityCouponsRandCodeMapper.findExistCodeSet(randCodeSet);
		if (CollectionUtils.isNotEmpty(existRandCode)) {
			// 从生成的集合中移除已存在的code
			randCodeSet.removeAll(existRandCode);
			// 如果有已经存的code，移除之后，需要继续添加。
			addRandCode(randCodeSet, randCodeNum);
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
		if (CollectionUtils.isNotEmpty(firstCagegoryList)) {
			StringBuffer sb = new StringBuffer();
			for (String categoryId : firstCagegoryList) {
				if (!"".equals(sb.toString())) {
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
		CouponsInfoQuery c = activityCouponsMapper.selectCouponsById(id);
		if (c != null) {
			// 如果是异业代金券,加载兑换码列表
			if (c.getType() == 4) {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("couponsId", id);
				List<ActivityCouponsThirdCode> list = activityCouponsThirdCodeMapper.listByParam(param);
				c.setThirdCodeList(list);
			}
		}
		return c;
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
		if (CouponsType.bldfwd.getValue().equals(coupons.getType())
				|| CouponsType.hfcz.getValue().equals(coupons.getType())
				|| CouponsType.yyhz.getValue().equals(coupons.getType())
				|| CouponsType.bldyf.getValue().equals(coupons.getType())) {
			activityCouponsMapper.updateCoupons(coupons);
		} else if (CouponsType.bld.getValue().equals(coupons.getType())
				|| CouponsType.fwd.getValue().equals(coupons.getType())
				|| CouponsType.film.getValue().equals(coupons.getType())
				|| CouponsType.bldty.getValue().equals(coupons.getType())
				|| CouponsType.tyzkq.getValue().equals(coupons.getType())) {
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
			activitycoupons.setStartTime(coupons.getStartTime());
			activitycoupons.setEndTime(coupons.getEndTime());
			this.addRelatedInfo(activitycoupons);
		}

		// add by zhangkeneng v2.6
		// 先把老数据删掉
		activityCouponsRandCodeMapper.deleteByCouponsId(coupons.getId());
		// 如果代金券设置了需要生成随机码，则根据代金券的发行量生成随机码
		if (coupons.getIsRandCode() != null && coupons.getIsRandCode() == WhetherEnum.whether.ordinal()) {
			// (由于新增和修改 接收的对象用的不是同一个,只能重新转换一下)
			ActivityCoupons activitycoupons = new ActivityCoupons();
			BeanMapper.copy(coupons, activitycoupons);
			activitycoupons.setUpdateTime(new Date());
			activitycoupons.setCreateTime(new Date());
			activitycoupons.setCreateUserId(coupons.getUpdateUserId());
			activitycoupons.setUpdateUserId(coupons.getUpdateUserId());
			generateRandCodeAndSave(activitycoupons);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateCouponsIng(CouponsInfoQuery coupons) throws ServiceException {
		ActivityCoupons source = activityCouponsMapper.selectById(coupons.getId());
		// 如果代金券设置了需要生成随机码，则根据代金券的发行量生成随机码(增量)
		if (source.getIsRandCode() != null && source.getIsRandCode() == WhetherEnum.whether.ordinal()) {
			// (由于新增和修改 接收的对象用的不是同一个,只能重新转换一下)
			ActivityCoupons activitycoupons = new ActivityCoupons();
			BeanMapper.copy(coupons, activitycoupons);
			activitycoupons.setUpdateTime(new Date());
			activitycoupons.setCreateTime(new Date());
			activitycoupons.setCreateUserId(coupons.getUpdateUserId());
			activitycoupons.setUpdateUserId(coupons.getUpdateUserId());

			// 增量的数字 是新填入的总发行量 减去 原数据的总发行量,如果为0,就不用重新生成了
			if (source != null && source.getTotalNum() != null && coupons.getTotalNum() > source.getTotalNum()) {
				generateRandCodeAndSaveIncrement(activitycoupons, coupons.getTotalNum() - source.getTotalNum());
			}
		}

		// 更新基本信息
		activityCouponsMapper.updateCoupons(coupons);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(CouponsInfoQuery coupons) throws ServiceException {
		activityCouponsMapper.updateCoupons(coupons);
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
		 * if (category > 0) { couponsManageService.deleteCouponsLimitCategory(id); }
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

		// 如果是异业合作代金券,要把关联的兑换码都删除 (异业的现在2.0.0不需要)
		// activityCouponsThirdCodeMapper.deleteByCouponsId(id);
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
							params.put("auditStatus", AuditStatusEnum.pass_verify);
							params.put("cityId", areas[0]);
							// 对应枚举 CouponsType 0：便利店和服务店，1：便利店 2 服务店 3 充值
							if (coupons.getType() == 1) {
								// 查询便利店店铺
								params.put("type", StoreTypeEnum.CLOUD_STORE);
							} else if (coupons.getType() == 2) {
								// 查询服务店店铺
								params.put("type", StoreTypeEnum.SERVICE_STORE);
							}
							// storeInfos =
							// storeInfoServiceApi.getByProvinceIdAndCityId(params);
							// storeInfoList.addAll(storeInfos);
						} else if ("1".equals(areas[1])) {
							activityCouponsArea.setCouponsAreaType(DistrictType.province);
							Map<String, Object> params = new HashMap<>();
							params.put("disabled", Disabled.valid);
							params.put("auditStatus", AuditStatusEnum.pass_verify);
							params.put("provinceId", areas[0]);
							// 对应枚举 CouponsType 0：便利店和服务店，1：便利店 2 服务店 3 充值
							if (coupons.getType() == 1) {
								// 查询便利店店铺
								params.put("type", StoreTypeEnum.CLOUD_STORE);
							} else if (coupons.getType() == 2) {
								// 查询服务店店铺
								params.put("type", StoreTypeEnum.SERVICE_STORE);
							}
							// storeInfos =
							// storeInfoServiceApi.getByProvinceIdAndCityId(params);
							// storeInfoList.addAll(storeInfos);
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
			if (StringUtils.isNotBlank(coupons.getAreaIds())) {
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
			if (StringUtils.isNotBlank(coupons.getAreaIds())) {
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
		if (CouponsType.bld.getValue().equals(coupons.getType())
				|| CouponsType.bldty.getValue().equals(coupons.getType())) {
			type = 1;
		} else if (CouponsType.fwd.getValue().equals(coupons.getType())
				|| CouponsType.tyzkq.getValue().equals(coupons.getType())) {
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
					storeInfo.put("storeName", map.get("storeName") == null ? "" : map.get("storeName"));
					storeInfo.put("storeAddress", map.get("storeAddress") == null ? "" : map.get("storeAddress"));
					storeList.add(storeInfo);

					resultMap.put(addressCity.getName(), storeList);
				}
			}
		}
		return resultMap;
	}

	@Override
	public List<AddressCityVo> getAddressByCouponsId(String couponsId) throws ServiceException {
		// 存放省信息,key是省id，value是省名称
		// 根据代金券的区域类型 查询省的名称
		Map<String, Object> provinceMap = new HashMap<String, Object>();
		ActivityCoupons activityCoupons = activityCouponsMapper.selectByPrimaryKey(couponsId);
		List<AddressCityVo> result = new ArrayList<AddressCityVo>();
		if (activityCoupons != null) {
			if (activityCoupons.getAreaType() == AreaType.national) {
				AddressCityVo address = new AddressCityVo();
				provinceMap.put("0", "全国");
				address.setId("0");
				address.setCityName("全国");
			} else if (activityCoupons.getAreaType() == AreaType.store) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("couponsId", couponsId);
				// 根据代金券id，查询省份id
				List<Map<String, Object>> provinceIdList = couponsRelationStoreMapper
						.selectAddressRelationProvinceByParams(params);
				if (provinceIdList != null && provinceIdList.size() > 0) {
					// AreaResponseVo areaResponseVo = new AreaResponseVo();
					// 循环省份id，查询出省名称，并且确定省名称唯一
					for (Map<String, Object> map : provinceIdList) {
						AddressCityVo address = new AddressCityVo();
						if (map != null && map.get("provinceId") != null
								&& !"".equals(map.get("provinceId").toString())) {
							Address addressProvince = addressService
									.getAddressById(Long.valueOf(map.get("provinceId").toString()));
							if (addressProvince != null
									&& !provinceMap.containsKey(addressProvince.getId().toString())) {
								provinceMap.put(addressProvince.getId().toString(), addressProvince.getName());
								address.setCityName(addressProvince.getName());
								address.setId(addressProvince.getId().toString());
								result.add(address);
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
						AddressCityVo address = new AddressCityVo();
						Address addressInfo = null;
						if (area.getCouponsAreaType() == DistrictType.city) {
							addressInfo = addressService.getAddressById(Long.valueOf(area.getAreaId().toString()));
							if (addressInfo != null && !provinceMap.containsKey(addressInfo.getParentId().toString())) {
								provinceMap.put(addressInfo.getParentId().toString(), addressInfo.getParentName());
								address.setCityName(addressInfo.getParentName());
								address.setId(addressInfo.getParentId().toString());
								result.add(address);
							}
						} else if (area.getCouponsAreaType() == DistrictType.province) {
							addressInfo = addressService.getAddressById(Long.valueOf(area.getAreaId().toString()));
							if (addressInfo != null && !provinceMap.containsKey(addressInfo.getId().toString())) {
								provinceMap.put(addressInfo.getId().toString(), addressInfo.getName());
								address.setCityName(addressInfo.getName());
								address.setId(addressInfo.getId().toString());
								result.add(address);
							}
						}
					}

				}
			}
		}
		return result;
	}

	/**
	 * 获取最近一个进行中的注册活动的代金券活动关联的代金券集合
	 * 
	 * @param map
	 *            有以下key type 0代金券领取活动，1注册活动
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
		return cou >= ids.size();
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
					 * else if (area.getCouponsAreaType() == DistrictType.province) { cityMap.put("0", "所属范围内都可以用"); }
					 */
			}

		}
		return cityMap;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ActivityCoupons> selectByActivityId(String id) throws Exception {
		List<ActivityCoupons> result = activityCouponsMapper.selectByActivityId(id);
		// 如果是按天类型的，把开始时间与结束时间也计算一下
		if (CollectionUtils.isNotEmpty(result)) {
			for (ActivityCoupons activityCoupons : result) {
				if (activityCoupons.getTermType() == ActivityCouponsTermType.BY_DAY) {
					activityCoupons.setStartTime(activityCouponsReceiveStrategy.getEffectTime(activityCoupons));
					activityCoupons.setEndTime(activityCouponsReceiveStrategy.getExpireTime(activityCoupons));
				}
			}
		}
		return result;
	}

	@Transactional(readOnly = true)
	@Override
	public int selectFaceMoney(String id) throws Exception {
		int result = activityCouponsMapper.selectFaceMoney(id);
		return result;
	}

	@Override
	public int findByExchangeCode(String exchangeCode) throws Exception {
		ActivityCoupons activity = activityCouponsMapper.selectByExchangeCode(exchangeCode);
		if (activity != null) {
			return 1;
		} else {
			return 0;
		}

	}

	@Override
	public int findByRandCode(String exchangeCode) throws Exception {
		return activityCouponsMapper.selectByRandCode(exchangeCode);
	}

	@Override
	public String findCityStoreList(String couponsId, String provinceId) throws ServiceException {
		// 存放市及下面的店铺信息，最外层map的key是市名称，map的value是小店铺名称和店铺地址的集合list
		Map<String, List<Map<String, Object>>> resultMap = new HashMap<String, List<Map<String, Object>>>();

		// 根据省份id、代金券id，查询出市id、店铺名称和店铺名称
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("couponsId", couponsId);
		params.put("provinceId", provinceId);
		List<Map<String, Object>> cityStoreList = couponsRelationStoreMapper.selectAddressRelationStoreByParams(params);
		List<AddressCityVo> listVo = new ArrayList<AddressCityVo>();
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
					storeInfo.put("storeName", map.get("storeName") == null ? "" : map.get("storeName"));
					storeInfo.put("storeAddress", map.get("storeAddress") == null ? "" : map.get("storeAddress"));
					storeList.add(storeInfo);
					/* resultMap.put(addressCity.getName(), storeList); */
					AddressCityVo addressCityVo = new AddressCityVo();
					addressCityVo.setStores(storeList);
					addressCityVo.setId(addressCity.getId().toString());
					addressCityVo.setCityName(addressCity.getName());
					listVo.add(addressCityVo);
				}
			}
		}
		String json = JsonMapper.nonDefaultMapper().toJson(listVo);
		return json;
	}

	@Override
	public String findCityList(String couponsId, String provinceId) throws ServiceException {
		Map<String, Object> cityMap = new HashMap<String, Object>();
		CouponsInfoQuery couponsInfo = null;
		couponsInfo = activityCouponsMapper.selectCouponsById(couponsId);
		List<ActivityCouponsArea> areaList = couponsInfo.getActivityCouponsAreaList();
		List<Address> cityList = new ArrayList<Address>();
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
								cityList.add(addressInfo);
							}
						}
					}
				}
			}

		}
		String json = JsonMapper.nonDefaultMapper().toJson(cityList);
		return json;
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

	// Begin V2.1.0 added by luosm 20170220
	@Override
	public ActivityCoupons findByOrderId(String orderId) throws ServiceException {
		return activityCouponsMapper.findByOrderId(orderId);
	}
	// End V2.1.0 added by luosm 20170220

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void takeCoupons(ActivityCollectCoupons activityCollectCoupons,
			TakeActivityCouponParamDto activityCouponParamDto) throws Exception {
		List<ActivityCoupons> activityCouponList = getActivityCoupons(activityCollectCoupons.getId());
		for (ActivityCoupons activityCoupons : activityCouponList) {
			try {
				checkActivityCouponsRule(activityCoupons, activityCouponParamDto);
				updateCouponsAndAddCouponsRecord(activityCoupons, activityCouponParamDto);
			} catch (Exception e) {
				logger.error("领取带金卷出错", e);
				if (activityCouponParamDto.isContinueTakeOther()) {
					continue;
				} else {
					throw e;
				}
			}
		}
	}

	private void updateCouponsAndAddCouponsRecord(ActivityCoupons activityCoupons,
			TakeActivityCouponParamDto activityCouponParamDto) throws Exception {
		// 更新代金劵数量
		updateCouponsNum(activityCoupons.getId());
		// 添加领取记录
		ActivityCouponsRecord activityCouponsRecord = createActivityCouponsRecord(activityCoupons,
				activityCouponParamDto);
		activityCouponsRecord.setCollectType(activityCouponParamDto.getActivityCouponsType());
		if (StringUtils.isNotEmpty(activityCouponParamDto.getUserId())) {
			// 用户已经存在
			activityCouponsRecordService.add(activityCouponsRecord);
		} else {
			// 用户还没有注册，添加预领取记录
			ActivityCouponsRecordBefore activityCouponsRecordBefore = BeanMapper.map(activityCouponsRecord,
					ActivityCouponsRecordBefore.class);
			activityCouponsRecordBefore.setCollectUser(activityCouponParamDto.getMobile());
			activityCouponsRecordBeforeService.add(activityCouponsRecordBefore);
		}
	}

	private void checkActivityCouponsRule(ActivityCoupons activityCoupons,
			TakeActivityCouponParamDto activityCouponParamDto) throws Exception {
		if (activityCoupons.getRemainNum() <= 0) {
			// 代金劵数量不够了
			throw new Exception("您来晚了一步，代经卷已经被领取完了");
		}
		int drawCount = 0;
		ActivityCouponsRecordQueryParamDto activityCouponsRecordQueryParam = new ActivityCouponsRecordQueryParamDto();
		activityCouponsRecordQueryParam.setCollectStartTime(DateUtils.getDateStart(new Date()));
		activityCouponsRecordQueryParam.setCollectEndTime(DateUtils.getDateEnd(new Date()));
		activityCouponsRecordQueryParam.setCouponsCollectId(activityCouponParamDto.getActivityId());
		activityCouponsRecordQueryParam.setCouponsId(activityCoupons.getId());
		if (StringUtils.isNotEmpty(activityCouponParamDto.getUserId())) {
			activityCouponsRecordQueryParam.setCollectUserId(activityCouponParamDto.getUserId());
			drawCount = activityCouponsRecordService.selectCountByParams(activityCouponsRecordQueryParam);
		} else {
			ActivityCouponsRecordBeforeParamDto activityCouponsRecordBeforeParam = BeanMapper
					.map(activityCouponsRecordQueryParam, ActivityCouponsRecordBeforeParamDto.class);
			activityCouponsRecordBeforeParam.setCollectUser(activityCouponParamDto.getMobile());
			drawCount = activityCouponsRecordBeforeService.selectCountByParam(activityCouponsRecordBeforeParam);
		}
		if (drawCount >= activityCoupons.getEveryLimit()) {
			throw new Exception("不要贪心哦，您已经领取了" + activityCoupons.getEveryLimit() + "张代金劵");
		}
	}

	private void updateCouponsNum(String activityCouponId) throws Exception {
		ActivityCouponsBo activityCouponsBo = new ActivityCouponsBo();
		activityCouponsBo.setId(activityCouponId);
		activityCouponsBo.setRemainNum(-1);
		activityCouponsBo.setUsedNum(1);
		activityCouponsBo.setUpdateTime(new Date());
		int result = activityCouponsMapper.updateCouponsNum(activityCouponsBo);
		if (result < 1) {
			throw new Exception("您来晚了一步,代金劵已经被领完了");
		}
	}

	private ActivityCouponsRecord createActivityCouponsRecord(ActivityCoupons activityCoupons,
			TakeActivityCouponParamDto activityCouponParamDto) {
		ActivityCouponsRecord activityCouponsRecord = new ActivityCouponsRecord();
		activityCouponsRecord.setCollectTime(new Date());
		activityCouponsRecord.setId(UuidUtils.getUuid());
		activityCouponsRecord.setCouponsId(activityCoupons.getId());
		activityCouponsRecord.setCouponsCollectId(activityCoupons.getActivityId());
		activityCouponsRecord.setCollectUserId(activityCouponParamDto.getUserId());
		activityCouponsRecord.setStatus(ActivityCouponsRecordStatusEnum.UNUSED);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_YEAR, activityCoupons.getValidDay());
		activityCouponsRecord.setValidTime(calendar.getTime());
		activityCouponsReceiveStrategy.process(activityCouponsRecord, activityCoupons);
		return activityCouponsRecord;
	}

	@Override
	public ActivityCouponsDto findDetailById(String id, ActivityCouponsQueryParamDto activityCouponsQueryParamDto) {
		ActivityCoupons activityCoupons = activityCouponsMapper.selectByPrimaryKey(id);
		if (activityCoupons == null) {
			return null;
		}
		ActivityCouponsDto dto = BeanMapper.map(activityCoupons, ActivityCouponsDto.class);

		dto.setStartTime(activityCouponsReceiveStrategy.getEffectTime(activityCoupons));
		// 代金券失效时间
		Date expireTime = activityCouponsReceiveStrategy.getExpireTime(activityCoupons);
		dto.setEndTime(expireTime);
		
		if (activityCoupons.getIsCategory() == 1 && activityCouponsQueryParamDto.isQueryCategory()) {
			List<ActivityCouponsCategory> categoryList = queryActivityCouponsCategory(activityCoupons);
			dto.setActivityCouponsCategory(categoryList);
			String categoryNames = mergeCategoryNames(dto);
			dto.setCategoryNames(categoryNames);
		}
		if (activityCouponsQueryParamDto.isQueryArea()) {
			if (activityCoupons.getAreaType() == AreaType.area) {
				ActivityCouponsArea activityCouponsArea = new ActivityCouponsArea();
				activityCouponsArea.setCouponsId(id);
				List<ActivityCouponsArea> areaList = activityCouponsAreaMapper.findLimitAreaList(activityCouponsArea);
				if (CollectionUtils.isNotEmpty(areaList)) {
					dto.setActivityCouponsAreaList(BeanMapper.mapList(areaList, ActivityCouponsAreaDto.class));
				}
			} else if (activityCoupons.getAreaType() == AreaType.store) {
				CouponsRelationStoreParamBo paramBo = new CouponsRelationStoreParamBo();
				paramBo.setCouponsId(id);
				List<ActivityCouponsRelationStore> storeList = couponsRelationStoreMapper.findlist(paramBo);
				if (CollectionUtils.isNotEmpty(storeList)) {
					dto.setActivityCouponsRelationStoreList(
							BeanMapper.mapList(storeList, ActivityCouponsRelationStoreDto.class));
				}
			}
		}
		String faceValueShowName = getCouponsFaceValueShowName(activityCoupons);
		dto.setFaceValueShowName(faceValueShowName);
		return dto;
	}

	/**
	 * @Description: 查询代金卷关联分类信息
	 * @param activityCoupons
	 * @return
	 * @author zengjizu
	 * @date 2017年11月15日
	 */
	private List<ActivityCouponsCategory> queryActivityCouponsCategory(ActivityCoupons activityCoupons) {
		// 查询分类信息
		ActivityCouponsCategoryParamBo activityCouponsCategoryParamBo = new ActivityCouponsCategoryParamBo();
		activityCouponsCategoryParamBo.setCouponId(activityCoupons.getId());
		List<ActivityCouponsCategory> categoryList = activityCouponsCategoryMapper
				.findList(activityCouponsCategoryParamBo);
		List<String> navigateCategoryIdList = Lists.newArrayList();
		List<String> spuCategoryIdList = Lists.newArrayList();
		categoryList.forEach(e -> {
			if (e.getType() == 1 && !navigateCategoryIdList.contains(e.getCategoryId())) {
				navigateCategoryIdList.add(e.getCategoryId());
			} else if (e.getType() == 2 && !spuCategoryIdList.contains(e.getCategoryId())) {
				spuCategoryIdList.add(e.getCategoryId());
			}
		});

		if (CollectionUtils.isNotEmpty(navigateCategoryIdList)) {
			GoodsNavigateCategoryParamDto goodsNavigateCategoryParamDto = new GoodsNavigateCategoryParamDto();
			goodsNavigateCategoryParamDto.setIdList(navigateCategoryIdList);
			goodsNavigateCategoryParamDto.setSort(false);
			List<GoodsNavigateCategoryDto> navigateCategoryList = goodsNavigateCategoryServiceApi
					.findList(goodsNavigateCategoryParamDto);

			Map<String, String> navigateCategoryMap = navigateCategoryList.stream()
					.collect(Collectors.toMap(GoodsNavigateCategoryDto::getId, GoodsNavigateCategoryDto::getName));
			categoryList.forEach(e -> {
				if (e.getType() == 1) {
					e.setCategoryName(navigateCategoryMap.get(e.getCategoryId()));
				}
			});
		}

		if (CollectionUtils.isNotEmpty(spuCategoryIdList)) {
			GoodsSpuCategoryParamDto paramDto = new GoodsSpuCategoryParamDto();
			paramDto.setIdList(spuCategoryIdList);
			List<GoodsSpuCategory> navigateCategoryList = goodsSpuCategoryServiceApi.findSpuCategoryByParam(paramDto);

			Map<String, String> navigateCategoryMap = navigateCategoryList.stream()
					.collect(Collectors.toMap(GoodsSpuCategory::getId, GoodsSpuCategory::getName));
			categoryList.forEach(e -> {
				if (e.getType() == 2) {
					e.setCategoryName(navigateCategoryMap.get(e.getCategoryId()));
				}
			});
		}
		return categoryList;
	}

	private String mergeCategoryNames(ActivityCouponsDto activityCoupons) {
		// 根据代金卷类型判断使用的分类
		StringBuilder categoryNames = new StringBuilder();
		List<ActivityCouponsCategory> cates = activityCoupons.getActivityCouponsCategory();
		if (CollectionUtils.isNotEmpty(cates)) {
			for (ActivityCouponsCategory category : cates) {
				categoryNames.append(category.getCategoryName() + "、");
			}
			if (categoryNames.length() > 0) {
				return categoryNames.toString().substring(0, categoryNames.length() - 1);
			}
		}
		return null;
	}

	private String getCouponsFaceValueShowName(ActivityCoupons activityCoupons) {
		if (activityCoupons.getType() == CouponsType.tyzkq.getValue()) {
			BigDecimal bg = new BigDecimal(activityCoupons.getFaceValue()).divide(new BigDecimal("10"));
			return bg.toString();
		} else {
			return "" + activityCoupons.getFaceValue();
		}
	}
}
