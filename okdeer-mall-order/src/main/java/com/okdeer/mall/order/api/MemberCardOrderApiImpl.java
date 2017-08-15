package com.okdeer.mall.order.api;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.dto.MemberCardResultDto;
import com.okdeer.mall.order.dto.MemberTradeOrderDto;
import com.okdeer.mall.order.dto.PayInfoDto;
import com.okdeer.mall.order.dto.PayInfoParamDto;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.handler.MemberCardOrderService;
import com.okdeer.mall.order.service.MemberCardOrderApi;
/**
 * ClassName: MemberCardOrderApi 
 * @Description: 会员卡订单同步实现类
 * @author tuzhd
 * @date 2017年8月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V2.5.2			2017-08-08			tuzhd				会员卡订单同步类
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.MemberCardOrderApi")
public class MemberCardOrderApiImpl implements MemberCardOrderApi {
	private static final Logger logger = LoggerFactory.getLogger(MemberCardOrderApiImpl.class);
	 
	@Resource
	public MemberCardOrderService memberCardOrderService;
	
	/**
	 * @Description: 会员卡订单同步
	 * @param vo 同步记录
	 * @return MemberCardResultDto  返回结果
	 * @author tuzhd
	 * @throws Exception 
	 * @date 2017年8月8日
	 */
	public MemberCardResultDto<MemberTradeOrderDto> pushMemberCardOrder(MemberTradeOrderDto vo) throws Exception{
		return memberCardOrderService.pushMemberCardOrder(vo);
	}
	
	/**
	 * @Description: 提交会员卡订单
	 * @param orderId
	 * @throws Exception   
	 * @author tuzhd
	 * @date 2017年8月9日
	 */
	public Response<PlaceOrderDto> submitOrder(String orderId){
		Response<PlaceOrderDto> resp = new Response<PlaceOrderDto>();
		PlaceOrderDto data = new PlaceOrderDto();
		resp.setData(data);
		try{
			resp = memberCardOrderService.submitOrder(orderId,resp);
		}catch(Exception e){
			resp.setResult(ResultCodeEnum.FAIL);
			logger.error("提交会员卡订单失败",e);
		}
		return resp;
	}
	
	/**
	 * @Description: 获取会员卡信息接口
	 * @param userId  用户id
	 * @param deviceId 设备id
	 * @author tuzhd
	 * @date 2017年8月9日
	 */
	public String getMemberPayNumber(String userId,String deviceId){
		return memberCardOrderService.getMemberPayNumber(userId, deviceId);
	}

	
	/**
     * @Description: 根据订单id取消订单,已提交订单不能清除，会导致用户支付无法对上账
     * @param orderId   
     * @return void  
     * @author tuzhd
     * @date 2017年8月10日
     */
    public boolean cancelMemberCardOrder(String orderId){
    	return memberCardOrderService.cancelMemberCardOrder(orderId);
    }

    /**
	 * @Description: 获取支付信息
	 * @param dto
	 * @throws
	 * @author tuzhd
	 * @date 2017年8月10日
	 */
	public MemberCardResultDto<PayInfoDto> getPayInfo(PayInfoParamDto dto)throws Exception{
		return memberCardOrderService.getPayInfo(dto);
	}
    
	/**
	  * @Description: 移除会员卡信息接口  
	  * @param userId  用户id
	  * @param deviceId 设备id
	  * @author tuzhd
	  * @date 2017年8月9日
	  */
	public void removetMemberPayNumber(String memberPayNum){
		memberCardOrderService.removetMemberPayNumber(memberPayNum);
	}
	
	/**
     * @Description: 获取会员卡信息接口
     * @param memberPayNum  会员卡信息
	 * @author tuzhd
	 * @date 2017年8月9日
	 */
   public String getUserIdByMemberCard(String memberPayNum){
	  return memberCardOrderService.getUserIdByMemberCard(memberPayNum);
   }

}
