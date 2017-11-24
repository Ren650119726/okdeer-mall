package com.okdeer.mall.activity.coupons.service.receive;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.activity.coupons.service.receive.bo.CouponsReceiveBo;
import com.okdeer.mall.common.entity.ResultMsg;

/**
 * ClassName: CheckCouponsHandler 
 * @Description: 代金券检查执行
 * @author tuzhd
 * @date 2017年11月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface CouponsReceiveHandler {

	public ResultMsg excute(CouponsReceiveBo bo) throws ServiceException;
}
