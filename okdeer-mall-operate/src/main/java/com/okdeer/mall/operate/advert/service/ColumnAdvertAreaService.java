
package com.okdeer.mall.operate.advert.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.advert.entity.ColumnAdvertArea;
import com.okdeer.mall.operate.advert.bo.ColumnAdvertAreaParamBo;

public interface ColumnAdvertAreaService extends IBaseService {
	
	/**
	 * @Description: 根据查询广告区域信息
	 * @param columnAdvertAreaParamBo 查询参数
	 * @return
	 * @author zengjizu
	 * @date 2017年11月8日
	 */
	List<ColumnAdvertArea> findList(ColumnAdvertAreaParamBo columnAdvertAreaParamBo);

}
