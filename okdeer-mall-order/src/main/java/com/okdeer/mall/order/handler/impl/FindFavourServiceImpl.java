package com.okdeer.mall.order.handler.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.bo.FavourParamBuilder;
import com.okdeer.mall.activity.discount.entity.PreferentialVo;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.service.GetPreferentialService;

/**
 * ClassName: FindFavourServiceImpl 
 * @Description: TODO
 * @author maojj
 * @date 2017年1月5日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.0 			2017年1月5日				maojj
 *		友门鹿2.1 			2017年2月15日				maojj		     添加最大优惠规则
 */
@Service("findFavourService")
public class FindFavourServiceImpl implements RequestHandler<PlaceOrderParamDto, PlaceOrderDto>{
	
	@Resource
	private FavourParamBuilder favourParamBuilder;

	@Resource
	private GetPreferentialService getPreferentialService;
	
	/**
	 * 查找用户有效的优惠记录
	 * 注：平台发起的满减、代金券活动，只有云上店可以使用，其它类型的店铺均不可使用
	 */
	@Override
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		PlaceOrderParamDto paramDto = req.getData();
		PlaceOrderDto respDto = resp.getData();
		
		StoreSkuParserBo parserBo = (StoreSkuParserBo)paramDto.get("parserBo");
		if(parserBo.isLowFavour()){
			// 参与低价之后，不能参与任何其他优惠活动
			return;
		}
		
		FavourParamBO favourParamBO = favourParamBuilder.build(paramDto, resp.getData());
		// 订单总金额存入上下文，后续流程需要使用
		paramDto.put("totalAmount", favourParamBO.getTotalAmount());
		// 查询用户的可用优惠
		PreferentialVo preferentialVo = getPreferentialService.findPreferentialByUser(favourParamBO);
		BeanMapper.copy(preferentialVo, respDto);
	}
}
