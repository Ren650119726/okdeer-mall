package com.okdeer.mall.system.service;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.system.dto.QuestionAnswerHelpDto;

/**
 * ClassName: QuestionAnswerHelpService 
 * @Description: service
 * @author zhulq
 * @date 2016年11月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2			 2016年11月8日 			zhulq
 */
public interface QuestionAnswerHelpService {
	
	/**
	 * @Description: 获取问题列表
	 * @param questionAnswerHelp dto
	 * @param pageNum 页码
	 * @param pageSize 每页的行数
	 * @return PageUtils
	 * @throws ServiceException 异常
	 * @author zhulq
	 * @date 2016年11月11日
	 */
	PageUtils<QuestionAnswerHelpDto> findList(QuestionAnswerHelpDto questionAnswerHelp, int pageNum, int pageSize)
			throws ServiceException;
	
	/**
	 * @Description: 根据id'获取详情
	 * @param id 主键id
	 * @return
	 * @throws ServiceException
	 * @author zhulq
	 * @date 2016年11月11日
	 */
	QuestionAnswerHelpDto findById(String id) throws ServiceException;
}
