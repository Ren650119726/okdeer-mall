/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: ColumnAdvert.java 
 * @Date: 2016年1月27日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */ 

package com.okdeer.mall.operate.advert.mapper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.okdeer.archive.goods.store.dto.GoodsStoreActivitySkuDto;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.advert.dto.ColumnAdvertQueryParamDto;
import com.okdeer.mall.advert.entity.AdvertDetailVo;
import com.okdeer.mall.advert.entity.AdvertGoodsVo;
import com.okdeer.mall.advert.entity.ColumnAdvert;
import com.okdeer.mall.advert.entity.ColumnAdvertCommunity;
import com.okdeer.mall.advert.entity.ColumnAdvertQueryVo;
import com.okdeer.mall.advert.entity.ColumnAdvertVo;

/**
 * 广告Mapper
 * @project yschome-mall
 * @author zhaoqc
 * @date 2016年1月27日 下午6:03:37
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		广告修改			2016-7-15			zhulq	            广告模块修改
 *		广告修改			2016-8-3			zhulq	            广告上架时候判断一定是审核通过
 *		v1.1.0			2016-10-18			zhulq        获取默认广告
 */
@Repository
public interface ColumnAdvertMapper extends IBaseMapper {

	/**
	 * 获取广告列表
	 *
	 * @param queryVo 查询Vo
	 * @return 广告列表
	 */
	List<ColumnAdvertVo> findColumnAdvertPage(ColumnAdvertQueryVo queryVo);
	
	/**
	 * 获取审核广告列表
	 *
	 * @param queryVo 查询Vo
	 * @return 广告列表
	 */
	List<ColumnAdvertVo> findAuditingAdvertPage(ColumnAdvertQueryVo queryVo);
	
	/**
	 * @Description: 运营商广告张数限制
	 * @param advert 广告
	 * @return   张数
	 * @author zhulq
	 * @date 2016年8月3日
	 */
	//begin 广告张数的限制  重写findAcrossTimeAdvertQty方法	add by zhulq  2016-7-15
	int findAcrossTimeAdvertQty(ColumnAdvert advert);
	//end 广告张数的限制  重写findAcrossTimeAdvertQty方法	add by zhulq  2016-7-15
	
	/**
	 * 获取已有广告的时间
	 *
	 * @param positionId 广告位Id
	 * @return 广告列表
	 */
	List<ColumnAdvert> findExistAdvertTime(@Param("positionId") String positionId);
	
	/**
	 * @Description: 根据id获取广告 详情
	 * @param id 广告id
	 * @return   AdvertDetailVo
	 * @author zhulq
	 * @date 2016年8月3日
	 */
	AdvertDetailVo getAdvertDetailById(@Param("id") String id);
	
	/**
	 * 查找广告位在某个区域内已经投放的广告的个数
	 *
	 * @param communitys 发布小区列表
	 * @param positionId 广告位Id
	 * @return 已发布广告的个数
	 */
	int getAdvertNumInCommunitys(@Param("communitys") List<ColumnAdvertCommunity> communitys, @Param("positionId") String positionId);
	
	/**
	 * @Description: 根据广告位ID查询云周边广告
	 * @param params  参数
	 * @return   集合
	 * @author zhulq
	 * @date 2016年8月3日
	 */
	List<ColumnAdvert> getAdvertById(Map<String,Object> params);
	
	
	/**
	 * @Description: 广告列表 pos用 张克能加
	 * @param map 参数
	 * @return   广告集合
	 * @author zhulq
	 * @date 2016年8月3日
	 */
	List<ColumnAdvert> listForPos(Map<String,Object> map);
	
	/**
	 * 根据交易流水号获得广告信息
	 *
	 * @param tradeNum 交易流水号
	 * @return 广告信息
	 */
	ColumnAdvert getAdvertByTradeNum(@Param("tradeNum") String tradeNum);
	
	/**
	 * 根据状态，当前时间，缴费状态查找广告信息
	 *
	 * @param status 广告状态
	 * @param currentTime 当前时间
	 * @param timeColumn 时间比较的字段
	 * @param timeLogicSymbol 时间逻辑操作符
	 * @param isPay 缴费状态
	 * @return 广告信息
	 */
	List<ColumnAdvert> getAdvertForJob(@Param("status") Integer status, @Param("currentTime") Date currentTime, @Param("timeColumn")
	 					String timeColumn,@Param("timeLogicSymbol") String timeLogicSymbol);
		
	/**
	 * 根据状态，当前时间，缴费状态查找广告信息
	 *
	 * @param status 广告状态
	 * @param currentTime 当前时间
	 * @param timeColumn 时间比较的字段
	 * @param timeLogicSymbol 时间逻辑操作符
	 * @param isPay 缴费状态
	 * @return 广告信息
	 */
	List<ColumnAdvert> getAdvertStartForJob(@Param("status") Integer status, @Param("currentTime") Date currentTime, @Param("timeColumn")
	 					String timeColumn,@Param("timeLogicSymbol") String timeLogicSymbol);
	
	//begin 广告张数的限制  审核代理商上传的张数  	add by zhulq  2016-7-16
	/**
	 * @Description: 审核代理商上传的张数 
	 * @param advert 广告实体
	 * @return   广告张数
	 * @author zhulq
	 * @date 2016年7月25日
	 */
	int findAcrossTimeAdvert(ColumnAdvert advert);
	//end 广告张数的限制  审核代理商上传的张数   	add by zhulq  2016-7-16
	
	//begin 广告张数的限制  审核代理商上传的张数  	add by zhulq  2016-7-16
	/**
	 * @Description: 根据城市名称获取正在进行中的手机开门页广告
	 * @param params params
	 * @return   ColumnAdvert
	 * @author zhulq
	 * @date 2016年7月25日
	 */
	List<ColumnAdvert> findMobileDoorAdvert(Map<String,Object> params);
	//end 广告张数的限制  审核代理商上传的张数   	add by zhulq  2016-7-16
	
	// begin  add　　by zhulq  获取默认的广告图片  2016-10-18
	/** 
	 * @Description: 获取默认的广告图片
	 * @param map  map
	 * @return ColumnAdvert
	 * @author zhulq
	 * @date 2016年10月18日
	 */
	ColumnAdvert listDefaultForPos(Map<String,Object> map);
	// begin  add　　by zhulq  获取默认的广告图片  2016-10-18
	
	// begin  add　　by zhangkn  获取广告商品列表
	/**
	 * @Description: 获取广告商品列表
	 * @param map  查询参数
	 * @return list
	 * @author zhangkn
	 * @date 2016年10月18日
	 */
	List<Map<String,Object>> listGoodsForAdvert(Map<String, Object> map);
	// end  add　　by zhangkn  获取广告商品列表
	
	/**
	 * 根据活动url查询广告信息
	 * @param targetUrl 活动url
	 * tuzhiding
	 * @return
	 */
	ColumnAdvert getAdvertForTargetURl(@Param("targetUrl") String targetUrl);
	
	//Begin V1.2 added by tangy  2016-11-28
	/**
	 * @Description:    广告区域统计
	 * @param advert    广告信息 
	 * @return HashMap<String,Integer>  
	 * @author tangy
	 * @date 2016年11月28日
	 */
	List<HashMap<String, Integer>> findAdvertRestrictByArea(ColumnAdvert advert);
	//End added by tangy
	
	/**
	 * @Description: 查询广告列表给app接口
	 * @param advertQueryParamDto 查询参数
	 * @return
	 * @author zengjizu
	 * @date 2017年1月3日
	 */
	List<ColumnAdvert> findForApp(ColumnAdvertQueryParamDto advertQueryParamDto);
	
	/**
	 * @Description: 根据广告id获取广告商品列表
	 * @param advertId  广告id
	 * @return list
	 * @author xuzq01
	 * @param storeId 
	 * @date 2017年02月08日
	 */
	List<GoodsStoreActivitySkuDto> findAdvertGoodsByAdvertId(@Param("advertId")String advertId, @Param("storeId")String storeId);
	
	
	/**
	 * @Description: 根据店铺活动类型 活动商品列表
	 * @return list
	 * @author tuzhd
	 * @param storeId 
	 * @date 2017年03月13日
	 */
	List<GoodsStoreActivitySkuDto> findGoodsByActivityType(@Param("storeId")String storeId,@Param("saleType")Integer saleType);
}