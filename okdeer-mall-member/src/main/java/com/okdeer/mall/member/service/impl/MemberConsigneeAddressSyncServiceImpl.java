/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @项目名称: yschome-mall 
 * @文件名称: MemberConsigneeAddressSyncServiceImpl.java 
 * @Date: 2016年2月1日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
package com.okdeer.mall.member.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.github.pagehelper.StringUtil;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.okdeer.mall.common.utils.RobotUserUtil;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.member.enums.AddressDefault;
import com.okdeer.mall.member.member.enums.AddressSource;
import com.okdeer.mall.member.member.enums.AddressType;
import com.okdeer.archive.system.entity.SysUser;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.framework.mq.AbstractRocketMQSubscriber;
import com.okdeer.mall.member.service.MemberConsigneeAddressService;

/***
 * 
 * ClassName: MemberConsigneeAddressSyncServiceImpl 
 * @Description: 会员收货地址同步
 * @author guocp
 * @date 2016年2月1日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			2016-08-03			luosm				新增省名，市名，区名，区域名称字段
 *      V1.1.0          2016-10-14     		luosm               根据小区id批量修改省市区名，小区名
 */

@Service
public class MemberConsigneeAddressSyncServiceImpl extends AbstractRocketMQSubscriber {

	private static final Logger logger = LoggerFactory.getLogger(MemberConsigneeAddressSyncServiceImpl.class);

	/** topic */
	private static final String TOPIC = "topic_consigness_address";
	/** add tag */
	private static final String TAG_ADD = "tag_consigness_address_add";
	/** update tag */
	private static final String TAG_UPDATE = "tag_consigness_address_update";
	
	/** update communityIds tag */
	private static final String TAG_UPDATE_CommunityId = "tag_consigness_address_update_communityId";
	
	/** delete tag */
	private static final String TAG_DELETE = "tag_consigness_address_delete";

	/** 会员收货地址服务 */
	@Autowired
	private MemberConsigneeAddressService memberConsigneeAddressService;

	/**
	 * topic
	 */
	@Override
	public String getTopic() {
		return TOPIC;
	}

	/**
	 * tags
	 */
	@Override
	public String getTags() {
		return "*";
	}

	/**
	 * 消费消息
	 * 
	 * @param msgs 标签
	 * @param context 上下文
	 */
	@Override
	public ConsumeConcurrentlyStatus subscribeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		MessageExt msgExt = msgs.get(0);
		switch (msgExt.getTags()) {
			case TAG_ADD: {
				//return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				JSONObject json = JSONObject.fromObject(new String(msgs.get(0).getBody(), Charsets.UTF_8));
				logger.info("同步物业收货地址接收新增消息:" + json);
				try {
					if (!Iterables.isEmpty(getAddressList(json))) {
						break;
					}
					MemberConsigneeAddress address = buildSmallCommunity(json, null);
					if (memberConsigneeAddressService.getConsigneeAddress(address.getId()) == null) {
						memberConsigneeAddressService.addConsigneeAddress(address, RobotUserUtil.getRobotUser());
					}
				} catch (Exception e) {
					logger.error("同步物业收货地址失败", e);
					return ConsumeConcurrentlyStatus.RECONSUME_LATER;
				}
				break;
			}
			
			case TAG_UPDATE: {
				//return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				
				JSONObject json = JSONObject.fromObject(new String(msgs.get(0).getBody(), Charsets.UTF_8));
				logger.info("同步物业收货地址修改消息:" + json);
				try {
					String msg = "商城无同步物业收货地址:" + json;
					if(getAddress(json) != null && getAddress(json).getId() != null){
						MemberConsigneeAddress address = buildSmallCommunity(json, getAddress(json).getId());
						memberConsigneeAddressService.updateConsigneeAddress(address, RobotUserUtil.getRobotUser());
						msg = "同步物业收货地址修改消息成功";
					}
					logger.info(msg);
				} catch (Exception e) {
					logger.error("同步物业收货地址失败", e);
					return ConsumeConcurrentlyStatus.RECONSUME_LATER;
				}
				break;
			}
			
			//begin added by luosm V1.1.0 20161014
			case TAG_UPDATE_CommunityId:{
				JSONObject json = JSONObject.fromObject(new String(msgs.get(0).getBody(), Charsets.UTF_8));
				logger.info("同步物业收货地址修改消息:" + json);
				try {
					String msg = "商城无同步物业收货地址:" + json;
					if(getByCommunityIdAddress(json) != null && getByCommunityIdAddress(json).size()>0){
						MemberConsigneeAddress address = buildCommunityId(json);
						if(address == null){
							return ConsumeConcurrentlyStatus.RECONSUME_LATER;
						}
						memberConsigneeAddressService.updateByCommunityIdsConsigneeAddress(address);
						msg = "同步物业收货地址修改消息成功";
					}
					logger.info(msg);
				} catch (Exception e) {
					logger.error("同步物业收货地址失败", e);
					return ConsumeConcurrentlyStatus.RECONSUME_LATER;
				}
				break;
			}
			//end added by luosm V1.1.0 20161014
			
			case TAG_DELETE: {
				//return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				JSONObject json = JSONObject.fromObject(new String(msgs.get(0).getBody(), Charsets.UTF_8));
				logger.info("同步物业收货地址接收删除消息:" + json);
				try {
					List<MemberConsigneeAddress> addrs = getAddressList(json);
					for (MemberConsigneeAddress addr : addrs) {
						memberConsigneeAddressService.deleteById(addr.getId(), RobotUserUtil.getRobotUser().getCreateUserId());
					}
				} catch (Exception e) {
					logger.error("同步物业收货地址删除失败", e);
					return ConsumeConcurrentlyStatus.RECONSUME_LATER;
				}
				break;
			}
			default: {
				break;
			}
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}
	
	private List<MemberConsigneeAddress> getAddressList(JSONObject json) throws Exception {
		MemberConsigneeAddress params = new MemberConsigneeAddress();
		params.setRoomId(json.getString("roomId"));
		params.setUserId(json.getString("userId"));
		return memberConsigneeAddressService.getList(params);
	}
	
	
	private MemberConsigneeAddress getAddress(JSONObject json) throws Exception {
		MemberConsigneeAddress params = new MemberConsigneeAddress();
		params.setRoomId(json.getString("roomId"));
		params.setUserId(json.getString("userId"));
		List<MemberConsigneeAddress> list = memberConsigneeAddressService.getList(params);
		if(list == null){
			list = new ArrayList<MemberConsigneeAddress>();
		}
		return Iterables.getOnlyElement(list);
	}
	
	//begin added by luosm V1.1.0 20161014
	private List<MemberConsigneeAddress> getByCommunityIdAddress(JSONObject json) throws Exception {
		MemberConsigneeAddress params = new MemberConsigneeAddress();
		params.setCommunityId(json.getString("communityId"));
		List<MemberConsigneeAddress> list = memberConsigneeAddressService.getList(params);
		if(list == null){
			list = new ArrayList<MemberConsigneeAddress>();
		}
		return list;
	}
	//end added by luosm V1.1.0 20161014
	

	/**
	 * 构建收货地址
	 */
	private MemberConsigneeAddress buildSmallCommunity(JSONObject json, String id) throws Exception {
		MemberConsigneeAddress address = new MemberConsigneeAddress();
		address.setId(id == null ? json.getString("id") : id);
		address.setUserId(json.getString("userId"));
		address.setMobile(json.getString("mobile"));
		if (json.get("phone") != null) {
			address.setTelephone(json.getString("phone"));
		}
		if (json.get("name") != null) {
			address.setConsigneeName(json.getString("name"));
		} else {
			address.setConsigneeName(json.getString("mobile"));
		}
		address.setProvinceId(json.getString("provinceId"));
		address.setAreaId(json.getString("areaId"));
		address.setCityId(json.getString("cityId"));
		
		//begin add by luosm 2016-08-01
		address.setProvinceName(json.getString("provinceName"));
		address.setCityName(json.getString("cityName"));
		address.setAreaName(json.getString("areaName"));
		address.setAreaExt(json.getString("areaExt"));
		//end add by luosm 2016-08-01
		
		address.setAddress(json.getString("address"));
		address.setCommunityId(json.getString("communityId"));
		address.setDisabled(Disabled.valid);

		address.setIsDefault(AddressDefault.NO);

		if (json.get("remark") != null) {
			address.setRemark(json.getString("remark"));
		}
		
		address.setRoomId(json.getString("roomId"));
		address.setSource(AddressSource.PSMS);
		address.setType(AddressType.BUYER);
		
		
		address.setLongitude(json.containsKey("longitude") ? Double.valueOf(json.getString("longitude")) : null);
		address.setLatitude(json.containsKey("latitude") ? Double.valueOf(json.getString("latitude")) :  null);
		return address;
	}

	//begin added by luosm V1.1.0 20161014
	/**
	 * 构建根据小区id批量修改物业地址
	 */
	private MemberConsigneeAddress buildCommunityId(JSONObject json) throws Exception {
		MemberConsigneeAddress address = new MemberConsigneeAddress();
		SysUser currentOperateUser = RobotUserUtil.getRobotUser();
		address.setCommunityId(json.getString("communityId"));
		address.setUpdateTime(new Date());
		address.setUpdateUserId(currentOperateUser.getId());
		if(!StringUtils.isEmpty(json.getString("provinceName"))){
			address.setProvinceName(json.getString("provinceName"));
		}else{
			logger.info("provinceName 为空");
			return null;
		}
		
		
		if(!StringUtils.isEmpty(json.getString("cityName"))){
			address.setCityName(json.getString("cityName"));
		}else{
			logger.info("cityName 为空");
			return null;
		}
		
		if(!StringUtils.isEmpty(json.getString("areaName"))){
			address.setAreaName(json.getString("areaName"));
		}else{
			logger.info("areaName 为空");
			return null;
		}
		
		if(!StringUtils.isEmpty(json.getString("provinceId"))){
			address.setProvinceId(json.getString("provinceId"));
		}else{
			logger.info("provinceId 为空");
			return null;
		}
		
		if(!StringUtils.isEmpty(json.getString("cityId"))){
			address.setCityId(json.getString("cityId"));
		}else{
			logger.info("cityId 为空");
			return null;
		}
		
		if(!StringUtils.isEmpty(json.getString("areaId"))){
			address.setAreaId(json.getString("areaId"));
		}else{
			logger.info("areaId 为空");
			return null;
		}
		
		if(!StringUtils.isEmpty(json.getString("areaExt"))){
			address.setAreaExt(json.getString("areaExt"));
		}else{
			logger.info("areaExt 为空");
			return null;
		}
		
		if(!StringUtils.isEmpty(json.getString("longitude"))){
			address.setLongitude(Double.valueOf(json.getString("longitude")));
		}else{
			logger.info("longitude 为空");
			return null;
		}
		
		if(!StringUtils.isEmpty(json.getString("latitude"))){
			address.setLongitude(Double.valueOf(json.getString("latitude")));
		}else{
			logger.info("latitude 为空");
			return null;
		}
		

		return address;
	}
	//end added by luosm V1.1.0 20161014
}
