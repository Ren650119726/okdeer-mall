package com.okdeer.mall.operate.entity;

/**
 * ClassName: ColumnOperationRelation
 *
 * @author wangf01
 * @Description: 运营栏目-关系
 * @date 2017年3月13日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public class ColumnOperationRelation {

    /**
     * 主键id
     */
    private String id;

    /**
     * 运营栏目id
     */
    private String columnOperationId;

    /**
     * 关联id（店铺id，店铺商品id）
     */
    private String relationId;

    /**
     * 关联值（字典表label）
     */
    private String relationValue;

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

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }

    public String getRelationValue() {
        return relationValue;
    }

    public void setRelationValue(String relationValue) {
        this.relationValue = relationValue;
    }
}
