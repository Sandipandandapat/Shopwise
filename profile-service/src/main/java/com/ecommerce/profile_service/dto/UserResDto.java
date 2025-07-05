package com.ecommerce.profile_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResDto {
    private Long id;
    private String fullName;
    private String email;
    private LocalDateTime time;
    private String role;
}
