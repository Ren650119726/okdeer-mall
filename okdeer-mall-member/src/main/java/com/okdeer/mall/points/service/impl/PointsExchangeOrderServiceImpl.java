/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: PointsRuleServiceImpl.java 
 * @Date: 2016年3月17日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
package com.okdeer.mall.points.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.member.points.entity.PointsExchangeOrder;
import com.okdeer.mall.member.points.service.PointsExchangeOrderServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.points.mapper.PointsExchangeOrderMapper;

/**
 * 积分变动记录接口实现类
 * @project yschome-mall
 * @author luosm
 * @date 2016年3月17日 上午11:43:45
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.member.points.service.PointsExchangeOrderServiceApi")
public class PointsExchangeOrderServiceImpl implements PointsExchangeOrderServiceApi {

	private static final Logger logger = LoggerFactory.getLogger(PointsRuleServiceImpl.class);

	/**
	 * 自动注入pointsExchangeOrderMapper
	 */
	@Autowired
	private PointsExchangeOrderMapper pointsExchangeOrderMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String add(PointsExchangeOrder pointsExchangeOrder) throws ServiceException {
		logger.info("兑吧信息输入数据库", pointsExchangeOrder);
		pointsExchangeOrderMapper.insertSelective(pointsExchangeOrder);
		String id = pointsExchangeOrder.getId();
		return id;
	}

	@Override
	public PointsExchangeOrder findById(String id) throws ServiceException {
		logger.info("id", id);

		return pointsExchangeOrderMapper.selectByPrimaryKey(id);
	}

	@Override
	public PointsExchangeOrder findByDuibaOrderNo(String duibaOrderNo) throws ServiceException {
		return pointsExchangeOrderMapper.selectByDuibaOrderNo(duibaOrderNo);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateByDuibaMap(PointsExchangeOrder pointsExchangeOrder) throws ServiceException {
		pointsExchangeOrderMapper.updateByPrimaryKeySelective(pointsExchangeOrder);
	}

}
