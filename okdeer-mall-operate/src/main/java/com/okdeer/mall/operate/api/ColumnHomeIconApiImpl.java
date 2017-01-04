/** 
 *@Project: okdeer-mall-operate 
 *@Author: tangzj02
 *@Date: 2016年12月28日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.common.utils.BaseResult;
import com.okdeer.mall.operate.dto.HomeIconDto;
import com.okdeer.mall.operate.dto.HomeIconGoodsDto;
import com.okdeer.mall.operate.dto.HomeIconParamDto;
import com.okdeer.mall.operate.entity.ColumnHomeIcon;
import com.okdeer.mall.operate.entity.ColumnHomeIconGoods;
import com.okdeer.mall.operate.entity.ColumnSelectArea;
import com.okdeer.mall.operate.enums.HomeIconPlace;
import com.okdeer.mall.operate.enums.HomeIconTaskType;
import com.okdeer.mall.operate.enums.SelectAreaType;
import com.okdeer.mall.operate.service.ColumnHomeIconApi;
import com.okdeer.mall.operate.service.ColumnHomeIconGoodsService;
import com.okdeer.mall.operate.service.ColumnHomeIconService;
import com.okdeer.mall.operate.service.ColumnSelectAreaService;

/**
 * ClassName: HomeIconApiImpl 
 * @Description: 首页ICON服务接口实现
 * @author tangzj02
 * @date 2016年12月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	   友门鹿2.0        2016-12-28        tangzj02                     添加
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.operate.service.ColumnHomeIconApi")
public class ColumnHomeIconApiImpl implements ColumnHomeIconApi {

	/** 日志记录 */
	private static final Logger log = LoggerFactory.getLogger(ColumnHomeIconApiImpl.class);

	@Autowired
	private ColumnHomeIconService homeIconService;

	@Autowired
	private ColumnHomeIconGoodsService homeIconGoodsService;

	@Autowired
	private ColumnSelectAreaService selectAreaService;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnSelectAreaApi#findList(com.okdeer.mall.operate.dto.HomeIconParamDto)
	 */
	@Override
	public List<HomeIconDto> findList(HomeIconParamDto paramDto) throws Exception {
		log.info("查询首页IOCN列表参数:{}", paramDto);
		List<ColumnHomeIcon> sourceList = homeIconService.findList(paramDto);
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
	 * @see com.okdeer.mall.operate.service.ColumnSelectAreaApi#findHomeIconProductDtoListByHomeIconId(java.lang.String)
	 */
	@Override
	public List<HomeIconGoodsDto> findHomeIconGoodsDtoListByHomeIconId(String homeIconId) throws Exception {
		List<HomeIconGoodsDto> dtoList = new ArrayList<>();
		if (StringUtils.isNotBlank(homeIconId)) {
			List<ColumnHomeIconGoods> sourceList = homeIconGoodsService.findListByHomeIconId(homeIconId);
			if (sourceList != null) {
				dtoList = BeanMapper.mapList(sourceList, HomeIconGoodsDto.class);
			}
		}
		return dtoList;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnSelectAreaApi#findById(java.lang.String)
	 */
	@Override
	public HomeIconDto findById(String homeIconId) throws Exception {
		if (StringUtils.isBlank(homeIconId)) {
			return null;
		}
		ColumnHomeIcon source = homeIconService.findById(homeIconId);
		if (null == source) {
			return null;
		}
		HomeIconDto dto = BeanMapper.map(source, HomeIconDto.class);
		return dto;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnSelectAreaApi#deleteById(java.lang.String)
	 */
	@Override
	public BaseResult deleteById(String iconId) throws Exception {
		if (StringUtils.isBlank(iconId)) {
			return new BaseResult("homeIconId不能为空");
		}
		int result = homeIconService.delete(iconId);
		if (result > 0) {
			return new BaseResult();
		}
		return new BaseResult("删除失败");
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnSelectAreaApi#save(com.okdeer.mall.operate.dto.HomeIconDto)
	 */
	@Override
	public BaseResult save(HomeIconDto dto) throws Exception {
		if (dto == null) {
			return new BaseResult("HomeIconDto信息不能为空");
		}
		if (null == dto.getPlace() || null == dto.getTaskScope() || null == dto.getTaskType()
				|| !StringUtils.isNotEmptyAll(dto.getName(), dto.getIconUrl())) {
			return new BaseResult("HomeIconDto信息不完整");
		}

		List<ColumnSelectArea> areaList = null;
		if (SelectAreaType.city.equals(dto.getTaskScope())) {
			if (null == dto.getAreaList() || 0 == dto.getAreaList().size()) {
				return new BaseResult("城市ID集合 当任务范围未1:按城市选择任务范围时， 不允许为空");
			} else {
				areaList = BeanMapper.mapList(dto.getAreaList(), ColumnSelectArea.class);
			}
		}

		if (HomeIconTaskType.goods.equals(dto.getTaskType())
				&& (null == dto.getGoodsIds() || 0 == dto.getGoodsIds().size())) {
			return new BaseResult("商品ID集合   当任务内容  0:指定指定商品推荐时， 不允许为空");
		}

		// 复制属性信息
		ColumnHomeIcon entity = BeanMapper.map(dto, ColumnHomeIcon.class);
		// 设置首页ICONID
		String homeIconId = StringUtils.isBlank(entity.getId()) ? UuidUtils.getUuid() : entity.getId();
		// 判断是否存在重复
		if (isRepeatArea(homeIconId, entity.getTaskScope(), entity.getPlace(), areaList)) {
			return new BaseResult("同一区域、同一位置不可同时存在同一ICON栏位设置");
		}

		// 冗余字段部分信息
		if (SelectAreaType.nationwide.equals(dto.getTaskScope())) {
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
			homeIconService.update(entity);
		} else {
			entity.setId(homeIconId);
			entity.setCreateTime(DateUtils.getSysDate());
			homeIconService.add(entity);
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
			}
			selectAreaService.insertMore(areaList);
		}

		// 插入所需的商品关联数据
		if (HomeIconTaskType.goods.equals(dto.getTaskType())) {
			List<ColumnHomeIconGoods> goodsList = new ArrayList<>();
			ColumnHomeIconGoods goods = null;
			for (String item : dto.getGoodsIds()) {
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
			List<ColumnSelectArea> areaList) {
		// 查询是否已经存在使用了相同的ICON位置数据
		HomeIconParamDto paramDto = new HomeIconParamDto();
		paramDto.setPlace(place);
		// 设置排除自己
		paramDto.setExcludeId(homeIconId);
		List<ColumnHomeIcon> list = homeIconService.findList(paramDto);
		// 未查出数据则表示当前ICON位置还未发布过
		if (null == list || list.size() == 0) {
			return false;
		}
		// 如果当前设置任务范围是全国
		if (SelectAreaType.nationwide.equals(taskScope)) {
			return true;
		}
		// 已保存省ID集合(全省)
		List<String> dbProvinceIds = new ArrayList<>();
		// 已保存省ID集合(部分城市)
		List<String> dbPartProvinceIds = new ArrayList<>();
		// 已保存选城市ID集合
		List<String> dbCityIds = new ArrayList<>();
		for (ColumnSelectArea item : areaList) {
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
