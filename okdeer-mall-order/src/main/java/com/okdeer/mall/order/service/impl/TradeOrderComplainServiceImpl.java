package com.okdeer.mall.order.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.mall.common.enums.IsRead;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderComplain;
import com.okdeer.mall.order.entity.TradeOrderComplainImage;
import com.okdeer.mall.order.enums.CompainStatusEnum;
import com.okdeer.mall.order.service.TradeOrderComplainServiceApi;
import com.okdeer.mall.order.vo.TradeOrderComplainQueryVo;
import com.okdeer.mall.order.vo.TradeOrderComplainVo;
import com.yschome.base.common.exception.ServiceException;
import com.yschome.base.common.utils.PageUtils;
import com.okdeer.mall.order.mapper.TradeOrderComplainImageMapper;
import com.okdeer.mall.order.mapper.TradeOrderComplainMapper;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.service.TradeOrderComplainService;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	重构4.1.0			2016-07-18   	wushp				重构4.1.0
 *    重构4.1           2016-7-16           wusw               添加根据店铺id，查询投诉单和订单信息的方法 
 *    重构4.1           2016-7-18           wusw               添加获取店铺的投诉消息记录数方法 
 *    重构4.1（代码评审优化） 2016-8-18           wusw               优化更新未读的投诉记录为已读状态 代码
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderComplainServiceApi")
class TradeOrderComplainServiceImpl implements TradeOrderComplainService, TradeOrderComplainServiceApi {

	@Resource
	private TradeOrderComplainMapper tradeOrderComplainMapper;
	// begin add by wushp
	/**
	 * 订单mapper
	 */
	@Resource
	private TradeOrderMapper tradeOrderMapper;
	/**
	 * 投诉图片mapper
	 */
	@Resource
	TradeOrderComplainImageMapper tradeOrderComplainImageMapper;
	// end add by wushp
	@Override
	public TradeOrderComplain findById(String id) throws ServiceException {

		return tradeOrderComplainMapper.selectByPrimaryKey(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(TradeOrderComplain tradeOrderComplain) throws ServiceException {
		// TODO Auto-generated method stub
		tradeOrderComplainMapper.insertSelective(tradeOrderComplain);
	}

	@Override
	public List<TradeOrderComplainVo> findByOrderId(String orderId) throws ServiceException {
		// TODO Auto-generated method stub
		return tradeOrderComplainMapper.findOrderComplainByParams(orderId);
	}
	
	// begin add by wushp
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderComplainServiceApi#serviceComplain
	 * (com.okdeer.mall.order.entity.TradeOrderComplain, java.util.List)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addServiceComplain(TradeOrderComplain tradeOrderComplain,
			List<TradeOrderComplainImage> tradeOrderComplainImageList) throws ServiceException {
		TradeOrder order = new TradeOrder();
		order.setCompainStatus(CompainStatusEnum.HAVE_COMPAIN);
		order.setId(tradeOrderComplain.getOrderId());
		//更新订单的投诉状态为已投诉
		tradeOrderMapper.updateOrderStatus(order);
		//插入数据到订单投诉表
		tradeOrderComplainMapper.insertSelective(tradeOrderComplain);
		if (CollectionUtils.isNotEmpty(tradeOrderComplainImageList)) {
			tradeOrderComplainImageMapper.insertByBatch(tradeOrderComplainImageList);
		}
	}
	// end add by wushp

	//Begin  重构4.1  add by wusw
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderComplainServiceApi#findByStoreId(java.lang.String)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public PageUtils<TradeOrderComplainQueryVo> updateComplainByStoreId(Map<String,Object> params,int pageNumber,int pageSize) throws ServiceException {
		//更新未读的投诉记录为已读状态
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("storeId", params.get("storeId"));
		map.put("isRead", IsRead.UNREAD);
		map.put("read", IsRead.READ);
		tradeOrderComplainMapper.updateReadByStoreId(map);
		
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradeOrderComplainQueryVo> result = tradeOrderComplainMapper.selectComplainByStoreId(params);
		if (result == null) {
			result = new ArrayList<TradeOrderComplainQueryVo>();
		}
		return new PageUtils<TradeOrderComplainQueryVo>(result);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderComplainServiceApi#updateComplainReds(java.lang.String)
	 */
	@Override
	public TradeOrderComplainVo findComplainContentImage(String id) throws ServiceException {
		return tradeOrderComplainMapper.selectComplainContentById(id);
	}
	//End  重构4.1  add by wusw

	//Begin  重构4.1  add by wusw  20160718
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderComplainServiceApi#findCountByStoreId(java.lang.String)
	 */
	@Override
	public int findCountByStoreId(Map<String,Object> params) throws ServiceException {
		return tradeOrderComplainMapper.selectCountUnReadByStoreId(params);
	}
	//End  重构4.1  add by wusw  20160718
}