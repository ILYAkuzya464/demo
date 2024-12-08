package com.example.demo.controller;

import com.example.demo.configuration.JwtTokenProvider;
import com.example.demo.controller.dto.LoginRequest;
import com.example.demo.controller.dto.LoginResponse;
import com.example.demo.controller.dto.RegRequest;
import com.example.demo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.stream.Collectors;


@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthenticateController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtProvider;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(new LoginResponse(request.getEmail(), jwtProvider.createToken(request.getEmail(),
                    authenticationManager
                            .authenticate(
                                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()))
                            .getAuthorities().stream().collect(Collectors.toSet()))));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Incorrect password");
        }
    }

    @PostMapping("/reg")
    public ResponseEntity<?> register(@RequestBody RegRequest request) {
        try {
            userService.create(request.getEmail(), request.getName(), request.getPassword());
            return ResponseEntity.ok("Successful");
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ex.getMessage());
        }
    }
}