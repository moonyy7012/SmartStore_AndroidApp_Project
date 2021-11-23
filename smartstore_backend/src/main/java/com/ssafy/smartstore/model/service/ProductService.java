package com.ssafy.smartstore.model.service;

import com.ssafy.smartstore.model.dto.Product;

import java.util.List;
import java.util.Map;

public interface ProductService {
    /**
     * 모든 상품 정보를 반환한다.
     * @return
     */
    List<Product> getProductList();

    /**
     * backend 관통 과정에서 추가됨
     * 상품의 정보, 판매량, 평점 정보를 함께 반환
     * @param productId
     * @return
     */
    List<Map<String, Object>> selectWithComment(Integer productId);

    int insert(Product product);

    int delete(Integer productId);

    int update(Product product);
}
