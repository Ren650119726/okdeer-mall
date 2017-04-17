/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ColumnNativeSubjectMapper.java
 * @Date 2017-04-13 Created
 * ע�⣺�����ݽ���������¹��˾�ڲ����ģ���ֹ��й�Լ�������������ҵĿ��
 */
package com.okdeer.mall.operate.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.operate.dto.ColumnNativeSubjectParamDto;
import com.okdeer.mall.operate.entity.ColumnNativeSubject;

public interface ColumnNativeSubjectMapper extends IBaseMapper {

	List<ColumnNativeSubject> findList(ColumnNativeSubjectParamDto param);
}