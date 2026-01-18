package com.shopwise.mapper;

import com.shopwise.dto.InventoryRequestDto;
import com.shopwise.dto.InventoryResponseDto;
import com.shopwise.model.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "onHand", source = "quantity")
    Inventory toInventoryEntity(InventoryRequestDto requestDto);

    @Mapping(target = "inStock", expression = "java(inventory.getOnHand()>0)")
    InventoryResponseDto toInventoryResponseDto(Inventory inventory);
}
