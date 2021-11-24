package com.ssafy.smartstore.controller.rest;

import com.ssafy.smartstore.model.dto.Order;
import com.ssafy.smartstore.model.dto.OrderDetail;
import com.ssafy.smartstore.model.dto.Stamp;
import com.ssafy.smartstore.model.dto.User;
import com.ssafy.smartstore.model.service.OrderService;
import com.ssafy.smartstore.model.service.StampService;
import com.ssafy.smartstore.model.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/order")
@CrossOrigin("*")
public class OrderRestController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private StampService stampService;

    @Autowired
    private UserService userService;

    @PostMapping
    @ApiOperation(value="order 객체를 저장하고 추가된 Order의 id를 반환한다.", response = Integer.class)
    @Transactional
    public Integer makeOrder(@RequestBody Order order) {
        orderService.makeOrder(order);

        // 총 수량 계산
        int quantity = 0;

        for (OrderDetail o : order.getDetails()) {
            o.setOrderId(order.getId());

            // OrderDetail DB에 저장
            orderService.makeOrderDetail(o);
            quantity += o.getQuantity();
        }

        // 스탬프 기록 반영
        Stamp stamp = new Stamp(order.getUserId(), order.getId(), quantity);
        stampService.addStamp(stamp);

        User user = userService.select(order.getUserId());
        user.setStamps(quantity);
        userService.updateStamp(user);

        return order.getId();
    }

    @GetMapping("/{orderId}")
    @ApiOperation(value="{orderId}에 해당하는 주문의 상세 내역을 목록 형태로 반환한다."
            + "이 정보는 사용자 정보 화면의 주문 내역 조회에서 사용된다.", response = List.class)
    public List<Map> getOrderDetail(@PathVariable Integer orderId) {
        return orderService.selectOrderTotalInfo(orderId);
    }

    @GetMapping("/byUser")
    @ApiOperation(value="{id}에 해당하는 사용자의 최근 1개월간 주문 내역을 반환한다."
            + "반환 정보는 1차 주문번호 내림차순, 2차 주문 상세 내림차순으로 정렬된다.", response = List.class)
    public List<Map<String, Object>> getLastMonthOrder(String id) {
        return orderService.getLastMonthOrder(id);
    }

    @PutMapping("/complete")
    @ApiOperation(value = "사용자의 주문 내역을 완료 처리합니다. (관리자용)")
    @Transactional
    public int setComplete(Integer orderId) {
        return orderService.setComplete(orderId);
    }
}
