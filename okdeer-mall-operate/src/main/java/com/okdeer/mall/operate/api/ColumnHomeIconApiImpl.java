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
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.common.utils.BaseResult;
import com.okdeer.mall.operate.dto.HomeIconDto;
import com.okdeer.mall.operate.dto.HomeIconGoodsDto;
import com.okdeer.mall.operate.dto.HomeIconParamDto;
import com.okdeer.mall.operate.entity.ColumnHomeIcon;
import com.okdeer.mall.operate.entity.ColumnHomeIconGoods;
import com.okdeer.mall.operate.entity.ColumnSelectArea;
import com.okdeer.mall.operate.enums.ColumnType;
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
	 * @see com.okdeer.mall.operate.service.ColumnHomeIconApi#findListPage(com.okdeer.mall.operate.dto.HomeIconParamDto)
	 */
	@Override
	public PageUtils<HomeIconDto> findListPage(HomeIconParamDto paramDto) throws Exception {
		PageHelper.startPage(paramDto.getPageNumber(), paramDto.getPageSize(), true);
		List<ColumnHomeIcon> result = homeIconService.findList(paramDto);
		if (result == null) {
			result = new ArrayList<ColumnHomeIcon>();
		}
		List<HomeIconDto> list = BeanMapper.mapList(result, HomeIconDto.class);
		return new PageUtils<HomeIconDto>(list);
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

		// 查询商品关联信息
		List<ColumnHomeIconGoods> goodsList = homeIconGoodsService.findListByHomeIconId(homeIconId);
		List<HomeIconGoodsDto> goodsDtoList = null;
		if (goodsList == null) {
			goodsDtoList = new ArrayList<>();
		} else {
			goodsDtoList = BeanMapper.mapList(goodsList, HomeIconGoodsDto.class);
		}
		dto.setGoodsList(goodsDtoList);
		return dto;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnSelectAreaApi#deleteById(java.lang.String)
	 */
	@Override
	public BaseResult deleteById(String homeIconId) throws Exception {
		if (StringUtils.isBlank(homeIconId)) {
			return new BaseResult("homeIconId不能为空");
		}
		int result = homeIconService.delete(homeIconId);
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
		ColumnHomeIcon entity = BeanMapper.map(dto, ColumnHomeIcon.class);
		List<ColumnSelectArea> areaList = null;
		if (null == dto.getAreaList()) {
			areaList = new ArrayList<>();
		} else {
			areaList = BeanMapper.mapList(dto.getAreaList(), ColumnSelectArea.class);
		}
		return homeIconService.save(entity, areaList, dto.getStoreSkuIds());
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnHomeIconApi#findListByCityId(java.lang.String, java.lang.String)
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
		List<HomeIconDto> sourceList = findList(paramDto);
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
	 * @see com.okdeer.mall.operate.service.ColumnHomeIconApi#findHomeIconGoodsIdsByHomeIconId(java.lang.String)
	 */
	@Override
	public List<String> findHomeIconGoodsIdsByHomeIconId(String homeIconId) throws Exception {
		if (StringUtils.isBlank(homeIconId)) {
			return new ArrayList<>();
		}
		List<ColumnHomeIconGoods> sourceList = homeIconGoodsService.findListByHomeIconId(homeIconId);
		if (sourceList == null) {
			return new ArrayList<>();
		}
		List<String> storeSkuIds = new ArrayList<>();
		for (ColumnHomeIconGoods item : sourceList) {
			storeSkuIds.add(item.getId());
		}
		return storeSkuIds;
	}
}
