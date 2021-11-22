package com.ssafy.smartstore.model.service;

import com.ssafy.smartstore.model.dao.CouponDao;
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
    public int usingCoupon(String id) {
        return couponDao.update(id);
    }
}
