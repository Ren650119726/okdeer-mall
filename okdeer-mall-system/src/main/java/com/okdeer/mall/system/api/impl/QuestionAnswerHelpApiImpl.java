package com.okdeer.mall.system.api.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.system.dto.QuestionAnswerHelpDto;
import com.okdeer.mall.system.entity.QuestionAnswerHelp;
import com.okdeer.mall.system.service.QuestionAnswerHelpApi;
import com.okdeer.mall.system.service.QuestionAnswerHelpService;

/**
 * ClassName: QuestionAnswerHelpApiImpl 
 * @Description: 问题帮助service
 * @author zhulq
 * @date 2016年11月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2			 2016年11月10日 			zhulq
 */
@Service(version = "1.0.0")
public class QuestionAnswerHelpApiImpl implements QuestionAnswerHelpApi {

	/**
	 * service 接口
	 */
	@Autowired
	QuestionAnswerHelpService questionAnswerHelpService;
	
	@Override
	public PageUtils<QuestionAnswerHelpDto> findList(QuestionAnswerHelpDto questionAnswerHelp, int pageNum, int pageSize)
			throws ServiceException {
		return questionAnswerHelpService.findList(questionAnswerHelp, pageNum, pageSize);
	}

	@Override
	public QuestionAnswerHelpDto findById(String id) throws ServiceException {
		return questionAnswerHelpService.findById(id);
	}

}
