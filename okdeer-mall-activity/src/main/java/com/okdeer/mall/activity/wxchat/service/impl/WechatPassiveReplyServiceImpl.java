
package com.okdeer.mall.activity.wxchat.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.wechat.dto.WechatPassiveReplyParamDto;
import com.okdeer.mall.activity.wxchat.entity.WechatPassiveReply;
import com.okdeer.mall.activity.wxchat.mapper.WechatPassiveReplyMapper;
import com.okdeer.mall.activity.wxchat.service.WechatPassiveReplyService;

@Service
public class WechatPassiveReplyServiceImpl extends BaseServiceImpl implements WechatPassiveReplyService {

	private WechatPassiveReplyMapper wechatPassiveReplyMapper;

	
	@Override
	public IBaseMapper getBaseMapper() {
		return wechatPassiveReplyMapper;
	}


	@Override
	public PageUtils<WechatPassiveReply> findPageList(WechatPassiveReplyParamDto wechatPassiveReplyParamDto,
			int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize, true);
		List<WechatPassiveReply> list = wechatPassiveReplyMapper.findList(wechatPassiveReplyParamDto);
		return new PageUtils<>(list);
	}


	@Override
	public List<WechatPassiveReply> findList(WechatPassiveReplyParamDto wechatPassiveReplyParamDto) {
		return wechatPassiveReplyMapper.findList(wechatPassiveReplyParamDto);
	}
	
	
	
}
