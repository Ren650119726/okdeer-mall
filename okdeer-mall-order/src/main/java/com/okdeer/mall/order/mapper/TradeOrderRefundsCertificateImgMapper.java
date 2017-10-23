package com.okdeer.mall.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseCrudMapper;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.order.entity.TradeOrderRefundsCertificateImg;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-03-08 14:19:47
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderRefundsCertificateImgMapper extends IBaseMapper {
	
	
	List<TradeOrderRefundsCertificateImg> findByCertificateId(@Param("certificateId") String certificateId);
	
}