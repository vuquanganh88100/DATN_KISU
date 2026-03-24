package com.elearning.elearning_support.utils.http;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public class HttpServletUtils {

    private static final String[] IP_ADD_HEADERS = {
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR"
    };

    /**
     * Get client's request ip address
     */
    public static String getIpAddress(HttpServletRequest request){
        for(String header : IP_ADD_HEADERS){
            String ipAddress = request.getHeader(header);
            if(Objects.nonNull(ipAddress) && StringUtils.hasText(ipAddress) && !"unknown".equalsIgnoreCase(ipAddress)){
                return ipAddress;
            }
        }
        return request.getRemoteAddr();
    }

}
