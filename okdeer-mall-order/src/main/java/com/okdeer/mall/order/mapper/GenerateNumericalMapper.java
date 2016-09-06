package com.okdeer.mall.order.mapper;

import java.util.Map;

import com.okdeer.mall.order.entity.GenerateNumericalNumber;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-17 15:22:36
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *    重构V4.1			2016-07-14			maojj			    新增保存编号的方法
 * 
 */
public interface GenerateNumericalMapper{
	
     String generateNumericalNumber(Map<String,String> map);
     
     String generateRandOrderNo(Map<String,String> map);
     
     String getNumericalNumber();
	
     // Begin added by maojj 2016-07-14
     /**
      * @Description: 保存编号
      * @param genNumber
      * @return   
      * @return int  
      * @throws
      * @author maojj
      * @date 2016年7月14日
      */
     int saveNumericalNumber(GenerateNumericalNumber genNumber);
     // End added by maojj 2016-07-14
}