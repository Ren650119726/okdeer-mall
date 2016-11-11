package com.okdeer.mall.system.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.okdeer.base.dal.IBaseCrudMapper;
import com.okdeer.mall.system.dto.QuestionAnswerHelpDto;
import com.okdeer.mall.system.entity.QuestionAnswerHelp;

/**
 * ClassName: QuestionAnswerHelp 
 * @Description: 问题帮助Mapper
 * @author zhulq
 * @date 2016年11月4日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2.0			 2016年11月4日 			zhulq
 */
@Repository
public interface QuestionAnswerHelpMapper  extends IBaseCrudMapper {

	/**
	 * @Description: 查询列表
	 * @param questionAnswerHelpDto    dto
	 * @return List
	 * @author zhulq
	 * @date 2016年11月4日
	 */
	List<QuestionAnswerHelpDto> selectList(QuestionAnswerHelpDto questionAnswerHelpDto);
	
	/**
	 * @Description: 根据主键获取信息
	 * @param id 主键
	 * @return QuestionAnswerHelp
	 * @author zhulq
	 * @date 2016年11月4日
	 */
	QuestionAnswerHelpDto selectById(@Param("id")String id);
	
}
