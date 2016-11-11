package com.okdeer.mall.activity.label.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.label.entity.ActivityLabel;
import com.okdeer.mall.activity.label.entity.ActivityLabelGoods;

/**
 * ClassName: ActivityLabelService 
 * @Description: 服务标签service
 * @author YSCGD
 * @date 2016年11月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			 2016年11月10日 			zhagnkn
 */
public interface ActivityLabelService extends IBaseService{
	/**
	 * @Description: 保存
	 * @param activityLabel 标签对象
	 * @param goodsIds 商品id集合
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月4日
	 */
	void add(ActivityLabel activityLabel,List<String> goodsIds) throws Exception;

	/**
	 * @Description: 修改
	 * @param activityLabel
	 * @param goodsIds
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月4日
	 */
	void update(ActivityLabel activityLabel,List<String> goodsIds) throws Exception;

	/**
	 * @Description: 通过id取对象
	 * @param id
	 * @return
	 * @author zhangkn
	 * @date 2016年11月4日
	 */
	ActivityLabel findById(String id);
	
	/**
	 * @Description: 查询标签列表
	 * @param map 参数
	 * @param pageNumber 当前页码
	 * @param pageSize 每页条数
	 * @return PageUtils<ActivityCollectCoupons>  
	 * @throws ServiceException service异常
	 * @author zhangkn
	 * @date 2016年7月13日
	 */
	PageUtils<ActivityLabel> list(Map<String,Object> map,int pageNumber,int pageSize) throws Exception;
	
	/**
	 * @Description: 查询可选商品列表
	 * @param map 参数
	 * @param pageNumber 当前页码
	 * @param pageSize 每页条数
	 * @return PageUtils<ActivityCollectCoupons>  
	 * @throws Exception service异常
	 * @author zhangkn
	 * @date 2016年7月13日
	 */
	PageUtils<Map<String,Object>> listGoods(Map<String,Object> map,int pageNumber,int pageSize) throws Exception;
	
	/**
	 * @Description: 批量修改状态
	 * @param id
	 * @param status 活动状态 0 未开始 ，1：进行中2:已结束 3 已关闭
	 * @param updateUserId 修改人
	 * @param updateTime 修改时间
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月4日
	 */
	void updateBatchStatus(String id,int status,String updateUserId,Date updateTime) throws Exception;
	
	/**
	 * @desc 用于判断某个时间段内活动是否冲突
	 * @param map
	 * @return
	 */
	int countTimeQuantum(Map<String,Object> map);
	
	/**
	 * @desc 查询出需要跑job的活动
	 * @return
	 */
	List<ActivityLabel> listByJob();
	
	/**
	 * @Description: 通过活动id获取关联商品列表
	 * @param activityId
	 * @return
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月7日
	 */
	List<ActivityLabelGoods> listActivityLabelGoods(String activityId) throws Exception;
}
