
package com.okdeer.mall.activity.wxchat.service;

import java.util.List;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.wechat.dto.WechatPassiveReplyParamDto;
import com.okdeer.mall.activity.wxchat.entity.WechatPassiveReply;

public interface WechatPassiveReplyService extends IBaseService {
	
	/**
	 * @Description: 查询分页列表
	 * @param wechatPassiveReplyParamDto
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @author zengjizu
	 * @date 2017年9月25日
	 */
	PageUtils<WechatPassiveReply> findPageList(WechatPassiveReplyParamDto wechatPassiveReplyParamDto, int pageNum,
			int pageSize);
	
	/**
	 * @Description: 查询列表
	 * @param wechatPassiveReplyParamDto
	 * @return
	 * @author zengjizu
	 * @date 2017年9月25日
	 */
	List<WechatPassiveReply> findList(WechatPassiveReplyParamDto wechatPassiveReplyParamDto);
	

}
