package com.ssafy.smartstore.model.dto;

import java.util.Date;

public class UserCoupon {
    private Integer id;
    private String userId;
    private Integer couponId;
    private Date publishTime;
    private Date validate;
    private String isUsed;
    private Date useTime;

    public UserCoupon(Integer id, String userId, Integer couponId, Date publishTime, Date validate, String isUsed, Date useTime) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.publishTime = publishTime;
        this.validate = validate;
        this.isUsed = isUsed;
        this.useTime = useTime;
    }

    public UserCoupon(String userId, Integer couponId, Date validate) {
        this.userId = userId;
        this.couponId = couponId;
        this.validate = validate;
    }

    public UserCoupon() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Date getValidate() {
        return validate;
    }

    public void setValidate(Date validate) {
        this.validate = validate;
    }

    public String getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(String isUsed) {
        this.isUsed = isUsed;
    }

    public Date getUseTime() {
        return useTime;
    }

    public void setUseTime(Date useTime) {
        this.useTime = useTime;
    }

    @Override
    public String toString() {
        return "UserCoupon [id = " + id + ", userId = " + userId + ", couponId = " + couponId
                + ", publishTime = " + publishTime + ", validate = " + validate + ", isUsed = "
                + isUsed + ", useTime = " + useTime + "]" ;
    }
}
