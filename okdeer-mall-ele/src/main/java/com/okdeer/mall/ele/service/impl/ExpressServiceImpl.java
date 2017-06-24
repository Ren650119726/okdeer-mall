package com.okdeer.mall.ele.service.impl;


import com.google.common.collect.Lists;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.UuidUtils;
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
import com.okdeer.mall.ele.util.ResultMsg;
import com.okdeer.mall.order.dto.TradeOrderExtSnapshotParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderExtSnapshot;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.mapper.TradeOrderExtSnapshotMapper;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import net.sf.json.JSONObject;
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
     * 订单查询mapper
     */
    @Autowired
    private TradeOrderMapper tradeOrderMapper;

    /**
     * 订单项查询
     */
    @Autowired
    private TradeOrderItemMapper tradeOrderItemMapper;

    /**
     * 订单扩展信息快照
     */
    @Autowired
    private TradeOrderExtSnapshotMapper tradeOrderExtSnapshotMapper;

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
    public ResultMsg saveExpressOrder(String orderId) throws Exception {
        // 1、获取商城订单信息

        //根据订单id查询订单基本信息
        TradeOrder tradeOrder = tradeOrderMapper.selectByPrimaryKey(orderId);
        //根据订单id查询订单项信息
        List<TradeOrderItem> orderItemList = tradeOrderItemMapper.selectOrderItemListById(orderId);
        tradeOrder.setTradeOrderItem(orderItemList);

        // 2、封装推送入参
        String pushJson = createPushObject(tradeOrder);

        // 3、推送订单
        String url = ElemeOpenConfig.API_URL + RequestConstant.orderCreate;
        String resultJson = HttpClient.postBody(url, pushJson);

        ResultMsg resultMsg = (ResultMsg) JSONObject.toBean(JSONObject.fromObject(resultJson), ResultMsg.class);

        // 4、保存推送日志
        ExpressPushLog param = new ExpressPushLog();
        param.setPushJson(pushJson);
        param.setResultJson(resultJson);
        savePushLog(tradeOrder, param);

        // 5、初始化回调数据信息，默认状态为200，推单成功才保存信息记录
        if (resultMsg.getCode() == 200) {
            ExpressCallback data = new ExpressCallback();
            data.setId(UuidUtils.getUuid());
            data.setPartnerOrderCode(tradeOrder.getId());
            data.setOrderStatus(resultMsg.getCode());
            data.setPushTime(DateUtils.getSysDate());
            expressCallbackMapper.insert(data);
        }
        return resultMsg;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveCallback(ExpressCallback data) throws Exception {
        //保存第三方回调信息
        expressCallbackMapper.update(data);
        //保存回调日志
        ExpressCallbackLog callbackLog = new ExpressCallbackLog();
        callbackLog.setId(UuidUtils.getUuid());
        callbackLog.setCreateTime(DateUtils.getSysDate());
        callbackLog.setOpenOrderCode(data.getOpenOrderCode());
        callbackLog.setPartnerOrderCode(data.getPartnerOrderCode());
        callbackLog.setCallbackJson(JsonMapper.nonDefaultMapper().toJson(data));
        expressCallbackLogMapper.insert(callbackLog);
    }


    /**
     * 封装推送入参
     *
     * @param tradeOrder TradeOrder
     * @return String
     */
    private String createPushObject(TradeOrder tradeOrder) throws Exception {
        ExpressRequestJson<ExpressOrderData> requestJson = new ExpressRequestJson<ExpressOrderData>();

        // 1、封装data数据（订单信息数据）
        ExpressOrderData data = createExpressOrderData(tradeOrder);
        requestJson.setData(data);

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
        //查询订单扩展信息快照
        TradeOrderExtSnapshotParamDto paramDto = new TradeOrderExtSnapshotParamDto();
        paramDto.setOrderId(tradeOrder.getId());
        TradeOrderExtSnapshot entity = tradeOrderExtSnapshotMapper.selectExtSnapshotByParam(paramDto);
        data.setTransport_info(createExpressTransport(tradeOrder, entity));
        // 3、设置收货人信息
        data.setReceiver_info(createExpressReceiver(tradeOrder, entity));
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
     * @param entity     TradeOrderExtSnapshot
     * @return ExpressTransportInfo
     */
    private ExpressTransportInfo createExpressTransport(TradeOrder tradeOrder, TradeOrderExtSnapshot entity) {
        ExpressTransportInfo data = new ExpressTransportInfo();
        data.setTransport_name(entity.getTransportName());
        data.setTransport_address(entity.getTransportAddress());
        data.setTransport_longitude(new BigDecimal(entity.getTransportLongitude()));
        data.setTransport_latitude(new BigDecimal(entity.getTransportLatitude()));
        data.setTransport_tel(entity.getTransportTel());
        return data;
    }

    /**
     * 封装订单收货人数据
     *
     * @param tradeOrder TradeOrder
     * @param entity     TradeOrderExtSnapshot
     * @return ExpressReceiverInfo
     */
    private ExpressReceiverInfo createExpressReceiver(TradeOrder tradeOrder, TradeOrderExtSnapshot entity) {
        ExpressReceiverInfo data = new ExpressReceiverInfo();
        data.setReceiver_name(entity.getReceiverName());
        data.setReceiver_primary_phone(entity.getReceiverPrimaryPhone());
        data.setReceiver_address(entity.getReceiverAddress());
        data.setReceiver_longitude(new BigDecimal(entity.getReceiverLongitude()));
        data.setReceiver_latitude(new BigDecimal(entity.getReceiverLatitude()));
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
        param.setOrderId(tradeOrder.getId());
        param.setOrderNo(tradeOrder.getOrderNo());
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
