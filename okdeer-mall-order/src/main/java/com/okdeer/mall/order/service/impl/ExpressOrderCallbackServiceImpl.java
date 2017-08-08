package com.okdeer.mall.order.service.impl;

import com.google.common.collect.Maps;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.ele.entity.ExpressCallback;
import com.okdeer.mall.ele.service.ExpressService;
import com.okdeer.mall.express.dto.ExpressCallbackDto;
import com.okdeer.mall.express.dto.ExpressOrderStatus;
import com.okdeer.mall.express.dto.ResultMsgDto;
import com.okdeer.mall.order.dto.ExpressModeParamDto;
import com.okdeer.mall.order.dto.TradeOrderExtSnapshotParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderCarrier;
import com.okdeer.mall.order.entity.TradeOrderExtSnapshot;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.service.ExpressOrderCallbackService;
import com.okdeer.mall.order.service.TradeOrderCarrierService;
import com.okdeer.mall.order.service.TradeOrderExtSnapshotService;
import com.okdeer.mall.order.service.TradeOrderItemService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.vo.TradeOrderOperateParamVo;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClassName: ExpressOrderCallbackServiceImpl
 *
 * @author wangf01
 * @Description: 订单快递配送信息回调-service-impl
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
@Service
public class ExpressOrderCallbackServiceImpl implements ExpressOrderCallbackService {

    /**
     * 注入配送-service
     */
    @Autowired
    private ExpressService expressService;

    /**
     * 注入订单骑手信息-service
     */
    @Autowired
    private TradeOrderCarrierService tradeOrderCarrierService;

    /**
     * 注入订单信息-service
     */
    @Autowired
    private TradeOrderService tradeOrderService;

    /**
     * 注入订单项信息-service
     */
    @Autowired
    private TradeOrderItemService tradeOrderItemService;

    /**
     * 注入订单扩展快照-service
     */
    @Autowired
    private TradeOrderExtSnapshotService snapshotService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveExpressModePlanA(ExpressModeParamDto paramDto) throws Exception {
        // 1，查询订单信息组装参数
        TradeOrder tradeOrder = tradeOrderService.selectById(paramDto.getExpressOrderId());
        //根据订单id查询订单项信息
        List<TradeOrderItem> orderItemList = tradeOrderItemService.selectOrderItemByOrderId(tradeOrder.getId());
        tradeOrder.setTradeOrderItem(orderItemList);
        TradeOrderExtSnapshotParamDto snapshotParamDto = new TradeOrderExtSnapshotParamDto();
        snapshotParamDto.setOrderId(paramDto.getExpressOrderId());
        TradeOrderExtSnapshot snapshot = snapshotService.selectExtSnapshotByParam(snapshotParamDto);
        tradeOrder.setTradeOrderExt(snapshot);
        // 2，保存第三方推送订单信息并进行判断
        ResultMsgDto<String> resultMsg = expressService.saveExpressOrder(tradeOrder);
        if (resultMsg.getCode() != Integer.parseInt(ExpressOrderStatus.STATUS_200.getValue())) {
            throw new Exception("蜂鸟推送异常" + resultMsg.getMsg());
        }
        // 3，修改订单方案和佣金字段
        updateOrderExpress(paramDto);
        // 4，修改订单扩展快照
        updateOrderSnapExpress(paramDto);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveExpressModePlanB(ExpressModeParamDto paramDto) throws Exception {
        // 自行发货流程 1：修改订单方案和佣金字段 2：修改订单扩展快照表佣金 3：走订单发货流程
        // 1，修改订单方案和佣金字段
        updateOrderExpress(paramDto);
        // 2，修改订单扩展快照
        updateOrderSnapExpress(paramDto);
        // 3，订单发货流程
        TradeOrderOperateParamVo param = new TradeOrderOperateParamVo();
        // 发货的订单ID
        param.setOrderId(paramDto.getExpressOrderId());
        // 操作人ID
        param.setUserId(paramDto.getUserId());
        // 店铺ID
        param.setStoreId(paramDto.getStoreId());
        tradeOrderService.updateOrderShipment(param);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveExpressCallback(ExpressCallbackDto data) throws Exception {
        ExpressCallback callback = new ExpressCallback();
        BeanMapper.copy(data, callback);
        expressService.saveCallback(callback);
        //保存骑手信息
        if (Integer.valueOf(ExpressOrderStatus.STATUS_20.getValue()).equals(data.getOrderStatus())) {
            TradeOrderCarrier entity = new TradeOrderCarrier();
            entity.setOrderNo(data.getPartnerOrderCode());
            entity.setCarrierDriverName(data.getCarrierDriverName());
            entity.setCarrierDriverPhone(data.getCarrierDriverPhone());
            Map<String, Object> map = Maps.newHashMap();
            map.put("orderNo", data.getPartnerOrderCode());
            List<TradeOrder> tradeOrderList = tradeOrderService.selectByParams(map);
            if (CollectionUtils.isNotEmpty(tradeOrderList)) {
                entity.setOrderId(tradeOrderList.get(0).getId());
            }
            tradeOrderCarrierService.insert(entity);
        } else if (Integer.valueOf(ExpressOrderStatus.STATUS_3.getValue()).equals(data.getOrderStatus())) {
            TradeOrderCarrier entity = new TradeOrderCarrier();
            entity.setOrderNo(data.getPartnerOrderCode());
            entity.setCarrierDriverName(data.getCarrierDriverName());
            entity.setCarrierDriverPhone(data.getCarrierDriverPhone());
            entity.setPushTime(data.getPushTime());
            tradeOrderCarrierService.update(entity);
        }
    }

    /**
     * 修改订单方案和佣金字段
     *
     * @param paramDto ExpressModeParamDto
     * @throws Exception
     */
    private void updateOrderExpress(ExpressModeParamDto paramDto) throws Exception {
        TradeOrder tradeOrder = new TradeOrder();
        tradeOrder.setId(paramDto.getExpressOrderId());
        tradeOrder.setDeliveryType(paramDto.getExpressType());
        tradeOrderService.updateByPrimaryKeySelective(tradeOrder);
    }

    /**
     * 修改订单扩展快照表佣金
     *
     * @param paramDto ExpressModeParamDto
     * @throws Exception
     */
    private void updateOrderSnapExpress(ExpressModeParamDto paramDto) throws Exception {
        TradeOrderExtSnapshotParamDto snapshotParamDto = new TradeOrderExtSnapshotParamDto();
        snapshotParamDto.setOrderId(paramDto.getExpressOrderId());
        snapshotParamDto.setDeliveryType(paramDto.getExpressType());
        snapshotService.update(snapshotParamDto);
    }
}
