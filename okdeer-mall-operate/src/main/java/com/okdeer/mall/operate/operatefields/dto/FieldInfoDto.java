package com.okdeer.mall.operate.operatefields.dto;

import java.io.Serializable;

public class FieldInfoDto implements Serializable {

    /**
     * 序列化器
     */
    private static final long serialVersionUID = -2112777355270660461L;

    /**
     * 栏位标题
     */
    private String title;
    
    /**
     * 头图
     */
    private String headPic;
    
    /**
     * 头连接地址
     */
    private String target;
    
    /**
     * 栏位主键
     */
    private String id;
    
    /**
     * 栏位类型
     */
    private Integer type;

    /**
     * 业务Id
     */
    private String businessId;
    
    /**
     * 栏位名称
     */
    private String name;
    
    /**
     * 模板
     */
    private Integer template;

    /**
     * 指向类型
     */
    private Integer pointType;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTemplate() {
        return template;
    }

    public void setTemplate(Integer template) {
        this.template = template;
    }

    public Integer getPointType() {
        return pointType;
    }

    public void setPointType(Integer pointType) {
        this.pointType = pointType;
    }
    
}
