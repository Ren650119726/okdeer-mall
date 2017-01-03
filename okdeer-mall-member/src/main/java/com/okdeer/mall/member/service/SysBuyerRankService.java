
package com.okdeer.mall.member.service;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.member.entity.SysBuyerRank;

/**
 * ClassName: SysBuyerRankService 
 * @Description: 买家等级service
 * @author zengjizu
 * @date 2016年12月31日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface SysBuyerRankService extends IBaseService {
	
	/**
	 * @Description: 根据等级code获取到会员等级信息
	 * @param code 等级code
	 * @return
	 * @author zengjizu
	 * @date 2016年12月31日
	 */
	SysBuyerRank findByRankCode(String code);
	
}
