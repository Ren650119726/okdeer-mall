
package com.okdeer.mall.activity.wxchat.bo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.okdeer.base.common.utils.StringUtils;

public class WechatBaseResult implements Serializable {

	/**
	 * 返回码
	 */
	@JsonProperty("errcode")
	private String errCode;

	/**
	 * 返回信息
	 */
	@JsonProperty("errmsg")
	private String errMsg;

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public boolean isSuccess(){
		return StringUtils.isEmpty(errCode) || "0".equals(errCode);
	}
}
