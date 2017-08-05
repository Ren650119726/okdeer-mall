
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

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.common.ThreadFactoryImpl;
import com.google.common.collect.Lists;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.wxchat.bo.AddMediaResult;
import com.okdeer.mall.activity.wxchat.bo.CreateQrCodeResult;
import com.okdeer.mall.activity.wxchat.bo.PosterAddWechatUserRequest;
import com.okdeer.mall.activity.wxchat.bo.WechatUserInfo;
import com.okdeer.mall.activity.wxchat.config.WechatConfig;
import com.okdeer.mall.activity.wxchat.entity.ActivityPosterConfig;
import com.okdeer.mall.activity.wxchat.entity.ActivityPosterWechatUserInfo;
import com.okdeer.mall.activity.wxchat.message.ImageWechatMsg;
import com.okdeer.mall.activity.wxchat.message.TextWechatMsg;
import com.okdeer.mall.activity.wxchat.message.WechatEventMsg;
import com.okdeer.mall.activity.wxchat.message.WechatMedia;
import com.okdeer.mall.activity.wxchat.service.ActivityPosterConfigService;
import com.okdeer.mall.activity.wxchat.service.ActivityPosterWechatUserService;
import com.okdeer.mall.activity.wxchat.service.CustomerService;
import com.okdeer.mall.activity.wxchat.service.PosterActivityService;
import com.okdeer.mall.activity.wxchat.service.WechatMenuProcessService;
import com.okdeer.mall.activity.wxchat.service.WechatService;
import com.okdeer.mall.activity.wxchat.util.ImageUtils;

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
	private CustomerService customerService;

	@Value("${operateImagePrefix}")
	private String operateImagePrefix;

	private static final String[] posterImg = { "posterpic1.png" };

	private static final String ACTIVITY_ID = "1";

	private ExecutorService cachedThreadPool;

	@Override
	public Object process(WechatEventMsg wechatEventMsg) throws MallApiException {
		try {
			asynCreatePoster(wechatEventMsg.getFromUserName());
			ActivityPosterConfig activityPosterConfig = activityPosterConfigService.findById(ACTIVITY_ID);
			if (activityPosterConfig != null) {
				return createImageingResponseMsg(wechatEventMsg.getFromUserName(), activityPosterConfig);
			}
		} catch (Exception e) {
			logger.error("处理请求失败信息出错", e);
		}
		return null;
	}

	private void asynCreatePoster(String openid) {
		cachedThreadPool.execute(() -> {
			try {
				//让线程先睡眠2秒钟，避免海报生成了，提示语还没发出去
				Thread.sleep(2000);
				createAndSendPoster(openid);
			} catch (Exception e) {
				logger.error("创建海报出错",e);
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
			ImageUtils.drawTextInImg(posterReadImg, "#EEE5DE", wechatUserInfo.getNickName(), 210,
					292 + convertImage.getHeight() + 20);
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
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		posterAddWechatUserRequestQueue = new LinkedBlockingQueue<>();
		cachedThreadPool = Executors.newCachedThreadPool(new ThreadFactoryImpl("PosterActivityServiceExecutorThread_"));
	}

}
