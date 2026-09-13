package com.mynoano.mynoano.controller;

import com.mynoano.mynoano.dto.RegisterRequest;
import com.mynoano.mynoano.dto.UserResponse;
import com.mynoano.mynoano.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    
}



