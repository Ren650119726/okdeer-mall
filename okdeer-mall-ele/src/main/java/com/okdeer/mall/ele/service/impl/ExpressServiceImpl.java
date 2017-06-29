package com.okdeer.mall.ele.service.impl;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.mall.ele.config.ElemeOpenConfig;
import com.okdeer.mall.ele.config.RequestConstant;
import com.okdeer.mall.ele.entity.*;
import com.okdeer.mall.ele.mapper.ExpressCallbackLogMapper;
import com.okdeer.mall.ele.mapper.ExpressCallbackMapper;
import com.okdeer.mall.ele.mapper.ExpressPushLogMapper;
import com.okdeer.mall.ele.response.TokenResponse;
import com.okdeer.mall.ele.service.ExpressService;
import com.okdeer.mall.ele.sign.OpenSignHelper;
import com.okdeer.mall.ele.util.HttpClient;
import com.okdeer.mall.ele.util.JsonUtils;
import com.okdeer.mall.ele.util.RandomUtils;
import com.okdeer.mall.ele.util.URLUtils;
import com.okdeer.mall.express.dto.ExpressCallbackParamDto;
import com.okdeer.mall.express.dto.ExpressCarrierDto;
import com.okdeer.mall.express.dto.ExpressOrderStatus;
import com.okdeer.mall.express.dto.ResultMsgDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName: EleServiceImpl
 *
 * @author wangf01
 * @Description: ele蜂鸟配送-service-impl
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
@Service
public class ExpressServiceImpl implements ExpressService {

    @Value("${ele.appId}")
    private String appId;

    @Value("${ele.secretKey}")
    private String secretKey;

    @Value("${ele.notifyUrl}")
    private String notifyUrl;

    /**
     * 推送订单日志
     */
    @Autowired
    private ExpressPushLogMapper expressPushLogMapper;

    /**
     * 回调信息处理
     */
    @Autowired
    private ExpressCallbackMapper expressCallbackMapper;

    /**
     * 回调信息日志保存
     */
    @Autowired
    private ExpressCallbackLogMapper expressCallbackLogMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultMsgDto<String> saveExpressOrder(TradeOrder tradeOrder) throws Exception {
        // 1、封装推送入参
        ExpressOrderData orderData = createExpressOrderData(tradeOrder);
        String pushJson = createPushObject(orderData);

        // 2、推送订单
        String url = ElemeOpenConfig.API_URL + RequestConstant.orderCreate;
        String resultJson = HttpClient.postBody(url, pushJson);

        ResultMsgDto<String> resultMsg = JsonMapper.nonDefaultMapper().fromJson(resultJson, ResultMsgDto.class);

        // 3、保存推送日志
        ExpressPushLog param = new ExpressPushLog();
        param.setPushJson(pushJson);
        param.setResultJson(resultJson);
        savePushLog(tradeOrder, param);

        // 4、初始化回调数据信息，默认状态为200，推单成功才保存信息记录
        if (resultMsg.getCode() == 200) {
            ExpressCallback data = new ExpressCallback();
            data.setId(UuidUtils.getUuid());
            data.setPartnerOrderCode(tradeOrder.getOrderNo());
            data.setOrderStatus(resultMsg.getCode());
            data.setPushTime(DateUtils.getSysDate());
            expressCallbackMapper.insert(data);
        }
        return resultMsg;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultMsgDto<String> cancelExpressOrder(String orderNo) throws Exception {
        // 1、封装推送入参
        ExpressCancelData cancelData = new ExpressCancelData();
        cancelData.setPartner_order_code(orderNo);
        String resultJson = requestHttpData(RequestConstant.orderCancel, cancelData);
        ResultMsgDto<String> resultMsg = JsonMapper.nonDefaultMapper().fromJson(resultJson, ResultMsgDto.class);

        // 2、保存推送日志
        ExpressPushLog param = new ExpressPushLog();
        String pushJson = createPushObject(cancelData);
        param.setPushJson(pushJson);
        param.setResultJson(resultJson);
        TradeOrder tradeOrder = new TradeOrder();
        tradeOrder.setOrderNo(orderNo);
        savePushLog(tradeOrder, param);
        return resultMsg;
    }

    @Override
    public List<ExpressCallback> findByParam(ExpressCallbackParamDto paramDto) throws Exception {
        return expressCallbackMapper.selectExpressCallbackByParamDto(paramDto);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveCallback(ExpressCallback data) throws Exception {
        Integer temp_orderStatus = data.getOrderStatus();
        //保存第三方回调信息
        if (data.getOrderStatus() != null) {
            //判断当前状态和回调的状态对比，如果当前状态大于回调状态，则不修改状态值字段
            ExpressCallback param = new ExpressCallback();
            param.setPartnerOrderCode(data.getPartnerOrderCode());
            ExpressCallback callback = expressCallbackMapper.selectExpressCallbackByParam(param);
            int data_index = ExpressOrderStatus.enumValueOf(String.valueOf(data.getOrderStatus())).ordinal();
            int callback_index = ExpressOrderStatus.enumValueOf(String.valueOf(callback.getOrderStatus())).ordinal();
            if (callback_index > data_index) {
                data.setOrderStatus(callback.getOrderStatus());
            }
        }
        expressCallbackMapper.update(data);
        data.setOrderStatus(temp_orderStatus);
        //保存回调日志
        ExpressCallbackLog callbackLog = new ExpressCallbackLog();
        callbackLog.setId(UuidUtils.getUuid());
        callbackLog.setCreateTime(data.getPushTime());
        callbackLog.setOpenOrderCode(data.getOpenOrderCode());
        callbackLog.setPartnerOrderCode(data.getPartnerOrderCode());
        callbackLog.setCallbackJson(JsonMapper.nonDefaultMapper().toJson(data));
        expressCallbackLogMapper.insert(callbackLog);
    }

    @Override
    public ResultMsgDto<ExpressOrderInfo> findExpressOrderInfo(String orderNo) throws Exception {
        Map<String, String> data = Maps.newHashMap();
        data.put("partner_order_code", orderNo);
        String resultJson = requestHttpData(RequestConstant.orderQuery, data);
        ResultMsgDto<ExpressOrderInfo> resultMsgDto = JsonMapper.nonDefaultMapper().fromJson(resultJson, ResultMsgDto.class);
        resultMsgDto.setData(BeanMapper.map(resultMsgDto.getData(), ExpressOrderInfo.class));
        return resultMsgDto;
    }

    @Override
    public ResultMsgDto<ExpressCarrierDto> findExpressCarrier(String orderNo) throws Exception {
        Map<String, String> data = Maps.newHashMap();
        data.put("partner_order_code", orderNo);
        String resultJson = requestHttpData(RequestConstant.orderCarrier, data);
        ResultMsgDto<ExpressCarrierDto> resultMsgDto = JsonMapper.nonDefaultMapper().fromJson(resultJson, ResultMsgDto.class);
        resultMsgDto.setData(BeanMapper.map(resultMsgDto.getData(), ExpressCarrierDto.class));
        return resultMsgDto;
    }

    /**
     * 请求http数据
     *
     * @param requestConstant RequestConstant常量类方法名
     * @param data            Object
     * @return String
     * @throws Exception
     */
    private String requestHttpData(String requestConstant, Object data) throws Exception {
        String url = ElemeOpenConfig.API_URL + requestConstant;
        String pushJson = createPushObject(data);
        String resultJson = HttpClient.postBody(url, pushJson);
        return resultJson;
    }


    /**
     * 封装推送入参
     *
     * @param data Object
     * @return String
     */
    private String createPushObject(Object data) throws Exception {
        ExpressRequestJson<String> requestJson = new ExpressRequestJson<String>();

        // 1、封装data数据（订单信息数据）
        //注:其中data的值需要用UTF-8进行urlEncode
        requestJson.setData(URLUtils.getInstance().urlEncode(JsonMapper.nonDefaultMapper().toJson(data)));

        // 2、封装data之外的参数
        requestJson.setApp_id(appId);
        setSigStr(requestJson);

        return JsonMapper.nonDefaultMapper().toJson(requestJson);
    }

    /**
     * 封装推单data数据
     *
     * @param tradeOrder TradeOrder
     * @return ExpressOrderData
     */
    private ExpressOrderData createExpressOrderData(TradeOrder tradeOrder) {
        ExpressOrderData data = new ExpressOrderData();
        // 1、设置基本信息
        createExpreOrderBaseData(data, tradeOrder);
        // 2、设置门店信息
        data.setTransport_info(createExpressTransport(tradeOrder));
        // 3、设置收货人信息
        data.setReceiver_info(createExpressReceiver(tradeOrder));
        // 4、设置订单项信息
        data.setItems_json(createExpressOrderItem(tradeOrder));
        return data;
    }

    /**
     * 设置推送订单基本数据
     *
     * @param data       ExpressOrderData
     * @param tradeOrder TradeOrder
     */
    private void createExpreOrderBaseData(ExpressOrderData data, TradeOrder tradeOrder) {
        data.setPartner_order_code(tradeOrder.getOrderNo());
        data.setNotify_url(notifyUrl);
        data.setOrder_total_amount(tradeOrder.getTotalAmount());
        data.setOrder_actual_amount(tradeOrder.getActualAmount());
        data.setGoods_count(tradeOrder.getTradeOrderItem().size());
    }

    /**
     * 封装订单门店地址数据
     *
     * @param tradeOrder TradeOrder
     * @return ExpressTransportInfo
     */
    private ExpressTransportInfo createExpressTransport(TradeOrder tradeOrder) {
        ExpressTransportInfo data = new ExpressTransportInfo();
        data.setTransport_name(tradeOrder.getTradeOrderExt().getTransportName());
        data.setTransport_address(tradeOrder.getTradeOrderExt().getTransportAddress());
        data.setTransport_longitude(new BigDecimal(tradeOrder.getTradeOrderExt().getTransportLongitude()));
        data.setTransport_latitude(new BigDecimal(tradeOrder.getTradeOrderExt().getTransportLatitude()));
        data.setTransport_tel(tradeOrder.getTradeOrderExt().getTransportTel());
        return data;
    }

    /**
     * 封装订单收货人数据
     *
     * @param tradeOrder TradeOrder
     * @return ExpressReceiverInfo
     */
    private ExpressReceiverInfo createExpressReceiver(TradeOrder tradeOrder) {
        ExpressReceiverInfo data = new ExpressReceiverInfo();
        data.setReceiver_name(tradeOrder.getTradeOrderExt().getReceiverName());
        data.setReceiver_primary_phone(tradeOrder.getTradeOrderExt().getReceiverPrimaryPhone());
        data.setReceiver_address(tradeOrder.getTradeOrderExt().getReceiverAddress());
        data.setReceiver_longitude(new BigDecimal(tradeOrder.getTradeOrderExt().getReceiverLongitude()));
        data.setReceiver_latitude(new BigDecimal(tradeOrder.getTradeOrderExt().getReceiverLatitude()));
        return data;
    }

    /**
     * 封装订单项数据
     *
     * @param tradeOrder TradeOrder
     * @return List<ExpressOrderItem>
     */
    private List<ExpressOrderItem> createExpressOrderItem(TradeOrder tradeOrder) {
        List<ExpressOrderItem> list = Lists.newArrayList();
        List<TradeOrderItem> orderItemList = tradeOrder.getTradeOrderItem();
        orderItemList.forEach(e -> {
            ExpressOrderItem data = new ExpressOrderItem();
            data.setItem_name(e.getSkuName());
            data.setItem_quantity(e.getQuantity());
            data.setItem_price(e.getUnitPrice());
            data.setItem_actual_price(e.getActualAmount());
            list.add(data);
        });
        return list;
    }

    /**
     * 保存推送订单日志
     *
     * @param tradeOrder TradeOrder
     * @param param      ExpressPushLog
     */
    private void savePushLog(TradeOrder tradeOrder, ExpressPushLog param) {
        param.setId(UuidUtils.getUuid());
        if (tradeOrder.getId() != null) {
            param.setOrderId(tradeOrder.getId());
        }
        if (tradeOrder.getOrderNo() != null) {
            param.setOrderNo(tradeOrder.getOrderNo());
        }
        param.setCreateTime(DateUtils.getSysDate());
        expressPushLogMapper.insert(param);
    }

    /**
     * 获取token
     *
     * @return String
     * @throws Exception
     */
    private String getToken() throws Exception {
        String url = ElemeOpenConfig.API_URL + RequestConstant.obtainToken;

        List<BasicNameValuePair> params = new ArrayList<>();
        String salt = String.valueOf(RandomUtils.getInstance().generateValue(1000, 10000));
        String sig = OpenSignHelper.generateSign(appId, salt, secretKey);

        // 请求token
        List<BasicNameValuePair> paramsToken = new ArrayList<>();
        paramsToken.add(new BasicNameValuePair("app_id", ElemeOpenConfig.appId));
        paramsToken.add(new BasicNameValuePair("salt", salt));
        paramsToken.add(new BasicNameValuePair("signature", sig));
        String tokenRes = "";
        try {
            tokenRes = HttpClient.get(url, paramsToken);
        } catch (Exception e) {
            throw new Exception("请求token出现异常", e);
        }
        /**
         * 生成token
         */
        String token = JsonUtils.getInstance().readValue(tokenRes, TokenResponse.class).getData().getAccess_token();
        return token;
    }

    /**
     * 生成签名
     *
     * @param requestJson ExpressRequestJson
     */
    private void setSigStr(ExpressRequestJson requestJson) throws Exception {
        Map<String, Object> sigStr = new LinkedHashMap<>();      // 注意添加的顺序, 应该如下面一样各个key值顺序一致
        sigStr.put("app_id", ElemeOpenConfig.appId);
        sigStr.put("access_token", getToken());        // 需要使用前面请求生成的token
        sigStr.put("data", requestJson.getData());
        int salt = RandomUtils.getInstance().generateValue(1000, 10000);
        requestJson.setSalt(salt);
        sigStr.put("salt", salt);
        // 生成签名
        String sig = OpenSignHelper.generateBusinessSign(sigStr);
        requestJson.setSignature(sig);
    }
}
