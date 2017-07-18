package com.okdeer.mall.ele.entity;

import java.io.Serializable;

/**
 * ClassName: ExpressChainStore
 *
 * @author wangf01
 * @Description: 添加门店
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public class ExpressChainStore implements Serializable {

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 门店联系信息(手机号或座机)
     */
    private String contactPhone;

    /**
     * 门店地址(64个汉字长度，支持汉字、符号、字母的组合)
     */
    private String address;

    /**
     * 门店经度
     */
    private String longitude;

    /**
     * 门店纬度
     */
    private String latitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
