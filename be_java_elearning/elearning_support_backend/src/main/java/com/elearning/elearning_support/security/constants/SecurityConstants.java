package com.elearning.elearning_support.security.constants;

import lombok.Data;


@Data
public class SecurityConstants {

    public static final String BEARER_AUTH_SCHEME = "Bearer";

    public static final String API_KEY = "ELEARNING_SUPPORT";

    public static final String AUTH_HEADER = "Authorization";

    public static final String[] WHITE_LIST = {"/api/auth/**", "/resources/upload/files/**", "/resources/excelTemplate/**",
        "/resources/wordTemplate/**", "/swagger-ui/**", "/v3/api-docs/**", "/public/**"};

    public static final String AUTH_KEY_PREFIX = "AUTH_USERNAME_";

    public static final Integer TOKEN_INVALID_STATUS = 0;

    public static final Integer TOKEN_VALID_STATUS = 1;

}
