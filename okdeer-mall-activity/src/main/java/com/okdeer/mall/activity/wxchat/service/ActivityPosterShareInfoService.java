
package com.okdeer.mall.activity.wxchat.service;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.wxchat.entity.ActivityPosterShareInfo;

public interface ActivityPosterShareInfoService extends IBaseService {
	
	ActivityPosterShareInfo findByOpenid(String openid);
}
