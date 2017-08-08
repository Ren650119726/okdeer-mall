
package com.okdeer.mall.activity.wxchat.service;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.wxchat.entity.ActivityPosterWechatUserInfo;

public interface ActivityPosterWechatUserService extends IBaseService {

	ActivityPosterWechatUserInfo findByOpenid(String openid);

	int updateUsedQualificaCount(String openid,int usedQualificaCount, int conditionUsedQualificaCount);

}
