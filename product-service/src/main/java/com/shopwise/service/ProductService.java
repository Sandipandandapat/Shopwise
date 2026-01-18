package com.shopwise.service;

import com.shopwise.dto.ProductRequestDto;
import com.shopwise.dto.ProductResponseDto;

import java.util.List;

public interface ProductService {
    List<ProductResponseDto> addProduct(List<ProductRequestDto> requestDto);

    List<ProductResponseDto> getAllProducts();

    ProductResponseDto getProductById(String productId);

    List<ProductResponseDto> getProductByCategory(String category);
}
