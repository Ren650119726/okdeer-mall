/** 
 *@Project: okdeer-mall-operate 
 *@Author: tangzj02
 *@Date: 2017年3月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.operate.entity.ColumnAdvertVersionDto;
import com.okdeer.mall.operate.mapper.ColumnAdvertVersionMapper;
import com.okdeer.mall.operate.service.ColumnAdvertVersionService;

/**
 * ClassName: ColumnAdvertVersionServiceImpl 
 * @Description: 广告与APP版本关联信息
 * @author tangzj02
 * @date 2017年3月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.2       2017-03-14        tangzj02                        添加
 */
@Service
public class ColumnAdvertVersionServiceImpl extends BaseServiceImpl implements ColumnAdvertVersionService {

	@Autowired
	private ColumnAdvertVersionMapper advertVersionMappe;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return advertVersionMappe;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAdvertVersionService#findListByAdvertId(java.lang.String)
	 */
	@Override
	public List<ColumnAdvertVersionDto> findListByAdvertId(String advertId) throws Exception {
		if (StringUtils.isBlank(advertId)) {
			return null;
		}
		return advertVersionMappe.findListByAdvertId(advertId);
	}

}
