/** 
 *@Project: okdeer-mall-operate 
 *@Author: tangzj02
 *@Date: 2016年12月28日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.common.utils.BaseResult;
import com.okdeer.mall.operate.dto.HomeIconDto;
import com.okdeer.mall.operate.dto.HomeIconParamDto;
import com.okdeer.mall.operate.entity.ColumnHomeIcon;
import com.okdeer.mall.operate.entity.ColumnHomeIconGoods;
import com.okdeer.mall.operate.entity.ColumnSelectArea;
import com.okdeer.mall.operate.enums.ColumnType;
import com.okdeer.mall.operate.enums.HomeIconPlace;
import com.okdeer.mall.operate.enums.HomeIconTaskType;
import com.okdeer.mall.operate.enums.SelectAreaType;
import com.okdeer.mall.operate.mapper.ColumnHomeIconMapper;
import com.okdeer.mall.operate.service.ColumnHomeIconGoodsService;
import com.okdeer.mall.operate.service.ColumnHomeIconService;
import com.okdeer.mall.operate.service.ColumnSelectAreaService;

/**
 * ClassName: ColumnHomeIconServiceImpl 
 * @Description: 首页ICON的服务实现
 * @author tangzj02
 * @date 2016年12月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * * 	        友门鹿2.0        2016-12-28        tangzj02                     添加
 */

@Service
public class ColumnHomeIconServiceImpl extends BaseServiceImpl implements ColumnHomeIconService {

	@Autowired
	private ColumnHomeIconMapper homeIconMapper;

	@Autowired
	private ColumnHomeIconGoodsService homeIconGoodsService;

	@Autowired
	private ColumnSelectAreaService selectAreaService;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return homeIconMapper;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnHomeIconService#findList(com.okdeer.mall.operate.dto.HomeIconParamDto)
	 */
	@Override
	public List<ColumnHomeIcon> findList(HomeIconParamDto paramDto) throws Exception {
		return homeIconMapper.findList(paramDto);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnHomeIconService#findListByCityId(java.lang.String, java.lang.String)
	 */
	@Override
	public List<HomeIconDto> findListByCityId(String provinceId, String cityId) throws Exception {
		if (!StringUtils.isNotEmptyAll(provinceId, cityId)) {
			return new ArrayList<HomeIconDto>();
		}
		// 根据城市查询相应的首页ICON栏位
		List<String> ids = selectAreaService.findColumnIdsByCity(cityId, provinceId, ColumnType.homeIcon.ordinal());
		if (null == ids || ids.size() == 0) {
			return new ArrayList<HomeIconDto>();
		}

		// 设置首页ICON查询参数
		HomeIconParamDto paramDto = new HomeIconParamDto();
		paramDto.setIds(ids);
		// 查询首页ICON列表
		List<ColumnHomeIcon> sourceList = findList(paramDto);
		List<HomeIconDto> dtoList = null;
		if (null == sourceList) {
			dtoList = new ArrayList<HomeIconDto>();
		} else {
			dtoList = BeanMapper.mapList(sourceList, HomeIconDto.class);
		}
		return dtoList;
	}

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.operate.service.ColumnHomeIconService#save(com.okdeer.mall.operate.entity.ColumnHomeIcon, java.util.List, java.util.List)
	 */
	@Override
	public BaseResult save(ColumnHomeIcon entity, List<ColumnSelectArea> areaList, List<String> goodsIds)
			throws Exception {
		if (entity == null) {
			return new BaseResult("HomeIconDto信息不能为空");
		}
		if (null == entity.getPlace() || null == entity.getTaskScope() || null == entity.getTaskType()
				|| !StringUtils.isNotEmptyAll(entity.getName(), entity.getIconUrl())) {
			return new BaseResult("HomeIconDto信息不完整");
		}

		if (SelectAreaType.city.equals(entity.getTaskScope()) && (null == areaList || 0 == areaList.size())) {
			return new BaseResult("城市ID集合 当任务范围为1:按城市选择任务范围时， 不允许为空");
		}

		if (HomeIconTaskType.goods.equals(entity.getTaskType()) && (null == goodsIds || 0 == goodsIds.size())) {
			return new BaseResult("商品ID集合   当任务内容  0:指定指定商品推荐时， 不允许为空");
		}

		// 设置首页ICONID
		String homeIconId = StringUtils.isBlank(entity.getId()) ? UuidUtils.getUuid() : entity.getId();
		// 判断是否存在重复
		if (isRepeatArea(homeIconId, entity.getTaskScope(), entity.getPlace(), areaList)) {
			return new BaseResult("同一区域、同一位置不可同时存在同一ICON栏位设置");
		}

		// 冗余字段部分信息
		if (SelectAreaType.nationwide.equals(entity.getTaskScope())) {
			// 如果任务范围是全国，则直接填充全国
			entity.setTaskContent("全国");
		} else {
			// 如果任务范围是根据城市选择， 这获取第一个城市或者是省
			ColumnSelectArea area = areaList.get(0);
			String areaName = "";
			if (SelectAreaType.province.equals(area.getAreaType())) {
				areaName = area.getProvinceName();
			} else {
				areaName = area.getCityName();
			}
			if (StringUtils.isNotBlank(areaName)) {
				entity.setTaskContent(areaName + "等" + areaList.size() + "个区域");
			}
		}

		// 修改首页ICON
		if (StringUtils.isNotBlank(entity.getId())) {
			// 删除之前的插入的关联数据
			selectAreaService.deleteByColumnId(entity.getId());
			homeIconGoodsService.deleteByHomeIconId(entity.getId());
			entity.setUpdateTime(DateUtils.getSysDate());
			homeIconMapper.update(entity);
		} else {
			entity.setId(homeIconId);
			entity.setCreateTime(DateUtils.getSysDate());
			homeIconMapper.add(entity);
		}

		// 插入所选地区范围关联数据
		if (null != areaList && areaList.size() > 0) {
			for (ColumnSelectArea item : areaList) {
				if (SelectAreaType.province.equals(item.getAreaType())) {
					item.setCityId("0");
				}
				item.setColumnType(2);
				item.setId(UuidUtils.getUuid());
				item.setColumnId(homeIconId);
				item.setColumnType(ColumnType.homeIcon.ordinal());
			}
			selectAreaService.insertMore(areaList);
		}

		// 插入所需的商品关联数据
		if (HomeIconTaskType.goods.equals(entity.getTaskType())) {
			List<ColumnHomeIconGoods> goodsList = new ArrayList<>();
			ColumnHomeIconGoods goods = null;
			for (String item : goodsIds) {
				goods = new ColumnHomeIconGoods();
				goods.setId(UuidUtils.getUuid());
				goods.setHomeIconId(entity.getId());
				goods.setStoreSkuId(item);
				goodsList.add(goods);
			}
			if (goodsList.size() > 0) {
				homeIconGoodsService.insertMore(goodsList);
			}
		}
		return new BaseResult();
	}

	private boolean isRepeatArea(String homeIconId, SelectAreaType taskScope, HomeIconPlace place,
			List<ColumnSelectArea> areaList) throws Exception {
		// 查询是否已经存在使用了相同的ICON位置数据
		HomeIconParamDto paramDto = new HomeIconParamDto();
		paramDto.setPlace(place);
		// 设置排除自己
		paramDto.setExcludeId(homeIconId);
		List<ColumnHomeIcon> list = homeIconMapper.findList(paramDto);
		// 未查出数据则表示当前ICON位置还未发布过
		if (null == list || list.size() == 0) {
			return false;
		}
		// 如果当前设置任务范围是全国
		if (SelectAreaType.nationwide.equals(taskScope)) {
			return true;
		}
		List<String> columnIds = new ArrayList<>();
		for (ColumnHomeIcon item : list) {
			columnIds.add(item.getId());
		}
		// 已保存省ID集合(全省)
		List<String> dbProvinceIds = new ArrayList<>();
		// 已保存省ID集合(部分城市)
		List<String> dbPartProvinceIds = new ArrayList<>();
		// 已保存选城市ID集合
		List<String> dbCityIds = new ArrayList<>();
		// 查询已存在的
		List<ColumnSelectArea> dbAreList = selectAreaService.findListByColumnIds(columnIds);
		for (ColumnSelectArea item : dbAreList) {
			if (SelectAreaType.province.equals(item.getAreaType())) {
				dbProvinceIds.add(item.getProvinceId());
			} else {
				dbPartProvinceIds.add(item.getProvinceId());
				dbCityIds.add(item.getCityId());
			}
		}
		for (ColumnSelectArea item : areaList) {
			if (SelectAreaType.province.equals(item.getAreaType())) {
				if (dbProvinceIds.add(item.getProvinceId()) || dbPartProvinceIds.add(item.getProvinceId())) {
					return true;
				}
			} else if (SelectAreaType.city.equals(item.getAreaType()) && dbCityIds.add(item.getCityId())) {
				return true;
			}
		}
		return false;
	}
}
