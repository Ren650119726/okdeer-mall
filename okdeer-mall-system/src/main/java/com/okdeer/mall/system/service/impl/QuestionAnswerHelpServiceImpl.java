package com.okdeer.mall.system.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.system.dto.QuestionAnswerHelpDto;
import com.okdeer.mall.system.mapper.QuestionAnswerHelpMapper;
import com.okdeer.mall.system.service.QuestionAnswerHelpService;

/**
 * ClassName: QuestionAnswerHelpServiceImpl 
 * @Description: 问题帮助实现类
 * @author zhulq
 * @date 2016年11月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			 2016年11月8日 			zhulq
 */
@Service
public class QuestionAnswerHelpServiceImpl implements QuestionAnswerHelpService {

	/**
	 * 邀请记录mapper
	 */
	@Autowired QuestionAnswerHelpMapper questionMapper;
	
	@Override
	public PageUtils<QuestionAnswerHelpDto> findList(QuestionAnswerHelpDto questionAnswerHelp, int pageNum, int pageSize)
			throws ServiceException {
		PageHelper.startPage(pageNum, pageSize, true);
		List<QuestionAnswerHelpDto> re = questionMapper.selectList(questionAnswerHelp);
		if (re == null) {
			re = new ArrayList<QuestionAnswerHelpDto>();
		}
		PageUtils<QuestionAnswerHelpDto> pageUtils = new PageUtils<QuestionAnswerHelpDto>(re);
		return pageUtils;
	}

	@Override
	public QuestionAnswerHelpDto findById(String id) throws ServiceException {
		return questionMapper.selectById(id);
	}

}
