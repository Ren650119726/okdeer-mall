package com.okdeer.mall.operate.advert.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.operate.advert.bo.ColumnAdvertShowRecordParamBo;
import com.okdeer.mall.operate.advert.entity.ColumnAdvertShowRecord;


public interface ColumnAdvertShowRecordService extends IBaseService {

	void save(List<ColumnAdvertShowRecord> saveList);

	int findCountByParam(ColumnAdvertShowRecordParamBo columnAdvertShowRecordParamBo);

}
