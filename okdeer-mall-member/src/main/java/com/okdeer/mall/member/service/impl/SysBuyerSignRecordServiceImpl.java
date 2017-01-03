
package com.okdeer.mall.member.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.member.bo.SignResult;
import com.okdeer.mall.member.bo.SysBuyerSignRecordParam;
import com.okdeer.mall.member.entity.SysBuyerSignRecord;
import com.okdeer.mall.member.mapper.SysBuyerSignRecordMapper;
import com.okdeer.mall.member.points.dto.AddPointsParamDto;
import com.okdeer.mall.member.points.enums.PointsRuleCode;
import com.okdeer.mall.member.service.SysBuyerSignRecordService;
import com.okdeer.mall.points.bo.AddPointsResult;
import com.okdeer.mall.points.service.PointsService;

@Service
public class SysBuyerSignRecordServiceImpl extends BaseServiceImpl implements SysBuyerSignRecordService {

	@Autowired
	private SysBuyerSignRecordMapper sysBuyerSignRecordMapper;

	@Autowired
	private PointsService pointsService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public SignResult add(String userId) throws Exception {
		SysBuyerSignRecord sysBuyerSignRecord = new SysBuyerSignRecord();
		sysBuyerSignRecord.setId(UuidUtils.getUuid());
		sysBuyerSignRecord.setSignTime(new Date());
		sysBuyerSignRecord.setUserId(userId);
		sysBuyerSignRecordMapper.add(sysBuyerSignRecord);

		// 添加签到积分
		AddPointsParamDto addPointsParamDto = new AddPointsParamDto();
		addPointsParamDto.setBusinessId(sysBuyerSignRecord.getId());
		addPointsParamDto.setUserId(userId);
		addPointsParamDto.setPointsRuleCode(PointsRuleCode.SIGN);
		AddPointsResult addPointsResult = pointsService.addPoints(addPointsParamDto);

		SignResult result = new SignResult();
		result.setPointVal(addPointsResult.getPointVal());
		result.setStatus(0);
		result.setMsg("签到成功");
		return result;
	}

	@Override
	public int findCountByParam(SysBuyerSignRecordParam buyerSignRecordParam) {
		return sysBuyerSignRecordMapper.findCountByParam(buyerSignRecordParam);
	}

	@Override
	public IBaseMapper getBaseMapper() {
		return sysBuyerSignRecordMapper;
	}

}
