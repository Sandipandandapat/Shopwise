package com.ecommerce.profile_service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserReqDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
