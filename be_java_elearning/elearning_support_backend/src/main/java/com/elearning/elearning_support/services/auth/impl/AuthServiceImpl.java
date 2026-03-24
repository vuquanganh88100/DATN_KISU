package com.elearning.elearning_support.services.auth.impl;

import static com.elearning.elearning_support.security.constants.SecurityConstants.AUTH_KEY_PREFIX;
import static com.elearning.elearning_support.security.constants.SecurityConstants.TOKEN_INVALID_STATUS;
import static com.elearning.elearning_support.security.constants.SecurityConstants.TOKEN_VALID_STATUS;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.elearning.elearning_support.constants.message.errorKey.ErrorKey;
import com.elearning.elearning_support.constants.message.messageConst.MessageConst;
import com.elearning.elearning_support.constants.message.messageConst.MessageConst.Resources;
import com.elearning.elearning_support.dtos.auth.AuthValidationDTO;
import com.elearning.elearning_support.dtos.auth.refresh.RefreshTokenResDTO;
import com.elearning.elearning_support.entities.auth.AuthInfo;
import com.elearning.elearning_support.entities.users.User;
import com.elearning.elearning_support.exceptions.CustomBadCredentialsException;
import com.elearning.elearning_support.exceptions.exceptionFactory.ExceptionFactory;
import com.elearning.elearning_support.repositories.auth.AuthInfoRepository;
import com.elearning.elearning_support.security.jwt.JwtUtils;
import com.elearning.elearning_support.services.auth.AuthInfoService;
import com.elearning.elearning_support.services.redis.RedisService;
import com.elearning.elearning_support.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthInfoService {

    private final RedisService redisService;

    private final AuthInfoRepository authInfoRepository;

    private final ExceptionFactory exceptionFactory;

    private final JwtUtils jwtUtils;

    @Value("${jwt.refreshTokenExpiredMs}")
    private Long refreshTokenExpiredMs;

    @Value("${jwt.accessTokenExpiredMs}")
    private Long accessTokenExpiredMs;

    @Override
    public AuthValidationDTO saveLoginAuthInfo(User user, String ip) {
        // Không sử dụng Redis, chỉ làm việc với cơ sở dữ liệu
        AuthInfo currentAuthInfo = authInfoRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId()).orElse(null);

        String accessToken = jwtUtils.generateJwt(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);
        AuthValidationDTO authValidationDTO;

        if (Objects.isNull(currentAuthInfo)) {
            // Nếu không có lịch sử đăng nhập, tạo mới
            AuthInfo newAuthInfo = AuthInfo.builder()
                    .userId(user.getId())
                    .token(accessToken)
                    .ipAddress(ip)
                    .status(TOKEN_VALID_STATUS)
                    .createdAt(LocalDateTime.now())
                    .lastLoginAt(LocalDateTime.now())
                    .refreshToken(refreshToken)
                    .rfTokenExpiredAt(new Date(DateUtils.getCurrentDateTime().getTime() + refreshTokenExpiredMs))
                    .build();

            authInfoRepository.save(newAuthInfo);
            authValidationDTO = new AuthValidationDTO(newAuthInfo.getToken(), newAuthInfo.getRefreshToken(), newAuthInfo.getStatus());
        } else {
            // Nếu đã có lịch sử đăng nhập, cập nhật thông tin
            currentAuthInfo.setToken(!jwtUtils.validateToken(currentAuthInfo.getToken()) ? accessToken : currentAuthInfo.getToken());
            currentAuthInfo.setRefreshToken(refreshToken);
            currentAuthInfo.setRfTokenExpiredAt(new Date(DateUtils.getCurrentDateTime().getTime() + refreshTokenExpiredMs));
            currentAuthInfo.setStatus(TOKEN_VALID_STATUS);
            currentAuthInfo.setLastLoginAt(LocalDateTime.now());

            authInfoRepository.save(currentAuthInfo);
            authValidationDTO = new AuthValidationDTO(currentAuthInfo.getToken(), currentAuthInfo.getRefreshToken(), currentAuthInfo.getStatus());
        }

        return authValidationDTO;  // Trả về thông tin xác thực
    }


    @Override
    public AuthInfo findByUserId(Long userId) {
        return authInfoRepository.findFirstByUserIdOrderByCreatedAtDesc(userId).orElse(null);
    }

    @Override
    public RefreshTokenResDTO refreshAccessToken(String refreshToken) {
        AuthInfo authInfo = authInfoRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> exceptionFactory.resourceNotFoundException(MessageConst.AuthInfo.NOT_FOUND, Resources.AUTH_INFORMATION,
                MessageConst.RESOURCE_NOT_FOUND, ErrorKey.AuthInfo.REFRESH_TOKEN));
        if (!jwtUtils.validateToken(authInfo.getToken()) || Objects.equals(authInfo.getStatus(), TOKEN_INVALID_STATUS)) {
            User user = authInfo.getUser();
            authInfo.setToken(jwtUtils.generateJwt(user));
            authInfo.setStatus(TOKEN_VALID_STATUS);
            // check refresh token if expired -> gen new refresh token
            if (authInfo.getRfTokenExpiredAt().before(DateUtils.getCurrentDateTime())) {
                throw new CustomBadCredentialsException(MessageConst.AuthInfo.REFRESH_TOKEN_EXPIRED, Resources.AUTH_INFORMATION,
                    ErrorKey.AuthInfo.REFRESH_TOKEN);
            }
            authInfo = authInfoRepository.save(authInfo);
            redisService.putWithExpiration(AUTH_KEY_PREFIX + user.getUsername(), new AuthValidationDTO(authInfo.getToken(), authInfo.getRefreshToken(), authInfo.getStatus()),
                accessTokenExpiredMs + 1000, TimeUnit.MILLISECONDS);
        }
        return new RefreshTokenResDTO(authInfo.getToken(), authInfo.getRefreshToken());
    }
}
