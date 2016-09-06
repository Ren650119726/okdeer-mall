/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: PointsBuriedServiceImpl.java 
 * @Date: 2016年1月30日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */

package com.okdeer.mall.points.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.system.entity.SysDict;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.member.member.entity.SysBuyerExt;
import com.okdeer.mall.member.points.entity.PointsRecord;
import com.okdeer.mall.member.points.entity.PointsRule;
import com.okdeer.mall.member.points.enums.PointsRuleCode;
import com.okdeer.mall.member.points.service.PointsBuriedServiceApi;
import com.okdeer.mall.system.service.SysBuyerUserServiceApi;
import com.yschome.base.common.exception.ServiceException;
import com.yschome.base.common.utils.StringUtils;
import com.yschome.base.common.utils.UuidUtils;
import com.okdeer.mall.member.mapper.SysBuyerExtMapper;
import com.okdeer.mall.points.mapper.PointsRecordMapper;
import com.okdeer.mall.points.mapper.PointsRuleMapper;
import com.okdeer.mall.points.service.PointsBuriedService;
import com.okdeer.mall.system.mapper.SysDictMapper;

/***
 * 
 * ClassName: PointsBuriedServiceImpl 
 * @Description: 积分埋点接口实现类
 * @author luosm
 * @date 2016年1月30日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			 2016-08-09			luosm			优化注册加积分
 */

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.member.points.service.PointsBuriedServiceApi")
public class PointsBuriedServiceImpl implements PointsBuriedService, PointsBuriedServiceApi {

	private static final Logger logger = LoggerFactory.getLogger(PointsRuleServiceImpl.class);

	/**
	 * 自动注入pointsRuleMapper
	 */
	@Autowired
	private PointsRuleMapper pointsRuleMapper;

	/**
	 * 自动注入会员扩展dao
	 */
	@Autowired
	private SysBuyerExtMapper sysBuyerExtMapper;

	/**
	 * 自动注入pointsRecordMapper
	 */
	@Autowired
	private PointsRecordMapper pointsRecordMapper;

	/**
	 * 自动注入sysDictMapper
	 */
	@Autowired
	private SysDictMapper sysDictMapper;

	@Reference(check = false)
	private SysBuyerUserServiceApi sysBuyerUserService;

	// ***************************app消费获取积分start***********************************************//
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String addConsumerPoints(String userId, BigDecimal totalAmount) throws Exception {
		logger.debug("app消费积分获取请求参数，userId={},code,{},totalAmount={}", userId, PointsRuleCode.APP_CONSUME, totalAmount);
		if (StringUtils.isBlank(userId)) {
			return responseResult(Constant.FOUR, "userId为空！");
		}
		if (totalAmount == null) {
			return responseResult(Constant.FOUR, "totalAmount为空！");
		}

		PointsRule pointsRule = new PointsRule();
		pointsRule.setCode(PointsRuleCode.APP_CONSUME.getCode());
		// 1 根据积分规则编码code查询规则
		PointsRule rule = pointsRuleMapper.selectByCode(pointsRule);
		if (rule == null) {
			return responseResult(Constant.ONE, "积分规则停用");
		}
		// 2 计算所获得积分,舍掉小数取整计算
		Integer limitPointVal = rule.getPointVal();
		Integer consumerPointVal = (int) Math.floor(totalAmount.doubleValue()) * limitPointVal;
		logger.debug("app消费积分获得积分，pointVal={}", consumerPointVal);
		// 3 判断是否总积分限制0：不限制，1：限制
		Integer isTotalPointLimit = rule.getIsTotalPointLimit();
		if (isTotalPointLimit == 0) {
			// 5.1 查询获取总积分并更新入库
			updateSumPoints(userId, consumerPointVal);
			// 5.2 添加积分记录对象
			addPointsRecord(userId, rule, consumerPointVal);
			return responseResult(Constant.ZERO, "成功");
		}
		// 4 数据词典查询日积分总额限制
		Integer totalPointLimit = 0;
		List<SysDict> sysDicts = sysDictMapper.selectByType("totalPointLimit");
		if (sysDicts != null && sysDicts.size() > 0) {
			totalPointLimit = Integer.valueOf(sysDicts.get(0).getValue());
		}
		// 5 条件查询当天获得汇总积分
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		Integer currentSum = pointsRecordMapper.currentSumByParams(params);
		// 可添加积分 = 当天获得汇总积分 - 当天已获取积分
		Integer surplusPoints = totalPointLimit - currentSum;
		// 6 判断当日获取积分是否大于总积分
		if (surplusPoints > 0) {
			Integer pointVal = 0;
			if (surplusPoints > consumerPointVal) {
				pointVal = consumerPointVal;
			} else {
				pointVal = surplusPoints;
			}
			// 6.1 查询获取总积分并更新入库
			updateSumPoints(userId, pointVal);
			// 6.2 添加积分记录对象
			addPointsRecord(userId, rule, pointVal);
		}
		return responseResult(Constant.ZERO, "成功");
	}

	// ***************************app消费获取积分end***********************************************//

	// ***************************注册/登录/授权/邀请/查看通知获得积分start*****************************//

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer doProcessPoints(String userId, PointsRuleCode ruleCode) throws ServiceException {
		if (StringUtils.isBlank(userId) || null == ruleCode) {
			throw new ServiceException("请求参数为空!");
		}

		PointsRule pointsRuleCondition = new PointsRule();
		pointsRuleCondition.setCode(ruleCode.getCode());
		// 1 根据积分规则编码code查询规则
		PointsRule pointsRule = pointsRuleMapper.selectByCode(pointsRuleCondition);
		if (pointsRule == null) {
			logger.debug("积分规则编码为空!");
			return 0;
		}
		if (pointsRule.getStatus() == 0) {
			logger.debug("没有启用的积分规则！！");
			return 0;
		}

		int limitNumDay = pointsRule.getLimitNum(); // 每日限制次数
		if (limitNumDay > 0) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", userId);
			params.put("code", ruleCode.getCode());
			
			//begin add by luosm 20160809
			// 查询此账号是否已注册
			if (ruleCode.getCode() == PointsRuleCode.REGISTER.getCode()) {
				List<PointsRecord> records = pointsRecordMapper.selectByParams(params);
				if (records.size() > 0) {
					logger.debug("已注册！！！");
					return 0;
				}
			}
			//end add by luosm 20160809
			
			// 1.1 查询是否有记录
			List<PointsRecord> listPointRecords = pointsRecordMapper.selectDayByParams(params);
			int surplusNumDay = limitNumDay - listPointRecords.size();
			if (surplusNumDay <= 0) {
				logger.debug("当天获得积分次数已达上限！！");
				return 0;
			}
		}

		// 数据词典查询日积分总额限制
		Integer totalPointLimit = 0;
		if (1 == pointsRule.getIsTotalPointLimit()) {
			List<SysDict> sysDicts = sysDictMapper.selectByType("totalPointLimit");
			if (sysDicts != null && sysDicts.size() > 0) {
				totalPointLimit = Integer.valueOf(sysDicts.get(0).getValue());
			}
		}
		int pointVal = pointsRule.getPointVal();
		// 总额限制
		if (totalPointLimit != 0) {
			// 条件查询当天获得汇总积分
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", userId);
			Integer currentSum = pointsRecordMapper.currentSumByParams(params);
			// 天限制积分 > 当天获得汇总积分 + 当前规则积分
			Integer surplusPoints = totalPointLimit - currentSum;
			pointVal = (surplusPoints > pointVal) ? pointVal : surplusPoints;

		}
		if (pointVal > 0) {
			// 1.2更新总积分
			SysBuyerExt sysBuyerExt = sysBuyerExtMapper.selectByUserId(userId);
			if (sysBuyerExt != null) {
				Integer currentSumPoints = sysBuyerExt.getPointVal();
				sysBuyerExt.setPointVal(pointVal + currentSumPoints);
				sysBuyerExtMapper.updateByPrimaryKeySelective(sysBuyerExt);
			} else {
				sysBuyerExt = new SysBuyerExt();
				sysBuyerExt.setId(UuidUtils.getUuid());
				sysBuyerExt.setUserId(userId);
				sysBuyerExt.setPointVal(pointVal);
				sysBuyerExtMapper.insertSelective(sysBuyerExt);
			}
			// 1.3添加积分详细记录
			PointsRecord pointsRecord = new PointsRecord();
			pointsRecord.setId(UuidUtils.getUuid());
			pointsRecord.setUserId(userId);
			pointsRecord.setCode(pointsRule.getCode());
			pointsRecord.setPointVal(pointVal);
			pointsRecord.setType((byte) 0);
			pointsRecord.setDescription(pointsRule.getRemark());
			pointsRecord.setCreateTime(new Date());
			pointsRecordMapper.insert(pointsRecord);
			return pointVal;
		}
		return 0;
	}

	// ***************************注册/登录/授权/邀请/查看通知获得积分end*****************************//

	// ***************************app消费获得start*****************************//

	/**
	 * app消费获得积分
	 *@author luosm
	 *@param userId：用户id
	 *@param totalMoney：消费总金额
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean doConsumePoints(String userId, BigDecimal totalMoney) throws ServiceException {
		if (StringUtils.isBlank(userId) || null == totalMoney) {
			throw new ServiceException("请求参数为空!");
		}

		if (totalMoney.doubleValue() < 1) {
			return false;
		}
		Integer totalNum = (int) Math.floor(totalMoney.doubleValue());
		PointsRuleCode ruleCode = PointsRuleCode.APP_CONSUME;

		PointsRule pointsRuleCondition = new PointsRule();
		pointsRuleCondition.setCode(ruleCode.getCode());
		// 1 根据积分规则编码code查询规则
		PointsRule pointsRule = pointsRuleMapper.selectByCode(pointsRuleCondition);
		if (pointsRule == null) {
			return false;
		}
		if (pointsRule.getStatus() == 0) {
			return false;
		}

		int limitNumDay = pointsRule.getLimitNum(); // 每日限制次数
		if (limitNumDay > 0) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", userId);
			params.put("code", ruleCode.getCode());
			// 1.1 查询是否有记录
			List<PointsRecord> listPointRecords = pointsRecordMapper.selectDayByParams(params);
			int surplusNumDay = limitNumDay - listPointRecords.size();
			if (surplusNumDay <= 0) {
				return false;
			}
		}

		// 数据词典查询日积分总额限制
		Integer totalPointLimit = 0;
		if (1 == pointsRule.getIsTotalPointLimit()) {
			List<SysDict> sysDicts = sysDictMapper.selectByType("totalPointLimit");
			if (sysDicts != null && sysDicts.size() > 0) {
				totalPointLimit = Integer.valueOf(sysDicts.get(0).getValue());
			}
		}
		int pointVal = totalNum * pointsRule.getPointVal();
		// 总额限制
		if (totalPointLimit != 0) {
			// 条件查询当天获得汇总积分
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", userId);
			Integer currentSum = pointsRecordMapper.currentSumByParams(params);
			// 天限制积分 > 当天获得汇总积分 + 当前规则积分
			Integer surplusPoints = totalPointLimit - currentSum;
			pointVal = (surplusPoints > pointVal) ? pointVal : surplusPoints;

		}
		if (pointVal > 0) {
			// 1.2更新总积分
			SysBuyerExt sysBuyerExt = sysBuyerExtMapper.selectByUserId(userId);
			if (sysBuyerExt != null) {
				Integer currentSumPoints = sysBuyerExt.getPointVal();
				sysBuyerExt.setPointVal(pointVal + currentSumPoints);
				sysBuyerExtMapper.updateByPrimaryKeySelective(sysBuyerExt);
			} else {
				sysBuyerExt = new SysBuyerExt();
				sysBuyerExt.setId(UuidUtils.getUuid());
				sysBuyerExt.setUserId(userId);
				sysBuyerExt.setPointVal(pointVal);
				sysBuyerExtMapper.insertSelective(sysBuyerExt);
			}
			// 1.3添加积分详细记录
			PointsRecord pointsRecord = new PointsRecord();
			pointsRecord.setId(UuidUtils.getUuid());
			pointsRecord.setUserId(userId);
			pointsRecord.setCode(pointsRule.getCode());
			pointsRecord.setPointVal(pointVal);
			pointsRecord.setType((byte) 0);
			pointsRecord.setDescription(pointsRule.getRemark());
			pointsRecord.setCreateTime(new Date());
			pointsRecordMapper.insert(pointsRecord);
			return true;
		}
		return false;
	}

	// ***************************app消费获得积分end*****************************//

	// Begin 开门红包 added by tangy 2016-7-21
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> saveIntegralInfo(String userId, PointsRuleCode ruleCode) throws ServiceException {
		Map<String, String> resultMap = new HashMap<String, String>();
		if (StringUtils.isBlank(userId) || null == ruleCode) {
			throw new ServiceException("请求参数为空!");
		}

		PointsRule pointsRuleCondition = new PointsRule();
		pointsRuleCondition.setCode(ruleCode.getCode());
		// 根据积分规则编码code查询规则
		PointsRule pointsRule = pointsRuleMapper.selectByCode(pointsRuleCondition);
		if (pointsRule != null && pointsRule.getStatus() == 1) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", userId);
			params.put("code", ruleCode.getCode());
			// 查询是否有记录
			List<PointsRecord> listPointRecords = pointsRecordMapper.selectDayByParams(params);
			// 每日限制次数
			int limitNumDay = pointsRule.getLimitNum();
			// 积分值
			int pointVal = pointsRule.getPointVal();
			int surplusNumDay = limitNumDay - listPointRecords.size();
			if (surplusNumDay > 0) {
				// 更新总积分
				SysBuyerExt sysBuyerExt = sysBuyerExtMapper.selectByUserId(userId);
				if (sysBuyerExt != null) {
					Integer currentSumPoints = sysBuyerExt.getPointVal();
					sysBuyerExt.setPointVal(pointVal + currentSumPoints);
					sysBuyerExtMapper.updateByPrimaryKeySelective(sysBuyerExt);
				} else {
					sysBuyerExt = new SysBuyerExt();
					sysBuyerExt.setId(UuidUtils.getUuid());
					sysBuyerExt.setUserId(userId);
					sysBuyerExt.setPointVal(pointVal);
					sysBuyerExtMapper.insertSelective(sysBuyerExt);
				}

				// 添加积分详细记录
				PointsRecord pointsRecord = new PointsRecord();
				pointsRecord.setId(UuidUtils.getUuid());
				pointsRecord.setUserId(userId);
				pointsRecord.setCode(pointsRule.getCode());
				pointsRecord.setPointVal(pointVal);
				pointsRecord.setType((byte) 0);
				pointsRecord.setDescription(pointsRule.getRemark());
				pointsRecord.setCreateTime(new Date());
				pointsRecordMapper.insert(pointsRecord);

				// 积分数值
				resultMap.put("IntegralNum", String.valueOf(pointVal));
				// 最大领取次数
				resultMap.put("maxNum", String.valueOf(limitNumDay));
				// 已经领取次数
				resultMap.put("alreadyNum", String.valueOf(listPointRecords.size() + 1));
			}

		}
		return resultMap;
	};
	// End added by tangy

	// 更新总积分
	@Transactional(rollbackFor = Exception.class)
	private void updateSumPoints(String userId, Integer pointVal) throws ServiceException {
		logger.debug("更新总积分请求参数，userId={}，pointVal={}", userId, pointVal);
		SysBuyerExt sysBuyerExt = sysBuyerExtMapper.selectByUserId(userId);
		if (sysBuyerExt != null) {
			Integer currentSumPoints = sysBuyerExt.getPointVal();
			sysBuyerExt.setPointVal(pointVal + currentSumPoints);
			sysBuyerExtMapper.updateByPrimaryKeySelective(sysBuyerExt);
		} else {
			sysBuyerExt = new SysBuyerExt();
			sysBuyerExt.setId(UuidUtils.getUuid());
			sysBuyerExt.setUserId(userId);
			sysBuyerExt.setPointVal(pointVal);
			sysBuyerExtMapper.insertSelective(sysBuyerExt);
		}
	}

	/**
	 * 添加积分记录对象
	 * @param userId 请求参数
	 * @param rule 请求规则
	 * @param pointVal 获得积分
	 * @throws ServiceException 返回异常
	 */
	@Transactional(rollbackFor = Exception.class)
	private void addPointsRecord(String userId, PointsRule rule, Integer pointVal) throws ServiceException {
		logger.debug("添加积分记录请求参数，userId={}，rule={},pointVal={}", userId, rule, pointVal);
		PointsRecord pointsRecord = new PointsRecord();
		pointsRecord.setId(UuidUtils.getUuid());
		pointsRecord.setUserId(userId);
		pointsRecord.setCode(rule.getCode());
		pointsRecord.setPointVal(pointVal);
		pointsRecord.setType((byte) 0);
		pointsRecord.setDescription(rule.getRemark());
		pointsRecord.setCreateTime(new Date());
		pointsRecordMapper.insert(pointsRecord);
	}

	/**
	 * 接口调用处理结果返回公共方法
	 * @param state 0:成功,1:积分规则停用,2:当天积分满限额,3:已到每日限制次数,4:失败
	 * @param message 返回消息
	 * @return 返回消息
	 */
	private String responseResult(Integer state, String message) {
		JSONObject json = new JSONObject();
		json.put("state", state);
		json.put("message", message);
		return json.toString();
	}
}
