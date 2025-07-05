package com.ecommerce.profile_service.controller;

import com.ecommerce.profile_service.dto.AuthRequestDto;
import com.ecommerce.profile_service.dto.AuthResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@FeignClient(name = "auth-service", url = "http://localhost:8081")
public interface AuthServiceClient {

    @PostMapping("/auth/login")
    String login(@RequestBody AuthRequestDto authRequestDto);
}
