
package com.okdeer.mall.points.api;

import java.util.List;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.common.dto.BaseResultDto;
import com.okdeer.mall.member.points.dto.PointsTeshProductDto;
import com.okdeer.mall.member.points.dto.PointsTeshProductQueryDto;
import com.okdeer.mall.member.points.service.PointsTeshProductApi;

@Service(interfaceName = "com.okdeer.mall.member.points.service.PointsTeshProductApi")
public class PointsTeshProductApiImpl implements PointsTeshProductApi {

	@Override
	public PageUtils<PointsTeshProductDto> findList(PointsTeshProductQueryDto pointsTeshProductQueryDto,
			Integer pageNum, Integer pageSize) {
		return null;
	}

	@Override
	public List<PointsTeshProductDto> findList(PointsTeshProductQueryDto pointsTeshProductQueryDto) {
		return null;
	}

	@Override
	public BaseResultDto updateStatus(int status) throws Exception {
		return null;
	}

	@Override
	public BaseResultDto edit(PointsTeshProductDto pointsTeshProductDto, boolean isShelves) throws Exception {
		return null;
	}

	@Override
	public PointsTeshProductDto findDetail(String id) {
		return null;
	}

}
