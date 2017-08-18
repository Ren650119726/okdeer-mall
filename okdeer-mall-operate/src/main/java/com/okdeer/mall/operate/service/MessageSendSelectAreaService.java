/** 
 *@Project: okdeer-mall-operate 
 *@Author: xuzq01
 *@Date: 2017年8月17日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.operate.entity.MessageSendSelectArea;

/**
 * ClassName: MessageSendSelectAreaService 
 * @Description: app消息推送关联推送地区城市表service
 * @author xuzq01
 * @date 2017年8月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface MessageSendSelectAreaService extends IBaseService {

	/**
	 * @Description: 通过消息id删除关联城市信息
	 * @param id   
	 * @author xuzq01
	 * @date 2017年8月17日
	 */
	void deleteByMessageId(String messageId);

	/**
	 * @Description: 通过消息id查询关联的城市列表
	 * @param id   
	 * @author xuzq01
	 * @return 
	 * @date 2017年8月18日
	 */
	List<MessageSendSelectArea> findListByMessageId(String messageId);

}
