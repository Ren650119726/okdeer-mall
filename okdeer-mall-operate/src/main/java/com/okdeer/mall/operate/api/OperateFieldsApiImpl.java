
package com.okdeer.mall.operate.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.operate.dto.OperateFieldsContentDto;
import com.okdeer.mall.operate.dto.OperateFieldsDto;
import com.okdeer.mall.operate.dto.OperateFieldsQueryParamDto;
import com.okdeer.mall.operate.operatefields.bo.OperateFieldsBo;
import com.okdeer.mall.operate.operatefields.entity.OperateFields;
import com.okdeer.mall.operate.operatefields.entity.OperateFieldsContent;
import com.okdeer.mall.operate.operatefields.service.OperateFieldsContentService;
import com.okdeer.mall.operate.operatefields.service.OperateFieldsService;
import com.okdeer.mall.operate.service.OperateFieldsApi;

@Service(version = "1.0.0")
public class OperateFieldsApiImpl implements OperateFieldsApi {

	@Autowired
	private OperateFieldsService operateFieldsService;
	
	@Autowired
	private OperateFieldsContentService operateFieldsContentService;
	
	@Override
	public List<OperateFieldsDto> findList(OperateFieldsQueryParamDto queryParamDto) {
		List<OperateFields> list = operateFieldsService.findList(queryParamDto);
		if(list == null){
			return Lists.newArrayList();
		}
		return BeanMapper.mapList(list, OperateFieldsDto.class);
	}

	@Override
	public List<OperateFieldsDto> findListWithContent(OperateFieldsQueryParamDto queryParamDto) {
		List<OperateFieldsBo> list = operateFieldsService.findListWithContent(queryParamDto);
		if(list == null){
			return Lists.newArrayList();
		}
		
		List<OperateFieldsDto> dtoList = Lists.newArrayList();
		for (OperateFieldsBo operateFieldsBo : list) {
			OperateFieldsDto dto = BeanMapper.map(operateFieldsBo, OperateFieldsDto.class);
			List<OperateFieldsContentDto> operateFieldscontentDtoList = BeanMapper.mapList(operateFieldsBo.getOperateFieldscontentList(), OperateFieldsContentDto.class);
			dto.setOperateFieldscontentDtoList(operateFieldscontentDtoList);
			dtoList.add(dto);
		}
		return dtoList;
		
	}

	@Override
	public void save(OperateFieldsDto operateFieldsDto, List<OperateFieldsContentDto> operateFieldscontentDtoList) {
		OperateFields operateFields = BeanMapper.map(operateFieldsDto, OperateFields.class);
		List<OperateFieldsContent> list = BeanMapper.mapList(operateFieldscontentDtoList, OperateFieldsContent.class);
		operateFieldsService.save(operateFields, list);
	}

	@Override
	public void update(OperateFieldsDto operateFieldsDto, List<OperateFieldsContentDto> operateFieldscontentDtoList) {
		OperateFields operateFields = BeanMapper.map(operateFieldsDto, OperateFields.class);
		List<OperateFieldsContent> list = BeanMapper.mapList(operateFieldscontentDtoList, OperateFieldsContent.class);
		operateFieldsService.update(operateFields, list);
	}

	@Override
	public void updateSort(String id, boolean isUp) throws Exception {
		operateFieldsService.updateSort(id, isUp);
	}

	@Override
	public int update(OperateFieldsDto operateFieldsDto) throws Exception {
		OperateFields operateFields = BeanMapper.map(operateFieldsDto, OperateFields.class);
		return operateFieldsService.update(operateFields);
	}

	@Override
	public OperateFieldsDto findById(String id) throws Exception {
		OperateFields operateFields =  operateFieldsService.findById(id);
		List<OperateFieldsContent> operateFieldsContentList =  operateFieldsContentService.findByFieldId(id);
		List<OperateFieldsContentDto> operateFieldsContentDtoList = BeanMapper.mapList(operateFieldsContentList, OperateFieldsContentDto.class);
		OperateFieldsDto dto = BeanMapper.map(operateFields, OperateFieldsDto.class);
		dto.setOperateFieldscontentDtoList(operateFieldsContentDtoList);
		return dto;
	}

	/**
     * 初始化店铺运营栏位
     * @param storeId
     * @throws Exception
     * @author zhaoqc
     * @date 2017-4-18
     */
    @Override
    public void initStoreOperateFieldData(String storeId) throws Exception {
        this.operateFieldsService.initStoreOperateFieldData(storeId);
    }

    /**
     * 初始化城市运营栏位
     * @param cityId
     * @throws Exception
     * @author zhaoqc
     * @date 2017-4-18
     */
    @Override
    public void initCityOperateFieldData(String cityId) throws Exception {
        this.operateFieldsService.initCityOperateFieldData(cityId);
    }
	
	
	
}
