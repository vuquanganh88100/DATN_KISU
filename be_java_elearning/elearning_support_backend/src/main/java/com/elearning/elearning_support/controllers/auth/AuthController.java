package com.elearning.elearning_support.controllers.auth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.elearning.elearning_support.dtos.auth.login.LoginRequest;
import com.elearning.elearning_support.dtos.auth.refresh.RefreshTokenResDTO;
import com.elearning.elearning_support.services.auth.AuthInfoService;
import com.elearning.elearning_support.services.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "APIs Xác thực/phân quyền (Authentication/Authorization)")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthInfoService authInfoService;

    private final AuthenticationService authenticationService;
    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Login API -> JWT Token
     */
    @PostMapping("/login")
    @Operation(summary = "Login vào hệ thống")
    public ResponseEntity<?> login(@RequestBody @Validated LoginRequest loginInfo, HttpServletRequest request) {
//        String decodedPasswordStr = new String(Base64.getDecoder().decode(loginInfo.getPassword()));
//        String rawPassword = new StringBuilder(decodedPasswordStr).reverse().toString();
//        System.out.println(loginInfo.getPassword());
//        System.out.println(passwordEncoder.matches(rawPassword,"$2a$12$/FmTDdhfjtVR9RqxKYdGgu.ljNjdjl5YR16migJXhiNRRNdW4hmOO"));
        log.info("Authorization = {}", request.getHeader("Authorization"));

        return ResponseEntity.ok(authenticationService.login(loginInfo, request));
    }

    /**
     * Refresh access token using access token
     */
    @PostMapping("/token/refresh")
    @Operation(summary = "Refresh token")
    public RefreshTokenResDTO refreshToken(@RequestHeader(name = "refreshToken") String refreshToken) {
        return authInfoService.refreshAccessToken(refreshToken);
    }
}
