package com.ecommerce.profile_service.controller;

import com.ecommerce.profile_service.dto.AuthResponseDto;
import com.ecommerce.profile_service.dto.UserReqDto;
import com.ecommerce.profile_service.dto.UserResDto;
import com.ecommerce.profile_service.entity.User;
import com.ecommerce.profile_service.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("profile")
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService){
        this.userService = userService;
    }


    @GetMapping("message")
    public String getResponse(){
        return "Response from profile service";
    }

    @PostMapping("register")
    public ResponseEntity<AuthResponseDto> userRegister(@RequestBody UserReqDto request){
        return ResponseEntity.ok(userService.register(request));
    }

    @GetMapping("getUser/{username}")
    public UserResDto getUserByUsername(@PathVariable String username){
        return userService.findByUsername(username);
    }

}
