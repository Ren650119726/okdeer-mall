/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: ColumnAdvertServiceImpl.java 
 * @Date: 2016年1月27日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */

package com.okdeer.mall.operate.advert.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.store.dto.GoodsStoreActivitySkuDto;
import com.okdeer.archive.system.entity.PsmsSmallCommunityInfo;
import com.okdeer.archive.system.entity.SysUser;
import com.okdeer.archive.system.service.IPsmsSmallCommunityInfoServiceApi;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.advert.dto.ColumnAdvertQueryParamDto;
import com.okdeer.mall.advert.entity.AdvertDetailVo;
import com.okdeer.mall.advert.entity.ColumnAdvert;
import com.okdeer.mall.advert.entity.ColumnAdvertApproval;
import com.okdeer.mall.advert.entity.ColumnAdvertArea;
import com.okdeer.mall.advert.entity.ColumnAdvertAreaVo;
import com.okdeer.mall.advert.entity.ColumnAdvertCommunity;
import com.okdeer.mall.advert.entity.ColumnAdvertInfo;
import com.okdeer.mall.advert.entity.ColumnAdvertQueryVo;
import com.okdeer.mall.advert.entity.ColumnAdvertVo;
import com.okdeer.mall.advert.enums.AdvertIsPayEnum;
import com.okdeer.mall.advert.enums.AdvertStatusEnum;
import com.okdeer.mall.advert.enums.AdvertTypeEnum;
import com.okdeer.mall.advert.service.IColumnAdvertServiceApi;
import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.mall.common.enums.AuditStatusEnum;
import com.okdeer.mall.common.enums.ClientTypeEnum;
import com.okdeer.mall.common.enums.UserType;
import com.okdeer.mall.operate.advert.mapper.ColumnAdvertApprovalMapper;
import com.okdeer.mall.operate.advert.mapper.ColumnAdvertAreaMapper;
import com.okdeer.mall.operate.advert.mapper.ColumnAdvertInfoMapper;
import com.okdeer.mall.operate.advert.mapper.ColumnAdvertMapper;
import com.okdeer.mall.operate.advert.service.ColumnAdvertService;
import com.okdeer.mall.operate.entity.ColumnAdvertVersionBo;
import com.okdeer.mall.operate.entity.ColumnAdvertVersionDto;
import com.okdeer.mall.operate.mapper.ColumnAdvertVersionMapper;

/**
 * 广告service实现类
 * @project yschome-mall
 * @author zhaoqc
 * @date 2016年1月27日 下午6:11:40
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		广告修改			2016-7-14			zhulq	            广告模块修改
 *		广告修改将			2016-8-1			zhulq	    将 associateIdList 去掉
 *		广告修改将			2016-8-6			zhulq	  修改job 根据时间 将广告状态置成 开始  结束  现在已经不用停用和过期两个状态了
 *		广告修改将			2016-8-8			zhulq	  修改job 将列加上对应的表弟额前缀
 * 		广告修改将			2016-9-1			zhulq	  修改结束广告时候重新给图片赋值  
 * 		v1.1.0          2016-10-18		zhulq    获取默认的广告图片
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.advert.service.IColumnAdvertServiceApi")
public class ColumnAdvertServiceImpl implements ColumnAdvertService, IColumnAdvertServiceApi {

	private static final Logger logger = LoggerFactory.getLogger(ColumnAdvertServiceImpl.class);
	/**
	 * 广告Mapper注入
	 */
	@Autowired
	private ColumnAdvertMapper advertMapper;

	/**
	 * 广告商Mappper注入
	 */
	@Autowired
	private ColumnAdvertInfoMapper advertInfoMapper;

	/**
	 * 店铺区域关系Mapper注入
	 */
	@Autowired
	private ColumnAdvertAreaMapper advertAreaMapper;

	/**
	 * 物业平台小区信息Service
	 */
	@Reference(version = "1.0.0")
	private IPsmsSmallCommunityInfoServiceApi psmsCommunityInfoServiceApi;

	/**
	 * 审核Mapper注入
	 */
	@Autowired
	private ColumnAdvertApprovalMapper advertApprovalMapper;
	
	/**
	 * 关联APP版本Mapper注入
	 */
	@Autowired
	private ColumnAdvertVersionMapper advertVersionMapper;

	/**
	 * @desc 查询广告列表
	 *
	 * @param queryVo 查询Vo
	 * @param pageNumber 页数
	 * @param pageSize 每页记录数
	 * @return 广告分页务数据
	 * @throws ServiceException 抛出异常
	 */
	@Transactional(readOnly = true)
	@Override
	public PageUtils<ColumnAdvertVo> findColumnAdvertPage(ColumnAdvertQueryVo queryVo, int pageNumber, int pageSize)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ColumnAdvertVo> advertList = this.advertMapper.findColumnAdvertPage(queryVo);

		return new PageUtils<ColumnAdvertVo>(advertList);
	}

	/**
	 * @desc 查询审核广告列表
	 *
	 * @param queryVo 查询Vo
	 * @param pageNumber 页数
	 * @param pageSize 每页记录数
	 * @return 广告分页务数据
	 * @throws ServiceException 抛出异常
	 */
	@Transactional(readOnly = true)
	@Override
	public PageUtils<ColumnAdvertVo> findAuditingAdvertPage(ColumnAdvertQueryVo queryVo, int pageNumber, int pageSize)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ColumnAdvertVo> advertList = this.advertMapper.findAuditingAdvertPage(queryVo);

		return new PageUtils<ColumnAdvertVo>(advertList);
	}

	// begin 广告张数的限制 重写findAcrossTimeAdvertQty方法 add by zhulq 2016-7-15
	// begin 将省市信息全部放进areaIdList add by zhulq 2016-8-1
	@Transactional(readOnly = true)
	@Override
	public int findAcrossTimeAdvertQty(ColumnAdvert advert, List<String> areaIdList) {
		return this.advertMapper.findAcrossTimeAdvertQty(advert);
	}
	// begin 广告张数的限制 重写findAcrossTimeAdvertQty方法 add by zhulq 2016-7-15
	// end 将省市信息全部放进areaIdList add by zhulq 2016-8-1

	/**
	 * @desc 创建广告
	 *
	 * @param advert 广告实体
	 * @param advertInfo 广告商实体
	 * @param currentUser 当前用户
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void addColumnAdvert(ColumnAdvert advert, ColumnAdvertInfo advertInfo, SysUser currentUser) {
		String[] idArr = null;
		String[] areas = null;
		// begin update by zhulq 修改结束广告时候重新给图片赋值 2016-9-1
		String advertId = advert.getId();
		// 创建广告
		if (StringUtils.isBlank(advertId)) {
			advertId = UuidUtils.getUuid();
			advert.setId(advertId);
		}
		// end update by zhulq 修改结束广告时候重新给图片赋值 2016-9-1
		advert.setIsPay(AdvertIsPayEnum.NOT_PAID);
		// begin 去掉缴费 add by zhulq 2016-7-14
		/*
		 * String tradeNum = TradeNumUtil.getTradeNum(); advert.setTradeNum(tradeNum);
		 */
		// end 去掉缴费 add by zhulq 2016-7-14
		advert.setStatus(AdvertStatusEnum.NOT_STARTING);
		advert.setDisabled(Disabled.valid);
		advert.setCreateTime(new Date());
		advert.setCreateUserId(currentUser.getId());
		advert.setUpdateTime(new Date());
		advert.setUpdateUserId(currentUser.getId());
		advert.setSort(0);
		advert.setPv(0);
		String targetUrl = advert.getTargetUrl();
		if (!StringUtils.isNullOrEmpty(targetUrl)) {
			targetUrl = StringEscapeUtils.unescapeHtml3(targetUrl);
			advert.setTargetUrl(targetUrl);
		}
		this.advertMapper.add(advert);

		// 创建广告商信息
		String advertInfoId = advertInfo.getId();
		if (StringUtils.isBlank(advertInfoId)) {
			advertInfoId = UuidUtils.getUuid();
			advertInfo.setId(advertInfoId);
		}
		advertInfo.setAdvertId(advertId);
		advertInfo.setCreateTime(new Date());
		advertInfo.setCreateUserId(currentUser.getId());
		advertInfo.setUpdateTime(new Date());
		advertInfo.setUpdateUserId(currentUser.getId());
		BigDecimal cost = advertInfo.getCost();
		if (cost == null) {
			advertInfo.setCost(new BigDecimal(0));
		}
		this.advertInfoMapper.insert(advertInfo);

		// 创建广告审核信息
		ColumnAdvertApproval advertApproval = new ColumnAdvertApproval();
		advertApproval.setId(UuidUtils.getUuid());
		advertApproval.setAdvertId(advertId);
		advertApproval.setApprovalTime(new Date());
		advertApproval.setCreateTime(new Date());
		advertApproval.setCreateUserId(currentUser.getId());
		advertApproval.setUpdateTime(new Date());
		advertApproval.setUpdateUserId(currentUser.getId());
		if (UserType.agentCode.equals(currentUser.getUserType())) {
			// 代理商创建广告需要审核
			advertApproval.setStatus(AuditStatusEnum.pending_verify);
		} else if (UserType.mallSuperAdmin.equals(currentUser.getUserType())
				|| UserType.operateCode.equals(currentUser.getUserType())) {
			// 超级管理员或者运营商创建广告直接通过
			advertApproval.setStatus(AuditStatusEnum.pass_verify);
		}
		this.advertApprovalMapper.insert(advertApproval);

		// 根据所选范围area_type创建广告区域关系
		AreaType areaType = advert.getAreaType();
		String dataIds = advert.getDataIds();
		if (!StringUtils.isNullOrEmpty(dataIds)) {
			idArr = dataIds.split(",");
			// begin 出掉小区 add by zhulq 2016-7-14
			// begin 将选择的省信息保存 add by zhulq 2016-7-14
			if (idArr != null && idArr.length > 0) {
				if (AreaType.area.equals(areaType)) {
					List<ColumnAdvertArea> advertAreaList = new ArrayList<ColumnAdvertArea>();
					ColumnAdvertArea advertArea = null;
					for (int i = 0; i < idArr.length; i++) {
						advertArea = new ColumnAdvertArea();
						areas = idArr[i].split("-");
						if ("0".equals(areas[1])) {
							advertArea.setType((short) 0);
						} else if ("1".equals(areas[1])) {
							advertArea.setType((short) 1);
						}
						advertArea.setId(UuidUtils.getUuid());
						advertArea.setAdvertId(advertId);
						advertArea.setAreaId(areas[0]);
						advertAreaList.add(advertArea);
					}
					this.advertAreaMapper.insertAdvertAreaBatch(advertAreaList);
				}
			}
			// end 出掉小区 add by zhulq 2016-7-14
			// end 将选择的省信息保存 add by zhulq 2016-7-14
		}
		// Begin add by tangzj02 2017-03-16
		//添加广告与APP以及版本的关联关系
		insertAdvertVersionBatch(advert);
		// End tangzj02 2017-03-16
	}

	/**
	 * @Description: 插入广告与APP版本的关联信息
	 * @author tangzj02
	 * @date 2017年3月14日
	 */
	private void insertAdvertVersionBatch(ColumnAdvert columnAdvert) {
		advertVersionMapper.deleteByAdvertId(columnAdvert.getId());
		List<ColumnAdvertVersionDto> versionList = Lists.newArrayList();
		//生成与便利店APP的关联数据
		if(CollectionUtils.isNotEmpty(columnAdvert.getCvsVersion())){
			versionList.addAll(createAdvertVersion(columnAdvert.getId(), columnAdvert.getCvsVersion(), ClientTypeEnum.CVS));
		}
		//生成与管家APP的关联数据
		if(CollectionUtils.isNotEmpty(columnAdvert.getStewardVersion())){
			versionList.addAll(createAdvertVersion(columnAdvert.getId(), columnAdvert.getStewardVersion(), ClientTypeEnum.STEWARD));
		}
		if(CollectionUtils.isNotEmpty(versionList)){
			advertVersionMapper.insertBatch(versionList);
		}
	}

	/**
	 * @Description: 生成广告与APP版本的关联实体信息
	 * @param advertId 广告ID
	 * @param versions 版本集合
	 * @param clientType app类型
	 * @return List<ColumnAdvertVersion>
	 * @author tangzj02
	 * @date 2017年3月14日
	 */
	private List<ColumnAdvertVersionDto> createAdvertVersion(String advertId, List<String> versions, ClientTypeEnum clientType) {
		List<ColumnAdvertVersionDto> versionList = Lists.newArrayList();
		ColumnAdvertVersionDto version = null;
		for(String item : versions){
			version = new ColumnAdvertVersionDto();
			version.setId(UuidUtils.getUuid());
			version.setType(clientType.getCode());
			version.setAdvertId(advertId);
			version.setVersion(item);
			versionList.add(version);
		}
		return versionList;
	}

	/**
	 * @desc 修改广告
	 *
	 * @param advert 广告实体
	 * @param advertInfo 广告商实体
	 * @param currentUser 当前用户
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateColumnAdvert(ColumnAdvert advert, ColumnAdvertInfo advertInfo, SysUser currentUser) {
		String userId = currentUser.getId();
		// 修改广告
		advert.setUpdateTime(new Date());
		advert.setUpdateUserId(userId);
		String targetUrl = advert.getTargetUrl();
		if (!StringUtils.isNullOrEmpty(targetUrl)) {
			targetUrl = StringEscapeUtils.unescapeHtml3(targetUrl);
			advert.setTargetUrl(targetUrl);
		}
		this.advertMapper.update(advert);

		// 修改广告商信息
		advertInfo.setUpdateTime(new Date());
		advertInfo.setUpdateUserId(userId);
		this.advertInfoMapper.updateByPrimaryKeySelective(advertInfo);

		// begin 将选择的省信息保存 之前的删除 add by zhulq 2016-7-15
		// 修改所选范围与广告关系
		this.advertAreaMapper.deleteByAdvertId(advert.getId());
		AreaType areaType = advert.getAreaType();
		String dataIds = advert.getDataIds();
		String[] idArr = null;
		String[] areas = null;
		if (!StringUtils.isNullOrEmpty(dataIds)) {
			idArr = dataIds.split(",");
			// begin 出掉小区 add by zhulq 2016-7-14
			if (idArr != null && idArr.length > 0) {
				if (AreaType.area.equals(areaType)) {
					List<ColumnAdvertArea> advertAreaList = new ArrayList<ColumnAdvertArea>();
					ColumnAdvertArea advertArea = null;
					for (int i = 0; i < idArr.length; i++) {
						advertArea = new ColumnAdvertArea();
						areas = idArr[i].split("-");
						if ("0".equals(areas[1])) {
							advertArea.setType((short) 0);
						} else if ("1".equals(areas[1])) {
							advertArea.setType((short) 1);
						}
						advertArea.setId(UuidUtils.getUuid());
						advertArea.setAdvertId(advert.getId());
						advertArea.setAreaId(areas[0]);
						advertAreaList.add(advertArea);
					}
					this.advertAreaMapper.insertAdvertAreaBatch(advertAreaList);
				}
			}
			// end 出掉小区 add by zhulq 2016-7-14
			// end 将选择的省信息保存 之前的删除 add by zhulq 2016-7-15
		}
		
		// Begin add by tangzj02 2017-03-16
		//添加广告与APP以及版本的关联关系
		insertAdvertVersionBatch(advert);
		// End tangzj02 2017-03-16
	}

	/**
	 * @desc 获得已有广告时间
	 * 
	 * @param positionId 广告位Id
	 * @return 广告列表
	 */
	@Transactional(readOnly = true)
	@Override
	public List<ColumnAdvert> findExistAdvertTime(String positionId) {
		return this.advertMapper.findExistAdvertTime(positionId);
	}

	/**   
	 *
	 * @param id 广告Id
	 * @return 广告详情Vo
	 */
	@Transactional(readOnly = true)
	@Override
	public AdvertDetailVo getAdvertDetailById(String id) {
		return this.advertMapper.getAdvertDetailById(id);
	}

	/**
	 * @desc 根据代理商Id代理商代理小区列表
	 *
	 * @param agentId 代理商Id
	 * @return 代理小区列表
	 */
	@Override
	public List<PsmsSmallCommunityInfo> getCommunityByAgentId(String agentId) {
		return this.psmsCommunityInfoServiceApi.getCommunityByAgentId(agentId);
	}

	/**
	 * @desc 广告审核
	 *
	 * @param advertApproval 审核实体
	 * @param currentUser 当前用户
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void auditAdvert(ColumnAdvertApproval advertApproval, SysUser currentUser) {
		advertApproval.setUpdateTime(new Date());
		advertApproval.setUpdateUserId(currentUser.getId());

		// 在代理商云钱包中生成一条代缴费

		this.advertApprovalMapper.updateByPrimaryKeySelective(advertApproval);
	}

	/**
	 * @desc 修改广告审核实体
	 *
	 * @param advertApproval 广告审核实体
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateAdvertApproval(ColumnAdvertApproval advertApproval) {
		this.advertApprovalMapper.updateByPrimaryKeySelective(advertApproval);
	}

	/**
	 * @desc 根据Id查找广告
	 *
	 * @param id 广告Id
	 * @return 广告实体
	 */
	@Override
	public ColumnAdvert findAdvertById(String id) {
		return this.advertMapper.findById(id);
	}

	/**
	 * @desc 查找广告位在某个区域内已经投放的广告的个数
	 *
	 * @param communitys 发布小区列表
	 * @param positionId 广告位Id
	 * @return 已发布广告的个数
	 */
	@Override
	public int getAdvertNumInCommunitys(List<ColumnAdvertCommunity> communitys, String positionId) {
		return this.advertMapper.getAdvertNumInCommunitys(communitys, positionId);
	}

	/**
	 * @desc 广告上架
	 *
	 * @param advertVo 广告Vo
	 * @param currentUser 当前用户
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void onShelfAdvert(AdvertDetailVo advertVo, SysUser currentUser) {
		ColumnAdvert advert = new ColumnAdvert();
		advert.setId(advertVo.getId());
		advert.setStatus(AdvertStatusEnum.STARTING);
		advert.setOnShelfTime(new Date());
		advert.setUpdateTime(new Date());
		advert.setStartTime(advertVo.getStartTime());
		advert.setUpdateUserId(currentUser.getId());

		this.advertMapper.update(advert);
	}

	/**
	 * @desc 广告下架
	 *
	 * @param advert 广告实体
	 * @param currentUser 当前用户
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void offShelfAdvert(ColumnAdvert advert, SysUser currentUser) {
		// begin 广告下架后状态是结束 add by zhulq 2016-8-1
		advert.setStatus(AdvertStatusEnum.FINISH);
		advert.setOffShelfTime(new Date());
		advert.setUpdateTime(new Date());
		advert.setEndTime(new Date());
		// begin 广告下架后状态是结束 add by zhulq 2016-8-1
		advert.setUpdateUserId(currentUser.getId());

		this.advertMapper.update(advert);
	}

	@Override
	public List<ColumnAdvert> getAdvertById(Map<String, Object> params) {
		List<ColumnAdvert> list = this.advertMapper.getAdvertById(params);
		return list;
	}
	
	@Override
	public List<ColumnAdvert> getAdvertByIdV220(Map<String, Object> params) {
		List<ColumnAdvert> list = this.advertMapper.getAdvertByIdV220(params);
		return list;
	}

	/**
	 * 广告列表 pos用 张克能加
	 * @param map 参数
	 * @return list
	 */
	@Override
	public List<ColumnAdvert> listForPos(Map<String, Object> map) {
		return advertMapper.listForPos(map);
	}

	/**
	 * @desc 根据交易流水号获得广告信息
	 *
	 * @param tradeNum 交易流水号
	 * @return 广告信息
	 */
	@Override
	public ColumnAdvert getAdvertByTradeNum(String tradeNum) {
		return this.advertMapper.getAdvertByTradeNum(tradeNum);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public int updateAdvertInfo(ColumnAdvert advert) {
		String targetUrl = advert.getTargetUrl();
		if (!StringUtils.isNullOrEmpty(targetUrl)) {
			targetUrl = StringEscapeUtils.unescapeHtml3(targetUrl);
			advert.setTargetUrl(targetUrl);
		}
		return this.advertMapper.update(advert);
	}

	/**
	 * 根据job扫描更新广告状态
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateAdvertStatusByJob() {
		// 查询开始时间小于当前时间状态为未开始的广告改为进行中
		// begin 将列加上对应的表的前缀 add by zhulq 2016-8-8
		List<ColumnAdvert> adverts = this.advertMapper.getAdvertForJob(0, new Date(), "ca.start_time", "<=");
		// end 将列加上对应的表的前缀 add by zhulq 2016-8-8
		// 将状态改为进行中
		if (adverts != null && !adverts.isEmpty()) {
			for (ColumnAdvert advert : adverts) {
				advert.setStatus(AdvertStatusEnum.STARTING);
				this.advertMapper.update(advert);
			}
		}

		// 查询进行中的广告结束时间小于等于当前时间状态改为已结束
		adverts = this.advertMapper.getAdvertStartForJob(1, new Date(), "ca.end_time", "<=");
		// 将状态改为已结束
		if (adverts != null && !adverts.isEmpty()) {
			for (ColumnAdvert advert : adverts) {
				advert.setStatus(AdvertStatusEnum.FINISH);
				this.advertMapper.update(advert);
			}
		}

		// begin 现在已经不涉及缴费了 add by zhulq 2016-8-6
		// 查询未交费结束时间小于等于的当前时间状态改为已过期
		/*
		 * adverts = this.advertMapper.getAdvertStartForJob(1, new Date(), "end_time", "<="); // 将状态改为已过期 if (adverts !=
		 * null && !adverts.isEmpty()) { for (ColumnAdvert advert : adverts) {
		 * advert.setStatus(AdvertStatusEnum.OUT_OF_DATE); this.advertMapper.update(advert); } }
		 */
		// end 现在已经不涉及缴费了 add by zhulq 2016-8-6
	}

	/**
	 * @desc 根据广告Id查找广告审核信息
	 *
	 * @param advertId 广告Id
	 * @return 广告审核信息
	 */
	@Transactional(readOnly = true)
	@Override
	public ColumnAdvertApproval getApprovalByAdvertId(String advertId) {
		return this.advertApprovalMapper.getApprovalByAdvertId(advertId);
	}

	@Transactional(readOnly = true)
	@Override
	public int findAcrossTimeAdvert(ColumnAdvert advert, List<String> areaIdList) {
		return this.advertMapper.findAcrossTimeAdvert(advert);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ColumnAdvert> findMobileDoorAdvertByMap(Map<String, Object> params) {
		return this.advertMapper.findMobileDoorAdvert(params);
	}

	// begin add by zhulq 获取默认的广告图片 2016-10-18
	@Override
	public ColumnAdvert listDefaultForPos(Map<String, Object> map) {
		return advertMapper.listDefaultForPos(map);
	}
	// end add by zhulq 获取默认的广告图片 2016-10-18

	// begin add by zhangkn 获取广告商品列表
	@Override
	public List<Map<String, Object>> listGoodsForAdvert(Map<String, Object> map) {
		return advertMapper.listGoodsForAdvert(map);
	}
	// end add by zhangkn 获取广告商品列表

	
	// begin  add　　by zengjz  修改广告的排序值
	@Override
	public void updateSort(String id, int sort) {
		ColumnAdvert columnAdvert = new ColumnAdvert();
		columnAdvert.setId(id);
		columnAdvert.setSort(sort);
		advertMapper.update(columnAdvert);
	}
	// end  add　　by zengjz  修改广告的排序值
	
	/**
	 * 根据活动url查询广告信息
	 * @param targetUrl 活动url
	 * tuzhiding
	 * @return
	 */
	@Transactional(readOnly = true)
	@Override
	public ColumnAdvert getAdvertForTargetURl(String targetUrl) {
		return this.advertMapper.getAdvertForTargetURl(targetUrl);
	}
	
	@Override
	public String findRestrictByArea(ColumnAdvert columnAdvert, Map<String, ColumnAdvertAreaVo> areaMap) throws Exception {
		String result = null;
		if(null == areaMap){
			return result;
		}
		//范围类型，0：全国，1：区域范围，2：小区范围 3.店铺
		if (AreaType.area.equals(columnAdvert.getAreaType()) || AreaType.national.equals(columnAdvert.getAreaType())) {
			for(Map.Entry<String, ColumnAdvertAreaVo> item : areaMap.entrySet()){
			    ColumnAdvertAreaVo vo = item.getValue();
			    List<String> areaIds = new ArrayList<String>();
			    //根据类型获取查询条件  1、全省，0、市
			    if ("1".equals(vo.getType())) {
			    	areaIds.addAll(vo.getAreaIds());
				} else {
					areaIds.add(vo.getpId());
				}
			    areaIds.add(vo.getId());
			    columnAdvert.setAreaIdList(areaIds);
			    List<ColumnAdvertVersionBo> countList = advertMapper.findAdvertRestrictByArea(columnAdvert);
			    if(CollectionUtils.isEmpty(countList)){
			    	continue;
			    }
			    
			    result = verifyAdvertAreaVersion(columnAdvert, countList, item.getValue());
			    
			    if (StringUtils.isNotBlank(result)) {
			    	return result;
				}
			} 
		}
		return result;
	}
	
	/**
	 * @Description: 验证同一个区域（城市）、同一个时间段、同一个版本下的广告发布情况
	 * @param advert 广告信息
	 * @param countList 相同地区发布广告的统计信息
	 * @param areaVo 广告的发布地区信息
	 * @return String 提示信息 非空则标识广告已经发布上限
	 * @throws Exception   
	 * @author tangzj02
	 * @date 2017年3月17日
	 */
	private String verifyAdvertAreaVersion(ColumnAdvert advert, List<ColumnAdvertVersionBo> countList, 
				ColumnAdvertAreaVo areaVo) throws Exception{
		//将lis转成Map
		Map<String, ColumnAdvertVersionBo> map = countList.stream()
				.collect(Collectors.toMap(ColumnAdvertVersionBo::getIdKey, bo -> bo));
		//返回信息
		String result = null;
		logger.info("校验广告版本 - 便利店版本:{}, 管家版本:{} 广告类型:{}", advert.getCvsVersion(), advert.getStewardVersion(), advert.getAdvertType());
		logger.info("闪屏广告 - ==:{}", AdvertTypeEnum.USER_APP_SPLASH_SCREEN.getIndex() ==  advert.getAdvertType());
		logger.info("闪屏广告 - equals:{}", advert.getAdvertType().equals(AdvertTypeEnum.USER_APP_SPLASH_SCREEN.getIndex()));
		//APP首页分割广告/APP闪屏广告/APP首页广告(便利店)
		if(CollectionUtils.isNotEmpty(countList) && CollectionUtils.isNotEmpty(advert.getCvsVersion())
				&& (advert.getAdvertType().equals(AdvertTypeEnum.USER_APP_INDEX_PARTITION.getIndex())
					|| advert.getAdvertType().equals(AdvertTypeEnum.USER_APP_SPLASH_SCREEN.getIndex()) 
			        || advert.getAdvertType().equals(AdvertTypeEnum.USER_APP_INDEX.getIndex()))){
			logger.info("APP首页分割广告/APP闪屏广告/APP首页广告(便利店)");
			//循环便利店APP版本集合
			for(String item : advert.getCvsVersion()){
				//通过地区ID-便利店APP类型-版本号获取广告数
				int count = getAreaVersionSum(ClientTypeEnum.CVS.getCode(), item, map, areaVo);
				//如果返回的数据不是非空则表示广告已经上限
				result = validateAcrossQty(advert, count, "便利店APP", item);
				if(StringUtils.isNotBlank(result)){
					return result;
				}
			}
		}
		
        //手机开门页广告/APP闪屏广告/APP首页广告(管家)
        if(CollectionUtils.isNotEmpty(countList) && CollectionUtils.isNotEmpty(advert.getStewardVersion())  
				&& (advert.getAdvertType().equals(AdvertTypeEnum.MOBILE_PHONE_DOOR.getIndex())
        		|| advert.getAdvertType().equals(AdvertTypeEnum.USER_APP_SPLASH_SCREEN.getIndex()) 
                || advert.getAdvertType().equals(AdvertTypeEnum.USER_APP_INDEX.getIndex()))){
        	logger.info("手机开门页广告/APP闪屏广告/APP首页广告 (管家)");
        	//循环管家版本集合
        	for(String item : advert.getStewardVersion()){
        		//通过地区ID-管家APP类型-版本号获取广告数
        		int count = getAreaVersionSum(ClientTypeEnum.STEWARD.getCode(), item, map, areaVo);
        		//如果返回的数据不是非空则表示广告已经上限
        		result = validateAcrossQty(advert, count, "管家APP", item);
				if(StringUtils.isNotBlank(result)){
					return result;
				}
			}
		}
		return result;
	} 
	
	/**
	 * @Description: 获取已发布的广告数
	 * @param clientType APP类型  ClientTypeEnum
	 * @param version APP版本号
	 * @param versionMap 已发布广告统计数，key:地区IDAPP类型版本号(例如1283V1.0)  value:ColumnAdvertVersionBo
	 * @param areaVo 当前发布广告的部分地区信息
	 * @return int 统计的广告记录数
	 * @throws Exception
	 * @author tangzj02
	 * @date 2017年3月17日
	 */
	private int getAreaVersionSum(Integer clientType, String version, Map<String, ColumnAdvertVersionBo> versionMap,
				ColumnAdvertAreaVo areaVo) throws Exception{
		int sum = 0;
		String key = clientType + version;
		//1、全省，0、市
	    if ("1".equals(areaVo.getType())) {
			for (String id : areaVo.getAreaIds()) {
				int tempCount = calcSum(id + key, versionMap, sum);
				//由于历史广告没有对应的APP类型以及版本号，则将其默认为V1.0版本
				if("V1.0".equals(version)){
					tempCount = calcSum(areaVo.getpId() + version, versionMap, tempCount);
				}
				//如果选择的地区是全省省，则该省下的城市发布数也计算在内，以最大发布数为准
				if (sum < tempCount) {
					sum = tempCount;
				}
			}
		} else {
			//如果是市，则该市所属省发布的广告也计算在内
			sum = calcSum(areaVo.getpId() + key, versionMap, sum);
			//由于历史广告没有对应的APP类型以及版本号，则将其默认为V1.0版本
			if("V1.0".equals(version)){
				sum = calcSum(areaVo.getpId() + version, versionMap, sum);
			}
		}
		    
	    //全国区域 获取直接发布在全国的广告统计数
	    sum = calcSum("0" + key, versionMap, sum);
	    //区域， 获取直接发布在当前地区的广告统计数
	    sum = calcSum(areaVo.getId() + key, versionMap, sum);
	    
	    //由于历史广告没有对应的APP类型以及版本号，则将其默认为V1.0版本
	    if("V1.0".equals(version)){
	    	//全国区域 获取直接发布在全国的广告统计数
	    	sum = calcSum("0" + version, versionMap, sum);
	    	//区域， 获取直接发布在当前地区的广告统计数
	    	sum = calcSum(areaVo.getId() + version, versionMap, sum);
	    }
	    
		return sum;
	}
	/**
	 * @Description: 计算广告发布总数
	 * @param key 地区 加 版本号  或者 地区 加 APP类型 加 版本号
	 * @param versionMap versionMap 已发布广告统计数，key:地区IDAPP类型版本号(例如1283V1.0)  value:ColumnAdvertVersionBo
	 * @param sum 已经统计广告发布数
	 * @return int  
	 * @author tangzj02
	 * @date 2017年3月20日
	 */
	private int calcSum(String key, Map<String, ColumnAdvertVersionBo> versionMap, int sum){
		//发布同一地区、时间、APP类型、APP版本的广告统计数
		ColumnAdvertVersionBo bo = versionMap.get(key);
	    if (null != bo) {
	    	sum += bo.getCount();
		}
		return sum;
	}
	
	/**
	 * @Description: 判断广告数是否已满
	 * @param advert 广告信息
	 * @param clientName APP引用名称
	 * @param version 发布的APP版本集合
	 * @param arcossTimeAdvertQty 地区已有最大数
	 * @return String  
	 * @author tangy
	 * @date 2016年11月28日
	 */
	private String validateAcrossQty(ColumnAdvert advert, Integer arcossTimeAdvertQty, String clientName, String version) {
		Integer advertType = advert.getAdvertType();
		String msg = "当前广告位置，" + clientName + "#已超过发布上限，无法提交广告";
		if (!"0".equals(advert.getBelongType())) {
			if (arcossTimeAdvertQty >= 2) {
				return msg.replace("#", version);
			}
		} else {
			if (advertType.equals(AdvertTypeEnum.USER_APP_SPLASH_SCREEN.getIndex()) && arcossTimeAdvertQty >= 1) {
				// 在同一个区域（城市）、同一个时间段、同一个版本下, 最多只能上传一张，且时间不能交叉
				return msg.replace("#", version);
			} else if (advertType.equals(AdvertTypeEnum.USER_APP_INDEX.getIndex()) && arcossTimeAdvertQty >= 5) {
				// 在同一个区域（城市）、同一个时间段、同一个版本下, 最多运营商能上传5张，且时间不能产生交叉；
				return msg.replace("#", version);
			} else if (advertType.equals(AdvertTypeEnum.MOBILE_PHONE_DOOR.getIndex()) && arcossTimeAdvertQty >= 1) {
				// 同一个区域（城市）、同一个时间段、同一个版本下 ,只允许发送1个手机开门页广告
				return msg.replace("#", version);
			} else if (advertType.equals(AdvertTypeEnum.USER_APP_INDEX_PARTITION.getIndex()) && arcossTimeAdvertQty >= 1) {
				// 同一个区域（城市）、同一个时间段、同一个版本下,最多运营商能上传1张，且时间不能产生交叉；
				return msg.replace("#", version);
			}
		}
		return null;
	}

	@Override
	public List<ColumnAdvert> findForApp(ColumnAdvertQueryParamDto advertQueryParamDto) {
		
		return advertMapper.findForApp(advertQueryParamDto);
	}
	
	@Override
	public List<ColumnAdvert> findForAppV220(ColumnAdvertQueryParamDto advertQueryParamDto) {
		return advertMapper.findForAppV220(advertQueryParamDto);
	}

	@Override
	public PageUtils<GoodsStoreActivitySkuDto> findAdvertGoodsByAdvertId(String advertId, String storeId, 
			Integer pageNumber, Integer pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<GoodsStoreActivitySkuDto> list = advertMapper.findAdvertGoodsByAdvertId(advertId, storeId);
		return new PageUtils<GoodsStoreActivitySkuDto>(list);
	}
	
	/**
	 * @Description:根据店铺活动类型 活动商品列表
	 * @param storeId
	 * @param saleType
	 * @author tuzhd
	 * @date 2017年3月13日
	 */
	@Override
	public List<GoodsStoreActivitySkuDto> findGoodsByActivityType(String storeId,Integer saleType) {
		return advertMapper.findGoodsByActivityType(storeId,saleType);
	}
}
