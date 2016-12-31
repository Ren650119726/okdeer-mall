package com.okdeer.mall.member.api;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.member.member.service.MemberApi;
import com.okdeer.mall.member.service.MemberService;

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.member.member.service.MemberApi")
public class MemberApiImpl implements MemberApi {

	@Autowired
	private MemberService memberService;

}
