
package com.okdeer.mall.operate.advert.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.operate.advert.bo.ColumnAdvertShowRecordParamBo;
import com.okdeer.mall.operate.advert.entity.ColumnAdvertShowRecord;
import com.okdeer.mall.operate.advert.mapper.ColumnAdvertShowRecordMapper;
import com.okdeer.mall.operate.advert.service.ColumnAdvertShowRecordService;

@Service
public class ColumnAdvertShowRecordServiceImpl extends BaseServiceImpl implements ColumnAdvertShowRecordService {

	@Autowired
	private ColumnAdvertShowRecordMapper columnAdvertShowRecordMapper;

	@Override
	public IBaseMapper getBaseMapper() {
		return columnAdvertShowRecordMapper;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(List<ColumnAdvertShowRecord> saveList) {
		for (ColumnAdvertShowRecord columnAdvertShowRecord : saveList) {
			columnAdvertShowRecord.setId(UuidUtils.getUuid());
			columnAdvertShowRecord.setCreateTime(new Date());
			columnAdvertShowRecordMapper.add(columnAdvertShowRecord);
		}
	}

	@Override
	public int findCountByParam(ColumnAdvertShowRecordParamBo columnAdvertShowRecordParamBo) {
		return columnAdvertShowRecordMapper.findCountByParam(columnAdvertShowRecordParamBo);
	}

}
