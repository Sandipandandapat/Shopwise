package com.shopwise.mapper;

import com.shopwise.dto.ProductRequestDto;
import com.shopwise.dto.ProductResponseDto;
import com.shopwise.model.Products;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Products toProductEntity(ProductRequestDto productRequestDto);
    ProductResponseDto toProductResponseDto(Products product);
}
