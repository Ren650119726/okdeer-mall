/** 
 *@Project: okdeer-mall-activity 
 *@Author: tangzj02
 *@Date: 2016年12月29日 
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
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.common.utils.BaseResult;
import com.okdeer.mall.operate.dto.AppRecommendDto;
import com.okdeer.mall.operate.dto.AppRecommendGoodsDto;
import com.okdeer.mall.operate.dto.AppRecommendGoodsParamDto;
import com.okdeer.mall.operate.dto.AppRecommendParamDto;
import com.okdeer.mall.operate.dto.SelectAreaDto;
import com.okdeer.mall.operate.entity.ColumnAppRecommend;
import com.okdeer.mall.operate.entity.ColumnAppRecommendGoods;
import com.okdeer.mall.operate.entity.ColumnSelectArea;
import com.okdeer.mall.operate.enums.AppRecommendPlace;
import com.okdeer.mall.operate.enums.AppRecommendStatus;
import com.okdeer.mall.operate.enums.ColumnType;
import com.okdeer.mall.operate.enums.GoodsShowStatus;
import com.okdeer.mall.operate.service.ColumnAppRecommendApi;
import com.okdeer.mall.operate.service.ColumnAppRecommendGoodsService;
import com.okdeer.mall.operate.service.ColumnAppRecommendService;
import com.okdeer.mall.operate.service.ColumnSelectAreaService;

/**
 * ClassName: ColumnAppRecommendApiImpl 
 * @Description: APP端服务商品推荐服务
 * @author tangzj02
 * @date 2016年12月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0       2016-12-29        tangzj02                     添加
 */

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.operate.service.ColumnAppRecommendApi")
public class ColumnAppRecommendApiImpl implements ColumnAppRecommendApi {

	/** 日志记录 */
	private static final Logger log = LoggerFactory.getLogger(ColumnAppRecommendApiImpl.class);

	@Autowired
	private ColumnAppRecommendService appRecommendService;

	@Autowired
	private ColumnAppRecommendGoodsService appRecommendGoodsService;

	@Autowired
	private ColumnSelectAreaService selectAreaService;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendApi#findList(com.okdeer.mall.operate.dto.AppRecommendParamDto)
	 */
	@Override
	public List<AppRecommendDto> findList(AppRecommendParamDto paramDto) throws Exception {
		log.info("查询APP端服务商品推荐列表参数:{}", paramDto);
		List<ColumnAppRecommend> sourceList = appRecommendService.findList(paramDto);
		List<AppRecommendDto> dtoList = null;
		if (null == sourceList) {
			dtoList = new ArrayList<AppRecommendDto>();
		} else {
			dtoList = BeanMapper.mapList(sourceList, AppRecommendDto.class);
		}
		return dtoList;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendApi#findById(java.lang.String)
	 */
	@Override
	public AppRecommendDto findById(String id) throws Exception {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		// 查询APP端服务商品推荐信息
		ColumnAppRecommend recommend = appRecommendService.findById(id);
		if (null == recommend) {
			return null;
		}
		// 复制APP端服务商品推荐信息
		AppRecommendDto dto = BeanMapper.map(recommend, AppRecommendDto.class);

		// 查询商品关联信息
		List<AppRecommendGoodsDto> goodsDtoList = null;
		List<ColumnAppRecommendGoods> goodsList = appRecommendGoodsService.findListByRecommendId(dto.getId());
		if (null == goodsList) {
			goodsDtoList = new ArrayList<>();
		} else {
			goodsDtoList = BeanMapper.mapList(goodsList, AppRecommendGoodsDto.class);
		}
		dto.setGoodsList(goodsDtoList);

		// 查询地区关联信息
		List<SelectAreaDto> areaDtoList = null;
		List<ColumnSelectArea> areaList = selectAreaService.findListByColumnId(dto.getId());
		if (null == areaList) {
			areaDtoList = new ArrayList<>();
		} else {
			areaDtoList = BeanMapper.mapList(areaList, SelectAreaDto.class);
		}
		dto.setAreaList(areaDtoList);
		return dto;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendApi#findAppRecommendGoodsDtoListByRecommendId(java.lang.String)
	 */
	@Override
	public List<AppRecommendGoodsDto> findAppRecommendGoodsDtoListByRecommendId(String recommendId) throws Exception {
		if (StringUtils.isBlank(recommendId)) {
			return null;
		}
		List<ColumnAppRecommendGoods> goodsList = appRecommendGoodsService.findListByRecommendId(recommendId);
		List<AppRecommendGoodsDto> goodsDtoList = null;
		if (null == goodsList) {
			goodsDtoList = new ArrayList<>();
		} else {
			goodsDtoList = BeanMapper.mapList(goodsList, AppRecommendGoodsDto.class);
		}
		return goodsDtoList;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendApi#findAppRecommendGoodsDtoList(com.okdeer.mall.operate.dto.AppRecommendGoodsParamDto)
	 */
	@Override
	public List<AppRecommendGoodsDto> findAppRecommendGoodsDtoList(AppRecommendGoodsParamDto paramDto)
			throws Exception {
		List<ColumnAppRecommendGoods> goodsList = appRecommendGoodsService.findList(paramDto);
		List<AppRecommendGoodsDto> goodsDtoList = null;
		if (null == goodsList) {
			goodsDtoList = new ArrayList<>();
		} else {
			goodsDtoList = BeanMapper.mapList(goodsList, AppRecommendGoodsDto.class);
		}
		return goodsDtoList;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendApi#save(com.okdeer.mall.operate.dto.AppRecommendDto)
	 */
	@Override
	public BaseResult save(AppRecommendDto dto) throws Exception {
		// 复制推荐信息
		ColumnAppRecommend entity = BeanMapper.map(dto, ColumnAppRecommend.class);
		// 复制推荐地区信息
		List<ColumnSelectArea> areaList = null;
		if (null == dto.getAreaList()) {
			areaList = new ArrayList<>();
		} else {
			areaList = BeanMapper.mapList(dto.getAreaList(), ColumnSelectArea.class);
		}
		// 复制关联商品信息
		List<ColumnAppRecommendGoods> goodsList = null;
		if (null == dto.getGoodsList()) {
			goodsList = new ArrayList<>();
		} else {
			goodsList = BeanMapper.mapList(dto.getGoodsList(), ColumnAppRecommendGoods.class);
		}
		return appRecommendService.save(entity, areaList, goodsList);

	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendApi#closeById(java.lang.String)
	 */
	@Override
	public int closeById(String id) throws Exception {
		if (StringUtils.isBlank(id)) {
			return 0;
		}
		ColumnAppRecommend entity = new ColumnAppRecommend();
		entity.setId(id);
		entity.setStatus(AppRecommendStatus.close);
		entity.setUpdateTime(DateUtils.getSysDate());
		return appRecommendService.update(entity);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendApi#updateById(java.lang.String, java.lang.Integer)
	 */
	@Override
	public int updateById(String id, Integer sort) throws Exception {
		if (StringUtils.isBlank(id) || null == sort) {
			return 0;
		}
		ColumnAppRecommend entity = new ColumnAppRecommend();
		entity.setId(id);
		entity.setSort(sort);
		return appRecommendService.update(entity);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendApi#deleteByIds(java.util.List)
	 */
	@Override
	public int deleteByIds(List<String> ids) throws Exception {
		if (null == ids || ids.size() == 0) {
			return 0;
		}
		return appRecommendService.deleteByIds(ids);
	}

	@Override
	public PageUtils<AppRecommendDto> findListPage(AppRecommendParamDto paramDto) throws Exception {
		return appRecommendService.findListPage(paramDto);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendApi#findListByCity(java.lang.String, java.lang.String, java.lang.Integer)
	 */
	@Override
	public List<AppRecommendDto> findListByCity(String provinceId, String cityId, AppRecommendPlace place)
			throws Exception {
		if (!StringUtils.isNotEmptyAll(provinceId, cityId)) {
			return new ArrayList<AppRecommendDto>();
		}
		// 根据城市查询相应的服务商品推荐栏位
		List<String> ids = selectAreaService.findColumnIdsByCity(provinceId, cityId, ColumnType.appRecommend.ordinal());
		// 设置服务商品推荐参数
		AppRecommendParamDto paramDto = new AppRecommendParamDto();
		if (null != ids && ids.size() > 0) {
			paramDto.setIds(ids);
		}
		paramDto.setPlace(place);
		paramDto.setSortType(1);
		paramDto.setContainNationwide(true);

		// 查询服务商品推荐
		List<AppRecommendDto> dtoList = findList(paramDto);
		if (dtoList.size() == 0) {
			return dtoList;
		}
		ids.clear();
		for(AppRecommendDto item : dtoList){
			ids.add(item.getId());
		}

		// 设置推荐服务商品关联信息查询参数
		AppRecommendGoodsParamDto goodsParamDto = new AppRecommendGoodsParamDto();
		goodsParamDto.setRecommendIds(ids);

		// 查询推荐服务商品关联信息
		List<ColumnAppRecommendGoods> goodsList = appRecommendGoodsService.findList(goodsParamDto);
		List<AppRecommendGoodsDto> goodsDtoList = null;
		if (null == goodsList) {
			goodsDtoList = new ArrayList<>();
		} else {
			goodsDtoList = BeanMapper.mapList(goodsList, AppRecommendGoodsDto.class);
		}
		// 组装数据
		for (AppRecommendDto dto : dtoList) {
			// 初始化服务商品推荐中的全部商品关联集合
			dto.setGoodsList(new ArrayList<>());
			// 初始化服务商品推荐中的全部商品ID集合
			dto.setStoreSkuIds(new ArrayList<>());
			// 初始化服务商品推荐中的展示商品关联集合
			dto.setShowGoodsList(new ArrayList<>());
			// 初始化服务商品推荐中的展示商品ID集合
			dto.setShowStoreSkuIds(new ArrayList<>());
			for (AppRecommendGoodsDto goodsDto : goodsDtoList) {
				if (dto.getId().equals(goodsDto.getRecommendId())) {
					dto.getGoodsList().add(goodsDto);
					dto.getStoreSkuIds().add(goodsDto.getStoreSkuId());
					// 判断商品是否需要展示
					if (goodsDto.getIsShow().equals(GoodsShowStatus.show.ordinal())) {
						dto.getShowGoodsList().add(goodsDto);
						dto.getShowStoreSkuIds().add(goodsDto.getStoreSkuId());
					}
				}
			}
		}
		return dtoList;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendApi#findShowAppRecommendGoodsDtoList(java.util.List)
	 */
	@Override
	public List<AppRecommendGoodsDto> findShowAppRecommendGoodsDtoList(List<String> storeSkuIds) throws Exception {
		if (null == storeSkuIds || storeSkuIds.size() == 0) {
			return new ArrayList<AppRecommendGoodsDto>();
		}
		List<ColumnAppRecommendGoods> goodsList = appRecommendGoodsService.findShowListByStoreSkuIds(storeSkuIds);
		List<AppRecommendGoodsDto> dtoList = null;
		if (null == goodsList) {
			dtoList = new ArrayList<AppRecommendGoodsDto>();
		} else {
			dtoList = BeanMapper.mapList(goodsList, AppRecommendGoodsDto.class);
		}
		return dtoList;
	}
}
