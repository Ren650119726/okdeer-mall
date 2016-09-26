/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: ColumnAdvertService.java 
 * @Date: 2016年1月27日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */ 

package com.okdeer.mall.operate.advert.service;

import java.util.List;
import java.util.Map;

import com.okdeer.archive.system.entity.PsmsSmallCommunityInfo;
import com.okdeer.archive.system.entity.SysUser;
import com.okdeer.mall.advert.entity.AdvertDetailVo;
import com.okdeer.mall.advert.entity.ColumnAdvert;
import com.okdeer.mall.advert.entity.ColumnAdvertApproval;
import com.okdeer.mall.advert.entity.ColumnAdvertCommunity;
import com.okdeer.mall.advert.entity.ColumnAdvertInfo;
import com.okdeer.mall.advert.entity.ColumnAdvertQueryVo;
import com.okdeer.mall.advert.entity.ColumnAdvertVo;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;

/**
 * 广告service
 * @project yschome-mall
 * @author zhaoqc
 * @date 2016年1月27日 下午6:09:04
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		广告修改			2016-7-14			zhulq	            广告模块修改
 *		广告修改将			2016-8-1			zhulq	    将 associateIdList 去掉
 */
public interface ColumnAdvertService {
	
	/**
	 * 查询广告列表
	 *
	 * @param queryVo 查询Vo
	 * @param pageNumber 页数
	 * @param pageSize 每页记录数
	 * @return 广告分页务数据
	 * @throws ServiceException 抛出异常
	 */
	PageUtils<ColumnAdvertVo> findColumnAdvertPage(ColumnAdvertQueryVo queryVo, 
					int pageNumber, int pageSize) throws ServiceException;
	
	/**
	 * 查询审核广告列表
	 *
	 * @param queryVo 查询Vo
	 * @param pageNumber 页数
	 * @param pageSize 每页记录数
	 * @return 审核广告分页务数据
	 * @throws ServiceException 抛出异常
	 */
	PageUtils<ColumnAdvertVo> findAuditingAdvertPage(ColumnAdvertQueryVo queryVo, 
					int pageNumber, int pageSize) throws ServiceException;
	
	/**
	 * @Description: 广告张数的限制
	 * @param advert 广告
	 * @param areaIdList  省 市ids
	 * @return   张数
	 * @author zhulq
	 * @date 2016年8月1日
	 */
	//begin 广告张数的限制  重写findAcrossTimeAdvertQty方法	add by zhulq  2016-7-15
	int findAcrossTimeAdvertQty(ColumnAdvert advert, List<String> areaIdList);
	//end 广告张数的限制  重写findAcrossTimeAdvertQty方法	add by zhulq  2016-7-15
	
	//begin 将省市信息全部放进areaIdList add by zhulq  2016-8-1
	/**
	 * @Description: 审核代理商时候 判断张数限制
	 * @param advert  广告
	 * @param areaIdList  省ids
	 * @return  记录数
	 * @author zhulq
	 * @date 2016年7月16日
	 */
	int findAcrossTimeAdvert(ColumnAdvert advert,List<String> areaIdList);
	//end 将省市信息全部放进areaIdList	add by zhulq  2016-8-1
	
	/**
	 * 根绝Id查找广告实体
	 *
	 * @param id 广告Id
	 * @return 广告
	 */
	ColumnAdvert findAdvertById(String id);
	
	/**
	 * 创建广告
	 *
	 * @param advert 广告实体
	 * @param advertInfo广告商实体
	 * @param currentUser 当前用户
	 */
	void addColumnAdvert(ColumnAdvert advert, ColumnAdvertInfo advertInfo, SysUser currentUser);
	
	/**
	 * 修改广告
	 *
	 * @param advert 广告实体
	 * @param advertInfo 广告商实体
	 * @param currentUser 当前用户
	 */
	void updateColumnAdvert(ColumnAdvert advert, ColumnAdvertInfo advertInfo, SysUser currentUser);
	
	/**
	 * 获取已有广告时间 
	 *
	 * @param positionId 广告位Id
	 * @return 广告列表
	 */
	List<ColumnAdvert> findExistAdvertTime(String positionId);
	
	/**
	 * 根据Id获得广告详情Vo
	 * @param id 广告Id
	 * @return 广告详情Vo
	 */
	AdvertDetailVo getAdvertDetailById(String id);
	
	/**
	 * 根据代理商Id代理商代理小区列表
	 * @param agentId 代理商Id
	 * @return 代理小区列表
	 */
	List<PsmsSmallCommunityInfo> getCommunityByAgentId(String agentId);
	
	/**
	 * 广告审核
	 * @param advertApproval 广告审核
	 * @param currentUser 当前用户
	 */
	void auditAdvert(ColumnAdvertApproval advertApproval, SysUser currentUser);
	
	/**
	 * 修改广告审核实体
	 * @param advertApproval 广告审核实体
	 */
	void updateAdvertApproval(ColumnAdvertApproval advertApproval);
	
	/**
	 * 查找广告位在某个区域内已经投放的广告的个数
	 * 
	 * @param communitys 发布小区列表
	 * @param positionId 广告位Id
	 * 
	 * @return 已发布广告的个数
	 */
	int getAdvertNumInCommunitys(List<ColumnAdvertCommunity> communitys, String positionId);
	
	/**
	 * 广告上架
	 * @param advertVo 广告详情Vo
	 * @param currentUser 当前用户
	 */
	void onShelfAdvert(AdvertDetailVo advertVo, SysUser currentUser);
	
	/**
	 * 广告下架
	 * @param advert 广告实体
	 * @param currentUser 当前用户
	 */
	void offShelfAdvert(ColumnAdvert advert, SysUser currentUser);
	
	/**
	 * 根据广告位置id查询广告
	 * @desc 获取广告信息
	 * @param params 参数
	 * @return list
	 */
	List<ColumnAdvert> getAdvertById(Map<String,Object> params);
	
	
	/**
	 * 广告列表 pos用 张克能加
	 * @param map 参数
	 * @return list
	 */
	List<ColumnAdvert> listForPos(Map<String,Object> map);

	/**
	 * 根据交易流水号获得广告信息
	 *
	 * @param tradeNum 交易流水号
	 * @return 广告信息
	 */
	ColumnAdvert getAdvertByTradeNum(String tradeNum);

	/**
	 * 修改广告信息
	 *
	 * @param advert 广告信息
	 * @return 影响条数
	 */
	int updateAdvertInfo(ColumnAdvert advert);
	
	/**
	 *	根据job扫描更新广告状态
	 */
	void updateAdvertStatusByJob();
	
	/**
	 * 根据广告Id查找广告审核信息
	 *
	 * @param advertId 广告Id
	 * @return 广告审核信息
	 */
	ColumnAdvertApproval getApprovalByAdvertId(String advertId);
	
}
