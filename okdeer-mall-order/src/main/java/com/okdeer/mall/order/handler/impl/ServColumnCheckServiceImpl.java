package com.okdeer.mall.order.handler.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.mall.common.vo.Request;
import com.okdeer.mall.common.vo.Response;
import com.okdeer.mall.operate.column.service.ServerColumnService;
import com.okdeer.mall.operate.entity.ServerColumn;
import com.okdeer.mall.operate.enums.ServerStatus;
import com.okdeer.mall.order.enums.OrderOptTypeEnum;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.vo.ServiceOrderReq;
import com.okdeer.mall.order.vo.ServiceOrderResp;

/**
 * ClassName: ServerCheckServiceImpl 
 * @Description: 服务栏目校验处理
 * @author maojj
 * @date 2016年9月21日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构1.1			2016年9月21日				maojj		服务栏目校验处理
 */
@Service("servColumnCheckService")
public class ServColumnCheckServiceImpl implements RequestHandler<ServiceOrderReq,ServiceOrderResp> {

	/**
	 * 服务栏目Service
	 */
	@Resource
	private ServerColumnService serverColumnService;

	@Override
	public void process(Request<ServiceOrderReq> req, Response<ServiceOrderResp> resp) throws Exception {
		ServiceOrderReq reqData = req.getData();
		ServiceOrderResp respData = resp.getData();
		// 查询服务栏目
		ServerColumn serverColumn = serverColumnService.findById(reqData.getColumnServerId());
		
		// 服务栏目不存在
//		if (serverColumn == null || serverColumn.getDisabled() == Disabled.invalid) {
//			resp.setResult(ResultCodeEnum.SERVER_COLUMN_NOT_EXISTS);
//			req.setComplete(true);
//			return;
//		}
		// 服务栏目已关闭
//		if (serverColumn.getServerStatus() == ServerStatus.close) {
//			resp.setResult(ResultCodeEnum.SERVER_COLUMN_IS_CLOSED);
//			req.setComplete(true);
//			return;
//		}
		// 设置响应信息
		if(req.getOrderOptType() == OrderOptTypeEnum.ORDER_SETTLEMENT && null != serverColumn){
			respData.setServerColumnId(serverColumn.getId());
		}
	}

}
