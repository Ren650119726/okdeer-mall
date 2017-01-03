
package com.okdeer.mall.member.service;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.member.bo.SignResult;
import com.okdeer.mall.member.bo.SysBuyerSignRecordParam;

public interface SysBuyerSignRecordService extends IBaseService {

	/**
	 * @Description: 添加签到
	 * @param userId 用户id
	 * @author zengjizu
	 * @date 2016年12月31日
	 */
	SignResult add(String userId) throws Exception;
	
	/**
	 * @Description: 根据查询条件查询签到次数
	 * @return
	 * @author zengjizu
	 * @date 2016年12月31日
	 */
	int findCountByParam(SysBuyerSignRecordParam buyerSignRecordParam);

}
