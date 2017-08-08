
package com.okdeer.mall.activity.wxchat.service;

import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.wechat.dto.PosterTakePrizeDto;
import com.okdeer.mall.activity.wxchat.bo.DrawResult;
import com.okdeer.mall.activity.wxchat.bo.PosterAddWechatUserRequest;
import com.okdeer.mall.activity.wxchat.bo.TakePrizeResult;

public interface PosterActivityService {

	/**
	 * @Description: 添加微信用户请求
	 * @param posterAddWechatUserRequest
	 * @author zengjizu
	 * @date 2017年8月4日
	 */
	void putPosterAddWechatUserRequest(PosterAddWechatUserRequest posterAddWechatUserRequest);

	/**
	 * @Description: 抽奖
	 * @param openid
	 * @param activityId
	 * @return
	 * @author zengjizu
	 * @date 2017年8月7日
	 */
	DrawResult draw(String openid, String activityId) throws MallApiException;

	TakePrizeResult takePrize(PosterTakePrizeDto posterTakePrizeDto) throws MallApiException;

}
