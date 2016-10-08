package com.okdeer.mall.system.mapper;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.system.entity.SysRandCodeRecord;

/**
 * 邀请码记录Mapper
 * 
 * @project yschome-mall
 * @author zhaoqc
 * @date 2016年10月05日 上午21:33:56
 */
public interface SysRandCodeRecordMapper {

    /**
     * @desc 生成随机码
     *
     * @param sysRandCodeRecord 随机码实体
     */
    void saveSysRandCodeRecord(SysRandCodeRecord sysRandCodeRecord);
 
    /**
     * @desc 修改随机码
     *
     * @param sysRandCodeRecord 随机码实体
     */
    void updateSysRandCodeRecord(SysRandCodeRecord sysRandCodeRecord);
 
    /**
     * 根据随机码查询随机码实体
     * 
     * @param randCode 随机码
     * @return 随机码实体
     */
    SysRandCodeRecord findRecordByRandCode(@Param("randomCode") String randomCode);
}
