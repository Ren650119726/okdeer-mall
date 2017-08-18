/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * MessageSendSettingMapper.java
 * @Date 2017-08-15 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.operate.dto.MessageSendSettingQueryDto;
import com.okdeer.mall.operate.entity.MessageSendSetting;

public interface MessageSendSettingMapper extends IBaseMapper {

	/**
	 * @Description: 根据条件查询消息推送列表
	 * @param paramDto   
	 * @author xuzq01
	 * @date 2017年8月15日
	 */
	List<MessageSendSetting> findPageList(MessageSendSettingQueryDto paramDto);

	/**
	 * @Description: 通过名称查询消息数量
	 * @param messageName
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月17日
	 */
	int findCountByName(String messageName);

	/**
	 * @Description: 根据消息设置的发送时间修改消息
	 * @param entity
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月18日
	 */
	int updateSetting(MessageSendSetting entity);

}