
package com.okdeer.mall.points.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.okdeer.archive.system.entity.SysDict;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.member.entity.SysBuyerRank;
import com.okdeer.mall.member.entity.SysBuyerRankRecord;
import com.okdeer.mall.member.mapper.SysBuyerExtMapper;
import com.okdeer.mall.member.mapper.SysBuyerRankMapper;
import com.okdeer.mall.member.mapper.SysBuyerRankRecordMapper;
import com.okdeer.mall.member.member.entity.SysBuyerExt;
import com.okdeer.mall.member.member.enums.RankCode;
import com.okdeer.mall.member.points.dto.AddPointsParamDto;
import com.okdeer.mall.member.points.dto.ConsumPointParamDto;
import com.okdeer.mall.member.points.dto.PointQueryParamDto;
import com.okdeer.mall.member.points.dto.RefundPointParamDto;
import com.okdeer.mall.member.points.entity.PointsRecord;
import com.okdeer.mall.member.points.entity.PointsRule;
import com.okdeer.mall.member.points.enums.PointsRuleCode;
import com.okdeer.mall.points.bo.AddPointsResult;
import com.okdeer.mall.points.bo.PointQueryResult;
import com.okdeer.mall.points.bo.StatisRecordParamBo;
import com.okdeer.mall.points.mapper.PointsRecordMapper;
import com.okdeer.mall.points.mapper.PointsRuleMapper;
import com.okdeer.mall.points.service.PointsService;
import com.okdeer.mall.system.mapper.SysDictMapper;

/**
 * ClassName: PointServiceImpl 
 * @Description: 积分service处理
 * @author zengjizu
 * @date 2016年12月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class PointServiceImpl implements PointsService {

	private static final Logger logger = LoggerFactory.getLogger(PointServiceImpl.class);

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

	@Autowired
	private SysBuyerRankMapper sysBuyerRankMapper;

	@Autowired
	private SysBuyerRankRecordMapper sysBuyerRankRecordMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public AddPointsResult addPoints(AddPointsParamDto addPointsParamDto) throws Exception {

		AddPointsResult result = new AddPointsResult();
		// 锁住数据，避免出现脏数据
		SysBuyerExt sysBuyerExt = sysBuyerExtMapper.findByUserIdForUpdate(addPointsParamDto.getUserId());
		// 校验是否重复消费消息
		boolean checkRepeatResult = checkRepeat(addPointsParamDto.getBusinessId());
		if (checkRepeatResult) {
			// 消费过消息了，直接返回，不做处理
			logger.error("重复扣积分了!");
			result.setMsg("积分重复扣取");
			result.setStatus(1);
			return result;
		}

		// 查询积分规则
		PointsRule pointsRule = pointsRuleMapper.selectByCode(addPointsParamDto.getPointsRuleCode().getCode());
		// 校验领取规则
		boolean checkResult = checkRule(addPointsParamDto, pointsRule, result);
		if (!checkResult) {
			return result;
		}

		String userId = addPointsParamDto.getUserId();
		// 获取应得积分
		int pointVal = getPoints(addPointsParamDto.getUserId(), addPointsParamDto.getAmount(), pointsRule, sysBuyerExt);

		if (pointVal > 0) {
			// 添加积分详细记录
			String description = StringUtils.isBlank(addPointsParamDto.getDescription()) ? pointsRule.getRemark()
					: addPointsParamDto.getDescription();
			addPointsRecord(userId, pointsRule.getCode(), pointVal, description, 0, addPointsParamDto.getBusinessId());
			result.setMsg("领取成功");
			result.setStatus(0);
			result.setPointVal(pointVal);
		} else {
			result.setMsg("已经超过领取积分上限了");
			result.setStatus(1);
			result.setPointVal(pointVal);
		}

		Integer growthVal = null;
		// 如果是消费积分的话，还需要增加成长值
		if (PointsRuleCode.APP_CONSUME == addPointsParamDto.getPointsRuleCode()) {
			// 一块钱一个成长值
			growthVal = (int) Math.floor(addPointsParamDto.getAmount().doubleValue());
			addRankRecord(addPointsParamDto, growthVal);
		}

		// 更新用户的积分值和成长值
		if (sysBuyerExt == null) {
			addUserExt(userId, pointVal, growthVal);
		} else {
			if (growthVal != null) {
				// 更新用户成长值
				updateUserGrowth(userId, growthVal);
			}
			// 更新用户积分
			updateUserPoint(userId, pointVal);
			// 更新用户的会员等级
			updateUserExt(userId);
		}
		return result;
	}

	/**
	 * @Description: 添加成长值
	 * @param addPointsParamDto
	 * @param sysBuyerExt
	 * @author zengjizu
	 * @date 2016年12月30日
	 */
	private void addRankRecord(AddPointsParamDto addPointsParamDto, int growthVal) {
		addRankRecord(addPointsParamDto.getUserId(), addPointsParamDto.getBusinessId(), 1,
				addPointsParamDto.getAmount(), growthVal);
	}
	
	/**
	 * @Description: 添加用户成长值记录
	 * @param userId 用户id
	 * @param bussinessId 业务id
	 * @param type 类型 1:加成长值 2:减成长值
	 * @param amount 金额
	 * @param growthVal 成长值
	 * @author zengjizu
	 * @date 2017年1月7日
	 */
	private void addRankRecord(String userId, String bussinessId, int type, BigDecimal amount, int growthVal) {
		SysBuyerRankRecord buyerRankRecord = new SysBuyerRankRecord();
		buyerRankRecord.setBusinessId(bussinessId);
		buyerRankRecord.setBusinessType(type);
		buyerRankRecord.setConsumeAmount(amount);
		buyerRankRecord.setCreateTime(new Date());
		buyerRankRecord.setGrowthVal(growthVal);
		buyerRankRecord.setId(UuidUtils.getUuid());
		buyerRankRecord.setUserId(userId);
		sysBuyerRankRecordMapper.add(buyerRankRecord);
	}

	/**
	 * @Description: 校验是否满足规则
	 * @param addPointsParamDto
	 * @param result
	 * @author zengjizu
	 * @date 2016年12月30日
	 */
	private boolean checkRule(AddPointsParamDto addPointsParamDto, PointsRule pointsRule, AddPointsResult result) {
		// 1 根据积分规则编码code查询规则

		if (pointsRule == null) {
			logger.debug("积分规则编码为空!");
			result.setMsg("积分规则编码为空");
			result.setStatus(1);
			return false;
		}

		if (pointsRule.getStatus() == 0) {
			logger.debug("没有启用的积分规则！！");
			result.setMsg("没有启用的积分规则");
			result.setStatus(1);
			return false;
		}

		String userId = addPointsParamDto.getUserId();
		PointsRuleCode ruleCode = addPointsParamDto.getPointsRuleCode();

		StatisRecordParamBo paramBo = new StatisRecordParamBo();
		paramBo.setCode(ruleCode.getCode());
		paramBo.setUserId(userId);
		// 查询此账号是否已注册
		if (PointsRuleCode.REGISTER.getCode().equals(ruleCode.getCode())) {
			// 注册只能有一次
			int records = pointsRecordMapper.statisRecordCount(paramBo);
			if (records > 0) {
				logger.debug("已注册！！！");
				result.setMsg("已经领取过注册积分了");
				result.setStatus(1);
				return false;
			}
		}
		// 每日限制次数
		int limitNumDay = pointsRule.getLimitNum();
		if (limitNumDay > 0) {
			// 校验是否超过每日次数限制
			String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			paramBo.setStartTime(today + " 00:00:00");
			paramBo.setEndTime(today + " 23:59:59");
			// 查询是否有记录
			int records = pointsRecordMapper.statisRecordCount(paramBo);
			int surplusNumDay = limitNumDay - records;
			if (surplusNumDay <= 0) {
				result.setMsg("已经超过每日次数限制了");
				result.setStatus(1);
				return false;
			}
		}
		return true;
	}

	/**
	 * @Description: 获取应得积分
	 * @param userId 用户id
	 * @param amount 金额信息
	 * @param pointsRule 规则信息
	 * @param sysBuyerExt 用户信息
	 * @return
	 * @author zengjizu
	 * @date 2017年1月7日
	 */
	private int getPoints(String userId, BigDecimal amount, PointsRule pointsRule, SysBuyerExt sysBuyerExt) {
		// 数据词典查询日积分总额限制
		int totalPointLimit = 0;

		if (1 == pointsRule.getIsTotalPointLimit()) {
			List<SysDict> sysDicts = sysDictMapper.selectByType("totalPointLimit");
			if (sysDicts != null && sysDicts.size() > 0) {
				totalPointLimit = Integer.valueOf(sysDicts.get(0).getValue());
			}
		}

		int pointVal = 0;
		if (pointsRule.getCode().equals(PointsRuleCode.APP_CONSUME.getCode())) {
			// app消费另外计算积分
			int limitPointVal = pointsRule.getPointVal();
			pointVal = (int) Math.floor(amount.doubleValue()) * limitPointVal;
		} else {
			pointVal = pointsRule.getPointVal();
		}

		// 积分倍数
		BigDecimal multiple = new BigDecimal("1");

		if (sysBuyerExt != null && StringUtils.isNotBlank(sysBuyerExt.getRankCode())) {
			SysBuyerRank sysBuyerRank = sysBuyerRankMapper.findByRankCode(sysBuyerExt.getRankCode());
			if (sysBuyerRank != null && sysBuyerRank.getPointBenefit() == WhetherEnum.whether) {
				multiple = sysBuyerRank.getPointBenefitVal();
			}
		}
		pointVal = (int) Math.floor(new BigDecimal(pointVal).multiply(multiple).doubleValue());

		// 总额限制
		if (totalPointLimit != 0) {
			// 条件查询当天获得汇总积分
			StatisRecordParamBo paramBo = new StatisRecordParamBo();
			paramBo.setUserId(userId);
			String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			paramBo.setStartTime(today + " 00:00:00");
			paramBo.setEndTime(today + " 23:59:59");
			if (!pointsRule.getCode().equals(PointsRuleCode.APP_CONSUME.getCode())) {
				// 如果不是app消费积分类型，需要排除app消费的积分,
				List<String> existsCodeList = Lists.newArrayList();
				existsCodeList.add(PointsRuleCode.APP_CONSUME.getCode());
				paramBo.setExistsCodeList(existsCodeList);
				paramBo.setType(0);
			}
			int currentSum = pointsRecordMapper.statisRecordPoint(paramBo);
			// 天限制积分 > 当天获得汇总积分 + 当前规则积分
			int surplusPoints = totalPointLimit - currentSum;
			pointVal = (surplusPoints > pointVal) ? pointVal : surplusPoints;
		}
		return pointVal;
	}

	/**
	 * @Description: 消费积分
	 * @param consumPointParamDto 消费积分参数
	 * @author zengjizu
	 * @date 2017年1月5日
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void consumPoint(ConsumPointParamDto consumPointParamDto) throws Exception {
		// 锁住数据，避免出现脏数据
		SysBuyerExt sysBuyerExt = sysBuyerExtMapper.findByUserIdForUpdate(consumPointParamDto.getUserId());
		// 校验是否重复消费消息
		boolean checkRepeatResult = checkRepeat(consumPointParamDto.getBusinessId());
		if (checkRepeatResult) {
			// 消费过消息了，直接返回，不做处理
			return;
		}
		// 校验用户积分是否够
		if (sysBuyerExt == null || sysBuyerExt.getPointVal() == null
				|| sysBuyerExt.getPointVal().compareTo(consumPointParamDto.getPointVal()) < 0) {
			throw new Exception("用户积分不够");
		}

		// 更新用户积分
		int reducePoint = new BigDecimal(consumPointParamDto.getPointVal()).multiply(new BigDecimal("-1")).intValue();
		updateUserPoint(consumPointParamDto.getUserId(), reducePoint);
		// 添加积分消费记录
		addPointsRecord(consumPointParamDto.getUserId(), reducePoint, consumPointParamDto.getDescription(), 1,
				consumPointParamDto.getBusinessId());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void refundPoint(RefundPointParamDto refundPointParamDto) throws Exception {
		// 锁住数据，避免出现脏数据
		SysBuyerExt sysBuyerExt = sysBuyerExtMapper.findByUserIdForUpdate(refundPointParamDto.getUserId());
		// 校验是否重复消费消息
		boolean checkRepeatResult = checkRepeat(refundPointParamDto.getBusinessId());
		if (checkRepeatResult) {
			// 消费过消息了，直接返回，不做处理
			return;
		}
		// 校验用户积分是否够
		if (sysBuyerExt == null || sysBuyerExt.getPointVal() == null || sysBuyerExt.getPointVal() < 0) {
			throw new Exception("用户积分不够");
		}

		// 扣减的成长值
		int growthVal = (int) Math.floor(refundPointParamDto.getAmount().doubleValue());
		if (sysBuyerExt.getGrowthVal() < growthVal) {
			growthVal = sysBuyerExt.getGrowthVal();
		}
		int reduceGrowthVal = new BigDecimal(growthVal).multiply(new BigDecimal("-1")).intValue();
		// 更新用户成长值
		updateUserGrowth(sysBuyerExt.getUserId(), reduceGrowthVal);
		// 添加成长值记录
		addRankRecord(sysBuyerExt.getUserId(), refundPointParamDto.getBusinessId(), 2, refundPointParamDto.getAmount(),
				reduceGrowthVal);
		// 计算扣减的积分
		PointsRule pointsRule = pointsRuleMapper.selectByCode(PointsRuleCode.APP_CONSUME.getCode());
		int pointVal = getPoints(sysBuyerExt.getUserId(), refundPointParamDto.getAmount(), pointsRule, sysBuyerExt);

		if (sysBuyerExt.getPointVal() < pointVal) {
			pointVal = sysBuyerExt.getPointVal();
		}
		int reducePoint = new BigDecimal(pointVal).multiply(new BigDecimal("-1")).intValue();
		// 更新用户积分
		updateUserPoint(sysBuyerExt.getUserId(), reducePoint);
		// 添加积分扣减记录
		addPointsRecord(sysBuyerExt.getUserId(), reducePoint, refundPointParamDto.getDescription(), 1,
				refundPointParamDto.getBusinessId());
		// 更新用户等级
		updateUserExt(sysBuyerExt.getUserId());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public PointQueryResult findUserPoint(PointQueryParamDto pointQueryParamDto) {
		// 锁住数据，避免出现脏数据
		PointQueryResult pointQueryResult = new PointQueryResult();

		SysBuyerExt sysBuyerExt = sysBuyerExtMapper.findByUserIdForUpdate(pointQueryParamDto.getUserId());

		// 查询积分记录
		PointsRecord pointsRecord = pointsRecordMapper.findByReferentId(pointQueryParamDto.getBusinessId());

		if (pointsRecord != null) {
			pointQueryResult.setPointVal(Math.abs(pointsRecord.getPointVal().intValue()));
			pointQueryResult.setUserPointVal(sysBuyerExt.getPointVal());
			return pointQueryResult;
		}

		// 查询不到积分记录，就计算出该次会获得多少积分
		PointsRule pointsRule = pointsRuleMapper.selectByCode(PointsRuleCode.APP_CONSUME.getCode());
		int pointVal = getPoints(pointQueryParamDto.getUserId(), pointQueryParamDto.getAmount(), pointsRule,
				sysBuyerExt);

		if (sysBuyerExt == null) {
			pointQueryResult.setUserPointVal(0);
		} else {
			pointQueryResult.setUserPointVal(sysBuyerExt.getPointVal());
		}

		pointQueryResult.setPointVal(pointVal);

		// 1为加积分 2:减积分
		if (pointQueryParamDto.getType() == 1) {
			pointQueryResult.setUserPointVal(pointQueryResult.getUserPointVal() + pointVal);
		} else {
			if (pointQueryResult.getUserPointVal().intValue() < pointVal) {
				pointQueryResult.setPointVal(pointQueryResult.getUserPointVal());
			}
			pointQueryResult.setUserPointVal(pointQueryResult.getUserPointVal() - pointVal);
		}

		// 小于0就返回0
		if (pointQueryResult.getUserPointVal() < 0) {
			pointQueryResult.setUserPointVal(0);
		}
		return pointQueryResult;
	}

	/**
	 * @Description: 更新用户积分
	 * @param userId 用户id
	 * @param pointVal 积分
	 * @throws Exception
	 * @author zengjizu
	 * @date 2017年1月5日
	 */
	private void updateUserPoint(String userId, int pointVal) throws Exception {
		int count = sysBuyerExtMapper.updatePoint(userId, pointVal);
		if (count < 0) {
			throw new Exception("用户积分不够");
		}
	}

	/**
	 * @Description: 更新用户积分
	 * @param userId 用户id
	 * @param pointVal 积分
	 * @throws Exception
	 * @author zengjizu
	 * @date 2017年1月5日
	 */
	private void updateUserGrowth(String userId, Integer growthVal) throws Exception {
		if (growthVal == null) {
			return;
		}
		int count = sysBuyerExtMapper.updateGrowth(userId, growthVal);
		if (count < 0) {
			throw new Exception("用户成长值不够");
		}
	}

	/**
	 * @Description: 校验数据是否重复
	 * @param businessId 业务id
	 * @return
	 * @author zengjizu
	 * @date 2017年1月5日
	 */
	private boolean checkRepeat(String businessId) {
		int count = pointsRecordMapper.findCountByReferentId(businessId);
		if (count > 0) {
			return true;
		}
		return false;
	}

	/**
	 * @Description: 添加积分记录
	 * @param userId 用户id 
	 * @param pointVal 积分值
	 * @param description 描述
	 * @param type 类型
	 * @throws ServiceException
	 * @author zengjizu
	 * @date 2017年1月7日
	 */
	private void addPointsRecord(String userId, Integer pointVal, String description, int type, String referentId)
			throws ServiceException {
		addPointsRecord(userId, null, pointVal, description, type, referentId);
	}

	/**
	 * @Description: 添加积分记录
	 * @param userId 用户id
	 * @param code 规则code
	 * @param pointVal 积分值
	 * @param description 文案描述
	 * @param type 类型 
	 * @throws ServiceException
	 * @author zengjizu
	 * @date 2017年1月7日
	 */
	private void addPointsRecord(String userId, String code, Integer pointVal, String description, int type,
			String referentId) throws ServiceException {
		logger.debug("添加积分记录请求参数，userId={}，code={},pointVal={},description={}", userId, code, pointVal, description);
		PointsRecord pointsRecord = new PointsRecord();
		pointsRecord.setId(UuidUtils.getUuid());
		pointsRecord.setUserId(userId);
		pointsRecord.setCode(code);
		pointsRecord.setDescription(description);
		pointsRecord.setPointVal(pointVal);
		pointsRecord.setType((byte) type);
		pointsRecord.setReferentId(referentId);
		pointsRecord.setCreateTime(new Date());
		pointsRecordMapper.insert(pointsRecord);
	}

	/**
	 * @Description: 添加用户的扩展信息（当用户没有扩展信息时）
	 * @param userId 
	 * @param pointVal
	 * @param growthVal
	 * @throws ServiceException
	 * @author zengjizu
	 * @date 2016年12月31日
	 */
	private void addUserExt(String userId, Integer pointVal, Integer growthVal) throws ServiceException {
		if (growthVal == null) {
			growthVal = 0;
		}
		if (pointVal == null) {
			pointVal = 0;
		}
		// 用户扩展信息还不存在
		SysBuyerExt sysBuyerExt = new SysBuyerExt();
		sysBuyerExt.setId(UuidUtils.getUuid());
		sysBuyerExt.setUserId(userId);
		sysBuyerExt.setPointVal(pointVal);
		sysBuyerExt.setGrowthVal(growthVal);
		SysBuyerRank sysBuyerRank = sysBuyerRankMapper.findByGrowth(growthVal);
		if (sysBuyerRank == null) {
			sysBuyerExt.setRankCode(RankCode.FE.getCode());
		} else {
			sysBuyerExt.setRankCode(sysBuyerRank.getRankCode());
		}
		sysBuyerExtMapper.insertSelective(sysBuyerExt);
	}

	/**
	 * @Description: 更新用户信息
	 * @param userId 用户id
	 * @throws ServiceException
	 * @author zengjizu
	 * @date 2016年12月31日
	 */
	private void updateUserExt(String userId) throws ServiceException {
		logger.debug("更新总积分请求参数，userId={}", userId);
		SysBuyerExt sysBuyerExt = sysBuyerExtMapper.selectByUserId(userId);
		// 更新会员等级
		int currentGorwth = sysBuyerExt.getGrowthVal();
		SysBuyerRank sysBuyerRank = sysBuyerRankMapper.findByGrowth(currentGorwth);
		if (sysBuyerRank != null) {
			sysBuyerExt.setRankCode(sysBuyerRank.getRankCode());
		}
		sysBuyerExtMapper.updateByPrimaryKeySelective(sysBuyerExt);
	}
}
