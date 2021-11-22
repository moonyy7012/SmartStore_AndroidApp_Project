package com.ssafy.smartstore.model.service;

import java.util.List;
import java.util.Map;

public interface UserCouponService {
    List<Map<String, Object>> getCouponByUserId(String userId);

    int usingCoupon(String id);
}
