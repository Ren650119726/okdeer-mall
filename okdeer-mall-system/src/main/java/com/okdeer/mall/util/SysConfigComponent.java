package com.okdeer.mall.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ClassName: PicPrefixComponent 
 * @Description: 系统配置组件。将系统配置的相关属性存放到公共地方
 * @author maojj
 * @date 2017年10月16日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.6.3 		2017年10月16日		maojj
 */
@Component
public class SysConfigComponent {

	/**
	 * 我的头像图片前缀
	 */
	@Value("${myinfoImagePrefix}")
	private String myinfoImagePrefix;

	/**
	 * 店铺商品图片前缀
	 */
	@Value("${storeImagePrefix}")
	private String storeImagePrefix;

	@Value("${group.share.link}")
	private String groupShareLink;

	public String getMyinfoImagePrefix() {
		return myinfoImagePrefix;
	}

	public String getStoreImagePrefix() {
		return storeImagePrefix;
	}

	public String getGroupShareLink() {
		return groupShareLink;
	}
}
