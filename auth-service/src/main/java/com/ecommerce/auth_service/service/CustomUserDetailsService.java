package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.controller.ProfileServiceClient;
import com.ecommerce.auth_service.dto.UserDto;
import com.ecommerce.auth_service.dto.UserResDto;
import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.mapper.AuthMapper;
import com.ecommerce.auth_service.model.UserPrincipal;
import com.ecommerce.auth_service.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;
    private final AuthMapper mapper;

    @Autowired
    public CustomUserDetailsService(AuthRepository authRepository, AuthMapper mapper){
        this.authRepository = authRepository;
        this.mapper = mapper;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = authRepository.findByEmail(username);
        UserResDto userResDto = mapper.toUserResDto(user);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        System.out.println("user: "+user.toString());
        return new UserPrincipal(userResDto);
    }
}
