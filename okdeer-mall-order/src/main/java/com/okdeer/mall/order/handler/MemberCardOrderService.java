package com.okdeer.mall.order.handler;

import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.dto.MemberCardResultDto;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.vo.MemberTradeOrderVo;

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
	 * @param memberPayNum
	 * @throws Exception   
	 * @author tuzhd
	 * @date 2017年8月9日
	 */
	Response<PlaceOrderDto> submitOrder(String memberPayNum,Response<PlaceOrderDto> resp) throws Exception;
	
	
	/**
	 * @Description: 会员卡订单同步
	 * @param vo 同步记录
	 * @return MemberCardResultDto  返回结果
	 * @author tuzhd
	 * @throws Exception 
	 * @date 2017年8月8日
	 */
	MemberCardResultDto<MemberTradeOrderVo> pushMemberCardOrder(MemberTradeOrderVo vo) throws Exception;
	
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
}
