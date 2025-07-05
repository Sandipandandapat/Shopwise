package com.ecommerce.auth_service.controller;

import com.ecommerce.auth_service.dto.UserDto;
import com.ecommerce.auth_service.dto.UserResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profile-service", url = "http://localhost:8082")
public interface ProfileServiceClient {
    @GetMapping("/profile/getUser/{username}")
    UserResDto getUserByUsername(@PathVariable String username);
}
