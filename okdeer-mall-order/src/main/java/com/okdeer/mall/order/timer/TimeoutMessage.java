/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @项目名称: yschome-mall 
 * @文件名称: TimeoutMessage.java 
 * @Date: 2016年5月11日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */ 
package com.okdeer.mall.order.timer;

import java.io.Serializable;

/**
 * 消息对象 
 * @pr yschome-mall
 * @author guocp
 * @date 2016年5月11日 下午3:26:25
 */
public class TimeoutMessage implements Serializable {
	
	/** 序列号 */
	private static final long serialVersionUID = 2938230404834613980L;

	/**
	 * 消息key
	 */
	String key;
	
	/**
	 * 消息发送时间
	 */
	Long sendDate;
	
	
	/**
	 * 构造方法
	 */
	public TimeoutMessage() {
	}
	
	/**
	 * 构造方法
	 */
	public TimeoutMessage(String key,Long sendDate) {
		this.key = key;
		this.sendDate = sendDate;
	}
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the sendDate
	 */
	public Long getSendDate() {
		return sendDate;
	}
	/**
	 * @param sendDate the sendDate to set
	 */
	public void setSendDate(Long sendDate) {
		this.sendDate = sendDate;
	}


	/**
	 * 获取json bytes
	 */
//	public byte[] getJsonBytes() {
//		return JsonMapper.nonEmptyMapper().toJson(this).getBytes(Charsets.UTF_8);
//	}
}
