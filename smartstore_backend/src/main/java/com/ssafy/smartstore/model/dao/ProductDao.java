package com.ssafy.smartstore.model.dao;

import com.ssafy.smartstore.model.dto.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface ProductDao {
    int insert(Product product);

    int update(Product product);

    int delete(Integer productId);

    Product select(Integer productId);

    List<Product> selectAll();

    // backend 관통 과정에서 추가됨.
    List<Map<String, Object>> selectWithComment(Integer productId);
}
