/** 
 *@Project: okdeer-mall-system 
 *@Author: guocp
 *@Date: 2016年11月17日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.risk.enums;

import com.okdeer.base.common.enums.BaseEnum;

/**
 * ClassName: PayAccountType 
 * @Description: 支付类型枚举
 * @author guocp
 * @date 2016年11月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public enum PayAccountType implements BaseEnum<PayAccountType, Integer> {

	ALIPAY(0, "支付宝"), 
	WECHAT(1, "微信");

	/**
	 * 编码
	 */
	private Integer code;

	/**
	 * 描述
	 */
	private String desc;

	private PayAccountType(Integer code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	@Override
	public Integer getCode() {
		return code;
	}

	@Override
	public String getDesc() {
		return desc;
	}

}
