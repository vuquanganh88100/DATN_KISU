package com.elearning.elearning_support.services.auth.impl;

import java.util.*;
import javax.servlet.http.HttpServletRequest;

import com.elearning.elearning_support.entities.users.User;
import com.elearning.elearning_support.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.elearning.elearning_support.constants.message.errorKey.ErrorKey;
import com.elearning.elearning_support.constants.message.messageConst.MessageConst;
import com.elearning.elearning_support.constants.message.messageConst.MessageConst.AuthInfo;
import com.elearning.elearning_support.dtos.auth.AuthValidationDTO;
import com.elearning.elearning_support.dtos.auth.login.LoginRequest;
import com.elearning.elearning_support.dtos.auth.login.LoginResponse;
import com.elearning.elearning_support.exceptions.CustomBadCredentialsException;
import com.elearning.elearning_support.security.models.CustomUserDetails;
import com.elearning.elearning_support.services.auth.AuthInfoService;
import com.elearning.elearning_support.services.auth.AuthenticationService;
import com.elearning.elearning_support.services.users.UserService;
import com.elearning.elearning_support.utils.http.HttpServletUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final AuthInfoService authInfoService;

    private final UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;

    @Override
    public LoginResponse login(LoginRequest loginInfo, HttpServletRequest request) {
        // check exists by username
        if (!userService.existsByUsername(loginInfo.getUsername())){
            throw new CustomBadCredentialsException(AuthInfo.USER_NAME_NOT_FOUND, MessageConst.UNAUTHORIZED, ErrorKey.User.USERNAME, loginInfo.getUsername());
        }

        // decode and reverse
        String decodedPasswordStr = new String(Base64.getDecoder().decode(loginInfo.getPassword()));
        String rawPassword = new StringBuilder(decodedPasswordStr).reverse().toString();
        Optional<User> user = userRepository.findByUsername(loginInfo.getUsername());
        System.out.println(user.get().getPassword());
        System.out.println(rawPassword);
        // Kiểm tra mật khẩu
        System.out.println("check match"+passwordEncoder.matches(rawPassword,user.get().getPassword()));

        // do authentication
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginInfo.getUsername(), rawPassword);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // get user details
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if (Objects.isNull(userDetails)) {
            throw new CustomBadCredentialsException(MessageConst.AuthInfo.WRONG_USERNAME_PASSWORD, MessageConst.UNAUTHORIZED,
                String.format("%s/%s", ErrorKey.AuthInfo.USERNAME, ErrorKey.AuthInfo.PASSWORD));
        }
        // Init request values
        String ipAddress = HttpServletUtils.getIpAddress(request);
        // update auth info in db
        AuthValidationDTO authInfo = authInfoService.saveLoginAuthInfo(userDetails.getUser(), ipAddress);

        Set<String> roles = userDetails.getRoles();
        return LoginResponse.builder()
            .issuedAt(new Date())
            .accessToken(authInfo.getAccessToken())
            .refreshToken(authInfo.getRefreshToken())
            .roles(roles)
            .build();
    }
}
