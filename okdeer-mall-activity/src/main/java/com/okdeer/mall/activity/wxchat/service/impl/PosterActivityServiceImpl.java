
package com.okdeer.mall.activity.wxchat.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.wxchat.bo.AddMediaResult;
import com.okdeer.mall.activity.wxchat.bo.WechatUserInfo;
import com.okdeer.mall.activity.wxchat.config.WechatConfig;
import com.okdeer.mall.activity.wxchat.message.ImageWechatMsg;
import com.okdeer.mall.activity.wxchat.message.WechatEventMsg;
import com.okdeer.mall.activity.wxchat.message.WechatMedia;
import com.okdeer.mall.activity.wxchat.service.PosterActivityService;
import com.okdeer.mall.activity.wxchat.service.WechatMenuProcessService;
import com.okdeer.mall.activity.wxchat.service.WechatService;
import com.okdeer.mall.activity.wxchat.service.WechatUserService;
import com.okdeer.mall.activity.wxchat.util.ImageUtils;

@Service("posterActivityService")
public class PosterActivityServiceImpl implements WechatMenuProcessService, PosterActivityService {

	private static final Logger logger = LoggerFactory.getLogger(PosterActivityServiceImpl.class);

	@Autowired
	private WechatUserService wechatUserService;

	@Autowired
	private WechatConfig wechatConfig;

	@Autowired
	private WechatService wechatService;

	@Value("${operateImagePrefix}")
	private String operateImagePrefix;

	private static final String[] posterImg = { "posterpic1.png" };

	@Override
	public Object process(WechatEventMsg wechatEventMsg) throws MallApiException {
		// 获取微信用户最新信息
		try {
			WechatUserInfo wechatUserInfo = wechatService.getUserInfo(wechatEventMsg.getFromUserName());
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

			byte[] posterImgIs = createPosterInStream(bufferedImage,
					fileName.substring(fileName.lastIndexOf('.') + 1));
			// 添加图片到微信服务器
			AddMediaResult result = wechatService.addMedia(posterImgIs, "image", fileName);
			if (!result.isSuccess()) {
				throw new MallApiException("上传海报图片出错!");
			}
			String mediaId = result.getMediaId();
			// 生成海报
			return createImageWechatMsg(wechatEventMsg.getFromUserName(), mediaId);
		} catch (Exception e) {
			logger.error("处理请求失败信息出错", e);
		}
		return null;
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
			BufferedImage posterImg = ImageIO.read(posterPicUrl);
			// 用户头像图片
			BufferedImage userImg = ImageIO.read(userPicUrl);
			// 将头像改为 160x160的正方形头像
			BufferedImage convertImage = ImageUtils.scaleByPercentage(userImg, 160, 160);
			// 将头像改为圆形
			convertImage = ImageUtils.convertCircular(convertImage);
			// 将用户头像合并到海报中
			BufferedImage newImg = ImageUtils.overlapImage(posterImg, convertImage, 210, 292);
			// 海报图片添加昵称
			ImageUtils.drawTextInImg(newImg, "#EEE5DE", wechatUserInfo.getNickName(), 210, 292 + convertImage.getHeight() + 20);
			return newImg;
		} catch (IOException e) {
			logger.error("读取图片出错", e);
		}
		return null;
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

	public static void main(String[] args) {

		try {
			// 用户头像
			String userInfoHead = "http://wx.qlogo.cn/mmopen/r48cSSlr7jiaX5qmicB2Ru90YgEpysNWlicYsDUfyphHKL9iczF6hMRCfDjjIWtLqBFckDwHXOCbMFBEo2V8XOWFZdOUJXg1ph3S/0";
			// 海报图片
			URL posterPicUrl = new URL("http://7xs69a.com1.z0.glb.clouddn.com/posterpic1.png");
			URL userPicUrl = new URL(userInfoHead);
			BufferedImage posterImg = ImageIO.read(posterPicUrl);
			BufferedImage userImg = ImageIO.read(userPicUrl);
			long l1 = System.currentTimeMillis();
			BufferedImage convertImage = ImageUtils.scaleByPercentage(userImg, 160, 160);
			convertImage = ImageUtils.convertCircular(convertImage);
			BufferedImage newImg = ImageUtils.overlapImage(posterImg, convertImage, 210, 292);
			ImageUtils.drawTextInImg(newImg, "#EEE5DE", "三届散仙", 210, 292 + convertImage.getHeight() + 20);
			long l2 = System.currentTimeMillis();
			System.out.println(l2 - l1);
			ImageIO.write(newImg, "png", new File("E:\\yschome\\11.png"));

		} catch (IOException e) {
			logger.error("读取图片出错", e);
		}

	}

}
