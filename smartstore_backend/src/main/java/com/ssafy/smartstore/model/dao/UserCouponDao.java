package com.ssafy.smartstore.model.dao;

import com.ssafy.smartstore.model.dto.Coupon;
import com.ssafy.smartstore.model.dto.UserCoupon;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface UserCouponDao {

    int insert(UserCoupon userCoupon);

    int update(Integer id);

    int updateExpired(String id);

    Map<String, Object> select(Integer id);

    List<Map<String, Object>> selectByUserId(String userId);

    List<Map<String, Object>> selectUsedCouponByUserId(String userId);

    List<String> selectExpiredIdByDate();
}
