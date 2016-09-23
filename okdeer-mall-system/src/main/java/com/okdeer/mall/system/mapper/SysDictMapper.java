package com.okdeer.mall.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.archive.system.entity.SysDict;
import com.okdeer.common.system.entity.SysDictVO;

/**
 * DESC: 系统数据字典Mapper
 * @author LIU.W
 * @DATE 2015年7月29日上午10:58:35
 * @version 0.1.0
 * @copyright www.yschome.com
 */
public interface SysDictMapper {
	
    /**
     * @desc 全字段添加j
     * @param record 
     * @return	执行条数
     */
    int insert(SysDict record);

    /**
     * @desc 可选字段添加
     * @param record	
     * @return	执行条数
     */
    int insertSelective(SysDict record);

    /**
     * @desc 通过主键查询
     * @param id 主键
     */
    SysDict selectByPrimaryKey(String id);

    /**
     * @desc 可选字段根据主键更新
     * @return	执行条数
     */
    int updateByPrimaryKeySelective(SysDict record);

    /**
     * @desc 全字段根据主键更新
     * @param record 
     * @return 执行条数
     */
    int updateByPrimaryKey(SysDict record);
    
    /**
	 * @desc 根据主键删除
	 * @param id 主键
	 * @return	执行条数
	 */
    int deleteByPrimaryKey(String id);
	
    
    List<SysDict> selectByParams(@Param("params")Map<String,Object> params);
    
    /**
     * @desc 检查标签是否唯一
     * @param label
     * @return
     */
    public SysDict checkLabel(SysDict sysDict);
    
	/**
	 * @desc 根据标签类型查询字典信息列表 interface
	 * @param type 标签类型
	 * @return 字典信息列表
	 */
	List<SysDictVO> findDictListByType(@Param(value="type")String type);
	
	/**
	 * @desc 根据字典值和字典类型查询字典信息
	 * @param value 字典值
	 * @param type 字典类型
	 * @return 字典
	 */
	SysDict findDictByValueAndType(@Param(value = "value") String value, @Param(value = "type") String type);
	
	/**
	 * 查询数据字典信息 
	 *
	 * @param type 类型
	 * @return List 结果集
	 */
	List<SysDict> selectByType(@Param(value = "type") String type);
    
}
