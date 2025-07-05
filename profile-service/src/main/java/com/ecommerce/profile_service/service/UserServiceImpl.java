package com.ecommerce.profile_service.service;

import com.ecommerce.profile_service.controller.AuthServiceClient;
import com.ecommerce.profile_service.dto.AuthRequestDto;
import com.ecommerce.profile_service.dto.AuthResponseDto;
import com.ecommerce.profile_service.dto.UserReqDto;
import com.ecommerce.profile_service.dto.UserResDto;
import com.ecommerce.profile_service.entity.User;
import com.ecommerce.profile_service.exceptions.UserAlreadyExistsException;
import com.ecommerce.profile_service.mapper.ProfileMapper;
import com.ecommerce.profile_service.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final ProfileMapper mapper;
    private final AuthServiceClient authServiceClient;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder, ProfileMapper mapper,AuthServiceClient authServiceClient){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
        this.authServiceClient = authServiceClient;
    }

    @Override
    public AuthResponseDto register(UserReqDto request) {
        if(userRepo.existsByEmail(request.getEmail())){
            throw new UserAlreadyExistsException("Email is already in Use");
        }
        User user = mapper.toUserEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRegisterTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setRole("USER");

        User savedUser = userRepo.save(user);

        String token = authServiceClient.login(new AuthRequestDto(request.getEmail(),request.getPassword()));
        AuthResponseDto response = mapper.toAuthResponseDto(savedUser);
        response.setToken(token);
        return response;
    }

    @Override
    public UserResDto findByUsername(String username) {
        User user = userRepo.findByEmail(username);
        return mapper.toUserResDto(user);
    }
}
