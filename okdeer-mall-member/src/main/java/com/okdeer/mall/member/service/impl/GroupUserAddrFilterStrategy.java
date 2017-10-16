package com.okdeer.mall.member.mapper;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.enums.ActivityBusinessType;
import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.mall.member.bo.UserAddressFilterCondition;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.service.AddressFilterStrategy;

/**
 * ClassName: GroupUserAddrFilterStrategy 
 * @Description: 团购用户地址过滤策略
 * @author maojj
 * @date 2017年10月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.6.3 		2017年10月11日				maojj
 */
@Service("groupUserAddrFilterStrategy")
public class GroupUserAddrFilterStrategy implements AddressFilterStrategy {

	@Override
	public boolean isOutRange(MemberConsigneeAddress addrInfo,
			UserAddressFilterCondition filterCondition) {
		ActivityDiscount actInfo = filterCondition.getActivityInfo();
		assertNotNull("团购活动信息不能为空", actInfo);
		// 选择是否参加，0：可以参加，1：不可以参加
		WhetherEnum limitRangeType = actInfo.getLimitRangeType();
		if (actInfo.getLimitRange() == AreaType.national) {
			// 如果是全国，标识不限
			return false;
		}
		Map<ActivityBusinessType, List<String>> areaLimitCondition = filterCondition.getAreaLimitCondition();
		assertNotNull("区域限制条件不能为空", areaLimitCondition);
		ActivityBusinessType limitType = null;
		List<String> areaIdList = null;
		for (Map.Entry<ActivityBusinessType, List<String>> entry : areaLimitCondition.entrySet()) {
			limitType = entry.getKey();
			areaIdList = entry.getValue();
			if (limitType == ActivityBusinessType.PROVINCE) {
				if (limitRangeType == WhetherEnum.whether && areaIdList.contains(addrInfo.getProvinceId())) {
					// 如果是反选，如果当前地址所属省在排除列表中，表示该地址超出范围
					return true;
				} else if (limitRangeType == WhetherEnum.not && areaIdList.contains(addrInfo.getProvinceId())) {
					// 如果是正选，且当前省份在列表中，表示未超出范围
					return false;
				}
			}
			if (limitType == ActivityBusinessType.CITY) {
				if (limitRangeType == WhetherEnum.whether && areaIdList.contains(addrInfo.getCityId())) {
					// 如果是反选，如果当前地址所属市在排除列表中，表示该地址超出范围
					return true;
				} else if (limitRangeType == WhetherEnum.not && areaIdList.contains(addrInfo.getCityId())) {
					return false;
				}
			}
		}
		// 如果地址正选，走到此处，标识当前地址不在设定的省份和地址范围之内，即超出范围
		// 如果地址反选，走到此处，标识当前地址不在设定的省份和地址范围之内，即未超出范围
		return limitRangeType == WhetherEnum.not;
	}
}
