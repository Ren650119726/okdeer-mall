
package com.okdeer.mall.member.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.member.entity.SysBuyerRank;
import com.okdeer.mall.member.member.dto.SysBuyerExtDto;
import com.okdeer.mall.member.member.entity.SysBuyerExt;
import com.okdeer.mall.member.member.enums.RankCode;
import com.okdeer.mall.member.member.service.MemberApi;
import com.okdeer.mall.member.service.SysBuyerExtService;
import com.okdeer.mall.member.service.SysBuyerRankService;

/**
 * ClassName: MemberApiImpl 
 * @Description: 
 * @author zengjizu
 * @date 2016年12月31日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.member.member.service.MemberApi")
public class MemberApiImpl implements MemberApi {

	@Autowired
	private SysBuyerExtService sysBuyerExtService;

	@Autowired
	private SysBuyerRankService sysBuyerRankService;

	@Override
	public SysBuyerExtDto findSysUserExtInfo(String userId) throws Exception {
		SysBuyerExt sysBuyerExt = sysBuyerExtService.findByUserId(userId);
		if (sysBuyerExt == null) {
			sysBuyerExt = new SysBuyerExt();
		}

		SysBuyerExtDto dto = BeanMapper.map(sysBuyerExt, SysBuyerExtDto.class);
		if (StringUtils.isNotBlank(sysBuyerExt.getRankCode())) {
			SysBuyerRank sysBuyerRank = sysBuyerRankService.findByRankCode(sysBuyerExt.getRankCode());
			dto.setRankName(sysBuyerRank.getName());
			dto.setIcoUrl(sysBuyerRank.getIcoUrl());
		} else {
			// 如果不存在，就默认是铁鹿会员
			SysBuyerRank sysBuyerRank = sysBuyerRankService.findByRankCode(RankCode.FE.getCode());
			dto.setRankName(sysBuyerRank.getName());
			dto.setIcoUrl(sysBuyerRank.getIcoUrl());
		}
		return dto;
	}

}
