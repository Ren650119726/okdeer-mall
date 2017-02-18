package com.okdeer.mall.order.handler.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.bo.FavourParamBuilder;
import com.okdeer.mall.activity.discount.entity.PreferentialVo;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.service.GetPreferentialService;
import com.okdeer.mall.order.vo.ServiceOrderReq;
import com.okdeer.mall.order.vo.ServiceOrderResp;

/**
 * ClassName: ServerCheckServiceImpl 
 * @Description: 活动查询
 * @author wushp
 * @date 2016年9月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.1.0			2016年9月28日				wushp		活动查询
 *	    V1.1.0		    2016-09-23			   tangy		到店消费按店铺地址查询代金券
 */
@Service("servActivityQueryService")
public class ServActivityQueryServiceImpl implements RequestHandler<ServiceOrderReq,ServiceOrderResp> {

	/**
	 * log
	 */
	// private static final Logger logger = LoggerFactory.getLogger(ServActivityQueryServiceImpl.class);
			
	@Resource
	private FavourParamBuilder favourParamBuilder;

	@Resource
	private GetPreferentialService getPreferentialService;
	
	@Override
	public void process(Request<ServiceOrderReq> req, Response<ServiceOrderResp> resp) throws Exception {
		ServiceOrderResp respData = resp.getData();
		FavourParamBO favourParamBO = favourParamBuilder.build(req, respData);
		// 查询用户的可用优惠
		PreferentialVo preferentialVo = getPreferentialService.findPreferentialByUser(favourParamBO);
		BeanMapper.copy(preferentialVo, respData);
	}
}
