package com.ssafy.smartstore.model.service;

import com.ssafy.smartstore.model.dao.ProductDao;
import com.ssafy.smartstore.model.dto.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductDao productDao;

    @Override
    public List<Product> getProductList() {
        return productDao.selectAll();
    }

    @Override
    public List<Map<String, Object>> selectWithComment(Integer productId) {
        return productDao.selectWithComment(productId);
    }

    @Override
    public int insert(Product product) {
        return productDao.insert(product);
    }

    @Override
    public int delete(Integer productId) {
        return productDao.delete(productId);
    }

    @Override
    public int update(Product product) {
        return productDao.update(product);
    }
}
