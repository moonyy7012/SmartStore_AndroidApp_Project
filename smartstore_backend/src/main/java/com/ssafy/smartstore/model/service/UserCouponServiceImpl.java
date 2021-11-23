package com.ssafy.smartstore.model.service;

import com.ssafy.smartstore.model.dao.UserCouponDao;
import com.ssafy.smartstore.model.dto.UserCoupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserCouponServiceImpl implements UserCouponService{

    @Autowired
    private UserCouponDao userCouponDao;


    @Override
    public List<Map<String, Object>> getCouponByUserId(String userId) {
        return userCouponDao.selectByUserId(userId);
    }

    @Override
    public List<Map<String, Object>> getUsedCouponByUserId(String userId) {
        return userCouponDao.selectUsedCouponByUserId(userId);
    }

    @Override
    public List<String> getExpiredCouponId() {
        return userCouponDao.selectExpiredIdByDate();
    }

    @Override
    public Map<String, Object> selectCoupon(Integer id) {
        return userCouponDao.select(id);
    }

    @Override
    public int usingCoupon(Integer id) {
        return userCouponDao.update(id);
    }

    @Override
    public int setExpired(String id) {
        return userCouponDao.updateExpired(id);
    }

    @Override
    public int insertCoupon(UserCoupon coupon) {
        return userCouponDao.insert(coupon);
    }

}
