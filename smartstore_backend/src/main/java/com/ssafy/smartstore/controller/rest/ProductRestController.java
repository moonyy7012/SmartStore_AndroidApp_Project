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

    @PostMapping
    @ApiOperation(value="상품 정보를 DB에 저장합니다. (관리자용)")
    public int insert(@RequestBody Product product) {
        return productService.insert(product);
    }

    @DeleteMapping("/delete")
    @ApiOperation(value="id에 해당하는 상품 정보를 삭제합니다. (관리자용)")
    public int delete(Integer productId) {
        return productService.delete(productId);
    }

    @PutMapping("/update")
    @ApiOperation(value = "상품 정보를 업데이트합니다. (관리자용)")
    public int update(@RequestBody Product product) {
        return productService.update(product);
    }
}
