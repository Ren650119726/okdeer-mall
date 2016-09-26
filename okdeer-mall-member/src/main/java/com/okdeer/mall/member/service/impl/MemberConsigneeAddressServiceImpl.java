/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: MemberConsigneeAddressServiceImpl.java 
 * @Date: 2015年11月26日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */

package com.okdeer.mall.member.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.system.entity.SysUser;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.mall.member.mapper.MemberConsigneeAddressMapper;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.member.enums.AddressDefault;
import com.okdeer.mall.member.member.enums.AddressSource;
import com.okdeer.mall.member.member.enums.AddressType;
import com.okdeer.mall.member.member.service.MemberConsigneeAddressServiceApi;
import com.okdeer.mall.member.member.vo.MemberConsigneeAddressVo;
import com.okdeer.mall.member.member.vo.UserAddressVo;
import com.okdeer.mall.member.service.MemberConsigneeAddressService;

/**
 * 地址service实现类
 * 
 * @project yschome-mall
 * @author wusw
 * @date 2015年12月9日 上午9:19:32
 * 
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构4.1          2016年7月25日                               zengj				添加查询用户收货地址列表，针对服务订单 方法
 *     12978           2016年8月19日                               wusw				修改获取收货地址的详细地址（用于商城后台会员详情）
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.member.member.service.MemberConsigneeAddressServiceApi")
public class MemberConsigneeAddressServiceImpl
		implements MemberConsigneeAddressServiceApi, MemberConsigneeAddressService {

	/**
	 * 自动注入会员收货地址dao
	 */
	@Autowired
	private MemberConsigneeAddressMapper memberConsigneeAddressMapper;

	/**
	 * 收货地址mapper
	 */
	// private IAddressService addressService;

	@Override
	public List<String> findByUserId(String userId) throws ServiceException {
		List<MemberConsigneeAddress> addresses = memberConsigneeAddressMapper.selectByUserId(userId);
		List<String> list = new ArrayList<String>();
		for (MemberConsigneeAddress address : addresses) {
			// Begin 12978 update by wusw 20160819
			StringBuffer s = new StringBuffer("");
			if (address.getProvinceName() != null) {
				s.append(address.getProvinceName());
			}
			if (address.getCityName() != null) {
				s.append(address.getCityName());
			}
            if (address.getAreaName() != null) {
            	s.append(address.getAreaName());
			}
            if (address.getAreaExt() != null) {
            	s.append(address.getAreaExt());
			}
            if (StringUtils.isNotEmpty(s.toString())) {
            	list.add(s.toString());	
            }
			// End 12978 update by wusw 20160819
		}
		return list;
	}

	/**
	 * @desc 根据用户查询用户收货地址列表
	 *
	 * @param userId 用户ID
	 */
	public List<MemberConsigneeAddress> findListByUserId(String userId) throws ServiceException {
		List<MemberConsigneeAddress> addresses = memberConsigneeAddressMapper.selectByUserId(userId);
		return addresses;
	}

	/**
	 * @desc 根据店铺ID查询默认地址
	 *
	 * @param userId 用户ID
	 */
	public MemberConsigneeAddress findByStoreId(String userId) throws Exception {
		return memberConsigneeAddressMapper.getSellerDefaultAddress(userId);
	}

	/**
	 * @desc 根据条件查询收货地址列表（不分页）
	 * @author luosm
	 * @param memberConsigneeAddress 收货地址对象（查询条件）
	 */
	@Override
	public List<MemberConsigneeAddress> getList(MemberConsigneeAddress memberConsigneeAddress) throws ServiceException {
		memberConsigneeAddress.setDisabled(Disabled.valid);
		List<MemberConsigneeAddress> result = memberConsigneeAddressMapper.selectByParams(memberConsigneeAddress);
		return result;
	}

	/**
	 * @desc 根据条件查询收货地址列表（分页）
	 *
	 * @param memberConsigneeAddress 收货地址对象（查询条件）
	 * @param pageNumber 页码
	 * @param pageSize 页大小
	 * @return 分页对象
	 */
	@Override
	public PageUtils<MemberConsigneeAddress> getPage(MemberConsigneeAddress memberConsigneeAddress, int pageNumber,
			int pageSize) throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true);
		memberConsigneeAddress.setDisabled(Disabled.valid);
		List<MemberConsigneeAddress> result = memberConsigneeAddressMapper.selectByParams(memberConsigneeAddress);
		if (result == null) {
			result = new ArrayList<MemberConsigneeAddress>();
		}

		return new PageUtils<MemberConsigneeAddress>(result);
	}

	/**
	 * @desc 根据主键id获取店铺地址详细信息
	 *
	 * @param id 主键id
	 * @return 店铺地址对象
	 */
	@Override
	public MemberConsigneeAddress getConsigneeAddress(String id) {

		return memberConsigneeAddressMapper.selectByPrimaryKey(id);
	}

	/**
	 * @desc 添加收货地址信息
	 * @author luosm
	 * @param memberConsigneeAddress 收货地址对象
	 * @param currentOperateUser 当前登陆用户id
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addConsigneeAddress(MemberConsigneeAddress memberConsigneeAddress, SysUser currentOperateUser) {

		Date date = new Date();
		memberConsigneeAddress.setCreateTime(date);
		memberConsigneeAddress.setUpdateTime(date);
		memberConsigneeAddress.setCreateUserId(currentOperateUser.getId());
		memberConsigneeAddress.setUpdateUserId(currentOperateUser.getId());
		memberConsigneeAddress.setIsDefault(AddressDefault.NO);
		if (memberConsigneeAddress.getSource() != AddressSource.PSMS) {
			memberConsigneeAddress.setSource(AddressSource.LOCAL);
			if (currentOperateUser.getRelation() != null && currentOperateUser.getRelation().getStoreId() != null
					&& !currentOperateUser.getRelation().getStoreId().equals("")) {
				memberConsigneeAddress.setType(AddressType.SELLER);
				memberConsigneeAddress.setUserId(currentOperateUser.getRelation().getStoreId());
			} else {
				memberConsigneeAddress.setType(AddressType.BUYER);
				memberConsigneeAddress.setUserId(currentOperateUser.getId());
			}
		}
		memberConsigneeAddressMapper.insertSelective(memberConsigneeAddress);
	}

	/**
	 * @desc 修改收货地址信息
	 *
	 * @param memberConsigneeAddress 收货地址对象
	 * @param currentOperateUser 当前登陆用户id
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateConsigneeAddress(MemberConsigneeAddress memberConsigneeAddress, SysUser currentOperateUser) {
		memberConsigneeAddress.setUpdateTime(new Date());
		memberConsigneeAddress.setUpdateUserId(currentOperateUser.getId());
		memberConsigneeAddressMapper.updateByPrimaryKeySelective(memberConsigneeAddress);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateDefaultAddress(String id, SysUser currentOperateUser) throws ServiceException {
		// 店铺id
		String storeId = currentOperateUser.getRelation().getStoreId();
		MemberConsigneeAddress queryAddress = new MemberConsigneeAddress();
		queryAddress.setUserId(storeId);
		queryAddress.setType(AddressType.SELLER);
		queryAddress.setIsDefault(AddressDefault.YES);
		// 根据条件查询该店铺的默认地址
		List<MemberConsigneeAddress> addressList = memberConsigneeAddressMapper.selectByParams(queryAddress);
		if (addressList != null && addressList.size() > 0) {
			// 如果存在店铺默认地址，先将该默认地址改为非默认地址
			MemberConsigneeAddress memberConsigneeAddress = new MemberConsigneeAddress();
			memberConsigneeAddress.setId(addressList.get(0).getId());
			memberConsigneeAddress.setIsDefault(AddressDefault.NO);
			memberConsigneeAddress.setUpdateTime(new Date());
			memberConsigneeAddress.setUpdateUserId(currentOperateUser.getId());
			memberConsigneeAddressMapper.updateByPrimaryKeySelective(memberConsigneeAddress);
		}
		MemberConsigneeAddress memberConsigneeAddress = new MemberConsigneeAddress();
		memberConsigneeAddress.setId(id);
		memberConsigneeAddress.setIsDefault(AddressDefault.YES);
		memberConsigneeAddress.setUpdateTime(new Date());
		memberConsigneeAddress.setUpdateUserId(currentOperateUser.getId());
		memberConsigneeAddressMapper.updateByPrimaryKeySelective(memberConsigneeAddress);
	}

	/**
	 * @desc 批量删除收货地址
	 *
	 * @param ids 收货地址id
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteByIds(List<String> ids, String updateUserId) {
		if (ids != null && ids.size() > 0) {
			memberConsigneeAddressMapper.deleteByIds(ids, Disabled.invalid, new Date(), updateUserId);
		}
	}

	/**
	 * @desc 根据主键id删除店铺地址
	 *
	 * @param id 店铺地址id
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteById(String id, String updateUserId) {
		memberConsigneeAddressMapper.deleteById(id, Disabled.invalid, new Date(), updateUserId);
	}

	/**
	 * @desc 根据主键查询地址
	 */
	@Override
	public MemberConsigneeAddress findById(String addressId) {
		return memberConsigneeAddressMapper.selectByPrimaryKey(addressId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String addListByAppUser(MemberConsigneeAddress memberConsigneeAddress) throws ServiceException {
		// TODO Auto-generated method stub
		memberConsigneeAddressMapper.insertSelective(memberConsigneeAddress);
		String id = memberConsigneeAddress.getId();
		return id;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String editListByAppUser(MemberConsigneeAddress memberConsigneeAddress) throws ServiceException {
		// TODO Auto-generated method stub
		memberConsigneeAddressMapper.updateByPrimaryKeySelective(memberConsigneeAddress);
		String id = memberConsigneeAddress.getId();
		return id;
	}

	@Override
	public List<MemberConsigneeAddressVo> findAppUserList(String userId, String storeId) throws ServiceException {
		// TODO Auto-generated method stub
		List<MemberConsigneeAddressVo> list = memberConsigneeAddressMapper.selectByDistance(userId, storeId);

		return list;
	}

	/**
	 * 微信查用户地址，排除来源为物业的地址
	 * @Tzd
	 */
	@Override
	public List<MemberConsigneeAddressVo> findWxUserList(String userId, String storeId) throws ServiceException {
		// TODO Auto-generated method stub
		List<MemberConsigneeAddressVo> list = memberConsigneeAddressMapper.selectByWxDistance(userId, storeId);

		return list;
	}

	/**
	 * 微信用户根据地址id查询收货地址
	 * @author TZD
	 * @param 地址id
	 * @return MemberConsigneeAddressVo
	 * @throws ServiceException
	 */
	public MemberConsigneeAddressVo findWxUserById(String id, String storeId) throws ServiceException {
		return memberConsigneeAddressMapper.selectWxDistanceById(id, storeId);
	}

	@Override
	public List<MemberConsigneeAddress> findAppUserAddress(String userId) throws ServiceException {
		// TODO Auto-generated method stub
		List<MemberConsigneeAddress> memberConsigneeAddress = memberConsigneeAddressMapper.selectByUserId(userId);
		return memberConsigneeAddress;
	}

	@Override
	public MemberConsigneeAddress findAppUserById(String id) throws ServiceException {
		// TODO Auto-generated method stub
		MemberConsigneeAddress memberConsigneeAddress = memberConsigneeAddressMapper.selectByPrimaryKey(id);
		return memberConsigneeAddress;
	}

	/**
	 * 获取默认地址 
	 * 
	 * @author yangq
	 * @param userId
	 * @return
	 */
	@Override
	public MemberConsigneeAddress getSellerDefaultAddress(String userId) throws Exception {
		MemberConsigneeAddress memberConsigneeAddress = new MemberConsigneeAddress();
		memberConsigneeAddress = memberConsigneeAddressMapper.getSellerDefaultAddress(userId);
		return memberConsigneeAddress;
	}

	@Override
	public MemberConsigneeAddress selectAddressById(String id) throws Exception {

		MemberConsigneeAddress address = new MemberConsigneeAddress();
		address = memberConsigneeAddressMapper.selectAddressById(id);
		return address;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertSelective(MemberConsigneeAddress memberConsigneeAddress) {
		memberConsigneeAddressMapper.insertSelective(memberConsigneeAddress);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateByPrimaryKeySelective(MemberConsigneeAddress memberConsigneeAddress) throws ServiceException {
		return memberConsigneeAddressMapper.updateByPrimaryKeySelective(memberConsigneeAddress);
	}

	// Begin 重构4.1 add by zengj
	/**
	 * 
	 * @Description: 用户收货地址列表，针对服务订单 
	 * @param params 查询参数
	 * @return  返回查询结果集
	 * @author zengj
	 * @date 2016年7月25日
	 */
	@Override
	public List<Map<String, Object>> findUserAddressList(Map<String, Object> params) {
		return memberConsigneeAddressMapper.findUserAddressList(params);
	}

	/**
	 * 
	 * @Description: 查询用户默认收货地址
	 * @param params 查询参数
	 * @return   返回查询结果集
	 * @author zengj 
	 * @date 2016年8月11日
	 */
	@Override
	public Map<String, Object> findUserDefaultAddress(Map<String, Object> params) {
		return memberConsigneeAddressMapper.findUserDefaultAddress(params);
	}
	
	/**
	 * 
	 * @Description: 查询用户默认收货地址-针对秒杀商品
	 * @param params 查询参数
	 * @return   返回查询结果集
	 * @author zengj 
	 * @date 2016年8月11日
	 */
	@Override
	public Map<String, Object> findUserDefilatSeckillAddress(Map<String, Object> params) {
		return memberConsigneeAddressMapper.findUserDefilatSeckillAddress(params);
	}
	// End 重构4.1 add by zengj

	// Begin 友门鹿重构1.1 added by maojj 2016-09-24 
	@Override
	public List<UserAddressVo> findUserAddr(Map<String, Object> params) {
		List<UserAddressVo> addrList = null;
		String seckillRangeType = String.valueOf(params.get("seckillRangeType"));
		String storeAreaType = String.valueOf(params.get("storeAreaType"));
		String userId = String.valueOf(params.get("userId"));
		if ("0".equals(seckillRangeType) && "0".equals(storeAreaType)) {
			// 如果秒杀区域类型和店铺服务区域都是全国范围，则用户地址均有效
			addrList = memberConsigneeAddressMapper.findAddrWithUserId(userId);
		} else if ("0".equals(seckillRangeType) && "1".equals(storeAreaType)) {
			// 如果秒杀区域类型为全国范围，服务店铺服务范围为区域，则按照店铺服务范围查询用户地址
			addrList = memberConsigneeAddressMapper.findAddrWithStoreServRange(params);
		} else if ("1".equals(seckillRangeType) && "0".equals(storeAreaType)) {
			// 如果秒杀区域类型为区域，服务店铺服务范围为全国，则按照秒杀服务范围查询
			addrList = memberConsigneeAddressMapper.findAddrWithSeckillServRange(params);
		} else if ("1".equals(seckillRangeType) && "1".equals(storeAreaType)) {
			// 如果秒杀区域类型和店铺服务区域类型都是区域，则按照两者的交集查询用户地址
			addrList = memberConsigneeAddressMapper.findAddrWithServRange(params);
		}
		return addrList;
	}
	// End added by maojj 2016-09-24
}
