package com.okdeer.mall.order.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.order.entity.TradeOrderRefundsCertificateImg;

public interface TradeOrderRefundsCertificateImgService extends IBaseService {
	
	
	List<TradeOrderRefundsCertificateImg> findByCertificateId(String certificateId);
}
