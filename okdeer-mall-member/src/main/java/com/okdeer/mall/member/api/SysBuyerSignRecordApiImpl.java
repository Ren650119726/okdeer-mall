
package com.okdeer.mall.member.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.member.bo.SignResult;
import com.okdeer.mall.member.bo.SysBuyerSignRecordParam;
import com.okdeer.mall.member.member.dto.SignResultDto;
import com.okdeer.mall.member.member.service.SysBuyerSignRecordApi;
import com.okdeer.mall.member.service.SysBuyerSignRecordService;

@Service(interfaceName = "com.okdeer.mall.member.member.service.SysBuyerSignRecordApi")
public class SysBuyerSignRecordApiImpl implements SysBuyerSignRecordApi {

	@Autowired
	private SysBuyerSignRecordService sysBuyerSignRecordService;

	@Override
	public SignResultDto sign(String userId) throws Exception {

		SysBuyerSignRecordParam buyerSignRecordParam = new SysBuyerSignRecordParam();
		buyerSignRecordParam.setUserId(userId);
		String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		buyerSignRecordParam.setStartTime(today + " 00:00:00");
		buyerSignRecordParam.setEndTime(today + " 23:59:59");
		int count = sysBuyerSignRecordService.findCountByParam(buyerSignRecordParam);

		SignResultDto dto = new SignResultDto();
		if (count > 0) {
			// 已经签到过了
			dto.setMsg("今天已经签到过了");
			dto.setStatus(1);
			return dto;
		}
		SignResult signResult = sysBuyerSignRecordService.add(userId);
		BeanMapper.copy(signResult, dto);
		return dto;
	}

}
