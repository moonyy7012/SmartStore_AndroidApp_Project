package com.ssafy.smartstore.model.dao;

import com.ssafy.smartstore.model.dto.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface OrderDao {
    int insert(Order order);

    int update(Order order);

    int delete(Integer orderId);

    Order select(Integer orderId);

    List<Order> selectAll();

    String getLastOrder();

    Order selectWithDetail(int id);

    List<Order> selectByUser(String userId);
    // back end 관통에서 추가함
    List<Map> selectOrderTotalInfo(int id);

    /**
     * 최근 1개월의 주문 내역을 반환한다.
     * 관통 6단계에서 추가됨
     * @param id
     * @return
     */
    List<Map<String, Object>> getLastMonthOrder(String id);

    // 고객의 주문을 완료 처리한다
    int setComplete(Integer orderId);
}
