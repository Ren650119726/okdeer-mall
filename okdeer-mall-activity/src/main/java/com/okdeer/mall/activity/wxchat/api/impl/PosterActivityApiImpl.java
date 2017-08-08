
package com.okdeer.mall.activity.wxchat.api.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.wechat.dto.ActivityPosterDrawRecordDto;
import com.okdeer.mall.activity.wechat.dto.ActivityPosterDrawRecordParamDto;
import com.okdeer.mall.activity.wechat.dto.ActivityPosterWechatUserDto;
import com.okdeer.mall.activity.wechat.dto.DrawResultDto;
import com.okdeer.mall.activity.wechat.dto.PosterTakePrizeDto;
import com.okdeer.mall.activity.wechat.service.PosterActivityApi;
import com.okdeer.mall.activity.wxchat.bo.DrawResult;
import com.okdeer.mall.activity.wxchat.bo.TakePrizeResult;
import com.okdeer.mall.activity.wxchat.entity.ActivityPosterConfig;
import com.okdeer.mall.activity.wxchat.entity.ActivityPosterDrawRecord;
import com.okdeer.mall.activity.wxchat.entity.ActivityPosterWechatUserInfo;
import com.okdeer.mall.activity.wxchat.service.ActivityPosterConfigService;
import com.okdeer.mall.activity.wxchat.service.ActivityPosterDrawRecordService;
import com.okdeer.mall.activity.wxchat.service.ActivityPosterWechatUserService;
import com.okdeer.mall.activity.wxchat.service.PosterActivityService;
import com.okdeer.mall.activity.wxchat.util.WxchatUtils;

@Service(version = "1.0.0")
public class PosterActivityApiImpl implements PosterActivityApi {

	@Autowired
	private ActivityPosterWechatUserService activityPosterWechatUserService;

	@Autowired
	private ActivityPosterDrawRecordService activityPosterDrawRecordService;

	@Autowired
	private ActivityPosterConfigService activityPosterConfigService;

	@Autowired
	private PosterActivityService posterActivityService;

	@Override
	public ActivityPosterWechatUserDto findByOpenid(String openid, String activityId) throws MallApiException {
		ActivityPosterWechatUserInfo activityPosterWechatUserInfo = activityPosterWechatUserService
				.findByOpenid(openid);
		ActivityPosterWechatUserDto activityPosterWechatUserDto = BeanMapper.map(activityPosterWechatUserInfo,
				ActivityPosterWechatUserDto.class);
		ActivityPosterDrawRecordParamDto activityPosterDrawRecordParamDto = new ActivityPosterDrawRecordParamDto();
		activityPosterDrawRecordParamDto.setActivityId(activityId);
		activityPosterDrawRecordParamDto.setOpenid(openid);
		activityPosterDrawRecordParamDto.setIsTake(0);
		activityPosterDrawRecordParamDto
				.setDrawStartTime(DateUtils.formatDate(DateUtils.getDateStart(new Date()), "yyyy-MM-dd HH:mm:ss"));
		activityPosterDrawRecordParamDto
				.setDrawEndTime(DateUtils.formatDate(DateUtils.getDateEnd(new Date()), "yyyy-MM-dd HH:mm:ss"));
		try {
			ActivityPosterConfig activityPosterConfig = activityPosterConfigService.findById(WxchatUtils.ACTIVITY_ID);
			int count = (int) activityPosterDrawRecordService.findCountByParams(activityPosterDrawRecordParamDto);
			activityPosterWechatUserDto.setTotaySurplusDrawCount(activityPosterConfig.getDrawCountLimit() - count);
			return activityPosterWechatUserDto;
		} catch (Exception e) {
			throw new MallApiException(e);
		}
	}

	@Override
	public List<ActivityPosterDrawRecordDto> findByParams(
			ActivityPosterDrawRecordParamDto activityPosterDrawRecordParamDto) {
		List<ActivityPosterDrawRecord> list = activityPosterDrawRecordService
				.findByParams(activityPosterDrawRecordParamDto);
		return BeanMapper.mapList(list, ActivityPosterDrawRecordDto.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public PageUtils<ActivityPosterDrawRecordDto> findByParams(
			ActivityPosterDrawRecordParamDto activityPosterDrawRecordParamDto, int pageNum, int pageSize) {
		return activityPosterDrawRecordService.findByParams(activityPosterDrawRecordParamDto, pageNum, pageSize)
				.toBean(ActivityPosterDrawRecordDto.class);
	}

	@Override
	public DrawResultDto draw(String openid, String activityId) throws MallApiException {
		DrawResult drawResult = posterActivityService.draw(openid, activityId);
		return BeanMapper.map(drawResult, DrawResultDto.class);
	}

	@Override
	public void takePrize(PosterTakePrizeDto posterTakePrizeDto) throws MallApiException {
		TakePrizeResult takePrizeResult = posterActivityService.takePrize(posterTakePrizeDto);
		if (takePrizeResult.getCode() != 0) {
			throw new MallApiException(takePrizeResult.getMsg());
		}
	}

	@Override
	public ActivityPosterDrawRecordDto findDrawRecordById(String id) throws MallApiException {

		try {
			ActivityPosterDrawRecord activityPosterDrawRecord = activityPosterDrawRecordService.findById(id);
			return BeanMapper.map(activityPosterDrawRecord, ActivityPosterDrawRecordDto.class);
		} catch (Exception e) {
			throw new MallApiException(e);
		}

	}

}
