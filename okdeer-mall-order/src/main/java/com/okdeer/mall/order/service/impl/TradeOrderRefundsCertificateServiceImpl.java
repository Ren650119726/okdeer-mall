package com.okdeer.mall.order.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.order.entity.TradeOrderRefundsCertificateImg;
import com.okdeer.mall.order.service.TradeOrderRefundsCertificateServiceApi;
import com.okdeer.mall.order.vo.TradeOrderRefundsCertificateVo;
import com.okdeer.mall.order.mapper.TradeOrderRefundsCertificateImgMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsCertificateMapper;
import com.okdeer.mall.order.service.TradeOrderRefundsCertificateService;

/**
 * @DESC:
 * @author YSCGD
 * @date 2016-02-29 16:19:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderRefundsCertificateServiceApi")
class TradeOrderRefundsCertificateServiceImpl implements TradeOrderRefundsCertificateService,
		TradeOrderRefundsCertificateServiceApi {

	@Resource
	private TradeOrderRefundsCertificateMapper tradeOrderRefundsCertificateMapper;

	@Resource
	private TradeOrderRefundsCertificateImgMapper tradeOrderRefundsCertificateImgMapper;

	@Override
	public List<TradeOrderRefundsCertificateVo> findByRefundsId(String refundsId) {
		return tradeOrderRefundsCertificateMapper.findByRefundsId(refundsId);
	}

	/**
	 * 
	 * 根据退款单ID查询退款单售后图片 
	 * @author zengj
	 * @param refundsId
	 * @return
	 */
	public List<String> findImageByRefundsId(String refundsId) {
		return tradeOrderRefundsCertificateMapper.findImageByRefundsId(refundsId);
	}

	/**
	 * @desc 添加凭证包含图片
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addCertificate(TradeOrderRefundsCertificateVo certificate) {
		tradeOrderRefundsCertificateMapper.insert(certificate);
		List<TradeOrderRefundsCertificateImg> certificateImgs = certificate.getTradeOrderRefundsCertificateImg();
		if (certificateImgs != null) {
			for (TradeOrderRefundsCertificateImg certificateImg : certificateImgs) {
				tradeOrderRefundsCertificateImgMapper.insert(certificateImg);
			}
		}
	}
}