package com.okdeer.mall.activity.discount.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.base.entity.GoodsSpuCategory;
import com.okdeer.archive.goods.base.service.GoodsSpuCategoryServiceApi;
import com.okdeer.archive.goods.dto.StoreSkuParamDto;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.archive.goods.store.dto.StoreSkuDto;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.enums.IsActivity;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.stock.dto.StockUpdateDetailDto;
import com.okdeer.archive.stock.dto.StockUpdateDto;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.service.GoodsStoreSkuStockApi;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.enums.StoreActivityTypeEnum;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.bdp.address.entity.Address;
import com.okdeer.bdp.address.service.IAddressService;
import com.okdeer.common.entity.ReturnInfo;
import com.okdeer.common.utils.EnumAdapter;
import com.okdeer.mall.activity.bo.ActLimitRelBuilder;
import com.okdeer.mall.activity.bo.ActivityParamBo;
import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.enums.CashDelivery;
import com.okdeer.mall.activity.discount.entity.ActivityBusinessRel;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountCondition;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountGroup;
import com.okdeer.mall.activity.discount.enums.ActivityBusinessType;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountStatus;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountType;
import com.okdeer.mall.activity.discount.mapper.ActivityBusinessRelMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountConditionMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountGroupMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.activity.dto.ActivityBusinessRelDto;
import com.okdeer.mall.activity.dto.ActivityInfoDto;
import com.okdeer.mall.activity.dto.ActivityParamDto;
import com.okdeer.mall.activity.dto.ActivityPinMoneyDto;
import com.okdeer.mall.activity.seckill.vo.ActivitySeckillFormVo;
import com.okdeer.mall.activity.service.FavourFilterStrategy;
import com.okdeer.mall.activity.service.MaxFavourStrategy;
import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.mall.common.enums.UseUserType;
import com.okdeer.mall.common.utils.RobotUserUtil;
import com.okdeer.mall.order.vo.FullSubtract;
import com.okdeer.mall.system.mq.RollbackMQProducer;
import com.okdeer.mall.system.utils.ConvertUtil;

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
@Service
public class ActivityDiscountServiceImpl extends BaseServiceImpl implements ActivityDiscountService {

	@Resource
	private ActivityDiscountMapper activityDiscountMapper;
	
	@Resource
	private ActivityDiscountConditionMapper activityDiscountConditionMapper;
	
	@Resource
	private ActivityBusinessRelMapper activityBusinessRelMapper;
	
	@Resource
	private MaxFavourStrategy maxFavourStrategy;
	
	@Reference(version = "1.0.0",check=false)
	private IAddressService addressApi;
	
	@Reference(version = "1.0.0",check=false)
	private StoreInfoServiceApi storeInfoServiceApi;
	
	@Reference(version = "1.0.0",check=false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;
	
	@Reference(version = "1.0.0",check=false)
	private GoodsSpuCategoryServiceApi goodsSpuCategoryServiceApi;
	
	/**
	* 注入库存API接口
	*/
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuStockApi goodsStoreSkuStockApi;
	
	/**
	 * 回滚MQ
	 */
	@Autowired
	RollbackMQProducer rollbackMQProducer;
	
	@Resource
	private MaxFavourStrategy genericMaxFavourStrategy;
	@Resource
	private ActivityDiscountGroupMapper activityDiscountGroupMapper;
	@Override
	public IBaseMapper getBaseMapper() {
		return activityDiscountMapper;
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public ReturnInfo update(ActivityInfoDto actInfoDto) throws Exception {
		// 初始化ReturnInfo
		ReturnInfo retInfo = new ReturnInfo();
		// 活动信息
		ActivityDiscount actInfo = actInfoDto.getActivityInfo();
		String activityId = actInfo.getId();
		ActivityDiscount currentAct = activityDiscountMapper.findById(activityId);
		if(currentAct.getStatus() != ActivityDiscountStatus.noStart){
			retInfo.setFlag(false);
			retInfo.setMessage("该满减活动处于" + currentAct.getStatus().getValue() + "状态下，不能修改！");
		}
		// 同一时间、同一地区、同一店铺活动唯一性检查。 团购不检查可以创建
		if(actInfo.getType() !=ActivityDiscountType.GROUP_PURCHASE && !checkUnique(actInfoDto)){
			retInfo.setFlag(false);
			retInfo.setMessage("创建失败，选定范围指定时间内已存在活动，请重新选择范围或更改时间！");
			return retInfo;
		}
		
		// 限制条件列表
		List<ActivityBusinessRel> limitList = parseLimitList(actInfoDto);
		if(actInfo.getType() != ActivityDiscountType.PIN_MONEY){
			actInfo.setGrantType(0);
		}
		// 活动限制首单用户，默认用户参与总次数为1.
		if(actInfo.getLimitUser() == UseUserType.ONlY_NEW_USER){
			actInfo.setLimitTotalFreq(1);
		}
		// 修改活动信息
		activityDiscountMapper.update(actInfo);
		
		// 删除活动下的优惠条件
		activityDiscountConditionMapper.deleteByActivityId(activityId);
		//存在优惠条件列表 满减及零花钱存在
		if(CollectionUtils.isNotEmpty(actInfoDto.getConditionList())){
			// 优惠条件列表
			List<ActivityDiscountCondition> conditionList = parseConditionList(actInfoDto);
			// 批量新增优惠条件列表
			activityDiscountConditionMapper.batchAdd(conditionList);
		}
		
		// 删除活动下的业务关联关系
		activityBusinessRelMapper.deleteByActivityId(activityId);
		// 批量新增活动限制信息
		if(CollectionUtils.isNotEmpty(limitList)){
			activityBusinessRelMapper.batchAdd(limitList);
		}
		
		// 删除团购活动下的团购商品集合
		activityDiscountGroupMapper.deleteByActivityId(activityId);
		//团购商品集合 批量保存
		List<ActivityDiscountGroup> groupGoodsList =  actInfoDto.getGroupGoodsList();
		
		// 释放原来商品活动库存 + 解除原商品关联关系
		removeSkuAndStockRelation(actInfo.getId());
		if(CollectionUtils.isNotEmpty(groupGoodsList)){
			activityDiscountGroupMapper.batchAdd(groupGoodsList);
			// 保存团购活动商品关系信息 + 锁定团购活动库存信息
			addSkuAndStockRelation(actInfo, groupGoodsList);
		}
		return retInfo;
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public ReturnInfo add(ActivityInfoDto actInfoDto) throws Exception {
		// 初始化ReturnInfo
		ReturnInfo retInfo = new ReturnInfo();
		// 活动信息
		ActivityDiscount actInfo = actInfoDto.getActivityInfo();
		// 同一时间、同一地区、同一店铺活动唯一性检查。
		if(actInfo.getType() !=ActivityDiscountType.GROUP_PURCHASE && !checkUnique(actInfoDto)){
			retInfo.setFlag(false);
			retInfo.setMessage("创建失败，选定范围指定时间内已存在活动，请重新选择范围或更改时间！");
			return retInfo;
		}
		
		// 活动状态默认为未开始
		actInfo.setStatus(ActivityDiscountStatus.noStart);
		if(actInfo.getType() != ActivityDiscountType.PIN_MONEY){
			actInfo.setGrantType(0);
		}
		// 活动限制首单用户，默认用户参与总次数为1.
		if(actInfo.getLimitUser() == UseUserType.ONlY_NEW_USER){
			actInfo.setLimitTotalFreq(1);
		}
		
		// 新增活动信息
		activityDiscountMapper.add(actInfo);
		if(CollectionUtils.isNotEmpty(actInfoDto.getConditionList())){
			// 优惠条件列表
			List<ActivityDiscountCondition> conditionList = parseConditionList(actInfoDto);
			// 批量新增优惠条件列表
			activityDiscountConditionMapper.batchAdd(conditionList);
		}
		
		// 限制条件列表
		List<ActivityBusinessRel> limitList = parseLimitList(actInfoDto);
		// 批量新增活动限制信息
		if(CollectionUtils.isNotEmpty(limitList)){
			activityBusinessRelMapper.batchAdd(limitList);
		}
		
		//团购商品集合 批量保存
		List<ActivityDiscountGroup> groupGoodsList =  actInfoDto.getGroupGoodsList();
		if(CollectionUtils.isNotEmpty(groupGoodsList)){
			activityDiscountGroupMapper.batchAdd(groupGoodsList);
			
			// 保存团购活动商品关系信息 + 锁定团购活动库存信息
			addSkuAndStockRelation(actInfo, groupGoodsList);
		}
		
		return retInfo;
	}
	
	/**
	 * @Description: 添加商品与活动的关联关系，更新店铺SKU表
	 * @param skuId 店铺skuId
	 * @param acvitityId 活动id
	 * @param rpcIdBySkuList RPCID
	 * @author tuzhd
	 * @date 2017年10月13日
	 */
	private void addSkuAndStockRelation(ActivityDiscount actInfo,List<ActivityDiscountGroup> groupList) throws Exception {
		List<String> rpcIdBySkuList = new ArrayList<>();
		List<String> rpcIdByStockList = new ArrayList<>();
		try{
			if(CollectionUtils.isNotEmpty(groupList)){
				for(ActivityDiscountGroup group : groupList){
					rpcIdBySkuList.add(
							goodsStoreSkuServiceApi.addSkuRelation(group.getStoreSkuId(), actInfo.getId(), 
									StoreActivityTypeEnum.GROUP_BY,this.getClass().getName() + ".addSkuAndStock")
						);
					rpcIdByStockList.add(
							goodsStoreSkuStockApi.lockActivitySkuStock(actInfo.getUpdateUserId(),group.getStoreSkuId(),group.getGoodsCountLimit(), 
									 this.getClass().getName() + ".addSkuAndStock",ActivityTypeEnum.GROUP_ACTIVITY)
							);
				}
			}
		} catch (Exception e) {
			rollbackMQProducer.sendStockRollbackMsg(rpcIdByStockList);
			rollbackMQProducer.sendSkuRollbackMsg(rpcIdBySkuList);
			throw e;
		}
	}
	
	/**
	 * @Description: 解除商品与活动的关联关系
	 * @param activityId 活动Id
	 * @throws Exception 抛出异常
	 * @author tuzhd
	 * @date 2016年10月13日
	 */
	private void removeSkuAndStockRelation(String activityId) throws Exception {
		List<String> rpcIdBySkuList = new ArrayList<>();
		List<String> rpcIdByStockList = new ArrayList<>();
		try{
			//查询团购活动商品
			List<ActivityDiscountGroup> oldGoodsList = activityDiscountGroupMapper.findByActivityId(activityId);
			if(CollectionUtils.isNotEmpty(oldGoodsList)){
				for(ActivityDiscountGroup group : oldGoodsList){
					rpcIdBySkuList.add(
							goodsStoreSkuServiceApi.removeSkuRelation(group.getStoreSkuId(), 
							this.getClass().getName() + ".removeSkuAndStock")
						);
					
					rpcIdByStockList.add(
							goodsStoreSkuStockApi.releaseActivitySkuStock(group.getStoreSkuId(), 
							this.getClass().getName() + ".removeSkuAndStock")
						);
				}
			}
		} catch (Exception e) {
			rollbackMQProducer.sendStockRollbackMsg(rpcIdByStockList);
			rollbackMQProducer.sendSkuRollbackMsg(rpcIdBySkuList);
			throw e;
		}
	}
	
	/**
	 * @Description: 活动唯一性校验
	 * @param actInfoDto
	 * @return   
	 * @author maojj
	 * @date 2017年4月21日
	 */
	private boolean checkUnique(ActivityInfoDto actInfoDto){
		ActivityDiscount actInfo = actInfoDto.getActivityInfo();
		ActivityParamBo paramBo = BeanMapper.map(actInfo, ActivityParamBo.class);
		if(StringUtils.isNotEmpty(actInfo.getId())){
			paramBo.setExcludedId(actInfo.getId());
		}
		paramBo.setLimitRange(actInfo.getLimitRange());
		if(StringUtils.isNotEmpty(actInfoDto.getLimitRangeIds())){
			String[] tempArr = actInfoDto.getLimitRangeIds().split(",");
			paramBo.setLimitRangeIds(Arrays.asList(tempArr));
		}
		int count = activityDiscountMapper.countConflict(paramBo);
		return count < 1;
	}
	
	private List<ActivityDiscountCondition> parseConditionList(ActivityInfoDto actInfoDto){
		List<ActivityDiscountCondition> conditionList = actInfoDto.getConditionList();
		ActivityDiscountType activityType = actInfoDto.getActivityInfo().getType();
		String activityId = actInfoDto.getActivityInfo().getId();
		// 最小区间
		int minSection = 0;
		// 最大区间
		int maxSection = 0;
		// 上一个最大区间值
		int lastMaxSection = 0;
		int sort = 0;
		for(ActivityDiscountCondition condition : conditionList){
			condition.setId(UuidUtils.getUuid());
			condition.setDiscountId(activityId);
			if(activityType == ActivityDiscountType.PIN_MONEY){
				// 如果是零钱活动，需要根据百分比计算百分比区间.百分比必须为整数，所以百分比区间值按照100进行划分。
				// 如优惠条件百分比分配为10%，40%，50%。则对应区间为：[1~10],[11~50],[51~100]
				minSection = lastMaxSection + 1;
				maxSection = lastMaxSection + condition.getRate();
				lastMaxSection = maxSection;
				condition.setMinSection(minSection);
				condition.setMaxSection(maxSection);
				condition.setArrive(BigDecimal.valueOf(0.00));
				condition.setDiscount(BigDecimal.valueOf(0.00));
			}else{
				// 设置默认值
				condition.setMinAmount(BigDecimal.valueOf(0.00));
				condition.setMaxAmount(BigDecimal.valueOf(0.00));
				condition.setRate(0);
				condition.setMinSection(0);
				condition.setMaxSection(0);
			}
			condition.setSort(sort++);
		}
		return conditionList;
	}
	
	/**
	 * @Description: 解析限制条件列表，为限制条件生成主键Id
	 * @param actInfoDto
	 * @return   
	 * @author maojj
	 * @date 2017年4月18日
	 */
	private List<ActivityBusinessRel> parseLimitList(ActivityInfoDto actInfoDto){
		// 限制条件列表
		List<ActivityBusinessRel> limitList = Lists.newArrayList();
		List<ActivityBusinessRelDto<Object>> relDtoList = actInfoDto.getRelDtoList();
		String activityId = actInfoDto.getActivityInfo().getId();
		// 活动业务关联关系（限制关系）
		ActivityBusinessRel limitRel = null;
		int sort = 0;
		for(ActivityBusinessRelDto<?> relDto : relDtoList){
			limitRel = BeanMapper.map(relDto, ActivityBusinessRel.class);
			limitRel.setId(UuidUtils.getUuid());
			limitRel.setActivityId(activityId);
			limitRel.setSort(sort++);
			limitList.add(limitRel);
		}
		return limitList;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void updateStatus() throws Exception {
		// 当前时间
		Date currentDate = DateUtils.getSysDate();
		// 查询需要更新状态的集合
		List<ActivityDiscount> actList = activityDiscountMapper.findNeedUpdateList(currentDate);
		if(CollectionUtils.isEmpty(actList)){
			return;
		}
		// 状态变更调度，需要将未开始的进行开始，已关闭的进行关闭。
		ActivityParamBo paramBo = new ActivityParamBo();
		paramBo.setUpdateTime(currentDate);
		paramBo.setUpdateUserId(RobotUserUtil.getRobotUser().getId());
		// 需要设置为进行中的活动Id列表
		List<String> ingList = Lists.newArrayList();
		// 需要设置为已结速的活动Id列表
		List<String> endList = Lists.newArrayList();
		for(ActivityDiscount act : actList){
			if(act.getStatus() == ActivityDiscountStatus.ing){
				ingList.add(act.getId());
			}else if(act.getStatus() == ActivityDiscountStatus.end){
				endList.add(act.getId());
				// 释放原来商品活动库存 + 解除原商品关联关系
				removeSkuAndStockRelation(act.getId());
			}
		}
		// 更新进行中的活动
		if(CollectionUtils.isNotEmpty(ingList)){
			paramBo.setStatus(ActivityDiscountStatus.ing);
			paramBo.setActivityIds(ingList);
			activityDiscountMapper.updateStatus(paramBo);
		}
		// 更新需要结束的活动
		if(CollectionUtils.isNotEmpty(endList)){
			paramBo.setStatus(ActivityDiscountStatus.end);
			paramBo.setActivityIds(endList);
		}
		activityDiscountMapper.updateStatus(paramBo);
	}

	@Override
	public List<Map<String, Object>> findActivityDiscountByStoreId(Map<String, Object> params) {
		return activityDiscountMapper.findActivityDiscountByStoreId(params);
	}

	@Override
	public List<ActivityDiscount> findListByParam(ActivityParamDto paramDto) {
		if(paramDto.getPageNumber() != null && paramDto.getPageSize() != null){
			PageHelper.startPage(paramDto.getPageNumber(),paramDto.getPageSize());
		}
		return activityDiscountMapper.findListByParam(paramDto);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public ReturnInfo batchClose(ActivityParamBo paramBo) throws Exception {
		ReturnInfo retInfo = new ReturnInfo();
		ActivityParamDto paramDto = new ActivityParamDto();
		paramDto.setActivityIds(paramBo.getActivityIds());
		// 检查需要关闭的活动状态
		List<ActivityDiscount> actList = activityDiscountMapper.findListByParam(paramDto);
		for(ActivityDiscount act : actList){
			if(act.getStatus() != ActivityDiscountStatus.noStart 
					&& act.getStatus() != ActivityDiscountStatus.ing){
				// 活动只能关闭未开始和已进行中的
				retInfo.setFlag(false);
				retInfo.setMessage("选中的活动中存在已关闭活动，请重新选择!");
				return retInfo;
			}
			// 释放原来商品活动库存 + 解除原商品关联关系
			removeSkuAndStockRelation(act.getId());
		}
		// 关闭活动
		activityDiscountMapper.updateStatus(paramBo);
		
		
		return retInfo;
	}

	@Override
	public ActivityInfoDto findInfoById(String id,boolean isLoadDetail) throws Exception {
		// 活动基本信息
		ActivityDiscount activityInfo = activityDiscountMapper.findById(id);
		// 活动优惠条件信息
		List<ActivityDiscountCondition> conditionList = activityDiscountConditionMapper.findByActivityId(id);
		// 活动业务限制信息
		List<ActivityBusinessRel> relList = activityBusinessRelMapper.findByActivityId(id);
		//查询团购活动商品
		List<ActivityDiscountGroup> groupGoodsList = activityDiscountGroupMapper.findByActivityId(id);
		// 解析业务限制信息
		ActLimitRelBuilder limitBuilder = parseRelList(relList,isLoadDetail);
		ActivityInfoDto actInfoDto = new ActivityInfoDto();
		actInfoDto.setActivityInfo(activityInfo);
		actInfoDto.setActivityType(activityInfo.getType().ordinal());
		//零花钱活动 定额发放时 将默认订单最小金额置0 
		if(activityInfo.getType() == ActivityDiscountType.PIN_MONEY && activityInfo.getGrantType() == 2){
			conditionList.get(0).setRate(0);
		}
		actInfoDto.setConditionList(conditionList);
		actInfoDto.setGroupGoodsList(groupGoodsList);
		if(limitBuilder != null){
			actInfoDto.setRelDtoList(limitBuilder.retrieveResult());
			actInfoDto.setLimitRangeIds(limitBuilder.getLimitRangeIds());
		}
		return actInfoDto;
	}
	
	/**
	 * @Description: 解析活动限制关系
	 * @param relList
	 * @param isLoadDetail
	 * @return
	 * @throws ServiceException   
	 * @author maojj
	 * @date 2017年4月21日
	 */
	private  ActLimitRelBuilder parseRelList(List<ActivityBusinessRel> relList,boolean isLoadDetail) throws ServiceException{
		if(CollectionUtils.isEmpty(relList)){
			return null;
		}
		ActLimitRelBuilder builder = new ActLimitRelBuilder();
		// 加载当前业务列表
		builder.loadBusiRelList(relList);
		if(!isLoadDetail){
			// 不用加载明细，直接返回
			return builder;
		}
		// 地址列表
		List<Address> areaList = Lists.newArrayList();
		// 店铺列表
		List<StoreInfo> storeInfoList = null;
		// 分类列表
		List<GoodsSpuCategory> spuCtgList = null;
		// 商品列表
		List<StoreSkuDto> storeSkuList = null;
		// 遍历具体业务信息
		for(Map.Entry<ActivityBusinessType, List<String>> entry : builder.getRelIdMap().entrySet()){
			ActivityBusinessType busiType = entry.getKey();
			List<String> busiIds = entry.getValue();
			switch (busiType) {
				case CITY:
				case PROVINCE:
					findAreaList(areaList,busiIds,busiType);
					break;
				case STORE:
					storeInfoList = storeInfoServiceApi.selectByIds(busiIds);
					postStoreList(storeInfoList);
					break;
				case SKU:
					storeSkuList = findStoreSkuList(busiIds);
					break;
				case SKU_CATEGORY:
					spuCtgList = goodsSpuCategoryServiceApi.selectByIds(busiIds);
					break;

				default:
					break;
			}
		}
		builder.loadAreaList(areaList);
		builder.loadStoreList(storeInfoList);
		builder.loadSpuCtgList(spuCtgList);
		builder.loadStoreSkuList(storeSkuList);
		return builder;
	}
	
	/**
	 * @Description: 查询地区列表
	 * @param areaList
	 * @param areaIds   
	 * @author maojj
	 * @date 2017年4月20日
	 */
	private void findAreaList(List<Address> areaList,List<String> areaIds,ActivityBusinessType busiType){
		Address addr = null;
		for(String areaId : areaIds){
			addr = addressApi.getAddressById(Long.parseLong(areaId));
			if(busiType == ActivityBusinessType.PROVINCE){
				areaList.addAll(addressApi.getChildrenList(Long.parseLong(areaId)));
			}
			areaList.add(addr);
		}
	}
	
	/**
	 * @Description: 查询店铺商品列表
	 * @param storeSkuIds
	 * @return   
	 * @author maojj
	 * @date 2017年4月20日
	 */
	private List<StoreSkuDto> findStoreSkuList(List<String> storeSkuIds){
		StoreSkuParamDto paramDto = new StoreSkuParamDto();
		paramDto.setPageNumber(1);
		paramDto.setPageSize(storeSkuIds.size());
		paramDto.setStoreSkuIds(storeSkuIds);
		PageUtils<StoreSkuDto> storeSkuPage = goodsStoreSkuServiceApi.findStoreSkuList(paramDto);
		return storeSkuPage.getList();
	}
	
	/**
	 * @Description: 后置处理店铺信息
	 * @param storeInfoList   
	 * @author maojj
	 * @date 2017年4月21日
	 */
	private void postStoreList(List<StoreInfo> storeInfoList){
		if(CollectionUtils.isEmpty(storeInfoList)){
			return;
		}
		for(StoreInfo storeInfo : storeInfoList){
			if(StringUtils.isNotEmpty(storeInfo.getCityId())){
				Address address = addressApi.getAddressById(Long.parseLong(storeInfo.getCityId()));
				storeInfo.setAddress(address.getName());
			}
		}
	}

	@Override
	public List<ActivityInfoDto> findByStore(ActivityParamDto paramDto) throws Exception {
		List<ActivityInfoDto> actInfoList = Lists.newArrayList();
		if(paramDto.getStoreType() == null){
			StoreInfo storeInfo = storeInfoServiceApi.findById(paramDto.getStoreId());
			paramDto.setStoreType(storeInfo.getType());
		}
		if(paramDto.getStoreType() != StoreTypeEnum.CLOUD_STORE){
			return actInfoList;
		}
		List<String> activityIds = activityDiscountMapper.findByStore(paramDto);
		
		ActivityInfoDto actInfo = null;
		for(String activityId : activityIds){
			actInfo = this.findInfoById(activityId,false);
			if(actInfo != null){
				actInfoList.add(actInfo);
			}
		}
		return actInfoList;
	}

	@Override
	public List<FullSubtract> findValidFullSubtract(FavourParamBO paramBo, FavourFilterStrategy favourFilter)
			throws Exception {
		List<FullSubtract> fullSubtractList = Lists.newArrayList();
		FullSubtract fullSubtract = null;
		// 满减活动查询条件
		ActivityParamDto paramDto = new ActivityParamDto();
		paramDto.setStoreId(paramBo.getStoreId());
		paramDto.setLimitChannel(String.valueOf(paramBo.getChannel().ordinal()));
		paramDto.setType(ActivityDiscountType.mlj);
		// 查询店铺所享有的满减活动
		List<ActivityInfoDto> actInfoDtoList = this.findByStore(paramDto);
		if(CollectionUtils.isEmpty(actInfoDtoList)){
			return fullSubtractList;
		}
		List<ActivityDiscountCondition> conditionList = null;
		for(ActivityInfoDto actInfoDto : actInfoDtoList){
			ActivityDiscount actInfo = actInfoDto.getActivityInfo();
			// 一个店铺同一时间只会存在一个满减活动。这里放入循环是为了后期加入折扣时使用。
			if(!favourFilter.accept(actInfoDto)){
				continue;
			}
			conditionList = actInfoDto.getConditionList();
			for(ActivityDiscountCondition condition : conditionList){
				if(condition.getArrive().compareTo(paramBo.getTotalAmount()) == 1){
					continue;
				}
				fullSubtract = new FullSubtract();
				// 活动Id
				fullSubtract.setId(actInfoDto.getActivityInfo().getId());
				// 满减条件Id
				fullSubtract.setActivityItemId(condition.getId());
				// 活动类型
				fullSubtract.setActivityType(EnumAdapter.convert(actInfo.getType()).ordinal());
				fullSubtract.setArrive(ConvertUtil.format(condition.getArrive()));
				fullSubtract.setFullSubtractPrice(ConvertUtil.format(condition.getDiscount()));
				fullSubtract.setIndate(DateUtils.formatDate(actInfo.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
				// 0：活动由平台发起，1：活动由商家发起
				fullSubtract.setType("0".equals(actInfo.getStoreId())? 0 : 1);
				// 可用范围(0,支持在线，1支持货到付款，2，支持所有支付方式)
				fullSubtract.setUsableRange(actInfo.getIsCashDelivery() == CashDelivery.yes ? "2" : "0");
				fullSubtract.setMaxFavourStrategy(genericMaxFavourStrategy.calMaxFavourRule(fullSubtract, paramBo.getTotalAmount()));
				
				fullSubtractList.add(fullSubtract);
			}
		}
		return fullSubtractList;
	}

	@Override
	public List<ActivityDiscount> findByIds(List<String> idList) {
		if(CollectionUtils.isEmpty(idList)){
			return Lists.newArrayList();
		}
		return activityDiscountMapper.findByIds(idList);
	}

	@Override
	public boolean isJoinPinMoney(ActivityPinMoneyDto activityPinMoneyDto, String storeId,String cityId, String provinceId) {
		
		AreaType areaType = activityPinMoneyDto.getLimitRange();
		//区域
		if(areaType == AreaType.area){
			List<ActivityBusinessRel> areaList = activityBusinessRelMapper.findByActivityId(activityPinMoneyDto.getId());
			for(ActivityBusinessRel rel : areaList){
				if(rel.getBusinessType() == ActivityBusinessType.PROVINCE && provinceId.equals(rel.getBusinessId())){
					return true;
				}
				if(rel.getBusinessType() == ActivityBusinessType.CITY && cityId.equals(rel.getBusinessId())){
					return true;
				}
			}
			return false;
		}
		//店铺
		if(areaType == AreaType.store){
			// 共用原来方法 查询列表后遍历
			List<ActivityBusinessRel> relList = activityBusinessRelMapper.findByActivityId(activityPinMoneyDto.getId());
			for(ActivityBusinessRel rel : relList){
				if(storeId.equals(rel.getBusinessId())){
					return true;
				}
			}
			return false;	
		}
		return true;
	}

	@Override
	public List<ActivityDiscountCondition> getActivityDiscountConditions(String discountId) {
		return activityDiscountConditionMapper.findByActivityId(discountId);
	}
}
