/** 
 *@Project: okdeer-mall-operate 
 *@Author: tangzj02
 *@Date: 2016年12月28日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.api;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.operate.dto.ColumnNativeSubjectDto;
import com.okdeer.mall.operate.dto.ColumnNativeSubjectGoodsDto;
import com.okdeer.mall.operate.dto.ColumnNativeSubjectParamDto;
import com.okdeer.mall.operate.entity.ColumnNativeSubject;
import com.okdeer.mall.operate.entity.ColumnNativeSubjectGoods;
import com.okdeer.mall.operate.service.ColumnNativeSubjectApi;
import com.okdeer.mall.operate.service.ColumnNativeSubjectGoodsService;
import com.okdeer.mall.operate.service.ColumnNativeSubjectService;

/**
 * ClassName: HomeIconApiImpl 
 * @Description: 原生专题服务接口实现
 * @author tangzj02
 * @date 2017-04-13
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	   友门鹿2.0        2017-04-13        zhangkn                     添加
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.operate.service.ColumnNativeSubjectApi")
public class ColumnNativeSubjectApiImpl implements ColumnNativeSubjectApi {

	/** 日志记录 */
	private static final Logger log = LoggerFactory.getLogger(ColumnNativeSubjectApiImpl.class);

	@Autowired
	private ColumnNativeSubjectService service;
	@Autowired
	private ColumnNativeSubjectGoodsService columnNativeSubjectGoodsService;

	@Override
	public List<ColumnNativeSubjectDto> findList(ColumnNativeSubjectParamDto param) throws Exception {
		List<ColumnNativeSubject> sourceList = service.findList(param);
		return BeanMapper.mapList(sourceList, ColumnNativeSubjectDto.class);
	}

	@Override
	public PageUtils<ColumnNativeSubjectDto> findListPage(ColumnNativeSubjectParamDto param,Integer pageNumber,Integer pageSize) throws Exception {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ColumnNativeSubject> result = service.findList(param);
		return new PageUtils<ColumnNativeSubject>(result).toBean(ColumnNativeSubjectDto.class);
	}

	@Override
	public ColumnNativeSubjectDto findById(String id,int isJoinDetail) throws Exception {
		ColumnNativeSubjectDto dto = BeanMapper.map(service.findById(id), ColumnNativeSubjectDto.class);
		if(dto != null){
			//关联的商品信息
			if(isJoinDetail == 1){
				List<ColumnNativeSubjectGoods> goodsList = columnNativeSubjectGoodsService.findByColumnNativeSubjectId(dto.getId());
				dto.setGoodsDtoList(BeanMapper.mapList(goodsList, ColumnNativeSubjectGoodsDto.class));
			}
		}
		return dto;
	}

	@Override
	public void deleteById(String id) throws Exception {
		service.deleteById(id);
	}

	@Override
	public void add(ColumnNativeSubjectDto dto) throws Exception {
		List<ColumnNativeSubjectGoods> goodsList = BeanMapper.mapList(dto.getGoodsDtoList(), ColumnNativeSubjectGoods.class);
		ColumnNativeSubject obj = BeanMapper.map(dto, ColumnNativeSubject.class);
		service.add(obj,goodsList);
	}

	@Override
	public void update(ColumnNativeSubjectDto dto) throws Exception {
		List<ColumnNativeSubjectGoods> goodsList = BeanMapper.mapList(dto.getGoodsDtoList(), ColumnNativeSubjectGoods.class);
		ColumnNativeSubject obj = BeanMapper.map(dto, ColumnNativeSubject.class);
		service.update(obj,goodsList);
	}
}