package com.okdeer.mall.order.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.mapper.TradeOrderItemDetailMapper;
import com.okdeer.mall.order.service.TradeOrderItemDetailService;
import com.okdeer.mall.order.service.TradeOrderItemDetailServiceApi;
import com.okdeer.mall.order.vo.OrderItemDetailConsumeVo;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *    V1.1.0            2016-09-26           wusw                 添加消费码验证（到店消费）相应方法
 *    V1.1.0            2016-09-29           luosm     			     根据订单id查询明细列表
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderItemDetailServiceApi")
class TradeOrderItemDetailServiceImpl implements TradeOrderItemDetailService, TradeOrderItemDetailServiceApi {

	@Resource
	private TradeOrderItemDetailMapper tradeOrderItemDetailMapper;

	/**
	 * @desc 根据订单项ID更新消费明细状态为退款
	 *
	 * @param orderItemId
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateStatusWithRefund(String orderItemId) {
		return tradeOrderItemDetailMapper.updateStatusWithRefund(orderItemId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateStatusWithExpire(String orderItemId) {
		return tradeOrderItemDetailMapper.updateStatusWithExpire(orderItemId);
	}

	/**
	 * @desc 查询订单明显
	 */
	@Override
	public List<TradeOrderItemDetail> selectByOrderItemId(String orderItemId) {
		return tradeOrderItemDetailMapper.selectByOrderItemId(orderItemId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertSelective(TradeOrderItemDetail itemDetail) throws Exception {
		tradeOrderItemDetailMapper.insertSelective(itemDetail);
	}

	@Override
	public List<TradeOrderItemDetail> selectByOrderItemById(String orderItemId) throws Exception {
		return tradeOrderItemDetailMapper.selectByOrderItemById(orderItemId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertBatch(List<TradeOrderItemDetail> itemDetailList) throws Exception {
		tradeOrderItemDetailMapper.insertBatch(itemDetailList);

	}

	/**
	 * 查询未消费数量
	 */
	@Override
	public int selectUnConsumerCount(String orderItemId) {
		return tradeOrderItemDetailMapper.selectUnConsumerCount(orderItemId);
	}

    // Begin V1.1.0 add by wusw 20160926
    /**
     * (non-Javadoc)
     * @see com.okdeer.mall.order.service.TradeOrderItemDetailServiceApi#findOrderInfoByConsumeCode(java.util.Map)
     */
    @Override
    public List<OrderItemDetailConsumeVo> findOrderDetailByConsumeCode(Map<String, Object> params) throws ServiceException{
        return tradeOrderItemDetailMapper.findOrderInfoByConsumeCode(params);
        
    }
    // End V1.1.0 add by wusw 20160926

    //start added by luosm 20160929 V1.1.0
    /**
     * 
     * 根据订单id查询明细列表
     */
    @Override
    public List<TradeOrderItemDetail> selectByOrderItemDetailByOrderId(String orderId) {
        return tradeOrderItemDetailMapper.selectByOrderItemDetailByOrderId(orderId);
    }
    //end added by luosm 20160929 V1.1.0
    
    //Begin added by zhaqoc 20160929 V1.1.0
    /**
     * 查询消费码在店铺中是否已经存在
     * @param storeId
     * @param consumeCode
     * @return
     */
    @Override
    public TradeOrderItemDetail checkConsumeHasExsit(String storeId, String consumeCode) {
        return tradeOrderItemDetailMapper.checkConsumeHasExsit(storeId, consumeCode);
    }
    //End added by zhaqoc 20160929 V1.1.0
}