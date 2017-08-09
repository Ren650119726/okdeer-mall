package com.okdeer.mall.order.api;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.dto.MemberCardResultDto;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.handler.MemberCardOrderService;
import com.okdeer.mall.order.service.MemberCardOrderApi;
import com.okdeer.mall.order.vo.MemberTradeOrderVo;
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
	public MemberCardResultDto<MemberTradeOrderVo> pushMemberCardOrder(MemberTradeOrderVo vo) throws Exception{
		return memberCardOrderService.pushMemberCardOrder(vo);
	}
	
	/**
	 * @Description: 提交会员卡订单
	 * @param memberPayNum
	 * @throws Exception   
	 * @author tuzhd
	 * @date 2017年8月9日
	 */
	public Response<PlaceOrderDto> submitOrder(String memberPayNum){
		Response<PlaceOrderDto> resp = new Response<PlaceOrderDto>();
		try{
			resp = memberCardOrderService.submitOrder(memberPayNum,resp);
		}catch(Exception e){
			resp.setResult(ResultCodeEnum.SERVER_COLUMN_IS_CLOSED);
			logger.error(e.getMessage());
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
	  * @Description: 移除会员卡信息接口  
	  * @param userId  用户id
	  * @param deviceId 设备id
	  * @author tuzhd
	  * @date 2017年8月9日
	  */
	public void removetMemberPayNumber(String memberPayNum){
		memberCardOrderService.removetMemberPayNumber(memberPayNum);
	}

}
