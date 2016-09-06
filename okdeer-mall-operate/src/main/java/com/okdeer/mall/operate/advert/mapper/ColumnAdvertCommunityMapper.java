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

import com.okdeer.mall.advert.entity.ColumnAdvertCommunity;
import com.yschome.base.dal.IBaseCrudMapper;

/**
 * 广告小区关系Mapper
 * @project yschome-mall
 * @author zhaoqc
 * @date 2016年1月28日 下午3:20:29
 */
@Repository
public interface ColumnAdvertCommunityMapper extends IBaseCrudMapper {
	/**
	 * 批量新增广告小区关系
	 *
	 * @param advertCommunityList 广告小区关系List
	 * @return 影响行数
	 */
	int insertAdvertCommunityBatch(List<ColumnAdvertCommunity> advertCommunityList);
	
	/**
	 * 根据广告Id删除广告与小区关系
	 *
	 * @param advertId 广告Id
	 */
	void deleteByAdvertId(@Param("advertId") String advertId);
}
