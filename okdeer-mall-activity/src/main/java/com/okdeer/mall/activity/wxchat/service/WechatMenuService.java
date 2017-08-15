
package com.okdeer.mall.activity.wxchat.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.wxchat.entity.WechatMenu;

public interface WechatMenuService extends IBaseService {

	List<WechatMenu> findByList();
}
