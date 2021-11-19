package com.ssafy.smartstore.model.service;

import com.ssafy.smartstore.model.dao.OrderDao;
import com.ssafy.smartstore.model.dto.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderDao orderDao;

    @Override
    public void makeOrder(Order order) {
        orderDao.insert(order);
    }

    @Override
    public Order getOrderWithDetails(Integer orderId) {
        return orderDao.selectWithDetail(orderId);
    }

    @Override
    public List<Order> getOrderByUser(String id) {
        return orderDao.selectByUser(id);
    }

    @Override
    public void updateOrder(Order order) {
        orderDao.update(order);
    }

    @Override
    public List<Map> selectOrderTotalInfo(int id) {
        return orderDao.selectOrderTotalInfo(id);
    }

    @Override
    public List<Map<String, Object>> getLastMonthOrder(String id) {
        return orderDao.getLastMonthOrder(id);
    }
}
