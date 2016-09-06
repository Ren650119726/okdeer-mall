package com.okdeer.mall.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.system.entity.SysBuyerUserUseLog;
import com.okdeer.mall.system.service.SysBuyerUserUseLogServiceApi;
import com.okdeer.mall.system.mapper.SysBuyerUserUseLogMapper;
import com.okdeer.mall.system.service.SysBuyerUserUseLogService;

/**
 * 手机用户使用日志
 * @project yschome-psms
 * @author tangy
 * @date 2016年4月20日 下午7:05:45
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.system.service.SysBuyerUserUseLogServiceApi")
public class SysBuyerUserUseLogServiceImpl implements SysBuyerUserUseLogService, SysBuyerUserUseLogServiceApi {

	/**
	 * 手机用户使用日志dao
	 */
	@Autowired
	private SysBuyerUserUseLogMapper sysBuyerUserUseLogMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(SysBuyerUserUseLog sysBuyerUserUseLog) {
		sysBuyerUserUseLogMapper.insertSysBuyerUserUseLog(sysBuyerUserUseLog);
	}

}
