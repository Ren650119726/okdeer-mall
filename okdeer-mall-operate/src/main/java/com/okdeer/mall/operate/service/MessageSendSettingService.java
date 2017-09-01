/** 
 *@Project: okdeer-mall-operate 
 *@Author: xuzq01
 *@Date: 2017年8月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.service;

import java.util.Date;
import java.util.List;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.operate.dto.MessageSendSettingQueryDto;
import com.okdeer.mall.operate.entity.MessageSendSetting;

/**
 * ClassName: MessageSendSettingService 
 * @Description: app消息推送service
 * @author xuzq01
 * @date 2017年8月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface MessageSendSettingService extends IBaseService {

	/**
	 * @Description: 根据条件获取消息推送设置列表
	 * @param paramDto
	 * @param pageNumber
	 * @param pageSize
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月15日
	 */
	PageUtils<MessageSendSetting> findPageList(MessageSendSettingQueryDto paramDto, int pageNumber, int pageSize);

	/**
	 * @Description: 通过消息名称查询消息数量
	 * @param messageName
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月17日
	 */
	int findCountByName(String messageName);

	/**
	 * @Description: 根据消息状态查询消息列表
	 * @param status 消息状态
	 * @param sendTime 发送时间
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月18日
	 */
	List<MessageSendSetting> findMessageListByStatus(int status, Date sendTime);

	/**
	 * @Description: 根据消息设置的发送时间修改消息
	 * @param entity
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月18日
	 */
	int updateSetting(MessageSendSetting entity);

}
