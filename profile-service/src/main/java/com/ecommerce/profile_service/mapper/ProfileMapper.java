package com.ecommerce.profile_service.mapper;

import com.ecommerce.profile_service.dto.AuthResponseDto;
import com.ecommerce.profile_service.dto.UserReqDto;
import com.ecommerce.profile_service.dto.UserResDto;
import com.ecommerce.profile_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    User toUserEntity(UserReqDto reqDto);

    @Mapping(target = "time", source = "registerTime")
    @Mapping(target = "fullName", expression = "java(user.getFirstName()+ \" \" + user.getLastName())")
    UserResDto toUserResDto(User user);
    @Mapping(target = "fullName", expression = "java(user.getFirstName()+ \" \" + user.getLastName())")
    AuthResponseDto toAuthResponseDto(User user);
}
