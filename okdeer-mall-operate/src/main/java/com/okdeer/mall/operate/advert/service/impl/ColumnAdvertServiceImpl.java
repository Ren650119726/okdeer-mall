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

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.system.entity.PsmsSmallCommunityInfo;
import com.okdeer.archive.system.entity.SysUser;
import com.okdeer.archive.system.service.IPsmsSmallCommunityInfoServiceApi;
import com.okdeer.mall.advert.entity.AdvertDetailVo;
import com.okdeer.mall.advert.entity.ColumnAdvert;
import com.okdeer.mall.advert.entity.ColumnAdvertApproval;
import com.okdeer.mall.advert.entity.ColumnAdvertArea;
import com.okdeer.mall.advert.entity.ColumnAdvertCommunity;
import com.okdeer.mall.advert.entity.ColumnAdvertInfo;
import com.okdeer.mall.advert.entity.ColumnAdvertQueryVo;
import com.okdeer.mall.advert.entity.ColumnAdvertVo;
import com.okdeer.mall.advert.enums.AdvertIsPayEnum;
import com.okdeer.mall.advert.enums.AdvertStatusEnum;
import com.okdeer.mall.advert.service.IColumnAdvertServiceApi;
import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.mall.common.enums.AuditStatusEnum;
import com.okdeer.mall.common.enums.DistrictType;
import com.okdeer.mall.common.enums.UserType;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.operate.advert.mapper.ColumnAdvertApprovalMapper;
import com.okdeer.mall.operate.advert.mapper.ColumnAdvertAreaMapper;
import com.okdeer.mall.operate.advert.mapper.ColumnAdvertCommunityMapper;
import com.okdeer.mall.operate.advert.mapper.ColumnAdvertInfoMapper;
import com.okdeer.mall.operate.advert.mapper.ColumnAdvertMapper;
import com.okdeer.mall.operate.advert.service.ColumnAdvertService;

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
	 * 店铺小区关系Mapper注入
	 */
	@Autowired
	private ColumnAdvertCommunityMapper advertCommunityMapper;

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

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.advert.service.ColumnAdvertService#findAcrossTimeAdvertQty
	 */
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
		advert.setPv(0);
		String targetUrl = advert.getTargetUrl();
		if (!StringUtils.isNullOrEmpty(targetUrl)) {
			targetUrl = StringEscapeUtils.unescapeHtml3(targetUrl);
			advert.setTargetUrl(targetUrl);
		}
		this.advertMapper.insert(advert);

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
		this.advertMapper.updateByPrimaryKeySelective(advert);

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
		return this.advertMapper.selectByPrimaryKey(id);
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

		this.advertMapper.updateByPrimaryKeySelective(advert);
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

		this.advertMapper.updateByPrimaryKeySelective(advert);
	}

	@Override
	public List<ColumnAdvert> getAdvertById(Map<String, Object> params) {
		List<ColumnAdvert> list = this.advertMapper.getAdvertById(params);
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

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.advert.service.ColumnAdvertService#updateAdvertInfo(com.okdeer.mall.advert.entity.ColumnAdvert)
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int updateAdvertInfo(ColumnAdvert advert) {
		String targetUrl = advert.getTargetUrl();
		if (!StringUtils.isNullOrEmpty(targetUrl)) {
			targetUrl = StringEscapeUtils.unescapeHtml3(targetUrl);
			advert.setTargetUrl(targetUrl);
		}
		return this.advertMapper.updateByPrimaryKeySelective(advert);
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
				this.advertMapper.updateByPrimaryKeySelective(advert);
			}
		}

		// 查询进行中的广告结束时间小于等于当前时间状态改为已结束
		adverts = this.advertMapper.getAdvertStartForJob(1, new Date(), "ca.end_time", "<=");
		// 将状态改为已结束
		if (adverts != null && !adverts.isEmpty()) {
			for (ColumnAdvert advert : adverts) {
				advert.setStatus(AdvertStatusEnum.FINISH);
				this.advertMapper.updateByPrimaryKeySelective(advert);
			}
		}

		// begin 现在已经不涉及缴费了 add by zhulq 2016-8-6
		// 查询未交费结束时间小于等于的当前时间状态改为已过期
		/*
		 * adverts = this.advertMapper.getAdvertStartForJob(1, new Date(), "end_time", "<="); // 将状态改为已过期 if (adverts !=
		 * null && !adverts.isEmpty()) { for (ColumnAdvert advert : adverts) {
		 * advert.setStatus(AdvertStatusEnum.OUT_OF_DATE); this.advertMapper.updateByPrimaryKeySelective(advert); } }
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

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.advert.service.IColumnAdvertServiceApi#findAcrossTimeAdvert()
	 */
	@Transactional(readOnly = true)
	@Override
	public int findAcrossTimeAdvert(ColumnAdvert advert, List<String> areaIdList) {
		return this.advertMapper.findAcrossTimeAdvert(advert);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.advert.service.IColumnAdvertServiceApi#findMobileDoorAdvertByMap(java.util.Map)
	 */
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
		advertMapper.updateByPrimaryKeySelective(columnAdvert);
	}
	// end  add　　by zengjz  修改广告的排序值
}
