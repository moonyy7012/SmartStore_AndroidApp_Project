package com.ssafy.smartstore.controller.rest;

import com.ssafy.smartstore.model.dto.Product;
import com.ssafy.smartstore.model.service.ProductService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/product")
@CrossOrigin("*")
public class ProductRestController {

    @Autowired
    ProductService productService;

    @GetMapping()
    @ApiOperation(value="전체 상품의 목록을 반환한다.", response = List.class)
    public List<Product> getProductList(){
        return productService.getProductList();
    }

    @GetMapping("/{productId}")
    @ApiOperation(value="{productId}에 해당하는 상품의 정보를 comment와 함께 반환한다."
            + "이 기능은 상품의 comment를 조회할 때 사용된다.", response = List.class)
    public List<Map<String, Object>> getProductWithComments(@PathVariable Integer productId){
        return productService.selectWithComment(productId);
    }
}
