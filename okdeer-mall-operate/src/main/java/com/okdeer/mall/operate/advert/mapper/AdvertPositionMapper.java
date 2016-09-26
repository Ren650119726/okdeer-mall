/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: AdvertPositionDao.java 
 * @Date: 2016年1月22日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */ 

package com.okdeer.mall.operate.advert.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.okdeer.mall.advert.entity.AdvertPosition;
import com.okdeer.mall.advert.entity.AdvertPositionQueryVo;
import com.okdeer.base.dal.IBaseCrudMapper;

/**
 * 广告位Mapper
 * @project yschome-mall
 * @author zhaoqc
 * @date 2016年1月22日 上午11:35:46
 */
@Repository
public interface AdvertPositionMapper extends IBaseCrudMapper {
	
	/**
	 * 获取广告位列表
	 *
	 * @param queryVo 查询Vo
	 * @return 广告位列表
	 */
	List<AdvertPosition> findAdvertPositions(AdvertPositionQueryVo queryVo);
	
}
