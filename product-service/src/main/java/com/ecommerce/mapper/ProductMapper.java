package com.ecommerce.mapper;

import com.ecommerce.dto.ProductRequestDto;
import com.ecommerce.dto.ProductResponseDto;
import com.ecommerce.model.Products;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Products toProductEntity(ProductRequestDto productRequestDto);
    ProductResponseDto toProductResponseDto(Products product);
}
