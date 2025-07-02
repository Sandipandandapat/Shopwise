package com.ecommerce.service;

import com.ecommerce.dto.ProductRequestDto;
import com.ecommerce.dto.ProductResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {
    List<ProductResponseDto> addProduct(List<ProductRequestDto> requestDto);

    List<ProductResponseDto> getAllProducts();

    ProductResponseDto getProductById(String productId);

    List<ProductResponseDto> getProductByCategory(String category);
}
