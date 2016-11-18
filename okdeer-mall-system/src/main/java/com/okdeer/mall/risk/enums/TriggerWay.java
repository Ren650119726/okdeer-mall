/** 
 *@Project: okdeer-mall-system 
 *@Author: guocp
 *@Date: 2016年11月17日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.risk.enums;

import com.okdeer.base.common.enums.BaseEnum;

/**
 * ClassName: TriggerWay 
 * @Description: 触发方式枚举
 * @author guocp
 * @date 2016年11月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public enum TriggerWay implements BaseEnum<TriggerWay, Integer> {

	NOTICE(0, "提醒"), 
	FORBID(1, "禁止下单");

	/**
	 * 编码
	 */
	private Integer code;

	/**
	 * 描述
	 */
	private String desc;

	private TriggerWay(Integer code, String desc) {
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
