
package com.okdeer.mall.activity.wxchat.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.rocketmq.common.ThreadFactoryImpl;
import com.google.common.collect.Lists;
import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordService;
import com.okdeer.mall.activity.coupons.service.ActivityDrawPrizeService;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeWeight;
import com.okdeer.mall.activity.wechat.dto.ActivityPosterDrawRecordParamDto;
import com.okdeer.mall.activity.wechat.dto.PosterTakePrizeDto;
import com.okdeer.mall.activity.wxchat.bo.AddMediaResult;
import com.okdeer.mall.activity.wxchat.bo.CreateQrCodeResult;
import com.okdeer.mall.activity.wxchat.bo.DrawResult;
import com.okdeer.mall.activity.wxchat.bo.PosterAddWechatUserRequest;
import com.okdeer.mall.activity.wxchat.bo.TakePrizeResult;
import com.okdeer.mall.activity.wxchat.bo.WechatUserInfo;
import com.okdeer.mall.activity.wxchat.config.WechatConfig;
import com.okdeer.mall.activity.wxchat.entity.ActivityPosterConfig;
import com.okdeer.mall.activity.wxchat.entity.ActivityPosterDrawRecord;
import com.okdeer.mall.activity.wxchat.entity.ActivityPosterShareInfo;
import com.okdeer.mall.activity.wxchat.entity.ActivityPosterWechatUserInfo;
import com.okdeer.mall.activity.wxchat.entity.WechatUser;
import com.okdeer.mall.activity.wxchat.message.ImageWechatMsg;
import com.okdeer.mall.activity.wxchat.message.SubscribeEventWechatEventMsg;
import com.okdeer.mall.activity.wxchat.message.TextWechatMsg;
import com.okdeer.mall.activity.wxchat.message.WechatEventMsg;
import com.okdeer.mall.activity.wxchat.message.WechatMedia;
import com.okdeer.mall.activity.wxchat.service.ActivityPosterConfigService;
import com.okdeer.mall.activity.wxchat.service.ActivityPosterDrawRecordService;
import com.okdeer.mall.activity.wxchat.service.ActivityPosterShareInfoService;
import com.okdeer.mall.activity.wxchat.service.ActivityPosterWechatUserService;
import com.okdeer.mall.activity.wxchat.service.CustomerService;
import com.okdeer.mall.activity.wxchat.service.PosterActivityService;
import com.okdeer.mall.activity.wxchat.service.WechatMenuProcessService;
import com.okdeer.mall.activity.wxchat.service.WechatService;
import com.okdeer.mall.activity.wxchat.service.WechatUserService;
import com.okdeer.mall.activity.wxchat.util.EmojiFilter;
import com.okdeer.mall.activity.wxchat.util.ImageUtils;
import com.okdeer.mall.activity.wxchat.util.WxchatUtils;
import com.okdeer.mall.system.mapper.SysBuyerUserMapper;
import com.okdeer.mcm.entity.BaseResponse;
import com.okdeer.mcm.entity.SmsVO;
import com.okdeer.mcm.service.ISmsService;

import net.sf.json.JSONObject;

@Service("posterActivityService")
public class PosterActivityServiceImpl
		implements WechatMenuProcessService, PosterActivityService, InitializingBean, DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(PosterActivityServiceImpl.class);

	private BlockingQueue<PosterAddWechatUserRequest> posterAddWechatUserRequestQueue;

	@Autowired
	private WechatConfig wechatConfig;

	@Autowired
	private WechatService wechatService;

	@Autowired
	private ActivityPosterWechatUserService activityPosterWechatUserService;

	@Autowired
	private ActivityPosterConfigService activityPosterConfigService;

	@Autowired
	private ActivityPosterShareInfoService activityPosterShareInfoService;

	@Autowired
	private WechatUserService wechatUserService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ActivityDrawPrizeService activityDrawPrizeService;

	@Autowired
	private ActivityPosterDrawRecordService activityPosterDrawRecordService;

	@Value("${operateImagePrefix}")
	private String operateImagePrefix;

	@Autowired
	private SysBuyerUserMapper sysBuyerUserMapper;

	@Autowired
	private ActivityCouponsRecordService activityCouponsRecordService;

	@Reference(version = "1.0.0", check = false)
	private ISmsService smsService;

	@Resource(name="redisLockRegistry")
	private RedisLockRegistry redisLockRegistry;
	
	/**
	 * 消息系统CODE
	 */
	@Value("${mcm.sys.code}")
	private String msgSysCode;

	/**
	 * 消息token
	 */
	@Value("${mcm.sys.token}")
	private String msgToken;

	private static final String[] posterImg = { "posterpic1.png","posterpic2.png"};

	public static final String ACTIVITY_ID = WxchatUtils.ACTIVITY_ID;

	private ExecutorService cachedThreadPool;

	private WechatUserSubscribleProcessSerivce wechatUserSubscribleProcessSerivce;

	private static final String QRSCENE_STR = "qrscene_";

	private ActivityPosterConfig activityPosterConfig;
	
	

	@Override
	public Object process(WechatEventMsg wechatEventMsg) throws MallApiException {
		try {
			TextWechatMsg textWechatMsg = createImageingResponseMsg(wechatEventMsg.getFromUserName(),
					activityPosterConfig);
			customerService.sendMsg(textWechatMsg);
			asynCreatePoster(wechatEventMsg.getFromUserName());
		} catch (Exception e) {
			logger.error("处理请求失败信息出错", e);
		}
		return null;
	}

	private void asynCreatePoster(String openid) {
		cachedThreadPool.execute(() -> {
			try {
				createAndSendPoster(openid);
			} catch (Exception e) {
				logger.error("创建海报出错", e);
			}

		});
	}

	/**
	 * @Description: 生成海报图片并且发送給用户
	 * @param fromUserName
	 * @author zengjizu
	 * @date 2017年8月5日
	 */
	private void createAndSendPoster(String fromUserName) {
		try {
			String mediaId = getMediaId(fromUserName);
			// 生成图片信息返回
			ImageWechatMsg imageWechatMsg = createImageWechatMsg(fromUserName, mediaId);
			customerService.sendMsg(imageWechatMsg);
		} catch (Exception e) {
			logger.error("生成海报图片出错");
		}
	}

	private TextWechatMsg createImageingResponseMsg(String openid, ActivityPosterConfig activityPosterConfig) {
		TextWechatMsg textWechatMsg = new TextWechatMsg();
		textWechatMsg.setFromUserName(wechatConfig.getAccount());
		textWechatMsg.setToUserName(openid);
		textWechatMsg.setContent("尊敬的用户，正在生成您的专属七夕情报，您可以保存情报，分享给好友或朋友圈，每三位好友扫码关注，您就可以领取iPhone 7/鲜花/100元优惠券，每天可领15次！");
		WechatUserInfo wechatUserInfo;
		try {
			wechatUserInfo = wechatService.getUserInfo(openid);
			String content = activityPosterConfig.getCreatePosterTip()
					.replaceAll("#nickname", wechatUserInfo.getNickName())
					.replaceAll("#drawCountLimit", String.valueOf(activityPosterConfig.getDrawCountLimit()));
			textWechatMsg.setContent(content);
		} catch (Exception e) {
			logger.error("获取微信用户信息出错", e);
		}
		return textWechatMsg;
	}

	private String getMediaId(String openid) throws Exception {
		// 查询用户是否已经有海报信息等
		ActivityPosterWechatUserInfo activityPosterWechatUserInfo = activityPosterWechatUserService
				.findByOpenid(openid);
		if (activityPosterWechatUserInfo != null
				&& StringUtils.isNotEmpty(activityPosterWechatUserInfo.getPosterMediaId())
				&& !isExpireForPoster(activityPosterWechatUserInfo.getPosterExpireTime())) {
			return activityPosterWechatUserInfo.getPosterMediaId();
		}

		// 获取微信用户最新信息
		WechatUserInfo wechatUserInfo = wechatService.getUserInfo(openid);
		// 随机一张图片
		Random random = new Random();
		int index = random.nextInt(posterImg.length);
		String posterUrl = operateImagePrefix + posterImg[index];
		// 创建海报图片
		BufferedImage bufferedImage = createPosterPic(posterUrl, wechatUserInfo);
		if (bufferedImage == null) {
			throw new MallApiException("生成海报图片出错!");
		}
		String fileName = posterImg[index];

		byte[] posterImgIs = createPosterInStream(bufferedImage, fileName.substring(fileName.lastIndexOf('.') + 1));
		// 添加图片到微信服务器
		AddMediaResult result = wechatService.addMedia(posterImgIs, "image", fileName);
		if (!result.isSuccess()) {
			throw new MallApiException("上传海报图片出错!");
		}
		String mediaId = result.getMediaId();
		if (activityPosterWechatUserInfo != null) {
			ActivityPosterWechatUserInfo updateActiPosterWechatUser = new ActivityPosterWechatUserInfo();
			updateActiPosterWechatUser.setPosterMediaId(mediaId);
			updateActiPosterWechatUser.setUpdateTime(new Date());
			updateActiPosterWechatUser
					.setPosterExpireTime(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000L - 10000));
			updateActiPosterWechatUser.setOpenid(activityPosterWechatUserInfo.getOpenid());
			activityPosterWechatUserService.update(updateActiPosterWechatUser);
		}
		return mediaId;
	}

	private boolean isExpireForPoster(Date posterExpireTime) {
		return posterExpireTime == null || System.currentTimeMillis() > posterExpireTime.getTime();
	}

	private byte[] createPosterInStream(BufferedImage bufferedImage, String type) throws IOException {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		ImageOutputStream imOut = ImageIO.createImageOutputStream(bs);
		ImageIO.write(bufferedImage, type, imOut);
		return bs.toByteArray();
	}

	private BufferedImage createPosterPic(String posterImgUrl, WechatUserInfo wechatUserInfo) {
		try {
			// 用户头像
			String userInfoHead = wechatUserInfo.getHeadImgUrl();
			// 海报图片
			URL posterPicUrl = new URL(posterImgUrl);
			URL userPicUrl = new URL(userInfoHead);
			// 海报图片
			BufferedImage posterReadImg = ImageIO.read(posterPicUrl);
			// 用户头像图片
			BufferedImage userImg = ImageIO.read(userPicUrl);
			// 将头像改为 160x160的正方形头像
			BufferedImage convertImage = ImageUtils.scaleByPercentage(userImg, 160, 160);
			// 将头像改为圆形
			convertImage = ImageUtils.convertCircular(convertImage);
			// 将用户头像合并到海报中
			ImageUtils.overlapImage(posterReadImg, convertImage, 210, 292);
			// 生成用户二维码分享图片
			BufferedImage qrCodeImg = createUserShareQrcodeImg(wechatUserInfo.getOpenid());
			// 将用户的二维码图片合成到海报中
			ImageUtils.overlapImage(posterReadImg, qrCodeImg, 240, 805);
			// 海报图片添加昵称
			// ImageUtils.drawTextInImg(posterReadImg, "#EEE5DE",
			// wechatUserInfo.getNickName(), 220,
			return posterReadImg;
		} catch (IOException e) {
			logger.error("读取图片出错", e);
		} catch (MallApiException e) {
			logger.error("获取用户的二维码图片出错");
		}
		return null;
	}

	private BufferedImage createUserShareQrcodeImg(String openid) throws MallApiException {
		String sceneStr = openid;
		int expireSeconds = 2592000;
		try {
			CreateQrCodeResult createQrCodeResult = wechatService.createQrCode(sceneStr, expireSeconds);
			if (!createQrCodeResult.isSuccess()) {
				throw new MallApiException("获取用户二维码图片出错，微信返回错误信息：" + createQrCodeResult.getErrMsg());
			}
			URL imgUrl = new URL("https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="
					+ URLEncoder.encode(createQrCodeResult.getTicket(), "UTF-8"));
			BufferedImage image = ImageIO.read(imgUrl);
			// 将图片转换为260x260的
			return ImageUtils.scaleByPercentage(image, 260, 260);
		} catch (Exception e) {
			throw new MallApiException("获取用户二维码图片出错");
		}
	}

	private ImageWechatMsg createImageWechatMsg(String openid, String mediaId) {
		ImageWechatMsg responseWechatMsg = new ImageWechatMsg();
		responseWechatMsg.setFromUserName(wechatConfig.getAccount());
		responseWechatMsg.setToUserName(openid);
		WechatMedia wechatMedia = new WechatMedia();
		List<String> mediaIdList = Lists.newArrayList();
		mediaIdList.add(mediaId);
		wechatMedia.setMediaIdList(mediaIdList);
		responseWechatMsg.setWechatMedia(wechatMedia);
		// 生成海报
		return responseWechatMsg;
	}

	@Override
	public void putPosterAddWechatUserRequest(PosterAddWechatUserRequest posterAddWechatUserRequest) {
		try {
			posterAddWechatUserRequestQueue.put(posterAddWechatUserRequest);
		} catch (InterruptedException e) {
			logger.error("当前线程{}已经中断", Thread.currentThread().getName(), e);
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void destroy() throws Exception {
		posterAddWechatUserRequestQueue.clear();
		cachedThreadPool.shutdown();
		wechatUserSubscribleProcessSerivce.interrupt();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		posterAddWechatUserRequestQueue = new LinkedBlockingQueue<>();
		cachedThreadPool = Executors.newCachedThreadPool(new ThreadFactoryImpl("PosterActivityServiceExecutorThread_"));
		wechatUserSubscribleProcessSerivce = new WechatUserSubscribleProcessSerivce();
		wechatUserSubscribleProcessSerivce.start();
		activityPosterConfig = activityPosterConfigService.findById(ACTIVITY_ID);
		if (activityPosterConfig == null) {
			logger.error("七夕海报活动配置不能为空!");
		}
	}

	class WechatUserSubscribleProcessSerivce extends Thread {

		public WechatUserSubscribleProcessSerivce() {
			super("WechatUserSubscribleProcessSerivceThread");
		}

		@Override
		public void run() {
			while (true) {
				try {
					PosterAddWechatUserRequest posterAddWechatUserRequest = posterAddWechatUserRequestQueue.take();
					cachedThreadPool.execute(() -> {
						doProcessSubscribleRequest(posterAddWechatUserRequest);
					});
				} catch (InterruptedException e) {
					logger.error("当前线程中断", e);
					Thread.currentThread().interrupt();
					break;
				}
			}
			logger.info("{}线程已经结束", Thread.currentThread().getName());
		}
	}

	public void doProcessSubscribleRequest(PosterAddWechatUserRequest posterAddWechatUserRequest) {
		SubscribeEventWechatEventMsg wechatEventMsg = posterAddWechatUserRequest.getSubscribeEventWechatEventMsg();
		WechatUserInfo subscribeUser = wechatUserService.updateUserInfo(wechatEventMsg.getFromUserName());
		if (StringUtils.isNotEmpty(wechatEventMsg.getEventKey())
				&& wechatEventMsg.getEventKey().startsWith(QRSCENE_STR)) {
			// 用户未关注我们的公众号，并且通过好友分享的二维码来关注我们的公众号
			String shareOpenid = wechatEventMsg.getEventKey()
					.substring(wechatEventMsg.getEventKey().indexOf("qrscene_") + QRSCENE_STR.length());
			ActivityPosterShareInfo activityPosterShareInfo = activityPosterShareInfoService
					.findByOpenid(wechatEventMsg.getFromUserName());
			if (activityPosterShareInfo == null) {
				// 如果用户之前已经是别人的推荐好友了，则不处理
				try {
					// 保存活动信息
					saveActivityPosterShareInfo(subscribeUser.getOpenid(), shareOpenid);
					// 获取关注用户的最新信息
					WechatUserInfo wechatUserInfo = wechatService.getUserInfo(shareOpenid);
					// 发送提示信息給关注的用户
					customerService.sendMsg(createSubscribleResponse(subscribeUser, wechatUserInfo));
					// 发送海报图片給关注用户
					createAndSendPoster(subscribeUser.getOpenid());
					// 給分享的好友发送提示信息
					customerService.sendMsg(createFriendTip(subscribeUser, wechatUserInfo));
					// 判断分享人的抽奖资格
					doProcessShareUserQualifica(shareOpenid);
				} catch (Exception e) {
					logger.error("处理用户关注信息出错", e);
				}
			}
		}
	}

	private void doProcessShareUserQualifica(String shareOpenid) throws InterruptedException {
		// 加锁，防止重复的交易号重复执行,出现重复扣款情况
		Lock lock = redisLockRegistry.obtain("WECHAT_USER_"+shareOpenid);
		if (lock.tryLock(10, TimeUnit.SECONDS)) {
			try {
				// 查询分享用户的好友关注数量
				int count = activityPosterShareInfoService.queryCountByShareOpenId(shareOpenid);
				if (count % 3 == 0) {
					// 如果是3的倍数，则更新用户的资格次数
					ActivityPosterWechatUserInfo activityPosterWechatUser = new ActivityPosterWechatUserInfo();
					activityPosterWechatUser.setOpenid(shareOpenid);
					activityPosterWechatUser.setQualificaCount(count / 3);
					try {
						activityPosterWechatUserService.update(activityPosterWechatUser);
					} catch (Exception e) {
						logger.error("更新用户的资格数出错", e);
					}
					customerService.sendMsg(getQucaTip(shareOpenid,count));
				}
			} finally {
				lock.unlock();
			}
		}

	}

	private void saveActivityPosterShareInfo(String openid, String shareOpenid) {
		ActivityPosterShareInfo activityPosterShareInfo = new ActivityPosterShareInfo();
		activityPosterShareInfo.setCreateTime(new Date());
		activityPosterShareInfo.setId(UuidUtils.getUuid());
		activityPosterShareInfo.setOpenid(openid);
		activityPosterShareInfo.setShareOpenid(shareOpenid);
		try {
			activityPosterShareInfoService.add(activityPosterShareInfo);
		} catch (Exception e) {
			logger.error("保存用户与好友的关联关系出错", e);
		}
	}

	private Object createFriendTip(WechatUserInfo subscribeUser, WechatUserInfo wechatUserInfo) {
		TextWechatMsg textWechatMsg = new TextWechatMsg();
		textWechatMsg.setFromUserName(wechatConfig.getAccount());
		textWechatMsg.setToUserName(wechatUserInfo.getOpenid());
		String content = activityPosterConfig.getFriendSubscribeTip().replaceAll("#frientname",
				subscribeUser.getNickName());
		textWechatMsg.setContent(content);
		return textWechatMsg;
	}

	private Object getQucaTip(String openid, int count) {
		TextWechatMsg textWechatMsg = new TextWechatMsg();
		textWechatMsg.setFromUserName(wechatConfig.getAccount());
		textWechatMsg.setToUserName(openid);
		String content = activityPosterConfig.getGetQualificaTip().replaceAll("#count", String.valueOf(count))
				.replaceAll("#N", String.valueOf(count / 3));
		textWechatMsg.setContent(content);
		return textWechatMsg;
	}

	private TextWechatMsg createSubscribleResponse(WechatUserInfo subscribeUser, WechatUserInfo wechatUserInfo) {
		TextWechatMsg textWechatMsg = new TextWechatMsg();
		textWechatMsg.setFromUserName(wechatConfig.getAccount());
		textWechatMsg.setToUserName(subscribeUser.getOpenid());
		String content = activityPosterConfig.getSubscribeWechatTip()
				.replaceAll("#nickname", subscribeUser.getNickName())
				.replaceAll("#friendname", wechatUserInfo.getNickName());
		textWechatMsg.setContent(content);
		return textWechatMsg;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public DrawResult draw(String openid, String activityId) throws MallApiException {
		DrawResult drawResult = new DrawResult();

		try {
			ActivityPosterWechatUserInfo activityPosterWechatUserInfo = activityPosterWechatUserService
					.findByOpenid(openid);
			// 判断是否有资格次数
			if (activityPosterWechatUserInfo.getQualificaCount().intValue()
					- activityPosterWechatUserInfo.getUsedQualificaCount().intValue() <= 0) {
				drawResult.setCode(101);
				drawResult.setMsg("您的资格次数不够啦！");
				return drawResult;
			}
			// 判断是否已经超过今天最多抽奖次数
			ActivityPosterDrawRecordParamDto activityPosterDrawRecordParamDto = new ActivityPosterDrawRecordParamDto();
			activityPosterDrawRecordParamDto.setActivityId(activityId);
			activityPosterDrawRecordParamDto.setOpenid(openid);
			activityPosterDrawRecordParamDto
					.setDrawStartTime(DateUtils.formatDate(DateUtils.getDateStart(new Date()), "yyyy-MM-dd HH:mm:ss"));
			activityPosterDrawRecordParamDto
					.setDrawEndTime(DateUtils.formatDate(DateUtils.getDateEnd(new Date()), "yyyy-MM-dd HH:mm:ss"));

			long count = activityPosterDrawRecordService.findCountByParams(activityPosterDrawRecordParamDto);
			if (count >= activityPosterConfig.getDrawCountLimit()) {
				drawResult.setCode(101);
				drawResult.setMsg("您的抽奖次数已经超过单日限制抽奖次数！");
				return drawResult;
			}

			ActivityPrizeWeight activityPrizeWeight = activityDrawPrizeService.drawByWithckDrawId(activityId);
			if (activityPrizeWeight == null) {
				drawResult.setCode(101);
				drawResult.setMsg("很遗憾，您什么也没有抽中，请再接再励!");
			} else {
				ActivityPosterDrawRecord activityPosterDrawRecord = buildActivityPosterDrawRecord(openid, activityId,
						activityPrizeWeight);
				// 添加领取记录
				activityPosterDrawRecordService.add(activityPosterDrawRecord);
				drawResult.setCode(0);
				drawResult.setMsg("哈哈，您拆倒的奖品是" + activityPrizeWeight.getPrizeName());
				drawResult.setPrizeId(activityPrizeWeight.getId());
				drawResult.setPrizeName(activityPrizeWeight.getPrizeName());
				drawResult.setRecordId(activityPosterDrawRecord.getId());

				if (StringUtils.isNotEmpty(activityPosterWechatUserInfo.getPhoneNo())) {
					// 手机号码已经有了，直接发放
					takePrize(openid, activityPosterDrawRecord.getId(), activityPosterWechatUserInfo.getPhoneNo());
				}
			}

			int result = activityPosterWechatUserService.updateUsedQualificaCount(openid,
					activityPosterWechatUserInfo.getUsedQualificaCount() + 1,
					activityPosterWechatUserInfo.getUsedQualificaCount());
			if (result < 0) {
				throw new MallApiException("操作太快，请重新再试!");
			}

			return drawResult;
		} catch (Exception e) {
			throw new MallApiException(e);
		}
	}

	private ActivityPosterDrawRecord buildActivityPosterDrawRecord(String openid, String activityId,
			ActivityPrizeWeight activityPrizeWeight) {
		ActivityPosterDrawRecord activityPosterDrawRecord = new ActivityPosterDrawRecord();
		activityPosterDrawRecord.setDrawTime(new Date());
		activityPosterDrawRecord.setActivityCollectId(activityPrizeWeight.getActivityCollectId());
		activityPosterDrawRecord.setActivityId(activityId);
		activityPosterDrawRecord.setId(UuidUtils.getUuid());
		activityPosterDrawRecord.setIsTake(0);
		activityPosterDrawRecord.setOpenid(openid);
		activityPosterDrawRecord.setPrizeId(activityPrizeWeight.getId());
		activityPosterDrawRecord.setPrizeName(activityPrizeWeight.getPrizeName());
		WechatUser wechatUser = wechatUserService.findByOpenid(openid);
		if (wechatUser == null) {
			WechatUserInfo wechatUserInfo = wechatUserService.updateUserInfo(openid);
			activityPosterDrawRecord.setNickName(EmojiFilter.filterEmoji(wechatUserInfo.getNickName()));
		} else {
			activityPosterDrawRecord.setNickName(wechatUser.getNickName());
		}
		return activityPosterDrawRecord;
	}

	private void takePrize(String openid, String recordId, String mobile) throws MallApiException {
		PosterTakePrizeDto posterTakePrizeDto = new PosterTakePrizeDto();
		posterTakePrizeDto.setMobile(mobile);
		posterTakePrizeDto.setOpenid(openid);
		posterTakePrizeDto.setRecordId(recordId);
		posterTakePrizeDto.setFirstTake(false);
		TakePrizeResult takePrizeResult = takePrize(posterTakePrizeDto);
		if (takePrizeResult.getCode() != 0) {
			throw new MallApiException(takePrizeResult.getMsg());
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public TakePrizeResult takePrize(PosterTakePrizeDto posterTakePrizeDto) throws MallApiException {
		TakePrizeResult takePrizeResult = new TakePrizeResult();
		try {
			if (posterTakePrizeDto.isFirstTake()) {
				// 第一次领取，记录手机号码
				ActivityPosterWechatUserInfo activityPosterWechatUserInfo = new ActivityPosterWechatUserInfo();
				activityPosterWechatUserInfo.setOpenid(posterTakePrizeDto.getOpenid());
				activityPosterWechatUserInfo.setPhoneNo(posterTakePrizeDto.getMobile());
				activityPosterWechatUserService.update(activityPosterWechatUserInfo);
			}

			ActivityPosterDrawRecord activityPosterDrawRecord = activityPosterDrawRecordService
					.findById(posterTakePrizeDto.getRecordId());
			if (!posterTakePrizeDto.getOpenid().equals(activityPosterDrawRecord.getOpenid())) {
				takePrizeResult.setCode(301);
				takePrizeResult.setMsg("openid不正确");
				return takePrizeResult;
			}
			if (activityPosterDrawRecord.getIsTake() == 1) {
				takePrizeResult.setCode(101);
				takePrizeResult.setMsg("奖品已经领取过了");
				return takePrizeResult;
			}
			activityPosterDrawRecord.setTakeMobile(posterTakePrizeDto.getMobile());
			activityPosterDrawRecord.setTakeTime(new Date());
			activityPosterDrawRecord.setIsTake(1);
			int result = activityPosterDrawRecordService.updateTakeInfo(activityPosterDrawRecord);
			if (result < 1) {
				takePrizeResult.setCode(101);
				takePrizeResult.setMsg("奖品已经领取过了");
				return takePrizeResult;
			}
			if (StringUtils.isNotEmpty(activityPosterDrawRecord.getActivityCollectId())) {
				// 发放代金劵
				List<SysBuyerUser> sysBuyerUserList = sysBuyerUserMapper
						.selectUserByPhone(posterTakePrizeDto.getMobile());

				JSONObject jsonObject = null;
				if (CollectionUtils.isNotEmpty(sysBuyerUserList)) {
					// 当该用户不存在邀请人时，记录该用户邀请人为此活动的邀请人
					SysBuyerUser sysBuyerUser = sysBuyerUserList.get(0);
					jsonObject = activityCouponsRecordService.addRecordsByCollectId(
							activityPosterDrawRecord.getActivityCollectId(), sysBuyerUser.getId());

				} else {
					// 当用户不存在则送到预领劵记录中addRecordsByCollectId
					jsonObject = activityCouponsRecordService.addBeforeRecords(
							activityPosterDrawRecord.getActivityCollectId(), posterTakePrizeDto.getMobile(), null,
							activityPosterDrawRecord.getActivityId());
				}

				if (jsonObject.getInt("code") != 100) {
					throw new MallApiException(jsonObject.getString("msg"));
				}
			}
			// 发送业务短信
			sendMsg(posterTakePrizeDto.getMobile(), activityPosterDrawRecord.getPrizeName());
			takePrizeResult.setCode(0);
			takePrizeResult.setMsg("领取成功");
		} catch (Exception e) {
			logger.error("操作数据出错", e);
			throw new MallApiException(e.getMessage());
		}
		return takePrizeResult;
	}

	/**
	 * @Description: 短信通知用户
	 * @param mobile
	 * @param prizeName
	 * @author zengjizu
	 * @date 2017年8月10日
	 */
	private void sendMsg(String mobile, String prizeName) {
		SmsVO sendVo = new SmsVO();
		sendVo.setId(UuidUtils.getUuid());
		sendVo.setUserId(null);
		sendVo.setSysCode(msgSysCode);
		sendVo.setToken(msgToken);
		String content = "尊敬的用户，您通过“七夕情报”活动获得了" + prizeName
				+ "，请登录友门鹿app使用，APP下载地址：http://update.okdeer.com/ymlstore.html";
		sendVo.setContent(content);
		sendVo.setIsTiming(0);
		sendVo.setMobile(mobile);
		sendVo.setSmsChannelType(3);
		BaseResponse response = smsService.sendSms(sendVo, false);
		if ("0".equals(response.getResult())) {
			logger.info("短信发送成功............");
		} else {
			logger.info("短信发送失败,失败原因：" + response.getMessage());
		}
	}

}
