/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: TradeOrderCommentServiceImpl.java 
 * @Date: 2016年1月30日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */

package com.okdeer.mall.order.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.client.producer.TransactionCheckListener;
import com.alibaba.rocketmq.client.producer.TransactionSendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.RocketMQTransactionProducer;
import com.okdeer.base.framework.mq.RocketMqResult;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.common.consts.PointConstants;
import com.okdeer.mall.member.points.dto.AddPointsParamDto;
import com.okdeer.mall.member.points.enums.PointsRuleCode;
import com.okdeer.mall.member.points.service.PointsApi;
import com.okdeer.mall.order.constant.OrderTraceConstant;
import com.okdeer.mall.order.constant.mq.OrderMessageConstant;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderComment;
import com.okdeer.mall.order.entity.TradeOrderCommentImage;
import com.okdeer.mall.order.entity.TradeOrderTrace;
import com.okdeer.mall.order.enums.ConsumerCodeStatusEnum;
import com.okdeer.mall.order.enums.OrderTraceEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.mapper.TradeOrderCommentImageMapper;
import com.okdeer.mall.order.mapper.TradeOrderCommentMapper;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.service.TradeOrderCommentService;
import com.okdeer.mall.order.service.TradeOrderCommentServiceApi;
import com.okdeer.mall.order.service.TradeOrderTraceService;
import com.okdeer.mall.order.vo.TradeOrderCommentVo;
import com.okdeer.mall.system.service.SysBuyerUserService;

import net.sf.json.JSONObject;

/**
 * 商品评论实现类
 * @project yschome-mall
 * @author zhongy
 * @date 2016年1月30日 上午10:59:07
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderCommentServiceApi")
public class TradeOrderCommentServiceImpl implements TradeOrderCommentService, TradeOrderCommentServiceApi,
		OrderMessageConstant {

	private static final Logger logger = LoggerFactory.getLogger(TradeOrderCommentServiceImpl.class);

	/**
	 * 自动注入商品评论dao
	 */
	@Autowired
	private TradeOrderCommentMapper tradeOrderCommentMapper;

	/**
	 * 自动注入商品评论图片dao
	 */
	@Autowired
	private TradeOrderCommentImageMapper tradeOrderCommentImageMapper;

	/**
	 * 店铺信息Mapper注入
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoService;

	/**
	 * 买家用户Mapper注入
	 */
	// @Autowired
	// private SysBuyerUserMapper sysBuyerUserMapper;
	/**
	 * 买家用户service注入
	 */
	@Autowired
	SysBuyerUserService sysBuyerUserService;

	/**
	 * 自动注入订单项
	 */
	@Autowired
	private TradeOrderItemMapper tradeOrderItemMapper;

	@Autowired
	private RocketMQTransactionProducer rocketMQTransactionProducer;
	
	@Autowired
	private RocketMQProducer rocketMQProducer;

	/**
	 * 自动注入订单项
	 */
	@Autowired
	private TradeOrderMapper tradeOrderMapper;
	
	@Resource
	private TradeOrderTraceService tradeOrderTraceService;

	@Override
	public TradeOrderCommentVo findById(String id) throws ServiceException {
		return tradeOrderCommentMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<TradeOrderCommentVo> findListByOrderId(String orderId) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("orderId", orderId);
		List<TradeOrderCommentVo> list = tradeOrderCommentMapper.selectByParams(params);
		return list;
	}

	@Override
	public List<TradeOrderCommentVo> findByParams(TradeOrderCommentVo vo) throws ServiceException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("storeSkuId", vo.getStoreSkuId());
		// 1 查询商品评论
		List<TradeOrderCommentVo> list = tradeOrderCommentMapper.selectCommentByParams(param);
		// for (TradeOrderCommentVo tradeOrderCommentVo : list) {
		// //2 查询商品评论图片
		// List<TradeOrderCommentImage> commentImages =
		// tradeOrderCommentImageMapper.selectByCommentId(tradeOrderCommentVo.getId());
		// tradeOrderCommentVo.setImagePaths(commentImages);
		// }
		return list;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void add(TradeOrderCommentVo vo) throws ServiceException {
		// 1 添加商品评论
		String id = UuidUtils.getUuid();
		vo.setId(id);
		addTradeOrderComment(vo);
		// 2 添加商品评论图片
		addTradeOrderCommentImage(id, vo);
	}

	// 添加商品评论图片
	@Transactional(rollbackFor = Exception.class)
	private void addTradeOrderCommentImage(String commentId, TradeOrderCommentVo vo) throws ServiceException {
		List<TradeOrderCommentImage> imagePaths = vo.getImagePaths();
		List<TradeOrderCommentImage> tradeOrderCommentImageList = new ArrayList<>();
		for (TradeOrderCommentImage iamgePath : imagePaths) {
			TradeOrderCommentImage commentImage = new TradeOrderCommentImage();
			commentImage.setId(UuidUtils.getUuid());
			commentImage.setCommentId(commentId);
			commentImage.setImagePath(iamgePath.getImagePath());
			// tradeOrderCommentImageMapper.insert(commentImage);
			tradeOrderCommentImageList.add(commentImage);
		}
		tradeOrderCommentImageMapper.insertByBatch(tradeOrderCommentImageList);
	}

	// 添加商品评论
	@Transactional(rollbackFor = Exception.class)
	private void addTradeOrderComment(TradeOrderCommentVo vo) throws ServiceException {
		TradeOrderComment tradeOrderComment = new TradeOrderComment();
		tradeOrderComment.setSkuId(vo.getSkuId());
		tradeOrderComment.setUserId(vo.getUserId());
		tradeOrderComment.setUserName(vo.getUserName());
		tradeOrderComment.setOrderId(vo.getOrderId());
		tradeOrderComment.setOrderItemId(vo.getOrderItemId());
		tradeOrderComment.setContent(vo.getContent());
		tradeOrderComment.setStoreSkuId(vo.getStoreSkuId());
		tradeOrderComment.setStoreSpuId(vo.getStoreSpuId());
		tradeOrderComment.setCreateTime(new Date());
		if (vo.getStar() != null) {
			tradeOrderComment.setStar(vo.getStar());
		}
		tradeOrderComment.setStatus(WhetherEnum.whether);
		tradeOrderCommentMapper.insert(tradeOrderComment);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteByPrimaryKey(String id) throws ServiceException {
		tradeOrderCommentMapper.deleteByPrimaryKey(id);
		tradeOrderCommentImageMapper.deleteByCommentId(id);
	}

	@Override
	public PageUtils<TradeOrderCommentVo> getByStoreSkuId(TradeOrderComment tradeOrderComment, int pageNumber,
			int pageSize) throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradeOrderCommentVo> voList = tradeOrderCommentMapper.selectByStoreSkuId(tradeOrderComment);
		PageUtils<TradeOrderCommentVo> page = new PageUtils<TradeOrderCommentVo>(voList);
		return page;
	}

	@Override
	public Integer getSkuCommentCount(String skuId) throws ServiceException {
		return tradeOrderCommentMapper.selectSkuCommentCount(skuId);
	}

	@Override
	public List<TradeOrderCommentVo> findOrderCommentByOrderId(String orderId) throws ServiceException {
		return tradeOrderCommentMapper.selectOrderCommentByOrderId(orderId);
	}

	private StoreInfo getStoreInfo(String storeId) throws Exception {
		return storeInfoService.selectStoreBaseInfoById(storeId);
	}

	/**
	 * 根据店铺类型获取TOPIC
	 *
	 * @param storeType
	 * @return
	 */
	private String getTopicByStoreType(StoreTypeEnum storeType) {
		switch (storeType) {
			case AROUND_STORE:
				return TOPIC_ORDER_AROUND;
			case FAST_DELIVERY_STORE:
				return TOPIC_ORDER_FAST;
			case CLOUD_STORE:
				return TOPIC_ORDER_CLOUD;
			case ACTIVITY_STORE:
				return TOPIC_ORDER_ACTIVITY;
			case SERVICE_STORE:
				return TOPIC_ORDER_SERVICE;
			default:
				break;
		}
		return null;
	}

	/**
	 * @desc 用户评价
	 *
	 * @param tradeOrder
	 * @return
	 * @throws MQClientException
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateUserEvaluate(List<TradeOrderCommentVo> tradeOrderCommentVoList) throws Exception {

		TradeOrderCommentVo TradeOrderCommentVo = tradeOrderCommentVoList.get(0);
		final TradeOrder tradeOrder = tradeOrderMapper.selectByPrimaryKey(TradeOrderCommentVo.getOrderId());
		JSONObject json = new JSONObject();
		json.put("id", tradeOrder.getId());
		json.put("operator", TradeOrderCommentVo.getUserId());
		json.put("isBuyerOperator", true);
		StoreInfo storeInfo = getStoreInfo(tradeOrder.getStoreId());
		Message msg = new Message(getTopicByStoreType(storeInfo.getType()), TAG_ORDER_EVALUATE, json.toString()
				.getBytes(Charsets.UTF_8));
		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, tradeOrderCommentVoList,
				new LocalTransactionExecuter() {

					@Override
					public LocalTransactionState executeLocalTransactionBranch(Message msg,
							Object tradeOrderCommentVoList) {
						// todo 执行本地业务
						try {
							addCommentByBatch((List<TradeOrderCommentVo>) tradeOrderCommentVoList);
							
							//begin add by zengjz  到店消费评价增加逻辑
							//到店消费订单需要更改消费码的状态，更改为已经完成
							if (tradeOrder.getType() == OrderTypeEnum.STORE_CONSUME_ORDER) {
								TradeOrder tempTradeOrder = new TradeOrder();
								tempTradeOrder.setId(tradeOrder.getId());
								tempTradeOrder.setConsumerCodeStatus(ConsumerCodeStatusEnum.COMPLETED);
								tradeOrderMapper.updateByPrimaryKeySelective(tempTradeOrder);
							}
							//end add by zengjz  到店消费评价增加逻辑
							
							// Begin V1.2 added by maojj 2016-11-21
							// 上门服务商品需要修改轨迹信息
							if (tradeOrder.getType() == OrderTypeEnum.SERVICE_STORE_ORDER) {
								TradeOrderTrace tradeTrace = new TradeOrderTrace();
								tradeTrace.setOrderId(tradeOrder.getId());
								tradeTrace.setTraceStatus(OrderTraceEnum.COMPLETED);
								tradeTrace.setRemark(OrderTraceConstant.COMPLETED_APPRAISE_REMARK);
								tradeOrderTraceService.updateRemarkAfterAppraise(tradeTrace);
							}
							// End V1.2 added by maojj 2016-11-21
							
							// Begin V2.0 added by chenzc 2017-1-10
							// 评论成功后并且评论字数大于10个字则添加积分
							List<TradeOrderCommentVo> commentList = (List<TradeOrderCommentVo>)tradeOrderCommentVoList;
							if (commentList.get(0).getContent().length() >= 10) {
								AddPointsParamDto addPointsParamDto = new AddPointsParamDto();
								addPointsParamDto.setPointsRuleCode(PointsRuleCode.GOODS_EVALUATE);
								addPointsParamDto.setUserId(commentList.get(0).getUserId());
								addPointsParamDto.setBusinessId(UuidUtils.getUuid());
								MQMessage anMessage = new MQMessage(PointConstants.POINT_TOPIC, (Serializable) addPointsParamDto);
								rocketMQProducer.sendMessage(anMessage);
							}
							// End V2.0 added by chenzc 2017-1-10
						} catch (Exception e) {
							logger.error("提交评价异常", e.getMessage());
							return LocalTransactionState.ROLLBACK_MESSAGE;
						}
						return LocalTransactionState.COMMIT_MESSAGE;
					}
				}, new TransactionCheckListener() {

					@Override
					public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
						return LocalTransactionState.COMMIT_MESSAGE;
					}
				});
		return RocketMqResult.returnResult(sendResult);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addCommentByBatch(List<TradeOrderCommentVo> tradeOrderCommentVoList) throws ServiceException {
		List<TradeOrderComment> tradeOrderCommentList = new ArrayList<>();
		List<TradeOrderCommentImage> tradeOrderCommentImageList = new ArrayList<>();
		// begin 跨模块调用，改为dubbo update by wushp
		// SysBuyerUser sysBuyerUser =
		// sysBuyerUserMapper.selectByPrimaryKey(tradeOrderCommentVoList.get(0).getUserId());
		SysBuyerUser sysBuyerUser = sysBuyerUserService.findByPrimaryKey(tradeOrderCommentVoList.get(0).getUserId());
		// end update by wushp
		for (TradeOrderCommentVo tradeOrderCommentVo : tradeOrderCommentVoList) {
			TradeOrderComment tradeOrderComment = new TradeOrderComment();
			String id = UuidUtils.getUuid();
			tradeOrderComment.setId(id);
			tradeOrderComment.setContent(tradeOrderCommentVo.getContent());
			tradeOrderComment.setSkuId(tradeOrderCommentVo.getSkuId());
			tradeOrderComment.setUserId(tradeOrderCommentVo.getUserId());
			tradeOrderComment.setUserName(sysBuyerUser.getLoginName());
			tradeOrderComment.setOrderId(tradeOrderCommentVo.getOrderId());
			tradeOrderComment.setOrderItemId(tradeOrderCommentVo.getOrderItemId());
			tradeOrderComment.setStoreSkuId(tradeOrderCommentVo.getStoreSkuId());
			tradeOrderComment.setStoreSpuId(tradeOrderCommentVo.getStoreSpuId());
			tradeOrderComment.setCreateTime(new Date());
			tradeOrderComment.setStatus(WhetherEnum.whether);
			tradeOrderCommentList.add(tradeOrderComment);
			String[] arrayImagePaths = tradeOrderCommentVo.getArrayImagePaths();
			if (arrayImagePaths != null && arrayImagePaths.length > 0) {
				for (int i = 0; i < arrayImagePaths.length; i++) {
					String imagePath = arrayImagePaths[i];
					TradeOrderCommentImage tradeOrderImage = new TradeOrderCommentImage();
					tradeOrderImage.setCommentId(id);
					tradeOrderImage.setId(UuidUtils.getUuid());
					tradeOrderImage.setImagePath(imagePath);
					tradeOrderCommentImageList.add(tradeOrderImage);
				}
			}
		}
		tradeOrderCommentMapper.insertByBatch(tradeOrderCommentList);
		tradeOrderItemMapper.updateByOrderId(tradeOrderCommentVoList.get(0).getOrderId());
		if (tradeOrderCommentImageList.size() > 0) {
			tradeOrderCommentImageMapper.insertByBatch(tradeOrderCommentImageList);
		}
	}

	/*
	 * @Override public void addByBatch(List<TradeOrderCommentImage> tradeOrderCommentImageList) throws ServiceException
	 * { tradeOrderCommentImageMapper.insertByBatch(tradeOrderCommentImageList); }
	 * 
	 * @Override public void addComment(TradeOrderComment tradeOrderComment) throws ServiceException {
	 * tradeOrderCommentMapper.insert(tradeOrderComment);
	 * 
	 * }
	 */

}
