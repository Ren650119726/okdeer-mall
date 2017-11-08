package com.okdeer.mall.operate.advert.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.advert.entity.ColumnAdvertArea;
import com.okdeer.mall.operate.advert.bo.ColumnAdvertAreaParamBo;
import com.okdeer.mall.operate.advert.mapper.ColumnAdvertAreaMapper;
import com.okdeer.mall.operate.advert.service.ColumnAdvertAreaService;

@Service
public class ColumnAdvertAreaServiceImpl extends BaseServiceImpl implements ColumnAdvertAreaService {

	 @Autowired
	private ColumnAdvertAreaMapper columnAdvertAreaMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return columnAdvertAreaMapper;
	}

	@Override
	public List<ColumnAdvertArea> findList(ColumnAdvertAreaParamBo columnAdvertAreaParamBo) {
		return columnAdvertAreaMapper.findList(columnAdvertAreaParamBo);
	}
}
