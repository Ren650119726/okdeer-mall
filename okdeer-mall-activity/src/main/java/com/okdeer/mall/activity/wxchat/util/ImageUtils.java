
package com.okdeer.mall.activity.wxchat.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * ClassName: ImageUtils 
 * @Description: 图片处理工具类
 * @author zengjizu
 * @date 2017年7月31日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class ImageUtils {

	private ImageUtils() {
	}

	/** 
	 * @Description:小图片贴到大图片形成一张图(合成) 
	 * @author:liuyc 
	 * @time:2016年5月27日 下午5:51:20 
	 */
	public static final void overlapImage(BufferedImage big, BufferedImage small, int startX, int startY) {
		try {
			Graphics2D g = big.createGraphics();
			g.drawImage(small, startX, startY, small.getWidth(), small.getHeight(), null);
			g.dispose();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** 
	 * 传入的图像必须是正方形的 才会 圆形  如果是长方形的比例则会变成椭圆的 
	 * @param url 用户头像地址   
	 * @return 
	 * @throws IOException 
	 */
	public static BufferedImage convertCircular(BufferedImage image) throws IOException {
		// 透明底的图片
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = newImage.createGraphics();
		g.drawImage(image, 0, 0, null);

		RoundRectangle2D round = new RoundRectangle2D.Double(0, 0, image.getWidth(), image.getHeight(),
				image.getWidth(), image.getHeight());
		Area clear = new Area(new Rectangle(0, 0, image.getWidth(), image.getHeight()));
		clear.subtract(new Area(round));
		g.setComposite(AlphaComposite.Clear);
		// 抗锯齿
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.fill(clear);
		g.dispose();
		return newImage;
	}

	/** 
	 * 缩小Image，此方法返回源图像按给定宽度、高度限制下缩放后的图像 
	 * @param inputImage 
	 * @param maxWidth：压缩后宽度 
	 * @param maxHeight：压缩后高度 
	 * @throws java.io.IOException 
	 * return  
	 */
	public static BufferedImage scaleByPercentage(BufferedImage inputImage, int newWidth, int newHeight) {
		try {
			// 获取原始图像透明度类型
			int type = inputImage.getColorModel().getTransparency();
			int width = inputImage.getWidth();
			int height = inputImage.getHeight();
			// 开启抗锯齿
			RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			// 使用高质量压缩
			renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			BufferedImage img = new BufferedImage(newWidth, newHeight, type);
			Graphics2D graphics2d = img.createGraphics();
			graphics2d.setRenderingHints(renderingHints);
			
			graphics2d.drawImage(inputImage, 0, 0, newWidth, newHeight, 0, 0, width, height, null);
			graphics2d.dispose();
			return img;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void drawTextInImg(BufferedImage bimage, String color, String text, int x, int y) {
		// 得到画笔对象
		Graphics g = bimage.getGraphics();
		Font f = new Font("microsoft YaHei", Font.PLAIN, 24);
		Color mycolor = getColor(color);
		g.setColor(mycolor);
		g.setFont(f);
		g.drawString(text, x, y);
		g.dispose();
	}

	// color #2395439
	public static Color getColor(String color) {
		if (color.charAt(0) == '#') {
			color = color.substring(1);
		}
		if (color.length() != 6) {
			return null;
		}
		try {
			int r = Integer.parseInt(color.substring(0, 2), 16);
			int g = Integer.parseInt(color.substring(2, 4), 16);
			int b = Integer.parseInt(color.substring(4), 16);
			return new Color(r, g, b);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}
}
