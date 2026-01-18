package com.shopwise.util;

import com.shopwise.controller.ProductServiceClient;
import com.shopwise.dto.ProductResponseDto;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductServiceClientFallbackFactory implements FallbackFactory<ProductServiceClient> {

    @Override
    public ProductServiceClient create(Throwable cause) {
        return new ProductServiceClient() {
            @Override
            public ProductResponseDto getProductByProductId(String productId) {
                System.out.println("Product service client fallback triggered for: "+productId+" "+"cause: "+cause.toString());
                return ProductResponseDto.builder()
                        .id(productId)
                        .name("Temporarily Unavailable!!")
                        .price(BigDecimal.ZERO)
                        .build();
            }
        };
    }
}
