/** 
 *@Project: okdeer-mall-system 
 *@Author: guocp
 *@Date: 2016年11月17日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.risk.enums;

import com.okdeer.base.common.enums.BaseEnum;

/**
 * ClassName: TriggerType 
 * @Description: 触发类型
 * @author guocp
 * @date 2016年11月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public enum TriggerType implements BaseEnum<TriggerType, Integer> {

	COUNT_LIMIT(0, "次数上限"), 
	TOTAL_LIMIT(1, "额度上限"), 
	TEL_LIMIT(2, "充值号码上限"), 
	DEVICE_LIMIT(3, "设备登入用户上限");

	/**
	 * 编码
	 */
	private Integer code;

	/**
	 * 描述
	 */
	private String desc;

	private TriggerType(Integer code, String desc) {
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
