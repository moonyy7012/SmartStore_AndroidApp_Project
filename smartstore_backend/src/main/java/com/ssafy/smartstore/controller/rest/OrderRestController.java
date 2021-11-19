package com.ssafy.smartstore.controller.rest;

import com.ssafy.smartstore.model.dto.Order;
import com.ssafy.smartstore.model.service.OrderService;
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

    @PostMapping
    @ApiOperation(value="order 객체를 저장하고 추가된 Order의 id를 반환한다.", response = Integer.class)
    @Transactional
    public Integer makeOrder(@RequestBody Order order) {
        orderService.makeOrder(order);
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
}
