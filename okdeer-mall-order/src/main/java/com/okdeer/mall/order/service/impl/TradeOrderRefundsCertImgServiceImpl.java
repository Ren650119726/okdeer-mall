package com.okdeer.mall.order.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.order.entity.TradeOrderRefundsCertificateImg;
import com.okdeer.mall.order.mapper.TradeOrderRefundsCertificateImgMapper;
import com.okdeer.mall.order.service.TradeOrderRefundsCertificateImgService;

@Service
public class TradeOrderRefundsCertImgServiceImpl extends BaseServiceImpl
		implements TradeOrderRefundsCertificateImgService {

	@Autowired
	private TradeOrderRefundsCertificateImgMapper tradeOrderRefundsCertificateImgMapper;
	
	@Override
	public List<TradeOrderRefundsCertificateImg> findByCertificateId(String certificateId) {
		return tradeOrderRefundsCertificateImgMapper.findByCertificateId(certificateId);
	}

	@Override
	public IBaseMapper getBaseMapper() {
		return tradeOrderRefundsCertificateImgMapper;
	}

}
