package com.okdeer.mall.member.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.member.entity.SysBuyerLocateInfo;
import com.okdeer.mall.member.mapper.SysBuyerLocateInfoMapper;
import com.okdeer.mall.member.service.SysBuyerLocateInfoService;

/**
 * 
 * ClassName: SysBuyerLocateInfoServiceImpl 
 * @Description: 买家用户定位信息service实现层
 * @author chenzc
 * @date 2017年2月17日
 */
@Service
public class SysBuyerLocateInfoServiceImpl extends BaseServiceImpl implements SysBuyerLocateInfoService {

	@Autowired
	private SysBuyerLocateInfoMapper sysBuyerLocateInfoMapper;
	
	@Override
	public void save(SysBuyerLocateInfo info) throws Exception {
		SysBuyerLocateInfo sysBuyerLocateInfo = sysBuyerLocateInfoMapper.findByUserId(info.getUserId());
		if (sysBuyerLocateInfo != null) {
			info.setId(sysBuyerLocateInfo.getId());
			info.setUpdateTime(new Date());
			sysBuyerLocateInfoMapper.update(info);
		} else {
			info.setId(UuidUtils.getUuid());
			info.setCreateTime(new Date());
			sysBuyerLocateInfoMapper.add(info);
		}
	}

	@Override
	public IBaseMapper getBaseMapper() {
		return sysBuyerLocateInfoMapper;
	}

}
