/** 
 *@Project: okdeer-mall-operate 
 *@Author: xuzq01
 *@Date: 2017年8月17日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.operate.entity.MessageSendSelectArea;
import com.okdeer.mall.operate.mapper.MessageSendSelectAreaMapper;
import com.okdeer.mall.operate.service.MessageSendSelectAreaService;


/**
 * ClassName: MessageSendSelectAreaServiceImpl 
 * @Description: app消息推送关联推送地区城市表service实现类
 * @author xuzq01
 * @date 2017年8月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class MessageSendSelectAreaServiceImpl extends BaseServiceImpl implements MessageSendSelectAreaService {

	@Autowired
	private MessageSendSelectAreaMapper messageSendSelectAreaMapper;

	@Override
	public IBaseMapper getBaseMapper() {
		return messageSendSelectAreaMapper;
	}

	@Override
	public void deleteByMessageId(String messageId) {
		messageSendSelectAreaMapper.deleteByMessageId(messageId);
	}

	@Override
	public List<MessageSendSelectArea> findListByMessageId(String messageId) {
		return messageSendSelectAreaMapper.findListByMessageId(messageId);
		
	}
	

}
