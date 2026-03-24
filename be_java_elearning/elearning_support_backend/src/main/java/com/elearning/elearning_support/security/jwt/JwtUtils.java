package com.elearning.elearning_support.security.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.elearning.elearning_support.entities.users.User;
import com.elearning.elearning_support.security.constants.SecurityConstants;
import com.elearning.elearning_support.utils.DateUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Getter
public class JwtUtils {

    @Value("${jwt.accessTokenExpiredMs}")
    public String ACCESS_TOKEN_EXPIRED_IN_MS;

    @Value("${jwt.refreshTokenExpiredMs}")
    public String REFRESH_TOKEN_EXPIRED_IN_MS;

    @Value("${jwt.secretKey}")
    public String SECRET_KEY;

    /**
     * Generate Json Token Web
     *
     * @param user : login user
     * @return : JWT
     */
    public String generateJwt(User user) {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("username", user.getUsername());
        return Jwts.builder().setSubject(user.getUsername())
            .addClaims(claims)
            .setIssuedAt(DateUtils.getCurrentDateTime())
            .setExpiration(new Date(DateUtils.getCurrentDateTime().getTime() + Long.parseLong(ACCESS_TOKEN_EXPIRED_IN_MS)))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
    }

    /**
     * Get claims in jwt
     *
     * @param jwt : token
     * @return : claims
     */
    public Claims getClaims(String jwt) {
        try {
            return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(jwt).getBody();
        } catch (Exception exception) {
            log.error("Get claims frm jwt error");
            return null;
        }
    }

    /**
     * Get username from jwt
     *
     * @param jwt : token
     * @return : username
     */
    public String getUsernameFromJwt(String jwt) {
        try {
            Claims claims = getClaims(jwt);
            return claims.get("username", String.class);
        } catch (Exception ex) {
            log.error("Get username from jwt error");
            return null;
        }
    }

    /**
     * Valid whether
     *
     * @param jwt : token
     * @return : valid : true - not valid : false
     */
    public Boolean validateToken(String jwt) {
        try {
            Claims claims = getClaims(jwt);
            Date now = DateUtils.getCurrentDateTime();
            return Objects.nonNull(claims) && claims.getExpiration().after(now);
        } catch (Exception exception) {
            log.error("Check jwt expired error");
            return false;
        }
    }

    /**
     * Generate Refresh Token
     *
     * @return : Refresh Token
     */
    public String generateRefreshToken(User user) {
        return Base64.getEncoder().withoutPadding().encodeToString((user.getUsername() + user.getEmail()).getBytes());
    }

    /**
     * Get Token from Request Header Authorization  = Bearer  " " + Token
     */
    public String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(SecurityConstants.AUTH_HEADER);
        return Objects.nonNull(authHeader) ? authHeader.split(" ")[1] : null;
    }
}
