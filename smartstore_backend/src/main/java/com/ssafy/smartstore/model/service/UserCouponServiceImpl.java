package com.ssafy.smartstore.model.service;

import com.ssafy.smartstore.model.dao.CouponDao;
import com.ssafy.smartstore.model.dto.UserCoupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserCouponServiceImpl implements UserCouponService{

    @Autowired
    private CouponDao couponDao;


    @Override
    public List<Map<String, Object>> getCouponByUserId(String userId) {
        return couponDao.selectByUserId(userId);
    }

    @Override
    public List<Map<String, Object>> getUsedCouponByUserId(String userId) {
        return couponDao.selectUsedCouponByUserId(userId);
    }

    @Override
    public int usingCoupon(String id) {
        return couponDao.update(id);
    }

    @Override
    public int insertCoupon(UserCoupon coupon) {
        return couponDao.insert(coupon);
    }

}
