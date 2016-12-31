/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: PointsRuleServiceImpl.java 
 * @Date: 2016年1月27日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */

package com.okdeer.mall.points.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.system.entity.SysBuyerUserThirdparty;
import com.okdeer.archive.system.entity.SysSmsVerifyCode;
import com.okdeer.mall.member.member.entity.SysBuyerExt;
import com.okdeer.mall.member.points.entity.PointsRecord;
import com.okdeer.mall.member.points.entity.PointsRule;
import com.okdeer.mall.member.points.enums.PointsRuleCode;
import com.okdeer.mall.member.points.service.PointsRuleServiceApi;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.ca.api.buyeruser.entity.SysBuyerUserDto;
import com.okdeer.ca.api.buyeruser.service.ISysBuyerUserApi;
import com.okdeer.common.exception.ApiException;
import com.okdeer.mall.member.mapper.SysBuyerExtMapper;
import com.okdeer.mall.points.mapper.PointsRecordMapper;
import com.okdeer.mall.points.mapper.PointsRuleMapper;
import com.okdeer.mall.system.mapper.SysBuyerUserThirdpartyMapper;
import com.okdeer.mall.system.mapper.SysSmsVerifyCodeMapper;

/**
 * 积分规则接口实现类
 * @project yschome-mall
 * @author zhongy
 * @date 2016年1月27日 上午11:40:09
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.member.points.service.PointsRuleServiceApi")
public class PointsRuleServiceImpl implements PointsRuleServiceApi {

	private static final Logger logger = LoggerFactory.getLogger(PointsRuleServiceImpl.class);

	/**
	 * 自动注入pointsRuleMapper
	 */
	@Autowired
	private PointsRuleMapper pointsRuleMapper;

	@Resource
	private PointsRecordMapper pointsRecordMapper;

	@Resource
	private SysSmsVerifyCodeMapper sysSmsVerifyCodeMapper;

	@Resource
	private SysBuyerUserThirdpartyMapper sysBuyerUserThirdpartyMapper;

	@Reference
	private ISysBuyerUserApi sysBuyerUserApi;

	@Autowired
	private SysBuyerExtMapper sysBuyerExtMapper;

	@Override
	public PointsRule findById(String id) throws ServiceException {
		logger.debug("主键查询id={}", id);
		return pointsRuleMapper.selectByPrimaryKey(id);
	}

	@Override
	public PageUtils<PointsRule> queryByParam(Integer pageNumber, Integer pageSize) throws ServiceException {
		logger.debug("积分规则查询请求参数，pageNumber={}，pageSize={}", pageNumber, pageSize);
		PageHelper.startPage(pageNumber, pageSize, true);
		List<PointsRule> list = pointsRuleMapper.queryByParam();
		return new PageUtils<PointsRule>(list);
	}

	@Override
	public List<PointsRule> queryAll() throws ServiceException {
		logger.debug("查询所有积分规则");
		return pointsRuleMapper.queryByParam();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void add(PointsRule pointsRule) throws ServiceException {
		logger.debug("添加积分规则请求参数,pointsRule={}", pointsRule);
		// 1 校验code是否重复
		PointsRule rule = pointsRuleMapper.selectByCode(pointsRule.getCode());
		if (rule != null) {
			throw new ServiceException("该积分规则" + pointsRule.getCode() + "已存在");
		}
		// 2 添加积分规则
		pointsRule.setId(UuidUtils.getUuid());
		pointsRule.setDisabled(Disabled.valid);
		pointsRule.setStatus(1);
		pointsRule.setCreateTime(new Date());
		pointsRule.setUpdateTime(new Date());
		pointsRuleMapper.insert(pointsRule);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addSelective(PointsRule pointsRule) throws ServiceException {
		logger.debug("按需添加积分规则请求参数,pointsRule={}", pointsRule);
		// 1 校验code是否重复
		PointsRule rule = pointsRuleMapper.selectByCode(pointsRule.getCode());
		if (rule != null) {
			throw new ServiceException("该积分规则" + pointsRule.getCode() + "已存在");
		}
		// 2 添加积分规则
		pointsRule.setId(UuidUtils.getUuid());
		pointsRule.setCreateTime(new Date());
		pointsRule.setUpdateTime(new Date());
		pointsRuleMapper.insertSelective(pointsRule);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateByIdSelective(PointsRule pointsRule) throws ServiceException {
		logger.debug("更新积分规则请求参数,pointsRule={}", pointsRule);
		pointsRule.setUpdateTime(new Date());
		pointsRuleMapper.updateByPrimaryKeySelective(pointsRule);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteById(String id) throws ServiceException {
		logger.debug("删除积分规则请求参数,id={}", id);
		pointsRuleMapper.deleteByPrimaryKey(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateStatus(List<String> ids, Integer status) throws ServiceException {
		pointsRuleMapper.batchUpdateStatus(ids, status, new Date());
	}

	@Override
	public List<PointsRule> queryValidList() throws ServiceException {
		// TODO Auto-generated method stub
		return pointsRuleMapper.findValidList();
	}

	/**
	 * DESC: 添加买家用户信息、修改验证码状态、添加第三方平台账号与本平台账号映射
	 * @author LIU.W
	 * @param sysSmsVerifyCodeUpdate
	 * @param buyerUserThirdparty
	 * @throws ServiceException
	 */
	@Transactional(rollbackFor = Exception.class)
	public String addSysBuyerSync(SysBuyerUserDto sysBuyerUserDto, SysSmsVerifyCode sysSmsVerifyCodeUpdate,
			SysBuyerUserThirdparty buyerUserThirdparty) throws ApiException, ServiceException {

		try {

			String buyerId = UuidUtils.getUuid();
			/**
			 * 1. 更新验证码状态
			 */
			if (null != sysSmsVerifyCodeUpdate) {
				sysSmsVerifyCodeMapper.updateByPrimaryKeySelective(sysSmsVerifyCodeUpdate);
			}

			/**
			 * 2. 添加第三方平台与自平台账号映射关系
			 */
			if (null != buyerUserThirdparty) {
				buyerUserThirdparty.setBuyerUserId(buyerId);
				sysBuyerUserThirdpartyMapper.insertSelective(buyerUserThirdparty);
			}
			/**
			 * 3. 添加用户注册信息
			 */
			if (null != sysBuyerUserDto) {

				/**
				 * 3.1 添加买家用户扩展表信息并注册用户送积分
				 */
				PointsRule pointsRule = new PointsRule();
				pointsRule.setCode(PointsRuleCode.REGISTER.getCode());
				/**
				 * 3.2 根据积分规则编码code查询规则
				 */
				PointsRule pointRule = pointsRuleMapper.selectByCode(pointsRule.getCode());
				if (null == pointRule) {
					return buyerId;
				}
				/**
				 * 3.3 添加用户扩展信息及积分详细记录
				 */
				SysBuyerExt sysBuyerExt = new SysBuyerExt();
				sysBuyerExt.setId(UuidUtils.getUuid());
				sysBuyerExt.setUserId(buyerId);
				sysBuyerExt.setPointVal(pointRule.getPointVal());
				sysBuyerExtMapper.insertSelective(sysBuyerExt);

				PointsRecord pointsRecord = new PointsRecord();
				pointsRecord.setId(UuidUtils.getUuid());
				pointsRecord.setUserId(buyerId);
				pointsRecord.setCode(pointRule.getCode());
				pointsRecord.setPointVal(pointRule.getPointVal());
				pointsRecord.setType((byte) 0);
				pointsRecord.setDescription(pointRule.getRemark());
				pointsRecord.setCreateTime(new Date());
				pointsRecordMapper.insert(pointsRecord);
				/**
				 * 3.4 添加用户信息
				 */
				sysBuyerUserDto.setId(buyerId);
				sysBuyerUserApi.save(sysBuyerUserDto);
			}

			return buyerId;
		} catch (Exception e) {
			throw new ServiceException("添加用户失败!", e);
		}
	}

	@Override
	public PointsRule selectByCode(PointsRule pointsRule) throws ServiceException {
		return pointsRuleMapper.selectByCode(pointsRule.getCode());
	}
}
