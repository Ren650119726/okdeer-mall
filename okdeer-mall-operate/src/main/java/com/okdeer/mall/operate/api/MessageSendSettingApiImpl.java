/** 
 *@Project: okdeer-mall-operate 
 *@Author: xuzq01
 *@Date: 2017年8月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.api;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.member.member.dto.LocateInfoQueryDto;
import com.okdeer.mall.member.member.dto.SysBuyerLocateInfoDto;
import com.okdeer.mall.member.member.service.SysBuyerLocateInfoServiceApi;
import com.okdeer.mall.operate.dto.MessageSendSettingDto;
import com.okdeer.mall.operate.dto.MessageSendSettingQueryDto;
import com.okdeer.mall.operate.entity.MessageSendSelectArea;
import com.okdeer.mall.operate.entity.MessageSendSetting;
import com.okdeer.mall.operate.service.MessageSendSelectAreaService;
import com.okdeer.mall.operate.service.MessageSendSettingApi;
import com.okdeer.mall.operate.service.MessageSendSettingService;
import com.okdeer.mall.order.constant.OrderMsgConstant;
import com.okdeer.mall.order.vo.PushMsgVo;
import com.okdeer.mall.order.vo.PushUserVo;
import com.okdeer.mcm.constant.MsgConstant;


/**
 * ClassName: MessageSendSettingApiImpl 
 * @Description: app消息推送设置api实现类
 * @author xuzq01
 * @date 2017年8月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version = "1.0.0")
public class MessageSendSettingApiImpl implements MessageSendSettingApi {
	
	//private static final Logger LOGGER = LoggerFactory.getLogger(MessageSendSettingApiImpl.class);

	@Autowired
	private MessageSendSettingService messageSendSettingService;

	@Autowired
	private MessageSendSelectAreaService messageSendSelectAreaService;
    
    /**
	 * 用户定位信息api
	 */
	@Reference(version = "1.0.0", check = false)
	private SysBuyerLocateInfoServiceApi sysBuyerLocateInfoApi;
    
    @Value("${mcm.sys.code}")
    private String msgSysCode;

    @Value("${mcm.sys.token}")
    private String msgToken;
    /**
     * 消息主题
     */
    private static final String TOPIC = "topic_mcm_msg";
    
	@Autowired
	private RocketMQProducer rocketMQProducer;
	@SuppressWarnings("unchecked")
	@Override
	public PageUtils<MessageSendSettingDto> findPageList(MessageSendSettingQueryDto paramDto, int pageNumber,
			int pageSize) {
		return messageSendSettingService.findPageList(paramDto, pageNumber, pageSize).toBean(MessageSendSettingDto.class);
	}

	@Override
	public MessageSendSettingDto findById(String id) throws Exception {
		return BeanMapper.map(messageSendSettingService.findById(id),MessageSendSettingDto.class);
	}

	@Override
	public int findCountByName(String messageName) {
		return messageSendSettingService.findCountByName(messageName);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateMessageSend(MessageSendSettingDto messageDto) throws Exception {
		MessageSendSetting entity = BeanMapper.map(messageDto, MessageSendSetting.class);
		
		entity.setUpdateTime(new Date());
		if(messageDto.getSendTimeType()==0){
			entity.setSendTime(new Date());
		}
		//根据时间修改消息未达到发送时间的消息
		int result = messageSendSettingService.updateSetting(entity);
		//推送地区选择为城市
		if(result>0 && !entity.getRangeType()){
			messageSendSelectAreaService.deleteByMessageId(messageDto.getId());
			
			List<MessageSendSelectArea> list = getRangeInfo(messageDto);
			for(MessageSendSelectArea selectArea : list){
				selectArea.setId(UuidUtils.getUuid());
				selectArea.setMessageId(messageDto.getId());
				selectArea.setMessageType(0);
				messageSendSelectAreaService.add(selectArea);
			}
		}
		//修改成功且发送类型为立即发送
		if(result>0 && messageDto.getSendTimeType()==0){
			messageSettingSend(entity);
		}
		return result;
	}

	

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addMessageSend(MessageSendSettingDto messageDto) throws Exception {
		
		MessageSendSetting entity = BeanMapper.map(messageDto, MessageSendSetting.class);
		
		if(messageDto.getSendTimeType()==0){
			entity.setSendTime(new Date());
		}
		String guid = UuidUtils.getUuid();
		entity.setId(guid);
		entity.setCreateTime(new Date());
		entity.setUpdateTime(new Date());
		int result = messageSendSettingService.add(entity);
		//推送地区选择为城市
		if(entity.getRangeType()){
			List<MessageSendSelectArea> list = getRangeInfo(messageDto);
			for(MessageSendSelectArea selectArea : list){
				selectArea.setId(UuidUtils.getUuid());
				selectArea.setMessageId(guid);
				selectArea.setMessageType(0);
				messageSendSelectAreaService.add(selectArea);
			}
		}
		//增加成功且消息发送时间为立即发送
		if(result>0 && messageDto.getSendTimeType()==0){
			messageSettingSend(entity);
		}
	}
	
	 /**
     * 
     * @Description: 设置解析消息关联的城市信息
     * @param MessageSendSettingDto
     * @return   
     * @author xuzq01
     * @date 2017年8月17日
     */
    private List<MessageSendSelectArea> getRangeInfo(MessageSendSettingDto settingDto) {
		// 1 解析前端传参区域数据
		// 120,葫芦岛市,0|6,辽宁省,1#119,朝阳市,0|6,辽宁省,1
		List<MessageSendSelectArea> selectAreaList = new ArrayList<MessageSendSelectArea>();
		//false 全部 true 选择城市
		boolean rangeType = settingDto.getRangeType();
		if (rangeType && StringUtils.isNotBlank(settingDto.getAreaIds())) {
			// 城市--省份
			String[] arrObjList = settingDto.getAreaIds().split("#");
			for (int i = 0; i < arrObjList.length; i++) {
				MessageSendSelectArea area = new MessageSendSelectArea();
				// arrObj[0]:城市--arrObj[1]:省份
				String[] arrObj = arrObjList[i].split("\\|");
				String[] city = arrObj[0].split(",");
				String[] province = arrObj[1].split(",");
				area.setProvinceId(province[0]);
				area.setProvinceName(province[1]);
				area.setCityId(city[0]);
				area.setCityName(city[1]);
				selectAreaList.add(area);
			}
		}
		return selectAreaList;
	}
    
    /**
	 * @Description: app消息设置推送
	 * @param entity   
	 * @author xuzq01
	 * @throws Exception 
	 * @date 2017年8月18日
	 */
	private void messageSettingSend(MessageSendSetting entity) throws Exception {
		//根据城市筛选用户
		List<String> cityIdsList =Lists.newArrayList();
		if(entity.getRangeType()){
			List<MessageSendSelectArea> areaList = messageSendSelectAreaService.findListByMessageId(entity.getId());
			areaList.forEach(selectArea -> cityIdsList.add(selectArea.getCityId()));
		}
		
		// 根据用户关联发送对象 新用户 老用户等 
		LocateInfoQueryDto dto = new LocateInfoQueryDto();
		if(entity.getType()==3){
			SimpleDateFormat df =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date beginDate = new Date();
			Calendar date = Calendar.getInstance();
			date.setTime(beginDate);
			date.add(Calendar.DATE, - (entity.getSendObject()));
			
			dto.setEndTime(df.parse(df.format(date.getTime())));
		}
		dto.setType(entity.getType());
		dto.setCityIdList(cityIdsList);
		
		List<SysBuyerLocateInfoDto> infoList = sysBuyerLocateInfoApi.findUserList(dto);
		//3 发送消息
		sendAppMessage(entity,infoList);
		//4 更新发送状态
		entity.setStatus(1);
		entity.setUpdateTime(new Date());
		messageSendSettingService.update(entity);
		
	}
	
    /**
	 * @Description: 构建消息类并发送推送消息
	 * @return   
	 * @author xuzq01
	 * @param messageSend 
	 * @param infoList 
	 * @throws Exception 
	 * @date 2017年8月18日
	 */
	private void sendAppMessage(MessageSendSetting messageSend, List<SysBuyerLocateInfoDto> infoList) throws Exception {
		PushMsgVo pushMsgVo = new PushMsgVo();
		pushMsgVo.setSysCode(msgSysCode);
		pushMsgVo.setToken(msgToken);
		pushMsgVo.setServiceTypes(new Integer[] { MsgConstant.ServiceTypes.MALL_OTHER });
		// 0:用户APP,2:商家APP,3POS机
		pushMsgVo.setAppType(Constant.ZERO);
		pushMsgVo.setIsUseTemplate(Constant.ZERO);
		pushMsgVo.setMsgType(Constant.ONE);
		pushMsgVo.setMsgTypeCustom(OrderMsgConstant.APP_MESSAGE_SEND);
		pushMsgVo.setMsgDetailType(Constant.ZERO);

		// 不使用模板 设置消息名称内容
		pushMsgVo.setMsgNotifyContent(messageSend.getContext());
		pushMsgVo.setMsgDetailType(Constant.ONE);
		pushMsgVo.setMsgDetailContent(messageSend.getContext());
		// 设置是否定时发送 定时发送
		pushMsgVo.setIsTiming(Constant.ONE);

		// 发送用户
		List<PushUserVo> userList = new ArrayList<PushUserVo>();
		infoList.forEach(user -> {
			PushUserVo pushUser = new PushUserVo();
			pushUser.setUserId(user.getUserId());
			pushUser.setMsgType(Constant.ONE);
			userList.add(pushUser);
		});
		// 查询的用户信息
		pushMsgVo.setUserList(userList);
		sendMessage(pushMsgVo);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void sendMessage(Object entity) throws Exception {
		MQMessage anMessage = new MQMessage(TOPIC, (Serializable)JsonMapper.nonDefaultMapper().toJson(entity));
		rocketMQProducer.sendMessage(anMessage);
	}
}
