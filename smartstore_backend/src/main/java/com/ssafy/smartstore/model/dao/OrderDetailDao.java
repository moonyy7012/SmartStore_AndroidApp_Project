package com.ssafy.smartstore.model.dao;

import com.ssafy.smartstore.model.dto.OrderDetail;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface OrderDetailDao {
    int insert(OrderDetail detail);

    int delete(Integer detailId);

    OrderDetail select(Integer detailId);

    List<OrderDetail> selectAll();
}
