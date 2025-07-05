package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.controller.ProfileServiceClient;
import com.ecommerce.auth_service.dto.AuthReqDto;
import com.ecommerce.auth_service.dto.AuthResDto;
import com.ecommerce.auth_service.dto.UserDto;
import com.ecommerce.auth_service.dto.UserResDto;
import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.repository.AuthRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AuthRepository authRepository;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager, AuthRepository authRepository, JwtUtil jwtUtil){
        this.authenticationManager = authenticationManager;
        this.authRepository = authRepository;
        this.jwtUtil = jwtUtil;
    }

    public AuthResDto authenticate(AuthReqDto request) {
        System.out.println(request.toString());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid credentials");
        } catch (UsernameNotFoundException e) {
            throw new RuntimeException("User not found: " + request.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
        User user = authRepository.findByEmail(request.getEmail());
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResDto(user.getEmail(), user.getRole(), token);
    }


}
