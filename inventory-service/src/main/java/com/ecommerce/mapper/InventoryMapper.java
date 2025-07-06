package com.ecommerce.mapper;

import com.ecommerce.dto.InventoryRequestDto;
import com.ecommerce.dto.InventoryResponseDto;
import com.ecommerce.model.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    Inventory toInventoryEntity(InventoryRequestDto requestDto);

    @Mapping(target = "inStock", expression = "java(inventory.getQuantity()>0)")
    InventoryResponseDto toInventoryResponseDto(Inventory inventory);
}
