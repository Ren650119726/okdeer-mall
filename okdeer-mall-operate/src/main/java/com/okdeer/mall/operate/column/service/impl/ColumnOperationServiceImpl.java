/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: ColumnOperationServiceImpl.java 
 * @Date: 2016年1月14日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
package com.okdeer.mall.operate.column.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.mall.common.enums.DistrictType;
import com.okdeer.mall.common.utils.RobotUserUtil;
import com.okdeer.mall.operate.column.mapper.ColumnOperationAreaMapper;
import com.okdeer.mall.operate.column.mapper.ColumnOperationCommunityMapper;
import com.okdeer.mall.operate.column.mapper.ColumnOperationMapper;
import com.okdeer.mall.operate.column.service.ColumnOperationService;
import com.okdeer.mall.operate.dto.ColumnOperationParamDto;
import com.okdeer.mall.operate.entity.*;
import com.okdeer.mall.operate.enums.State;
import com.okdeer.mall.operate.service.IColumnOperationServiceApi;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 运营栏目service实现
 * @project yschome-mall
 * @author wusw
 * @date 2016年1月14日 上午9:33:00
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构 4.1			2016-7-19			wusw	                         修改查询运营栏目时间、地区冲突的方法
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.operate.service.IColumnOperationServiceApi")
public class ColumnOperationServiceImpl implements ColumnOperationService, IColumnOperationServiceApi {

	private static final Logger log = Logger.getLogger(ColumnOperationServiceImpl.class);

	/**
	 * 运营栏目mapper
	 */
	@Autowired
	private ColumnOperationMapper columnOperationMapper;

	/**
	 * 运营栏目区域关联mapper
	 */
	@Autowired
	private ColumnOperationAreaMapper columnOperationAreaMapper;

	/**
	 * 运营栏目小区关联mapper
	 */
	@Autowired
	private ColumnOperationCommunityMapper columnOperationCommunityMapper;

	/**
	 * @desc 根据条件获取运营栏目任务列表（参数类型实体）
	 *
	 * @param columnOperation
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public PageUtils<ColumnOperation> findByEntity(ColumnOperation columnOperation, int pageNumber, int pageSize)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true);
		columnOperation.setDisabled(Disabled.valid);
		List<ColumnOperation> result = columnOperationMapper.selectByEntity(columnOperation);
		if (result == null) {
			result = new ArrayList<ColumnOperation>();
		}
		return new PageUtils<ColumnOperation>(result);
	}

	/**
	 * @desc 根据主键id获取运营栏目任务信息
	 *
	 * @param id 主键id
	 * @return 运营栏目任务信息
	 * @throws ServiceException
	 */
	@Override
	public ColumnOperation getById(String id) throws ServiceException {
		return columnOperationMapper.selectByPrimaryKey(id);
	}

	/**
	 * 
	 * 根据主键id获取运营栏目任务详细信息 （包括区域、小区信息）
	 *
	 * @param id 主键id
	 * @return 运营栏目任务详细信息 （包括区域、小区信息）
	 * @throws ServiceException
	 */
	@Override
	public ColumnOperationVo getColumnOperationVoById(String id) throws ServiceException {
		return columnOperationMapper.selectOperationAssociateById(id);
	}

	/**
	 * @desc 添加运营栏目任务
	 *
	 * @param columnOperation 运营栏目任务
	 * @param currentOperateUserId
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addColumnOperation(ColumnOperation columnOperation, String currentOperateUserId)
			throws ServiceException {

		columnOperation.setId(UuidUtils.getUuid());
		columnOperation.setDisabled(Disabled.valid);
		columnOperation.setState(State.noStart);
		columnOperation.setCreateUserId(currentOperateUserId);
		columnOperation.setUpdateUserId(currentOperateUserId);
		Date date = new Date();
		columnOperation.setCreateTime(date);
		columnOperation.setUpdateTime(date);

		columnOperationMapper.insertSelective(columnOperation);

		// 插入与运营栏目关联的区域或者小区数据
		this.insertAreaInfo(columnOperation);
	}

	/**
	 * @desc 修改运营栏目任务信息
	 *
	 * @param columnOperation 运营栏目任务
	 * @param currentOperateUserId 当前登陆用户ID
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateColumnOperation(ColumnOperation columnOperation, String currentOperateUserId)
			throws ServiceException {

		ColumnOperation columnOperationSelect = columnOperationMapper.selectByPrimaryKey(columnOperation.getId());

		columnOperation.setUpdateTime(new Date());
		columnOperation.setUpdateUserId(currentOperateUserId);
		// 状态为已结束的运营栏目任务修改后，状态修改为未开始
		if (columnOperationSelect.getState() == State.complete) {
			columnOperation.setState(State.noStart);
		}
		columnOperationMapper.updateByPrimaryKeySelective(columnOperation);

		// 删除运营栏目与区域关联信息
		columnOperationAreaMapper.deleteByOperationId(columnOperation.getId());
		// 删除运营栏目与小区关联信息
		columnOperationCommunityMapper.deleteByOperationId(columnOperation.getId());

		// 重新插入运营栏目与区域或小区信息
		this.insertAreaInfo(columnOperation);

	}

	/**
	 * @desc 逻辑删除运营栏目任务（单个）
	 *
	 * @param id 主键id
	 * @param currentOperateUserId 当前登陆用户ID
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteColumnOperation(String id, String currentOperateUserId) throws ServiceException {
		columnOperationMapper.deleteById(id, Disabled.invalid, new Date(), currentOperateUserId);
	}

	/**
	 * @desc 停用运营栏目任务
	 *
	 * @param id 主键id
	 * @param currentOperateUserId 当前登陆用户ID
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void disableColumnOperation(String id, String currentOperateUserId) throws ServiceException {
		columnOperationMapper.disableById(id, State.complete, new Date(), currentOperateUserId);
	}

	/**
	 * @desc 查询指定名称相同的运营栏目任务记录数量  
	 *
	 * @param columnOperation
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public int selectCountByName(ColumnOperation columnOperation) throws ServiceException {
		columnOperation.setDisabled(Disabled.valid);
		return columnOperationMapper.selectCountByName(columnOperation);
	}

	/**
	 * @desc 查询与指定开始结束时间有交集、指定区域有交集的运营栏目任务记录数量  
	 *
	 * @param columnOperation
	 * @param areaIdList 区域ID（省市ID）集合
	 * @param associateIdList 省下所有市和市所在省的id集合
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public int selectCountByDistrict(ColumnOperation columnOperation, List<String> areaIdList,
			List<String> associateIdList) throws ServiceException {
		// Begin 重构4.1  add by wusw  20160719
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("disabled", Disabled.valid);
		params.put("noStartStatus", State.noStart);
		params.put("startStatus", State.underWay);
		params.put("type", columnOperation.getType());
		params.put("startTime", columnOperation.getStartTime());
		params.put("endTime", columnOperation.getStartTime());
		params.put("id", columnOperation.getId());
		params.put("areaType", columnOperation.getAreaType());
		params.put("areaIdList", areaIdList);
		params.put("associateIdList", associateIdList);
		
		return columnOperationMapper.selectCountByDistrict(params);
		// Begin 重构4.1  add by wusw  20160719
	}

	/**
	 * @desc 查询与指定开始结束时间有交集、指定区域有交集的运营栏目任务记录数量
	 *
	 * @param paramDto ColumnOperationParamDto
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = true)
	@Override
	public int selectCountVersionByDistrict(ColumnOperationParamDto paramDto) throws Exception {
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("disabled", Disabled.valid);
		params.put("noStartStatus", State.noStart);
		params.put("startStatus", State.underWay);
		params.put("type", paramDto.getType());
		params.put("startTime", paramDto.getStartTime());
		params.put("endTime", paramDto.getStartTime());
		params.put("id", paramDto.getId());
		params.put("areaType", paramDto.getAreaType());
		params.put("areaIdList", paramDto.getAreaIdList());
		params.put("associateIdList", paramDto.getAssociateIdList());
		params.put("versionList", paramDto.getVersionList());
		return columnOperationMapper.selectCountByDistrict(params);
	}

	/**
	 * 
	 * 插入运营栏目与区域、小区关联信息
	 *
	 * @param columnOperation
	 */
	@Transactional(rollbackFor = Exception.class)
	private void insertAreaInfo(ColumnOperation columnOperation) {
		// 插入与运营栏目关联的区域或者小区数据
		if (columnOperation.getAreaType() == AreaType.area) {
			// 区域信息字符串格式：id-level,id-level,.....
			String areaIds = columnOperation.getAreaIds();
			if (StringUtils.isNotEmpty(areaIds)) {
				String[] areaArr = areaIds.split(",");
				List<ColumnOperationArea> areaList = new ArrayList<ColumnOperationArea>();
				ColumnOperationArea columnOperationArea = null;
				for (String areaIdType : areaArr) {
					String[] areaIdTypeArr = areaIdType.split("-");
					columnOperationArea = new ColumnOperationArea();
					columnOperationArea.setId(UuidUtils.getUuid());
					columnOperationArea.setOperationId(columnOperation.getId());
					columnOperationArea.setAreaId(areaIdTypeArr[0]);
					// 根据level判断是省还是市
					if (areaIdTypeArr[1].equals("1")) {
						columnOperationArea.setType(DistrictType.province);
					} else {
						columnOperationArea.setType(DistrictType.city);
					}
					areaList.add(columnOperationArea);
				}
				columnOperationAreaMapper.insertAreaBatch(areaList);
			}

		} else if (columnOperation.getAreaType() == AreaType.community) {
			// 小区信息字符串格式：id,id,.....
			String areaIds = columnOperation.getAreaIds();
			if (StringUtils.isNotEmpty(areaIds)) {
				List<ColumnOperationCommunity> areaList = new ArrayList<ColumnOperationCommunity>();
				ColumnOperationCommunity columnOperationCommunity = null;
				String[] areaIdArr = areaIds.split(",");
				for (String areaId : areaIdArr) {
					columnOperationCommunity = new ColumnOperationCommunity();
					columnOperationCommunity.setId(UuidUtils.getUuid());
					columnOperationCommunity.setOperationId(columnOperation.getId());
					columnOperationCommunity.setCommunityId(areaId);
					areaList.add(columnOperationCommunity);
				}
				columnOperationCommunityMapper.insertCommunityBatch(areaList);
			}

		}
	}

	@Override
	public List<ColumnOperationQueryVo> findByCityOrCommunity(Map<String, Object> params) throws ServiceException {
		List<ColumnOperationQueryVo> result = columnOperationMapper.selectByCityOrCommunity(params);
		/*
		 * if (result != null) { for (ColumnOperationQueryVo vo:result) {
		 * vo.setName(vo.getType().getValue()); vo.setType(null); } }
		 */
		return result;
	}

	@Override
	public int selectCountByUpdateTime(Date updateTime) throws ServiceException {

		return columnOperationMapper.selectCountByUpdateTime(updateTime);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateByJob() {
		log.info("运营商后台运营栏目定时器开始");

		List<ColumnOperation> columnOperationList = columnOperationMapper.listByJob(new Date());

		if (columnOperationList != null && columnOperationList.size() > 0) {

			List<String> listIdIng = new ArrayList<String>();
			List<String> listIdEnd = new ArrayList<String>();

			for (ColumnOperation a : columnOperationList) {
				// 未开始的
				if (a.getState() == State.noStart) {
					listIdIng.add(a.getId());
				}
				// 进行中的改为已结束的
				else if (a.getState() == State.underWay) {
					listIdEnd.add(a.getId());
				}
			}

			String updateUserId = RobotUserUtil.getRobotUser().getId();
			Date updateTime = new Date();

			// 改为进行中
			if (listIdIng != null && listIdIng.size() > 0) {
				columnOperationMapper.updateStateByIds(listIdIng, State.underWay, updateTime, updateUserId);
			}
			// 改为已经结束
			if (listIdEnd != null && listIdEnd.size() > 0) {
				columnOperationMapper.updateStateByIds(listIdEnd, State.complete, updateTime, updateUserId);
			}

		}
		log.info("运营商后台运营栏目定时器结束");
	}
}
