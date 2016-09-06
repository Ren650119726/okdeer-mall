/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @项目名称: yschome-mall 
 * @文件名称: OrderTimeoutConfigur.java 
 * @Date: 2016年5月11日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */ 
package com.okdeer.mall.order.timer;

import org.springframework.beans.factory.annotation.Value;

/**
 * 订单超时配置
 * @pr yschome-mall
 * @author guocp
 * @date 2016年5月11日 下午2:41:41
 */
public class OrderTimeoutConfigurer {
	
	@Value("${pay_timeout}")
	public Long pay_timeout = 30 * 60L;
}
