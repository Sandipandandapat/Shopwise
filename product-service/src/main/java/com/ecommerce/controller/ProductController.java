package com.ecommerce.controller;

import com.ecommerce.dto.ProductRequestDto;
import com.ecommerce.dto.ProductResponseDto;
import com.ecommerce.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService=productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("{productId}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable String productId){
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("category/{category}")
    public ResponseEntity<List<ProductResponseDto>> getProductByCategory(@PathVariable String category){
        return ResponseEntity.ok(productService.getProductByCategory(category));
    }

    @GetMapping("/{productId}/exists")
    public ResponseEntity<Void> checkProductExists(@PathVariable String productId){
        productService.getProductById(productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("add")
    public ResponseEntity<List<ProductResponseDto>> addProduct(@RequestBody List<ProductRequestDto> request){
        return ResponseEntity.ok(productService.addProduct(request));
    }

}
