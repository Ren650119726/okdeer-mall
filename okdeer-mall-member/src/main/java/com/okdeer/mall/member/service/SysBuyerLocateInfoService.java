package com.okdeer.mall.member.service;

import com.okdeer.mall.member.entity.SysBuyerLocateInfo;

/**
 * 
 * ClassName: SysBuyerLocateInfoService 
 * @Description: 买家用户定位信息service
 * @author chenzc
 * @date 2017年2月17日
 */
public interface SysBuyerLocateInfoService {
	
	/**
	 * 
	 * @Description: 保存用户定位信息
	 * @return void  
	 * @throws 异常
	 * @author chenzc
	 * @date 2017年2月17日
	 */
	void save(SysBuyerLocateInfo info) throws Exception;
}
