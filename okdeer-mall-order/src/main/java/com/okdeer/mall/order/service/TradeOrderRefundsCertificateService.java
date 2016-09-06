package com.okdeer.mall.order.service;

import java.util.List;

import com.okdeer.mall.order.vo.TradeOrderRefundsCertificateVo;

/**
 * @DESC:
 * @author YSCGD
 * @date 2016-02-29 16:19:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderRefundsCertificateService {

	/**
	 * desc: 添加凭证包含图片
	 */
	void addCertificate(TradeOrderRefundsCertificateVo TradeOrderRefundsCertificate);

	/**
	 * 根据售后订单id查询操作记录
	 *
	 * @param refundsId
	 * @return
	 */
	List<TradeOrderRefundsCertificateVo> findByRefundsId(String refundsId);

	/**
	 * 
	 * 根据退款单ID查询退款单售后图片 
	 * @author zengj
	 * @param refundsId
	 * @return
	 */
	List<String> findImageByRefundsId(String refundsId);
}