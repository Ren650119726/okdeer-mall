
package com.okdeer.mall.activity.wxchat.api.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.wechat.dto.WechatPassiveReplyDto;
import com.okdeer.mall.activity.wechat.dto.WechatPassiveReplyParamDto;
import com.okdeer.mall.activity.wechat.service.WechatPassiveReplyApi;
import com.okdeer.mall.activity.wxchat.entity.WechatPassiveReply;
import com.okdeer.mall.activity.wxchat.service.WechatPassiveReplyService;

@Service(version = "1.0.0")
public class WechatPassiveReplyApiImpl implements WechatPassiveReplyApi {

	@Autowired
	private WechatPassiveReplyService wechatPassiveReplyService;

	@Override
	public PageUtils<WechatPassiveReplyDto> findPageList(WechatPassiveReplyParamDto wechatPassiveReplyParamDto,
			int pageNum, int pageSize) {
		
		return wechatPassiveReplyService.findPageList(wechatPassiveReplyParamDto,pageNum,pageSize).toBean(WechatPassiveReplyDto.class);
	}

	@Override
	public void add(WechatPassiveReplyDto wechatPassiveReplyDto) throws MallApiException {
		WechatPassiveReply wechatPassiveReply = BeanMapper.map(wechatPassiveReplyDto, WechatPassiveReply.class);
		wechatPassiveReply.setId(UuidUtils.getUuid());
		wechatPassiveReply.setCreateTime(new Date());
		wechatPassiveReply.setDisabled(Disabled.valid);
		try {
			wechatPassiveReplyService.add(wechatPassiveReply);
		} catch (Exception e) {
			throw new MallApiException("添加失败", e);
		}
	}

	@Override
	public void edit(WechatPassiveReplyDto wechatPassiveReplyDto) throws MallApiException {
		WechatPassiveReply wechatPassiveReply = BeanMapper.map(wechatPassiveReplyDto, WechatPassiveReply.class);
		wechatPassiveReply.setUpdateTime(new Date());
		try {
			wechatPassiveReplyService.update(wechatPassiveReply);
		} catch (Exception e) {
			throw new MallApiException("修改失败", e);
		}
	}

	@Override
	public WechatPassiveReplyDto findById(String id) throws MallApiException {
		WechatPassiveReply wechatPassiveReply;
		try {
			wechatPassiveReply = wechatPassiveReplyService.findById(id);
		} catch (Exception e) {
			throw new MallApiException(e);
		}
		return BeanMapper.map(wechatPassiveReply, WechatPassiveReplyDto.class);
	}

}
