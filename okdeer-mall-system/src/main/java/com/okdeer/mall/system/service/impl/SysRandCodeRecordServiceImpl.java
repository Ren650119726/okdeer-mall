/** 
 *@Project: okdeer-mall-system 
 *@Author: tangzj02
 *@Date: 2017年2月27日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.system.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.common.utils.CommonUtils;
import com.okdeer.mall.system.entity.SysRandCodeRecord;
import com.okdeer.mall.system.mapper.SysRandCodeRecordMapper;
import com.okdeer.mall.system.service.SysRandCodeRecordService;

/**
 * ClassName: SysRandCodeRecordServiceImpl 
 * @Description: 随机码service
 * @author tangzj02
 * @date 2017年02月27日
 *
 * =================================================================================================
 *     Task ID            Date               Author           Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     V2.1           2017年02月27日                        tangzj02       添加
 */
@Service
public class SysRandCodeRecordServiceImpl implements SysRandCodeRecordService {

	@Autowired
	private SysRandCodeRecordMapper sysRandCodeRecordMapper;

	/**
	 * RedisTemplate
	 */
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.system.service.SysRandCodeRecordService#findRecordByRandCode()
	 */
	@Override
	public String findRecordByRandCode() throws ServiceException {
		// 通过redis获取一个邀请码
		String randCode = (String) redisTemplate.boundListOps("MALL:RANDCODE").rightPop();
		// 如果获取不到则初始化一redis中的邀请码数据
		if (StringUtils.isBlank(randCode)) {
			redisTemplate.delete("MALL:RANDCODE");
			List<String> recordList = sysRandCodeRecordMapper.findValidRandCodeList();
			Collections.shuffle(recordList);
			List<List<String>> splitList = CommonUtils.splitList(recordList);
			for (List<String> list : splitList) {
				redisTemplate.boundListOps("MALL:RANDCODE").leftPushAll(list.toArray(new String[list.size()]));
			}
			// 初始化完以后， 重新从redis中获取一个邀请码
			randCode = (String) redisTemplate.boundListOps("MALL:RANDCODE").rightPop();
		}
		return randCode;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.system.service.SysRandCodeRecordService#deleteRecordByRandCodeByCode(java.lang.String)
	 */
	@Override
	public void deleteRecordByRandCodeByCode(String code) throws ServiceException {
		SysRandCodeRecord record = sysRandCodeRecordMapper.findRecordByRandCode(code);
		if (null != record) {
			record.setUpdateTime(new Date());
			record.setDisabled(Disabled.invalid);
			sysRandCodeRecordMapper.updateSysRandCodeRecord(record);
		}

	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.system.service.SysRandCodeRecordService#findValidRandCodeList()
	 */
	@Override
	public List<String> findValidRandCodeList() throws ServiceException {
		return sysRandCodeRecordMapper.findValidRandCodeList();
	}

}
