/** 
 *@Project: yschome-mall-mobile 
 *@Author: wangf01
 *@Date: 2016年8月5日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.system.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import com.okdeer.mall.common.utils.DateUtils;

/**
 * ClassName: JsonDateUtil 
 * @Description: json格式转换帮助类
 * @author wangf01
 * @date 2016年8月5日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 *     重构V4.1.0          2016年8月5日               wangf01             新增编辑  
 */

public class ConvertUtil {
	
	/**
	 * 保留小数位数
	 */
	public static final Integer BIGDECIMAL_SCALE_NUM = 2;
	
	/**
	 * 四舍五入保留类型 1直接删除多余的小数位
	 */
	public static final Integer BIGDECIMAL_SCALE_TYPE_ONE = 1;
	
	/**
	 * 四舍五入保留类型 2进位处理
	 */
	public static final Integer BIGDECIMAL_SCALE_TYPE_TWO = 2;
	
	/**
	 * 四舍五入保留类型 3四舍五入
	 */
	public static final Integer BIGDECIMAL_SCALE_TYPE_THREE = 3;

	/**
	 * 获取主机所处时区的对象
	 */
	private static final String TZID = TimeZone.getDefault().getID();

	/**
	 * 时间函数所使用的默认时区
	 */
	private static final TimeZone TZ = TimeZone.getTimeZone(TZID);
	
	// Begin added by maojj 2016-09-23
	private static final String DEFAULT_NULL_STR = "";
	// End added by maojj 2016-09-23

	/**
	 * 
	 * @Description: 时间格式转换
	 * @param strTime String 搜索引擎时间数据  yyyy-MM-dd'T'HH:mm:sssxxx
	 * @param formatter SimpleDateFormat 格式化正确的时间格式 new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
	 * @param sdf SimpleDateFormat 要转换的时间格式
	 * @return String 转换后的时间格式  yyyy-MM-dd HH:mm:ss  
	 * @throws ParseException  转换异常 
	 * @author wangf01
	 * @date 2016年8月5日
	 */
	public static String dateConvert(String strTime, SimpleDateFormat formatter, SimpleDateFormat sdf)
			throws ParseException {
		// Begin add by zengj
		// 如果时间为空，默认为当前时间展示
		if (StringUtils.isBlank(strTime)) {
			strTime = sdf.format(DateUtils.getSysDate());
		}
		// End add by zengj
		formatter.setTimeZone(TZ);
		Date convertTime = formatter.parse(strTime);
		return sdf.format(convertTime);
	}

	/**
	 * 
	 * @Description: 价格字段的BigDecimal转换
	 * @param obj Object 需要转换的值
	 * @param num Integer 保存的小数位数
	 * @param type Integer 保存小数的类型 1：直接删除多余的小数位 2：进位处理 3：四舍五入
	 * @return priceDb  BigDecimal
	 * @author wangf01
	 * @date 2016年8月12日
	 */
	public static BigDecimal priceConvertToBigDecimal(Object obj, Integer num, Integer type) {
		return bigDecimalConvert(obj, num, type);
	}

	/**
	 * 
	 * @Description: 价格字段的String转换
	 * @param obj Object 需要转换的值
	 * @param num Integer 保存的小数位数
	 * @param type Integer 保存小数的类型 1：直接删除多余的小数位 2：进位处理 3：四舍五入
	 * @return resultPrice  String
	 * @author wangf01
	 * @date 2016年8月12日
	 */
	public static String priceConvertToString(Object obj, Integer num, Integer type) {
		BigDecimal resultPrice = bigDecimalConvert(obj, num, type);
		if (resultPrice != null) {
			return resultPrice.toString();
		}
		return "";
	}

	/**
	 * 
	 * @Description: 价格字段的转换
	 * @param obj Object 需要转换的值
	 * @param num Integer 保存的小数位数
	 * @param type Integer 保存小数的类型 1：直接删除多余的小数位 2：进位处理 3：四舍五入
	 * @return priceDb  BigDecimal
	 * @author wangf01
	 * @date 2016年8月16日
	 */
	private static BigDecimal bigDecimalConvert(Object obj, Integer num, Integer type) {
		// setScale(1,BigDecimal.ROUND_DOWN)直接删除多余的小数位
		// setScale(1,BigDecimal.ROUND_UP)进位处理
		// setScale(1,BigDecimal.ROUND_HALF_UP)四舍五入
		// Begin add by zengj
		// 如果金额为空，默认为0展示
		String price = String.valueOf(obj);
		if (StringUtils.isBlank(price)) {
			price = BigDecimal.ZERO.toString();
		}
		// End add by zengj
		BigDecimal priceDb = null;
		switch (type) {
			case 1:
				priceDb = new BigDecimal(price).setScale(num, BigDecimal.ROUND_DOWN);
				break;
			case 2:
				priceDb = new BigDecimal(price).setScale(num, BigDecimal.ROUND_UP);
				break;
			case 3:
				priceDb = new BigDecimal(price).setScale(num, BigDecimal.ROUND_HALF_UP);
				break;
			default:
				priceDb = new BigDecimal(price).setScale(num, BigDecimal.ROUND_DOWN);
				break;
		}
		return priceDb;
	}

	/**
	 * @Description: 格式化价格
	 * @param price 价格
	 * @return   
	 * @author maojj
	 * @date 2016年9月23日
	 */
	public static String format(BigDecimal price){
		return format(price,BIGDECIMAL_SCALE_NUM,BIGDECIMAL_SCALE_TYPE_ONE);
	}
	
	/**
	 * @Description: 格式化价格参数
	 * @param price
	 * @param num
	 * @param type
	 * @return   
	 * @author maojj
	 * @date 2016年9月23日
	 */
	public static String format(BigDecimal price,Integer num, Integer type){
		if(price == null){
			return "0.00";
		}
		return priceConvertToString(price,num,type);
	}
	
	/**
	 * @Description: 格式化字符串
	 * @param str
	 * @return   
	 * @author maojj
	 * @date 2016年9月23日
	 */
	public static Integer parseInt(String str){
		String convertStr = format(str,DEFAULT_NULL_STR);
		return Integer.parseInt(convertStr);
	}
	
	public static String format(String str){
		return format(str,DEFAULT_NULL_STR);
	}
	
	/**
	 * @Description: 格式化字符串对象
	 * @param str
	 * @param defaultStr
	 * @return   
	 * @author maojj
	 * @date 2016年9月23日
	 */
	public static String format(String str,String defaultStr){
		return str == null ? defaultStr : str;
	}
}
