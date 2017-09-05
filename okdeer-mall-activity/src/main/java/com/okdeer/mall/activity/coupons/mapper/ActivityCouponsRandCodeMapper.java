package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseCrudMapper;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRandCode;

/**
 * ClassName: ActivityCouponsRandCodeMapper 
 * @Description: 代金券随机码对应关系Mapper
 * @author maojj
 * @date 2016年10月25日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿1.1.1			2016年10月25日				maojj		代金券随机码对应关系Mapper
 */
public interface ActivityCouponsRandCodeMapper extends IBaseCrudMapper {
	
	/**
	 * @Description: 根据代金券id删除
	 * @param couponsId
	 * @author zhangkn
	 * @date 2017年9月5日
	 */
	void deleteByCouponsId(String couponsId);
	
	/**
	 * 批量新增
	 * @param activityCouponsRandCodeList
	 */
	void batchInsert(List<ActivityCouponsRandCode> activityCouponsRandCodeList);
	
	/**
	 * @Description: 查询已经存在的随机数
	 * @param randCodeSet
	 * @return   
	 * @author maojj
	 * @date 2016年10月25日
	 */
	Set<String> findExistCodeSet(Set<String> randCodeSet);
	
	/**
	 * @Description: 根据随机码 
	 * @param randCode randCode
	 * @return int
	 * @author zhulq
	 * @date 2016年10月25日
	 */
	ActivityCouponsRandCode selectByRandCode(@Param("randCode") String randCode);
	
	/**
	 * @Description: 根据代金券id获取代金券随机码
	 * @param couponsId 代金券id
	 * @return 集合
	 * @author zhulq
	 * @date 2017年4月6日
	 */
	List<ActivityCouponsRandCode> findByCouponsId(@Param("couponsId") String couponsId);
}
