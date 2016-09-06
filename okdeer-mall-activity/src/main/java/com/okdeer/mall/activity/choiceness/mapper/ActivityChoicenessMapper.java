/** 
 *@Project: yschome-mall-activity 
 *@Author: lijun
 *@Date: 2016年7月18日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */    

package com.okdeer.mall.activity.choiceness.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.activity.choiceness.entity.ActivityChoiceness;
import com.okdeer.mall.activity.choiceness.vo.ActivityChoicenessFilterVo;
import com.okdeer.mall.activity.choiceness.vo.ActivityChoicenessListPageVo;

/**
 * ClassName: ActivityChoicenessMapper 
 * @Description: 精选活动Mapper接口
 * @author lijun
 * @date 2016年7月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *      重构 4.1         2016年7月18日                                 lijun               新增
 *      重构 4.1         2016年8月04日                                 zhongy              新增校验商品是否存在方法
 *      重构 4.1         2016年8月05日                                 zhongy              批量删除精选服务商品
 */
public interface ActivityChoicenessMapper {
	
	/**
	 * @Description: 查询精选活动列表页
	 * @param queryFilterVo 查询条件Vo
	 * @return List 列表集合
	 * @author lijun
	 * @date 2016年7月19日
	 */
	List<ActivityChoicenessListPageVo> findChoicenessListPageByFilter(ActivityChoicenessFilterVo queryFilterVo);
	
	
	/**
	 * @Description: 批量添加精选服务
	 * @param activityChoicenessList 精选活动集合
	 * @return Integer 受影响行数
	 * @author lijun
	 * @date 2016年7月19日
	 */
	Integer addByBatch(@Param("choicenessList") List<ActivityChoiceness> activityChoicenessList);
	
	
	/**
	 * @Description: 批量删除精选服务
	 * @param choicenessIds 精选服务id集合
	 * @return 受影响行数
	 * @author lijun
	 * @date 2016年7月19日
	 */
	Integer deleteByIds(@Param("choicenessIds") List<String> choicenessIds);
	
	/**
	 * @Description: 通过主键id获取精选服务信息
	 * @param choicenessId 精选服务主键id
	 * @return ActivityChoiceness 实体类
	 * @author lijun
	 * @date 2016年7月19日
	 */
	ActivityChoiceness findByPrimaryKey(@Param("choicenessId") String choicenessId);
	
	/**
	 * @Description: 通过主键id获取精选服务信息(集合)
	 * @param choicenessIdList 精选服务主键id集合
	 * @return ActivityChoiceness 实体类
	 * @author lijun
	 * @date 2016年7月19日
	 */
	List<ActivityChoiceness> findByPrimaryKeyList(@Param("choicenessIdList") List<String> choicenessIdList);
	
	/**
	 * @Description: 更新精选活动状态
	 * @param activityId 活动id
	 * @param sortValue 排序值
	 * @throws Exception 抛出异常
	 * @author lijun
	 * @date 2016年7月21日
	 */
	void updateChoicenessStatus(@Param("activityId") String activityId, @Param("sortValue") String sortValue);
	
	
	/**
	 * @Description: 批量校验商品是否存在
	 * @param skuIds 商品id集合
	 * @return 返回结果
	 * @author zhongy
	 * @date 2016年8月04日
	 */
	Integer findCountBySkuIds(@Param("skuIds") List<String> skuIds);
	
	/**
	 * @Description: 批量删除精选服务商品
	 * @param  精选服务商品id集合
	 * @author zhongy
	 * @date 2016年8月05日
	 */
	Integer deleteBySkuIds(@Param("goodsStoreSkuIds") List<String> goodsStoreSkuIds);
}
