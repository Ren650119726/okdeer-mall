package com.okdeer.mall.activity.group.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.okdeer.archive.goods.spu.vo.ActivityGroupGoodsVo;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.enums.BSSC;
import com.okdeer.archive.goods.store.enums.IsActivity;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.service.StockManagerServiceApi;
import com.okdeer.archive.stock.vo.AdjustDetailVo;
import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.okdeer.archive.store.enums.StoreActivityTypeEnum;
import com.okdeer.archive.system.entity.PsmsSmallCommunityInfo;
import com.okdeer.archive.system.entity.SysUser;
import com.okdeer.mall.activity.group.entity.ActivityGroup;
import com.okdeer.mall.activity.group.entity.ActivityGroupArea;
import com.okdeer.mall.activity.group.entity.ActivityGroupCommuntity;
import com.okdeer.mall.activity.group.entity.ActivityGroupGoods;
import com.okdeer.mall.activity.group.enums.ActivityGroupAreaEnum;
import com.okdeer.mall.activity.group.enums.ActivityGroupAuditStatus;
import com.okdeer.mall.activity.group.enums.ActivityGroupStatus;
import com.okdeer.mall.activity.group.service.ActivityGroupServiceApi;
import com.okdeer.mall.common.entity.TreeVo;
import com.yschome.base.common.enums.Disabled;
import com.yschome.base.common.exception.ServiceException;
import com.yschome.base.common.utils.PageUtils;
import com.yschome.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.group.mapper.ActivityGroupMapper;
import com.okdeer.mall.activity.group.service.ActivityGroupGoodsService;
import com.okdeer.mall.activity.group.service.ActivityGroupService;
import com.okdeer.mall.system.mapper.SysUserMapper;

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
@Service(version = "1.0.0", interfaceName="com.okdeer.mall.activity.group.service.ActivityGroupServiceApi")
public class ActivityGroupServiceImpl implements ActivityGroupServiceApi,ActivityGroupService {
	
	/**
	 * 日志记录
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(ActivityGroupServiceImpl.class);
	 
	/**
	 * activityGroupMapper
	 */
	@Autowired
	private ActivityGroupMapper activityGroupMapper;
	
	/**
	 * sysUserMapper
	 */
	@Autowired
	private SysUserMapper sysUserMapper;
	
	/**
	 * 库存调整
	 */
	@Reference(version="1.0.0",check=false)
	private StockManagerServiceApi stockManagerServiceApi;

	/**
	 * 团购商品service
	 */
	@Autowired
	private ActivityGroupGoodsService activityGroupGoodsService;
	
	@Reference(version="1.0.0")
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;
	
	@Override
	public void insert(ActivityGroup activityGroup) throws ServiceException {
		activityGroupMapper.insert(activityGroup);
	}

	@Override
	public void update(ActivityGroup activityGroup) throws ServiceException {
		activityGroupMapper.update(activityGroup);
	}

	@Override
	public void deleteByPrimaryKey(String id) throws ServiceException {
		activityGroupMapper.deleteByPrimaryKey(id);
		
	}

	@Override
	public ActivityGroup selectByPrimaryKey(String id) {
		return activityGroupMapper.selectByPrimaryKey(id);
	}

	@Override
	public PageUtils<ActivityGroup> findActivityGroups(ActivityGroup activityGroup,int pageNumber, int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ActivityGroup> activityGroups = activityGroupMapper.findActivityGroups(activityGroup);
		if(activityGroups != null && activityGroups.size() > 0){
			for (ActivityGroup group : activityGroups) {
				/*PsmsAgent agent = activityGroupMapper.getPsmsAgent(group.getBelongType());
				if(agent != null){
					group.setCreateUser(agent.getFullName());
				}*/
				SysUser sysUser = sysUserMapper.selectByPrimaryKey(group.getCreateUserId());
				if(sysUser != null){
					group.setCreateUser(sysUser.getUserName());
				}
			}
		}
		return new PageUtils<ActivityGroup>(activityGroups);
	}

	@Override
	public void insertActivityGroupArea(ActivityGroupArea activityGroupArea)
			throws ServiceException {
		activityGroupMapper.insertActivityGroupArea(activityGroupArea);
	}

	@Override
	public void insertActivityGroupCommuntity(
					ActivityGroupCommuntity activityGroupCommuntity)
			throws ServiceException {
		activityGroupMapper.insertActivityGroupCommuntity(activityGroupCommuntity);
	}

	@Override
	public void deleteActivityGroupArea(String groupId) throws ServiceException {
		activityGroupMapper.deleteActivityGroupArea(groupId);
	}

	@Override
	public void deleteActivityGroupCommuntity(String groupId)
			throws ServiceException {
		activityGroupMapper.deleteActivityGroupCommuntity(groupId);
	}

	@Override
	public List<ActivityGroupArea> findActivityGroupAreas(String groupId) {
		return activityGroupMapper.findActivityGroupAreas(groupId);
	}

	@Override
	public List<ActivityGroupCommuntity> findActivityGroupCommuntitys(
					String groupId) {
		return activityGroupMapper.findActivityGroupCommuntitys(groupId);
	}

	@Override
	public PageUtils<ActivityGroupGoods> getActivityGroupGoods(String groupId,String storeId,String online,int pageNumber,int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true,false);
		List<ActivityGroupGoods> result = activityGroupGoodsService.getActivityGroupGoods(groupId);
		List<String> ids = null;
		if (result != null && result.size() > 0) {
			for (ActivityGroupGoods activityGroupGoods : result) {
				
				ids = new ArrayList<String>();
				ids.add(activityGroupGoods.getStoreSkuId());
				
				List<ActivityGroupGoodsVo> goodsList = activityGroupGoodsService.findSpuByGoodsStoreId(ids, storeId,online);
				if(goodsList != null && goodsList.size() > 0){
					ActivityGroupGoodsVo goods = goodsList.get(0);
					activityGroupGoods.setBarcode(goods.getBarcode());
					//String categroyName = this.findCategoryNameByLevelIds(goods.getLevelId());
					activityGroupGoods.setCategoryName(goods.getCategoryName());
					activityGroupGoods.setGoodsName(goods.getGoodsName());
					activityGroupGoods.setGoodsStock(goods.getGoodsStock());
					activityGroupGoods.setLockedStock(goods.getLockedStock());
					activityGroupGoods.setGoodsType(goods.getGoodsType());
					activityGroupGoods.setSalePrice(goods.getSalePrice());
					activityGroupGoods.setGoodsPic(goods.getUrl());
					activityGroupGoods.setSpuId(goods.getId());
					activityGroupGoods.setSkuId(goods.getSkuId());
					activityGroupGoods.setStoreSpuId(goods.getStoreSpuId());
					activityGroupGoods.setPropertiesIndb(goods.getPropertiesIndb());
				}
			}
		}
		return new PageUtils<ActivityGroupGoods>(result);
	}
	
	
	@Override
	public PageUtils<ActivityGroupGoods> getActivityGroupGoods(String groupId,String online,int pageNumber, int pageSize) {
		ActivityGroup activityGroup = this.selectByPrimaryKey(groupId);
		PageHelper.startPage(pageNumber, pageSize, true);
		//Page page  = (Page)activityGroupGoodsService.getActivityGroupGoods(groupId);
		
		List<ActivityGroupGoods> result = activityGroupGoodsService.getActivityGroupGoods(groupId);
		List<String> ids = null;
		if (result != null && result.size() > 0) {
			for (ActivityGroupGoods activityGroupGoods : result) {
				
				ids = new ArrayList<String>();
				ids.add(activityGroupGoods.getStoreSkuId());
				
				List<ActivityGroupGoodsVo> goodsList = activityGroupGoodsService.findSpuByGoodsStoreId(ids,activityGroup.getStoreId(),online);
				if(goodsList != null && goodsList.size() > 0){
					ActivityGroupGoodsVo goods = goodsList.get(0);
					activityGroupGoods.setBarcode(goods.getBarcode());
					activityGroupGoods.setCategoryName(goods.getCategoryName());
					activityGroupGoods.setGoodsName(goods.getGoodsName());
					activityGroupGoods.setGoodsStock(goods.getGoodsStock());
					activityGroupGoods.setGoodsType(goods.getGoodsType());
					activityGroupGoods.setSalePrice(goods.getSalePrice());
					activityGroupGoods.setGoodsPic(goods.getUrl());
					activityGroupGoods.setSpuId(goods.getId());
					activityGroupGoods.setSkuId(goods.getSkuId());
				}
			}
		}
		return new PageUtils<ActivityGroupGoods>(result);
	}

	@Override
	public List<ActivityGroup> findActivityGroupList(ActivityGroup activityGroup) {
		return activityGroupMapper.findActivityGroupList(activityGroup);
	}

	@Override
	public List<PsmsSmallCommunityInfo> findCommuntityByCityId(String cityId,String agentId) {
		return activityGroupMapper.findCommuntityByCityId(cityId,agentId);
	}

	@Override
	public PageUtils<ActivityGroup> findActivityGroupsByPrames(
			Map<String,Object> map, int pageNumber, int pageSize) {
		
		PageHelper.startPage(pageNumber, pageSize, true,false);
		List<ActivityGroup> activityGroups = activityGroupMapper.findActivityGroupsByPrames(map);
		if(activityGroups != null && activityGroups.size() > 0){
			for (ActivityGroup group : activityGroups) {
				//PsmsAgent agent = activityGroupMapper.getPsmsAgent(group.getBelongType());
				SysUser sysUser = sysUserMapper.selectByPrimaryKey(group.getCreateUserId());
				if(sysUser != null){
					group.setCreateUser(sysUser.getUserName());
				}
			}
		}
		return new PageUtils<ActivityGroup>(activityGroups);
	}
	
	public static void main(String[] args) {
		String formJson = "{activityGroup={\"id\":\"8a94e43d5308a033015308a033a70000\",\"city\":\"[{'id':'81','pId':'3','name':'衡水市'}]\",\"areaType\":\"1\"}";
		JSONObject json = JSONObject.fromObject(formJson);
		ActivityGroup activityGroup = (ActivityGroup)JSONObject.toBean(
				(JSONObject)json.get("activityGroup"), ActivityGroup.class);
		System.out.println(activityGroup);
	}

	@Override
	public void publishActivityGroup(JSONObject json,SysUser user,Object city) throws Exception {
		//团购活动
		ActivityGroup activityGroup = (ActivityGroup)JSONObject.toBean(
				(JSONObject)json.get("activityGroup"), ActivityGroup.class);
		
		List<ActivityGroupArea> areaList = null;
		List<ActivityGroupCommuntity> communtityList = null;
		//HttpSession session = request.getSession();
		if(ActivityGroupAreaEnum.area.getKey().equals(activityGroup.getAreaType())){
			
			//Object city = session.getAttribute("city");
			json.put("city", city);
			List<JSONObject> jsonArealist = (List<JSONObject>)json.getJSONArray("city");
			
			//List<TreeVo> jsonArealist = activityGroup.getCity();
			
			if(null != jsonArealist && jsonArealist.size() > 0){
				areaList = new ArrayList<ActivityGroupArea>();
				for ( JSONObject areaJson : jsonArealist) {
					//TreeVo areaJson = (TreeVo)jsonArealist.get(i);
					TreeVo vo = (TreeVo)JSONObject.toBean(areaJson, TreeVo.class);
					ActivityGroupArea area = new ActivityGroupArea();
					area.setAreaId(vo.getId());
					area.setAreaName(vo.getName());
					areaList.add(area);
				}
			}
		}else{
			
			//Object city = session.getAttribute("community");
			json.put("community", city);
			//所属小区
			
			List<JSONObject> jsonCommuntitylist = (List<JSONObject>)json.getJSONArray("community");
			
			if(null != jsonCommuntitylist && jsonCommuntitylist.size() > 0){
				communtityList = new ArrayList<ActivityGroupCommuntity>();
				for ( JSONObject communtityJson : jsonCommuntitylist) {
					TreeVo vo = (TreeVo)JSONObject.toBean(communtityJson, TreeVo.class);
					ActivityGroupCommuntity communtity = new ActivityGroupCommuntity();
					communtity.setCommunityId(vo.getId());
					communtity.setCommunityName(vo.getName());
					communtityList.add(communtity);
				}
			}
		}
		
		//商品
		List<ActivityGroupGoods> goodsList = null;
		List<JSONObject> jsonGoodslist = (List<JSONObject>)json.getJSONArray("goods");
		if(null != jsonGoodslist && jsonGoodslist.size() > 0){
			goodsList = new ArrayList<ActivityGroupGoods>();
			for ( JSONObject goodsJson : jsonGoodslist) {
				ActivityGroupGoods goods = (ActivityGroupGoods)JSONObject.toBean(goodsJson, ActivityGroupGoods.class);
				goodsList.add(goods);
			}
		}
		
		if(StringUtils.isNotBlank(activityGroup.getId())){
			this.updateActivityGroup(user, activityGroup, areaList, communtityList, goodsList);
		}else{
			this.addActivityGroup(user, activityGroup, areaList, communtityList, goodsList);
		}
		
	}
	
	void addActivityGroup(SysUser user,ActivityGroup activityGroup,List<ActivityGroupArea> areaList,
			List<ActivityGroupCommuntity> communtityList,List<ActivityGroupGoods> goodsList) throws Exception{
		String belongType = "0";
		String storeId = "";
		if(user != null && user.getRelation() != null){
			storeId = user.getRelation().getStoreId();
		}
		if(!"".equals(storeId)){
			belongType = storeId;
		}
		
		Date nowTime = new Date();
		activityGroup.setId(UuidUtils.getUuid());
		activityGroup.setBelongType(belongType);
		activityGroup.setDisabled(Disabled.valid);
		activityGroup.setStatus(ActivityGroupStatus.unStarted.getKey());
		activityGroup.setApprovalStatus(ActivityGroupAuditStatus.unAudit.getKey());
		activityGroup.setCreateUserId(user.getId());
		activityGroup.setUpdateUserId(user.getId());
		activityGroup.setStoreId(storeId);
		activityGroup.setCreateTime(nowTime);
		activityGroup.setUpdateTime(nowTime);
		insert(activityGroup);
		if(ActivityGroupAreaEnum.area.getKey().equals(activityGroup.getAreaType())){
			if(areaList != null && areaList.size() > 0){
				for (ActivityGroupArea area : areaList) {
					area.setType("0");
					area.setId(UuidUtils.getUuid());
					area.setGroupId(activityGroup.getId());
					this.insertActivityGroupArea(area);
				}
			}
		}else{
			if(communtityList != null && communtityList.size() > 0){
				for (ActivityGroupCommuntity communtity : communtityList) {
					communtity.setGroupId(activityGroup.getId());
					communtity.setId(UuidUtils.getUuid());
					this.insertActivityGroupCommuntity(communtity);
				}
			}
		}
		
		if(goodsList != null && goodsList.size() > 0){
			for (ActivityGroupGoods goods : goodsList) {
				goods.setId(UuidUtils.getUuid());
				goods.setDisabled(Disabled.valid);
				goods.setGroupId(activityGroup.getId());
				goods.setStatus("0");
				activityGroupGoodsService.insert(goods);
				
				//同步库存
				this.syncGoodsStock(goods, user.getId(), storeId);
				
				GoodsStoreSku goodsStoreSku = goodsStoreSkuServiceApi.getById(goods.getStoreSkuId());
				if(goodsStoreSku != null){
					goodsStoreSku.setActivityId(activityGroup.getId());
					goodsStoreSku.setActivityName(activityGroup.getName());
					goodsStoreSku.setIsActivity(IsActivity.ATTEND);
					goodsStoreSku.setActivityType(StoreActivityTypeEnum.GROUP_BY);
					goodsStoreSku.setSort(goods.getSort());
					goodsStoreSkuServiceApi.updateByPrimaryKey(goodsStoreSku);
				}
				
			}
		}
	}
	
	void updateActivityGroup(SysUser user,ActivityGroup activityGroup,List<ActivityGroupArea> areaList,
			List<ActivityGroupCommuntity> communtityList,List<ActivityGroupGoods> goodsList) throws Exception{
		String belongType = "0";
		String storeId = "";
		if(user != null && user.getRelation() != null){
			storeId = user.getRelation().getStoreId();
		}
		if(!"".equals(storeId)){
			belongType = storeId;
		}
		Date nowTime = new Date();
		activityGroup.setBelongType(belongType);
		activityGroup.setUpdateUserId(user.getId());
		activityGroup.setStoreId(storeId);
		activityGroup.setUpdateTime(nowTime);
		activityGroup.setApprovalStatus(ActivityGroupAuditStatus.unAudit.getKey());
		this.update(activityGroup);
		if(ActivityGroupAreaEnum.area.getKey().equals(activityGroup.getAreaType())){
			if(areaList != null && areaList.size() > 0){
				this.deleteActivityGroupArea(activityGroup.getId());
				for (ActivityGroupArea area : areaList) {
					area.setType("0");
					area.setId(UuidUtils.getUuid());
					area.setGroupId(activityGroup.getId());
					this.insertActivityGroupArea(area);
				}
			}
		}else{
			if(communtityList != null && communtityList.size() > 0){
				this.deleteActivityGroupCommuntity(activityGroup.getId());
				for (ActivityGroupCommuntity communtity : communtityList) {
					communtity.setGroupId(activityGroup.getId());
					communtity.setId(UuidUtils.getUuid());
					this.insertActivityGroupCommuntity(communtity);
				}
			}
		}
		List<String> storeSkuIds = new ArrayList<String>();
		if(goodsList != null && goodsList.size() > 0){
			activityGroupGoodsService.removeByGroupId(activityGroup.getId());
			for (ActivityGroupGoods goods : goodsList) {
				goods.setId(UuidUtils.getUuid());
				goods.setDisabled(Disabled.valid);
				goods.setGroupId(activityGroup.getId());
				goods.setStatus("0");
				activityGroupGoodsService.insert(goods);
				storeSkuIds.add(goods.getStoreSkuId());
				
				//同步库存
				this.syncGoodsStock(goods, user.getId(), storeId);
				
				GoodsStoreSku goodsStoreSku = goodsStoreSkuServiceApi.getById(goods.getStoreSkuId());
				if(goodsStoreSku != null){
					goodsStoreSku.setSort(goods.getSort());
					goodsStoreSkuServiceApi.updateByPrimaryKey(goodsStoreSku);
				}
			}
		}
		goodsStoreSkuServiceApi.updateStoreSkuBsscStatus(storeSkuIds, BSSC.UNSHELVE);
	}
	
	private void syncGoodsStock(ActivityGroupGoods goods,String userId,String storeId) {
		try {
			GoodsStoreSku goodsStoreSku = goodsStoreSkuServiceApi.getById(goods.getStoreSkuId());
			StockAdjustVo stockAdjustVo = new StockAdjustVo();
			stockAdjustVo.setUserId(userId);
			stockAdjustVo.setStoreId(storeId);
			List<AdjustDetailVo> adjustDetailList = new ArrayList<AdjustDetailVo>();
			AdjustDetailVo adjustDetailVo = new AdjustDetailVo();
			adjustDetailVo.setStoreSkuId(goods.getStoreSkuId());
			adjustDetailVo.setNum(goods.getGroupInventory());
			adjustDetailVo.setGoodsName(goodsStoreSku.getName());
			adjustDetailList.add(adjustDetailVo);
			stockAdjustVo.setAdjustDetailList(adjustDetailList);
			stockAdjustVo.setStockOperateEnum(StockOperateEnum.ACTIVITY_STOCK);
			logger.info("商家中心修改团购活动商品库存参数:" + stockAdjustVo.toString());
			stockManagerServiceApi.updateStock(stockAdjustVo);
			logger.info("商家中心修改团购活动商品完成:");
		} catch (Exception e) {
			logger.info("商家中心修改团购活动商品发生异常:",e);
		}
	}

	@Override
	public PsmsSmallCommunityInfo findCommuntity(String id) {
		return activityGroupMapper.findCommuntity(id);
	}

	@Override
	public int selectUnApprovalNum(String storeId,Integer approvalStatus,Integer status){
		Map<String,Object> params = Maps.newHashMap();
		params.put("storeId", storeId);
		params.put("approvalStatus", approvalStatus);
		params.put("status", status);
		return activityGroupMapper.selectUnApprovalNum(params);
	}

	@Override
	public ActivityGroup selectServiceTime(String activityId) throws Exception {
		
		ActivityGroup activityGroup = new ActivityGroup();
		activityGroup = activityGroupMapper.selectServiceTime(activityId);
		
		return activityGroup;
	}

	@Override
	public ActivityGroup findByPrimaryKey(String id) {
		return activityGroupMapper.selectById(id);
	}

	@Override
	public ActivityGroup selectGroupStatus(String activityId) {
		return activityGroupMapper.selectGroupStatus(activityId);
	}
	
}
