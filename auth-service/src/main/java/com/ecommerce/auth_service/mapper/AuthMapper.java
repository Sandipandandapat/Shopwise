package com.ecommerce.auth_service.mapper;

import com.ecommerce.auth_service.dto.UserResDto;
import com.ecommerce.auth_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    UserResDto toUserResDto(User user);
}
