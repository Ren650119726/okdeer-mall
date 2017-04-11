/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ColumnHomeIconVersionMapper.java
 * @Date 2017-04-07 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.operate.dto.ColumnHomeIconVersionDto;
import com.okdeer.mall.operate.entity.ColumnHomeIconVersion;

public interface ColumnHomeIconVersionMapper extends IBaseMapper {
    
    /**
     * @Description: 批量插入ICON与版本关联关系
     * @param list ICON与版本关系List
     * @return 影响行数
     * @author zhaoqc
     * @date 2017-04-07
     */
    public long insertBatch(@Param("list") List<ColumnHomeIconVersion> list);
    
    /**
     * @Description: 通过iconId查询ICON与版本关联关系
     * @param iconId 
     * @return List<ColumnHomeIconVersion>
     * @author zhaoqc
     * @date 2017-04-07
     */
    List<ColumnHomeIconVersionDto> findListByIconId(@Param("iconId") String iconId);
    
    /**
     * @Description: 通过iconId删除ICON与版本关联关系
     * @param iconId 
     * @author zhaoqc
     * @date 2017-04-07
     */
    void deleteByIconId(@Param("iconId") String iconId);
}