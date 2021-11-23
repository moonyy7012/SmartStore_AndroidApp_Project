package com.ssafy.smartstore.model.service;

import com.ssafy.smartstore.model.dto.Coupon;

import java.util.List;

public interface CouponService {

    int insert(Coupon coupon);

    List<Coupon> select();

    int update(Coupon coupon);
}
