
package com.okdeer.mall.points.api;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.common.dto.BaseResultDto;
import com.okdeer.mall.member.points.dto.PointsTeshProductDto;
import com.okdeer.mall.member.points.dto.PointsTeshProductQueryDto;
import com.okdeer.mall.member.points.service.PointsTeshProductApi;
import com.okdeer.mall.points.entity.PointsTeshProduct;
import com.okdeer.mall.points.entity.PointsTeshProductQuery;
import com.okdeer.mall.points.service.PointsTeshProductService;

@Service(interfaceName = "com.okdeer.mall.member.points.service.PointsTeshProductApi")
public class PointsTeshProductApiImpl implements PointsTeshProductApi {

	@Autowired
	private PointsTeshProductService pointsTeshProductService;

	@Override
	public PageUtils<PointsTeshProductDto> findList(PointsTeshProductQueryDto pointsTeshProductQueryDto,
			Integer pageNum, Integer pageSize) {
		PointsTeshProductQuery pointsTeshProductQuery = new PointsTeshProductQuery();
		BeanUtils.copyProperties(pointsTeshProductQueryDto, pointsTeshProductQuery);
		PageUtils<PointsTeshProduct> pageUtils = pointsTeshProductService.findList(pointsTeshProductQuery, pageNum,
				pageSize);
		Page<PointsTeshProductDto> page = new Page<>(pageNum, pageSize);
		List<PointsTeshProduct> dataList = pageUtils.getRows();
		for (PointsTeshProduct pointsTeshProduct : dataList) {
			PointsTeshProductDto dto = new PointsTeshProductDto();
			BeanUtils.copyProperties(pointsTeshProduct, dto);
			page.add(dto);
		}
		page.setTotal(pageUtils.getTotal());
		PageUtils<PointsTeshProductDto> pageResult = new PageUtils<PointsTeshProductDto>(page);
		return pageResult;
	}

	@Override
	public List<PointsTeshProductDto> findList(PointsTeshProductQueryDto pointsTeshProductQueryDto) {
		PointsTeshProductQuery pointsTeshProductQuery = new PointsTeshProductQuery();
		BeanUtils.copyProperties(pointsTeshProductQueryDto, pointsTeshProductQuery);
		List<PointsTeshProduct> dataList = pointsTeshProductService.findList(pointsTeshProductQuery);
		List<PointsTeshProductDto> resultList = Lists.newArrayList();
		for (PointsTeshProduct pointsTeshProduct : dataList) {
			PointsTeshProductDto dto = new PointsTeshProductDto();
			BeanUtils.copyProperties(pointsTeshProduct, dto);
			resultList.add(dto);
		}
		return resultList;
	}

	@Override
	public BaseResultDto updateStatus(List<String> ids, int status, String opeatorUser) throws Exception {
		BaseResultDto result = new BaseResultDto();

		for (String id : ids) {
			PointsTeshProduct product = pointsTeshProductService.findById(id);

			if (status == 1) {
				// 上架操作
				if (1 == product.getStatus().intValue()) {
					result.setCode("1");
					result.setMsg("上架失败，请检查已勾选的商品是否已经上架");
					return result;
				}
				if (product.getScores() == null) {
					result.setCode("1");
					result.setMsg("上架失败，请检查已勾选的商品是否全部设置了销售价");
					return result;
				}

			} else {
				// 下架操作
				if (0 == product.getStatus().intValue()) {
					result.setCode("1");
					result.setMsg("下架失败，请检查已勾选的商品是否已经下架");
					return result;
				}
			}

			PointsTeshProduct updateProduct = new PointsTeshProduct();

			updateProduct.setId(product.getId());
			updateProduct.setStatus(status);
			updateProduct.setUpdateTime(new Date());
			updateProduct.setUpdateUserId(opeatorUser);

			int count = pointsTeshProductService.update(updateProduct);

			if (count < 0) {
				result.setCode("1");
				result.setMsg("操作失败");
				return result;
			}
		}
		result.setCode("0");
		result.setMsg("操作成功");
		return result;
	}

	@Override
	public BaseResultDto edit(PointsTeshProductDto pointsTeshProductDto, boolean isShelves) throws Exception {
		BaseResultDto result = new BaseResultDto();

		PointsTeshProduct product = new PointsTeshProduct();
		if (isShelves) {
			if (pointsTeshProductDto.getScores() == null) {
				result.setCode("1");
				result.setMsg("保存失败，请检查商品是否设置了销售价");
				return result;
			}
			product.setStatus(1);
		}

		product.setId(pointsTeshProductDto.getId());
		product.setUpdateTime(new Date());
		product.setUpdateTime(new Date());
		product.setUpdateUserId(pointsTeshProductDto.getUpdateUserId());

		int count = pointsTeshProductService.update(product);

		if (count < 0) {
			result.setCode("1");
			result.setMsg("操作失败");
			return result;
		}

		result.setCode("0");
		result.setMsg("操作成功");
		return result;
	}

	@Override
	public PointsTeshProductDto findDetail(String id) throws Exception {
		PointsTeshProduct product = pointsTeshProductService.findById(id);
		PointsTeshProductDto dto = new PointsTeshProductDto();
		BeanUtils.copyProperties(product, dto);
		return dto;
	}

}
