/** 
 *@Project: okdeer-mall-activity 
 *@Author: tangzj02
 *@Date: 2016年12月29日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.operate.dto.SelectAreaDto;
import com.okdeer.mall.operate.entity.ColumnSelectArea;
import com.okdeer.mall.operate.service.ColumnSelectAreaApi;
import com.okdeer.mall.operate.service.ColumnSelectAreaService;

/**
 * ClassName: ColumnSelectAreaApiImpl 
 * @Description: 栏目与地区地区城市关联服务
 * @author tangzj02
 * @date 2016年12月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0       2016-12-29        tangzj02                     添加
 */

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.operate.service.ColumnSelectAreaApi")
public class ColumnSelectAreaApiImpl implements ColumnSelectAreaApi {

	/** 日志记录 */
	private static final Logger log = LoggerFactory.getLogger(ColumnSelectAreaApiImpl.class);

	@Autowired
	private ColumnSelectAreaService selectAreaService;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnSelectAreaApi#findSelectAreaDtoListByColumnId(java.lang.String)
	 */
	@Override
	public List<SelectAreaDto> findSelectAreaDtoListByColumnId(String columnId) throws Exception {
		List<SelectAreaDto> dtoList = new ArrayList<>();
		log.info("columnId:{}", columnId);
		if (StringUtils.isNotBlank(columnId)) {
			List<ColumnSelectArea> sourceList = selectAreaService.findListByColumnId(columnId);
			if (sourceList != null) {
				dtoList = BeanMapper.mapList(sourceList, SelectAreaDto.class);
			}
		}
		return dtoList;
	}

}
