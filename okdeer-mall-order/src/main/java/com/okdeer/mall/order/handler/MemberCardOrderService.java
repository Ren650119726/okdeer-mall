package com.okdeer.mall.order.handler;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.dto.MemberCardResultDto;
import com.okdeer.mall.order.dto.MemberTradeOrderDto;
import com.okdeer.mall.order.dto.PayInfoDto;
import com.okdeer.mall.order.dto.PayInfoParamDto;
import com.okdeer.mall.order.dto.PlaceOrderDto;

/**
 * ClassName: MemberCardOrderService 
 * @Description: 会员卡服务处理接口
 * @author tuzhd
 * @date 2017年8月9日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V2.5.2			2017-08-09			tuzhd				会员卡订单同步类
 */
public interface MemberCardOrderService {
	/**
	 * @Description: 提交会员卡订单
	 * @param orderId
	 * @throws Exception   
	 * @author tuzhd
	 * @date 2017年8月9日
	 */
	Response<PlaceOrderDto> submitOrder(String orderId,Response<PlaceOrderDto> resp) throws Exception;
	
	
	/**
	 * @Description: 会员卡订单同步
	 * @param vo 同步记录
	 * @return MemberCardResultDto  返回结果
	 * @author tuzhd
	 * @throws Exception 
	 * @date 2017年8月8日
	 */
	MemberCardResultDto<MemberTradeOrderDto> pushMemberCardOrder(MemberTradeOrderDto vo) throws Exception;
	
	/**
	  * @Description: 获取会员卡信息接口
	  * @param userId  用户id
	  * @param deviceId 设备id
	  * @author tuzhd
	  * @date 2017年8月9日
	  */
	String getMemberPayNumber(String userId,String deviceId);
	/**
	  * @Description: 移除会员卡信息接口  
	  * @param userId  用户id
	  * @param deviceId 设备id
	  * @author tuzhd
	  * @date 2017年8月9日
	  */
	void removetMemberPayNumber(String memberPayNum);
	
	/**
	 * @Description: 获取支付信息
	 * @param dto
	 * @throws
	 * @author tuzhd
	 * @date 2017年8月10日
	 */
	MemberCardResultDto<PayInfoDto> getPayInfo(PayInfoParamDto dto) throws ServiceException;
	
	/**
     * @Description: 根据订单id取消订单,已提交订单不能清除，会导致用户支付无法对上账
     * @param orderId   
     * @return void  
     * @author tuzhd
     * @date 2017年8月10日
     */
    boolean cancelMemberCardOrder(String orderId);
    
    /**
     * @Description: 获取会员卡信息接口
     * @param memberPayNum  会员卡信息
	 * @author tuzhd
	 * @date 2017年8月9日
	 */
   String getUserIdByMemberCard(String memberPayNum);
}
