
package com.okdeer.mall.operate.api;

import static com.okdeer.mall.operate.contants.OperateFieldContants.TAG_ADDEDIT_OPERATE_FIELD;
import static com.okdeer.mall.operate.contants.OperateFieldContants.TAG_ENABLEDISABLE_OPERATE_FIELD;
import static com.okdeer.mall.operate.contants.OperateFieldContants.TAG_RANK_OPERATE_FIELD;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.mall.operate.contants.OperateFieldContants;
import com.okdeer.mall.operate.dto.GoodsChangedMsgDto;
import com.okdeer.mall.operate.dto.OperateFieldContentDto;
import com.okdeer.mall.operate.dto.OperateFieldDto;
import com.okdeer.mall.operate.dto.OperateFieldsContentDto;
import com.okdeer.mall.operate.dto.OperateFieldsDto;
import com.okdeer.mall.operate.dto.OperateFieldsQueryParamDto;
import com.okdeer.mall.operate.enums.OperateFieldsType;
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
	
	/**
     * mq注入
     */
    @Autowired
    private RocketMQProducer rocketMQProducer;
    
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
		
		//发送栏位变更消息
		GoodsChangedMsgDto msgDto = new GoodsChangedMsgDto();
		OperateFieldsType type = operateFieldsDto.getType();
		if (type == OperateFieldsType.CITY) {
		    msgDto.setCityId(operateFieldsDto.getBusinessId());
		} else if (type == OperateFieldsType.STORE) {
		    msgDto.setStoreId(operateFieldsDto.getBusinessId());
		}
		
		try {
            produceMessage(msgDto, TAG_ADDEDIT_OPERATE_FIELD);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@Override
	public void update(OperateFieldsDto operateFieldsDto, List<OperateFieldsContentDto> operateFieldscontentDtoList) {
		OperateFields operateFields = BeanMapper.map(operateFieldsDto, OperateFields.class);
		List<OperateFieldsContent> list = BeanMapper.mapList(operateFieldscontentDtoList, OperateFieldsContent.class);
		operateFieldsService.update(operateFields, list);
		
	    //发送栏位变更消息
        GoodsChangedMsgDto msgDto = new GoodsChangedMsgDto();
        OperateFieldsType type = operateFieldsDto.getType();
        if (type == OperateFieldsType.CITY) {
            msgDto.setCityId(operateFieldsDto.getBusinessId());
        } else if (type == OperateFieldsType.STORE) {
            msgDto.setStoreId(operateFieldsDto.getBusinessId());
        }
        
        try {
            produceMessage(msgDto, TAG_ADDEDIT_OPERATE_FIELD);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public void updateSort(String id, boolean isUp) throws Exception {
		operateFieldsService.updateSort(id, isUp);
		
		//查询栏位信息
		OperateFields operateFields = this.operateFieldsService.findById(id);
        //发送栏位变更消息
        GoodsChangedMsgDto msgDto = new GoodsChangedMsgDto();
        OperateFieldsType type = operateFields.getType();
        if (type == OperateFieldsType.CITY) {
            msgDto.setCityId(operateFields.getBusinessId());
        } else if (type == OperateFieldsType.STORE) {
            msgDto.setStoreId(operateFields.getBusinessId());
        }
        try {
            produceMessage(msgDto, TAG_RANK_OPERATE_FIELD);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public int update(OperateFieldsDto operateFieldsDto) throws Exception {
		OperateFields operateFields = BeanMapper.map(operateFieldsDto, OperateFields.class);
		
        // 启用禁用栏位时发送消息
        // 发送栏位变更消息
        GoodsChangedMsgDto msgDto = new GoodsChangedMsgDto();
        OperateFieldsType type = operateFieldsDto.getType();
        if (type == OperateFieldsType.CITY) {
            msgDto.setCityId(operateFieldsDto.getBusinessId());
        } else if (type == OperateFieldsType.STORE) {
            msgDto.setStoreId(operateFieldsDto.getBusinessId());
        }

        try {
            produceMessage(msgDto, TAG_ENABLEDISABLE_OPERATE_FIELD);
        } catch (Exception e) {
            e.printStackTrace();
        }
		
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
    public Set<OperateFieldDto> initStoreOperateFieldData(String storeId) throws Exception {
       return this.operateFieldsService.initStoreOperateFieldData(storeId);
    }

    /**
     * 初始化城市运营栏位
     * @param cityId
     * @throws Exception
     * @author zhaoqc
     * @date 2017-4-18
     */
    @Override
    public Set<OperateFieldDto> initCityOperateFieldData(String cityId) throws Exception {
        return this.operateFieldsService.initCityOperateFieldData(cityId);
    }

	@Override
	public void initOperationField(String storeId) throws Exception {
		if(StringUtils.isNotBlank(storeId)){
			operateFieldsService.initOperationField(storeId);
		}
	}

	@Override
	public void initOperationFieldContext(String storeId) throws Exception {
		if(StringUtils.isNotBlank(storeId)){
			operateFieldsContentService.initOperationFieldContext(storeId);
		}
	}

    @Override
    public OperateFieldContentDto getSingleGoodsOfOperateField(String goodsId, 
            String storeId) throws Exception {
        return operateFieldsService.getSingleGoodsOfOperateField(goodsId, storeId);
    }
	
    public void produceMessage(GoodsChangedMsgDto data, String tag) throws Exception {
        MQMessage anMessage = new MQMessage(OperateFieldContants.TOPIC_OPERATE_FIELD);
        anMessage.setTags(tag);
        anMessage.setContent(data);
        rocketMQProducer.sendMessage(anMessage);
    }
	
}
