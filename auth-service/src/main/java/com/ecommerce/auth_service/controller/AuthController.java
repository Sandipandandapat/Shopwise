package com.ecommerce.auth_service.controller;

import com.ecommerce.auth_service.dto.AuthReqDto;
import com.ecommerce.auth_service.dto.AuthResDto;
import com.ecommerce.auth_service.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }
    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody AuthReqDto request){
        System.out.println("from auth service");
        AuthResDto response = authService.authenticate(request);
        return ResponseEntity.ok(response.getToken());
    }
}
