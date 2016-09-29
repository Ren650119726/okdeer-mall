/** 
 *@Project: okdeer-mall-activity 
 *@Author: yangq
 *@Date: 2016年9月28日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */

package com.okdeer.mall.activity.seckill.service;

import com.okdeer.mall.activity.seckill.entity.SeckillReminde;
import com.okdeer.mall.order.vo.SendMsgParamVo;

/**
 * ClassName: SeckillRemindeService 
 * @Description: TODO
 * @author yangq
 * @date 2016年9月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface SeckillRemindeService {

	void insertSeckillReminde(SeckillReminde seckill) throws Exception;

	void sendPosMessage(SendMsgParamVo sendMsgParamVo, String skuName) throws Exception;
	
}
