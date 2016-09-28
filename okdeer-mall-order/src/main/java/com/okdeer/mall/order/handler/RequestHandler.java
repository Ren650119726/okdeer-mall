package com.okdeer.mall.order.handler;

import com.okdeer.mall.common.vo.Request;
import com.okdeer.mall.common.vo.Response;
/**
 * ClassName: ServOrderProcessHandler 
 * @Description: 服务栏目处理操作接口
 * @author maojj
 * @date 2016年9月21日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *	   	重构1.1			2016-09-21			maojj			服务栏目处理操作接口
 */
public interface RequestHandler<Q,R> {

	/**
	 * @Description: 处理流程
	 * @param req 服务订单请求对象
	 * @param respDto 服务订单响应对象
	 * @throws Exception 订单异常
	 * @author maojj
	 * @date 2016年9月21日
	 */
	void process(Request<Q> req,Response<R> resp) throws Exception;
}
