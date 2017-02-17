package com.okdeer.mall.member.api;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.member.entity.SysBuyerLocateInfo;
import com.okdeer.mall.member.member.dto.SysBuyerLocateInfoDto;
import com.okdeer.mall.member.member.service.SysBuyerLocateInfoServiceApi;
import com.okdeer.mall.member.service.SysBuyerLocateInfoService;

/**
 * 
 * ClassName: SysBuyerLocateInfoServiceApiImpl 
 * @Description: 用户定位信息api实现层
 * @author chenzc
 * @date 2017年2月17日
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.member.member.service.SysBuyerLocateInfoServiceApi")
public class SysBuyerLocateInfoServiceApiImpl implements SysBuyerLocateInfoServiceApi {

	@Autowired
	private SysBuyerLocateInfoService sysBuyerLocateInfoService;
	
	@Override
	public void save(SysBuyerLocateInfoDto dto) throws Exception {
		SysBuyerLocateInfo info = BeanMapper.map(dto, SysBuyerLocateInfo.class);
		sysBuyerLocateInfoService.save(info);
	}
}
