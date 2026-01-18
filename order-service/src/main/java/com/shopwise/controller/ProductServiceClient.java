package com.shopwise.controller;

import com.shopwise.dto.ProductResponseDto;
import com.shopwise.util.ProductServiceClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service",
        fallbackFactory = ProductServiceClientFallbackFactory.class
)
public interface ProductServiceClient {
    @GetMapping("/products/{productId}")
    public ProductResponseDto getProductByProductId(@PathVariable String productId);
}
