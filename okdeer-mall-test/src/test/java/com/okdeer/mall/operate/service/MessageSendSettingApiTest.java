/** 
 *@Project: okdeer-mall-test 
 *@Author: xuzq01
 *@Date: 2017年9月19日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.operate.dto.MessageSendSettingDto;
import com.okdeer.mall.operate.dto.MessageSendSettingQueryDto;

/**
 * ClassName: MessageSendSettingApiTest 
 * @Description: app消息推送设置api测试类
 * @author xuzq01
 * @date 2017年9月19日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Transactional
public class MessageSendSettingApiTest extends BaseServiceTest {
	
	@Autowired
	private MessageSendSettingApi messageSendSettingApi;
	
	private MessageSendSettingDto messageDto = new MessageSendSettingDto();
	@Before
    public void setUp() throws Exception {
        // 初始化测试用例类中由Mockito的注解标注的所有模拟对象
        MockitoAnnotations.initMocks(this);
        messageDto.setId(UuidUtils.getUuid());
        messageDto.setMessageName("单元测试添加");
        messageDto.setContext("单元测试添加修改");
        messageDto.setRangeType(0);
        messageDto.setSendType(0);
        messageDto.setType((byte) 2);
        messageDto.setStatus(0);
        messageDto.setCreateTime(DateUtils.getSysDate());
        messageDto.setCreateUserId("1");
        messageDto.setUpdateTime(DateUtils.getSysDate());
        messageDto.setUpdateUserId("1");
    }
	@Test
	public void findPageList() {
		MessageSendSettingQueryDto paramDto = new MessageSendSettingQueryDto();
		
		PageUtils<MessageSendSettingDto> settingDto = messageSendSettingApi.findPageList(paramDto, 1, 10);
		assertNotNull(settingDto);
	}
	
	@Test
	public void findById() throws Exception {
		String id = "402801635e2cdd31015e2cffa6ab00ed";
		MessageSendSettingDto dto = messageSendSettingApi.findById(id);
		assertNotNull(dto);
	}
	
	@Test
	public void findCountByName() {
		String messageName = "消息推送";
		int count = messageSendSettingApi.findCountByName(messageName);
		assertNotNull(count);
	}
	
	@Rollback(true)
	@Test
	public void updateMessageSend() throws Exception {
		messageDto.setContext("测试修改测试修改");
		int count = messageSendSettingApi.updateMessageSend(messageDto);
		
		assertNotNull(count);
	}
	
	@Rollback(true)
	@Test
	public void addMessageSend() throws Exception {
		messageSendSettingApi.addMessageSend(messageDto);
	}
	
	@Rollback(true)
	@Test
	public void closeMessage() throws Exception {
		MessageSendSettingDto sendDto = new MessageSendSettingDto();
		sendDto.setId(UuidUtils.getUuid());
	    sendDto.setMessageName("单元测试添加");
	    sendDto.setContext("单元测试添加修改");
	    sendDto.setRangeType(0);
	    sendDto.setSendType(0);
	    sendDto.setType((byte) 2);
	    sendDto.setStatus(0);
	    sendDto.setCreateTime(DateUtils.getSysDate());
	    sendDto.setCreateUserId("1");
	    sendDto.setUpdateTime(DateUtils.getSysDate());
	    sendDto.setUpdateUserId("1");
		int count = messageSendSettingApi.closeMessage(sendDto);
		
		assertNotNull(count);
	}
}
