package com.okdeer.mall.order.mapper;

import java.util.List;

import com.okdeer.mall.order.entity.TradeOrderRefundsCertificate;
import com.okdeer.mall.order.vo.TradeOrderRefundsCertificateVo;
import com.okdeer.base.dal.IBaseCrudMapper;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-29 16:19:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderRefundsCertificateMapper extends IBaseCrudMapper {

	/**
	 * 根据售后订单id查询操作记录
	 *
	 * @param refundsId 售后单ID
	 * @return 售后单操作记录
	 */
	List<TradeOrderRefundsCertificateVo> findByRefundsId(String refundsId);

	/**
	 * 根据售后订单id查询用户上传的凭证和照片 
	 */
	List<String> findImageByRefundsId(String id);
	
	/**
	 * 查找用户申请售后单上传的凭证
	 */
	TradeOrderRefundsCertificate findFirstByRefundsId(String id);
	
	/**
	 * 删除凭证和图片
	 */
	int deleteAndImageById(String id);
}