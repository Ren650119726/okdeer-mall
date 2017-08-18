/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * MessageSendSelectAreaMapper.java
 * @Date 2017-08-15 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.operate.entity.MessageSendSelectArea;

public interface MessageSendSelectAreaMapper extends IBaseMapper {

	/**
	 * @Description: 修改消息时删除原来消息关联城市
	 * @param messageId   
	 * @author xuzq01
	 * @date 2017年8月18日
	 */
	void deleteByMessageId(String messageId);

	/**
	 * @Description: 通过消息id查询城市列表
	 * @param messageId
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月18日
	 */
	List<MessageSendSelectArea> findListByMessageId(String messageId);


}