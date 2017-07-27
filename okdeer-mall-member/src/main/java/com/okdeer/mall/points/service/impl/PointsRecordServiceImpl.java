/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: PointsRecordServiceImpl.java 
 * @Date: 2016年1月27日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */

package com.okdeer.mall.points.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.mall.member.points.entity.PointsRecord;
import com.okdeer.mall.member.points.service.PointsRecordServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.points.mapper.PointsRecordMapper;

/**
 * 积分变动记录接口实现类
 * @project yschome-mall
 * @author zhongy
 * @date 2016年1月27日 上午11:43:45
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.member.points.service.PointsRecordServiceApi")
public class PointsRecordServiceImpl implements PointsRecordServiceApi {

	private static final Logger logger = LoggerFactory.getLogger(PointsRecordServiceImpl.class);

	/**
	 * 自动注入pointsRecordMapper
	 */
	@Autowired
	private PointsRecordMapper pointsRecordMapper;

	@Override
	public PageUtils<PointsRecord> findByParams(Map<String, Object> param, Integer pageNumber, Integer pageSize)
			throws ServiceException {
		logger.debug("查询用户积分变动记录请求参数，param={}", param);
		PageHelper.startPage(pageNumber, pageSize, true);
		List<PointsRecord> list = pointsRecordMapper.selectByParams(param);
		return new PageUtils<PointsRecord>(list);
	}

	@Override
	public Integer countByParams(Map<String, Object> param) throws ServiceException {
		return pointsRecordMapper.countByParams(param);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void add(PointsRecord pointsRecord) throws ServiceException {
		logger.debug("添加用户积分变动记录请求参数，pointsRecord={}", pointsRecord);
		pointsRecord.setId(UuidUtils.getUuid());
		pointsRecord.setCreateTime(new Date());
		pointsRecordMapper.insert(pointsRecord);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addSelective(PointsRecord pointsRecord) throws ServiceException {
		logger.debug("按需添加用户积分变动记录请求参数，pointsRecord={}", pointsRecord);
		pointsRecord.setId(UuidUtils.getUuid());
		pointsRecord.setCreateTime(new Date());
		pointsRecordMapper.insertSelective(pointsRecord);
	}

	@Override
	public List<PointsRecord> findByUserId(Map<String, Object> param) throws ServiceException {
		return pointsRecordMapper.selectByParams(param);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insert(PointsRecord pointsRecord) throws ServiceException {
		pointsRecordMapper.insert(pointsRecord);
	}

}
