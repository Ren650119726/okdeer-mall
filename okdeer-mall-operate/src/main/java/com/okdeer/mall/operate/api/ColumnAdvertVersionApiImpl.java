/** 
 *@Project: okdeer-mall-operate 
 *@Author: tangzj02
 *@Date: 2017年3月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.api;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.operate.entity.ColumnAdvertVersionDto;
import com.okdeer.mall.operate.entity.ColumnAdvertVersionDto;
import com.okdeer.mall.operate.service.ColumnAdvertVersionApi;
import com.okdeer.mall.operate.service.ColumnAdvertVersionService;

/**
 * ClassName: ColumnAdvertVersionApiImpl 
 * @Description: 广告与APP版本关联信息
 * @author tangzj02
 * @date 2017年3月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.2       2017-03-14        tangzj02                        添加
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.operate.service.ColumnAdvertVersionApi")
public class ColumnAdvertVersionApiImpl implements ColumnAdvertVersionApi {

	@Autowired
	private ColumnAdvertVersionService advertVersionService;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAdvertVersionApi#findListByAdvertId(java.lang.String)
	 */
	@Override
	public List<ColumnAdvertVersionDto> findListByAdvertId(String advertId) throws Exception {
		List<ColumnAdvertVersionDto> list = advertVersionService.findListByAdvertId(advertId);
		List<ColumnAdvertVersionDto> dtoList = null;
		if (CollectionUtils.isEmpty(list)) {
			dtoList = Lists.newArrayList();
		} else {
			dtoList = BeanMapper.mapList(list, ColumnAdvertVersionDto.class);
		}
		return dtoList;
	}

}
