package com.ssafy.smartstore.model.dao;

import com.ssafy.smartstore.model.dto.UserCoupon;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface CouponDao {

    int insert(UserCoupon userCoupon);

    int update(String id);

    List<Map<String, Object>> selectByUserId(String userId);
}
