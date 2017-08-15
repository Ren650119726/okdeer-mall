/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * wechatUserMapper.java
 * @Date 2017-08-01 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.wxchat.mapper;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.wxchat.entity.WechatUser;

public interface WechatUserMapper extends IBaseMapper {

	WechatUser findByOpenid(String openid);

}