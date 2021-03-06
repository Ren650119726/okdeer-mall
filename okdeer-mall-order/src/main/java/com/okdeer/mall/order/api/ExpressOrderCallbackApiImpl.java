package com.okdeer.mall.order.api;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Maps;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.entity.StoreInfoExt;
import com.okdeer.archive.store.service.IStoreInfoExtServiceApi;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.mall.ele.entity.ExpressCallback;
import com.okdeer.mall.ele.service.ExpressService;
import com.okdeer.mall.express.dto.ExpressCallbackDto;
import com.okdeer.mall.express.dto.ExpressCallbackParamDto;
import com.okdeer.mall.express.dto.ExpressOrderStatus;
import com.okdeer.mall.express.dto.ResultMsgDto;
import com.okdeer.mall.express.enums.ExpressModeCheckEnum;
import com.okdeer.mall.order.dto.ExpressModeParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderCarrier;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PickUpTypeEnum;
import com.okdeer.mall.order.service.ExpressOrderCallbackApi;
import com.okdeer.mall.order.service.ExpressOrderCallbackService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.vo.TradeOrderOperateParamVo;
import com.okdeer.mcm.entity.SmsVO;
import com.okdeer.mcm.service.ISmsService;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * ClassName: ExpressOrderCallbackApiImpl
 *
 * @author wangf01
 * @Description: 订单快递配送信息回调-api-impl
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * V2.6.0 禅道3271         20170808         wangf01           新增saveExpressMode
 * <p>
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.ExpressOrderCallbackApi")
public class ExpressOrderCallbackApiImpl implements ExpressOrderCallbackApi {

    private static final Logger logger = LoggerFactory.getLogger(ExpressOrderCallbackApiImpl.class);

    private static final String EXPRESSMODE = "expressMode";

    /**
     * 短信code
     */
    @Value("${mcm.sys.code}")
    private String mcmSysCode;

    /**
     * 短信token
     */
    @Value("${mcm.sys.token}")
    private String mcmSysToken;

    /**
     * 注入-service
     */
    @Autowired
    private ExpressOrderCallbackService expressOrderCallbackService;

    /**
     * 注入配送-service
     */
    @Autowired
    private ExpressService expressService;

    /**
     * 注入订单-service
     */
    @Autowired
    private TradeOrderService tradeOrderService;

    @Autowired
    private RocketMQProducer rocketMQProducer;

    /**
     * 注入店铺-api
     */
    @Reference(version = "1.0.0", check = false)
    private StoreInfoServiceApi storeInfoServiceApi;

    /**
     * 注入店铺扩展-api
     */
    @Reference(version = "1.0.0", check = false)
    private IStoreInfoExtServiceApi storeInfoExtServiceApi;

    /**
     * 短信接口
     */
    @Reference(version = "1.0.0")
    private ISmsService smsService;

    @Autowired
    private RedisTemplate<String, Boolean> redisTemplate;

    @Override
    public ResultMsgDto<String> findExpressCallback(ExpressModeParamDto paramDto) throws Exception {
        ResultMsgDto<String> resultMsgDto = new ResultMsgDto();
        try {
            TradeOrder order = tradeOrderService.selectById(paramDto.getOrderId());
            ExpressCallbackParamDto callbackParamDto = new ExpressCallbackParamDto();
            callbackParamDto.setOrderNo(order.getOrderNo());
            List<ExpressCallback> callbackList = expressService.findByParam(callbackParamDto);
            if (CollectionUtils.isNotEmpty(callbackList)) {
                ExpressCallback callback = callbackList.get(0);
                resultMsgDto.setCode(callback.getOrderStatus());
                resultMsgDto.setMsg(callback.getDescription());
            } else {
                resultMsgDto.setCode(ExpressModeCheckEnum.TRADE_ORDER_NULL.getCode());
                resultMsgDto.setMsg(ExpressModeCheckEnum.TRADE_ORDER_NULL.getMsg());
            }
        } catch (Exception e) {
            resultMsgDto.setCode(ExpressModeCheckEnum.SUCCESS.getCode());
            resultMsgDto.setMsg("查询第三方信息异常");
        }
        return resultMsgDto;
    }

    @Override
    public ResultMsgDto<String> saveExpressMode(ExpressModeParamDto paramDto) throws Exception {
        ResultMsgDto<String> resultMsgDto = new ResultMsgDto();
        resultMsgDto.setCode(ExpressModeCheckEnum.SUCCESS.getCode());
        resultMsgDto.setMsg(ExpressModeCheckEnum.SUCCESS.getMsg());
        if (paramDto == null) {
            resultMsgDto.setCode(ExpressModeCheckEnum.DTO_NULL.getCode());
            resultMsgDto.setMsg(ExpressModeCheckEnum.DTO_NULL.getMsg());
        } else {
            //检查传入的参数数据
            resultMsgDto = checkData(paramDto, resultMsgDto);
            if (resultMsgDto.getCode() == ExpressModeCheckEnum.SUCCESS.getCode()) {
                //使用redis防止并发操作
                boolean flag = redisTemplate.boundValueOps(EXPRESSMODE + paramDto.getOrderId()).setIfAbsent(true);
                if (!flag) {
                    resultMsgDto.setCode(ExpressModeCheckEnum.ORDER_STATUS_FAIL.getCode());
                    resultMsgDto.setMsg(ExpressModeCheckEnum.ORDER_STATUS_FAIL.getMsg());
                } else {
                    //保险起见，设置redis数据6秒超时删除key
                    redisTemplate.expire(EXPRESSMODE + paramDto.getOrderId(), 6, TimeUnit.SECONDS);
                    //获取店铺佣金方案比例
                    StoreInfoExt storeDetailVo = storeInfoExtServiceApi.findByStoreId(paramDto.getStoreId());
                    switch (paramDto.getExpressType()) {
                        case 1:
                            paramDto.setCommisionRatio(storeDetailVo.getCommisionRatio());
                            break;
                        case 2:
                            paramDto.setCommisionRatio(storeDetailVo.getCommisionRatioPlanB());
                            break;
                        default:
                            break;
                    }
                    //根据配送方式的不同，进入不同的业务流程 1：蜂鸟配送 2：自行配送
                    switch (paramDto.getExpressType()) {
                        case 1:
                            try {
                                expressOrderCallbackService.saveExpressModePlanA(paramDto);
                            } catch (Exception e) {
                                logger.error("蜂鸟配送异常(订单:" + paramDto.getOrderId() + "):{}", e);
                                resultMsgDto.setCode(ExpressModeCheckEnum.ELE_EXPRESS_EXCEPTION.getCode());
                                resultMsgDto.setMsg(ExpressModeCheckEnum.ELE_EXPRESS_EXCEPTION.getMsg());
                            }
                            break;
                        case 2:
                            try {
                                expressOrderCallbackService.saveExpressModePlanB(paramDto);
                            } catch (Exception e) {
                                logger.error("自行配送异常异常(订单:" + paramDto.getOrderId() + "):{}", e);
                                resultMsgDto.setCode(ExpressModeCheckEnum.SELLER_EXPRESS_EXCEPTION.getCode());
                                resultMsgDto.setMsg(ExpressModeCheckEnum.SELLER_EXPRESS_EXCEPTION.getMsg());
                            }
                            break;
                        default:
                            break;
                    }
                }
                //业务走完后删除redis中的key，以便后续操作
                redisTemplate.delete(EXPRESSMODE + paramDto.getOrderId());
            }
        }
        return resultMsgDto;
    }

    @Override
    public void saveExpressCallback(ExpressCallbackDto data) throws Exception {
        expressOrderCallbackService.saveExpressCallback(data);
        switch (data.getOrderStatus()) {
            case 20:
                TradeOrderCarrier carrier = new TradeOrderCarrier();
                carrier.setOrderNo(data.getPartnerOrderCode());
                carrier.setCarrierDriverName(data.getCarrierDriverName());
                carrier.setCarrierDriverPhone(data.getCarrierDriverPhone());
                Map<String, Object> map = Maps.newHashMap();
                map.put("orderNo", data.getPartnerOrderCode());
                List<TradeOrder> tradeOrderList = tradeOrderService.selectByParams(map);
                if (CollectionUtils.isNotEmpty(tradeOrderList)) {
                    carrier.setOrderId(tradeOrderList.get(0).getId());
                }
                MQMessage message = new MQMessage("topic_order_carrier_msg", (Serializable) carrier);
                //加一个key 订单id+状态,没有实际意义,方便查询定位错误
                message.setKey(carrier.getOrderNo());
                rocketMQProducer.sendMessage(message);
                break;
            case 80:
            case 2:
            case 3:
                TradeOrder tradeOrder = tradeOrderService.findByOrderNo(data.getPartnerOrderCode());
                if (tradeOrder.getStatus() == OrderStatusEnum.DROPSHIPPING) {
                    TradeOrderOperateParamVo param = new TradeOrderOperateParamVo();
                    // 发货的订单ID
                    param.setOrderId(tradeOrder.getId());
                    // 根据店铺ID查询店主ID
                    String bossId = storeInfoServiceApi.getBossIdByStoreId(tradeOrder.getStoreId());
                    // 操作人ID
                    param.setUserId(bossId);
                    // 店铺ID
                    param.setStoreId(tradeOrder.getStoreId());
                    tradeOrderService.updateOrderShipment(param);
                }
                break;
            case 5:
                try {
                    TradeOrder order = tradeOrderService.findByOrderNo(data.getPartnerOrderCode());
                    StoreInfo storeInfo = storeInfoServiceApi.findById(order.getStoreId());
                    StringBuilder str = new StringBuilder();
                    str.append(order.getOrderNo()).append("(订单号)配送异常，您可以重新选择自行配送或取消订单");
                    SmsVO smsVo = new SmsVO();
                    smsVo.setId(UuidUtils.getUuid());
                    smsVo.setUserId(order.getSellerId());
                    smsVo.setIsTiming(0);
                    smsVo.setToken(mcmSysToken);
                    smsVo.setSysCode(mcmSysCode);
                    smsVo.setMobile(storeInfo.getMobile());
                    smsVo.setContent(str.toString());
                    smsVo.setSmsChannelType(3);
                    smsVo.setSendTime(DateUtils.getDateTime());
                    smsService.sendSms(smsVo);
                } catch (Exception e) {
                    logger.error("订单号(" + data.getPartnerOrderCode() + ")蜂鸟配送回调发送业务短信异常:{}", e);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 入参检查
     *
     * @param paramDto     ExpressModeParamDto
     * @param resultMsgDto ResultMsgDto
     * @return ResultMsgDto
     */
    private ResultMsgDto<String> checkData(ExpressModeParamDto paramDto, ResultMsgDto<String> resultMsgDto) throws Exception {
        if (null == paramDto.getCommisionRatio()) {
            try {
                //获取店铺佣金方案比例
                StoreInfoExt storeDetailVo = storeInfoExtServiceApi.findByStoreId(paramDto.getStoreId());
                switch (paramDto.getExpressType()) {
                    case 1:
                        paramDto.setCommisionRatio(storeDetailVo.getCommisionRatio());
                        break;
                    case 2:
                        paramDto.setCommisionRatio(storeDetailVo.getCommisionRatioPlanB());
                        break;
                    default:
                        break;
                }
            } catch (ServiceException e) {
                logger.error("查询店铺佣金方案比例异常(店铺" + paramDto.getStoreId() + ")：{}", e);
            }
        }
        if (StringUtils.isBlank(paramDto.getOrderId())) {
            resultMsgDto.setCode(ExpressModeCheckEnum.EXPRESS_ORDER_ID_NULL.getCode());
            resultMsgDto.setMsg(ExpressModeCheckEnum.EXPRESS_ORDER_ID_NULL.getMsg());
        } else if (paramDto.getExpressType() > 2 || paramDto.getExpressType() == 0) {
            resultMsgDto.setCode(ExpressModeCheckEnum.EXPRESS_TYPE_FAIL.getCode());
            resultMsgDto.setMsg(ExpressModeCheckEnum.EXPRESS_TYPE_FAIL.getMsg());
        } else if (StringUtils.isBlank(paramDto.getStoreId())) {
            resultMsgDto.setCode(ExpressModeCheckEnum.STORE_ID_NULL.getCode());
            resultMsgDto.setMsg(ExpressModeCheckEnum.STORE_ID_NULL.getMsg());
        } else if (StringUtils.isBlank(paramDto.getUserId())) {
            resultMsgDto.setCode(ExpressModeCheckEnum.USER_ID_NULL.getCode());
            resultMsgDto.setMsg(ExpressModeCheckEnum.USER_ID_NULL.getMsg());
        } else if (null == paramDto.getCommisionRatio()) {
            resultMsgDto.setCode(ExpressModeCheckEnum.COMMISION_RATIO_NULL.getCode());
            resultMsgDto.setMsg(ExpressModeCheckEnum.COMMISION_RATIO_NULL.getMsg());
        } else {
            //检查订单数据
            checkOrderData(paramDto, resultMsgDto);
        }
        return resultMsgDto;
    }

    /**
     * 订单数据检查
     *
     * @param paramDto     ExpressModeParamDto
     * @param resultMsgDto ResultMsgDto
     */
    private void checkOrderData(ExpressModeParamDto paramDto, ResultMsgDto<String> resultMsgDto) throws Exception {
        //检查订单数据正确性，是否符合配送要求
        TradeOrder tradeOrder = tradeOrderService.selectById(paramDto.getOrderId());
        if (tradeOrder == null) {
            resultMsgDto.setCode(ExpressModeCheckEnum.TRADE_ORDER_NULL.getCode());
            resultMsgDto.setMsg(ExpressModeCheckEnum.TRADE_ORDER_NULL.getMsg());
        } else if (!OrderTypeEnum.PHYSICAL_ORDER.equals(tradeOrder.getType())
                || !PickUpTypeEnum.DELIVERY_DOOR.equals(tradeOrder.getPickUpType())) {
            resultMsgDto.setCode(ExpressModeCheckEnum.ORDER_TYPE_FAIL.getCode());
            resultMsgDto.setMsg(ExpressModeCheckEnum.ORDER_TYPE_FAIL.getMsg());
        } else if (!OrderStatusEnum.DROPSHIPPING.equals(tradeOrder.getStatus())) {
            resultMsgDto.setCode(ExpressModeCheckEnum.ORDER_STATUS_FAIL.getCode());
            resultMsgDto.setMsg(ExpressModeCheckEnum.ORDER_STATUS_FAIL.getMsg());
        } else if (!tradeOrder.getStoreId().equals(paramDto.getStoreId())) {
            resultMsgDto.setCode(ExpressModeCheckEnum.ORDER_STORE_FAIL.getCode());
            resultMsgDto.setMsg(ExpressModeCheckEnum.ORDER_STORE_FAIL.getMsg());
        } else {
            //检查配送数据
            checkExpressData(tradeOrder.getOrderNo(), paramDto.getExpressType(), resultMsgDto);
        }
    }

    /**
     * 配送数据检查
     *
     * @param orderNo      String 订单编号
     * @param expressType  int 前端配送方式类型 1：蜂鸟配送 2：自行配送
     * @param resultMsgDto ResultMsgDto
     */
    private void checkExpressData(String orderNo, int expressType, ResultMsgDto<String> resultMsgDto) throws Exception {
        ExpressCallbackParamDto paramDto = new ExpressCallbackParamDto();
        paramDto.setOrderNo(orderNo);
        List<ExpressCallback> callbackList = expressService.findByParam(paramDto);
        //判断是否已存在第三方配送信息,如果存在，给出相应的提示信息
        if (CollectionUtils.isNotEmpty(callbackList)) {
            switch (expressType) {
                case 1:
                    resultMsgDto.setCode(ExpressModeCheckEnum.EXPRESS_STATUS_FAIL.getCode());
                    resultMsgDto.setMsg(ExpressModeCheckEnum.EXPRESS_STATUS_FAIL.getMsg());
                    break;
                case 2:
                    if (callbackList.get(0).getOrderStatus() != Integer.parseInt(ExpressOrderStatus.STATUS_5.getValue())) {
                        resultMsgDto.setCode(ExpressModeCheckEnum.ORDER_STATUS_FAIL.getCode());
                        resultMsgDto.setMsg(ExpressModeCheckEnum.ORDER_STATUS_FAIL.getMsg());
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
