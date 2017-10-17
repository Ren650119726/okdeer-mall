package com.okdeer.mall.order.api;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.discount.dto.ActivityDiscountGroupSkuDto;
import com.okdeer.mall.order.dto.TradeOrderGroupDto;
import com.okdeer.mall.order.dto.TradeOrderGroupParamDto;
import com.okdeer.mall.order.service.TradeOrderGroupApi;
import com.okdeer.mall.order.service.TradeOrderGroupService;

/**
 * ClassName: TradeOrderCarrierApiImpl
 *
 * @author zhangkn
 * @Description: 团购订单api
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderGroupApi")
public class TradeOrderGroupApiImpl implements TradeOrderGroupApi {

    /**
     * 注入-service
     */
    @Autowired
    private TradeOrderGroupService tradeOrderGroupService;

	@Override
	public TradeOrderGroupDto findById(String id) throws Exception {
		return BeanMapper.map(tradeOrderGroupService.findById(id), TradeOrderGroupDto.class);
	}

	@Override
	public PageUtils<TradeOrderGroupDto> findPage(TradeOrderGroupParamDto param, int pageNum, int pageSize)
			throws Exception {
		return tradeOrderGroupService.findPage(param, pageNum, pageSize);
	}
	
	/**
	 * @Description: 查询团购商品信息
	 * @param activityId 用户id
	 * @param storeSkuId 商品id
	 * @return ActivityDiscountGroupDto  
	 * @author tuzhd
	 * @date 2017年10月16日
	 */
	public ActivityDiscountGroupSkuDto findGoodsGroupList(String activityId,String storeSkuId){
		return tradeOrderGroupService.findGoodsGroupList(activityId,storeSkuId);
	}
}
