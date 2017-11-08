/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ColumnAdvertShowRecordMapper.java
 * @Date 2017-11-08 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.advert.mapper;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.operate.advert.bo.ColumnAdvertShowRecordParamBo;

public interface ColumnAdvertShowRecordMapper extends IBaseMapper {

	int findCountByParam(ColumnAdvertShowRecordParamBo columnAdvertShowRecordParamBo);

}