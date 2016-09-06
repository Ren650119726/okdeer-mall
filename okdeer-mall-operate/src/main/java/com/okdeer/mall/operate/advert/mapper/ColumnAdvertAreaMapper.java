/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: ColumnAdvertAreaMapper.java 
 * @Date: 2016年1月28日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */ 

package com.okdeer.mall.operate.advert.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.okdeer.mall.advert.entity.ColumnAdvertArea;
import com.yschome.base.dal.IBaseCrudMapper;

/**
 * 广告区域关系Mapper
 * @project yschome-mall
 * @author zhaoqc
 * @date 2016年1月28日 下午3:20:29
 */
@Repository
public interface ColumnAdvertAreaMapper extends IBaseCrudMapper {
	/**
	 * 批量新增广告区域关系
	 *
	 * @param advertAreaList 广告区域关系List
	 * @return 影响行数
	 */
	int insertAdvertAreaBatch(List<ColumnAdvertArea> advertAreaList);

	/**
	 * 根据广告Id删除广告区域关系 
	 *
	 * @param advertId 广告Id
	 */
	void deleteByAdvertId(@Param("advertId") String advertId);
}
