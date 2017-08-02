
package com.okdeer.mall.activity.wxchat.api.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.mall.activity.wechat.dto.WechatMenuDto;
import com.okdeer.mall.activity.wechat.service.WechatMenuApi;
import com.okdeer.mall.activity.wxchat.entity.WechatMenu;
import com.okdeer.mall.activity.wxchat.service.WechatMenuService;
import com.okdeer.mall.activity.wxchat.service.WechatService;

@Service(version = "1.0.0")
public class WechatMenuApiImpl implements WechatMenuApi {

	@Autowired
	private WechatMenuService wechatMenuService;

	@Autowired
	private WechatService wechatService;

	@Override
	public List<WechatMenuDto> findList() {
		return BeanMapper.mapList(wechatMenuService.findByList(), WechatMenuDto.class);
	}

	@Override
	public void add(WechatMenuDto wechatMenuDto) throws Exception {
		wechatMenuDto.setCreateTime(new Date());
		WechatMenu wechatMenu = BeanMapper.map(wechatMenuDto, WechatMenu.class);
		wechatMenuService.add(wechatMenu);
	}

	@Override
	public void update(WechatMenuDto wechatMenuDto) throws Exception {
		wechatMenuDto.setUpdateTime(new Date());
		WechatMenu wechatMenu = BeanMapper.map(wechatMenuDto, WechatMenu.class);
		wechatMenuService.update(wechatMenu);
	}

	@Override
	public void delete(String id) throws Exception {
		wechatMenuService.delete(id);
	}

	@Override
	public void synToWxserver() throws Exception {
		List<WechatMenu> list = wechatMenuService.findByList();
		if (CollectionUtils.isEmpty(list)) {
			throw new Exception("当前没有设置任何菜单");
		}
		Map<String, Object> menu = Maps.newHashMap();
		List<Map<String, Object>> firstThirdMenuList = Lists.newArrayList();
		Map<String, List<Map<String, Object>>> secondMenuMap = Maps.newHashMap();

		for (WechatMenu wechatMenu : list) {
			Map<String, Object> wechatMenuMap = Maps.newHashMap();
			wechatMenuMap.put("type", wechatMenu.getType().name().toLowerCase());
			wechatMenuMap.put("name", wechatMenu.getButtonName());
			wechatMenuMap.put("key", wechatMenu.getButtonKey());
			wechatMenuMap.put("url", wechatMenu.getUrl());
			wechatMenuMap.put("media_id", wechatMenu.getMediaId());

			if (StringUtils.isNotEmpty(wechatMenu.getParentId())) {
				List<Map<String, Object>> sencMenuList = secondMenuMap.get(wechatMenu.getParentId());
				if (sencMenuList == null) {
					sencMenuList = Lists.newArrayList();
					secondMenuMap.put(wechatMenu.getParentId(), sencMenuList);
				}
			}
			if (wechatMenu.getLevelType() == 1) {
				// 一级菜单
				firstThirdMenuList.add(wechatMenuMap);
				wechatMenuMap.put("sub_button", secondMenuMap.get(wechatMenu.getId()));
			} else if (wechatMenu.getLevelType() == 2) {
				secondMenuMap.get(wechatMenu.getParentId()).add(wechatMenuMap);
			}
		}
		menu.put("button", menu);
		String requestJson = JsonMapper.nonEmptyMapper().toJson(menu);
		wechatService.createMenu(requestJson);
	}

	@Override
	public void sysFromWxServer() throws Exception {

	}

}
