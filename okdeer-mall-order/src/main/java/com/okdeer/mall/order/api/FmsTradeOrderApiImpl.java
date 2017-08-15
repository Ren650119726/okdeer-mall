
package com.okdeer.mall.order.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.okdeer.archive.store.dto.StoreInfoDto;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.bdp.address.entity.Address;
import com.okdeer.bdp.address.service.IAddressService;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsService;
import com.okdeer.mall.activity.coupons.service.ActivitySaleService;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillService;
import com.okdeer.mall.order.bo.FmsTradeOrderBo;
import com.okdeer.mall.order.dto.FmsTradeOrderDto;
import com.okdeer.mall.order.dto.TradeOrderQueryParamDto;
import com.okdeer.mall.order.entity.TradeOrderLogistics;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PickUpTypeEnum;
import com.okdeer.mall.order.service.FmsTradeOrderApi;
import com.okdeer.mall.order.service.TradeOrderLogisticsService;
import com.okdeer.mall.order.service.TradeOrderRefundsService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.system.entity.SysUserInvitationLoginNameVO;
import com.okdeer.mall.system.service.InvitationCodeServiceApi;

/**
 * ClassName: FmsTradeOrderApiImpl
 * 
 * @Description: 财务系统订单api查询
 * @author zengjizu
 * @date 2017年7月27日
 *
 *       =======================================================================
 *       ========================== Task ID Date Author Description
 *       ----------------+----------------+-------------------+-----------------
 *       --------------------------
 *
 */
@Service(version = "1.0.0")
public class FmsTradeOrderApiImpl implements FmsTradeOrderApi {

	@Autowired
	private TradeOrderService tradeOrderService;

	@Autowired
	private ActivityCollectCouponsService activityCollectCouponsService;

	@Autowired
	private ActivityDiscountService activityDiscountService;

	@Autowired
	private ActivitySaleService activitySaleService;

	@Autowired
	private ActivitySeckillService activitySeckillService;

	@Autowired
	private TradeOrderLogisticsService tradeOrderLogisticsService;

	@Reference(version = "1.0.0", check = false)
	private IAddressService addressService;

	@Autowired
	private TradeOrderRefundsService tradeOrderRefundsService;

	/**
	 * 地址service
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoServiceApi;

	/***
	 * 邀请信息
	 */
	@Reference(version = "1.0.0", check = false)
	private InvitationCodeServiceApi invitationCodeService;

	@Override
	public PageUtils<FmsTradeOrderDto> findOrderListByParams(TradeOrderQueryParamDto tradeOrderQueryParamDto,
			int pageNum, int pageSize) throws MallApiException {
		try {
			PageUtils<FmsTradeOrderBo> fmsTradeOrderBoPage = tradeOrderService
					.findOrderForFinanceByParams(tradeOrderQueryParamDto, pageNum, pageSize);
			List<FmsTradeOrderBo> fmsTradeOrderBoList = fmsTradeOrderBoPage.getList();
			List<FmsTradeOrderDto> fmsTradeOrderDtoList = converToFmsTradeOrderDtoList(fmsTradeOrderBoList,
					tradeOrderQueryParamDto);
			PageUtils<FmsTradeOrderDto> pageUtils = new PageUtils<>(fmsTradeOrderDtoList);
			pageUtils.setPages(fmsTradeOrderBoPage.getPages());
			pageUtils.setPageNum(fmsTradeOrderBoPage.getPageNum());
			pageUtils.setTotal(fmsTradeOrderBoPage.getTotal());
			return pageUtils;
		} catch (ServiceException e) {
			throw new MallApiException(e);
		}
	}

	@Override
	public List<FmsTradeOrderDto> findOrderListByParams(TradeOrderQueryParamDto tradeOrderQueryParamDto)
			throws MallApiException {
		List<FmsTradeOrderBo> fmsTradeOrderBoList;
		try {
			fmsTradeOrderBoList = tradeOrderService.findOrderListForFinanceByParams(tradeOrderQueryParamDto);
			return converToFmsTradeOrderDtoList(fmsTradeOrderBoList, tradeOrderQueryParamDto);
		} catch (ServiceException e) {
			throw new MallApiException(e);
		}

	}

	@Override
	public long findOrderListCount(TradeOrderQueryParamDto tradeOrderQueryParamDto) throws MallApiException {
		try {
			return tradeOrderService.findOrderCountForFinanceByParams(tradeOrderQueryParamDto);
		} catch (ServiceException e) {
			throw new MallApiException(e);
		}
	}

	private List<FmsTradeOrderDto> converToFmsTradeOrderDtoList(List<FmsTradeOrderBo> fmsTradeOrderBoList,
			TradeOrderQueryParamDto tradeOrderQueryParamDto) throws MallApiException {
		if (CollectionUtils.isEmpty(fmsTradeOrderBoList)) {
			return Lists.newArrayList();
		}
		List<FmsTradeOrderDto> list = BeanMapper.mapList(fmsTradeOrderBoList, FmsTradeOrderDto.class);
		if (tradeOrderQueryParamDto.isQueryActive()) {
			// 设置活动信息
			setActiveInfo(list);
		}

		if (tradeOrderQueryParamDto.isQueryShipAddress()) {
			// 设置收货地址
			setShipAddressInfo(list);
		}

		if (tradeOrderQueryParamDto.isQueryRefund()) {
			// 设置退款信息
			setRefundInfo(list);
		}
		if (tradeOrderQueryParamDto.isQueryInviteInfo()) {
			// 设置邀请人信息
			setInviteInfo(list);
		}
		return list;
	}

	private void setInviteInfo(List<FmsTradeOrderDto> fmsTradeOrderDtoList) {
		List<String> userIdList = Lists.newArrayList();
		for (FmsTradeOrderDto fmsTradeOrderDto : fmsTradeOrderDtoList) {
			if (!userIdList.contains(fmsTradeOrderDto.getUserId())) {
				userIdList.add(fmsTradeOrderDto.getUserId());
			}
		}
		List<SysUserInvitationLoginNameVO> inviteNameLists = new ArrayList<SysUserInvitationLoginNameVO>();
		if (CollectionUtils.isNotEmpty(userIdList)) {
			inviteNameLists = invitationCodeService.selectLoginNameByUserId(userIdList);
		}
		Map<String, SysUserInvitationLoginNameVO> inviteMap = inviteNameLists.stream()
				.collect(Collectors.toMap(SysUserInvitationLoginNameVO::getUserId, e -> e));

		for (FmsTradeOrderDto fmsTradeOrderDto : fmsTradeOrderDtoList) {
			SysUserInvitationLoginNameVO sysUserInvitationLoginNameVO = inviteMap.get(fmsTradeOrderDto.getUserId());
			if (sysUserInvitationLoginNameVO == null) {
				continue;
			}
			if (sysUserInvitationLoginNameVO.getsLoginName() != null) {
				fmsTradeOrderDto.setInviteName(sysUserInvitationLoginNameVO.getsLoginName());
			} else if (sysUserInvitationLoginNameVO.getbLoginName() != null) {
				fmsTradeOrderDto.setInviteName(sysUserInvitationLoginNameVO.getbLoginName());
			}
		}
	}

	/**
	 * @Description: 设置收货地址信息
	 * @param fmsTradeOrderDtoList
	 * @throws ServiceException
	 * @author zengjizu
	 * @date 2017年7月27日
	 */
	private void setShipAddressInfo(List<FmsTradeOrderDto> fmsTradeOrderDtoList) throws MallApiException {
		// 有物流信息的订单id
		List<String> logisticOrderIdList = Lists.newArrayList();
		List<String> storeIdList = Lists.newArrayList();
		for (FmsTradeOrderDto fmsTradeOrderDto : fmsTradeOrderDtoList) {
			boolean isQueryShipAddress = isQueryShipAddress(fmsTradeOrderDto);
			if (isQueryShipAddress) {
				logisticOrderIdList.add(fmsTradeOrderDto.getId());
			}
			boolean isQueryStoreAddress = isQueryStoreAddress(fmsTradeOrderDto);
			if (isQueryStoreAddress && !storeIdList.contains(fmsTradeOrderDto.getStoreId())) {
				storeIdList.add(fmsTradeOrderDto.getStoreId());
			}
		}

		try {

			List<TradeOrderLogistics> logisticsList = Lists.newArrayList();
			if (CollectionUtils.isNotEmpty(logisticOrderIdList)) {
				logisticsList = tradeOrderLogisticsService.selectByOrderIds(logisticOrderIdList);
			}

			Map<String, TradeOrderLogistics> logisticsMap = logisticsList.stream()
					.collect(Collectors.toMap(TradeOrderLogistics::getOrderId, e -> e));

			List<StoreInfoDto> addressList = Lists.newArrayList();
			if (CollectionUtils.isNotEmpty(storeIdList)) {
				addressList = storeInfoServiceApi.findByIds(storeIdList);
			}

			Map<String, StoreInfoDto> storeAddressMap = addressList.stream()
					.collect(Collectors.toMap(StoreInfoDto::getId, e -> e));

			Map<String, Address> addressMap = Maps.newHashMap();
			for (FmsTradeOrderDto fmsTradeOrderDto : fmsTradeOrderDtoList) {
				TradeOrderLogistics logistics = logisticsMap.get(fmsTradeOrderDto.getId());
				boolean isQueryShipAddress = isQueryShipAddress(fmsTradeOrderDto);
				if (isQueryShipAddress && logistics != null && !StringUtils.isBlank(logistics.getCityId())) {
					Address address = getAddressByCityId(logistics.getCityId(), addressMap);
					// 所属城市 实物订单的送货上门取物流表的地址
					fmsTradeOrderDto.setCityName(address == null ? "" : address.getName());
					String area = logistics.getArea() == null ? "" : logistics.getArea();
					String addressExt = logistics.getAddress() == null ? "" : logistics.getAddress();
					// 收货地址
					fmsTradeOrderDto.setAddress(area + addressExt);
				}

				StoreInfoDto storeInfoDto = storeAddressMap.get(fmsTradeOrderDto.getStoreId());
				boolean isQueryStoreAddress = isQueryStoreAddress(fmsTradeOrderDto);
				if (isQueryStoreAddress && storeInfoDto != null) {
					Address address = getAddressByCityId(storeInfoDto.getCityId(), addressMap);
					fmsTradeOrderDto.setCityName(address == null ? "" : address.getName());
					fmsTradeOrderDto.setAddress(storeInfoDto.getAddress());
				}

			}
		} catch (ServiceException e1) {
			throw new MallApiException(e1);
		}
	}

	private boolean isQueryStoreAddress(FmsTradeOrderDto fmsTradeOrderDto) {
		// 送货上门
		boolean isToStore = fmsTradeOrderDto.getType() == OrderTypeEnum.PHYSICAL_ORDER
				&& fmsTradeOrderDto.getPickUpType() == PickUpTypeEnum.TO_STORE_PICKUP;
		// 上门服务订单
		boolean isStoreConsumeOrder = fmsTradeOrderDto.getType() == OrderTypeEnum.STORE_CONSUME_ORDER;
		return isToStore || isStoreConsumeOrder;
	}

	private boolean isQueryShipAddress(FmsTradeOrderDto fmsTradeOrderDto) {
		// 送货上门
		boolean isDliver = fmsTradeOrderDto.getType() == OrderTypeEnum.PHYSICAL_ORDER
				&& fmsTradeOrderDto.getPickUpType() == PickUpTypeEnum.DELIVERY_DOOR;
		// 上门服务订单
		boolean isServiceStoreOrder = fmsTradeOrderDto.getType() == OrderTypeEnum.SERVICE_STORE_ORDER;
		return isDliver || isServiceStoreOrder;
	}

	/**
	 * @Description: 获得城市信息
	 * @param cityId
	 *            城市id
	 * @param cityMap
	 *            缓村map
	 * @return
	 * @author zengjizu
	 * @date 2017年4月26日
	 */
	private Address getAddressByCityId(String cityId, Map<String, Address> cityMap) {
		if (cityMap.get(cityId) != null) {
			return cityMap.get(cityId);
		}
		Address address = addressService.getAddressById(Long.parseLong(cityId));
		cityMap.put(cityId, address);
		return address;
	}

	/**
	 * @Description: 设置优惠信息
	 * @param fmsTradeOrderBoList
	 * @author zengjizu
	 * @date 2017年7月27日
	 */
	private void setActiveInfo(List<FmsTradeOrderDto> fmsTradeOrderDtoList) {
		// 代金劵活动id
		List<String> voncherActiviIdList = Lists.newArrayList();
		// 满折活动id、满减活动id
		List<String> discountActiviIdList = Lists.newArrayList();
		// 特惠活动 id 低价抢购Id
		List<String> saleActiviIdList = Lists.newArrayList();
		// 秒杀活动Id
		List<String> seckillActiviIdList = Lists.newArrayList();

		for (FmsTradeOrderDto fmsTradeOrderDto : fmsTradeOrderDtoList) {
			switch (fmsTradeOrderDto.getActivityType()) {
			case VONCHER:
				addActivityId(voncherActiviIdList, fmsTradeOrderDto.getActivityId());
				break;
			case FULL_REDUCTION_ACTIVITIES:
			case FULL_DISCOUNT_ACTIVITIES:
				addActivityId(discountActiviIdList, fmsTradeOrderDto.getActivityId());
				break;
			case SALE_ACTIVITIES:
			case LOW_PRICE:
				addActivityId(saleActiviIdList, fmsTradeOrderDto.getActivityId());
				break;
			case SECKILL_ACTIVITY:
				addActivityId(seckillActiviIdList, fmsTradeOrderDto.getActivityId());
				break;

			default:
				break;
			}
		}
		Map<String, ActivityCollectCoupons> voncherActiviMap = queryVoncherActiviList(voncherActiviIdList);

		Map<String, ActivityDiscount> discountActiviMap = queryDiscountActiviList(discountActiviIdList);

		Map<String, ActivitySale> saleActiviMap = querySaleActiviList(saleActiviIdList);

		Map<String, ActivitySeckill> seckillActiviMap = querySeckillActiviList(seckillActiviIdList);

		for (FmsTradeOrderDto fmsTradeOrderDto : fmsTradeOrderDtoList) {
			switch (fmsTradeOrderDto.getActivityType()) {
			case VONCHER:
				if (voncherActiviMap.get(fmsTradeOrderDto.getActivityId()) != null) {
					fmsTradeOrderDto.setActivityName(voncherActiviMap.get(fmsTradeOrderDto.getActivityId()).getName());
				}
				break;
			case FULL_REDUCTION_ACTIVITIES:
			case FULL_DISCOUNT_ACTIVITIES:
				if (discountActiviMap.get(fmsTradeOrderDto.getActivityId()) != null) {
					fmsTradeOrderDto.setActivityName(discountActiviMap.get(fmsTradeOrderDto.getActivityId()).getName());
				}
				break;
			case SALE_ACTIVITIES:
			case LOW_PRICE:
				if (saleActiviMap.get(fmsTradeOrderDto.getActivityId()) != null) {
					fmsTradeOrderDto.setActivityName(saleActiviMap.get(fmsTradeOrderDto.getActivityId()).getName());
				}
				break;
			case SECKILL_ACTIVITY:
				if (seckillActiviMap.get(fmsTradeOrderDto.getActivityId()) != null) {
					fmsTradeOrderDto
							.setActivityName(seckillActiviMap.get(fmsTradeOrderDto.getActivityId()).getSeckillName());
				}
				break;
			default:
				break;
			}
		}

	}

	/**
	 * @Description: 设置退款信息
	 * @param list
	 * @author zengjizu
	 * @throws MallApiException
	 * @date 2017年7月27日
	 */
	private void setRefundInfo(List<FmsTradeOrderDto> fmsTradeOrderDtoList) throws MallApiException {
		List<String> orderIdList = Lists.newArrayList();
		for (FmsTradeOrderDto fmsTradeOrderDto : fmsTradeOrderDtoList) {
			if (fmsTradeOrderDto.getType() == OrderTypeEnum.PHYSICAL_ORDER
					|| fmsTradeOrderDto.getType() == OrderTypeEnum.STORE_CONSUME_ORDER) {
				orderIdList.add(fmsTradeOrderDto.getId());
			}
		}
		if(CollectionUtils.isEmpty(orderIdList)){
			return ;
		}
		try {
			List<TradeOrderRefunds> tradeOrderRefundsList = tradeOrderRefundsService.selectByOrderIds(orderIdList);

			Map<String, List<TradeOrderRefunds>> groupRefundMap = tradeOrderRefundsList.stream()
					.collect(Collectors.groupingBy(TradeOrderRefunds::getOrderId));

			for (FmsTradeOrderDto fmsTradeOrderDto : fmsTradeOrderDtoList) {
				setRunfundInfo(fmsTradeOrderDto, groupRefundMap.get(fmsTradeOrderDto.getId()));
			}
		} catch (Exception e) {
			throw new MallApiException(e);
		}

	}

	private void setRunfundInfo(FmsTradeOrderDto fmsTradeOrderDto, List<TradeOrderRefunds> refundList) {
		if (CollectionUtils.isEmpty(refundList)) {
			fmsTradeOrderDto.setIsRefund(WhetherEnum.not.getValue());
		} else {
			fmsTradeOrderDto.setIsRefund(WhetherEnum.whether.getValue());
			BigDecimal refundPrice = new BigDecimal("0");
			BigDecimal refundPreferentialPrice = new BigDecimal("0");
			for (TradeOrderRefunds tradeOrderRefunds : refundList) {
				if (tradeOrderRefunds.getTotalAmount() != null) {
					refundPrice = refundPrice.add(tradeOrderRefunds.getTotalAmount());

				}
				if (tradeOrderRefunds.getTotalPreferentialPrice() != null) {
					refundPreferentialPrice = refundPreferentialPrice
							.add(tradeOrderRefunds.getTotalPreferentialPrice());
				}
			}
			// 退款总金额
			fmsTradeOrderDto.setRefundPrice(refundPrice);
			// 退款优惠金额
			fmsTradeOrderDto.setRefundPreferentialPrice(refundPreferentialPrice);
		}
	}

	private Map<String, ActivityDiscount> queryDiscountActiviList(List<String> discountActiviIdList) {
		List<ActivityDiscount> list = activityDiscountService.findByIds(discountActiviIdList);
		return list.stream().collect(Collectors.toMap(ActivityDiscount::getId, e -> e));
	}

	private Map<String, ActivityCollectCoupons> queryVoncherActiviList(List<String> voncherActiviIdList) {
		List<ActivityCollectCoupons> list = activityCollectCouponsService.findByIds(voncherActiviIdList);
		return list.stream().collect(Collectors.toMap(ActivityCollectCoupons::getId, e -> e));
	}

	private Map<String, ActivitySale> querySaleActiviList(List<String> saleActiviIdList) {
		List<ActivitySale> list = activitySaleService.findByIds(saleActiviIdList);
		return list.stream().collect(Collectors.toMap(ActivitySale::getId, e -> e));
	}

	private void addActivityId(List<String> list, String activityId) {
		if (!list.contains(activityId)) {
			list.add(activityId);
		}
	}

	private Map<String, ActivitySeckill> querySeckillActiviList(List<String> seckillActiviIdList) {
		List<ActivitySeckill> list = activitySeckillService.findByIds(seckillActiviIdList);
		return list.stream().collect(Collectors.toMap(ActivitySeckill::getId, e -> e));
	}

}
