package com.okdeer.mall.member.service;

import java.util.List;

import com.okdeer.mall.member.bo.SysBuyerLocateInfoBo;
import com.okdeer.mall.member.entity.SysBuyerLocateInfo;
import com.okdeer.mall.member.member.dto.LocateInfoQueryDto;

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

	/**
	 * @Description: 查询用户信息列表 目前用于app消息推送
	 * @param dto
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月18日
	 */
	List<SysBuyerLocateInfoBo> findUserList(LocateInfoQueryDto dto);
}
