package com.ssafy.smartstore.controller.rest;

import com.ssafy.smartstore.model.service.UserCouponService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/coupon")
@CrossOrigin("*")
public class CouponRestController {

    @Autowired
    private UserCouponService cService;

    @GetMapping("/{userId}")
    @ApiOperation(value="사용자별로 사용 가능한 쿠폰 목록을 보여줍니다.", response = List.class)
    @Transactional
    public List<Map<String, Object>> getCouponInfoByUserId(@PathVariable String userId) {
        return cService.getCouponByUserId(userId);
    }

//    @PutMapping("/{couponId}")
//    @ApiOperation(value="사용자의 쿠폰을 사용 상태로 바꿔줍니다.", response = List.class)
//    @Transactional
//    public int usingCoupon(@PathVariable String couponId) {
//        return cService.usingCoupon(couponId);
//    }
}
