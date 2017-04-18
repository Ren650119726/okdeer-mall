package com.okdeer.mall.operate.operatefields.dto;

import java.io.Serializable;
import java.util.List;

public class OperateFieldDto implements Serializable {

    /**
     * 序列化器
     */
    private static final long serialVersionUID = 3444875178780465884L;

    /**
     * 栏位信息
     */
    private FieldInfoDto fieldInfo;
    
    /**
     * 栏位内容信息
     */
    private List<OperateFieldContentDto> contentList;

    public FieldInfoDto getFieldInfo() {
        return fieldInfo;
    }

    public void setFieldInfo(FieldInfoDto fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public List<OperateFieldContentDto> getContentList() {
        return contentList;
    }

    public void setContentList(List<OperateFieldContentDto> contentList) {
        this.contentList = contentList;
    }
    
}
