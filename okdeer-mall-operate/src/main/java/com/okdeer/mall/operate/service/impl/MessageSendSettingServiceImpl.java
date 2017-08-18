/** 
 *@Project: okdeer-mall-operate 
 *@Author: xuzq01
 *@Date: 2017年8月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.operate.dto.MessageSendSettingQueryDto;
import com.okdeer.mall.operate.entity.MessageSendSetting;
import com.okdeer.mall.operate.mapper.MessageSendSettingMapper;
import com.okdeer.mall.operate.service.MessageSendSettingService;


/**
 * ClassName: MessageSendSettingServiceImpl 
 * @Description: 消息推送设置service实现类
 * @author xuzq01
 * @date 2017年8月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class MessageSendSettingServiceImpl extends BaseServiceImpl implements MessageSendSettingService {

	@Autowired
	private MessageSendSettingMapper messageSendSettingMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return messageSendSettingMapper;
	}

	@Override
	public PageUtils<MessageSendSetting> findPageList(MessageSendSettingQueryDto paramDto, int pageNumber,
			int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<MessageSendSetting>  messageList = messageSendSettingMapper.findPageList(paramDto);
		return new PageUtils<MessageSendSetting>(messageList);
	}

	@Override
	public int findCountByName(String messageName) {
		return messageSendSettingMapper.findCountByName(messageName);
	}

	@Override
	public List<MessageSendSetting> findMessageListByStatus(int status) {
		MessageSendSettingQueryDto setting = new MessageSendSettingQueryDto();
		setting.setStatus(status);
		return messageSendSettingMapper.findPageList(setting);
	}

	@Override
	public int updateSetting(MessageSendSetting entity) {
		return messageSendSettingMapper.updateSetting(entity);
	}

}
