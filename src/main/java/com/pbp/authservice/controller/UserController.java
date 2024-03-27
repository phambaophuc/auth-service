package com.pbp.authservice.controller;

import com.pbp.authservice.dto.request.LoginRequest;
import com.pbp.authservice.dto.request.SignupRequest;
import com.pbp.authservice.dto.response.MessageResponse;
import com.pbp.authservice.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> signin(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("** User controller: user login **");
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        log.info("** User controller: register user **");
        return new ResponseEntity<>(userService.register(signupRequest), HttpStatus.CREATED);
    }
}
