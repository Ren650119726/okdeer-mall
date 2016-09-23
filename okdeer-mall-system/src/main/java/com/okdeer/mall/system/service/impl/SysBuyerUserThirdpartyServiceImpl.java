package com.okdeer.mall.system.service.impl;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.system.entity.SysBuyerUserThirdparty;
import com.okdeer.mall.system.service.SysBuyerUserThirdpartyServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.dal.IBaseCrudMapper;
import com.okdeer.base.service.BaseCrudServiceImpl;
import com.okdeer.mall.system.mapper.SysBuyerUserThirdpartyMapper;
import com.okdeer.mall.system.service.SysBuyerUserThirdpartyService;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-03-17 11:04:03
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.system.service.SysBuyerUserThirdpartyServiceApi")
public class SysBuyerUserThirdpartyServiceImpl extends BaseCrudServiceImpl implements SysBuyerUserThirdpartyService,
		SysBuyerUserThirdpartyServiceApi {

	@Resource
	private SysBuyerUserThirdpartyMapper sysBuyerUserThirdpartyMapper;

	@Override
	public IBaseCrudMapper init() {
		return sysBuyerUserThirdpartyMapper;
	}

	/**
	 * DESC: 根据openID删除
	 * @author LIU.W
	 * @param openId
	 * @param openType
	 * @return
	 * @throws ServiceException
	 */
	@Transactional(rollbackFor = Exception.class)
	public int removeByOpenId(String openId, Integer openType) throws ServiceException {

		try {
			SysBuyerUserThirdparty sysBuyerUserThirdparty = new SysBuyerUserThirdparty();
			sysBuyerUserThirdparty.setOpenId(openId);
			sysBuyerUserThirdparty.setOpenType(openType);
			/**
			 * 1. 删除关联关系
			 */
			return sysBuyerUserThirdpartyMapper.deleteByOpenId(sysBuyerUserThirdparty);

		} catch (Exception e) {
			throw new ServiceException("", e);
		}
	}

}