
package com.okdeer.mall.activity.wxchat.api.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.wechat.dto.CheckWxchatServerParamDto;
import com.okdeer.mall.activity.wechat.dto.MaterialDto;
import com.okdeer.mall.activity.wechat.dto.NewsDto;
import com.okdeer.mall.activity.wechat.dto.WechatConfigDto;
import com.okdeer.mall.activity.wechat.service.WechatApi;
import com.okdeer.mall.activity.wxchat.bo.Material;
import com.okdeer.mall.activity.wxchat.bo.QueryMaterialResponse;
import com.okdeer.mall.activity.wxchat.config.WechatConfig;
import com.okdeer.mall.activity.wxchat.service.WechatMsgProcessService;
import com.okdeer.mall.activity.wxchat.service.WechatService;

@Service(version = "1.0.0")
public class WechatApiImpl implements WechatApi {

	@Autowired
	private WechatConfig wechatConfig;

	@Autowired
	private WechatService wechatService;
	
	@Autowired
	private WechatMsgProcessService wechatMsgProcessService;

	@Override
	public boolean checkWxchatServer(CheckWxchatServerParamDto checkWxchatServerParamDto) {
		String[] array = new String[] { wechatConfig.getToken(), checkWxchatServerParamDto.getTimestamp(),
				checkWxchatServerParamDto.getNonce() };
		StringBuilder sb = new StringBuilder();
		// 字符串排序
		Arrays.sort(array);
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
		}
		String str = sb.toString();
		String msgSignature = DigestUtils.sha1Hex(str);
		return msgSignature.equals(checkWxchatServerParamDto.getSignature());
	}

	@Override
	public PageUtils<MaterialDto> findMaterialList(String type, int pageNum, int pageSize) throws MallApiException {
		try {
			QueryMaterialResponse queryMaterialResponse = wechatService.findMaterialList(type, pageNum, pageSize);
			if (!queryMaterialResponse.isSuccess()) {
				throw new MallApiException("微信返回出错信息:" + queryMaterialResponse.getErrMsg());
			}

			List<MaterialDto> dtoList = Lists.newArrayList();
			List<Material> itemList = queryMaterialResponse.getItem();
			if (CollectionUtils.isNotEmpty(itemList)) {
				for (Material material : itemList) {
					MaterialDto materialDto = new MaterialDto();
					BeanMapper.copy(material, materialDto);
					if (material.getContent() != null) {
						materialDto.setNewsList(BeanMapper.mapList(material.getContent().getNewsItem(), NewsDto.class));
					}
					dtoList.add(materialDto);
				}
			}
			PageUtils<MaterialDto> pageUtils = new PageUtils<>(dtoList);
			pageUtils.setPageSize(pageSize);
			pageUtils.setPageNum(pageNum);
			pageUtils.setTotal(queryMaterialResponse.getTotalCount());
			return pageUtils;
		} catch (Exception e) {
			throw new MallApiException(e);
		}
	}

	@Override
	public String processRequest(String xmlCotent) throws MallApiException {
		return wechatMsgProcessService.process(xmlCotent);
	}

	@Override
	public WechatConfigDto getWechatConfig() throws MallApiException {
		try {
			return wechatService.getWechatConfig();
		} catch (Exception e) {
			throw new MallApiException(e);
		}
	}

}
