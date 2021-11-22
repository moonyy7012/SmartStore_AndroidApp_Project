package com.ssafy.smartstore.sceduler;

import com.ssafy.smartstore.model.service.UserCouponService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log
public class DailyScheduler {

    @Autowired
    private UserCouponService couponService;


    /* 매일 오전 0시 0분 0초에 만료된 쿠폰 처리*/
    @Scheduled(cron = "0 0 0 * * ?")
    public int runAt0amEveryday() {
        List<String> expiredCouponList = couponService.getExpiredCouponId();

        for (String id : expiredCouponList) {
            couponService.setExpired(id);
        }

        return 0;
    }
}
