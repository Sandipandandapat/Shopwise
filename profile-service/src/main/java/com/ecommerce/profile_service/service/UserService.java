package com.ecommerce.profile_service.service;

import com.ecommerce.profile_service.dto.AuthResponseDto;
import com.ecommerce.profile_service.dto.UserReqDto;
import com.ecommerce.profile_service.dto.UserResDto;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    public AuthResponseDto register(UserReqDto request) ;

    UserResDto findByUsername(String username);
}
