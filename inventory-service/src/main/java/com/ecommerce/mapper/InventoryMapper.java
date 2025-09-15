package com.ecommerce.mapper;

import com.ecommerce.dto.InventoryRequestDto;
import com.ecommerce.dto.InventoryResponseDto;
import com.ecommerce.model.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "onHand", source = "quantity")
    Inventory toInventoryEntity(InventoryRequestDto requestDto);

    @Mapping(target = "inStock", expression = "java(inventory.getOnHand()>0)")
    InventoryResponseDto toInventoryResponseDto(Inventory inventory);
}
