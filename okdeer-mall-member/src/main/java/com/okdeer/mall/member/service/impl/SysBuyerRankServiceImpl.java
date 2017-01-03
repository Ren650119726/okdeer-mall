
package com.okdeer.mall.member.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.member.entity.SysBuyerRank;
import com.okdeer.mall.member.mapper.SysBuyerRankMapper;
import com.okdeer.mall.member.service.SysBuyerRankService;

@Service
public class SysBuyerRankServiceImpl extends BaseServiceImpl implements SysBuyerRankService {

	@Autowired
	private SysBuyerRankMapper sysBuyerRankMapper;

	@Override
	public SysBuyerRank findByRankCode(String code) {
		
		return sysBuyerRankMapper.findByRankCode(code);
	}

	@Override
	public IBaseMapper getBaseMapper() {
		return sysBuyerRankMapper;
	}

}
