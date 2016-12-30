package com.okdeer.mall.points.util;

import com.okdeer.mall.common.utils.security.MD5;

/**
 * 
 * ClassName: TeshSignUtils 
 * @Description: 特奢汇签名
 * @author tangy
 * @date 2016年12月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     1.3.0          2016年12月17日                               tangy
 */
public class TeshSignUtils {
	
	/**
	 * 
	 * @Description: 生成签名
	 * @param signature   签名串
	 * @throws Exception   
	 * @return String     返回MD5加密的32位大写
	 * @author tangy
	 * @date 2016年12月17日
	 */
    public static String sign(String signature) throws Exception {
        return MD5.md5(signature).toUpperCase();
    }
	
}
