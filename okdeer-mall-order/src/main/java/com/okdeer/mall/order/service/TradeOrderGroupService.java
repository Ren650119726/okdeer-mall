package com.okdeer.mall.order.service;

import java.util.List;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.discount.dto.ActivityDiscountGroupSkuDto;
import com.okdeer.mall.order.bo.TradeOrderGroupParamBo;
import com.okdeer.mall.order.dto.GroupJoinUserDto;
import com.okdeer.mall.order.dto.TradeOrderGroupDetailDto;
import com.okdeer.mall.order.dto.TradeOrderGroupDto;
import com.okdeer.mall.order.dto.TradeOrderGroupGoodsDto;
import com.okdeer.mall.order.dto.TradeOrderGroupParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderGroupRelation;


public interface TradeOrderGroupService extends IBaseService {
	
	/**
	 * @Description: 根据团购订单查询参与用户列表
	 * @param groupOrderId 团购订单id
	 * @param screen 屏幕分辨率。用于处理用户头像图片
	 * @return   
	 * @author maojj
	 * @date 2017年10月16日
	 */
	List<GroupJoinUserDto> findGroupJoinUserList(String groupOrderId,String screen) throws ServiceException;
	
	/**
	 * @Description: 查询团购商品信息
	 * @param activityId 用户id
	 * @param storeSkuId 商品id
	 * @return ActivityDiscountGroupDto  
	 * @author tuzhd
	 * @date 2017年10月16日
	 */
	ActivityDiscountGroupSkuDto findGoodsGroupList(String activityId,String storeSkuId);
	
	/**
	 * @Description: 分页list 后台管理用
	 * @param param
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @throws Exception
	 * @author zhangkn
	 * @date 2017年10月12日
	 */
	PageUtils<TradeOrderGroupDto> findPage(TradeOrderGroupParamDto param, int pageNum, int pageSize)
		throws Exception;

	/**
	 * @Description: 关闭活动时更新团购订单
	 * @param activityId   
	 * @author maojj
	 * @date 2017年10月18日
	 */
	void updateByColseActivity(String activityId) throws Exception;
	
	/**
	 * @Description: 查询拼团详情
	 * @param groupOrderId
	 * @return   
	 * @author maojj
	 * @date 2017年10月18日
	 */
	TradeOrderGroupDetailDto findGroupJoinDetail(String groupOrderId,String screen) throws ServiceException;

	/**
	 * @Description: 查询分页的为成团数据
	 * @param paramBo
	 * @param pageNumber
	 * @param pageSize
	 * @author tuzhd
	 * @date 2017年10月18日
	 */
	PageUtils<TradeOrderGroupGoodsDto> findOrderGroupList(TradeOrderGroupParamBo paramBo, Integer pageNumber,
			Integer pageSize);
	
	/**
	 * @Description: 团购订单开团
	 * @param tradeOrder 交易订单
	 * @param orderGroupRel 团购订单关联关系
	 * @throws Exception   
	 * @author maojj
	 * @date 2017年10月27日
	 */
	void openGroup(TradeOrder tradeOrder, TradeOrderGroupRelation orderGroupRel) throws Exception;
	
	/**
	 * @Description: 订单入团
	 * @param tradeOrder
	 * @param orderGroupRel
	 * @throws Exception   
	 * @author maojj
	 * @date 2017年10月27日
	 */
	void joinGroup(TradeOrder tradeOrder, TradeOrderGroupRelation orderGroupRel) throws Exception;
}
