package com.ssafy.smartstore.controller.rest;

import com.ssafy.smartstore.model.dto.Coupon;
import com.ssafy.smartstore.model.dto.UserCoupon;
import com.ssafy.smartstore.model.service.CouponService;
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
    private UserCouponService userCouponService;

    @Autowired
    private CouponService couponService;

    @GetMapping("/{userId}")
    @ApiOperation(value="사용자별로 사용 가능한 쿠폰 목록을 최근 발급된 순서로 보여줍니다.", response = List.class)
    @Transactional
    public List<Map<String, Object>> getCouponInfoByUserId(@PathVariable String userId) {
        return userCouponService.getCouponByUserId(userId);
    }

    @GetMapping("/used/{userId}")
    @ApiOperation(value="사용자별로 쿠폰 사용 내역을 사용 일자 순서대로 보여줍니다.", response = List.class)
    @Transactional
    public List<Map<String, Object>> getUsedCouponInfoByUserId(@PathVariable String userId) {
        return userCouponService.getUsedCouponByUserId(userId);
    }

    @GetMapping("/use/coupon/{id}")
    @ApiOperation(value="사용할 쿠폰의 정보를 반환합니다.", response = Map.class)
    @Transactional
    public Map<String, Object> selectCoupon(@PathVariable Integer id) {
        return userCouponService.selectCoupon(id);
    }

    @PutMapping("/{id}")
    @ApiOperation(value="사용자의 쿠폰을 사용 상태로 바꿔줍니다.", response = Boolean.class)
    @Transactional
    public Boolean usingCoupon(@PathVariable Integer id) {
        return userCouponService.usingCoupon(id) > 0;
    }

    @PostMapping
    @ApiOperation(value="사용자가 발급받은 쿠폰을 DB에 저장합니다.", response = List.class)
    @Transactional
    public int insertCoupon(@RequestBody UserCoupon coupon) {
        return userCouponService.insertCoupon(coupon);
    }

    @PostMapping("/insert")
    @Transactional
    public int insert(@RequestBody Coupon coupon) {
        return couponService.insert(coupon);
    }

    @PostMapping("/select")
    @Transactional
    public List<Coupon> select() {
        return couponService.select();
    }

    @PutMapping("/update")
    @Transactional
    public int update(@RequestBody Coupon coupon) {
        return couponService.update(coupon);
    }

}
