
package com.okdeer.mall.activity.wxchat.enums;

import org.apache.commons.lang3.StringUtils;

public enum MsgTypeEnum {

	EVENT("事件消息"), TEXT("文本消息"), IMAGE("图片消息"), VOICE("语音消息"), VIDEO("视频消息"), SHORTVIDEO("小视频消息"), LOCATION(
			"地理位置消息"), LINK("链接消息");

	private String value;

	MsgTypeEnum(String value) {
		this.value = value;
	}

	public String getName() {
		return this.name();
	}

	public String getValue() {
		return this.value;
	}

	/**
	 * 根据值取枚举
	 *
	 * @param value 枚举值
	 * @return 枚举对象
	 */
	public static MsgTypeEnum enumValueOf(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		for (MsgTypeEnum msgType : values()) {
			if (value.equalsIgnoreCase(msgType.getValue())) {
				return msgType;
			}
		}
		return null;
	}
	
	/**
	 * 根据值取枚举
	 *
	 * @param value 枚举值
	 * @return 枚举对象
	 */
	public static MsgTypeEnum enumNameOf(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}
		for (MsgTypeEnum msgType : values()) {
			if (name.equalsIgnoreCase(msgType.getName())) {
				return msgType;
			}
		}
		return null;
	}

	/**
	 * 根据值取枚举
	 *
	 * @param ordinal 枚举值
	 * @return 枚举对象
	 */
	public static MsgTypeEnum enumValueOf(int ordinal) {
		if (ordinal < 0) {
			return null;
		}
		for (MsgTypeEnum msgType : values()) {
			if (ordinal == msgType.ordinal()) {
				return msgType;
			}
		}
		return null;
	}
}
