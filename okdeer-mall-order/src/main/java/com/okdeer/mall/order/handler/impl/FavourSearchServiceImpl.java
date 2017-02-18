package com.okdeer.mall.order.handler.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.bo.FavourParamBuilder;
import com.okdeer.mall.activity.discount.entity.PreferentialVo;
import com.okdeer.mall.order.handler.FavourSearchService;
import com.okdeer.mall.order.service.GetPreferentialService;
import com.okdeer.mall.order.vo.TradeOrderReqDto;
import com.okdeer.mall.order.vo.TradeOrderResp;
import com.okdeer.mall.order.vo.TradeOrderRespDto;

/**
 * ClassName: ValidFavourFindServiceImpl 
 * @Description: 查找用户有效的优惠记录
 * @author maojj
 * @date 2016年7月22日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1			2016-07-14			maojj			查找用户有效的优惠记录
 *		重构V4.1			2016-07-14			maojj			查找用户有效的优惠记录增加店铺类型的判断
 *		V1.1.0			2016-09-23			tangy			代金券判断指定分类使用
 *		V1.1.0			2016-09-23			tangy			添加日志
 *		友门鹿V2.1			2017-02-17		    maojj			代金券查询优化
 */
@Service
public class FavourSearchServiceImpl implements FavourSearchService {
	/**
	 * log
	 */
	// private static final Logger logger = LoggerFactory.getLogger(FavourSearchServiceImpl.class);
	
	// Begin added by maojj 2017-02-17
	@Resource
	private FavourParamBuilder favourParamBuilder;

	@Resource
	private GetPreferentialService getPreferentialService;
	// End added by maojj 2017-02-17
	
	/**
	 * 查找用户有效的优惠记录
	 * 注：平台发起的满减、代金券活动，只有云上店可以使用，其它类型的店铺均不可使用
	 */
	@Override
	public void process(TradeOrderReqDto reqDto, TradeOrderRespDto respDto) throws Exception {
		TradeOrderResp resp = respDto.getResp();
		FavourParamBO favourParamBo = favourParamBuilder.build(reqDto);
		// 订单总金额存入上下文，后续流程需要使用
		reqDto.getContext().setTotalAmount(favourParamBo.getTotalAmount());
		// 查询用户的可用优惠
		PreferentialVo preferentialVo = getPreferentialService.findPreferentialByUser(favourParamBo);
		BeanMapper.copy(preferentialVo, resp);
	}
}
