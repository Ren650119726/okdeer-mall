package com.okdeer.mall.operate.entity;

/**
 * ClassName: ColumnOperationVersion
 *
 * @author wangf01
 * @Description: 运营栏目-版本
 * @date 2017年3月13日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public class ColumnOperationVersion {

    /**
     * 主键id
     */
    private String id;

    /**
     * 运营栏目id
     */
    private String columnOperationId;

    /**
     * 客户端类型 0:管家版 3:便利店版
     */
    private Integer type;

    /**
     * 版本 例：V2.1
     */
    private String version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColumnOperationId() {
        return columnOperationId;
    }

    public void setColumnOperationId(String columnOperationId) {
        this.columnOperationId = columnOperationId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
