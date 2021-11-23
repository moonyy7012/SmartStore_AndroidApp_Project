package com.ssafy.smartstore.model.service;

import com.ssafy.smartstore.model.dao.CouponDao;
import com.ssafy.smartstore.model.dto.Coupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponDao couponDao;

    @Override
    public int insert(Coupon coupon) {
        return couponDao.insert(coupon);
    }

    @Override
    public List<Coupon> select() {
        return couponDao.select();
    }

    @Override
    public int update(Coupon coupon) {
        return couponDao.update(coupon);
    }
}
